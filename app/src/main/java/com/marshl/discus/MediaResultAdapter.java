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

public class MediaResultAdapter implements ListAdapter {
    private Activity context;

    private MediaSearcher searcher;

    public MediaResultAdapter(Activity context, MediaSearcher searcher) {
        this.context = context;
        this.searcher = searcher;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return this.searcher.getResultCount();
    }

    @Override
    public Object getItem(int position) {
        try {
            return this.searcher.getMedia(position);
        } catch (MediaSearchException ex) {
            Log.e("MediaResultAdapter", ex.toString());
            return null;
        }
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

        final Media media = (Media) this.getItem(position);
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
        return this.getCount() != 0;
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
