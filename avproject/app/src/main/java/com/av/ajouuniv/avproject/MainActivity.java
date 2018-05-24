package com.av.ajouuniv.avproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.ajouuniv.linphone.service.Linphone;
import com.ajouuniv.linphone.service.callback.PhoneCallback;
import com.av.ajouuniv.avproject.model.ApiUser;
import com.av.ajouuniv.avproject.model.User;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.linphone.core.LinphoneCall;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.hang_up) Button mHangUp;
    @BindView(R.id.toggle_speaker) Button mToggleSpeaker;
    @BindView(R.id.toggle_mute) Button mToggleMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
                Rx2AndroidNetworking.get("https://ajouuniv.av/getAnUser/{userId}")
                        .addPathParameter("userId", "1")
                        .build()
                        .getObjectObservable(ApiUser.class)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<ApiUser, User>() {
                            @Override
                            public User apply(ApiUser apiUser) throws Exception {
                                // here we get ApiUser from server
                                User user = new User(apiUser);
                                // then by converting, we are returning user
                                return user;
                            }
                        })
                        .subscribe(new Observer<User>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(User user) {
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }

            @Override
            public void callEnd() {
                super.callEnd();
                mHangUp.setVisibility(View.GONE);
                mToggleMute.setVisibility(View.GONE);
                mToggleSpeaker.setVisibility(View.GONE);
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
