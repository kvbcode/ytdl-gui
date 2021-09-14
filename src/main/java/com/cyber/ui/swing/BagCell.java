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

import java.awt.GridBagConstraints;

/**
 *
 * @author Kirill Bereznyakov
 */
public class BagCell extends GridBagConstraints{

    public BagCell() {
        super();
    }

    /**
     * Create next relative Cell
     * @return
     */
    public static BagCell next(){
        BagCell cell = new BagCell();
        cell.insets.right = 5;
        cell.insets.bottom = 5;
        return cell;
    }

    /**
     * Create Cell and set position
     * @param x
     * @param y
     * @return
     */
    public static BagCell pos(int x, int y){
        BagCell cell = next();
        cell.gridx = x;
        cell.gridy = y;
        return cell;
    }

    public static BagCell row(int y){
        return pos(0,y);
    }

    public BagCell width(int gridwidth){
        this.gridwidth = gridwidth;
        return this;
    }

    public BagCell height(int gridheight){
        this.gridheight = gridheight;
        return this;
    }

    /**
     * Set zero values to disable autosize
     * @param weightx
     * @param weighty
     * @return
     */
    public BagCell weight(double weightx, double weighty){
        this.weightx = weightx;
        this.weighty = weighty;
        return this;
    }

    public BagCell gap(int x, int y){
        this.insets.right = x;
        this.insets.left = y;
        return this;
    }

    public BagCell endRow(){
        this.gridwidth = GridBagConstraints.REMAINDER;
        this.insets.right = 0;
        return this;
    }

    public BagCell fillX(){
        this.fill = GridBagConstraints.HORIZONTAL;
        return this;
    }

    public BagCell fillX(double weightx){
        this.fill = GridBagConstraints.HORIZONTAL;
        this.weightx = weightx;
        return this;
    }

    public BagCell fillY(){
        this.fill = GridBagConstraints.VERTICAL;
        return this;
    }

    public BagCell fillY(double weighty){
        this.fill = GridBagConstraints.VERTICAL;
        this.weighty = weighty;
        return this;
    }

    public BagCell fillBoth(){
        this.fill = GridBagConstraints.BOTH;
        return this;
    }

    public BagCell alignLeft(){
        this.anchor = GridBagConstraints.LINE_START;
        return this;
    }

    public BagCell alignRight(){
        this.anchor = GridBagConstraints.LINE_END;
        return this;
    }

}
