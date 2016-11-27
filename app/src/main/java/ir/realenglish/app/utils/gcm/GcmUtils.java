package ir.realenglish.app.utils.gcm;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

import ir.realenglish.app.app.MyApp;
import ir.realenglish.app.app.PrefKey;
import ir.realenglish.app.presenter.AppService;
import ir.realenglish.app.utils.TinyDB;

public class GcmUtils {

    public static void init(final Context context) {
        if (TinyDB.getString(PrefKey.GCM_TOKEN, "").isEmpty()) {
            getTokenFromGoogle(context);
        } else {
            subscribeToTopics(context);
            if (TinyDB.getBoolean(PrefKey.GCM_MUST_UPDATE_IN_SERVER, false)) {
                AppService.updateGcmToken();
            }
        }
    }

    public static void subscribeToTopics(Context context) {
        Intent intent = new Intent(context, GCMIntentService.class);
        intent.setAction(GCMIntentService.SUBSCRIBE);
        context.startService(intent);
    }

    private static void getTokenFromGoogle(final Context context) {
        Intent getTokenIntent = new Intent(context, GCMIntentService.class);
        getTokenIntent.setAction(GCMIntentService.GET_TOKEN);
        context.startService(getTokenIntent);
    }


    public static class MyInstanceIDListenerService extends InstanceIDListenerService {
        @Override
        public void onTokenRefresh() {
            TinyDB.putString(PrefKey.GCM_TOKEN, "");
            TinyDB.putBoolean(PrefKey.GCM_MUST_UPDATE_IN_SERVER, true);
            init(MyApp.getInstance());
        }
    }

    public static void subscribeToTopics(String[] topics) {
        Context context = MyApp.getInstance();
        for (String topic : topics) {
            Intent intent = new Intent(context, GCMIntentService.class);
            //   intent.putExtra(GCMIntentService.ACTION, GCMIntentService.SUBSCRIBE);
            intent.putExtra(GCMIntentService.TOPIC, "topic_" + topic);
            context.startService(intent);
        }
        Intent intent = new Intent(context, GCMIntentService.class);
        // intent.putExtra(GCMIntentService.ACTION, GCMIntentService.SUBSCRIBE);
        intent.putExtra(GCMIntentService.TOPIC, GCMIntentService.TOPIC_GLOBAL);
        context.startService(intent);
    }
}
