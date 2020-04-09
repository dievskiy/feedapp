package com.feedapp.app.data.models.connection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData


class ConnectionLiveData(val context: Context) : LiveData<ConnectionModel?>() {
    override fun onActive() {
        super.onActive()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(networkReceiver, filter)
    }

    override fun onInactive() {
        super.onInactive()
        context.unregisterReceiver(networkReceiver)
    }

    val WIFI_DATA = 0
    val MOBILE_DATA = 0

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.extras != null) {
                val activeNetwork =
                    intent.extras!![ConnectivityManager.EXTRA_NETWORK_INFO] as NetworkInfo?
                val isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting
                if (isConnected) {
                    when (activeNetwork!!.type) {
                        ConnectivityManager.TYPE_WIFI -> postValue(ConnectionModel(WIFI_DATA, true))
                        ConnectivityManager.TYPE_MOBILE -> postValue(
                            ConnectionModel(
                                MOBILE_DATA,
                                true
                            )
                        )
                    }
                } else {
                    postValue(ConnectionModel(0, false))
                }
            }
        }
    }

}