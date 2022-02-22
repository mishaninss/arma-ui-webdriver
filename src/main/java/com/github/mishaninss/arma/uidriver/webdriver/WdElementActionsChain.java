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

import com.github.mishaninss.arma.uidriver.interfaces.IActionsChain;
import com.github.mishaninss.arma.uidriver.interfaces.IElementActionsChain;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdElementActionsChain implements IElementActionsChain {

  private final ILocatable element;
  private boolean autoperform;

  @Autowired
  private IActionsChain actionsChain;

  public WdElementActionsChain(ILocatable element) {
    this.element = element;
  }

  public WdElementActionsChain(ILocatable element, boolean autoperform) {
    this.element = element;
    this.autoperform = autoperform;
  }

  private void autoperform() {
    if (autoperform) {
      perform();
    }
  }

  @Override
  public IElementActionsChain click() {
    actionsChain.click();
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain clickOnElement() {
    actionsChain.click(element);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain moveToElement() {
    actionsChain.moveToElement(element);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain moveToElement(int xOffset, int yOffset) {
    actionsChain.moveToElement(element, xOffset, yOffset);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain pause(long pause) {
    actionsChain.pause(pause);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain pause(Duration duration) {
    actionsChain.pause(duration);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain keyDown(CharSequence key) {
    actionsChain.keyDown(key);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain keyDownOnElement(CharSequence key) {
    actionsChain.keyDown(element, key);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain keyUp(CharSequence key) {
    actionsChain.keyUp(key);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain keyUpOnElement(CharSequence key) {
    actionsChain.keyUp(element, key);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain sendKeys(CharSequence... keys) {
    actionsChain.sendKeys(element, keys);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain clickElementAndHold() {
    actionsChain.clickAndHold(element);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain clickAndHold() {
    actionsChain.clickAndHold();
    autoperform();
    return this;
  }


  @Override
  public IElementActionsChain release() {
    actionsChain.release();
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain releaseOnElement() {
    actionsChain.release(element);
    autoperform();
    return this;
  }


  @Override
  public IElementActionsChain doubleClick() {
    actionsChain.doubleClick(element);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain moveByOffset(int xOffset, int yOffset) {
    actionsChain.moveByOffset(xOffset, yOffset);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain contextClick() {
    actionsChain.contextClick(element);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain dragAndDrop(ILocatable target) {
    actionsChain.dragAndDrop(element, target);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain dragAndDropBy(int xOffset, int yOffset) {
    actionsChain.dragAndDropBy(element, xOffset, yOffset);
    autoperform();
    return this;
  }

  @Override
  public IElementActionsChain build() {
    actionsChain.build();
    return this;
  }

  @Override
  public IElementActionsChain perform() {
    actionsChain.perform();
    return this;
  }
}
