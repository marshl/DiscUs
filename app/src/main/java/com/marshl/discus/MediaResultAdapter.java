package com.marshl.discus;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MediaResultAdapter extends ArrayAdapter<Media> {
    private Context context;

    private MediaSearcher mediaSearcher;
    private List<Media> mediaList;

    /*public MediaResultAdapter() {

    }

    public MediaResultAdapter(Activity context, List<Media> mediaList, MediaSearcher mediaSearcher) {
        this.context = context;
        this.mediaSearcher = mediaSearcher;
        this.mediaList = mediaList;
    }*/

    public void setMediaSearcher(MediaSearcher mediaSearcher) {
        this.mediaSearcher = mediaSearcher;
    }

    public MediaResultAdapter(Context context, ArrayList<Media> items) {
        super(context, 0, items);

        this.context = context;
        this.mediaList = items;
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
        if (this.mediaSearcher.hasMoreResults()) {
            return this.mediaList.size() + 1;
        } else {
            return this.mediaList.size();
        }
    }

    @Override
    public Media getItem(int position) {
        if (position == this.mediaList.size()) {
            return null;
        } else {
            return this.mediaList.get(position);
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

        if (position == this.mediaList.size() && this.mediaSearcher.hasMoreResults()) {
            return this.getResultsExpanderView(parent);
        }

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
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

        return convertView;
    }

    private View getResultsExpanderView(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        View view = layoutInflater.inflate(R.layout.fragment_media_list_item, parent, false);

        TextView titleTextView = (TextView) view.findViewById(R.id.media_list_item_title);
        titleTextView.setText(R.string.media_result_list_show_more);

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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

    /*public class MoreMediaResultsTask extends AsyncTask<Object, Integer, List<Media>> {
        private Exception exception;
        private List<Media> mediaList;

        @Override
        protected List<Media> doInBackground(Object... params) {

            try {
                this.mediaList = MediaResultAdapter.this.mediaSearcher.runSearch();
                return this.mediaList;
            } catch (Exception ex) {
                this.exception = ex;
                this.mediaList = null;
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(List<Media> result) {

            MediaResultAdapter.this.mediaList.addAll(result);
            super.onPostExecute(result);
        }
    }*/
}
