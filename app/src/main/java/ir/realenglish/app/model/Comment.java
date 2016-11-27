package ir.realenglish.app.model;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.gson.annotations.SerializedName;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.Serializable;

import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.Utils;

/**
 * Created by ALI on 7/18/2016.
 */
public class Comment implements Serializable {
    @SerializedName("id")
    public int remoteId;
    public int score;
    public String body, timestamp;
    public User user;
    @SerializedName("is_updated")
    public boolean isUpdated;

    public String getPrettyTimeStamp() {
        if (this.timestamp == null || this.timestamp.isEmpty()) {
            return "";
        }
        try {
            return Utils.getPrettyDate(this.timestamp, false);
        } catch (Exception e) {
            return "";
        }
    }


    public static void send(Context context, View view, String type, int id, String body) {
        if (!UserService.grantWithSnackBar(context,view)) return;
        Ion.with(context).load("POST", EndPoints.COMMENT_SEND)
                .setBodyParameter("type", type)
                .setBodyParameter("api_token", UserService.getApiToken())
                .setBodyParameter("id", "" + id)
                .setBodyParameter("body", body)
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {

            }
        });
        Snackbar.make(view, "Thanks for sending feedback", Snackbar.LENGTH_SHORT).show();

    }

    public static void remove(Context context, int remoteId) {
        Ion.with(context).load("DELETE", EndPoints.COMMENT_UPDATE_AND_DELETE.replace("_R_", String.valueOf(remoteId)))
                .setBodyParameter("api_token", UserService.getApiToken())
                .asJsonObject();
    }

    public static void update(Context context, int remoteId, String body) {
        Ion.with(context).load("PATCH", EndPoints.COMMENT_UPDATE_AND_DELETE.replace("_R_", String.valueOf(remoteId)))
                .setBodyParameter("body", body)
                .setBodyParameter("api_token", UserService.getApiToken())
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {

            }
        });
    }
}
