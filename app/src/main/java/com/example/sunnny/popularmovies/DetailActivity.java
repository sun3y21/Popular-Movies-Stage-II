package com.example.sunnny.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.sunnny.popularmovies.data.MovieContract;

public class DetailActivity extends AppCompatActivity {

    ShareActionProvider myShareActionProvider;

    String movie_id=null;
    String poster_url=null;
    String fav=null;
    String movie_name=null;
    String SAVED_DATA="data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("Movie Detail");
        Intent intent=getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if(intent!=null)
        {
            movie_id=intent.getStringExtra(MovieContract.COLUMN_MOVIES_ID);
            poster_url=intent.getStringExtra(MovieContract.COLUMN_POSTER);
            fav=intent.getStringExtra(MovieContract.COLUMN_FAVOURITE);
            movie_name=intent.getStringExtra(MovieContract.COLUMN_TITLE);
        }
        if(savedInstanceState==null)
        {
            FragmentManager fragmentManager=getSupportFragmentManager();
            Bundle b=new Bundle();
            DetailFragment df=new DetailFragment();
            b.putString(MovieContract.COLUMN_MOVIES_ID,movie_id);
            df.setArguments(b);
            fragmentManager.beginTransaction().add(R.id.detail_container,df).commit();
        }
    }


    public void onResume()
    {
        super.onResume();
        Bundle b=getIntent().getBundleExtra(SAVED_DATA);
        if(b!=null)
        {
            fav=b.getString(MovieContract.COLUMN_FAVOURITE);
            movie_name=b.getString(MovieContract.COLUMN_TITLE);
            movie_id=b.getString(MovieContract.COLUMN_MOVIES_ID);
            poster_url=b.getString(MovieContract.COLUMN_POSTER);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if(outState!=null)
        {
            if(fav==null||fav.equals("true"))
            {
                outState.putString(MovieContract.COLUMN_FAVOURITE,"true");
            }
            else
            {
                outState.putString(MovieContract.COLUMN_FAVOURITE,"false");
            }

            if(movie_id!=null)
            {
                outState.putString(MovieContract.COLUMN_MOVIES_ID,movie_id);
            }
            if(movie_name!=null)
            {
                outState.putString(MovieContract.COLUMN_TITLE,movie_name);
            }
            if(poster_url!=null)
            {
                outState.putString(MovieContract.COLUMN_POSTER,poster_url);
            }
            getIntent().putExtra(SAVED_DATA,outState);
        }
        super.onSaveInstanceState(outState);

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.detail_activity_menu,menu);
        if(fav!=null)
        {
            if(fav.equals("true"))
            {
                menu.findItem(R.id.menu_item_favourite).setIcon(R.drawable.likedbutton);
            }
        }

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem menu)
    {
        switch (menu.getItemId())
        {
            case R.id.menu_item_favourite:
                if(fav!=null&&movie_id!=null) {
                    if (fav.equalsIgnoreCase("true")) {
                        fav = "false";
                        menu.setIcon(R.drawable.likebutton);
                    } else {
                        fav = "true";
                        menu.setIcon(R.drawable.likedbutton);
                    }
                    ContentValues c = new ContentValues();
                    c.put(MovieContract.COLUMN_FAVOURITE, fav);
                    String where = MovieContract.COLUMN_MOVIES_ID + "==" + "\"" + movie_id + "\"";
                    getContentResolver().update(MovieContract.CONTENT_URI, c, where, null);
                }
                break;
            case R.id.menu_item_share:
                String shareBody = poster_url;
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,movie_name);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
                break;

        }
        return true;
    }


}
