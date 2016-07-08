package com.example.sunnny.popularmovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sunnny.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sunnny on 27/06/16.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    String movieCodes[]={"www"};
    ImageView imageView;
    TextView title,date,rating,desc,users_reviews;


    String movie_id=null;
    Uri mUri=MovieContract.CONTENT_URI;

    private static final int DETAIL_LODER=0;
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

            };

    static final int COL_ID=0;
    static final int COL_MOVIES_ID=1;
    static final int COL_TITLE=2;
    static final int COL_RELEASE_DATE=3;
    static final int COL_DESCRIPTION=4;
    static final int COL_USER_RATING=5;
    static final int COL_POSTER=6;
    static final int COL_REVIEW=7;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.detail_fragment,container,false);
        manageTrailorView(view,movieCodes);

        Bundle b=getArguments();
        if(b!=null) {
            movie_id = b.getString(MovieContract.COLUMN_MOVIES_ID);
        }

            getLoaderManager().initLoader(DETAIL_LODER, savedInstanceState, this);
            imageView = (ImageView) view.findViewById(R.id.detail_poster_view);
            title = (TextView) view.findViewById(R.id.detail_movie_title);
            date = (TextView) view.findViewById(R.id.detail_movie_date);
            rating = (TextView) view.findViewById(R.id.detail_rating);
            desc = (TextView) view.findViewById(R.id.detail_description);
            users_reviews = (TextView) view.findViewById(R.id.detail_reviews);

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }



    @TargetApi(Build.VERSION_CODES.M)
    void  manageTrailorView(View view,String str[]) {

        if(view!=null)
        {
            LinearLayout l = (LinearLayout) view.findViewById(R.id.dot_layout);
            l.removeAllViewsInLayout();
            final int n =str.length;
            final TextView textView[] = new TextView[n];
            for (int i = 0; i < n; i++) {
                TextView t = new TextView(getContext());
                t.setText(".");
                t.setId(i + 100);
                t.setTextSize(65);
                t.setTextColor(getResources().getColor(R.color.forDotsUnSelected));
                t.setVisibility(View.VISIBLE);
                textView[i] = t;
                l.addView(t, i);
            }
            if(textView.length>=1)
            {
                textView[0].setTextColor(getResources().getColor(R.color.forDotSelected));
            }
            LinearLayout l1 = (LinearLayout) view.findViewById(R.id.image_layout);
            l1.removeAllViewsInLayout();
            final ImageView imageView[] = new ImageView[n];
            final FrameLayout frameLayout[] = new FrameLayout[n];
            for (int i = 0; i < n; i++) {
                FrameLayout frame = new FrameLayout(getContext());
                imageView[i] = new ImageView(getContext());
                imageView[i].setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView[i].setId(1001 + i);
                Picasso.with(getContext()).load(str[i]).placeholder(R.drawable.background)
                        .error(R.drawable.background).resize(550, 400).into(imageView[i]);
                frame.addView(imageView[i]);
                if (str[i].contains("youtube")) {
                    ImageView temp = new ImageView(getContext());
                    temp.setImageResource(R.drawable.playicon);
                    temp.setScaleType(ImageView.ScaleType.CENTER);
                    frame.addView(temp);
                }
                frame.setId(101 + i);
                frame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index=view.getId()-101;
                        if(movieCodes!=null)
                        {
                            String url="https://www.youtube.com/watch?v="+movieCodes[index];
                            Intent i=new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(getContext(),"Trailer unavailable",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                frameLayout[i] = frame;
                l1.addView(frame, i);
            }

            final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.scroll_view);
            horizontalScrollView.setSmoothScrollingEnabled(true);

            if (horizontalScrollView != null) {
                horizontalScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {

                    @Override
                    public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                        int k = 0;
                        for (k = 0; k < n; k++) {
                            if (i < frameLayout[k].getX())
                                break;
                        }

                        k--;
                        for (int j = 0; j < n; j++) {
                            if (j == k)
                                textView[j].setTextColor(getResources().getColor(R.color.forDotSelected));
                            else
                                textView[j].setTextColor(getResources().getColor(R.color.forDotsUnSelected));
                        }
                    }
                });
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(movie_id!=null)
        {
            String where=MovieContract.COLUMN_MOVIES_ID+" = \""+movie_id+"\"";
            return new CursorLoader(getContext(),mUri,MOVIES_COLUMNS,where,null,null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data!=null&&data.moveToFirst()) {

            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Picasso.with(getContext()).load(data.getString(COL_POSTER)).resize(200, 350)
                    .placeholder(R.drawable.loading).error(R.drawable.error).into(imageView);

            title.setText(data.getString(COL_TITLE));

            date.setText(data.getString(COL_RELEASE_DATE).substring(0, 4));

            rating.setText(data.getString(COL_USER_RATING) + "/10");

            desc.setText(data.getString(COL_DESCRIPTION));

            FetchTrailersUrl f = new FetchTrailersUrl();
            f.execute(data.getString(COL_MOVIES_ID));

            FetchUsersReview fetchUsersReview = new FetchUsersReview(getContext(), users_reviews);
            String arr[] = {data.getString(COL_MOVIES_ID)};
            fetchUsersReview.execute(arr);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    class FetchTrailersUrl extends AsyncTask<String,Void,String[]> {


        public String [] fetchTrailorsUrlFromJson(String jsonString)
        {
            try
            {
                JSONObject jsonObject=new JSONObject(jsonString);
                JSONArray results=jsonObject.getJSONArray("results");
                String arr[]=new String[results.length()];
                for(int i=0;i<results.length();i++)
                {
                   // arr[i]="https://www.youtube.com/watch?v="+results.getJSONObject(i).getString("key");
                     arr[i]=results.getJSONObject(i).getString("key");
                    // Log.v("Output : ",arr[i]);
                }
                return arr;

            }catch (Exception e)
            {
                Log.e("Error : ",e.getMessage());
            }
            return null;
        }

        @Override
        protected String[] doInBackground(String... params) {

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
                String MOVIE_DATA_BASE_URL ="http://api.themoviedb.org/3/movie/"+params[0]+"/videos";


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
                return fetchTrailorsUrlFromJson(moviesDataJsonStr);

            }catch (Exception e)
            {
                Log.e("Error",e.getMessage());
            }

            return null;

        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            if(strings!=null)
            {
                movieCodes=new String[strings.length];
                for(int i=0;i<strings.length;i++)
                {
                    movieCodes[i]=strings[i];
                }
                for(int i=0;i<strings.length;i++)
                {
                    strings[i]="http://img.youtube.com/vi/"+strings[i]+"/0.jpg";
                }
                manageTrailorView(getView(),strings);
            }
            else
            {
                Toast.makeText(getContext(),"No Internet Access.",Toast.LENGTH_LONG).show();
            }
        }
    }


}
