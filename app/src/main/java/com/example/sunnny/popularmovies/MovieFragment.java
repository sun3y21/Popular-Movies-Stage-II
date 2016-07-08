package com.example.sunnny.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.sunnny.popularmovies.data.Movie;
import com.example.sunnny.popularmovies.data.MovieContract;

import java.util.ArrayList;

/**
 * Created by Sunnny on 06/07/16.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    final static String MOVIE_FRAGMENT="MOVIE_FRAGMENT";

    int mPosition= ListView.INVALID_POSITION;
    final String SELECTED_POSITION="selected";

    GridView gridView=null;

    private static final String[] MOVIES_COLUMNS=
                                   {
                                           MovieContract._ID,
                                           MovieContract.COLUMN_MOVIES_ID,
                                           MovieContract.COLUMN_TITLE,
                                           MovieContract.COLUMN_RELEASE_DATE,
                                           MovieContract.COLUMN_DESCRIPTION,
                                           MovieContract.COLUMN_USER_RATING,
                                           MovieContract.COLUMN_POSTER,
                                           MovieContract.COLUMN_REVIEW,
                                           MovieContract.COLUMN_FAVOURITE,
                                           MovieContract.COLUMN_TYPE
                                 };

    static final int COL_ID=0;
    static final int COL_MOVIES_ID=1;
    static final int COL_TITLE=2;
    static final int COL_RELEASE_DATE=3;
    static final int COL_DESCRIPTION=4;
    static final int COL_USER_RATING=5;
    static final int COL_POSTER=6;
    static final int COL_REVIEW=7;
    static final int COL_FAVOURITE=8;
    static final int COL_TYPE=9;


    private static final int MOVIE_LOADER=0;
    ArrayList<String> urls=new ArrayList<>();

    Movie[] movies=null;
    FetchMovieData f=null;

    private MoviesAdapter movieAdapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.main_fragment,container,false);
        gridView=(GridView)view.findViewById(R.id.main_fragment);

        updateMoviesData();
        //create a new object of image adapter
        movieAdapter=new MoviesAdapter(getContext(),null,0);

        //set adapter to grid view
        gridView.setAdapter(movieAdapter);

        if(savedInstanceState!=null&&savedInstanceState.containsKey(SELECTED_POSITION))
        {
            mPosition=savedInstanceState.getInt(SELECTED_POSITION);
        }


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                  Cursor c=(Cursor)adapterView.getItemAtPosition(i);
                  mPosition=c.getPosition();
                  Bundle b=new Bundle();
                  b.putString(MovieContract.COLUMN_MOVIES_ID,c.getString(MovieFragment.COL_MOVIES_ID));
                  b.putString(MovieContract.COLUMN_TITLE,c.getString(MovieFragment.COL_TITLE));
                  b.putString(MovieContract.COLUMN_POSTER,c.getString(MovieFragment.COL_POSTER));
                  b.putString(MovieContract.COLUMN_FAVOURITE,c.getString(MovieFragment.COL_FAVOURITE));
                 ((Callback)getActivity()).onItemSelected(b);
            }
        });

        return view;
    }





    //helper method during 2pane layout
    public interface Callback
    {
        public void onItemSelected(Bundle b);
    }


    public void updateMoviesData()
    {
        //get the saved preference from settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortByType = prefs.getString(getString(R.string.pref_sort_by),getString(R.string.default_sort_type));
        //fatch data for movies as per preference
        if(!sortByType.equals("Favourite"))
        {
            f=new FetchMovieData(getContext(),movieAdapter,movies,urls);
            f.execute(sortByType);
        }
    }

    public void onSaveInstanceState(Bundle outState)
    {
        if(mPosition!=ListView.INVALID_POSITION)
        {
            outState.putInt(SELECTED_POSITION,mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    public void onMovieTypeChanged()
    {
        updateMoviesData();
        getLoaderManager().restartLoader(MOVIE_LOADER,null,this);
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER,null,this);
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortBy=MovieContract.COLUMN_USER_RATING+" DESC";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortByType = prefs.getString(getString(R.string.pref_sort_by),getString(R.string.default_sort_type));
        String selection=MovieContract.COLUMN_TYPE+" = \""+sortByType+"\"";
        if(sortByType.equalsIgnoreCase("Favourite"))
        {
            selection=MovieContract.COLUMN_FAVOURITE+" = \"true\"";
        }
        CursorLoader cs= new CursorLoader(getContext(),MovieContract.CONTENT_URI,MOVIES_COLUMNS,selection,null,sortBy);
        return cs;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
        if(mPosition!=ListView.INVALID_POSITION)
        {
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
       movieAdapter.swapCursor(null);
    }
}
