package com.ajouuniv.linphone.service.callback;

import org.linphone.core.LinphoneCall;

public abstract class PhoneCallback {

    public void incomingCall(LinphoneCall linphoneCall) {}

    public void outgoingInit() {}

    public void callConnected() {}

    public void callEnd() {}

    public void callReleased() {}

    public void error() {}
}
