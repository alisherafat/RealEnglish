package ir.realenglish.app.app;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {

    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR_APP = DIR_SDCARD + "/RealEnglish";
    public static final String DIR_AUDIO = DIR_APP + "/audio";
    public static final String DIR_IMAGE = DIR_APP + "/image";
    public static final String DIR_POST = DIR_APP + "/post";
    public static final String DIR_LESSON = DIR_APP + "/lesson";

    public static final String DIR_RECORD = DIR_AUDIO + "/record";

    public static List<File> getDirectories() {
        List<File> directories = new ArrayList<>();
        directories.add(new File(DIR_APP));
        directories.add(new File(DIR_AUDIO));
        directories.add(new File(DIR_IMAGE));
        directories.add(new File(DIR_POST));
        directories.add(new File(DIR_LESSON));

        directories.add(new File(DIR_RECORD));


        return directories;
    }

    public static final String GCM_SENDER_ID = "404683069020";
    public static final String RSA_KEY = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC9ahXBHpOzmA11O4H+zUkJ+3feUNW0oLZ2joZpSoAziLqxC1KB6L+BWK30vG7aBlAfyPPylFW6XzeYLcyc2iK1zOVSbzPMWMsL6hf7LCxCxgsgs+oWGjLZaLMLIpAwRRNQP1xaw4yf88jHt+5K9s3/lza35pPRZ01XMTRdxa+ACSiLe/F9tXqb59L2h0+fdYgk/0CNbLizyk7mpeUGHn9v0el1daujARrCqkBMogMCAwEAAQ==";
    public static final String WEBSERVICE_URL = "http://192.168.56.1:8080/api/v1/";//http://realenglish.ir/api/v1/
    public static final String PROFILE_IMAGE_URL = "http://realenglish.ir/upload/android/uimage/";
    public static final String DOWNLOAD_ANDROID_URL = "http://192.168.56.1:8080/dl/android/content/";//http://realenglish.ir/dl/android/content/
    public static final int NOTIFICATION_APP_ID = 100;
    public static final int NOTIFICATION_PUSH_USER_ID = 101;
    public static final String SKU_COURSE_A = "pro_course_a";
    public static final String SKU_COURSE_B = "pro_course_b";
    public static final String PROFILE_IMAGE_PATH = DIR_IMAGE + "/profile.png";

    public static final int PUSH_TYPE_GENERAL = 1;
    public static final int PUSH_TYPE_CHATROOM = 2;
    public static final int PUSH_TYPE_USER = 3;

    public static final int QUIZ_SCORE = 10;

    public static final String SUPORT_USERNAME = "support";
    public static final String GCM_TOKEN_RECEIVED = "gcm_token_received";
    public static final String GCM_SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String GCM_CHATROOM_GENERAL = "General";
    public static final String GCM_CHAT_MESSAGE_RECEIVED = "gcmChatMessage";

    public static final String GCM_TOPIC_GENERALCHAT = "gchat";


}
