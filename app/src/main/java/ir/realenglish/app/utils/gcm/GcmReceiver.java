package ir.realenglish.app.utils.gcm;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import ir.realenglish.app.model.PushNotification;
import ir.realenglish.app.utils.Utils;

public class GcmReceiver extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        Utils.log(bundle.toString());
        PushNotification.fromBundle(bundle).handle();
    }

}
