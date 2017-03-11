package com.marshl.mediamogul;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Locale;

public class MediaDetailActivity extends AppCompatActivity {

    public static final String RESULT_INTENT_IMDB_ID = "IMDB_ID";
    public static final String RESULT_INTENT_OWNERSHIP = "OWNERSHIP";

    private ParcelableMedia media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.media = this.getIntent().getParcelableExtra(ParcelableMedia.MEDIA_PARCEL_NAME);

        setContentView(R.layout.activity_media_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView mediaTitleView = (TextView) this.findViewById(R.id.media_details_title);
        mediaTitleView.setText(this.media.getTitle());

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


        if (media.getMetascore() != null && media.getMetascore() != 0) {
            TextView metascoreView = (TextView) this.findViewById(R.id.media_metascore);
            LinearLayout metascoreWrapper = (LinearLayout) this.findViewById(R.id.media_metascore_wrapper);

            metascoreView.setText(String.format(Locale.getDefault(), "%d", this.media.getMetascore()));
            int metascoreColor = MetascoreUtils.getMetascoreColor(this.media.getMetascore(), this.media.isGame());
            int metascoreTextColor = MetascoreUtils.getMetascoreTextColor(this.media.getMetascore(), this.media.isGame());

            metascoreWrapper.setBackgroundColor(metascoreColor);
            metascoreView.setTextColor(metascoreTextColor);
        } else {
            LinearLayout metascoreContainer = (LinearLayout) findViewById(R.id.media_metascore_container);
            metascoreContainer.setVisibility(View.GONE);
        }

        if (this.media.getImdbRating() != null) {
            TextView imdbRating = (TextView) this.findViewById(R.id.media_imdb_rating);
            TextView imdbVotesView = (TextView) this.findViewById(R.id.media_imdb_votes);
            imdbRating.setText(getResources().getString(R.string.imdb_rating, media.getImdbRating()));
            imdbVotesView.setText(getResources().getQuantityString(R.plurals.imdb_votes, this.media.getImdbVotes(), this.media.getImdbVotes()));
        } else {
            View imdbContainer = this.findViewById(R.id.media_imdb_container);
            imdbContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(RESULT_INTENT_IMDB_ID, this.media.getImdbId());
        data.putExtra(RESULT_INTENT_OWNERSHIP, this.media.getOwnershipStatus().ordinal());
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
