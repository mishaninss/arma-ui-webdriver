/*
 * Copyright 2018 Sergey Mishanin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mishaninss.aspects;

import com.github.mishaninss.data.UiCommonsProperties;
import com.github.mishaninss.exceptions.SessionLostException;
import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.webdriver.IWebDriverFactory;
import com.github.mishaninss.utils.ConcurrentUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Aspect
@Component
public class SeleniumAspects {
    private static final Logger LOGGER = LoggerFactory.getLogger("Selenuim");
    private ThreadLocal<Stack<String>> callStack = ThreadLocal.withInitial(Stack::new);

    @Autowired
    private UiCommonsProperties uiCommonsProperties;
    @Autowired
    private IWebDriverFactory webDriverFactory;
    @ElementDriver
    private IElementDriver elementDriver;

	@Pointcut("call(* org.openqa.selenium..* (..))")
	public void pointcutUiDriverCall() {
	    //Declaration of a pointcut for call to any Selenium method
    }

    @Pointcut(
                "call(* org.openqa.selenium.remote.DesiredCapabilities.* (..))" +
            " || call(* org.openqa.selenium.By.* (..))" +
            " || call(* org.openqa.selenium.WebDriver.manage (..))" +
            " || call(* org.openqa.selenium.support.ui.ExpectedConditions.* (..))" +
            " || target(org.openqa.selenium.Platform)" +
            " || target(org.openqa.selenium.WebDriver$Options)" +
            " || target(org.openqa.selenium.WebDriver$Timeouts)")
    public void ignored() {
        //Declaration of a pointcut for call to any Selenium method
    }

	@Around(value="pointcutUiDriverCall() && !ignored()")
	public Object adviceAroundSeleniumCall(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            callStack.get().push(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            LOGGER.trace("call {}", callStack.get());
            return ConcurrentUtils.runWithTimeout(() -> {
                try {
                    return joinPoint.proceed();
                } catch (Exception ex) {
                    throw ex;
                } catch (Throwable throwable) {
                    throw new InvocationTargetException(throwable);
                }
            }, uiCommonsProperties.driver().timeoutsDriverOperation, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            callStack.get().clear();
            Throwable cause = e;

            if ((cause instanceof InvocationTargetException || cause instanceof ExecutionException)  && cause.getCause() != null) {
                cause = cause.getCause();
            }
            if (cause instanceof java.util.concurrent.TimeoutException) {
                cause = new SessionLostException("driver operation timeout", cause);
            }

            if (!(cause instanceof NoSuchElementException
                    || cause instanceof StaleElementReferenceException
                    || cause instanceof UnhandledAlertException
                    || cause instanceof NoAlertPresentException
                    || cause instanceof TimeoutException)) {
                LOGGER.warn("Selenium exception during " + joinPoint.getSignature().getName(), cause);
            }

            if (isHardSessionLostException(cause, joinPoint)){
                LOGGER.error("Hard Session lost exception detected", cause);
                webDriverFactory.hardCloseDriver();
                elementDriver.clearCache();
                if (cause instanceof SessionLostException){
                    throw cause;
                } else {
                    throw new SessionLostException("Driver session has been lost", cause);
                }
            } else if (isSessionLostException(cause, joinPoint)) {
                LOGGER.error("Session lost exception detected", cause);
                webDriverFactory.closeDriver();
                elementDriver.clearCache();
                if (cause instanceof SessionLostException){
                    throw cause;
                } else {
                    throw new SessionLostException("Driver session has been lost", cause);
                }
            } else if (cause != null){
                throw cause;
            } else {
                throw new WebDriverException("Null exception");
            }
        } finally {
            if (!callStack.get().isEmpty()) {
                callStack.get().pop();
            }
        }
    }

    private boolean isHardSessionLostException(Throwable cause, JoinPoint joinPoint){
        return
            cause != null &&
            (
                cause instanceof NoSuchSessionException ||
                StringUtils.equalsAnyIgnoreCase(joinPoint.getSignature().getName(), "get", "close", "quit")
            );
    }

	private boolean isSessionLostException(Throwable cause, JoinPoint joinPoint){
	    return
            cause != null &&
            (
                cause instanceof SessionLostException ||
                cause instanceof UnsupportedCommandException ||
                cause instanceof UnreachableBrowserException ||
                cause instanceof ConnectException ||
                cause instanceof NoSuchWindowException ||
                (cause instanceof TimeoutException && "getCurrentUrl".equals(joinPoint.getSignature().getName())) ||
                StringUtils.containsAny(cause.getMessage(),
"Session not started or terminated",
                    "not reachable",
                    "not connected to DevTools",
                    "Unable to communicate to node",
                    "Remote browser did not respond",
                    "cannot get automation extension",
                    "was terminated due to",
                    "Connection refused",
                    "not available and is not among the last 1000 terminated sessions",
                    "session deleted because of page crash",
                    "Java heap space",
                    "unable to connect to renderer",
                    "Address already in use")
            );
    }
}
