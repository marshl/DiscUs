package com.marshl.mediamogul;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Locale;

public class MediaDetailActivity extends AppCompatActivity {

    private ParcelableMedia media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.media = this.getIntent().getParcelableExtra(ParcelableMedia.MEDIA_PARCEL_NAME);

        setContentView(R.layout.activity_media_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(this.media.getTitle() + " (" + this.media.getYear() + ")");

        TextView releaseDateView = (TextView) this.findViewById(R.id.media_release_date);
        if (this.media.getReleaseDate() != null) {
            DateFormat formatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
            releaseDateView.setText(formatter.format(this.media.getReleaseDate()));
        } else {
            TextView releaseDateLabel = (TextView) this.findViewById(R.id.media_release_label);
            releaseDateLabel.setVisibility(View.GONE);
            releaseDateView.setVisibility(View.GONE);
        }

        TextView contentRatingView = (TextView) this.findViewById(R.id.media_content_rating);
        contentRatingView.setText(this.media.getContentRating());

        TextView genreView = (TextView) this.findViewById(R.id.media_genre);
        genreView.setText(this.media.getGenres());

        TextView plotView = (TextView) this.findViewById(R.id.media_plot);
        plotView.setText(this.media.getPlot());

        TextView directorView = (TextView) this.findViewById(R.id.media_director);
        directorView.setText(this.media.getDirector());

        TextView writerView = (TextView) this.findViewById(R.id.media_writer);
        writerView.setText(this.media.getWriter());

        TextView metascoreView = (TextView) this.findViewById(R.id.media_metascore);
        TextView metascoreLabelVew = (TextView) this.findViewById(R.id.media_metascore_label);
        if (media.getMetascore() != null && media.getMetascore() != 0) {
            metascoreView.setText(String.format(Locale.getDefault(), "%d", this.media.getMetascore()));

            int metascoreColor = MetascoreUtils.getMetascoreColor(this.media.getMetascore(), this.media.getType() != null && this.media.getType().equals("game"));
            metascoreView.setBackgroundColor(metascoreColor);
        } else {
            metascoreView.setVisibility(View.GONE);
            metascoreLabelVew.setVisibility(View.GONE);
        }

        RatingBar imdbRatingBar = (RatingBar) this.findViewById(R.id.media_imdb_rating);
        TextView imdbVotesView = (TextView) this.findViewById(R.id.media_imdb_votes);
        if (this.media.getImdbRating() != null) {
            imdbRatingBar.setRating(this.media.getImdbRating());

            DecimalFormat formatter = new DecimalFormat("#,###");
            imdbVotesView.setText("(" + formatter.format(this.media.getImdbVotes()) + ")");
        } else {

            imdbRatingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("IMDB_ID", this.media.getImdbId());
        data.putExtra("OWNERSHIP", this.media.getOwnershipStatus().ordinal());
        setResult(RESULT_OK, data);
        finish();
    }

    public void onAddToLibraryClick(View view) {

        this.media.setOwnershipStatus(Media.OwnershipType.OWNED);

        MediaReaderDbHelper dbHelper = new MediaReaderDbHelper(this);
        try {
            dbHelper.insertMediaRecord(this.media);
        } catch (SQLException ex) {
            Toast toast = Toast.makeText(this,
                    "An error occurred when adding the media: " + ex.getMessage(),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
