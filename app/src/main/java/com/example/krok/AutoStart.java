package com.example.krok;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.krok.MService;

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MService.class));
    }
}
