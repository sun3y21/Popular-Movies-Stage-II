package com.example.sunnny.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sunnny on 07/07/16.
 */
public class FetchUsersReview extends AsyncTask<String,Void,String[]>{

    TextView t=null;
    Context mContext=null;
    String movieID=null;
    public FetchUsersReview(Context c,TextView t)
    {
        this.mContext=c;
        this.t=t;
    }

    @Override
    protected String[] doInBackground(String... params) {

        if(params==null)
        {
            return null;
        }

        movieID=params[0];

        //for url and buffer
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;




        String moviesDataJsonStr = null;
        String format = "json";


        try {

            //expected url http://api.themoviedb.org/3/movie/popular?api_key=
            String MOVIE_DATA_BASE_URL ="http://api.themoviedb.org/3/movie/"+params[0]+"/reviews";


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
            return extractReviewsFromJson(moviesDataJsonStr);

        }catch (Exception e)
        {
            Log.e("Error",e.getMessage());
        }

        return null;

    }


    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        if(strings!=null) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < strings.length; i++) {
                str.append(strings[i] + "\n\n");
            }
            if (strings.length != 0) {
                t.setText(str.toString());
                //if want to put it in data base put it from here
                //but reviews are two big to keep in database so leave
            }
            else
            {
                t.setText("Reviews not available");
            }
        }
    }

    public String[] extractReviewsFromJson(String str)
    {
        String arr[]=null;
        try
        {
            JSONObject jsonObject=new JSONObject(str);
            JSONArray jsonArray=jsonObject.getJSONArray("results");
            arr=new String[jsonArray.length()];
            for(int i=0;i<jsonArray.length();i++)
            {
                arr[i]=jsonArray.getJSONObject(i).getString("author")+" : "+jsonArray.getJSONObject(i).getString("content");
            }
        }
        catch (Exception e)
        {
            Log.e("Error : ","Json parsing error");
        }
        return arr;
    }
}
