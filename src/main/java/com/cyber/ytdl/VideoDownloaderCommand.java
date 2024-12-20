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
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Kirill Bereznyakov
 */
public class VideoDownloaderCommand{

    private String downloaderExe = "yt-dlp";
    private String url = "";
    private String outputPath = "";
    private String fileNamesPattern = "";
    private VideoDownloaderSourceFormat sourceFormat = VideoDownloaderSourceFormat.BEST;
    private boolean compatibleFormat = false;
    private boolean playlistAllowed = false;
    private boolean subtitlesAllowed = false;
    private boolean thumbnailAllowed = false;
    private boolean debug = false;
    private int socketTimeout = 10;
    private int connectRetries = 10;
    private int extractorRetries = 10;
    private int taskRetries = 10;
    private String proxyUrl = "";

    private LinkedHashMap<String,String> params;

    public VideoDownloaderCommand(){
        params = new LinkedHashMap<>();
    }

    public VideoDownloaderCommand(String url){
        this();
        this.url = url;
    }

    public VideoDownloaderCommand(String url, String outputDir){
        this(url);
        this.outputPath = outputDir;
    }

    public VideoDownloaderCommand(VideoDownloaderCommand source){
        this();
        downloaderExe = source.getDownloaderExe();
        url = source.getUrl();
        outputPath = source.getOutputPath();
        fileNamesPattern = source.getFileNamesPattern();
        sourceFormat = source.getSourceFormat();
        compatibleFormat = source.isCompatibleFormat();
        playlistAllowed = source.isPlaylistAllowed();
        thumbnailAllowed = source.isThumbnailAllowed();
        debug = source.isDebug();
        socketTimeout = source.getSocketTimeout();
        connectRetries = source.getConnectRetries();
        extractorRetries = source.getExtractorRetries();
        taskRetries = source.getTaskRetries();
        proxyUrl = source.getProxyUrl();

        params.putAll(source.getParams());
    }

    public List<String> toList(){
        List<String> cmd = new ArrayList<>();

        // executable at first place
        cmd.add(downloaderExe);

        // debug verbosity
        if (debug){
            cmd.add("-v");
        }

        // audio only params
        if (!sourceFormat.hasVideo()){
            cmd.add("-x");
        }

        // use proxy
        if (!proxyUrl.isBlank()){
            add("--proxy", getProxyUrl());
        }

        // get source format selection string
        add("-f", sourceFormat.getFormatString(compatibleFormat));
        
        // output files pattern (with path)
        add("-o", VideoDownloader.getOutputFilesPattern(outputPath, fileNamesPattern));

        if (socketTimeout>0) {
            add("--socket-timeout", String.valueOf(socketTimeout));
        }

        if (extractorRetries>0) {
            add("--extractor-retries", String.valueOf(extractorRetries));
        }

        if (connectRetries>0) {
            add("--retries", String.valueOf(connectRetries));
        }

        if (playlistAllowed){
            add("--download-archive",
                VideoDownloader.getOutputFilesPattern(outputPath, genDownloadArchiveFilename(url)));
        }else{
            add("--no-playlist");
        }

        // get all subtitles
        if (subtitlesAllowed){
            add("--all-subs");
        }

        if (thumbnailAllowed){
            add("--write-thumbnail --embed-thumbnail");
        }

        params.entrySet().stream()
            .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
            .filter(Predicate.not(String::isEmpty))
            .forEach(cmd::add);

        // url at last place
        cmd.add(url);

        return cmd;
    }

    public String printInfo(){
        StringBuilder sb = new StringBuilder();

        sb  .append("url: ").append(url).append("\n")
            .append("source format: ").append(sourceFormat).append("\n")
            .append("downloader: ").append(downloaderExe).append("\n")
            .append("output path: ").append(outputPath).append("\n");

        if (compatibleFormat) sb.append("compatible format: true\n");
        if (playlistAllowed) sb.append("playlist allowed: true\n");
        if (!proxyUrl.isBlank()) sb.append("proxy: ").append(proxyUrl).append("\n");

        if (debug){
            sb.append("debug mode: enabled\n");
            sb.append("file names: ").append(fileNamesPattern).append("\n");
            sb.append("cmdline: ").append(toList()).append("\n");
        }

        return sb.toString();
    }

    public void add(String name, Object value){
        params.put(name, String.valueOf(value));
    }

    public void add(String name){
        add(name, "");
    }

    /*  ===================
        Getters and Setters
        ===================
    */

    public Map<String,String> getParams(){
        return params;
    }

    public String getQuality(){
        return sourceFormat.toString();
    }

    /**
     * Select sourceFormat by quality string.
     * @param qualityStr sourceFormat title
     * @see VideoDownloaderSourceFormat#getByTitle(java.lang.String)
     */
    public void setQuality(String qualityStr){
        this.sourceFormat = VideoDownloaderSourceFormat.getByTitle(qualityStr);
    }

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

    public String getFileNamesPattern() {
        return fileNamesPattern;
    }

    public void setFileNamesPattern(String fileNamesPattern) {
        this.fileNamesPattern = fileNamesPattern;
    }

    public VideoDownloaderSourceFormat getSourceFormat() {
        return sourceFormat;
    }

    public void setSourceFormat(VideoDownloaderSourceFormat sourceFormat) {
        this.sourceFormat = sourceFormat;
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

    public boolean isThumbnailAllowed() {
        return thumbnailAllowed;
    }

    public void setThumbnailAllowed(boolean thumbnailAllowed) {
        this.thumbnailAllowed = thumbnailAllowed;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectRetries() {
        return connectRetries;
    }

    public void setConnectRetries(int retries) {
        this.connectRetries = retries;
    }

    public int getExtractorRetries() {
        return extractorRetries;
    }

    public void setExtractorRetries(int extractorRetries) {
        this.extractorRetries = extractorRetries;
    }

    public int getTaskRetries() {
        return taskRetries;
    }

    public void setTaskRetries(int taskRetries) {
        this.taskRetries = taskRetries;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isSubtitlesAllowed() {
        return subtitlesAllowed;
    }

    public void setSubtitlesAllowed(boolean subtitlesAllowed) {
        this.subtitlesAllowed = subtitlesAllowed;
    }

    protected String genDownloadArchiveFilename(String archiveId) {
        return "files-" + Integer.toHexString(Objects.hash(archiveId)) + ".lst";
    }

    public String getProxyUrl() {
        return proxyUrl;
    }

    public void setProxyUrl(String proxyUrl) {
        this.proxyUrl = proxyUrl;
    }

}
