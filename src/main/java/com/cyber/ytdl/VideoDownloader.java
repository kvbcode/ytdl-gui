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

package com.cyber.ytdl;

import com.cyber.util.RunnableProcess;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Kirill Bereznyakov
 */
public class VideoDownloader {

    public static String[] DOWNLOADER_LIST = {"yt-dlp", "yt-dlp_x86"};

    public static String YTDL_OUTFILE_FORMAT_STR = "%(title)s - [%(channel)s]-%(resolution)s.%(ext)s";
    private static Pattern DOWNLOAD_PROGRESS_PATTERN = Pattern.compile("\\[download\\]\\s+([\\d\\.]+)\\% of ", Pattern.UNICODE_CHARACTER_CLASS);

    private RunnableProcess proc;
    private boolean interrupted = false;

    private Consumer<RunnableProcess> processStartHandler = p -> {};
    private Consumer<String> processConsoleOutputHandler = line -> {};
    private Consumer<Float> downloadProgressValueHandler = value -> {};
    private Consumer<String> downloadProgressStringHandler = str -> {};
    private Runnable onCompleteHandler = () ->{};
    private Runnable onErrorHandler = () ->{};
    private Runnable onTerminationHandler = () ->{};
    private AtomicInteger attempt = new AtomicInteger(0);

    public VideoDownloader(){

    }

    /**
     * Build output files pattern string. If fileName pattern is empty uses default YTDL_OUTFILE_FORMAT_STR.
     * @param outputDir is optional, may be null or empty
     * @param fileNamePattern is optional, may be null or empty
     * @return
     */
    public static String getOutputFilesPattern(String outputDir, String fileNamePattern){
        if (fileNamePattern==null || fileNamePattern.isEmpty()){
            fileNamePattern = YTDL_OUTFILE_FORMAT_STR;
        }

        return (outputDir==null || outputDir.isEmpty())
            ? fileNamePattern
            : outputDir + File.separator + fileNamePattern;
    }

    /**
     * Execute downloader with parameters. Use {@link VideoDownloaderCommand}
     * @param vdc
     */
    public void execute(VideoDownloaderCommand vdc){
        attempt.set(1);
        interrupted = false;
        List<String> commandList = vdc.toList();

        proc = new RunnableProcess(commandList)
            .charset(Charset.defaultCharset())
            .onOutput(this::handleOutput)
            .onExit(p -> {
                if (!interrupted){
                    if (p.exitValue()==0){
                        onCompleteHandler.run();
                        onTerminationHandler.run();
                    }else{
                        if (attempt.incrementAndGet() > vdc.getTaskRetries()) {
                            onErrorHandler.run();
                            onTerminationHandler.run();
                        } else {
                            proc.destroy();
                            execute(vdc);
                        }
                    }
                }
            });

        new Thread(proc).start();
        processStartHandler.accept(proc);
    }

    protected void handleOutput(String line){
        Matcher mat = DOWNLOAD_PROGRESS_PATTERN.matcher(line);
        if (mat.find()){
            downloadProgressValueHandler.accept(Float.valueOf(mat.group(1)));
            downloadProgressStringHandler.accept(line);
            return;
        }
        processConsoleOutputHandler.accept(line);
    }

    public boolean isAlive(){
        return proc!=null && proc.isAlive();
    }

    public void destroy(){
        if (isAlive()){
            interrupted = true;
            proc.destroy();
        }
    }

    public void onStart(Consumer<RunnableProcess> processStartHandler){
        this.processStartHandler = processStartHandler;
    }

    public void onMessage(Consumer<String> processConsoleOutputHandler){
        this.processConsoleOutputHandler = processConsoleOutputHandler;
    }

    public void onDownloadProgressValue(Consumer<Float> downloadProgressValueHandler){
        this.downloadProgressValueHandler = downloadProgressValueHandler;
    }

    public void onDownloadProgressString(Consumer<String> downloadProgressStringHandler){
        this.downloadProgressStringHandler = downloadProgressStringHandler;
    }

    public void onComplete(Runnable onCompleteHandler){
        this.onCompleteHandler = onCompleteHandler;
    }

    public void onError(Runnable onErrorHandler){
        this.onErrorHandler = onErrorHandler;
    }

    public void onTermination(Runnable onTerminationHandler){
        this.onTerminationHandler = onTerminationHandler;
    }

}
