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
import com.github.mishaninss.uidriver.interfaces.IBrowserDriver;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 
 * @author Sergey Mishanin
 *
 */
@Component
public class WdBrowserDriver implements IBrowserDriver {
    @Autowired
    private WebDriverProperties properties;
    @Autowired
    private IWebDriverFactory webDriverFactory;

    @Override
    public void deleteAllCookies() {
        webDriverFactory.getDriver().manage().deleteAllCookies();
    }

    @Override
    public void deleteCookieNamed(String cookieName) {
        webDriverFactory.getDriver().manage().deleteCookieNamed(cookieName);
    }

    @Override
    public Set<Cookie> getAllCookies() {
        return webDriverFactory.getDriver().manage().getCookies();
    }

    @Override
    public void addCookie(Cookie cookie) {
        webDriverFactory.getDriver().manage().addCookie(cookie);
    }

    @Override
    public Cookie getCookieNamed(String cookieName) {
        return webDriverFactory.getDriver().manage().getCookieNamed(cookieName);
    }

    @Override
    public Set<String> getWindowHandles() {
        return webDriverFactory.getDriver().getWindowHandles();
    }

    @Override
    public void switchToWindow(String windowHandle) {
        webDriverFactory.getDriver().switchTo().window(windowHandle);
    }

    @Override
    public void closeCurrentWindow() {
        webDriverFactory.getDriver().close();
    }

    @Override
    public void closeWindow(String windowHandle) {
        webDriverFactory.getDriver().switchTo().window(windowHandle).close();
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
    public void maximizeWindow() {
        WebDriver driver = webDriverFactory.getDriver();
        if (StringUtils.isNoneBlank(properties.driver().screenResolution)){
            driver.manage().window().setSize(webDriverFactory.getWindowDimension());
        } else {
            driver.manage().window().maximize();
        }
    }

    @Override
    public void setWindowSize(int width, int height) {
        webDriverFactory.getDriver().manage().window().setSize(new Dimension(width, height));
    }
}
