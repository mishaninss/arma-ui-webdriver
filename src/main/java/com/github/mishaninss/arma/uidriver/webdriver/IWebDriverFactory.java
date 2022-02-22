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

import java.util.Set;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by Sergey_Mishanin
 */
public interface IWebDriverFactory {

  String QUALIFIER = "IWebDriverFactory";

  /**
   * Provides an instance of WebDriver. Creates this instance if it has not been created yet, or
   * returns an existed one otherwise.
   *
   * @return an instance of WebDriver
   */
  WebDriver getDriver();

  void switchToSession(String sessionName);

  Set<String> getAvailableSessions();

  void switchToDefaultSession();

  void closeSession(String sessionName);

  void closeAllSessions();

  /**
   * Closes a browser and nulls WebDriver instance
   */
  void closeDriver();

  void hardCloseDriver();

  /**
   * Determines if browser is already started
   */
  boolean isBrowserStarted();

  boolean isBrowserAlive();

  /**
   * Sets implicitly waiting timeout for the current WebDriver.
   *
   * @param timeout - timeout in milliseconds
   */
  void setWaitingTimeout(int timeout);

  void restoreWaitingTimeout();

  Dimension getWindowDimension();

  void setDesiredCapabilities(DesiredCapabilities capabilities);

  String getSessionId();
}
