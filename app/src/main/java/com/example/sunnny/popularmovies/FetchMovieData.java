package com.example.sunnny.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.sunnny.popularmovies.data.Movie;
import com.example.sunnny.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Sunnny on 29/06/16.
 */

class FetchMovieData extends AsyncTask<String,Void,Void> {


    public FetchMovieData(Context mContext,MoviesAdapter imageAdapter,Movie[] movies,ArrayList<String> url)
    {
        this.imageAdapter=imageAdapter;
        this.movies=movies;
        this.urls=url;
        this.mContext=mContext;
    }

    private Context mContext;
    ArrayList<String> urls;
    Movie [] movies;
    MoviesAdapter imageAdapter;

    //tags to recover data from the json Objects
    final String ARRAY_NAME="results";
    final String POSTER_PATH="poster_path";
    final String PLOT_SYNOPSIS="overview";
    final String TITLE="original_title";
    final String RATING="vote_average";
    final String RELEASE_DATE="release_date";
    final String MOVIE_ID="id";


    public Movie[] fetchMoviesDataFromJson(String jsonString,String type)
    {
        movies=null;
        try
        {


            String BASE_URL_FOR_POSTER="http://image.tmdb.org/t/p/";
            String POSTER_SIZE="w185";
            //append that data to url
            BASE_URL_FOR_POSTER+=POSTER_SIZE+"/";

            JSONObject jsonObject=new JSONObject(jsonString);
            JSONArray jsonArray=jsonObject.getJSONArray(ARRAY_NAME);

            movies=new Movie[jsonArray.length()];

            for(int i=0;i<jsonArray.length();i++)
            {
                //create a new movie Object
                Movie temp=new Movie();
                //get Corresponding json object from json array
                JSONObject obj=(JSONObject)jsonArray.get(i);

                //set corresponding fields
                temp.setOrigional_title(obj.getString(TITLE));
                temp.setPlot_synopsis(obj.getString(PLOT_SYNOPSIS));
                temp.setPoster_url(BASE_URL_FOR_POSTER+obj.getString(POSTER_PATH));
                temp.setRelease_date(obj.getString(RELEASE_DATE));
                temp.setId(obj.getString(MOVIE_ID));
                temp.setUser_rating(obj.getString(RATING));
                temp.setType(type);
                //  Log.v("URL : "+i,temp.getPoster_url());
                movies[i]=temp;
            }

        }
        catch (Exception e)
        {
            Log.e("Error: ","JSON parsing error "+e.getMessage());
        }
        return movies;
    }


    @Override
    protected Void doInBackground(String... params) {

        if(params==null)
        {
            return null;
        }


        //for url and buffer
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;




        String moviesDataJsonStr = null;
        String format = "json";


        try {

            //expected url http://api.themoviedb.org/3/movie/popular?api_key=
            String MOVIE_DATA_BASE_URL ="http://api.themoviedb.org/3/movie/popular?";


            if(params[0].equals("Top_Rated"))
            {
                MOVIE_DATA_BASE_URL ="http://api.themoviedb.org/3/movie/top_rated?";
            }

            final String KEY="api_key";


            Uri builtUri = Uri.parse(MOVIE_DATA_BASE_URL).buildUpon()
                    .appendQueryParameter(KEY,BuildConfig.API_KEY)
                    .build();

            URL url=new URL(builtUri.toString());

            //check url
             //Log.v("URl---",url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();


            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                moviesDataJsonStr = null;

            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                moviesDataJsonStr = null;
            }
            moviesDataJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            moviesDataJsonStr = null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        try
        {
            //got data its its time to parse
            movies= fetchMoviesDataFromJson(moviesDataJsonStr,params[0]);
            writeToDataBase(movies);

        }catch (Exception e)
        {
            Log.e("Error",e.getMessage());
        }

        return null;
    }



    Movie [] getMovies()
    {
        return movies;
    }


    public void writeToDataBase(Movie[] movies)
    {
        Vector<ContentValues> moviesDataVector=new Vector<>();
        for(int i=0;i<movies.length;i++)
        {
            ContentValues contentValues=new ContentValues();
            contentValues.put(MovieContract.COLUMN_DESCRIPTION,movies[i].getPlot_synopsis());
            contentValues.put(MovieContract.COLUMN_FAVOURITE,movies[i].isFavourite());
            contentValues.put(MovieContract.COLUMN_MOVIES_ID,movies[i].getId());
            contentValues.put(MovieContract.COLUMN_POSTER,movies[i].getPoster_url());
            contentValues.put(MovieContract.COLUMN_TITLE,movies[i].getOrigional_title());
            contentValues.put(MovieContract.COLUMN_RELEASE_DATE,movies[i].getRelease_date());
            contentValues.put(MovieContract.COLUMN_USER_RATING,movies[i].getUser_rating());
            contentValues.put(MovieContract.COLUMN_TYPE,movies[i].getType());
            moviesDataVector.add(contentValues);
        }

        int inserted=0;
        if(moviesDataVector.size()>0)
        {
            ContentValues contentValues[]=new ContentValues[moviesDataVector.size()];
            moviesDataVector.toArray(contentValues);
            inserted=mContext.getContentResolver().bulkInsert(MovieContract.CONTENT_URI,contentValues);
        }
    }
}
