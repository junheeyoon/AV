package com.ajouuniv.linphone.service;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.view.SurfaceView;

import com.ajouuniv.linphone.service.callback.PhoneCallback;
import com.ajouuniv.linphone.service.callback.RegistrationCallback;
import com.ajouuniv.linphone.service.linphone.LinphoneManager;
import com.ajouuniv.linphone.service.linphone.LinphoneUtils;
import com.ajouuniv.linphone.service.linphone.PhoneBean;
import com.ajouuniv.linphone.service.keepservice.LinphoneService;

import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.mediastream.video.AndroidVideoWindowImpl;

import static java.lang.Thread.sleep;

public class Linphone {
    private static ServiceWaitThread mServiceWaitThread;
    private static String mUsername, mPassword, mServerIP;
    private static AndroidVideoWindowImpl mAndroidVideoWindow;
    private static SurfaceView mRenderingView, mPreviewView;

    public static void startService(Context context) {
        if (!LinphoneService.isReady()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClass(context, LinphoneService.class);
            context.startService(intent);
        }
    }

    public static void setAccount(String username, String password, String serverIP) {
        mUsername = username;
        mPassword = password;
        mServerIP = serverIP;
    }

    public static void addCallback(RegistrationCallback registrationCallback,
                                   PhoneCallback phoneCallback) {
        if (LinphoneService.isReady()) {
            LinphoneService.addRegistrationCallback(registrationCallback);
            LinphoneService.addPhoneCallback(phoneCallback);
        } else {
            mServiceWaitThread = new ServiceWaitThread(registrationCallback, phoneCallback);
            mServiceWaitThread.start();
        }
    }

    public static void login() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!LinphoneService.isReady()) {
                    try {
                        sleep(80);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                loginToServer();
            }
        }).start();
    }

    public static void callTo(String num, boolean isVideoCall) {
        if (!LinphoneService.isReady() || !LinphoneManager.isInstanceiated()) {
            return;
        }
        if (!num.equals("")) {
            PhoneBean phone = new PhoneBean();
            phone.setUserName(num);
            phone.setHost(mServerIP);
            LinphoneUtils.getInstance().startSingleCallingTo(phone, isVideoCall);
        }
    }

    public static void acceptCall() {
        try {
            LinphoneManager.getLc().acceptCall(LinphoneManager.getLc().getCurrentCall());
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }
    }

    public static void hangUp() {
        LinphoneUtils.getInstance().hangUp();
    }

    public static void toggleMicro(boolean isMicMuted) {
        LinphoneUtils.getInstance().toggleMicro(isMicMuted);
    }

    public static void toggleSpeaker(boolean isSpeakerEnabled) {
        LinphoneUtils.getInstance().toggleSpeaker(isSpeakerEnabled);
    }

    private static class ServiceWaitThread extends Thread {
        private PhoneCallback mPhoneCallback;
        private RegistrationCallback mRegistrationCallback;

        ServiceWaitThread(RegistrationCallback registrationCallback, PhoneCallback phoneCallback) {
            mRegistrationCallback = registrationCallback;
            mPhoneCallback = phoneCallback;
        }

        @Override
        public void run() {
            super.run();
            while (!LinphoneService.isReady()) {
                try {
                    sleep(80);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            LinphoneService.addPhoneCallback(mPhoneCallback);
            LinphoneService.addRegistrationCallback(mRegistrationCallback);
            mServiceWaitThread = null;
        }
    }

    private static void loginToServer() {
        try {
            if (mUsername == null || mPassword == null || mServerIP == null) {
                throw new RuntimeException("The sip account is not configured.");
            }
            LinphoneUtils.getInstance().registerUserAuth(mUsername, mPassword, mServerIP);
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }
    }

    public static boolean getVideoEnabled() {
        LinphoneCallParams remoteParams = LinphoneManager.getLc().getCurrentCall().getRemoteParams();
        return remoteParams != null && remoteParams.getVideoEnabled();
    }

    public static void setAndroidVideoWindow(final SurfaceView[] renderingView, final SurfaceView[] previewView) {
        mRenderingView = renderingView[0];
        mPreviewView = previewView[0];
        fixZOrder(mRenderingView, mPreviewView);
        mAndroidVideoWindow = new AndroidVideoWindowImpl(renderingView[0], previewView[0], new AndroidVideoWindowImpl.VideoWindowListener() {
            @Override
            public void onVideoRenderingSurfaceReady(AndroidVideoWindowImpl androidVideoWindow, SurfaceView surfaceView) {
                setVideoWindow(androidVideoWindow);
                renderingView[0] = surfaceView;
            }

            @Override
            public void onVideoRenderingSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {
                removeVideoWindow();
            }

            @Override
            public void onVideoPreviewSurfaceReady(AndroidVideoWindowImpl androidVideoWindow, SurfaceView surfaceView) {
                mPreviewView = surfaceView;
                setPreviewWindow(mPreviewView);
            }

            @Override
            public void onVideoPreviewSurfaceDestroyed(AndroidVideoWindowImpl androidVideoWindow) {
                removePreviewWindow();
            }
        });
    }

    public static void onResume() {
        if (mRenderingView != null) {
            ((GLSurfaceView) mRenderingView).onResume();
        }

        if (mAndroidVideoWindow != null) {
            synchronized (mAndroidVideoWindow) {
                LinphoneManager.getLc().setVideoWindow(mAndroidVideoWindow);
            }
        }
    }

    public static void onPause() {
        if (mAndroidVideoWindow != null) {
            synchronized (mAndroidVideoWindow) {
                LinphoneManager.getLc().setVideoWindow(null);
            }
        }

        if (mRenderingView != null) {
            ((GLSurfaceView) mRenderingView).onPause();
        }
    }

    public static void onDestroy() {
        mPreviewView = null;
        mRenderingView = null;

        if (mAndroidVideoWindow != null) {
            mAndroidVideoWindow.release();
            mAndroidVideoWindow = null;
        }
    }

    private static void fixZOrder(SurfaceView rendering, SurfaceView preview) {
        rendering.setZOrderOnTop(false);
        preview.setZOrderOnTop(true);
        preview.setZOrderMediaOverlay(true); // Needed to be able to display control layout over
    }

    private static void setVideoWindow(Object o) {
        LinphoneManager.getLc().setVideoWindow(o);
    }

    private static void removeVideoWindow() {
        LinphoneCore linphoneCore = LinphoneManager.getLc();
        if (linphoneCore != null) {
            linphoneCore.setVideoWindow(null);
        }
    }

    private static void setPreviewWindow(Object o) {
        LinphoneManager.getLc().setPreviewWindow(o);
    }

    private static void removePreviewWindow() {
        LinphoneManager.getLc().setPreviewWindow(null);
    }

    public static LinphoneCore getLC() {
        return LinphoneManager.getLc();
    }
}
