package ir.realenglish.app.network;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import ir.realenglish.app.R;
import ir.realenglish.app.app.Config;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.event.MyEvent;
import ir.realenglish.app.model.Post;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.Utils;

/**
 * Created by ALI on 7/12/2016.
 */
public class MyNetworkService extends IntentService {
    public static final String ACTION = "my_network_service_action";
    public static final int SEND_POST = 1;
    public static final int UPLOAD_IMAGE = 2;
    public static final int DOWNLOAD_LESSON_FILES = 3;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    private final int SEND_POST_ID = 165;
    private final int DOWNLOAD_LESSON_FILES_ID = 196;

    public MyNetworkService() {
        super("myapp");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
    }

    @Override
    protected void onHandleIntent(@NonNull Intent intent) {
        switch (intent.getIntExtra(ACTION, -1)) {
            case SEND_POST:
                sendPost(intent);
                break;
            case UPLOAD_IMAGE:
                uploadProfileImage(intent);
                break;
            case DOWNLOAD_LESSON_FILES:
                downloadLessonFiles(intent);
                break;

        }
    }

    private void sendPost(Intent intent) {
        boolean isEditMode = intent.getBooleanExtra("editMode", false);
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Sending post")
                .setContentText("Please wait...")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_action_upload)).build();

        Post post = (Post) intent.getSerializableExtra("post");
        Utils.log("sended:" + post.toJson());
        String method = "POST";
        String url = isEditMode ? EndPoints.POST_UPDATE.replace("_R_", String.valueOf(post.remoteId)) : EndPoints.POST_SEND;
        try {
            String result = Ion.with(this).load(method, url)
                    .uploadProgress(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded, long total) {
                            int progress = (int) (downloaded * 100 / total);
                            mBuilder.setProgress(100, progress, false);
                            mNotifyManager.notify(SEND_POST_ID, mBuilder.build());
                        }
                    })
                    .addMultipartParts(post.getUploadItems())
                    .setMultipartParameter("post", post.toJson())
                    .setMultipartParameter("api_token", UserService.getApiToken())
                    .asString()
                    .get();
            Utils.log("response: " + result);
            mBuilder.setContentTitle("Successfully sent").setContentText("It will be available real soon!");
            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(MyNetworkService.this.getResources(), R.mipmap.ic_action_tick));

        } catch (Exception e) {
            e.printStackTrace();
            mBuilder.setContentTitle("Oops!").setContentText(getString(R.string.went_wrong_while_sending)).setSmallIcon(R.mipmap.ic_action_emo_err);
        }

        mBuilder.setProgress(0, 0, false);
        mBuilder.setOngoing(false).setOnlyAlertOnce(false).setAutoCancel(true);
        mNotifyManager.notify(SEND_POST_ID, mBuilder.build());

    }

    private void uploadProfileImage(Intent intent) {
        try {
            Ion.with(this).load("POST", EndPoints.USER_IMAGES.replace("_R_", String.valueOf(UserService.getId(0))))
                    .setMultipartFile("body", new File(Config.PROFILE_IMAGE_PATH))
                    .setMultipartParameter("api_token", UserService.getApiToken())
                    .asString().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadLessonFiles(Intent intent) {
        String url = intent.getStringExtra("url");
        String fileName = intent.getStringExtra("file");
        mBuilder.setContentTitle("Downloading Lesson Files")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), android.R.drawable.stat_sys_download)).build();
        try {
            File file = Ion.with(this).load(url)
                    .progress(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded, long total) {
                            int progress = (int) (downloaded * 100 / total);
                            mBuilder.setProgress(100, progress, false)
                                    .setContentText("Progress: " + progress + "%");
                            mNotifyManager.notify(DOWNLOAD_LESSON_FILES_ID, mBuilder.build());
                        }
                    })
                    .write(new File(fileName))
                    .get();
            // Utils.unzip(file, file.getParentFile());
           // file.delete();
            mBuilder.setContentTitle("Successfully downloaded")
                    .setLargeIcon(BitmapFactory.decodeResource(MyNetworkService.this.getResources(), R.mipmap.ic_action_tick));
            EventBus.getDefault().post(new MyEvent.DownloadComplete());
        } catch (Exception e) {
            e.printStackTrace();
            mBuilder.setContentTitle("Oops! something went wrong...")
                    .setLargeIcon(BitmapFactory.decodeResource(MyNetworkService.this.getResources(), R.mipmap.ic_action_emo_err));
        }
        mBuilder.setProgress(0, 0, false)
                .setContentText("")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setOngoing(false)
                .setOnlyAlertOnce(false)
                .setAutoCancel(true);
        mNotifyManager.notify(DOWNLOAD_LESSON_FILES_ID, mBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mNotifyManager.cancelAll();
        super.onTaskRemoved(rootIntent);
    }
}
