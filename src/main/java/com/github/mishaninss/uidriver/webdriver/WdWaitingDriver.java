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
import com.github.mishaninss.uidriver.annotations.ElementDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
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

@Component
public class WdWaitingDriver implements IWaitingDriver {

    @Autowired
    protected IWebDriverFactory webDriverFactory;
    @ElementDriver
    private IElementDriver elementDriver;
    @Autowired
    private WebDriverProperties properties;

    @Override
    public void waitForElementIsVisible(ILocatable element){
        waitForElementIsVisible(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForElementIsVisible(ILocatable element, long timeoutInSeconds){
        waitForElementIsVisible(element, timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForElementIsVisible(ILocatable element, long timeout, TemporalUnit unit){
        WebElement webElement = elementDriver.findElement(element);
        performWait(ExpectedConditions.visibilityOf(webElement), timeout, unit);
    }

    @Override
    public void waitForElementIsNotVisible(ILocatable element){
        waitForElementIsNotVisible(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForElementIsNotVisible(ILocatable element, long timeoutInSeconds){
        waitForElementIsNotVisible(element, timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForElementIsNotVisible(ILocatable element, long timeout, TemporalUnit unit){
        WebElement webElement = elementDriver.findElement(element);
        performWait(ExpectedConditions.invisibilityOf(webElement), timeout, unit);
    }

    @Override
    public void waitForElementIsClickable(ILocatable element){
        waitForElementIsClickable(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForElementIsClickable(ILocatable element, long timeoutInSeconds){
        waitForElementIsClickable(element, timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForElementIsClickable(ILocatable element, long timeout, TemporalUnit unit){
        WebElement webElement = elementDriver.findElement(element);
        performWait(ExpectedConditions.elementToBeClickable(webElement), timeout, unit);
    }

    @Override
    public void waitForElementToBeSelected(ILocatable element){
        waitForElementToBeSelected(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForElementToBeSelected(ILocatable element, long timeoutInSeconds){
        waitForElementToBeSelected(element, timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForElementToBeSelected(ILocatable element, long timeout, TemporalUnit unit){
        WebElement webElement = elementDriver.findElement(element);
        performWait(ExpectedConditions.elementToBeSelected(webElement), timeout, unit);
    }

    @Override
    public void waitForElementToBeNotSelected(ILocatable element){
        waitForElementToBeNotSelected(element, properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForElementToBeNotSelected(ILocatable element, long timeoutInSeconds){
        waitForElementToBeNotSelected(element, timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForElementToBeNotSelected(ILocatable element, long timeout, TemporalUnit unit){
        WebElement webElement = elementDriver.findElement(element);
        performWait(ExpectedConditions.elementSelectionStateToBe(webElement, false), timeout, unit);
    }

    @Override
    public void waitForAlertIsPresent(){
        waitForAlertIsPresent(properties.driver().timeoutsElement, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForAlertIsPresent(long timeoutInSeconds){
        waitForAlertIsPresent(timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForAlertIsPresent(long timeout, TemporalUnit unit){
        performWait(ExpectedConditions.alertIsPresent(), timeout, unit);
    }

    @Override
    public void waitForPageUpdate(){
        waitForPageUpdate(properties.driver().timeoutsPageLoad, ChronoUnit.MILLIS);
    }

    @Override
    public void waitForPageUpdate(long timeoutInSeconds){
        waitForPageUpdate(timeoutInSeconds, ChronoUnit.SECONDS);
    }

    @Override
    public void waitForPageUpdate(long timeout, TemporalUnit unit){
        performWait(IS_PAGE_UPDATED, timeout, unit);
    }

    protected void performWait(ExpectedCondition<?> condition, long timeout, TemporalUnit unit){
        FluentWait<WebDriver> wait = new FluentWait<>(webDriverFactory.getDriver());
        Duration duration = Duration.of(timeout, unit);
        wait.withTimeout(duration).until(condition);
    }

    /** JavaScript code to check if all the ajax requests completed */
    private static final String JS_AJAX_COMPLETION =
            "var docReady = window.document.readyState === 'complete';"
                + "var hasJQuery = window.jQuery !== undefined;"
                + "var isJqueryComplete = hasJQuery ? window.jQuery.active === 0 : true;"
                + "var isAnimatedComplete = hasJQuery ? window.jQuery(':animated').size() === 0 : true;"
                + "var hasAngular = window.angular !== undefined;"
                + "var isAngularCompleted = hasAngular ? window.angular.element(document).injector().get('$http').pendingRequests.length === 0 : true;"
                + "return docReady && isJqueryComplete && isAnimatedComplete && isAngularCompleted;";

    private static final ExpectedCondition<Object> IS_PAGE_UPDATED = (WebDriver webDriver) -> {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        Object result = js.executeScript(JS_AJAX_COMPLETION);
        return result == null || Boolean.parseBoolean(result.toString());
    };
}
