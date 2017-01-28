package com.marshl.discus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Locale;

public class MediaDetailActivity extends AppCompatActivity {

    private ParcelableMedia media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.media = this.getIntent().getParcelableExtra("media");

        setContentView(R.layout.activity_media_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(this.media.getTitle() + " (" + this.media.getYear() + ")");

        TextView releaseDateView = (TextView) this.findViewById(R.id.media_release_date);
        if (this.media.getReleaseDate() != null) {
            DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            releaseDateView.setText(formatter.format(this.media.getReleaseDate()));
        } else {
            TextView releaseDateLabel = (TextView)this.findViewById(R.id.media_release_label);
            releaseDateLabel.setVisibility(View.GONE);
            releaseDateView.setVisibility(View.GONE);
        }
    }
}
