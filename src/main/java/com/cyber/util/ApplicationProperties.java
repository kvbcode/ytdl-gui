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

package com.cyber.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Kirill Bereznyakov
 */
public class ApplicationProperties extends Properties{

    protected final String filePath;

    public ApplicationProperties(String filePath) {
        super();
        this.filePath = filePath;
        load();
    }

    public void load(){
        try(FileInputStream fis = new FileInputStream(filePath)){
            load(fis);
        }catch(IOException ex){
            System.err.println("ApplicationProperties.load() error: " + ex);
        }
    }

    public void save(){
        try(FileOutputStream fos = new FileOutputStream(filePath)){
            store(fos, "");
        }catch(IOException ex){
            System.err.println("ApplicationProperties.save() error: " + ex);
        }
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        return super.put(key, String.valueOf(value));
    }

    public Integer getInt(Object key, Integer defaultValue){
        return Integer.valueOf(getOrDefault(key, defaultValue).toString());
    }

    public Boolean getBool(Object key, Boolean defaultValue){
        return Boolean.valueOf(getOrDefault(key, defaultValue).toString());
    }

}
