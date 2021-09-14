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

package com.cyber.ui.swing;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author Kirill Bereznyakov
 */
public class BorderPanel extends JPanel{

    final BorderLayout layout;
    
    public BorderPanel(){
        this.layout = new BorderLayout(5,5);
        setLayout(layout);
    }

    public static BorderPanel wrap(JComponent center){
        return new BorderPanel().center(center);
    }

    public BorderPanel center(JComponent center){
        add(center, BorderLayout.CENTER);
        return this;
    }

    public BorderPanel left(JComponent west){
        add(west, BorderLayout.WEST);
        return this;
    }

    public BorderPanel right(JComponent east){
        add(east, BorderLayout.EAST);
        return this;
    }

    public BorderPanel up(JComponent north){
        add(north, BorderLayout.NORTH);
        return this;
    }

    public BorderPanel down(JComponent south){
        add(south, BorderLayout.SOUTH);
        return this;
    }

    public BorderPanel padding(int hgap, int vgap){
        layout.setHgap(hgap);
        layout.setVgap(vgap);
        return this;
    }

    public BorderPanel zeroPadding(){
        layout.setHgap(0);
        layout.setVgap(0);
        return this;
    }

}
