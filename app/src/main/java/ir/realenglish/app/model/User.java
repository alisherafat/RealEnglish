package ir.realenglish.app.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

import ir.realenglish.app.utils.Utils;

/**
 * Created by ALI on 7/18/2016.
 */
public class User implements Serializable {
    @Expose
    public int id, score;
    @Expose
    public String name, email, timestamp,thumbnail;

    @Expose
    public List<Image> images;

    public boolean hasThumbnail() {
        if (thumbnail !=null && thumbnail.trim().length() > 1) {
            return true;
        }
        return false;
    }

    public String getTextDrawable() {
        if (this.name == null) throw new NullPointerException();
        return this.name.substring(0, 1).toUpperCase();
    }


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


}
