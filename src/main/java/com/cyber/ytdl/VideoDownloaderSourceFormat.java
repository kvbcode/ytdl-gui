/*
 * The MIT License
 *
 * Copyright 2022 Kirill Bereznyakov.
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

import java.util.stream.Stream;

/**
 *
 * @author Kirill Bereznyakov
 */
public enum VideoDownloaderSourceFormat {
    BEST("best", Short.MAX_VALUE),
    VIDEO_4320("4320", 2160),
    VIDEO_2160("2160", 2160),
    VIDEO_1440("1440", 1440),
    VIDEO_1080("1080", 1080),
    VIDEO_720("720", 720),
    VIDEO_480("480", 480),
    VIDEO_360("360", 360),
    AUDIO_ONLY("audio", -1);

    public static String VIDEO_FORMAT_STR = "bestvideo[height<=%1$d]+bestaudio/best[height<=%1$d]";
    public static String VIDEO_COMPAT_FORMAT_STR = "best[vcodec^=avc1][height=%1$d][fps<=30][acodec^=mp4a]/bestvideo[vcodec^=avc1][height<=%1$d][fps<=30]+bestaudio[acodec^=mp4a]";

    public static String AUDIO_FORMAT_STR = "bestaudio/best";
    public static String AUDIO_COMPAT_FORMAT_STR = "bestaudio[acodec^=mp4a]/best[acodec^=mp4a]";

    private final String title;
    private final int height;

    VideoDownloaderSourceFormat(String title, int height){
        this.title = title;
        this.height = height;
    }

    @Override
    public String toString() {
        return title;
    }

    public boolean hasVideo(){
        return height > 0;
    }

    public static VideoDownloaderSourceFormat getByTitle(String title){
        for(VideoDownloaderSourceFormat e: values()){
            if (e.title.equals(title)) return e;
        }
        System.err.println("format '" + title + "' is not found.");
        return BEST;
    }

    public static VideoDownloaderSourceFormat getByHeight(int height){
        for(VideoDownloaderSourceFormat e: values()){
            if (e.height==height) return e;
        }
        System.err.println("format height " + height + " is not found");
        return BEST;
    }

    public static String[] getTitles(){
        return Stream.of(VideoDownloaderSourceFormat.values())
            .map(Object::toString)
            .toArray(String[]::new);
    }

    public String getFormatString(boolean compatibility){
        if (hasVideo()){
            return compatibility
                ? String.format(VIDEO_COMPAT_FORMAT_STR, height)
                : String.format(VIDEO_FORMAT_STR, height);
        }
        return compatibility
            ? AUDIO_COMPAT_FORMAT_STR
            : AUDIO_FORMAT_STR;
    }

}
