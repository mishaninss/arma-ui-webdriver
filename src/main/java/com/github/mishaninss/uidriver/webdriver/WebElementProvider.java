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

import com.github.mishaninss.uidriver.interfaces.ILocatable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebElementProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebElementProvider.class);

    @Autowired
    private IWebDriverFactory webDriverFactory;

    /**  WebElements cache */
    private final Map<ILocatable, WebElement> elements = new HashMap<>();

    public void clearCache(){
        elements.clear();
    }

    public WebElement findElement(ILocatable element){
        if (!element.useContextLookup()){
            String locator = element.getLocator();
            Object[] indexCheck = LocatorConverter.checkForIndex(locator);
            WebElement webElement;
            if (ArrayUtils.isEmpty(indexCheck)) {
                webElement = findElement(null, locator);
            } else {
                webElement = findElement(null, indexCheck[1].toString(), (Integer) indexCheck[0]);
            }
            elements.put(element, webElement);
            return webElement;
        } else {
            Deque<ILocatable> elementsStack = element.getRealLocatableObjectDeque();
            LOGGER.trace("Locatable deque: {}", elementsStack);
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
            LOGGER.trace("find element {}", locator);
            return driver.findElement(LocatorConverter.toBy(locator));
        } else {
            LOGGER.trace("find element {} {}", context, locator);
            return context.findElement(LocatorConverter.toBy(locator));
        }
    }

    private WebElement findElement(WebElement context, String locator, int index) {
        WebDriver driver = webDriverFactory.getDriver();
        List<WebElement> webElements;
        if (context == null) {
            LOGGER.trace("find element {} [{}]", locator, index);
            webElements = driver.findElements(LocatorConverter.toBy(locator));
        } else {
            LOGGER.trace("find element {} {} [{}]", context, locator, index);
            webElements = context.findElements(LocatorConverter.toBy(locator));
        }
        if (webElements.size() < index) {
            throw new NoSuchElementException("Cannot find element [" + locator + "] with index [" + index + "]");
        }
        return webElements.get(index - 1);
    }
}
