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

import com.github.mishaninss.uidriver.interfaces.IPoint;
import org.openqa.selenium.Point;

public class WdPoint implements IPoint {

    private Point seleniumPoint;

    public WdPoint(Point seleniumPoint) {
        this.seleniumPoint = seleniumPoint;
    }

    public WdPoint(int x, int y){
        this.seleniumPoint = new Point(x, y);
    }

    @Override
    public int getX() {
        return seleniumPoint.getX();
    }

    @Override
    public int getY() {
        return seleniumPoint.getY();
    }

    @Override
    public IPoint moveBy(int xOffset, int yOffset) {
        return new WdPoint(seleniumPoint.moveBy(xOffset, yOffset));
    }

    @Override
    public void move(int newX, int newY) {
        seleniumPoint.move(newX, newY);
    }

    public Point toSeleniumPoint(){
        return seleniumPoint;
    }
}
