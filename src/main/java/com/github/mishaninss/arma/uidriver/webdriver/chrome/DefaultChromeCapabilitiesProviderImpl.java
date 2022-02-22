package com.github.mishaninss.arma.uidriver.webdriver.chrome;

import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.interfaces.IDownloadsManager;
import com.github.mishaninss.arma.uidriver.webdriver.DesiredCapabilitiesLoader;
import com.github.mishaninss.arma.uidriver.webdriver.ICapabilitiesProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.github.mishaninss.arma.data.DataObject.GSON;

@Component
@Profile("chrome")
public class DefaultChromeCapabilitiesProviderImpl implements ICapabilitiesProvider {

  public static final String CAPABILITIES_PROPERTY_PREFIX = "arma.driver.chrome.capability.";
  public static final String CAPABILITIES_FILE_PROPERTY = "arma.driver.chrome.capabilities.file";
  public static final String EXTENSION_FILES_PROPERTY = "arma.driver.chrome.capabilities.extensions";
  private static final String DEFAULT_CHROME_CAPABILITIES_FILE = "./chrome_capabilities.properties";
  @Value("${" + CAPABILITIES_FILE_PROPERTY + ":" + DEFAULT_CHROME_CAPABILITIES_FILE + "}")
  public String capabilitiesFile;
  @Value("#{'${" + EXTENSION_FILES_PROPERTY + ":}'.split(',')}")
  private List<String> extensions;

  @Autowired
  private WebDriverProperties properties;
  @Autowired
  private DesiredCapabilitiesLoader capabilitiesLoader;
  @Reporter
  private IReporter reporter;
  @Autowired
  private IDownloadsManager downloadsManager;

  private ChromeOptions getChromeOptions() {
    var chromeOptions = new ChromeOptions();
    chromeOptions.addArguments(
        "disable-blink-features=BlockCredentialedSubresources",
        "disable-infobars",
        "ignore-certificate-errors");
    if (properties.driver().shouldCollectPerfLogs()) {
      Map<String, Object> perfLogPrefs = new HashMap<>();
      if (properties.driver().collectTracingLogs) {
        perfLogPrefs.put("traceCategories", "blink.user_timing, loading");
      }
      if (properties.driver().collectNetworkLogs) {
        perfLogPrefs.put("enableNetwork", true);
      }
      chromeOptions.setExperimentalOption("perfLoggingPrefs", perfLogPrefs);
    }
    if (CollectionUtils.isNotEmpty(extensions)) {
      extensions.forEach(extension -> {
        if (StringUtils.isNotBlank(extension)) {
          chromeOptions.addExtensions(FileUtils.getFile(extension));
        }
      });
    }
    if (StringUtils.isNotBlank(downloadsManager.getDownloadsDir())) {
      Map<String, Object> prefs = new HashMap<>();
      prefs.put("download.default_directory", downloadsManager.getDownloadsDir());
      chromeOptions.setExperimentalOption("prefs", prefs);
    }

    reporter.debug("Default chrome options:\n%s", GSON.toJson(chromeOptions.asMap()));

    return chromeOptions;
  }

  private MutableCapabilities getChromeCapabilities() {
    ChromeOptions chromeOptions = getChromeOptions();
    LoggingPreferences logPrefs = new LoggingPreferences();
    if (properties.driver().areConsoleLogsEnabled()) {
      logPrefs.enable(LogType.BROWSER, properties.driver().browserLogsLevel);
    }
    if (properties.driver().shouldCollectPerfLogs()) {
      logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
    }
    chromeOptions.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
    setUnexpectedAlertBehaviour(chromeOptions);
    return chromeOptions;
  }

  private MutableCapabilities getGridCapabilities() {
    ChromeOptions chromeOptions = getChromeOptions();
    if (properties.driver().shouldCollectPerfLogs()) {
      LoggingPreferences logPrefs = new LoggingPreferences();
      logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
      chromeOptions.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
    }
    String browserVersion = properties.driver().browserVersion;
    if (StringUtils.isNoneBlank(browserVersion)) {
      chromeOptions.setCapability(CapabilityType.VERSION, browserVersion);
    }
    String platform = properties.driver().platformName;
    if (StringUtils.isNoneBlank(platform)) {
      chromeOptions.setCapability(CapabilityType.PLATFORM_NAME,
          Platform.valueOf(platform.toUpperCase()));
    }
    setUnexpectedAlertBehaviour(chromeOptions);
    return chromeOptions;
  }

  private void setUnexpectedAlertBehaviour(ChromeOptions caps) {
    String unexpectedAlertBehaviour = properties.driver().unexpectedAlertBehaviour;
    if (StringUtils.isNoneBlank(unexpectedAlertBehaviour)) {
      switch (unexpectedAlertBehaviour.toLowerCase()) {
        case "accept":
          caps.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
          break;
        case "dismiss":
          caps.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.DISMISS);
          break;
        default:
          caps.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.IGNORE);
      }
    }
  }

  @Override
  public Capabilities getCapabilities() {
    MutableCapabilities capabilities = properties.driver().isRemote() ?
        getGridCapabilities() :
        getChromeCapabilities();
    capabilities = mergeCapabilities(
        capabilitiesLoader.loadCapabilities(capabilitiesFile, ChromeOptions.class), capabilities);
    capabilities = mergeCapabilities(
        capabilitiesLoader.loadEnvironmentProperties(ChromeOptions.class), capabilities);
    capabilities = mergeCapabilities(
        capabilitiesLoader.loadEnvironmentProperties(CAPABILITIES_PROPERTY_PREFIX,
            ChromeOptions.class), capabilities);
    return capabilities;
  }

  public static MutableCapabilities mergeCapabilities(Capabilities from, MutableCapabilities to) {
    Map<String, Object> resultMap = mergeMaps((Map<String, Object>) from.asMap(), to.asMap());
    return new MutableCapabilities(resultMap);
  }

  public static Map<String, Object> mergeMaps(Map<String, Object> from, Map<String, Object> to) {
    if (MapUtils.isEmpty(to)) {
      return new TreeMap<>(from);
    }
    Map<String, Object> result = new TreeMap<>(to);
    from.forEach((key, value) -> {
      if (value instanceof Map) {
        result.put(key, mergeMaps((Map<String, Object>) value, (Map<String, Object>) to.get(key)));
      } else if (value instanceof Collection) {
        Set<String> resultValues = new LinkedHashSet<>((Collection<String>) value);
        if (to.get(key) != null) {
          resultValues.addAll((Collection<String>) to.get(key));
        }
        result.put(key, new ArrayList<>(resultValues));
      } else {
        result.put(key, value);
      }
    });
    return result;
  }
}