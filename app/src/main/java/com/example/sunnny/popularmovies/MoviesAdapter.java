package com.example.sunnny.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Sunnny on 05/07/16.
 */
public class MoviesAdapter extends CursorAdapter{


    Context mContext;
    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        View view=null;
        view= LayoutInflater.from(context).inflate(R.layout.image_grid_item,viewGroup,false);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView imageView=(ImageView)view.findViewById(R.id.grid_image);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Picasso.with(mContext)
                .load(cursor.getString(MovieFragment.COL_POSTER))
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(imageView);

        TextView textView=(TextView)view.findViewById(R.id.movie_name_text);
        textView.setText(cursor.getString(MovieFragment.COL_TITLE));
    }
}
