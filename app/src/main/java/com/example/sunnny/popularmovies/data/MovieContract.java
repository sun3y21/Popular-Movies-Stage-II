package com.example.sunnny.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sunnny on 05/07/16.
 */
public class MovieContract implements BaseColumns {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String TABLE_NAME="movies";

    public static final String COLUMN_MOVIES_ID="movies_id";

    public static final String COLUMN_TITLE="title";

    public static final String COLUMN_RELEASE_DATE="release_date";

    public static final String COLUMN_USER_RATING="rating";

    public static final String COLUMN_DESCRIPTION="description";

    public static final String COLUMN_POSTER="poster";

    public static final String COLUMN_REVIEW="review";

    public static final String COLUMN_FAVOURITE="favourite";

    public static final String COLUMN_TYPE="type";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

    public static Uri buildUriOnID(long id)
    {
         return ContentUris.withAppendedId(CONTENT_URI,id);
    }
}
