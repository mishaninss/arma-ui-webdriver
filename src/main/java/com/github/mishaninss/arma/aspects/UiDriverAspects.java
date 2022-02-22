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

import com.github.mishaninss.arma.uidriver.WindowsManager;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;
import com.github.mishaninss.arma.uidriver.webdriver.WebElementProvider;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@SuppressWarnings("unused")
@Aspect
public class UiDriverAspects {

  @Autowired
  private ApplicationContext applicationContext;

  private static final Logger LOGGER = LoggerFactory.getLogger("Framework");

  @Pointcut("execution(* com.github.mishaninss.arma.uidriver.interfaces.IElementDriver.* (..))")
  public void pointcutIElementDriverCall() {
    //Declaration of a pointcut for call to any IElementDriver interface method
  }

  @Pointcut("execution(* com.github.mishaninss.arma.uidriver.interfaces.IElementsDriver.* (..))")
  public void pointcutIElementsDriverCall() {
    //Declaration of a pointcut for call to any IElementsDriver interface method
  }

  @Pointcut("execution(* com.github.mishaninss.arma.uidriver.interfaces.IPageDriver.switchToFrame (com.github.mishaninss.arma.uidriver.interfaces.ILocatable))")
  public void pointcutIPageDriverCall() {
    //Declaration of a pointcut for call to any IElementsDriver interface method
  }

  @Pointcut("execution(* com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver.* (..))")
  public void pointcutIWaitingDriverCall() {
    //Declaration of a pointcut for call to any IElementsDriver interface method
  }

  @Pointcut("execution(* com.github.mishaninss.arma.uidriver.interfaces.IActionsChain.* (..))")
  public void pointcutIActionsChainCall() {
    //Declaration of a pointcut for call to any IElementsDriver interface method
  }

  @Pointcut("execution(* com.github.mishaninss.arma.uidriver.webdriver.WebElementProvider.* (..))")
  public void pointcutWebElementProviderExecution() {
    //Declaration of a pointcut for call to any IElementsDriver interface method
  }

  @Pointcut("execution(* com.github.mishaninss.arma.uidriver.webdriver.WebElementProvider.findElement (com.github.mishaninss.arma.uidriver.interfaces.ILocatable))")
  public void topFindElementExecutionExecution() {
    //Declaration of a pointcut for call to any IElementsDriver interface method
  }

  @Around("pointcutIActionsChainCall() || pointcutIElementDriverCall() || pointcutIElementsDriverCall() || pointcutIPageDriverCall() || pointcutIWaitingDriverCall() || (pointcutWebElementProviderExecution() && !topFindElementExecutionExecution())")
  public Object adviceAroundIElementDriverMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    LOGGER.trace("call [{}.{}]", joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName());
    try {
      return joinPoint.proceed();
    } catch (StaleElementReferenceException ex) {
      LOGGER.trace("StaleElementReferenceException", ex);
      applicationContext.getBean(WebElementProvider.class).clearCache();
      return joinPoint.proceed();
    } catch (InvalidElementStateException ex) {
      LOGGER.trace("InvalidElementStateException", ex);
      applicationContext.getBean(WebElementProvider.class).clearCache();
      applicationContext.getBean(IWaitingDriver.QUALIFIER, IWaitingDriver.class).waitForPageUpdate();
      return joinPoint.proceed();
    } catch (TimeoutException ex) {
      if (StringUtils.contains(ex.getMessage(), "Timed out receiving message from renderer")) {
        LOGGER.trace("TimeoutException", ex);
        return null;
      } else {
        throw ex;
      }
    }
  }

  @Around("topFindElementExecutionExecution()")
  public Object adviceAroundTopFindElementExecution(ProceedingJoinPoint joinPoint)
      throws Throwable {
    LOGGER.trace("Find element: {}", ((ILocatable) joinPoint.getArgs()[0]).getLocatorsPath());
    try {
      return joinPoint.proceed();
    } catch (NoSuchWindowException ex) {
      LOGGER.trace("NoSuchWindowException", ex);
      applicationContext.getBean(WebElementProvider.class).clearCache();
      applicationContext.getBean(WindowsManager.class).ensureLastWindow();
      applicationContext.getBean(IWaitingDriver.class).waitForPageUpdate();
      return joinPoint.proceed();
    }
  }
}
