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

package com.github.mishaninss.arma.uidriver.webdriver;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.ElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.mishaninss.arma.utils.Keys.CONTROL;

/**
 * @author Sergey Mishanin
 */
@Component
public class WdElementsDriver implements IElementsDriver {
    @Reporter
    private IReporter reporter;
    @Autowired
    private WebDriverProperties properties;
    @ElementDriver
    private IElementDriver elementDriver;
    @Autowired
    private IWebDriverFactory webDriverFactory;
    @Autowired
    private WebElementProvider webElementProvider;
    @Autowired
    private LocatorConverter locatorConverter;

    @Override
    public boolean areElementsDisplayed(String locator) {
        return areElementsDisplayed(locator, false);
    }

    @Override
    public boolean areElementsDisplayed(String locator, boolean waitForElement) {
        if (!waitForElement) {
            webDriverFactory.setWaitingTimeout(0);
        }
        try {
            List<WebElement> elements = findElements(locator);
            for (WebElement element : elements) {
                if (!element.isDisplayed()) {
                    return false;
                }
            }
        } catch (NoSuchElementException ex) {
            reporter.ignoredException(ex);
            return false;
        } finally {
            webDriverFactory.restoreWaitingTimeout();
        }
        return true;
    }

    @Override
    public boolean areElementsEnabled(String locator) {
        List<WebElement> elements = findElements(locator);
        for (WebElement element : elements) {
            if (!element.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean areElementsSelected(String locator) {
        List<WebElement> elements = findElements(locator);
        for (WebElement element : elements) {
            if (!element.isSelected()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String[] getAttributeOfElements(String locator, String attribute) {
        List<WebElement> elements = findElements(locator);
        String[] attributes = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            attributes[i] = elements.get(i).getAttribute(attribute);
        }
        return attributes;
    }

    @Override
    public WdElementsDriver clickOnElements(String locator) {
        List<WebElement> elements = findElements(locator);
        for (WebElement element : elements) {
            waitForElementToBeClickable(element);
            element.click();
        }
        return this;
    }

    @Override
    public WdElementsDriver clickOnElementsWithKeyPressed(String locator, CharSequence key) {
        WebDriver driver = webDriverFactory.getDriver();
        List<WebElement> elements = findElements(locator);
        for (WebElement element : elements) {
            waitForElementToBeClickable(element);
            Actions actions = new Actions(driver);
            actions.keyDown(CONTROL).click(element).keyUp(CONTROL).perform();
        }
        return this;
    }

    @Override
    public String[] getTextFromElements(String locator) {
        List<WebElement> elements = findElements(locator);
        String[] texts = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            texts[i] = elements.get(i).getText();
        }
        return texts;
    }

    @Override
    public String[] getFullTextFromElements(String locator) {
        return getAttributeOfElements(locator, "textContent");
    }

    @Override
    public WdElementsDriver sendKeysToElements(String locator, CharSequence... keysToSend) {
        List<WebElement> elements = findElements(locator);
        for (WebElement element : elements) {
            element.sendKeys(keysToSend);
        }
        return this;
    }

    @Override
    public WdElementsDriver clearElements(String locator) {
        List<WebElement> elements = findElements(locator);
        for (WebElement element : elements) {
            element.clear();
        }
        return this;
    }

    private void waitForElementToBeClickable(WebElement element) {
        waitForElementToBeClickable(element, properties.driver().timeoutsElement);
    }

    private void waitForElementToBeClickable(WebElement element, int timeout) {
        WebDriver driver = webDriverFactory.getDriver();
        new WebDriverWait(driver, Duration.of(timeout, ChronoUnit.MILLIS)).until(ExpectedConditions.elementToBeClickable(element));
    }

    private List<WebElement> findElements(ILocatable element) {
        if (!element.useContextLookup()) {
            return findElements(null, element.getLocator());
        } else {
            WebElement contextWebElement = null;
            Deque<ILocatable> contextDeque = element.getRealLocatableObjectDeque();
            contextDeque.pollLast();
            if (!contextDeque.isEmpty()) {
                ILocatable context = contextDeque.pollLast();
                contextWebElement = webElementProvider.findElement(context);
            }
            return findElements(contextWebElement, element.getLocator());
        }
    }

    private List<WebElement> findElements(WebElement context, String locator) {
        WebDriver driver = webDriverFactory.getDriver();
        if (context == null) {
            return driver.findElements(locatorConverter.toBy(locator));
        } else {
            return context.findElements(locatorConverter.toBy(locator));
        }
    }

    private List<WebElement> findElements(String locator) {
        WebDriver driver = webDriverFactory.getDriver();
        return driver.findElements(locatorConverter.toBy(locator));
    }

    @Override
    public int getElementsCount(String locator) {
        return findElements(locator).size();
    }

    @Override
    public int getElementsCount(ILocatable element) {
        return findElements(element).size();
    }

    @Override
    public Map<String, String> getSrcOfAllImages() {
        String locator = "//img[@src and not(@src='')]";
        List<WebElement> images = findElements(locator);
        Map<String, String> imgs = new HashMap<>();
        for (int i = 0; i < images.size(); i++) {
            WebElement img = images.get(i);
            String imgLocator = "(" + locator + ")[" + (i + 1) + "]";
            imgs.put(imgLocator, img.getAttribute("src"));
        }
        return imgs;
    }
}
