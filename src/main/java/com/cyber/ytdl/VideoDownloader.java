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
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Kirill Bereznyakov
 */
public class VideoDownloader {

    public static String[] QUALITY_LIST = {"2160", "1440", "1080", "720", "480", "360"};
    public static String[] DOWNLOADER_LIST = {"youtube-dl", "yt-dlp", "yt-dlp_x86"};

    public static String YTDL_FORMAT_STR = "bestvideo[height<=%1$s]+bestaudio/best[height<=%1$s]";
    public static String YTDL_COMPAT_FORMAT_STR = "best[vcodec^=avc1][height=%1$s][fps<=30][acodec^=mp4a]/bestvideo[vcodec^=avc1][height<=%s][fps<=30]+bestaudio[acodec^=mp4a]";
    public static String YTDL_OUTFILE_FORMAT_STR = "%(title)s - [%(channel)s]-%(resolution)s.%(ext)s";
    protected static Pattern DOWNLOAD_PROGRESS_PATTERN = Pattern.compile("\\[download\\]\\s+([\\d\\.]+)\\% of ", Pattern.UNICODE_CHARACTER_CLASS);

    protected RunnableProcess proc;
    protected boolean interrupted = false;

    protected Consumer<RunnableProcess> processStartHandler = p -> {};
    protected Consumer<String> processConsoleOutputHandler = line -> {};
    protected Consumer<Float> downloadProgressValueHandler = value -> {};
    protected Consumer<String> downloadProgressStringHandler = str -> {};
    protected Runnable onCompleteHandler = () ->{};
    protected Runnable onErrorHandler = () ->{};
    protected Runnable onTerminationHandler = () ->{};


    public VideoDownloader(){

    }

    public static String buildVideoFormatSelectString(String height, boolean compatibility){
        return compatibility
            ? String.format(YTDL_COMPAT_FORMAT_STR, height)
            : String.format(YTDL_FORMAT_STR, height);
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
     * Simple downloading method. For more options use {@link VideoDownloaderCommand}
     * @param url
     * @param downloaderExe downloader program executable
     * @param outputDir output path with no slash ending
     * @param quality video height
     * @param compatibleFormat force most compatible file format. Usually h264(avc1)+aac(mp4a) in MP4 container
     */
    public void download(String url, String downloaderExe, String outputDir, String quality, boolean compatibleFormat){
        List<String> cmd = new ArrayList<>();
        cmd.add(downloaderExe);
        cmd.add("-f");
        cmd.add( buildVideoFormatSelectString( quality, compatibleFormat ) );
        cmd.add("-o");
        cmd.add( getOutputFilesPattern(outputDir, "") );
        cmd.add(url);
        execute( cmd );
    }

    /**
     * Execute downloader with parameters. Use {@link VideoDownloaderCommand}
     * @param commandList command line parts
     */
    public void execute(List<String> commandList){
        interrupted = false;

        proc = new RunnableProcess(commandList)
            .charset(Charset.defaultCharset())
            .onOutput(this::handleOutput)
            .onExit(p -> {
                if (!interrupted){
                    if (p.exitValue()==0){
                        onCompleteHandler.run();
                    }else{
                        onErrorHandler.run();
                    }
                }
                onTerminationHandler.run();
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
