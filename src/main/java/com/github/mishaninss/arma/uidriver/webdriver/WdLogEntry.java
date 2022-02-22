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

import org.openqa.selenium.logging.LogEntry;
import com.github.mishaninss.arma.uidriver.interfaces.ILogEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class WdLogEntry implements ILogEntry {
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));

    private final Level level;
    private final long timestamp;
    private final String message;

    /**
     * @param level     the severity of the log entry
     * @param timestamp UNIX Epoch timestamp at which this log entry was created
     * @param message   ew  the log entry's message
     */
    public WdLogEntry(Level level, long timestamp, String message) {
        this.level = level;
        this.timestamp = timestamp;
        this.message = message;
    }

    public WdLogEntry(LogEntry seleniumLogEntry) {
        this(seleniumLogEntry.getLevel(), seleniumLogEntry.getTimestamp(), seleniumLogEntry.getMessage());
    }

    /**
     * Gets the logging entry's severity.
     *
     * @return severity of log statement
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Gets the timestamp of the log statement in milliseconds since UNIX Epoch.
     *
     * @return timestamp as UNIX Epoch
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the log entry's message.
     *
     * @return the log statement
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s",
                DATE_FORMAT.get().format(new Date(timestamp)), level, message);
    }
}
