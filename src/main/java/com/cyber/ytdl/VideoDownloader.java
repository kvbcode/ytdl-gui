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
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Kirill Bereznyakov
 */
public class VideoDownloader {

    public static final String[] QUALITY_LIST = {"2160", "1440", "1080", "720", "480", "360"};
    public static final String[] DOWNLOADER_LIST = {"youtube-dl", "yt-dlp", "yt-dlp_x86"};

    private static final String YTDL_FORMAT_STR = "bestvideo[height<=%1$s]+bestaudio/best[height<=%1$s]";
    private static final String YTDL_COMPAT_FORMAT_STR = "bestvideo[vcodec^=avc1][height<=%s][fps<=30]+bestaudio[acodec^=mp4a]";
    private static final String YTDL_OUTFILE_FORMAT_STR = "%(title)s-%(resolution)s.%(ext)s";
    private static final Pattern DOWNLOAD_PROGRESS_PATTERN = Pattern.compile("\\[download\\]\\s+([\\d\\.]+)\\% of ", Pattern.UNICODE_CHARACTER_CLASS);
    private static final String CHARSET = "cp1251";

    protected RunnableProcess proc;

    protected Consumer<RunnableProcess> processStartHandler = p -> {};
    protected Consumer<String> processConsoleOutputHandler = line -> {};
    protected Consumer<Float> downloadProgressValueHandler = value -> {};
    protected Consumer<String> downloadProgressStringHandler = str -> {};
    protected Runnable processExitHandler = () -> {};


    public VideoDownloader(){

    }

    public String buildVideoFormatSelectString(String height, boolean compatibility){
        return compatibility
            ? String.format(YTDL_COMPAT_FORMAT_STR, height)
            : String.format(YTDL_FORMAT_STR, height);
    }

    public void download(String url, String downloaderExe, String outputDir, String quality, boolean compatibleFormat){
        download(url, downloaderExe, outputDir, buildVideoFormatSelectString( quality, compatibleFormat ) );
    }

    public void download(String url, String downloaderExe, String outputDir, String selectFormatString){

        String outputFilesPattern = !outputDir.isEmpty()
            ? outputDir + File.separator + YTDL_OUTFILE_FORMAT_STR
            : YTDL_OUTFILE_FORMAT_STR;

        proc = new RunnableProcess(downloaderExe, "-f", selectFormatString, "-o", outputFilesPattern, url)
            .charset(Charset.forName(CHARSET))
            .onOutput(this::handleOutput)
            .onExit(processExitHandler);

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
        if (isAlive()) proc.destroy();
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

    public void onExit(Runnable processExitHandler){
        this.processExitHandler = processExitHandler;
    }

}
