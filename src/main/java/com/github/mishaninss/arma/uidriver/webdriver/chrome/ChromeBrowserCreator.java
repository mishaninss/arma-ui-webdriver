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
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.LocalFileDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.github.mishaninss.arma.data.DataObject.GSON;

@Component
@Profile("chrome")
public class ChromeBrowserCreator implements IWebDriverCreator {

  private static final String COULD_NOT_START_SESSION_MESSAGE = "Could not start a new browser session";

  @Autowired
  private WebDriverProperties properties;
  @Autowired
  private ICapabilitiesProvider capabilitiesProvider;
  @Autowired
  private IChromeDriverServiceCreator chromeDriverServiceCreator;
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
        ChromeDriverService chromeDriverService = chromeDriverServiceCreator
            .getChromeDriverService();
        if (!chromeDriverService.isRunning()) {
          chromeDriverService.start();
        }
        webDriver = new ExtendedChromeDriver(chromeDriverService, capabilities);
      }
      NetworkConditions networkConditions = properties.driver().getNetworkConditions();
      if (networkConditions != null) {
        webDriver.setNetworkConditions(networkConditions);
      }
    } catch (Exception ex) {
      throw new FrameworkConfigurationException(COULD_NOT_START_SESSION_MESSAGE, ex);
    }

    webDriver.manage().timeouts()
        .implicitlyWait(properties.driver().timeoutsElement, TimeUnit.MILLISECONDS);
    webDriver.manage().timeouts()
        .pageLoadTimeout(properties.driver().timeoutsPageLoad, TimeUnit.MILLISECONDS);
    return webDriver;
  }
}
