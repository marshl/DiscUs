package com.marshl.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

//import static com.marshl.discus.MediaSearchActivity.CONNECTIVITY_ACTION_LOLLIPOP;

/**
 * Created by Tommaso Resti (http://stackoverflow.com/questions/6169059/android-event-for-internet-connectivity-state-change)
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    protected List<NetworkStateReceiverListener> listeners;
    protected Boolean connected;

    public NetworkStateReceiver() {
        listeners = new ArrayList<NetworkStateReceiverListener>();
        connected = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();

        if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        } else {
            connected = false;
        }

        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for (NetworkStateReceiverListener listener : listeners) {
            notifyState(listener);
        }
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if (connected == null || listener == null) {
            return;
        }

        if (connected) {
            listener.networkAvailable();
        } else {
            listener.networkUnavailable();
        }
    }

    public void addListener(NetworkStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(NetworkStateReceiverListener l) {
        listeners.remove(l);
    }

    public interface NetworkStateReceiverListener {
        public void networkAvailable();

        public void networkUnavailable();
    }
}
