package com.github.mishaninss.arma.config;

import com.github.mishaninss.arma.aspects.IInteractiveElementExceptionBuilder;
import com.github.mishaninss.arma.aspects.SeleniumAspects;
import com.github.mishaninss.arma.aspects.UiDriverAspects;
import com.github.mishaninss.arma.aspects.WebdriverInteractiveElementExceptionBuilder;
import com.github.mishaninss.arma.uidriver.annotations.AlertHandler;
import com.github.mishaninss.arma.uidriver.annotations.BrowserDriver;
import com.github.mishaninss.arma.uidriver.annotations.ElementDriver;
import com.github.mishaninss.arma.uidriver.annotations.ElementsDriver;
import com.github.mishaninss.arma.uidriver.annotations.PageDriver;
import com.github.mishaninss.arma.uidriver.annotations.SelectElementDriver;
import com.github.mishaninss.arma.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IAlertHandler;
import com.github.mishaninss.arma.uidriver.interfaces.IBrowserDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementsDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IPageDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ISelectElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;
import com.github.mishaninss.arma.uidriver.webdriver.WdAlertHandler;
import com.github.mishaninss.arma.uidriver.webdriver.WdBrowserDriver;
import com.github.mishaninss.arma.uidriver.webdriver.WdElementDriver;
import com.github.mishaninss.arma.uidriver.webdriver.WdElementsDriver;
import com.github.mishaninss.arma.uidriver.webdriver.WdPageDriver;
import com.github.mishaninss.arma.uidriver.webdriver.WdSelectElementDriver;
import com.github.mishaninss.arma.uidriver.webdriver.WdWaitingDriver;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(UiCommonsConfig.class)
public class UiWdConfig {

  @Bean
  public SeleniumAspects seleniumAspects() {
    return Aspects.aspectOf(SeleniumAspects.class);
  }

  @Bean
  public UiDriverAspects uiDriverAspects() {
    return Aspects.aspectOf(UiDriverAspects.class);
  }

  @Bean
  public IInteractiveElementExceptionBuilder exceptionBuilder() {
    return new WebdriverInteractiveElementExceptionBuilder();
  }

  @Bean(IWaitingDriver.QUALIFIER)
  @WaitingDriver
  public IWaitingDriver waitingDriver() {
    return new WdWaitingDriver();
  }

  @Bean(IBrowserDriver.QUALIFIER)
  @BrowserDriver
  public IBrowserDriver browserDriver() {
    return new WdBrowserDriver();
  }

  @Bean(IElementDriver.QUALIFIER)
  @ElementDriver
  public IElementDriver elementDriver() {
    return new WdElementDriver();
  }

  @Bean(IElementsDriver.QUALIFIER)
  @ElementsDriver
  public IElementsDriver elementsDriver() {
    return new WdElementsDriver();
  }

  @Bean(IPageDriver.QUALIFIER)
  @PageDriver
  public IPageDriver pageDriver() {
    return new WdPageDriver();
  }

  @Bean(ISelectElementDriver.QUALIFIER)
  @SelectElementDriver
  public ISelectElementDriver selectElementDriver() {
    return new WdSelectElementDriver();
  }

  @Bean(IAlertHandler.QUALIFIER)
  @AlertHandler
  public IAlertHandler alertHandler() {
    return new WdAlertHandler();
  }

}
