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

import com.github.mishaninss.uidriver.interfaces.ICookie;
import org.openqa.selenium.Cookie;

import java.util.Date;
import java.util.Objects;

public class WdCookie implements ICookie {

    private Cookie seleniumCookie;

    /**
     * Creates an insecure non-httpOnly cookie with no domain specified.
     *
     * @param name The name of the cookie; may not be null or an empty string.
     * @param value The cookie value; may not be null.
     * @param path The path the cookie is visible to. If left blank or set to null, will be set to
     *        "/".
     * @param expiry The cookie's expiration date; may be null.
     */
    public WdCookie(String name, String value, String path, Date expiry) {
        this(name, value, null, path, expiry);
    }

    /**
     * Creates an insecure non-httpOnly cookie.
     *
     * @param name The name of the cookie; may not be null or an empty string.
     * @param value The cookie value; may not be null.
     * @param domain The domain the cookie is visible to.
     * @param path The path the cookie is visible to. If left blank or set to null, will be set to
     *        "/".
     * @param expiry The cookie's expiration date; may be null.
     */
    public WdCookie(String name, String value, String domain, String path, Date expiry) {
        this(name, value, domain, path, expiry, false);
    }

    /**
     * Creates a non-httpOnly cookie.
     *
     * @param name The name of the cookie; may not be null or an empty string.
     * @param value The cookie value; may not be null.
     * @param domain The domain the cookie is visible to.
     * @param path The path the cookie is visible to. If left blank or set to null, will be set to
     *        "/".
     * @param expiry The cookie's expiration date; may be null.
     * @param isSecure Whether this cookie requires a secure connection.
     */
    public WdCookie(String name, String value, String domain, String path, Date expiry,
                  boolean isSecure) {
        this(name, value, domain, path, expiry, isSecure, false);
    }

    /**
     * Creates a cookie.
     *
     * @param name The name of the cookie; may not be null or an empty string.
     * @param value The cookie value; may not be null.
     * @param domain The domain the cookie is visible to.
     * @param path The path the cookie is visible to. If left blank or set to null, will be set to
     *        "/".
     * @param expiry The cookie's expiration date; may be null.
     * @param isSecure Whether this cookie requires a secure connection.
     * @param isHttpOnly Whether this cookie is a httpOnly cooke.
     */
    public WdCookie(String name, String value, String domain, String path, Date expiry,
                  boolean isSecure, boolean isHttpOnly) {
        seleniumCookie = new Cookie(name, value, domain, path, expiry, isSecure, isHttpOnly);
    }

    /**
     * Create a cookie for the default path with the given name and value with no expiry set.
     *
     * @param name The cookie's name
     * @param value The cookie's value
     */
    public WdCookie(String name, String value) {
        this(name, value, "/", null);
    }

    public WdCookie(Cookie seleniumCookie){
        this.seleniumCookie = seleniumCookie;
    }

    /**
     * Create a cookie.
     *
     * @param name The cookie's name
     * @param value The cookie's value
     * @param path The path the cookie is for
     */
    public WdCookie(String name, String value, String path) {
        this(name, value, path, null);
    }

    @Override
    public String getName() {
        return seleniumCookie.getName();
    }

    @Override
    public String getValue() {
        return seleniumCookie.getValue();
    }

    @Override
    public String getDomain() {
        return seleniumCookie.getDomain();
    }

    @Override
    public String getPath() {
        return seleniumCookie.getPath();
    }

    @Override
    public boolean isSecure() {
        return seleniumCookie.isSecure();
    }

    @Override
    public boolean isHttpOnly() {
        return seleniumCookie.isHttpOnly();
    }

    @Override
    public Date getExpiry() {
        return seleniumCookie.getExpiry();
    }

    @Override
    public void validate() {
        seleniumCookie.validate();
    }

    @Override
    public String toString() {
        return seleniumCookie.toString();
    }

    /**
     * Two cookies are equal if the name and value match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WdCookie)) {
            return false;
        }

        WdCookie cookie = (WdCookie) o;
        return Objects.equals(seleniumCookie, cookie.seleniumCookie);
    }

    @Override
    public int hashCode() {
        return seleniumCookie.hashCode();
    }

    public Cookie toSeleniumCookie(){
        return seleniumCookie;
    }
}
