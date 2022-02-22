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

import com.github.mishaninss.arma.uidriver.annotations.ElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementDriver;
import com.github.mishaninss.arma.uidriver.interfaces.IElementGetActionDriver;
import com.github.mishaninss.arma.uidriver.interfaces.ILocatable;
import com.github.mishaninss.arma.uidriver.interfaces.IPoint;
import com.github.mishaninss.arma.utils.Dimension;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Sergey Mishanin
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WdElementGetActionsDriver implements IElementGetActionDriver {

  @ElementDriver
  private IElementDriver elementDriver;

  private final ILocatable element;

  public WdElementGetActionsDriver(ILocatable element) {
    this.element = element;
  }


  @Override
  public Dimension size() {
    return elementDriver.getSize(element);
  }

  @Override
  public IPoint location() {
    return elementDriver.getLocation(element);
  }

  @Override
  public byte[] screenshot() {
    return elementDriver.takeElementScreenshot(element);
  }

  @Override
  public ILocatable getElement() {
    return element;
  }
}
