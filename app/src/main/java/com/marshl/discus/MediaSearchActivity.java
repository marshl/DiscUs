package com.marshl.discus;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.marshl.util.Connectivity;
import com.marshl.util.NetworkStateReceiver;

public class MediaSearchActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private RadioButton ownedSearchRadioButton;
    private RadioButton anySearchRadioButton;
    private RadioButton unownedSearchRadioButton;
    private RadioGroup ownershipRadioGroup;
    private NetworkStateReceiver networkStateReceiver;
    private EditText searchTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.ownedSearchRadioButton = (RadioButton) this.findViewById(R.id.ownership_owned_radio);
        this.anySearchRadioButton = (RadioButton) this.findViewById(R.id.ownership_both_radio);
        this.unownedSearchRadioButton = (RadioButton) this.findViewById(R.id.ownership_not_owned_radio);
        this.ownershipRadioGroup = (RadioGroup) this.findViewById(R.id.ownership_radio_group);
        this.searchTextView = (EditText) findViewById(R.id.search_text);

        this.searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    MediaSearchActivity.this.performSearch();
                    return true;
                }
                return false;
            }
        });

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        if (Connectivity.isConnected(this)) {
            this.enableNetworkActions();
        } else {
            this.disableNetworkActions();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    public void onSearchButtonClick(View view) {
        this.performSearch();
    }

    private void performSearch() {

        if (!Connectivity.isConnected(this) && this.ownershipRadioGroup.getCheckedRadioButtonId() != R.id.ownership_owned_radio) {
            Toast toast = Toast.makeText(this,
                    "You have to connect to the internet to search for media you don't own",
                    Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Intent intent = new Intent(this, MediaSearchResults.class);

        SearchParameters params = new SearchParameters();

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

    @Override
    public void networkAvailable() {

        Toast toast = Toast.makeText(this,
                "Connected to internet",
                Toast.LENGTH_LONG);
        toast.show();

        this.enableNetworkActions();
    }

    @Override
    public void networkUnavailable() {
        Toast toast = Toast.makeText(this,
                "No internet connection",
                Toast.LENGTH_LONG);
        toast.show();

        this.disableNetworkActions();
    }

    private void disableNetworkActions() {
        this.unownedSearchRadioButton.setEnabled(false);
        this.anySearchRadioButton.setEnabled(false);
        this.ownershipRadioGroup.check(R.id.ownership_owned_radio);
    }

    private void enableNetworkActions() {
        this.unownedSearchRadioButton.setEnabled(true);
        this.anySearchRadioButton.setEnabled(true);
    }
}
