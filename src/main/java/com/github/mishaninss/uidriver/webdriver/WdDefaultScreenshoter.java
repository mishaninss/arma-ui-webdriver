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

import com.github.mishaninss.reporting.IReporter;
import com.github.mishaninss.reporting.Reporter;
import com.github.mishaninss.uidriver.interfaces.IOutputType;
import com.github.mishaninss.uidriver.interfaces.IScreenshoter;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Profile("!chrome")
public class WdDefaultScreenshoter implements IScreenshoter {

    @Autowired
    private IWebDriverFactory webDriverFactory;
    @Reporter
    private IReporter reporter;

    @Override
    public byte[] takeScreenshot() {
        try {
            return takeScreenshotAs(IOutputType.BYTES);
        } catch (Exception ex) {
            reporter.debug("Could not take a screenshot", ex);
            return new byte[0];
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X takeScreenshotAs(IOutputType<X> outputType) {
        Class<?> clazz = outputType.getMyType();
        if (String.class.equals(clazz)) {
            return (X) takeScreenshot(OutputType.BASE64);
        }
        if (byte[].class.equals(clazz)) {
            return (X) takeScreenshot(OutputType.BYTES);
        }
        if (File.class.equals(clazz)) {
            return (X) takeScreenshot(OutputType.FILE);
        }
        throw new IllegalArgumentException("Unsupported format of output type " + clazz);
    }

    protected <X> X takeScreenshot(OutputType<X> seleniumOutputType) {
        return ((TakesScreenshot) webDriverFactory.getDriver()).getScreenshotAs(seleniumOutputType);
    }
}
