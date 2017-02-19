package com.marshl.discus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MediaSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView searchText = (TextView) findViewById(R.id.search_text);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    public void onSearchButtonClick(View view) {
        this.performSearch();
    }

    private void performSearch() {
        Intent intent = new Intent(this, MediaSearchResults.class);

        SearchParameters params = new SearchParameters();

        EditText searchTextView = (EditText) findViewById(R.id.search_text);
        String searchText = searchTextView.getText().toString();
        params.setSearchText(searchText);

        RadioGroup ownershipRadioGroup = (RadioGroup) findViewById(R.id.ownership_radio_group);
        switch (ownershipRadioGroup.getCheckedRadioButtonId()) {
            case R.id.ownership_owned_radio:
                params.setSearchType(SearchParameters.SearchType.USER_OWNED);
                break;

            case R.id.ownership_not_owned_radio:
                params.setSearchType(SearchParameters.SearchType.NOT_USER_OWNED);
                break;

            case R.id.ownership_both_radio:
                params.setSearchType(SearchParameters.SearchType.BOTH);
                break;
        }

        intent.putExtra(SearchParameters.SEARCH_PARAM_PARCEL_NAME, params);
        startActivity(intent);
    }
}
