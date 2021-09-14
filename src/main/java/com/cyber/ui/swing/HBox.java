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

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.util.stream.Stream;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author Kirill Bereznyakov
 */
public class HBox extends JPanel{

    private static final int HGAP = 5;
    private static final int VGAP = 0;

    public HBox(LayoutManager layout) {
        super(layout);
    }

    public HBox(int align, JComponent...component){
        this(new FlowLayout(align, HGAP, VGAP));
        Stream.of(component).forEach(this::add);
    }

    /**
     * Creates a JPanel with leading alignment
     * @param component
     * @return 
     */
    public static HBox of(JComponent...component){
        return new HBox(FlowLayout.LEADING, component);
    }

    /**
     * Creates a JPanel with left alignment
     * @param component
     * @return
     */
    public static HBox alignLeft(JComponent...component){
        return new HBox(FlowLayout.LEFT, component);
    }

    /**
     * Creates a JPanel with right alignment
     * @param component
     * @return
     */
    public static HBox alignRight(JComponent...component){
        return new HBox(FlowLayout.RIGHT, component);
    }

    /**
     * Creates a panel with center alignment
     * @param component
     * @return
     */
    public static HBox alignCenter(JComponent...component){
        return new HBox(FlowLayout.CENTER, component);
    }    

}
