package com.aprendizagem.manu.boaviagemapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.aprendizagem.manu.boaviagemapp.R;
import com.squareup.picasso.Picasso;

public class GaleriaImagensAdapter extends BaseAdapter {
    private Context mContext;

    public GaleriaImagensAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;

        Picasso.with(mContext).load(mThumbIds)
                .into(viewHolder.imageView);
    }



    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.example_appwidget_preview, R.drawable.ic_add_24dp,
            R.drawable.ic_done_black, R.drawable.ic_google_icon

    };


    public static class ViewHolder {
        public final ImageView imageView;


        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.grid_item_image);


        }
}