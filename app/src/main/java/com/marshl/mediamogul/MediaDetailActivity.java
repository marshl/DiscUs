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
        mediaTitleView.setText(this.media.getString(Media.TITLE_KEY));

        TextView releaseDateView = (TextView) this.findViewById(R.id.media_release_date);
        if (this.media.hasKey(Media.RELEASE_DATE_KEY)) {
            releaseDateView.setText(this.media.getString(Media.RELEASE_DATE_KEY));
        } else {
            releaseDateView.setVisibility(View.GONE);
        }

        TextView contentRatingView = (TextView) this.findViewById(R.id.media_content_rating);
        contentRatingView.setText(this.media.getString(media.CONTENT_RATING_KEY));

        TextView genreView = (TextView) this.findViewById(R.id.media_genre);
        genreView.setText(this.media.getString(Media.GENRES_KEY));

        TextView plotView = (TextView) this.findViewById(R.id.media_plot);
        plotView.setText(this.media.getString(Media.PLOT_KEY));

        TextView directorView = (TextView) this.findViewById(R.id.media_director);
        directorView.setText(res.getString(R.string.media_director, this.media.getString(Media.DIRECTOR_KEY)));

        TextView writerView = (TextView) this.findViewById(R.id.media_writer);
        writerView.setText(res.getString(R.string.media_writer, this.media.getString(Media.WRITER_KEY)));

        if (media.hasKey(Media.METASCORE_KEY)) {
            final TextView metascoreView = (TextView) this.findViewById(R.id.media_metascore);
            final LinearLayout metascoreWrapper = (LinearLayout) this.findViewById(R.id.media_metascore_wrapper);

            metascoreView.setText(this.media.getString(Media.METASCORE_KEY));
            try {
                int metascore = this.media.getInt(Media.METASCORE_KEY);
                final int metascoreColor = MetascoreUtils.getMetascoreColor(metascore, this.media.isGame());
                final int metascoreTextColor = MetascoreUtils.getMetascoreTextColor(metascore, this.media.isGame());

                metascoreWrapper.setBackgroundColor(metascoreColor);
                metascoreView.setTextColor(metascoreTextColor);

                final TextView metacriticLink = (TextView) this.findViewById(R.id.media_metacritic_link);
                metacriticLink.setText(this.getUrlLinkText(metacriticLink.getText()), TextView.BufferType.SPANNABLE);

                metacriticLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.metacritic.com/search/all/" + URLEncoder.encode(media.getString(Media.TITLE_KEY), URL_ENCODING) + "/results"));
                            startActivity(browserIntent);
                        } catch (UnsupportedEncodingException ex) {

                        }
                    }
                });
            } catch (NumberFormatException ex) {
                LinearLayout metascoreContainer = (LinearLayout) findViewById(R.id.media_metascore_container);
                metascoreContainer.setVisibility(View.GONE);
            }
        } else {
            LinearLayout metascoreContainer = (LinearLayout) findViewById(R.id.media_metascore_container);
            metascoreContainer.setVisibility(View.GONE);
        }

        if (this.media.hasKey(Media.IMDB_RATING_KEY)) {
            TextView imdbRating = (TextView) this.findViewById(R.id.media_imdb_rating);
            TextView imdbVotesView = (TextView) this.findViewById(R.id.media_imdb_votes);
            imdbRating.setText(res.getString(R.string.imdb_rating, media.getFloat(Media.IMDB_RATING_KEY)));
            final int imdbVotes = this.media.getInt(Media.IMDB_VOTES_KEY);
            imdbVotesView.setText(res.getQuantityString(R.plurals.imdb_votes, imdbVotes, imdbVotes));

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
        data.putExtra(RESULT_INTENT_OWNERSHIP, this.media.getOwnershipStatus());
        setResult(RESULT_OK, data);
        finish();
    }

    public void onAddToLibraryClick(View view) {
        if (this.media.getOwnershipStatus() == Media.OWNERSHIP_OWNED) {
            this.media.setOwnershipStatus(Media.OWNERSHIP_NOT_OWNED);
        } else {
            this.media.setOwnershipStatus(Media.OWNERSHIP_OWNED);
        }

        this.insertIntoDatabase();
        this.refreshButtonLayout();
    }

    public void onAddToWishlistClick(View view) {

        if (this.media.getOwnershipStatus() == Media.OWNERSHIP_OWNED) {
            return;
        }

        if (this.media.getOwnershipStatus() == Media.OWNERSHIP_ON_WISHLIST) {
            this.media.setOwnershipStatus(Media.OWNERSHIP_NOT_OWNED);
        } else {
            this.media.setOwnershipStatus(Media.OWNERSHIP_ON_WISHLIST);
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

        final int highlightColor = ResourcesCompat.getColor(res, android.R.color.holo_blue_dark, null);
        final int normalColor = ResourcesCompat.getColor(res, android.R.color.background_light, null);

        final int textHighlightColor = ResourcesCompat.getColor(res, android.R.color.white, null);
        final int textNormalColor = ResourcesCompat.getColor(res, android.R.color.black, null);

        switch (this.media.getOwnershipStatus()) {
            case Media.OWNERSHIP_OWNED:
                this.libraryButton.setBackgroundColor(highlightColor);
                this.libraryButton.setTextColor(textHighlightColor);
                this.libraryButton.setText(res.getString(R.string.remove_from_library));

                this.wishlistButton.setBackgroundColor(normalColor);
                this.wishlistButton.setTextColor(textNormalColor);
                this.wishlistButton.setText(res.getString(R.string.add_to_wishlist));
                this.wishlistButton.setEnabled(false);
                break;
            case Media.OWNERSHIP_NOT_OWNED:
                this.libraryButton.setBackgroundColor(normalColor);
                this.libraryButton.setTextColor(textNormalColor);
                this.libraryButton.setText(res.getString(R.string.add_to_library));

                this.wishlistButton.setBackgroundColor(normalColor);
                this.wishlistButton.setTextColor(textNormalColor);
                this.wishlistButton.setText(res.getString(R.string.add_to_wishlist));
                this.wishlistButton.setEnabled(true);
                break;
            case Media.OWNERSHIP_ON_WISHLIST:
                this.libraryButton.setBackgroundColor(normalColor);
                this.libraryButton.setTextColor(textNormalColor);
                this.libraryButton.setText(res.getString(R.string.add_to_library));

                this.wishlistButton.setBackgroundColor(highlightColor);
                this.wishlistButton.setTextColor(textHighlightColor);
                this.wishlistButton.setText(res.getString(R.string.remove_from_wishlist));
                this.wishlistButton.setEnabled(true);
                break;
        }
    }
}
