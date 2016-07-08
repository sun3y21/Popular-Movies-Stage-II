package com.example.sunnny.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Sunnny on 05/07/16.
 */
public class MovieProvider extends ContentProvider {

    private MovieDbHelper movieDbHelper;

    @Override
    public boolean onCreate() {
        movieDbHelper=new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
      Cursor c= movieDbHelper.getReadableDatabase().query(MovieContract.TABLE_NAME,strings,s,strings1,null,null,s1);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return MovieContract.CONTENT_TYPE;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db=movieDbHelper.getWritableDatabase();
        long _id = db.insert(MovieContract.TABLE_NAME, null,contentValues);
        if ( _id > 0 )
        {
            getContext().getContentResolver().notifyChange(uri, null);
            return MovieContract.buildUriOnID(_id);
        }
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int n= db.delete(MovieContract.TABLE_NAME,s,strings);
        if(n!=0)
            getContext().getContentResolver().notifyChange(uri, null);
        return n;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int n= db.update(MovieContract.TABLE_NAME,contentValues,s,strings);
        if(n!=0)
            getContext().getContentResolver().notifyChange(uri, null);
        return n;
    }


   public int bulkInsert(Uri uri, ContentValues[] values) {
       String columns[]={MovieContract.COLUMN_FAVOURITE};
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        String movie_id=value.getAsString(MovieContract.COLUMN_MOVIES_ID);
                        String where=MovieContract.COLUMN_MOVIES_ID+" = \""+movie_id+"\"";
                        Cursor c=db.query(true,MovieContract.TABLE_NAME,columns,where,null,null,null,null,null);
                        if(!c.moveToNext())
                        {
                            long _id = db.insert(MovieContract.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            }
                        }
                        else if(!c.getString(0).equals("true"))
                        {
                            long _id = db.insert(MovieContract.TABLE_NAME, null, value);
                            if (_id != -1) {
                                returnCount++;
                            }
                        }
                    }
                     db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
    }


    public void shutdown() {
        movieDbHelper.close();
        super.shutdown();
    }

}
