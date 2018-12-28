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
import com.github.mishaninss.uidriver.interfaces.IPoint;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author Sergey Mishanin
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
    @Autowired
    private WebElementProvider webElementProvider;

    /**
     * Performs scrolling to make the element visible on screen
     *
     * @param element - locatable element
     */
    @Override
    public WdElementDriver scrollToElement(@NonNull ILocatable element) {
        WebDriver driver = webDriverFactory.getDriver();
        WebElement webElement = webElementProvider.findElement(element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false)", webElement);
        return this;
    }

    /**
     * Simulates right click on the element
     *
     * @param element - locatable element
     */
    @Override
    public IElementDriver contextClickOnElement(@NonNull ILocatable element) {
        waitingDriver.waitForElementIsClickable(element);
        arma.actionsChain()
                .contextClick(element)
                .perform();
        return this;
    }

    /**
     * Checks if the element is displayed on the page or not.
     *
     * @param element - locator of the element.
     * @return true if the element exists on the page and displayed; false otherwise.
     */
    @Override
    public boolean isElementDisplayed(@NonNull ILocatable element) {
        return isElementDisplayed(element, true);
    }

    /**
     * Checks if element with specified locator is displayed on the page or not.
     *
     * @param element        - locator of the element.
     * @param waitForElement - true if you want to wait for an element existence;
     *                       false otherwise.
     * @return true if element exists on the page and displayed; false otherwise.
     */
    @Override
    public boolean isElementDisplayed(@NonNull ILocatable element, boolean waitForElement) {
        if (!waitForElement) {
            webDriverFactory.setWaitingTimeout(0);
        }
        try {
            WebElement webElement = webElementProvider.findElement(element);
            if (waitForElement) {
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
     *
     * @param element - locator of the element.
     * @return true if the element is enabled; false otherwise.
     */
    @Override
    public boolean isElementEnabled(@NonNull ILocatable element) {
        return webElementProvider.findElement(element).isEnabled();
    }

    /**
     * Checks if the element is selected or not.
     *
     * @param element - locator of the element.
     * @return true if the element is selected; false otherwise.
     */
    @Override
    public boolean isElementSelected(@NonNull ILocatable element) {
        return webElementProvider.findElement(element).isSelected();
    }

    /**
     * Get the value of a the given attribute of the element.
     *
     * @param element   - locator of the element
     * @param attribute - name of the attribute
     * @return the value of a the given attribute
     */
    @Override
    public String getAttributeOfElement(@NonNull ILocatable element, String attribute) {
        return webElementProvider.findElement(element).getAttribute(attribute);
    }

    /**
     * Simulates left click on the element
     *
     * @param element - locator of the element
     */
    @Override
    public WdElementDriver clickOnElement(@NonNull ILocatable element) {
        WebElement webElement = webElementProvider.findElement(element);
        try {
            webElement.click();
        } catch (WebDriverException ex) {
            String message = ex.getMessage();
            if (StringUtils.isNotBlank(message) && message.contains("is not clickable")) {
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
     *
     * @param element - locator of the element
     */
    @Override
    public WdElementDriver simpleClickOnElement(@NonNull ILocatable element) {
        webElementProvider.findElement(element).click();
        return this;
    }

    /**
     * Simulates left click with a pressed key (eg. CTRL, SHIFT, ALT)
     *
     * @param element - locator of the element
     * @param key     - pressed key
     */
    @Override
    public WdElementDriver clickOnElementWithKeyPressed(@NonNull ILocatable element, CharSequence key) {
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
     *
     * @param element - locator of the element
     * @return The visible inner text of this element.
     */
    @Override
    public String getTextFromElement(@NonNull ILocatable element) {
        return webElementProvider.findElement(element).getText();
    }

    /**
     * Get the full inner text of this element, including hidden text and text from sub-elements, without any leading or trailing whitespace.
     *
     * @param element - locator of the element
     * @return The full inner text of this element.
     */
    @Override
    public String getFullTextFromElement(@NonNull ILocatable element) {
        return webElementProvider.findElement(element).getAttribute("textContent");
    }

    /**
     * Simulates typing into an element
     *
     * @param element    - locator of the element
     * @param keysToSend - keys to send
     */
    @Override
    public WdElementDriver sendKeysToElement(@NonNull ILocatable element, CharSequence... keysToSend) {
        webElementProvider.findElement(element).sendKeys(keysToSend);
        return this;
    }

    /**
     * Can be used for text inputs to clear the current value
     *
     * @param element - locator of the element
     */
    @Override
    public WdElementDriver clearElement(@NonNull ILocatable element) {
        webElementProvider.findElement(element).clear();
        return this;
    }

    @Override
    public byte[] takeElementScreenshot(@NonNull ILocatable element) {
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
    public void clearCache() {
        webElementProvider.clearCache();
    }

    @Override
    public void highlightElement(@NonNull ILocatable element) {
        try {
            WebElement webElement = webElementProvider.findElement(element);
            highlightElement(webElement);
        } catch (Exception ex) {
            reporter.ignoredException(ex);
        }
    }

    private void highlightElement(@NonNull WebElement webElement) {
        WebDriver webDriver = webDriverFactory.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].style.border='5px solid red'", webElement);
    }

    @Override
    public void unhighlightElement(@NonNull ILocatable element) {
        WebElement webElement = webElementProvider.findElement(element);
        try {
            unhighlightElement(webElement);
        } catch (Exception ex) {
            reporter.ignoredException(ex);
        }
    }

    private void unhighlightElement(@NonNull WebElement webElement) {
        WebDriver webDriver = webDriverFactory.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("arguments[0].style.border='';", webElement);
    }

    @Override
    public void addElementDebugInfo(@NonNull ILocatable element, final String info, final String tooltip) {
        WebDriver driver = webDriverFactory.getDriver();
        WebElement webElement = webElementProvider.findElement(element);
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
    public void removeElementDebugInfo() {
        WebDriver driver = webDriverFactory.getDriver();
        ((JavascriptExecutor) driver).executeScript(
                "var node = document.getElementById('wdDebugInfo');" +
                        "if (node) {node.style.display = 'none'}");
    }

    @Override
    public String getTagName(@NonNull ILocatable element) {
        return webElementProvider.findElement(element).getTagName();
    }

    @Override
    public IPoint getLocation(@NonNull ILocatable element) {
        return new WdPoint(webElementProvider.findElement(element).getLocation());
    }

    @Override
    public IElementDriver hoverElement(@NonNull ILocatable element) {
        arma.actionsChain()
                .moveToElement(element)
                .perform();
        return this;
    }

    @Override
    public IElementDriver clickWithDelayElement(@NonNull ILocatable element) {
        arma.actionsChain()
                .clickAndHold(element)
                .pause(Duration.ofSeconds(2))
                .release()
                .perform();
        return this;
    }

    @Override
    public Object executeJsOnElement(@NonNull String javaScript, @NonNull ILocatable element) {
        WebElement webElement = webElementProvider.findElement(element);
        return ((JavascriptExecutor) webDriverFactory.getDriver()).executeScript(javaScript, webElement);
    }

    @Override
    public IElementDriver setValueToElement(@NonNull ILocatable element, String value) {
        return setAttributeOfElement(element, "value", value);
    }

    @Override
    public IElementDriver setAttributeOfElement(@NonNull ILocatable element, @NonNull String attribute, String value) {
        String js = String.format("arguments[0].setAttribute(\"%s\",\"%s\")", attribute, value);
        executeJsOnElement(js, element);
        return this;
    }

    @Override
    public void removeAttributeOfElement(ILocatable element, String attribute) {
        String js = String.format("arguments[0].removeAttribute(\"%s\")", attribute);
        executeJsOnElement(js, element);
    }

    public WebElement findElement(@NonNull By by) {
        return webDriverFactory.getDriver().findElement(by);
    }
}
