package com.marshl.discus;

import android.app.Activity;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class MediaResultAdapter implements ListAdapter {
    private Activity context;

    public MediaResultAdapter(Activity context)
    {
        this.context = context;
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
        return 0;
    }

    @Override
    public Object getItem(int _position) {
        return null;
    }

    @Override
    public long getItemId(int _position) {
        return _position;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getView(int _position, View _convertView, ViewGroup _parent) {
        if (_convertView == null) {
            LayoutInflater layoutInflater = this.context.getLayoutInflater();
            //_convertView = layoutInflater.inflate(R.layout.station_list_item, _parent, false);
        }

        return _convertView;
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
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
