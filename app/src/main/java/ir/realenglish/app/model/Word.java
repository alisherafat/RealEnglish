package ir.realenglish.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ALI on 8/23/2016.
 */
public class Word implements Serializable {
    @Expose
    @SerializedName("id")
    public int remoteId = -1;

    @Expose
    @SerializedName("lesson_id")
    public int lessonId = -1;

    @Expose
    public String title;

    @Expose
    public String body;

    @Expose
    public String type;

    @Expose
    public String link = null;

    @Expose
    public String timestamp;


    public boolean hasLink() {
        if (link != null && link.length() > 1) {
            return true;
        }
        return false;
    }
}
