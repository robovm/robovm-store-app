/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 */
package org.robovm.store.util;

import org.robovm.apple.uikit.UIColor;

public class Colors {
    public static final UIColor Clear = UIColor.clear();
    public static final UIColor White = UIColor.white();
    public static final UIColor Black = UIColor.black();
    public static final UIColor Purple = fromColor(Color.Purple);
    public static final UIColor Blue = fromColor(Color.Blue);
    public static final UIColor DarkBlue = fromColor(Color.DarkBlue);
    public static final UIColor Green = fromColor(Color.Green);
    public static final UIColor Gray = fromColor(Color.Gray);
    public static final UIColor LightGray = fromColor(Color.LightGray);
    public static final UIColor DarkGray = UIColor.darkGray();

    private static UIColor fromColor(Color color) {
        return UIColor.fromRGBA(color.r, color.g, color.b, 1);
    }
}
