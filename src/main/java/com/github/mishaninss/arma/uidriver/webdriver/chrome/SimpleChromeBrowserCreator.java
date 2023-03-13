package com.github.mishaninss.arma.uidriver.webdriver.chrome;

import com.github.mishaninss.arma.data.WebDriverProperties;
import com.github.mishaninss.arma.exceptions.FrameworkConfigurationException;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.webdriver.ICapabilitiesProvider;
import com.github.mishaninss.arma.uidriver.webdriver.IWebDriverCreator;
import com.github.mishaninss.arma.uidriver.webdriver.NetworkConditions;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.mishaninss.arma.data.DataObject.GSON;

public class SimpleChromeBrowserCreator implements IWebDriverCreator {

  private static final String COULD_NOT_START_SESSION_MESSAGE = "Could not start a new browser session";

  @Autowired
  private WebDriverProperties properties;
  @Autowired
  private ICapabilitiesProvider capabilitiesProvider;
  @Reporter
  private IReporter reporter;

  @Override
  public WebDriver createDriver(Capabilities desiredCapabilities) {
    ExtendedChromeDriver webDriver;

    Capabilities capabilities = capabilitiesProvider.getCapabilities();
    capabilities.merge(desiredCapabilities);
    reporter.debug("Final desired capabilities: %s", GSON.toJson(capabilities));

    try {
      if (properties.driver().isRemote()) {
        String gridUrl = properties.driver().gridUrl;
        webDriver = new ExtendedChromeDriver(new URL(gridUrl), capabilities);
        webDriver.setFileDetector(new LocalFileDetector());
      } else {
        WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
        webDriver = new ExtendedChromeDriver(capabilities);
      }
      NetworkConditions networkConditions = properties.driver().getNetworkConditions();
      if (networkConditions != null) {
        webDriver.setNetworkConditions(networkConditions);
      }
    } catch (Exception ex) {
      throw new FrameworkConfigurationException(COULD_NOT_START_SESSION_MESSAGE, ex);
    }

    webDriver.manage().timeouts()
        .implicitlyWait(Duration.of(properties.driver().timeoutsElement, ChronoUnit.MILLIS));
    webDriver.manage().timeouts()
        .pageLoadTimeout(Duration.of(properties.driver().timeoutsPageLoad, ChronoUnit.MILLIS));
    return webDriver;
  }
}
