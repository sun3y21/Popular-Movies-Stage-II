package com.example.sunnny.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.sunnny.popularmovies.data.MovieContract;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    String movieType=null;
    boolean twoPane=false;

    String fav,movie_id,poster_url,movie_name;
    final String DETAIL_FRAGMENT_TAG="DFAG";
    final String SAVED_DATA="data";

    MenuItem like=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        movieType=pref.getString(getString(R.string.pref_sort_by),getString(R.string.default_sort_type));

        if(findViewById(R.id.detail_container)!=null)
        {
            twoPane=true;
        }


    }

    public void onResume()
    {
        super.onResume();

        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String type=pref.getString(getString(R.string.pref_sort_by),getString(R.string.default_sort_type));
       // Log.v("Output : ",type+"   "+movieType);
        if(movieType!=null)
        {
            if(!movieType.equalsIgnoreCase(type))
            {
                movieType=type;
                MovieFragment ff=(MovieFragment)getSupportFragmentManager().findFragmentById(R.id.grid_container);
                if(ff!=null)
                {
                    ff.onMovieTypeChanged();
                }
                DetailFragment df=(DetailFragment)getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
                if(df!=null)
                {
                    getSupportFragmentManager().beginTransaction().remove(df).commit();

                }
            }
        }
        Bundle b=getIntent().getBundleExtra(SAVED_DATA);
        if(b!=null)
        {
            fav=b.getString(MovieContract.COLUMN_FAVOURITE);
            movie_name=b.getString(MovieContract.COLUMN_TITLE);
            movie_id=b.getString(MovieContract.COLUMN_MOVIES_ID);
            poster_url=b.getString(MovieContract.COLUMN_POSTER);
        }



    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        if(fav!=null)
        {
            if(fav.equals("true"))
            {
                like=menu.findItem(R.id.menu_item_favourite);
                if(like!=null)
                {
                    like.setIcon(R.drawable.likedbutton);
                }
            }
        }
        return true;
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

    public boolean onOptionsItemSelected(MenuItem menu)
    {
        switch (menu.getItemId())
        {
            case R.id.settings:
                Intent intent=new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_item_setting:
                intent=new Intent(getApplicationContext(),SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_item_favourite:
                if(fav!=null&&movie_id!=null) {

                    if (fav.equalsIgnoreCase("true")) {
                        fav = "false";
                        menu.setIcon(R.drawable.likebutton);
                    }
                    else
                    {
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
                if(poster_url!=null&&movie_name!=null) {
                    String shareBody = poster_url;
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, movie_name);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }
                break;
        }
        return true;
    }

    @Override
    public void onItemSelected(Bundle b)
    {
        if(twoPane)
        {
            DetailFragment df=new DetailFragment();
            df.setArguments(b);
            fav=b.getString(MovieContract.COLUMN_FAVOURITE);
            movie_id=b.getString(MovieContract.COLUMN_MOVIES_ID);
            poster_url=b.getString(MovieContract.COLUMN_POSTER);
            movie_name=b.getString(MovieContract.COLUMN_TITLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container,df,DETAIL_FRAGMENT_TAG).commit();
        }
        else
        {
            Intent i=new Intent(getApplicationContext(),DetailActivity.class);
            i.putExtra(MovieContract.COLUMN_MOVIES_ID,b.getString(MovieContract.COLUMN_MOVIES_ID));
            i.putExtra(MovieContract.COLUMN_TITLE,b.getString(MovieContract.COLUMN_TITLE));
            i.putExtra(MovieContract.COLUMN_POSTER,b.getString(MovieContract.COLUMN_POSTER));
            i.putExtra(MovieContract.COLUMN_FAVOURITE,b.getString(MovieContract.COLUMN_FAVOURITE));
            startActivity(i);
        }
    }
}
