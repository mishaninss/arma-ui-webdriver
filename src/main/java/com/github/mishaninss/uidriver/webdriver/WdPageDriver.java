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
import com.github.mishaninss.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.uidriver.interfaces.ILocatable;
import com.github.mishaninss.uidriver.interfaces.IPageDriver;
import com.github.mishaninss.uidriver.interfaces.IWaitingDriver;
import com.github.mishaninss.uidriver.webdriver.chrome.ChromeExtender;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Sergey Mishanin
 *
 */
@Component
public class WdPageDriver implements IPageDriver {
    @Autowired
    private IReporter reporter;
    @Autowired
    private WebDriverProperties properties;
    @Autowired
    private IWebDriverFactory webDriverFactory;
    @Autowired
    private IElementDriver elementDriver;
    @Autowired
    private IWaitingDriver waitingDriver;

    @Override
    public WdPageDriver goToUrl(String url){
        reporter.info("Open URL " + url);
        WebDriver driver = webDriverFactory.getDriver();
        driver.get(url);
        try {
            waitingDriver.waitForPageUpdate();
        } catch (UnhandledAlertException ex){
            String unexpectedAlertBehaviour = properties.driver().unexpectedAlertBehaviour;
            if (!StringUtils.equalsAnyIgnoreCase(unexpectedAlertBehaviour, "accept", "dismiss")){
                throw ex;
            }
        }
        return this;
    }

	@Override
	public boolean isAlertDisplayed(){
	    return isAlertDisplayed(false);
    }

    @Override
    public boolean isAlertDisplayed(boolean waitForAlert){
        WebDriver driver = webDriverFactory.getDriver();
        try{
            if (waitForAlert){
                Wait<WebDriver> wait = new WebDriverWait(driver, properties.driver().timeoutsElement/1000);
                wait.until(ExpectedConditions.alertIsPresent());
            }
            driver.switchTo().alert();
            return true;
        }catch(Exception ex){
            reporter.ignoredException(ex);
            return false;
        }
    }

	@Override
	public WdPageDriver acceptAlert(){
        Alert alert = getAlert();
        reporter.info("Accept alert [" + alert.getText() + "]");
        alert.accept();
        return this;
	}

	@Override
    public WdPageDriver dismissAlert() {
        Alert alert = getAlert();
        reporter.info("Dismiss alert [" + alert.getText() + "]");
        alert.dismiss();
        return this;
    }

	@Override
    public String getAlertMessage(){
        return getAlert().getText();
    }

    private Alert getAlert(){
        WebDriver driver = webDriverFactory.getDriver();
        Wait<WebDriver> wait = new WebDriverWait(driver, TimeUnit.MILLISECONDS.toSeconds(properties.driver().timeoutsElement));
        wait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert();
    }

	@Override
	public WdPageDriver refreshPage(){
        webDriverFactory.getDriver().navigate().refresh();
        return this;
    }

    @Override
    public WdPageDriver navigateBack(){
        webDriverFactory.getDriver().navigate().back();
        return this;
    }

	@Override
	public Object executeJS(String javaScript){
	    WebDriver driver = webDriverFactory.getDriver();
	    return ((JavascriptExecutor)driver).executeScript(javaScript);
	}

	@Override
    public Object executeJS(String javaScript, String locator){
        WebDriver driver = webDriverFactory.getDriver();
        WebElement element = driver.findElement(LocatorConverter.toBy(locator));
        return ((JavascriptExecutor)driver).executeScript(javaScript, element);
    }

	@Override
	public String getCurrentUrl(){
	    WebDriver driver = webDriverFactory.getDriver();
	    return driver.getCurrentUrl();
	}

	@Override
	public String getPageTitle(){
	    WebDriver driver = webDriverFactory.getDriver();
	    return driver.getTitle();
	}

    @Override
    public byte[] takeScreenshot(){
        try {
            return ChromeExtender.takeScreenshot();
        } catch (Exception e) {
            reporter.ignoredException(e);
            return new byte[0];
        }
    }

    @Override
    public void deleteAllCookies() {
        WebDriver driver = webDriverFactory.getDriver();
        driver.manage().deleteAllCookies();
    }

    @Override
    public void deleteCookieNamed(String cookieName) {
        WebDriver driver = webDriverFactory.getDriver();
        driver.manage().deleteCookieNamed(cookieName);
    }

    @Override
    public Set<Cookie> getAllCookies() {
        WebDriver driver = webDriverFactory.getDriver();
        return driver.manage().getCookies();
    }

    @Override
    public void addCookie(Cookie cookie) {
        WebDriver driver = webDriverFactory.getDriver();
        driver.manage().addCookie(cookie);
    }

    @Override
    public Cookie getCookieNamed(String cookieName) {
        WebDriver driver = webDriverFactory.getDriver();
        return driver.manage().getCookieNamed(cookieName);
    }

    @Override
    public Set<String> getWindowHandles() {
        WebDriver driver = webDriverFactory.getDriver();
        return driver.getWindowHandles();
    }

    @Override
    public void switchToWindow(String windowHandle) {
        WebDriver driver = webDriverFactory.getDriver();
        driver.switchTo().window(windowHandle);
    }

    @Override
    public void closeCurrentWindow() {
        WebDriver driver = webDriverFactory.getDriver();
        driver.close();
    }

    @Override
    public void closeWindow(String windowHandle) {
        WebDriver driver = webDriverFactory.getDriver();
        driver.switchTo().window(windowHandle).close();
    }

    @Override
    public boolean scrollToBottom(){
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
        } catch (NumberFormatException ex){
            return false;
        }
    }

    @Override
    public LogEntries getLogEntries(String logType){
        return webDriverFactory.getDriver().manage().logs().get(logType);
    }

    @Override
    public boolean isBrowserStarted(){
        return webDriverFactory.isBrowserStarted();
    }

    @Override
    public String getPageSource() {
        return webDriverFactory.getDriver().getPageSource();
    }

    @Override
    public void switchToFrame(String nameOrId) {
        WebDriver driver = webDriverFactory.getDriver();
        driver.switchTo().frame(nameOrId);
    }

    @Override
    public void switchToFrame(ILocatable frameElement) {
        WebDriver driver = webDriverFactory.getDriver();
        WebElement webElement = elementDriver.findElement(frameElement);
        driver.switchTo().frame(webElement);
    }

    @Override
    public void switchToDefaultContent() {
        webDriverFactory.getDriver().switchTo().defaultContent();
    }

    @Override
    public void maximizeWindow() {
        WebDriver driver = webDriverFactory.getDriver();
        if (StringUtils.isNoneBlank(properties.driver().screenResolution)){
            driver.manage().window().setSize(webDriverFactory.getWindowDimension());
        } else {
            driver.manage().window().maximize();
        }
    }

    @Override
    public void scrollToTop() {
        executeJS("window.scrollTo(0,0);");
    }
}
