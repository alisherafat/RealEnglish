package ir.realenglish.app.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ir.realenglish.app.BuildConfig;
import ir.realenglish.app.app.MyApp;
import ir.realenglish.app.utils.NotificationHelper;
import ir.realenglish.app.utils.Utils;

/**
 * Created by ALI on 8/14/2016.
 */
@Table(name = "push_notifications")
public class PushNotification extends Model implements Serializable {

    @Column(name = "remote_id", unique = true, index = true,
            onUniqueConflict = Column.ConflictAction.REPLACE)
    public int remoteID;

    @Column(name = "type")
    public int type;

    @Column(name = "title")
    public String title;

    @Column(name = "body")
    public String body;

    @Column(name = "data")
    public String data;


    @Column(name = "timestamp", index = true)
    private Date timestamp;

    public void setTimestamp(String date) {
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sf.setLenient(true);
            this.timestamp = sf.parse(date);
        } catch (Exception e) {
            this.timestamp = new Date(System.currentTimeMillis());
            e.printStackTrace();
        }
    }


    public String getNiceTimestamp() {
        try {
            return new SimpleDateFormat("MMM dd HH:mm").format(this.timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static List<PushNotification> findRecent(Date newerThan) {
        return new Select().from(PushNotification.class).where("timestamp > ?", newerThan.getTime()).execute();
    }


    public void handle() {
        try {
            JSONObject object = new JSONObject(this.data);
            switch (this.type) {
                case 1:
                    // Basic notification
                    new NotificationHelper(MyApp.getInstance()).setTitle(this.title).setMessage(this.body).showNotification();
                    break;
                case 2: {
                    //open browser
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getString("url")));
                    new NotificationHelper(MyApp.getInstance()).setTitle(this.title).setMessage(this.body)
                            .setIntent(intent).showNotification();
                    break;
                }
                case 3:
                    // update app
                    if (BuildConfig.VERSION_CODE >= object.getInt("version_code")) {
                        return;
                    }
                    Intent intent;
                    if (Utils.isBazaarInstalled()) {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("bazaar://details?id=" + MyApp.getInstance().getPackageName()));
                        intent.setPackage("com.farsitel.bazaar");
                    } else {
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getString("url")));
                    }
                    new NotificationHelper(MyApp.getInstance()).setTitle(this.title).setMessage(this.body)
                            .setIntent(intent).showNotification();
                    break;
            }

            if (object.has("save") && object.getBoolean("save")) {
                this.save();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void handleClick(Context context) {
        try {
            JSONObject object = new JSONObject(this.data);
            switch (this.type) {
                case 2: {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(object.getString("url")));
                    context.startActivity(intent);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static PushNotification fromBundle(Bundle bundle) {
        PushNotification push = new PushNotification();
        try {
            push.remoteID = Integer.parseInt(bundle.getString("id"));
            push.type = Integer.parseInt(bundle.getString("type"));
            push.title = bundle.getString("title");
            push.body = bundle.getString("body");
            push.data = bundle.getString("data");
            push.setTimestamp(bundle.getString("timestamp"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return push;
    }

    public static void deleteByRemoteId(int remoteID) {
        new Delete().from(PushNotification.class).where("remote_id = ?", remoteID).execute();
    }

    @Override
    public String toString() {
        return "remote id: " + this.remoteID +
                " type: " + this.type +
                " title: " + this.title +
                " body: " + this.body +
                " data: " + this.data;
    }

}
