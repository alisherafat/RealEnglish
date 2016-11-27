package ir.realenglish.app.utils.gcm;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import ir.realenglish.app.app.Config;
import ir.realenglish.app.app.PrefKey;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.TinyDB;
import ir.realenglish.app.utils.Utils;

public class GCMIntentService extends IntentService {

    private static final String TAG = GCMIntentService.class.getSimpleName();

    public GCMIntentService() {
        super(TAG);
    }

    public static final String TOPIC = "topic";
    public static final String TOPIC_GLOBAL = "global";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";
    public static final String GET_TOKEN = "get_token";


    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()) {
            case SUBSCRIBE:
                subscribeToTopic();
                break;
            case UNSUBSCRIBE:
                unsubscribeFromTopic(intent.getStringExtra(TOPIC));
                break;
            case GET_TOKEN:
                getGCMToken();
                break;
        }

    }


    private void getGCMToken() {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(Config.GCM_SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            if (token != null && !token.isEmpty()) {
                TinyDB.putString(PrefKey.GCM_TOKEN, token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic() {
        String token = PrefKey.getGcmToken();
        if (token == null) {
            Utils.log("couldn't subscripte to topics since token is empty");
            return;
        }
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : UserService.getSubscribedTopics()) {
            try {
                pubSub.subscribe(token, "/topics/" + topic, null);
                Utils.log("Subscribed to topic: " + topic);
            } catch (Exception e) {
                Utils.log("failed to subscribe to " + topic);
                e.printStackTrace();
            }
        }
        try {
            pubSub.subscribe(token, "/topics/global", null);
            Utils.log("Subscribed to topic: global");
        } catch (Exception e) {
            Utils.log("failed to subscribe to global");
            e.printStackTrace();
        }
    }

    public void unsubscribeFromTopic(String topic) {
        try {
            GcmPubSub pubSub = GcmPubSub.getInstance(getApplicationContext());
            String token = PrefKey.getGcmToken();
            if (token != null) {
                Utils.log(token);
                pubSub.unsubscribe(token, "/topics/" + topic);
                Utils.log("Unsubscribed from topic: " + topic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
