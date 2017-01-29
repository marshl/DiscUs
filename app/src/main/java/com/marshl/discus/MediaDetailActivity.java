package com.marshl.discus;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
            DateFormat formatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
            releaseDateView.setText(formatter.format(this.media.getReleaseDate()));
        } else {
            TextView releaseDateLabel = (TextView)this.findViewById(R.id.media_release_label);
            releaseDateLabel.setVisibility(View.GONE);
            releaseDateView.setVisibility(View.GONE);
        }

        TextView contentRatingView = (TextView)this.findViewById(R.id.media_content_rating);
        contentRatingView.setText(this.media.getContentRating());

        TextView genreView = (TextView)this.findViewById(R.id.media_genre);
        genreView.setText(this.media.getGenres());

        TextView plotView = (TextView)this.findViewById(R.id.media_plot);
        plotView.setText(this.media.getPlot());

        TextView directorView = (TextView)this.findViewById(R.id.media_director);
        directorView.setText(this.media.getDirector());

        TextView writerView = (TextView)this.findViewById(R.id.media_writer);
        writerView.setText(this.media.getWriter());

        TextView metascoreView = (TextView)this.findViewById(R.id.media_metascore);
        metascoreView.setText(String.format(Locale.getDefault(), "%d", this.media.getMetascore()));

        int metascoreColor = MetascoreUtils.getMetascoreColor(this.media.getMetascore(), this.media.getType() != null && this.media.getType().equals("game"));
        metascoreView.setBackgroundColor(metascoreColor);
        Log.d("COLOR", "Score: " + Integer.toString(this.media.getMetascore()) + " " + String.format(Locale.getDefault(), "%d", this.media.getMetascore()));
    }
}
