package ir.realenglish.app.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;

import java.io.File;

/**
 * Created by ALI-PC on 5/25/2016.
 */
public class AppService {
    public static void shareApplication(Context context) {
        ApplicationInfo app = context.getApplicationInfo();
        String filePath = app.sourceDir;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        context.startActivity(Intent.createChooser(intent, "Share app via"));
    }

    public static void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);
            fileOrDirectory.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateGcmToken() {
        //get token from pref and send to server and update pref if success

    }


}
