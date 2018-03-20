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

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.http.HttpMethod;

import java.net.URL;

class ExtendedChromeDriverHttpCommandExecutor extends HttpCommandExecutor {
    private static final ImmutableMap<String, CommandInfo> CHROME_COMMAND_NAME_TO_URL;

    ExtendedChromeDriverHttpCommandExecutor(URL addressOfRemoteServer) {
        super(CHROME_COMMAND_NAME_TO_URL, addressOfRemoteServer);
    }

    static {
        CHROME_COMMAND_NAME_TO_URL = ImmutableMap.of(
                "launchApp", new CommandInfo("/session/:sessionId/chromium/launch_app", HttpMethod.POST),
                "sendCommandWithResult", new CommandInfo("/session/:sessionId/chromium/send_command_and_get_result", HttpMethod.POST),
                "setNetworkConditions", new CommandInfo("/session/:sessionId/chromium/network_conditions", HttpMethod.POST)
        );
    }
}
