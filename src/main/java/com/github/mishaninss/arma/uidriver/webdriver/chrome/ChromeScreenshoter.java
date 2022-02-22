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

package com.github.mishaninss.arma.uidriver.webdriver.chrome;

import com.github.mishaninss.arma.uidriver.webdriver.WdDefaultScreenshoter;
import org.openqa.selenium.OutputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile("chrome")
public class ChromeScreenshoter extends WdDefaultScreenshoter {

    @Autowired
    private ChromeExtender chromeExtender;

    @Override
    @SuppressWarnings("unchecked")
    protected <X> X takeScreenshot(OutputType<X> seleniumOutputType) {
        try {
            if (OutputType.BASE64.equals(seleniumOutputType)) {
                return (X) chromeExtender.takeScreenshotAsString();
            } else if (OutputType.BYTES.equals(seleniumOutputType)) {
                return (X) chromeExtender.takeScreenshot();
            } else if (OutputType.FILE.equals(seleniumOutputType)) {
                return (X) chromeExtender.takeScreenshotAsFile();
            } else {
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
    }
}
