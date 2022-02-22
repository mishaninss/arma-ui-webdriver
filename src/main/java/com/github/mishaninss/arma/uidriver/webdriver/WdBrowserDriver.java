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

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ICookie;
import com.github.mishaninss.arma.uidriver.interfaces.ILogEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Sergey Mishanin
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
    public Set<ICookie> getAllCookies() {
        Set<Cookie> seleniumCookies = webDriverFactory.getDriver().manage().getCookies();
        return seleniumCookies.stream().map(WdCookie::new).collect(Collectors.toSet());
    }

    @Override
    public void addCookie(ICookie cookie) {
        if (cookie instanceof WdCookie) {
            webDriverFactory.getDriver().manage().addCookie(((WdCookie) cookie).toSeleniumCookie());
        }
    }

    @Override
    public ICookie getCookieNamed(String cookieName) {
        return new WdCookie(webDriverFactory.getDriver().manage().getCookieNamed(cookieName));
    }

    @Override
    public Set<String> getWindowHandles() {
        return webDriverFactory.getDriver().getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return webDriverFactory.getDriver().getWindowHandle();
    }

    @Override
    public void switchToWindow(String windowHandle) {
        webDriverFactory.getDriver().switchTo().window(windowHandle);
    }

    @Override
    public void switchToWindow(int windowIndex) {
        List<String> windowHandles = new ArrayList<>(getWindowHandles());
        webDriverFactory.getDriver().switchTo().window(windowHandles.get(windowIndex));
    }

    @Override
    public void switchToLastWindow() {
        List<String> windowHandles = new ArrayList<>(getWindowHandles());
        webDriverFactory.getDriver().switchTo().window(windowHandles.get(windowHandles.size() - 1));
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
    public List<ILogEntry> getLogEntries(String logType) {
        LogEntries logEntries = webDriverFactory.getDriver().manage().logs().get(logType);
        return logEntries.getAll().stream().map(WdLogEntry::new).collect(Collectors.toList());
    }

    @Override
    public boolean isBrowserStarted() {
        return webDriverFactory.isBrowserStarted();
    }


    @Override
    public void maximizeWindow() {
        WebDriver driver = webDriverFactory.getDriver();
        if (StringUtils.isNoneBlank(properties.driver().screenResolution)) {
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
