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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 *
 * @author Kirill Bereznyakov
 */
public class RunnableProcess implements Runnable{

    final String[] command;
    Consumer<String> processOutputConsumer;
    Consumer<Process> onExitProcessHandler;
    Charset charset = Charset.defaultCharset();
    Process proc;

    public RunnableProcess(String...command){
        this.command = command;
    }

    public RunnableProcess(List<String> commandList){
        this(commandList.toArray(String[]::new));
    }

    @Override
    public void run() {
        try {
            proc = new ProcessBuilder(command).start();

            if (onExitProcessHandler!=null) proc.onExit().thenAccept(onExitProcessHandler);

            if (processOutputConsumer!=null){
                try(Scanner sc = new Scanner(proc.getInputStream(), charset);
                    Scanner scerr = new Scanner(proc.getErrorStream(), charset)){

                    // Warning! May block
                    while(proc.isAlive() || sc.hasNextLine() || scerr.hasNextLine()){
                        if (sc.hasNextLine()) processOutputConsumer.accept(sc.nextLine());
                        if (scerr.hasNextLine()) processOutputConsumer.accept(scerr.nextLine());
                    }
                }catch(NoSuchElementException ex){
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public RunnableProcess onOutput(Consumer<String> processOutputConsumer){
        this.processOutputConsumer = processOutputConsumer;
        return this;
    }

    public RunnableProcess onExit(Consumer<Process> onExitProcessHandler){
        this.onExitProcessHandler = onExitProcessHandler;
        return this;
    }

    public RunnableProcess charset(Charset charset){
        this.charset = charset;
        return this;
    }

    public void destroy(){
        if (proc.supportsNormalTermination()){
            proc.destroy();
        }else{
            proc.destroyForcibly();
        }
    }

    public boolean isAlive(){
        return proc!=null && proc.isAlive();
    }
}
