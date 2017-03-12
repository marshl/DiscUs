package com.marshl.mediamogul;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Locale;

public class MediaDetailActivity extends AppCompatActivity {

    public static final String RESULT_INTENT_IMDB_ID = "IMDB_ID";
    public static final String RESULT_INTENT_OWNERSHIP = "OWNERSHIP";

    private static final String URL_ENCODING = "utf-8";

    private ParcelableMedia media;

    private Button wishlistButton;
    private Button libraryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.media = this.getIntent().getParcelableExtra(ParcelableMedia.MEDIA_PARCEL_NAME);

        final Resources res = this.getResources();

        setContentView(R.layout.activity_media_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.wishlistButton = (Button) this.findViewById(R.id.media_wishlist_button);
        this.libraryButton = (Button) this.findViewById(R.id.media_library_button);

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
        directorView.setText(res.getString(R.string.media_director, this.media.getDirector()));

        TextView writerView = (TextView) this.findViewById(R.id.media_writer);
        writerView.setText(res.getString(R.string.media_writer, this.media.getWriter()));

        if (media.getMetascore() != null && media.getMetascore() != 0) {
            final TextView metascoreView = (TextView) this.findViewById(R.id.media_metascore);
            final LinearLayout metascoreWrapper = (LinearLayout) this.findViewById(R.id.media_metascore_wrapper);

            metascoreView.setText(String.format(Locale.getDefault(), "%d", this.media.getMetascore()));
            final int metascoreColor = MetascoreUtils.getMetascoreColor(this.media.getMetascore(), this.media.isGame());
            final int metascoreTextColor = MetascoreUtils.getMetascoreTextColor(this.media.getMetascore(), this.media.isGame());

            metascoreWrapper.setBackgroundColor(metascoreColor);
            metascoreView.setTextColor(metascoreTextColor);

            final TextView metacriticLink = (TextView) this.findViewById(R.id.media_metacritic_link);
            metacriticLink.setText(this.getUrlLinkText(metacriticLink.getText()), TextView.BufferType.SPANNABLE);

            metacriticLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.metacritic.com/search/all/" + URLEncoder.encode(media.getTitle(), URL_ENCODING) + "/results"));
                        startActivity(browserIntent);
                    } catch (UnsupportedEncodingException ex) {

                    }
                }
            });

        } else {
            LinearLayout metascoreContainer = (LinearLayout) findViewById(R.id.media_metascore_container);
            metascoreContainer.setVisibility(View.GONE);
        }

        if (this.media.getImdbRating() != null) {
            TextView imdbRating = (TextView) this.findViewById(R.id.media_imdb_rating);
            TextView imdbVotesView = (TextView) this.findViewById(R.id.media_imdb_votes);
            imdbRating.setText(res.getString(R.string.imdb_rating, media.getImdbRating()));
            imdbVotesView.setText(res.getQuantityString(R.plurals.imdb_votes, this.media.getImdbVotes(), this.media.getImdbVotes()));

            final TextView imdbLink = (TextView) this.findViewById(R.id.media_imdb_link);
            imdbLink.setText(this.getUrlLinkText(imdbLink.getText()), TextView.BufferType.SPANNABLE);

            imdbLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.imdb.com/title/" + URLEncoder.encode(media.getImdbId(), URL_ENCODING) + "/"));
                        startActivity(browserIntent);
                    } catch (UnsupportedEncodingException ex) {

                    }
                }
            });

        } else {
            View imdbContainer = this.findViewById(R.id.media_imdb_container);
            imdbContainer.setVisibility(View.GONE);
        }

        this.refreshButtonLayout();
    }

    private SpannableString getUrlLinkText(CharSequence text) {
        final SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new URLSpan(""), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
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
        if (this.media.getOwnershipStatus() == Media.OwnershipType.OWNED) {
            this.media.setOwnershipStatus(Media.OwnershipType.NOT_OWNED);
        } else {
            this.media.setOwnershipStatus(Media.OwnershipType.OWNED);
        }

        this.insertIntoDatabase();
        this.refreshButtonLayout();
    }

    public void onAddToWishlistClick(View view) {

        if (this.media.getOwnershipStatus() == Media.OwnershipType.OWNED) {
            return;
        }

        if (this.media.getOwnershipStatus() == Media.OwnershipType.ON_WISHLIST) {
            this.media.setOwnershipStatus(Media.OwnershipType.NOT_OWNED);
        } else {
            this.media.setOwnershipStatus(Media.OwnershipType.ON_WISHLIST);
        }
        this.insertIntoDatabase();
        this.refreshButtonLayout();
    }


    private void insertIntoDatabase() {
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

    private void refreshButtonLayout() {

        final Resources res = this.getResources();

        switch (this.media.getOwnershipStatus()) {
            case OWNED:
                this.libraryButton.setBackgroundColor(ResourcesCompat.getColor(res, android.R.color.holo_blue_dark, null));
                this.libraryButton.setText(res.getString(R.string.remove_from_library));

                this.wishlistButton.setBackgroundColor(ResourcesCompat.getColor(res, android.R.color.background_light, null));
                this.wishlistButton.setText(res.getString(R.string.add_to_wishlist));
                this.wishlistButton.setEnabled(false);
                break;
            case NOT_OWNED:
                this.libraryButton.setBackgroundColor(ResourcesCompat.getColor(res, android.R.color.background_light, null));
                this.libraryButton.setText(res.getString(R.string.add_to_library));
                this.wishlistButton.setBackgroundColor(ResourcesCompat.getColor(res, android.R.color.background_light, null));
                this.wishlistButton.setText(res.getString(R.string.add_to_wishlist));
                this.wishlistButton.setEnabled(true);
                break;
            case ON_WISHLIST:
                this.libraryButton.setBackgroundColor(ResourcesCompat.getColor(res, android.R.color.background_light, null));
                this.libraryButton.setText(res.getString(R.string.add_to_library));
                this.wishlistButton.setBackgroundColor(ResourcesCompat.getColor(res, android.R.color.holo_blue_dark, null));
                this.wishlistButton.setText(res.getString(R.string.remove_from_wishlist));
                this.wishlistButton.setEnabled(true);
                break;
        }
    }
}
