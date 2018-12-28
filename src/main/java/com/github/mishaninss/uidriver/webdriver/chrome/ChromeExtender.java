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

import com.github.mishaninss.data.UiCommonsProperties;
import com.github.mishaninss.uidriver.webdriver.IWebDriverFactory;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("chrome")
public class ChromeExtender {
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";

    @Autowired
    private IWebDriverFactory webDriverFactory;
    @Autowired
    private UiCommonsProperties properties;

    public byte[] takeScreenshot() throws IOException {
        String image = takeScreenshotAsString();
        if (image == null) {
            return new byte[0];
        } else {
            return Base64.getDecoder().decode(image);
        }
    }

    public String takeScreenshotAsString() throws IOException {
        Object visibleSize = evaluate("({x:0,y:0,width:window.innerWidth,height:window.innerHeight})");
        Long value = jsonValue(visibleSize, "result.value.width", Long.class);
        long visibleW = value != null ? value : 0;
        value = jsonValue(visibleSize, "result.value.height", Long.class);
        long visibleH = value != null ? value : 0;

        Object contentSize = send("Page.getLayoutMetrics", new HashMap<>());
        value = jsonValue(contentSize, "contentSize.width", Long.class);
        long cw = value != null ? value : 0;
        value = jsonValue(contentSize, "contentSize.height", Long.class);
        long ch = value != null ? value : 0;

        send("Emulation.setDeviceMetricsOverride",
                ImmutableMap.of(WIDTH, cw, HEIGHT, ch, "deviceScaleFactor", 1, "mobile", Boolean.FALSE, "fitWindow", Boolean.FALSE)
        );
        send("Emulation.setVisibleSize", ImmutableMap.of(WIDTH, cw, HEIGHT, ch));

        Object screenshotResponse = send("Page.captureScreenshot", ImmutableMap.of("format", "png", "fromSurface", Boolean.TRUE));

        send("Emulation.setVisibleSize", ImmutableMap.of("x", 0, "y", 0, WIDTH, visibleW, HEIGHT, visibleH));

        return jsonValue(screenshotResponse, "data", String.class);
    }

    public File takeScreenshotAsFile() throws IOException {
        String screenshotsDir = properties.framework().screenshotsDir;
        File dir = new File(screenshotsDir);
        if (!dir.exists()) {
            FileUtils.forceMkdir(dir);
        }
        String fileName = "Screenshot_" + new Date().getTime();
        File screenshotFile = new File(dir, fileName);
        FileUtils.writeByteArrayToFile(screenshotFile, takeScreenshot());
        return screenshotFile;
    }

    @Nonnull
    private Object evaluate(@Nonnull String script) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("returnByValue", Boolean.TRUE);
        param.put("expression", script);

        return send("Runtime.evaluate", param);
    }

    @Nonnull
    private Object send(@Nonnull String cmd, @Nonnull Map<String, Object> params) throws IOException {
        ExtendedChromeDriver driver = (ExtendedChromeDriver) webDriverFactory.getDriver();
        Map<String, Object> exe = ImmutableMap.of("cmd", cmd, "params", params);
        Command xc = new Command(driver.getSessionId(), "sendCommandWithResult", exe);
        Response response = driver.getCommandExecutor().execute(xc);

        Object value = response.getValue();
        if (response.getStatus() == null || response.getStatus() != 0) {
            throw new WebDriverException("Command '" + cmd + "' failed: " + value);
        }
        if (null == value)
            throw new WebDriverException("Null response value to command '" + cmd + "'");
        return value;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T> T jsonValue(@Nonnull Object map, @Nonnull String path, @Nonnull Class<T> type) {
        String[] segs = path.split("\\.");
        Object current = map;
        for (String name : segs) {
            Map<String, Object> cm = (Map<String, Object>) current;
            Object o = cm.get(name);
            if (null == o)
                return null;
            current = o;
        }
        return (T) current;
    }
}
