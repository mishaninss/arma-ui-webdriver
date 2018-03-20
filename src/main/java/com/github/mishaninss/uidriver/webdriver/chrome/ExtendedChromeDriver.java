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

package com.github.mishaninss.uidriver.webdriver.chrome;

import com.github.mishaninss.uidriver.webdriver.NetworkConditions;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.html5.*;
import org.openqa.selenium.interactions.HasTouchScreen;
import org.openqa.selenium.interactions.TouchScreen;
import org.openqa.selenium.mobile.NetworkConnection;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.RemoteTouchScreen;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.html5.RemoteLocationContext;
import org.openqa.selenium.remote.html5.RemoteWebStorage;
import org.openqa.selenium.remote.mobile.RemoteNetworkConnection;

import java.net.URL;

public class ExtendedChromeDriver extends RemoteWebDriver implements LocationContext, WebStorage, HasTouchScreen, NetworkConnection {
    private RemoteLocationContext locationContext;
    private RemoteWebStorage webStorage;
    private TouchScreen touchScreen;
    private RemoteNetworkConnection networkConnection;

    public ExtendedChromeDriver(Capabilities capabilities) {
        this(ChromeDriverService.createDefaultService(), capabilities);
    }

    public ExtendedChromeDriver(ChromeDriverService service, Capabilities capabilities) {
        super(new ExtendedChromeDriverCommandExecutor(service), capabilities);
        this.locationContext = new RemoteLocationContext(this.getExecuteMethod());
        this.webStorage = new RemoteWebStorage(this.getExecuteMethod());
        this.touchScreen = new RemoteTouchScreen(this.getExecuteMethod());
        this.networkConnection = new RemoteNetworkConnection(this.getExecuteMethod());
    }

    public ExtendedChromeDriver(URL remoteAddress, Capabilities desiredCapabilities) {
        super(new ExtendedChromeDriverHttpCommandExecutor(remoteAddress), desiredCapabilities);
        this.locationContext = new RemoteLocationContext(this.getExecuteMethod());
        this.webStorage = new RemoteWebStorage(this.getExecuteMethod());
        this.touchScreen = new RemoteTouchScreen(this.getExecuteMethod());
        this.networkConnection = new RemoteNetworkConnection(this.getExecuteMethod());
    }

    @Override
    public void setFileDetector(FileDetector detector) {
        throw new WebDriverException("Setting the file detector only works on remote webdriver instances obtained via RemoteWebDriver");
    }

    @Override
    public LocalStorage getLocalStorage() {
        return this.webStorage.getLocalStorage();
    }

    @Override
    public SessionStorage getSessionStorage() {
        return this.webStorage.getSessionStorage();
    }

    @Override
    public Location location() {
        return this.locationContext.location();
    }

    @Override
    public void setLocation(Location location) {
        this.locationContext.setLocation(location);
    }

    @Override
    public TouchScreen getTouch() {
        return this.touchScreen;
    }

    @Override
    public ConnectionType getNetworkConnection() {
        return this.networkConnection.getNetworkConnection();
    }

    @Override
    public ConnectionType setNetworkConnection(ConnectionType type) {
        return this.networkConnection.setNetworkConnection(type);
    }

    public void launchApp(String id) {
        this.execute("launchApp", ImmutableMap.of("id", id));
    }

    public void setNetworkConditions(boolean offline, double latency, double downloadThroughput, double uploadThroughput) {
        this.execute("setNetworkConditions",
                ImmutableMap.of (
                "network_conditions",
                    ImmutableMap.of(
                        "offline", offline,
                        "latency", latency,
                        "download_throughput", downloadThroughput,
                        "upload_throughput", uploadThroughput)));
    }

    public void setNetworkConditions(NetworkConditions networkConditions) {
        setNetworkConditions(
                networkConditions.isOffline(),
                networkConditions.getLatency(),
                networkConditions.getDownloadThroughput(),
                networkConditions.getUploadThroughput()
        );
    }
}
