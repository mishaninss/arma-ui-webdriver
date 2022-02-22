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

package com.github.mishaninss.arma.aspects;

import com.github.mishaninss.arma.data.UiCommonsProperties;
import com.github.mishaninss.arma.exceptions.SessionLostException;
import com.github.mishaninss.arma.uidriver.webdriver.IWebDriverFactory;
import com.github.mishaninss.arma.uidriver.webdriver.WebElementProvider;
import com.github.mishaninss.arma.utils.ConcurrentUtils;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@SuppressWarnings("unused")
@Aspect
public class SeleniumAspects {

  @Autowired
  private ApplicationContext applicationContext;

  private static final Logger LOGGER = LoggerFactory.getLogger("Selenium");
  private final ThreadLocal<Stack<String>> callStack = ThreadLocal.withInitial(Stack::new);

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
          " || target(org.openqa.selenium.WebDriver$Timeouts)" +
          " || target(org.openqa.selenium.chrome.ChromeDriverService$Builder)" +
          " || target(org.openqa.selenium.logging.LoggingPreferences)" +
          " || target(org.openqa.selenium.Point)" +
          " || target(org.openqa.selenium.logging.LogEntry)" +
          " || (call(* org.openqa.selenium.interactions.Actions.* (..)) && !call(* org.openqa.selenium.interactions.Actions.perform (..)))"
          +
          " || (call(* org.openqa.selenium.support.ui.FluentWait.* (..)) && !call(* org.openqa.selenium.support.ui.FluentWait.until (..)))"
          +
          " || target(org.openqa.selenium.Cookie)")
  public void ignored() {
    //Declaration of a pointcut for call to any Selenium method
  }

  @Around(value = "pointcutUiDriverCall() && !ignored()")
  public Object adviceAroundSeleniumCall(ProceedingJoinPoint joinPoint) throws Throwable {
    if (applicationContext.getBean(UiCommonsProperties.class).driver().timeoutsDriverOperation
        > 0) {
      try {
        callStack.get().push(
            joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature()
                .getName());
        LOGGER.trace("call {} [{}]", callStack.get(),
            applicationContext.getBean(UiCommonsProperties.class).driver().timeoutsDriverOperation);
        return proceedJoinPoint(joinPoint);
      } catch (Exception e) {
        callStack.get().clear();
        Throwable cause = e;

        if (cause instanceof ExecutionException && cause.getCause() != null) {
          cause = cause.getCause();
        }
        if (cause instanceof java.util.concurrent.TimeoutException) {
          cause = new SessionLostException(String.format("driver operation timeout [%d] ms",
              applicationContext.getBean(UiCommonsProperties.class)
                  .driver().timeoutsDriverOperation),
              cause);
        }

        logSeleniumException(joinPoint, cause);

        if (isHardSessionLostException(cause, joinPoint)) {
          handleHardSessionLostException(cause);
        } else if (isSessionLostException(cause, joinPoint)) {
          handleSessionLostException(cause);
        } else if (cause != null) {
          throw cause;
        } else {
          throw new WebDriverException("Unknown exception");
        }
      } finally {
        if (!callStack.get().isEmpty()) {
          callStack.get().pop();
        }
      }
      return null;
    } else {
      return joinPoint.proceed();
    }
  }

  private Object proceedJoinPoint(ProceedingJoinPoint joinPoint) throws Exception {
    return ConcurrentUtils.runWithTimeout(() -> {
          try {
            return joinPoint.proceed();
          } catch (Exception ex) {
            throw ex;
          } catch (Throwable throwable) {
            throw new InvocationTargetException(throwable);
          }
        }, applicationContext.getBean(UiCommonsProperties.class).driver().timeoutsDriverOperation,
        TimeUnit.MILLISECONDS);
  }

  private void logSeleniumException(JoinPoint joinPoint, Throwable cause) {
    if (!(cause instanceof NoSuchElementException
        || cause instanceof StaleElementReferenceException
        || cause instanceof UnhandledAlertException
        || cause instanceof NoAlertPresentException
        || cause instanceof TimeoutException)) {
      LOGGER.trace("Selenium exception during " + joinPoint.getSignature().getName(), cause);
    }
  }

  private void handleHardSessionLostException(Throwable cause) {
    LOGGER.error("Hard Session lost exception detected", cause);
    applicationContext.getBean(IWebDriverFactory.class).hardCloseDriver();
    applicationContext.getBean(WebElementProvider.class).clearCache();
    throwSle(cause);
  }

  private void handleSessionLostException(Throwable cause) {
    LOGGER.error("Session lost exception detected", cause);
    applicationContext.getBean(IWebDriverFactory.class).closeDriver();
    applicationContext.getBean(WebElementProvider.class).clearCache();
    throwSle(cause);
  }

  private void throwSle(Throwable cause) {
    if (cause instanceof SessionLostException) {
      throw (SessionLostException) cause;
    } else {
      throw new SessionLostException("Driver session has been lost", cause);
    }
  }

  private boolean isHardSessionLostException(Throwable cause, JoinPoint joinPoint) {
    return
        cause != null && !(cause instanceof UnhandledAlertException) &&
            (
                cause instanceof NoSuchSessionException ||
                    StringUtils
                        .equalsAnyIgnoreCase(joinPoint.getSignature().getName(), "get", "close",
                            "quit")
            );
  }

  private boolean isSessionLostException(Throwable cause, JoinPoint joinPoint) {
    return
        cause != null &&
            (
                cause instanceof SessionLostException ||
                    cause instanceof UnsupportedCommandException ||
                    cause instanceof UnreachableBrowserException ||
                    cause instanceof ConnectException ||
//                                        cause instanceof NoSuchWindowException ||
                    (cause instanceof TimeoutException && "getCurrentUrl"
                        .equals(joinPoint.getSignature().getName())) ||
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
