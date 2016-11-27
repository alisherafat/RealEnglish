package ir.realenglish.app.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.prashantsolanki.secureprefmanager.SecurePrefManager;

import org.json.JSONObject;

import java.util.ArrayList;

import ir.realenglish.app.app.MyApp;
import ir.realenglish.app.app.PrefKey;
import ir.realenglish.app.utils.TinyDB;
import ir.realenglish.app.view.AccountActivity;

/**
 * Created by ALI-PC on 5/25/2016.
 */
public class UserService {
    public static final String SUBSCRIBED_TOPICS = "subscribed_topics";

    public static ArrayList<String> getSubscribedTopics() {
        return TinyDB.getListString(SUBSCRIBED_TOPICS);
    }

    public static void putSubscribedTopics(ArrayList<String> topics) {
        TinyDB.putListString(SUBSCRIBED_TOPICS, topics);
    }

    public static void putId(int id) {
        TinyDB.putInt(PrefKey.USER_ID, id);
    }

    public static int getId(int defaultValue) {
        return TinyDB.getInt(PrefKey.USER_ID, defaultValue);
    }

    public static void putUsername(String username) {
        TinyDB.putString(PrefKey.USERNAME, username);
    }

    public static String getUsername(String defaultValue) {
        return TinyDB.getString(PrefKey.USERNAME, defaultValue);
    }

    public static void putEmail(String email) {
        TinyDB.putString(PrefKey.USER_EMAIL, email);
    }

    public static String getEmail(String defaultValue) {
        return TinyDB.getString(PrefKey.USER_EMAIL, defaultValue);
    }

    public static void putTimestamp(String time) {
        TinyDB.putString(PrefKey.USER_TIMESTAMP, time);
    }

    public static String getTimestamp(String defaultValue) {
        return TinyDB.getString(PrefKey.USER_TIMESTAMP, defaultValue);
    }

    public static void putScore(int score) {
        TinyDB.putInt(PrefKey.USER_SCORE, score);
    }

    public static int getScore(int defValue) {
        return TinyDB.getInt(PrefKey.USER_SCORE, defValue);
    }

    public static void putPostsScore(int score) {
        TinyDB.putInt(PrefKey.USER_POST_SCORE, score);
    }

    public static int getPostsScore(int defValue) {
        return TinyDB.getInt(PrefKey.USER_POST_SCORE, defValue);
    }

    public static void putCommentsScore(int score) {
        TinyDB.putInt(PrefKey.USER_COMMENT_SCORE, score);
    }

    public static int getCommentsScore(int defValue) {
        return TinyDB.getInt(PrefKey.USER_COMMENT_SCORE, defValue);
    }

    public static void putCommentsCount(int count) {
        TinyDB.putInt(PrefKey.USER_COMMENT_COUNT, count);
    }

    public static int getCommentsCount(int defValue) {
        return TinyDB.getInt(PrefKey.USER_COMMENT_COUNT, defValue);
    }

    public static void putPostsCount(int count) {
        TinyDB.putInt(PrefKey.USER_POST_COUNT, count);
    }

    public static int getPostsCount(int defValue) {
        return TinyDB.getInt(PrefKey.USER_POST_COUNT, defValue);
    }

    public static boolean grant() {
        return getId(0) != 0;
    }


    public static boolean login(JSONObject object) {
        try {
            putApiToken(object.getString("token"));
            putId(object.getInt("id"));
            putUsername(object.getString("name"));
            putEmail(object.getString("email"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void putApiToken(String token) {
        SecurePrefManager.with(MyApp.getInstance()).set("api_token").value(token).go();
    }

    public static String getApiToken() {
        return SecurePrefManager.with(MyApp.getInstance())
                .get("api_token")
                .defaultValue("")
                .go();
    }

    public static boolean grantWithSnackBar(final Context context, View view) {
        if (grant()) {
            return true;
        }
        Snackbar.make(view, "You should create an account", Snackbar.LENGTH_LONG)
                .setAction("ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, AccountActivity.class));
                    }
                })
                .show();
        return false;
    }

    public static boolean grantSuper() {
        return getId(0) == 1;
    }
}
