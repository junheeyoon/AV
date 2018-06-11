package com.av.ajouuniv.avproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ajouuniv.linphone.service.Linphone;
import com.ajouuniv.linphone.service.callback.PhoneCallback;

import org.linphone.core.LinphoneCall;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BluetoothSPP bt;
    public static Context mContext;

    @BindView(R.id.hang_up) Button mHangUp;
    @BindView(R.id.toggle_speaker) Button mToggleSpeaker;
    @BindView(R.id.toggle_mute) Button mToggleMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;

        startService(new Intent(this, CallMonitorService.class));

        bt = new BluetoothSPP(this);
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_ANDROID);
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to " + name + "\n" + address, Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext(), "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            public void onDataReceived(byte[] data, String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, VideoActivity.class));

            }
        });

        Linphone.addCallback(null, new PhoneCallback() {
            @Override
            public void incomingCall(LinphoneCall linphoneCall) {
                super.incomingCall(linphoneCall);
                Linphone.toggleSpeaker(true);
                Linphone.acceptCall();
                mHangUp.setVisibility(View.VISIBLE);
            }

            @Override
            public void callConnected() {
                super.callConnected();
                //Linphone.toggleSpeaker(Linphone.getVideoEnabled());
                Linphone.toggleSpeaker(true);
                Linphone.toggleMicro(false);
                mToggleSpeaker.setVisibility(View.VISIBLE);
                mToggleMute.setVisibility(View.VISIBLE);
                btSend();
            }

            @Override
            public void callEnd() {
                super.callEnd();
                sendBroadcast(new Intent(VideoActivity.RECEIVE_FINISH_VIDEO_ACTIVITY));
                mHangUp.setVisibility(View.GONE);
                mToggleMute.setVisibility(View.GONE);
                mToggleSpeaker.setVisibility(View.GONE);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.hang_up)
    public void hangUp() {
        Linphone.hangUp();
    }

    @OnClick(R.id.toggle_mute)
    public void toggleMute() {
        Linphone.toggleMicro(!Linphone.getLC().isMicMuted());
    }

    @OnClick(R.id.toggle_speaker)
    public void toggleSpeaker() {
        Linphone.toggleSpeaker(!Linphone.getLC().isSpeakerEnabled());
    }

    public void btSend(){
        final AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                if(!audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(true);
                if(audioManager.isMicrophoneMute())
                    audioManager.setMicrophoneMute(false);
                bt.send("Text", true);
            }
        }, 1500);
    }
}
