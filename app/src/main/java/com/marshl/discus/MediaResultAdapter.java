package com.marshl.discus;

import android.app.Activity;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

public class MediaResultAdapter implements ListAdapter {
    private Activity context;

    private List<Media> mediaList;

    public MediaResultAdapter(Activity context, List<Media> mediaList) {
        this.context = context;
        this.mediaList = mediaList;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getCount() {
        return this.mediaList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mediaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = this.context.getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.fragment_media_list_item, parent, false);
        }

        final Media media = this.mediaList.get(position);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.media_list_item_title);
        titleTextView.setText(media.getTitle() + " (" + media.getYear() + ")");

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.media_list_item_icon);

        int drawableId = R.drawable.ic_unknown;
        switch (media.getType()) {
            case "series":
                drawableId = R.drawable.ic_television;
                break;
            case "game":
                drawableId = R.drawable.ic_game;
                break;
            case "movie":
                drawableId = R.drawable.ic_film;
                break;
        }

        iconImageView.setImageResource(drawableId);

        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MediaResultsAdapter", "Inserting new record for " + media.getTitle());
                MediaReaderDbHelper dbHelper = new MediaReaderDbHelper(context);
                dbHelper.insertMediaRecord(media);
            }
        });*/

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.mediaList.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
