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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.StreamSupport;

@Component
public class DesiredCapabilitiesLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final String CAPABILITIES_PROPERTY_PREFIX = "arma.driver.capability.";
    public static final String CAPABILITIES_FILE_PROPERTY = "arma.driver.capabilities.file";
    @Value("${" + CAPABILITIES_FILE_PROPERTY + ":/capabilities.properties}")
    public String defaultCapabilitiesFile;
    @Reporter
    private IReporter reporter;
    @Autowired
    private Environment environment;

    public Capabilities loadCapabilities() {
        return loadCapabilities(defaultCapabilitiesFile, DesiredCapabilities.class);
    }

    public @NonNull
    Capabilities loadCapabilities(@Nullable String capabilitiesFilePath, @NonNull Class<? extends MutableCapabilities> capabilitiesClass) {
        if (StringUtils.isBlank(capabilitiesFilePath)) {
            reporter.debug("Provided blank desired capabilities file path");
            return new DesiredCapabilities();
        }

        File capabilitiesFile = getCapabilitiesFile(capabilitiesFilePath);
        if (capabilitiesFile == null || !capabilitiesFile.exists()) {
            reporter.debug("Could not resolve desired capabilities file path [" + capabilitiesFilePath + "]");
            return new DesiredCapabilities();
        }

        String format = FilenameUtils.getExtension(capabilitiesFilePath).toLowerCase();
        switch (format) {
            case "properties":
                return loadPropertiesFile(capabilitiesFile, capabilitiesClass);
            case "json":
                return loadJsonFile(capabilitiesFile, capabilitiesClass);
            default:
                reporter.warn("Unknown desired capabilities file format [{}]. Supported formats: .properties, .json", format);
        }
        return new DesiredCapabilities();
    }

    private @NonNull
    File getCapabilitiesFile(@NonNull String capabilitiesFilePath) {
        URL resource = DesiredCapabilities.class.getResource(capabilitiesFilePath);
        if (resource != null) {
            return FileUtils.toFile(resource);
        } else {
            return new File(capabilitiesFilePath);
        }
    }

    public @NonNull
    Capabilities loadPropertiesFile(@NonNull File capabilitiesFile, Class<? extends MutableCapabilities> capabilitiesClass) {
        try {
            Properties props = new Properties();
            MutableCapabilities capabilities = capabilitiesClass.newInstance();
            props.load(new FileInputStream(capabilitiesFile));
            props.forEach((key, value) -> capabilities.setCapability(key.toString(), value));
            reporter.debug("Desired capabilities loaded from file [{}]: {}", capabilitiesFile, GSON.toJson(capabilities.asMap()));
            return capabilities;
        } catch (Exception ex) {
            reporter.debug("Could not load desired capabilities from file [" + capabilitiesFile + "]", ex);
            return new DesiredCapabilities();
        }
    }

    public Capabilities loadJsonFile(File capabilitiesFile, Class<? extends Capabilities> capabilitiesClass) {
        try {
            String json = FileUtils.readFileToString(capabilitiesFile, "UTF-8");
            Capabilities capabilities = GSON.fromJson(json, capabilitiesClass);
            reporter.debug("Desired capabilities loaded from file [{}]: {}", capabilitiesFile, GSON.toJson(capabilities.asMap()));
            return capabilities;
        } catch (Exception ex) {
            reporter.debug("Could not load desired capabilities from file [" + capabilitiesFile + "]", ex);
            return null;
        }
    }

    public Capabilities loadEnvironmentProperties() {
        return loadEnvironmentProperties(CAPABILITIES_PROPERTY_PREFIX, DesiredCapabilities.class);
    }

    public Capabilities loadEnvironmentProperties(Class<? extends MutableCapabilities> capabilitiesClass) {
        return loadEnvironmentProperties(CAPABILITIES_PROPERTY_PREFIX, capabilitiesClass);
    }

    public Capabilities loadEnvironmentProperties(String prefix, Class<? extends MutableCapabilities> capabilitiesClass) {
        try {
            MutableCapabilities capabilities = capabilitiesClass.newInstance();
            MutablePropertySources propSrcs = ((AbstractEnvironment) environment).getPropertySources();
            StreamSupport.stream(propSrcs.spliterator(), false)
                    .filter(ps -> ps instanceof EnumerablePropertySource)
                    .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                    .flatMap(Arrays::<String>stream)
                    .forEach(propName -> {
                                if (propName.startsWith(prefix)) {
                                    capabilities.setCapability(StringUtils.substringAfter(propName, prefix), environment.getProperty(propName));
                                }
                            }
                    );
            reporter.debug("Desired capabilities loaded from Environment properties with prefix [{}] : {}", prefix, GSON.toJson(capabilities.asMap()));
            return capabilities;
        } catch (Exception ex) {
            reporter.debug("Could not load desired capabilities from file with prefix[" + prefix + "]", ex);
            return null;
        }
    }
}
