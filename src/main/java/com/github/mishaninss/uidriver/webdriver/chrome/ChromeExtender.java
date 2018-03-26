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

import com.github.mishaninss.uidriver.webdriver.IWebDriverFactory;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("chrome")
public class ChromeExtender {

    @Autowired
    private IWebDriverFactory webDriverFactory;

    public byte[] takeScreenshot() throws IOException {
        Object visibleSize = evaluate("({x:0,y:0,width:window.innerWidth,height:window.innerHeight})");
        Long visibleW = jsonValue(visibleSize, "result.value.width", Long.class);
        Long visibleH = jsonValue(visibleSize, "result.value.height", Long.class);

        Object contentSize = send("Page.getLayoutMetrics", new HashMap<>());
        Long cw = jsonValue(contentSize, "contentSize.width", Long.class);
        Long ch = jsonValue(contentSize, "contentSize.height", Long.class);

        send("Emulation.setDeviceMetricsOverride",
                ImmutableMap.of("width", cw, "height", ch, "deviceScaleFactor", Long.valueOf(1), "mobile", Boolean.FALSE, "fitWindow", Boolean.FALSE)
        );
        send("Emulation.setVisibleSize", ImmutableMap.of("width", cw, "height", ch));

        Object value = send("Page.captureScreenshot", ImmutableMap.of("format", "png", "fromSurface", Boolean.TRUE));

        send("Emulation.setVisibleSize", ImmutableMap.of("x", Long.valueOf(0), "y", Long.valueOf(0), "width", visibleW, "height", visibleH));

        String image = jsonValue(value, "data", String.class);
        if (image == null){
            return new byte[0];
        } else {
            return Base64.getDecoder().decode(image);
        }
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
        if(response.getStatus() == null || response.getStatus() != 0) {
            throw new WebDriverException("Command '" + cmd + "' failed: " + value);
        }
        if(null == value)
            throw new WebDriverException("Null response value to command '" + cmd + "'");
        return value;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static <T> T jsonValue(@Nonnull Object map, @Nonnull String path, @Nonnull Class<T> type) {
        String[] segs = path.split("\\.");
        Object current = map;
        for(String name: segs) {
            Map<String, Object> cm = (Map<String, Object>) current;
            Object o = cm.get(name);
            if(null == o)
                return null;
            current = o;
        }
        return (T) current;
    }
}
