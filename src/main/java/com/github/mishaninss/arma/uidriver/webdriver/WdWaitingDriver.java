package com.github.mishaninss.arma.uidriver.webdriver;

import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.ElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;
import com.google.common.base.Preconditions;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class WdWaitingDriver implements IWaitingDriver {

  @Autowired
  protected IWebDriverFactory webDriverFactory;
  @Autowired
  private WebElementProvider webElementProvider;
  @ElementDriver
  @Lazy
  private IElementDriver elementDriver;
  @Autowired
  private WebDriverProperties properties;
  @Reporter
  private IReporter reporter;
  private BiConsumer<Long, TemporalUnit> waitForPageUpdateMethod;
  private static final String WAIT_FOR_PAGE_UPDATE_MSG = "Не дождались полной загрузки страницы в течение %d %s";
  @Value("${arma.driver.timeouts.page.load.fail:true}")
  private boolean failOnPageLoadTimeout;

  /**
   * Use this method to specify Java Script to check if page is updated
   *
   * @param script - Java Script must return true, if page is updated or false otherwise
   * @see WdWaitingDriver#JQUERY_COMPLETE
   * @see WdWaitingDriver#ANGULAR_HTTP_COMPLETE
   * @see WdWaitingDriver#DOC_READY_STATE_COMPLETE
   */
  @Override
  public void setWaitForPageUpdateScript(String script) {
    setWaitForPageUpdateMethod((timeout, unit) -> performWait(
        (WebDriver webDriver) -> {
          Preconditions.checkArgument(webDriver != null);
          JavascriptExecutor js = (JavascriptExecutor) webDriver;
          Object result = js.executeScript(script);
          return result == null || Boolean.parseBoolean(result.toString());
        }, timeout, unit));
  }

  @Override
  public void setWaitForPageUpdateMethod(BiConsumer<Long, TemporalUnit> method) {
    waitForPageUpdateMethod = method;
  }

  @Override
  public void waitForElementIsVisible(ILocatable element) {
    waitForElementIsVisible(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForElementIsVisible(ILocatable element, long timeoutInSeconds) {
    waitForElementIsVisible(element, timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForElementIsVisible(ILocatable element, long timeout, TemporalUnit unit) {
    WebElement webElement = webElementProvider
        .findElement(element, Duration.of(timeout, unit).toMillis());
    performWait(ExpectedConditions.visibilityOf(webElement), timeout, unit);
  }

  @Override
  public void waitForElementExists(ILocatable element) {
    waitForElementExists(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForElementExists(ILocatable element, long timeoutInSeconds) {
    waitForElementExists(element, timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForElementExists(ILocatable element, long timeout, TemporalUnit unit) {
    webElementProvider.findElement(element, Duration.of(timeout, unit).toMillis());
  }

  @Override
  public void waitForElementIsNotVisible(ILocatable element) {
    waitForElementIsNotVisible(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForElementIsNotVisible(ILocatable element, long timeoutInSeconds) {
    waitForElementIsNotVisible(element, timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForElementIsNotVisible(ILocatable element, long timeout, TemporalUnit unit) {
    WebElement webElement = executeWithoutWaiting(() -> {
      try {
        return webElementProvider.findElement(element);
      } catch (NoSuchElementException ex) {
        return null;
      }
    });
    if (webElement != null) {
      performWait(ExpectedConditions.invisibilityOf(webElement), timeout, unit);
    }
  }

  @Override
  public void waitForElementIsClickable(ILocatable element) {
    waitForElementIsClickable(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForElementIsClickable(ILocatable element, long timeoutInSeconds) {
    waitForElementIsClickable(element, timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForElementIsClickable(ILocatable element, long timeout, TemporalUnit unit) {
    WebElement webElement = webElementProvider.findElement(element);
    performWait(ExpectedConditions.elementToBeClickable(webElement), timeout, unit);
  }

  @Override
  public void waitForElementToBeSelected(ILocatable element) {
    waitForElementToBeSelected(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForElementToBeSelected(ILocatable element, long timeoutInSeconds) {
    waitForElementToBeSelected(element, timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForElementToBeSelected(ILocatable element, long timeout, TemporalUnit unit) {
    WebElement webElement = webElementProvider.findElement(element);
    performWait(ExpectedConditions.elementToBeSelected(webElement), timeout, unit);
  }

  @Override
  public void waitForElementToBeNotSelected(ILocatable element) {
    waitForElementToBeNotSelected(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForElementToBeNotSelected(ILocatable element, long timeoutInSeconds) {
    waitForElementToBeNotSelected(element, timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForElementToBeNotSelected(ILocatable element, long timeout, TemporalUnit unit) {
    WebElement webElement = webElementProvider.findElement(element);
    performWait(ExpectedConditions.elementSelectionStateToBe(webElement, false), timeout, unit);
  }

  @Override
  public void waitForElementAttributeToBeNotEmpty(ILocatable element, String attribute) {
    WebElement webElement = webElementProvider.findElement(element);
    performWait(ExpectedConditions.attributeToBeNotEmpty(webElement, attribute),
        properties.driver().timeoutsElement, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForElementAttributeToBeNotEmpty(ILocatable element, String attribute,
      long timeoutInSeconds) {
    WebElement webElement = webElementProvider.findElement(element);
    performWait(ExpectedConditions.attributeToBeNotEmpty(webElement, attribute), timeoutInSeconds,
        ChronoUnit.SECONDS);
  }

  @Override
  public void waitForElementAttributeToBeNotEmpty(ILocatable element, String attribute,
      long timeout, TemporalUnit unit) {
    WebElement webElement = webElementProvider.findElement(element);
    performWait(ExpectedConditions.attributeToBeNotEmpty(webElement, attribute), timeout, unit);
  }

  @Override
  public void waitForUrlToBe(String url) {
    waitForUrlToBe(url, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForUrlToBe(String url, long timeoutInSeconds) {
    waitForUrlToBe(url, timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForUrlToBe(String url, long timeout, TemporalUnit unit) {
    performWait(ExpectedConditions.urlToBe(url), timeout, unit);
  }

  @Override
  public void waitForAlertIsPresent() {
    waitForAlertIsPresent(properties.driver().timeoutsElement, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForAlertIsPresent(long timeoutInSeconds) {
    waitForAlertIsPresent(timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForAlertIsPresent(long timeout, TemporalUnit unit) {
    performWait(ExpectedConditions.alertIsPresent(), timeout, unit);
  }

  @Override
  public void waitForElementAttributeToBe(ILocatable element, String attribute, String value) {
    waitForElementAttributeToBe(element, attribute, value, properties.driver().timeoutsElement,
        ChronoUnit.MILLIS);
  }

  @Override
  public void waitForElementAttributeToBe(ILocatable element, String attribute, String value,
      long timeoutInSeconds) {
    waitForElementAttributeToBe(element, attribute, value, timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForElementAttributeToBe(ILocatable element, String attribute, String value,
      long timeout, TemporalUnit unit) {
    WebElement webElement = webElementProvider.findElement(element);
    performWait(ExpectedConditions.attributeToBe(webElement, attribute, value), timeout, unit);
  }

  @Override
  public void waitForElementAttributeContains(ILocatable element, String attribute, String value) {
    waitForElementAttributeContains(element, attribute, value, properties.driver().timeoutsElement,
        ChronoUnit.MILLIS);
  }

  @Override
  public void waitForElementAttributeContains(ILocatable element, String attribute, String value,
      long timeoutInSeconds) {
    waitForElementAttributeContains(element, attribute, value, timeoutInSeconds,
        ChronoUnit.SECONDS);
  }

  @Override
  public void waitForElementAttributeContains(ILocatable element, String attribute, String value,
      long timeout, TemporalUnit unit) {
    WebElement webElement = webElementProvider.findElement(element);
    performWait(ExpectedConditions.attributeContains(webElement, attribute, value), timeout, unit);
  }

  @Override
  public <T> T waitForCondition(Supplier<T> condition) {
    return waitForCondition(condition, null);
  }

  @Override
  public <T, R> R waitForCondition(Function<T, R> condition, T arg) {
    return waitForCondition(condition, arg, null);
  }

  @Override
  public <T> T waitForCondition(Supplier<T> condition, String message) {
    return waitForCondition(condition, properties.driver().timeoutsElement, ChronoUnit.MILLIS,
        message);
  }

  @Override
  public <T, R> R waitForCondition(Function<T, R> condition, T arg, String message) {
    return waitForCondition(condition, arg, properties.driver().timeoutsElement, ChronoUnit.MILLIS,
        message);
  }

  @Override
  public <T> T waitForCondition(Supplier<T> condition, long timeoutInSeconds) {
    return waitForCondition(condition, timeoutInSeconds, ChronoUnit.SECONDS, null);
  }

  @Override
  public <T, R> R waitForCondition(Function<T, R> condition, T arg, long timeoutInSeconds) {
    return waitForCondition(condition, arg, timeoutInSeconds, ChronoUnit.SECONDS, null);
  }

  @Override
  public <T> T waitForCondition(Supplier<T> condition, long timeoutInSeconds, String message) {
    return waitForCondition(condition, timeoutInSeconds, ChronoUnit.SECONDS, message);
  }

  @Override
  public <T, R> R waitForCondition(Function<T, R> condition, T arg, long timeoutInSeconds,
      String message) {
    return waitForCondition(condition, arg, timeoutInSeconds, ChronoUnit.SECONDS, message);
  }

  @Override
  public <T> T waitForCondition(Supplier<T> condition, long timeout, TemporalUnit unit) {
    return waitForCondition(condition, timeout, unit, null);
  }

  @Override
  public <T, R> R waitForCondition(Function<T, R> condition, T arg, long timeout,
      TemporalUnit unit) {
    return waitForCondition(condition, arg, timeout, unit, null);
  }

  @Override
  public <T> T waitForCondition(Supplier<T> condition, long timeout, TemporalUnit unit,
      String message) {
    ExpectedCondition<T> ec = (WebDriver webdriver) -> condition.get();
    return performWait((ec), timeout, unit, message);
  }

  @Override
  public <T, R> R waitForCondition(Function<T, R> condition, T arg, long timeout, TemporalUnit unit,
      String message) {
    ExpectedCondition<R> ec = (WebDriver webdriver) -> condition.apply(arg);
    return performWait((ec), timeout, unit, message);
  }

  @Override
  public void waitForPageUpdate() {
    waitForPageUpdate(properties.driver().timeoutsPageLoad, ChronoUnit.MILLIS);
  }

  @Override
  public void waitForPageUpdate(long timeoutInSeconds) {
    waitForPageUpdate(timeoutInSeconds, ChronoUnit.SECONDS);
  }

  @Override
  public void waitForPageUpdate(long timeout, TemporalUnit unit) {
    if (waitForPageUpdateMethod == null) {
      detectWaitForPageUpdateMethod();
    }
    try {
      waitForPageUpdateMethod.accept(timeout, unit);
    } catch (Throwable ex) {
      if (!failOnPageLoadTimeout) {
        reporter.warn("Ошибка при ожидании полной загрузки страницы", ex);
      } else {
        throw ex;
      }
    }
  }

  @Override
  public <T> T executeWithoutWaiting(Supplier<T> supplier) {
    webDriverFactory.setWaitingTimeout(0);
    try {
      return supplier.get();
    } finally {
      webDriverFactory.restoreWaitingTimeout();
    }
  }

  @Override
  public void executeWithoutWaiting(Runnable runnable) {
    webDriverFactory.setWaitingTimeout(0);
    try {
      runnable.run();
    } finally {
      webDriverFactory.restoreWaitingTimeout();
    }
  }

  private void detectWaitForPageUpdateMethod() {
    if (isJQuery()) {
      reporter.debug("jQuery detected");
      if (checkWaitingScript(webDriverFactory.getDriver(), JQUERY_COMPLETE)) {
        waitForPageUpdateMethod =
            (timeout, unit) -> performWait(isJQueryCompleted, timeout, unit,
                String.format(WAIT_FOR_PAGE_UPDATE_MSG, timeout, unit));
        return;
      }
    }

    if (isAngular()) {
      reporter.debug("Angular detected");
      boolean angularHttpSupported = isAngularHttpSupported();
      if (angularHttpSupported) {
        reporter.debug("Angular http waiter supported");
        if (checkWaitingScript(webDriverFactory.getDriver(), ANGULAR_HTTP_COMPLETE)) {
          waitForPageUpdateMethod =
              (timeout, unit) -> performWait(isAngularHttpCompleted, timeout, unit,
                  String.format(WAIT_FOR_PAGE_UPDATE_MSG, timeout, unit));
          return;
        }
      }
    }

    try {
      waitForPageUpdateMethod = (timeout, unit) -> performWait(IS_DOC_READY_STATE_COMPLETED,
          timeout, unit, String.format(WAIT_FOR_PAGE_UPDATE_MSG, timeout, unit));
      waitForPageUpdateMethod.accept(1L, ChronoUnit.SECONDS);
      reporter.debug("Using default page load waiter");
    } catch (Exception ex) {
      reporter.debug("Using noop page load waiter");
      waitForPageUpdateMethod = (timeout, unit) -> {
      };
    }
  }

  private boolean isJQuery() {
    try {
      JavascriptExecutor js = (JavascriptExecutor) webDriverFactory.getDriver();
      Object result = js.executeScript("return window.jQuery !== undefined;");
      return result == null || Boolean.parseBoolean(result.toString());
    } catch (Exception ex) {
      reporter.debug("Could not check jQuery", ex);
      return false;
    }
  }

  private boolean isAngular() {
    try {
      JavascriptExecutor js = (JavascriptExecutor) webDriverFactory.getDriver();
      Object result = js.executeScript("return window.angular !== undefined;");
      return result == null || Boolean.parseBoolean(result.toString());
    } catch (Exception ex) {
      reporter.debug("Could not check Angular", ex);
      return false;
    }
  }

  private boolean isAngularHttpSupported() {
    try {
      JavascriptExecutor js = (JavascriptExecutor) webDriverFactory.getDriver();
      js.executeScript(ANGULAR_HTTP_COMPLETE);
      return true;
    } catch (Exception ex) {
      reporter.ignoredException(ex);
      return false;
    }
  }

  protected <T> T performWait(ExpectedCondition<T> condition, long timeout, TemporalUnit unit) {
    return performWait(condition, timeout, unit, null);
  }

  protected <T> T performWait(ExpectedCondition<T> condition, long timeout, TemporalUnit unit,
      String message) {
    Duration duration = Duration.of(timeout, unit);
    long timeoutInMillis = duration.toMillis();
    if (properties.driver().timeoutsDriverOperation > 0 && timeoutInMillis >= properties
        .driver().timeoutsDriverOperation) {
      return splitWait(condition, timeoutInMillis, message);
    } else {
      FluentWait<WebDriver> wait = new FluentWait<>(webDriverFactory.getDriver());
      wait.withTimeout(duration);
      if (StringUtils.isNotBlank(message)) {
        wait.withMessage(message);
      }
      return wait.until(condition);
    }
  }

  private <T> T splitWait(ExpectedCondition<T> condition, long timeoutInMillis, String message) {
    long partTimeout = properties.driver().timeoutsDriverOperation - 5000L;
    long count = timeoutInMillis / partTimeout;
    long delta = timeoutInMillis % partTimeout;
    T ret = null;
    for (int i = 0; i < count; i++) {
      try {
        ret = performWait(condition, partTimeout, ChronoUnit.MILLIS, message);
        break;
      } catch (TimeoutException ex) {
        //ignore exception
      }
    }
    if (ret == null) {
      ret = performWait(condition, delta, ChronoUnit.MILLIS, message);
    }
    return ret;
  }

  /**
   * JavaScript code to check if all the ajax requests completed
   */
  public static final String JQUERY_COMPLETE =
      "var docReady = window.document.readyState === 'complete';"
          + "var hasJQuery = window.jQuery !== undefined;"
          + "var isJqueryComplete = hasJQuery ? window.jQuery.active === 0 : true;"
          + "var isAnimatedComplete = hasJQuery ? window.jQuery(':animated').length === 0 : true;"
          + "return docReady && isJqueryComplete && isAnimatedComplete;";

  /**
   * JavaScript code to check if all the ajax requests completed
   */
  public static final String ANGULAR_HTTP_COMPLETE =
      "var docReady = window.document.readyState === 'complete';"
          + "var hasAngular = window.angular !== undefined;"
          + "var isAngularCompleted = hasAngular ? window.angular.element(document).injector().get('$http').pendingRequests.length === 0 : true;"
          + "var isAnimatedComplete = hasAngular ? document.querySelector('.ng-animate') ? document.querySelector('.ng-animate').size == 0 : true : true;"
          + "return docReady && isAngularCompleted && isAnimatedComplete;";

  /**
   * JavaScript code to check if all the ajax requests completed
   */
  public static final String DOC_READY_STATE_COMPLETE = "return window.document.readyState === 'complete';";

  private final ExpectedCondition<Object> isJQueryCompleted = (WebDriver webDriver) -> {
    Preconditions.checkArgument(webDriver != null);
    JavascriptExecutor js = (JavascriptExecutor) webDriver;
    Object result = js.executeScript(JQUERY_COMPLETE);
    return result == null || Boolean.parseBoolean(result.toString());
  };

  private final ExpectedCondition<Object> isAngularHttpCompleted = (WebDriver webDriver) -> {
    Preconditions.checkArgument(webDriver != null);
    JavascriptExecutor js = (JavascriptExecutor) webDriver;
    Object result = js.executeScript(ANGULAR_HTTP_COMPLETE);
    return result == null || Boolean.parseBoolean(result.toString());
  };

  private static final ExpectedCondition<Object> IS_DOC_READY_STATE_COMPLETED = (WebDriver webDriver) -> {
    Preconditions.checkArgument(webDriver != null);
    JavascriptExecutor js = (JavascriptExecutor) webDriver;
    Object result = js.executeScript(DOC_READY_STATE_COMPLETE);
    return result == null || Boolean.parseBoolean(result.toString());
  };

  private boolean checkWaitingScript(WebDriver webDriver, String script) {
    try {
      JavascriptExecutor js = (JavascriptExecutor) webDriver;
      js.executeScript(script);
      return true;
    } catch (Exception ex) {
      reporter.warn("Provided waiting script [" + script + "] doesn't work", ex);
      return false;
    }
  }
}
