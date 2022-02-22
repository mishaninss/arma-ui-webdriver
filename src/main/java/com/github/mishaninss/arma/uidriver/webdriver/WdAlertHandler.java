package com.github.mishaninss.arma.uidriver.webdriver;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.mishaninss.arma.exceptions.SessionLostException;
import com.github.mishaninss.arma.reporting.IReporter;
import com.github.mishaninss.arma.reporting.Reporter;
import com.github.mishaninss.arma.uidriver.annotations.WaitingDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IAlertHandler;
import com.github.mishaninss.arma.uidriver.interfaces.IWaitingDriver;

/**
 * Implementation of {@link com.github.mishaninss.arma.uidriver.interfaces.IAlertHandler} interface based on WebDriver engine
 * Provides methods to interact with alert popups.
 *
 * @author Sergey Mishanin
 */
@Component
public class WdAlertHandler implements IAlertHandler {
    @Reporter
    private IReporter reporter;
    @Autowired
    private IWebDriverFactory webDriverFactory;
    @WaitingDriver
    private IWaitingDriver waitingDriver;

    @Override
    public boolean isDisplayed() {
        return isDisplayed(false);
    }

    @Override
    public boolean isDisplayed(boolean waitForAlert) {
        try {

            if (waitForAlert) {
                waitingDriver.waitForAlertIsPresent();
            } else {
                waitingDriver.waitForAlertIsPresent(1);
            }
            return true;
        } catch (SessionLostException ex) {
            throw ex;
        } catch (Exception ex) {
            reporter.ignoredException(ex);
            return false;
        }
    }

    @Override
    public void accept() {
        Alert alert = getAlert();
        reporter.info("Accept alert [{}]", getText());
        alert.accept();
    }

    @Override
    public void dismiss() {
        Alert alert = getAlert();
        reporter.info("Dismiss alert [{}]", getText());
        alert.dismiss();
    }

    @Override
    public IAlertHandler sendKeys(String keys) {
        Alert alert = getAlert();
        reporter.info("Send [{}] keys to alert [{}]", keys, getText());
        alert.sendKeys(keys);
        return this;
    }

    @Override
    public String getText() {
        return getAlert().getText();
    }

    private Alert getAlert() {
        WebDriver driver = webDriverFactory.getDriver();
        waitingDriver.waitForAlertIsPresent();
        return driver.switchTo().alert();
    }
}
