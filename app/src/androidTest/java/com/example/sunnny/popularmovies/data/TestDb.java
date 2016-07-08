package com.example.sunnny.popularmovies.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by Sunnny on 05/07/16.
 */
public class TestDb extends AndroidTestCase{

    void deleteDb()
    {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        deleteDb();
    }

    public void testCreateDatabase()
    {
        SQLiteDatabase db=new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true,db.isOpen());


        Cursor c=db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        c.moveToNext();
        assertEquals(MovieContract.TABLE_NAME,c.getString(0));

        c=db.rawQuery("PRAGMA table_info(" + MovieContract.TABLE_NAME + ")",
        null);


        assertEquals(6,c.getColumnCount());
        c.moveToNext();
        assertEquals("id",c.getString(1));
        c.moveToNext();
        assertEquals("title",c.getString(1));
        c.moveToNext();c.moveToNext();c.moveToNext();c.moveToNext();c.moveToNext();
    }
}
