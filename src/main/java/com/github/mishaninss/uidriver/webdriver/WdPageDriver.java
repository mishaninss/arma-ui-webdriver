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
import com.github.mishaninss.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IPageDriver;
import com.github.mishaninss.uidriver.interfaces.IScreenshoter;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import com.github.mishaninss.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

/**
 * Implementation of {@link IPageDriver} interface based on WebDriver engine
 * Provides methods to interact with a page in browser.
 *
 * @author Sergey Mishanin
 */
@Component
public class WdPageDriver implements IPageDriver {
    @Reporter
    private IReporter reporter;
    @Autowired
    private WebDriverProperties properties;
    @Autowired
    private IWebDriverFactory webDriverFactory;
    @Autowired
    private WebElementProvider webElementProvider;
    @WaitingDriver
    private IWaitingDriver waitingDriver;
    @Autowired
    private IScreenshoter screenshoter;
    @Autowired
    private UrlUtils urlUtils;
    @Autowired
    private Arma arma;

    private BiConsumer<String, Arma> postPageOpenMethod;

    @Override
    public void setPostPageOpenMethod(BiConsumer<String, Arma> postPageOpenMethod) {
        this.postPageOpenMethod = postPageOpenMethod;
    }

    @Override
    public WdPageDriver goToUrl(String url) {
        String resolvedUrl = urlUtils.resolveUrl(url);
        reporter.info("Open URL " + resolvedUrl);
        WebDriver driver = webDriverFactory.getDriver();
        driver.get(resolvedUrl);
        try {
            waitingDriver.waitForPageUpdate();
        } catch (UnhandledAlertException ex) {
            String unexpectedAlertBehaviour = properties.driver().unexpectedAlertBehaviour;
            if (!StringUtils.equalsAnyIgnoreCase(unexpectedAlertBehaviour, "accept", "dismiss")) {
                throw ex;
            }
        }
        if (postPageOpenMethod != null) {
            postPageOpenMethod.accept(url, arma);
        }
        return this;
    }

    @Override
    public WdPageDriver refreshPage() {
        webDriverFactory.getDriver().navigate().refresh();
        return this;
    }

    @Override
    public WdPageDriver navigateBack() {
        webDriverFactory.getDriver().navigate().back();
        return this;
    }

    @Override
    public Object executeAsyncJS(String javaScript) {
        WebDriver driver = webDriverFactory.getDriver();
        return ((JavascriptExecutor) driver).executeAsyncScript(javaScript);
    }

    @Override
    public Object executeAsyncJS(String javaScript, Object... args) {
        WebDriver driver = webDriverFactory.getDriver();
        return ((JavascriptExecutor) driver).executeAsyncScript(javaScript, args);
    }

    @Override
    public Object executeAsyncJS(String javaScript, ILocatable element, Object... args) {
        WebDriver driver = webDriverFactory.getDriver();
        WebElement webElement = webElementProvider.findElement(element);
        return ((JavascriptExecutor) driver).executeAsyncScript(javaScript, webElement, args);
    }

    @Override
    public Object executeJS(String javaScript) {
        WebDriver driver = webDriverFactory.getDriver();
        return ((JavascriptExecutor) driver).executeScript(javaScript);
    }

    @Override
    public Object executeJS(String javaScript, String locator, Object... args) {
        WebDriver driver = webDriverFactory.getDriver();
        WebElement webElement = driver.findElement(LocatorConverter.toBy(locator));
        return ((JavascriptExecutor) driver).executeScript(javaScript, webElement, args);
    }

    @Override
    public String getCurrentUrl() {
        WebDriver driver = webDriverFactory.getDriver();
        String url = driver.getCurrentUrl();
        reporter.debug("Current URL {}", url);
        return url;
    }

    @Override
    public String getPageTitle() {
        WebDriver driver = webDriverFactory.getDriver();
        return driver.getTitle();
    }

    @Override
    public byte[] takeScreenshot() {
        return screenshoter.takeScreenshot();
    }

    @Override
    public boolean scrollToBottom() {
        try {
            int innerHeight = Integer.parseInt(executeJS("return document.body.scrollHeight").toString());
            int positionBefore;
            int positionAfter;
            do {
                positionBefore = Integer.parseInt(executeJS("return window.pageYOffset;").toString());
                executeJS("window.scrollBy(0," + innerHeight + ");");
                waitingDriver.waitForPageUpdate();
                positionAfter = Integer.parseInt(executeJS("return window.pageYOffset;").toString());
            } while (positionAfter - positionBefore > 0);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    public String getPageSource() {
        return webDriverFactory.getDriver().getPageSource();
    }

    @Override
    public WdPageDriver switchToFrame(String nameOrId) {
        WebDriver driver = webDriverFactory.getDriver();
        driver.switchTo().frame(nameOrId);
        return this;
    }

    @Override
    public WdPageDriver switchToFrame(ILocatable frameElement) {
        WebDriver driver = webDriverFactory.getDriver();
        WebElement webElement = webElementProvider.findElement(frameElement);
        driver.switchTo().frame(webElement);
        return this;
    }

    @Override
    public WdPageDriver switchToDefaultContent() {
        webDriverFactory.getDriver().switchTo().defaultContent();
        return this;
    }

    @Override
    public WdPageDriver scrollToTop() {
        executeJS("window.scrollTo(0,0);");
        return this;
    }
}
