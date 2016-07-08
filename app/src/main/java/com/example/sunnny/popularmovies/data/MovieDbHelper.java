package com.example.sunnny.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sunnny on 05/07/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=2;
    public static String DATABASE_NAME="movies.db";

    public MovieDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TABLE="CREATE TABLE "+ MovieContract.TABLE_NAME+" ("+

                MovieContract._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+

                MovieContract.COLUMN_MOVIES_ID+ " TEXT NOT NULL , "+

                MovieContract.COLUMN_TITLE+" TEXT NOT NULL, "+

                MovieContract.COLUMN_RELEASE_DATE+" TEXT NOT NULL, "+

                MovieContract.COLUMN_DESCRIPTION+ " TEXT NOT NULL, "+

                MovieContract.COLUMN_USER_RATING+ " TEXT NOT NULL, "+

                MovieContract.COLUMN_POSTER+ " TEXT,  "+

                MovieContract.COLUMN_REVIEW+ " TEXT, "+
                MovieContract.COLUMN_FAVOURITE+" TEXT, "+

                MovieContract.COLUMN_TYPE+" TEXT NOT NULL, "+

                "UNIQUE ( "+MovieContract.COLUMN_MOVIES_ID+" ) ON CONFLICT REPLACE"+
                " ) ;" ;

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +MovieContract.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
