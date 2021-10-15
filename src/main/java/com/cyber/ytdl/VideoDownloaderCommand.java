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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author Kirill Bereznyakov
 */
public class VideoDownloaderCommand extends LinkedHashMap<String,String>{

    private String downloaderExe = "youtube-dl";
    private String url = "";
    private String outputPath = "";
    private String quality = "1080";
    private boolean compatibleFormat = false;
    private boolean playlistAllowed = true;
    private boolean debug = false;
    private int socketTimeout = 20;

    public VideoDownloaderCommand(){
    }

    public VideoDownloaderCommand(String url){
        this.url = url;
    }

    public VideoDownloaderCommand(String url, String outputDir){
        this.url = url;
        this.outputPath = outputDir;
    }

    public List<String> toList(){
        List<String> cmd = new ArrayList<>();

        // executable at first place
        cmd.add(downloaderExe);

        // debug verbosity
        if (debug) cmd.add("-v");

        // format selection string
        putIfAbsent("-f", VideoDownloader.buildVideoFormatSelectString(quality, compatibleFormat));
        
        // output files pattern (with path)
        putIfAbsent("-o", VideoDownloader.getOutputFilesPattern(outputPath));

        if (socketTimeout>0)
            putIfAbsent("--socket-timeout", String.valueOf(socketTimeout));

        if (!playlistAllowed)
            putIfAbsent("--no-playlist", "");

        entrySet().stream()
            .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
            .forEach(cmd::add);

        // url at last place
        cmd.add(url);

        return cmd;
    }

    public String printInfo(){
        StringBuilder sb = new StringBuilder();

        sb  .append("url: ").append(url).append("\n")
            .append("quality: ").append(quality).append("\n")
            .append("downloader: ").append(downloaderExe).append("\n")
            .append("output path: ").append(outputPath).append("\n");

        if (compatibleFormat) sb.append("compatible format: true\n");
        if (playlistAllowed) sb.append("playlist allowed: true\n");

        if (debug){
            sb.append("debug mode enabled\n");
            sb.append("cmdline: ").append(toList()).append("\n");
        };

        return sb.toString();
    }

    /*  ===================
        Getters and Setters
        ===================
    */

    public String getDownloaderExe() {
        return downloaderExe;
    }

    public void setDownloaderExe(String downloaderExe) {
        this.downloaderExe = downloaderExe;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public boolean isCompatibleFormat() {
        return compatibleFormat;
    }

    public void setCompatibleFormat(boolean compatibleFormat) {
        this.compatibleFormat = compatibleFormat;
    }

    public boolean isPlaylistAllowed() {
        return playlistAllowed;
    }

    public void setPlaylistAllowed(boolean playlistAllowed) {
        this.playlistAllowed = playlistAllowed;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }



}
