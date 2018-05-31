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
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provides a single instance of WebDriver
 * @author Sergey Mishanin
 *
 */
@Component
@Profile("!chrome")
public class WebDriverFactory implements IWebDriverFactory {

    @Reporter
    protected IReporter reporter;
    @Autowired
    protected WebDriverProperties properties;
    @Autowired
    private IWebDriverCreator webDriverCreator;

    private Map<String, WebDriver> namedDrivers = new HashMap<>();

    private volatile String currentSessionName = DEFAULT_DRIVER_NAME;
    private volatile WebDriver driver;

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFactory.class);
    private static final ThreadLocal<IWebDriverFactory> INSTANCES = new ThreadLocal<>();
    private static final String DEFAULT_DRIVER_NAME = "DEFAULT_DRIVER";
    protected DesiredCapabilities desiredCapabilities;

    @PostConstruct
    private void init(){
        INSTANCES.set(this);
    }

    @PreDestroy
    protected void destroy(){
        closeAllSessions();
        INSTANCES.remove();
    }

    public static IWebDriverFactory get(){
        return INSTANCES.get();
    }

    /**
     * Provides an instance of WebDriver. Creates this instance if it has not
     * been created yet, or returns an existed one otherwise.
     * @return an instance of WebDriver
     */
    @Override
    public WebDriver getDriver(){
        if (!isBrowserStarted()){
            LOGGER.info("Starting driver session [{}]", currentSessionName);
            driver = webDriverCreator.createDriver(desiredCapabilities);
            namedDrivers.put(currentSessionName, driver);
        }
        return driver;
    }

    @Override
    public void switchToSession(String sessionName){
        currentSessionName = sessionName;
        driver = namedDrivers.get(sessionName);
    }

    @Override
    public void switchToDefaultSession(){
        switchToSession(DEFAULT_DRIVER_NAME);
    }

    @Override
    public void closeSession(String sessionName){
        switchToSession(sessionName);
        closeDriver();
    }

    @Override
    public void closeAllSessions(){
        new HashSet<>(namedDrivers.keySet()).forEach(sessionName -> {
            switchToSession(sessionName);
            closeDriver();
        });
        namedDrivers.clear();
    }

    /**
     * Closes a browser and nulls WebDriver instance
     */
    @Override
    public void closeDriver(){
        if (isBrowserStarted()){
            LOGGER.info("Quit driver [{}]", currentSessionName);
            try {
                driver.quit();
            } finally{
                driver = null;
                namedDrivers.remove(currentSessionName);
            }
        }
    }

    /**
     * nulls WebDriver instance
     */
    @Override
    public void hardCloseDriver(){
        LOGGER.info("Terminating driver session [{}]", currentSessionName);
        driver = null;
        namedDrivers.remove(currentSessionName);
    }

    /**
     * Determines if browser is started and active
     */
    @Override
    public boolean isBrowserStarted() {
        return driver != null;
    }

    @Override
    public boolean isBrowserAlive(){
        if (isBrowserStarted()){
            try {
                return !driver.getWindowHandles().isEmpty();
            } catch (Exception ex){
                reporter.ignoredException(ex);
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Sets implicitly waiting timeout for the current WebDriver.
     * @param timeout - timeout in milliseconds
     */
    @Override
    public void setWaitingTimeout(int timeout){
        if (isBrowserStarted()) {
            driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void restoreWaitingTimeout(){
        if (isBrowserStarted()) {
            driver.manage().timeouts().implicitlyWait(properties.driver().timeoutsElement, TimeUnit.MILLISECONDS);
        }
    }

    public Dimension getWindowDimension(){
        String screenSize = properties.driver().screenResolution;
        if (StringUtils.isBlank(screenSize)){
            LOGGER.info("Screen resolution was not set. Default will be used: 1280x1024");
            return new Dimension(1280, 1024);
        }
        String[] strDimensions = screenSize.split("x");
        try {
            return new Dimension(Integer.parseInt(strDimensions[0]), Integer.parseInt(strDimensions[1]));
        } catch (Exception ex){
            LOGGER.warn("Incorrect screen resolution. Default will be used: 1280x1024", ex);
            return new Dimension(1280, 1024);
        }
    }

    @Override
    public void setDesiredCapabilities(DesiredCapabilities capabilities) {
        this.desiredCapabilities = capabilities;
    }
}