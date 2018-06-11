package com.av.ajouuniv.avproject;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;
import java.util.List;

import static android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;


public class CallMonitorService extends Service {
    MediaSessionManager mediaSessionManager;
    MediaController mCallMediaController;
    boolean mRinging;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    PhoneStateListener mLitener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    mRinging = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sendHeadsetHookLollipop();
                    } else {
                        acceptCall_4_1();
                    }
                    break;
                default:
                    mRinging = false;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mLitener, PhoneStateListener.LISTEN_CALL_STATE);
        mediaSessionManager = (MediaSessionManager) getApplicationContext().getSystemService(Context.MEDIA_SESSION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager.addOnActiveSessionsChangedListener(new OnActiveSessionsChangedListener() {
                @Override
                public void onActiveSessionsChanged(@Nullable List<MediaController> controllers) {
                    for (MediaController m : controllers) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if ("com.android.server.telecom".equals(m.getPackageName())) {
                                mCallMediaController = m;
                                if (mRinging) {
                                    sendHeadsetHookLollipop();
                                }
                            }
                        }
                    }
                }
            }, new ComponentName(getApplicationContext(), NotificationReceiverService.class));
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void sendHeadsetHookLollipop() {

        try {
            if (mCallMediaController != null) {
                mCallMediaController.dispatchMediaButtonEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                ((MainActivity)MainActivity.mContext).btSend();
            }
        } catch (SecurityException e) {
            Log.e(getClass().getSimpleName(), "Permission error. ", e);
        }
    }


    private static final String MANUFACTURER_HTC = "HTC";

    public void acceptCall_4_1() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        boolean broadcastConnected = MANUFACTURER_HTC.equalsIgnoreCase(Build.MANUFACTURER)
                && !audioManager.isWiredHeadsetOn();
        if (broadcastConnected) {
            broadcastHeadsetConnected(false);
        }
        try {
            try {
                Runtime.getRuntime().exec("input keyevent " +
                        Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
            } catch (IOException e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(
                        Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP,
                                KeyEvent.KEYCODE_HEADSETHOOK));
                sendOrderedBroadcast(btnDown, enforcedPerm);
                sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(false);
            }
        }
    }

    private void broadcastHeadsetConnected(boolean connected) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");
        try {
            sendOrderedBroadcast(i, null);
        } catch (Exception e) {
        }
    }

}
