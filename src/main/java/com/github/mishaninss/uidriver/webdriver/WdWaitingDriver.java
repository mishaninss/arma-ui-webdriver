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

package com.github.mishaninss.uidriver.webdriver;

import com.github.mishaninss.data.WebDriverProperties;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.Arma;
import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Component
public class WdWaitingDriver implements IWaitingDriver {

    @Autowired
    private Arma arma;
    @Autowired
    protected IWebDriverFactory webDriverFactory;
    @Autowired
    private WebElementProvider webElementProvider;
    @ElementDriver
    private IElementDriver elementDriver;
    @Autowired
    private WebDriverProperties properties;
    @Reporter
    private IReporter reporter;
    private BiConsumer<Long, TemporalUnit> waitForPageUpdateMethod;

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
        WebElement webElement = webElementProvider.findElement(element);
        performWait(ExpectedConditions.visibilityOf(webElement), timeout, unit);
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
        if (!elementDriver.isElementDisplayed(element, false)) {
            return;
        }
        WebElement webElement = webElementProvider.findElement(element);
        performWait(ExpectedConditions.invisibilityOf(webElement), timeout, unit);
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
        performWait(ExpectedConditions.attributeToBeNotEmpty(webElement, attribute), properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForElementAttributeToBeNotEmpty(ILocatable element, String attribute, long timeoutInSeconds) {
        WebElement webElement = webElementProvider.findElement(element);
        performWait(ExpectedConditions.attributeToBeNotEmpty(webElement, attribute), timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForElementAttributeToBeNotEmpty(ILocatable element, String attribute, long timeout, TemporalUnit unit) {
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
        waitForElementAttributeToBe(element, attribute, value, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForElementAttributeToBe(ILocatable element, String attribute, String value, long timeoutInSeconds) {
        waitForElementAttributeToBe(element, attribute, value, timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForElementAttributeToBe(ILocatable element, String attribute, String value, long timeout, TemporalUnit unit) {
        WebElement webElement = webElementProvider.findElement(element);
        performWait(ExpectedConditions.attributeToBe(webElement, attribute, value), timeout, unit);
    }

    @Override
    public void waitForElementAttributeContains(ILocatable element, String attribute, String value) {
        waitForElementAttributeContains(element, attribute, value, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForElementAttributeContains(ILocatable element, String attribute, String value, long timeoutInSeconds) {
        waitForElementAttributeContains(element, attribute, value, timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForElementAttributeContains(ILocatable element, String attribute, String value, long timeout, TemporalUnit unit) {
        WebElement webElement = webElementProvider.findElement(element);
        performWait(ExpectedConditions.attributeContains(webElement, attribute, value), timeout, unit);
    }

    @Override
    public <T> T waitForCondition(Supplier<T> condition) {
        return waitForCondition(condition, null);
    }

    @Override
    public <T> T waitForCondition(Supplier<T> condition, String message) {
        return waitForCondition(condition, properties.driver().timeoutsElement, ChronoUnit.MILLIS, message);
    }

    @Override
    public <T> T waitForCondition(Supplier<T> condition, long timeoutInSeconds) {
        return waitForCondition(condition, timeoutInSeconds, ChronoUnit.SECONDS, null);
    }

    @Override
    public <T> T waitForCondition(Supplier<T> condition, long timeoutInSeconds, String message) {
        return waitForCondition(condition, timeoutInSeconds, ChronoUnit.SECONDS, message);
    }

    @Override
    public <T> T waitForCondition(Supplier<T> condition, long timeout, TemporalUnit unit) {
        return waitForCondition(condition, timeout, unit, null);
    }

    @Override
    public <T> T waitForCondition(Supplier<T> condition, long timeout, TemporalUnit unit, String message) {
        ExpectedCondition<T> ec = (WebDriver webdriver) -> condition.get();
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
        waitForPageUpdateMethod.accept(timeout, unit);
    }

    @Override
    public <T> T executeWithoutWaiting(Supplier<T> supplier){
        webDriverFactory.setWaitingTimeout(0);
        try {
            return supplier.get();
        } finally {
            webDriverFactory.restoreWaitingTimeout();
        }
    }

    @Override
    public void executeWithoutWaiting(Runnable runnable){
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
                        (timeout, unit) -> performWait(isJQueryCompleted, timeout, unit);
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
                            (timeout, unit) -> performWait(isAngularHttpCompleted, timeout, unit);
                    return;
                }
            }
        }

        reporter.debug("Using default page load waiter");

        waitForPageUpdateMethod = (timeout, unit) -> performWait(IS_DOC_READY_STATE_COMPLETED, timeout, unit);
    }

    private boolean isJQuery() {
        JavascriptExecutor js = (JavascriptExecutor) webDriverFactory.getDriver();
        Object result = js.executeScript("return window.jQuery !== undefined;");
        return result == null || Boolean.parseBoolean(result.toString());
    }

    private boolean isAngular() {
        JavascriptExecutor js = (JavascriptExecutor) webDriverFactory.getDriver();
        Object result = js.executeScript("return window.angular !== undefined;");
        return result == null || Boolean.parseBoolean(result.toString());
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

    protected <T> T performWait(ExpectedCondition<T> condition, long timeout, TemporalUnit unit, String message) {
        FluentWait<WebDriver> wait = new FluentWait<>(webDriverFactory.getDriver());
        Duration duration = Duration.of(timeout, unit);
        wait.withTimeout(duration);
        if (StringUtils.isNotBlank(message)){
            wait.withMessage(message);
        }
        return wait.until(condition);
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
