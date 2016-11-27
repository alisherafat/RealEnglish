package ir.realenglish.app.app;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import ir.realenglish.app.BuildConfig;


public class PrefKey {

    static int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "RealEnglish";

    static SharedPreferences pref = MyApp.getInstance().getSharedPreferences(PREF_NAME, PRIVATE_MODE);

    static Editor editor = pref.edit();

    // USER KEYS
    public static final String USER_ID = "user_id";
    public static final String USERNAME = "username";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_TIMESTAMP = "user_timestamp";
    public static final String USER_POST_SCORE = "user_post_score";
    public static final String USER_COMMENT_SCORE = "user_comment_score";
    public static final String USER_POST_COUNT = "user_post_count";
    public static final String USER_COMMENT_COUNT = "user_comment_count";
    public static final String USER_SCORE = "user_score";

    private static final String SCORE = "score";
    private static final String PAYMENTS = "payments";

    // GCM KEYS
    public static final String GCM_TOKEN = "gcm_token";
    public static final String GCM_MUST_UPDATE_IN_SERVER = "gcm_must_update_in_server";
    private static final String PRIMARY_COLOR = "primary_color";
    private static final String HAS_SEEN_TOUR = "has_seen_tour";
    private static final String HAS_SEEN_WHATS_NEW = "version";

    public static void clearPreWhatsNew() {
        for (int i = 1; i <= 3; i++) {
            try {
                editor.remove(HAS_SEEN_WHATS_NEW + (BuildConfig.VERSION_CODE - i)).commit();
            } catch (Exception e) {}
        }
    }

    public static void setHasSeenWhatsNew(int versionCode) {
        editor.putBoolean(HAS_SEEN_WHATS_NEW + versionCode, true).commit();
    }

    public static boolean hasSeenWhatsNew(int versionCode) {
        return pref.getBoolean(HAS_SEEN_WHATS_NEW + versionCode, false);
    }

    public static void setSeenTour() {
        editor.putBoolean(HAS_SEEN_TOUR, true);
        editor.commit();
    }

    public static boolean hasSeenTour() {
        return pref.getBoolean(HAS_SEEN_TOUR, false);
    }

    public static String getGcmToken() {
        return pref.getString(GCM_TOKEN, null);
    }


    public static void setGcmToken(String token) {
        editor.putString(GCM_TOKEN, token);
        editor.commit();
    }


    public static String getUserId() {
        return pref.getString(USER_ID, null);
    }

    public static String getUsername() {
        return pref.getString(USERNAME, null);
    }


    public static String getScore() {
        return pref.getString(SCORE, null);
    }


    public static int getPrimaryColor() {
        return pref.getInt(PRIMARY_COLOR, -1);
    }

    public static void setPrimaryColor(int color) {
        editor.putInt(PRIMARY_COLOR, color);
        editor.commit();
    }


    public static void setCoursePayment(String course) {
        String payments = pref.getString(PAYMENTS, "") + course + ";";  // ; is a devider
        editor.putString(PAYMENTS, payments);
        editor.commit();
    }

    public static void setUsername(String username) {
        editor.putString(USERNAME, username);
        editor.commit();
    }

    public static void setScore(String score) {
        editor.putString(SCORE, score);
        editor.commit();
    }
}
