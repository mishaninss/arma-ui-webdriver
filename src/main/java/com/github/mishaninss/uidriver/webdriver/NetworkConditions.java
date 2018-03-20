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

public enum NetworkConditions {
    OFFLINE(true, 0, 0, 0),
    GPRS(false, 50 * 1024, 20 * 1024, 500),
    REGULAR_2G(false, 250 * 1024, 50 * 1024, 300),
    GOOD_2G(false, 450 * 1024, 150 * 1024, 150),
    REGULAR_3G(false, 750 * 1024, 250 * 1024, 100),
    GOOD_3G(false, 1.5 * 1024 * 1024, 750 * 1024, 40),
    REGULAR_4G(false, 4 * 1024 * 1024, 3 * 1024 * 1024, 20),
    DSL(false, 2 * 1024 * 1024, 1024 * 1024, 5),
    WIFI(false, 30 * 1024 * 1024, 15 * 1024 * 1024, 2);

    private boolean offline;
    private double latency;
    private double downloadThroughput;
    private double uploadThroughput;

    NetworkConditions(boolean offline, double downloadThroughput, double uploadThroughput, double latency) {
        this.offline = offline;
        this.latency = latency;
        this.downloadThroughput = downloadThroughput;
        this.uploadThroughput = uploadThroughput;
    }

    public boolean isOffline() {
        return offline;
    }

    public double getLatency() {
        return latency;
    }

    public double getDownloadThroughput() {
        return downloadThroughput;
    }

    public double getUploadThroughput() {
        return uploadThroughput;
    }
}
