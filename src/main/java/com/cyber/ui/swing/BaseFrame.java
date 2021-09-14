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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Kirill Bereznyakov
 */
abstract public class BaseFrame extends JFrame{

    private static final int BORDER_WIDTH = 10;
    private static final int DEFAULT_HGAP = 5;
    private static final int DEFAULT_VGAP = 5;

    protected JPanel root;

    public BaseFrame(String title, boolean configure){
        super(title);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        root = new JPanel(new BorderLayout(DEFAULT_HGAP, DEFAULT_VGAP));
        root.setBorder(new EmptyBorder(BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH));
        add(root);

        addKeyListener(new KeyListener(){
            @Override public void keyTyped(KeyEvent e) {}
            @Override public void keyReleased(KeyEvent e) {}
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    dispose();
                }
            }
        });

        if (configure) configure();
    };

    public BaseFrame(String title){
        this(title, false);
    }

    protected void configure(){
        initComponents(root);
    }

    public void showAtCenter(){
        setLocationRelativeTo(null);
        setVisible(true);
    }

    abstract public void initComponents(JPanel root);

}
