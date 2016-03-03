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

class Color {
    public static final Color Purple = new Color(0xB455B6);
    public static final Color Blue = new Color(0x00AEEF);
    public static final Color DarkBlue = new Color(0x2C3E50);
    public static final Color Green = new Color(0x93C624);
    public static final Color Gray = new Color(0x444444);
    public static final Color LightGray = new Color(0x666666);
    public static final Color Red = new Color(0xEF4100);

    public int hex;
    public double r, g, b;

    public Color(int hex) {
        this.hex = hex;
        r = ((hex & 0xFF0000) >> 16) / 255f;
        g = ((hex & 0xFF00) >> 8) / 255f;
        b = (hex & 0xFF) / 255f;
    }
}
