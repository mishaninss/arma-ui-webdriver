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

import org.apache.commons.exec.OS;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Provides a single instance of WebDriverService
 *
 * @author Sergey Mishanin
 */
@Component
@Profile("chrome")
public class ChromeDriverServiceCreatorImpl implements IChromeDriverServiceCreator, DisposableBean {

    @Reporter
    private IReporter reporter;

    private static final AtomicBoolean useCliCleanup = new AtomicBoolean(true);
    private volatile Set<ChromeDriverService> chromeDriverServices = new HashSet<>();

    @Override
    public void destroy() {
        terminateChrome();
    }

    public ChromeDriverService getChromeDriverService() {
        ChromeDriverService chromeDriverService = new ChromeDriverService.Builder()
                .withSilent(true)
                .usingAnyFreePort()
                .build();
        chromeDriverServices.add(chromeDriverService);

        return chromeDriverService;
    }

    public void dismissChromeDriverService() {
        //TODO this method is useless in the current implementation
    }

    public void terminateChrome() {
        chromeDriverServices.forEach(chromeDriverService -> {
                    try {
                        if (OS.isFamilyUnix() && useCliCleanup.get()) {
                            int port = chromeDriverService.getUrl().getPort();
                            String sh = "for drv in $(pgrep -f \"chromedriver --port=" + port + "\");do for p in $(pgrep -P $drv);do kill -KILL $p;done;kill -KILL $drv;done;";
                            reporter.debug(sh);
                            String[] command = {"/bin/bash", "-c", sh};
                            Runtime.getRuntime().exec(command);
                        }
                    } catch (IOException e) {
                        useCliCleanup.set(false);
                        reporter.ignoredException(e);
                    } finally {
                        chromeDriverService.stop();
                    }
                }
        );
        chromeDriverServices.clear();
    }
}