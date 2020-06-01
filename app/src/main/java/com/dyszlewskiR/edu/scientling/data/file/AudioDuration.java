package com.dyszlewskiR.edu.scientling.data.file;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class AudioDuration {
    private static final int MAX_DURATION = 10000;

    public static boolean checkDuration(Uri uri, Context context){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(context, uri);
        String durationString = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int millSeconds = Integer.parseInt(durationString);
        return millSeconds <= MAX_DURATION;
    }

    public static int getMaxDurationSeconds(){
        return MAX_DURATION/1000;
    }
}
