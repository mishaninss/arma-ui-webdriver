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
import com.github.mishaninss.html.interfaces.INamed;
import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.Arma;
import com.github.mishaninss.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Sergey Mishanin
 *
 */
@Component
public class WdElementDriver implements IElementDriver {
    @Reporter
    private IReporter reporter;
    @Autowired
    private WebDriverProperties properties;
    @Autowired
    private IWebDriverFactory webDriverFactory;
    @WaitingDriver
    private IWaitingDriver waitingDriver;
    @Autowired
    private Arma arma;
    
    /**  WebElements cache */
    private final Map<ILocatable, WebElement> elements = new HashMap<>();

    /**
     * Performs scrolling to make the element visible on screen
     * @param element - locatable element
     */
    @Override
    public WdElementDriver scrollToElement(ILocatable element){
        WebDriver driver = webDriverFactory.getDriver();
        WebElement webElement = findElement(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false)", webElement);
        return this;
    }

    /**
     * Simulates right click on the element
     * @param element - locatable element
     */
    @Override
    public IElementDriver contextClickOnElement(ILocatable element) {
        waitingDriver.waitForElementIsClickable(element);
        arma.actionsChain()
                .contextClick(element)
                .perform();
        return this;
    }

    /**
     * Checks if the element is displayed on the page or not.
     * @param element - locator of the element.
     * @return true if the element exists on the page and displayed; false otherwise.
     */
    @Override
    public boolean isElementDisplayed(ILocatable element){
        return isElementDisplayed(element, true);
    }

    /**
     * Checks if element with specified locator is displayed on the page or not.
     * @param element - locator of the element.
     * @param waitForElement - true if you want to wait for an element existence;
     * false otherwise.
     * @return true if element exists on the page and displayed; false otherwise.
     */
    @Override
    public boolean isElementDisplayed(ILocatable element, boolean waitForElement){
        if (!waitForElement){
            webDriverFactory.setWaitingTimeout(0);
        }
        try{
            WebElement webElement = findElement(element);
            if (waitForElement){
                new WebDriverWait(webDriverFactory.getDriver(), properties.driver().timeoutsElement)
                        .until(ExpectedConditions.visibilityOf(webElement));
            }
            return webElement.isDisplayed();
        } catch (NoSuchElementException | TimeoutException ex) {
            reporter.ignoredException(ex);
            return false;
        } finally {
            if (!waitForElement) {
                webDriverFactory.restoreWaitingTimeout();
            }
        }
    }

    /**
     * Checks if the element is enabled or not.
     * @param element - locator of the element.
     * @return true if the element is enabled; false otherwise.
     */
    @Override
    public boolean isElementEnabled(ILocatable element){
        return findElement(element).isEnabled();
    }

    /**
     * Checks if the element is selected or not.
     * @param element - locator of the element.
     * @return true if the element is selected; false otherwise.
     */
    @Override
    public boolean isElementSelected(ILocatable element){
        return findElement(element).isSelected();
    }

    /**
     * Get the value of a the given attribute of the element.
     * @param element - locator of the element
     * @param attribute - name of the attribute
     * @return the value of a the given attribute
     */
    @Override
    public String getAttributeOfElement(ILocatable element, String attribute){
        return findElement(element).getAttribute(attribute);
    }

    /**
     * Simulates left click on the element
     * @param element - locator of the element
     */
    @Override
    public WdElementDriver clickOnElement(ILocatable element){
        WebElement webElement = findElement(element);
        try {
            webElement.click();
        } catch (WebDriverException ex){
            String message = ex.getMessage();
            if (StringUtils.isNotBlank(message) && message.contains("is not clickable")){
                reporter.warn("Сaught element is not clickable exception. Will try JS click");
                executeJsOnElement("arguments[0].click()", element);
            } else {
                throw ex;
            }
        }
        return this;
    }

    /**
     * Simulates left click on the element without waiting for element is clickable
     * @param element - locator of the element
     */
    @Override
    public WdElementDriver simpleClickOnElement(ILocatable element){
        findElement(element).click();
        return this;
    }

    /**
     * Simulates left click with a pressed key (eg. CTRL, SHIFT, ALT)
     * @param element - locator of the element
     * @param key - pressed key
     */
    @Override
    public WdElementDriver clickOnElementWithKeyPressed(ILocatable element, Keys key){
        waitingDriver.waitForElementIsClickable(element);
        arma.actionsChain()
                .keyDown(key)
                .click(element)
                .keyUp(key)
                .perform();
        return this;
    }

    /**
     * Get the visible inner text of this element, including sub-elements, without any leading or trailing whitespace.
     * @param element - locator of the element
     * @return The visible inner text of this element.
     */
    @Override
    public String getTextFromElement(ILocatable element){
        return findElement(element).getText();
    }

    /**
     * Get the full inner text of this element, including hidden text and text from sub-elements, without any leading or trailing whitespace.
     * @param element - locator of the element
     * @return The full inner text of this element.
     */
    @Override
    public String getFullTextFromElement(ILocatable element){
        return findElement(element).getAttribute("textContent");
    }

    /**
     * Simulates typing into an element
     * @param element - locator of the element
     * @param keysToSend - keys to send
     */
    @Override
    public WdElementDriver sendKeysToElement(ILocatable element, CharSequence... keysToSend){
        findElement(element).sendKeys(keysToSend);
        return this;
    }

    /**
     * Can be used for text inputs to clear the current value
     * @param element - locator of the element
     */
    @Override
    public WdElementDriver clearElement(ILocatable element){
        findElement(element).clear();
        return this;
    }

    @Override
    public WebElement findElement(ILocatable element){
        if (!element.useContextLookup()){
            WebElement webElement = findElement(null, element.getLocator());
            elements.put(element, webElement);
            return webElement;
        } else {
            Deque<ILocatable> elementsStack = element.getRealLocatableObjectDeque();

            WebElement contextElement = null;
            WebElement webElement = null;
            while (!elementsStack.isEmpty()) {
                ILocatable nextElement = elementsStack.pop();
                webElement = cacheLookup(nextElement);
                if (webElement == null) {
                    String locator = nextElement.getLocator();
                    Object[] indexCheck = LocatorConverter.checkForIndex(locator);
                    if (ArrayUtils.isEmpty(indexCheck)) {
                        webElement = findElement(contextElement, locator);
                    } else {
                        webElement = findElement(contextElement, indexCheck[1].toString(), (Integer) indexCheck[0]);
                    }
                    elements.put(nextElement, webElement);
                }
                contextElement = webElement;
            }
            if (webElement == null){
                throw new NoSuchElementException("Cannot find element " + StringUtils.join(element.getLocatorDeque(), " -> "));
            }
            return webElement;
        }
    }

    private WebElement cacheLookup(ILocatable element){
        return elements.get(element);
    }

    private WebElement findElement(WebElement context, String locator) {
        WebDriver driver = webDriverFactory.getDriver();
        if (context == null) {
            return driver.findElement(LocatorConverter.toBy(locator));
        } else {
            return context.findElement(LocatorConverter.toBy(locator));
        }
    }

    private WebElement findElement(WebElement context, String locator, int index) {
        WebDriver driver = webDriverFactory.getDriver();
        List<WebElement> webElements;
        if (context == null) {
            webElements = driver.findElements(LocatorConverter.toBy(locator));
        } else {
            webElements = context.findElements(LocatorConverter.toBy(locator));
        }
        if (webElements.size() < index) {
            throw new NoSuchElementException("Cannot find element [" + locator + "] with index [" + index + "]");
        }
        return webElements.get(index - 1);
    }

    @Override
    public byte[] takeElementScreenshot(ILocatable element){
        highlightElement(element);
        String info = element.getLocator();
        String name = INamed.getLoggableNameIfApplicable(element);
        if (StringUtils.isNotBlank(name)) {
            info = name;
        }
        addElementDebugInfo(element, info, "");
        byte[] screenshot = arma.page().takeScreenshot();
        removeElementDebugInfo();
        unhighlightElement(element);
        return screenshot;
    }

    @Override
    public void clearCache(){
        elements.clear();
    }

    @Override
    public void highlightElement(ILocatable element){
        try {
            WebElement webElement = findElement(element);
            highlightElement(webElement);
        } catch (Exception ex){
            reporter.ignoredException(ex);
        }
    }

    private void highlightElement(WebElement webElement){
        WebDriver webDriver = webDriverFactory.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].style.border='5px solid red'", webElement);
    }

    @Override
    public void unhighlightElement(ILocatable element){
        WebElement webElement = findElement(element);
        try {
            unhighlightElement(webElement);
        } catch(Exception ex){
            reporter.ignoredException(ex);
        }
    }

    private void unhighlightElement(WebElement webElement){
        WebDriver webDriver = webDriverFactory.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].style.border='';", webElement);
    }

    @Override
    public void addElementDebugInfo(ILocatable element, final String info, final String tooltip){
        WebDriver driver = webDriverFactory.getDriver();
        WebElement webElement = findElement(element);
        if (webElement != null) {
            Point point = ((Locatable) webElement).getCoordinates().inViewPort();
            int x = point.x;
            int y = point.y;
            ((JavascriptExecutor) driver).executeScript(
                "var node = document.getElementById('wdDebugInfo');"
                        + "if (!node){"
                        + "node = document.createElement('span');"
                        + "node.id = 'wdDebugInfo';"
                        + "node.style.position = 'fixed';"
                        + "node.style.zIndex = '9999999';"
                        + "node.style.color = 'white';"
                        + "node.style.background = 'red';"
                        + "node.style['font-weight'] = 'bold';"
                        + "node.style['font-size'] = '10pt';"
                        + "document.body.appendChild(node);}"
                        + "node.innerHTML = arguments[2];"
                        + "node.title = arguments[3];"
                        + "node.style.display = 'block';"
                        + "node.style.left = window.innerWidth < arguments[0] + node.offsetWidth ? (window.innerWidth - node.offsetWidth - 5) < 0 ? 0 + 'px': (window.innerWidth - node.offsetWidth - 5) + 'px' : arguments[0] + 'px';"
                        + "node.style.top = arguments[1] - node.offsetHeight - 5 > 0 ? (arguments[1] - node.offsetHeight - 5) + 'px' : (arguments[1] + arguments[4].offsetHeight + 5) + 'px';"
                , x, y, info, tooltip, webElement);
        }
    }

    @Override
    public void removeElementDebugInfo(){
        WebDriver driver = webDriverFactory.getDriver();
        ((JavascriptExecutor) driver).executeScript(
                "var node = document.getElementById('wdDebugInfo');" +
                        "if (node) {node.style.display = 'none'}");
    }

    @Override
    public String getTagName(ILocatable element) {
        return findElement(element).getTagName();
    }

    @Override
    public Point getLocation(ILocatable element) {
        return findElement(element).getLocation();
    }

    @Override
    public IElementDriver hoverElement(ILocatable element) {
        arma.actionsChain()
                .moveToElement(element)
                .perform();
        return this;
    }

    @Override
    public IElementDriver clickWithDelayElement(ILocatable element) {
        arma.actionsChain()
                .clickAndHold(element)
                .pause(Duration.ofSeconds(2))
                .release()
                .perform();
        return this;
    }

    @Override
    public Object executeJsOnElement(String javaScript, ILocatable element){
        WebElement webElement = findElement(element);
        return ((JavascriptExecutor)webDriverFactory.getDriver()).executeScript(javaScript, webElement);
    }

    public WebElement findElement(By by){
        return webDriverFactory.getDriver().findElement(by);
    }
}
