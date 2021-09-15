/*
 * The MIT License
 *
 * Copyright 2021 Kirill Bereznyakov.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cyber.ytdl.gui;

import com.cyber.util.ApplicationProperties;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Enumeration;
import java.util.Map;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 *
 * @author Kirill Bereznyakov
 */
public class Main {

    private static final int BASE_DPI = 96;

    public static void main(String[] args) throws Exception{

        // Properties
        ApplicationProperties properties = new ApplicationProperties("app.properties");

        // init System LaF
        UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

        // LaF tweaks
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        if (dpi!=BASE_DPI) setDefaultFontsScale((float)dpi/BASE_DPI);

        MainFrame main = new MainFrame(properties);
        main.showAtCenter();

    }

    public static void setDefaultFontsScale(float scale){
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration keys = defaults.keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object obj = UIManager.get(key);
            if (obj instanceof FontUIResource) {
                FontUIResource resource = (FontUIResource) obj;
                defaults.put(key, new FontUIResource(resource.deriveFont(resource.getSize2D()*scale)));
            } else if (obj instanceof Font) {
                Font resource = (Font) obj;
                defaults.put(key, resource.deriveFont(resource.getSize2D()*scale));
            }
        }
    }

}
