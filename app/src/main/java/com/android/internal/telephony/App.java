package com.android.internal.telephony;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.services.CallService;
import com.android.internal.telephony.services.SignalingService;
import com.android.internal.telephony.utils.Constants;
import com.android.internal.telephony.utils.NetworkUtils;

import org.abtollc.sdk.AbtoApplication;

/**
 * Created by Eslam on 1/22/2018.
 */

public class App extends AbtoApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListner(), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        stopService(new Intent(getBaseContext(), SignalingService.class));
        stopService(new Intent(getBaseContext(), CallService.class));
    }

    private class PhoneStateListner extends PhoneStateListener {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            Constants.setCurrentCellularLevel((short)signalStrength.getLevel());
            //read wifi
            if(!NetworkUtils.getWifiState(getApplicationContext())){
                NetworkUtils.turnOnWifi(getApplicationContext());
            }
            Constants.setDeviceIP(NetworkUtils.getWifiApIpAddress());
            Constants.setCurrentWifiLevel((short) NetworkUtils.getWifiSignalLevel(getApplicationContext()));
            Constants.setCurrentCellularLevel((short) signalStrength.getLevel());
            Log.i(Constants.TAG,String.format("Current cellular level is: %d",signalStrength.getLevel()));
            Log.i(Constants.TAG,String.format("Current wifi level is: %d", NetworkUtils.getWifiSignalLevel(getApplicationContext())));
        }
    }

}
