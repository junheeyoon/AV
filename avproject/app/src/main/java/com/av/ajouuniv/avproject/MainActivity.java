package com.av.ajouuniv.avproject;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.ajouuniv.linphone.service.Linphone;
import com.ajouuniv.linphone.service.callback.PhoneCallback;
import com.aykuttasil.callrecord.CallRecord;

import org.linphone.core.LinphoneCall;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    CallRecord callRecord;
    @BindView(R.id.hang_up) Button mHangUp;
    @BindView(R.id.toggle_speaker) Button mToggleSpeaker;
    @BindView(R.id.toggle_mute) Button mToggleMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        callRecord = new CallRecord.Builder(this)
                .setRecordFileName("RecordFileName")
                .setRecordDirName("Recordtest")
                .setRecordDirPath(Environment.getExternalStorageDirectory().getPath())
                .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                .setShowSeed(true)
                .build();

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
                Linphone.toggleSpeaker(Linphone.getVideoEnabled());
                Linphone.toggleMicro(false);
                mToggleSpeaker.setVisibility(View.VISIBLE);
                mToggleMute.setVisibility(View.VISIBLE);
                callRecord.startCallReceiver();
            }

            @Override
            public void callEnd() {
                super.callEnd();
                mHangUp.setVisibility(View.GONE);
                mToggleMute.setVisibility(View.GONE);
                mToggleSpeaker.setVisibility(View.GONE);
                callRecord.stopCallReceiver();
                Log.d(TAG,""+callRecord.getStateSaveFile()+callRecord.getRecordDirPath()+callRecord.getRecordDirName()+callRecord.getRecordFileName());
            }
        });
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
}
