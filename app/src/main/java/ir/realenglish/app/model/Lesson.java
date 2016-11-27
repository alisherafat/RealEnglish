package ir.realenglish.app.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

public class Lesson implements Serializable {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd");
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Expose
    public List<Comment> comments;

    @Expose
    public List<Word> words;

    @Expose
    public List<Test> tests;

    @Expose(serialize = false)
    @SerializedName("id")
    public int remoteId = -1;

    @Expose
    public int number;

    @Expose
    public String name;

    @Expose
    public String level;

    @Expose
    public String transcript;

    @Expose(serialize = false)
    public int score;

    @Expose
    public Data data;

    @Expose
    public String timestamp;


    @Expose
    public List<Tag> tags;


    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    public String getNamePlaceHolder() {
        return this.name.substring(0, 1).toUpperCase();
    }

    public String getTimestamp() {
        try {
            return simpleDateFormat.format(sf.parse(this.timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static class Data implements Serializable {
        @Expose
        public String author;
        @Expose
        public List<File> files;
    }

    public static class File implements Serializable {
        @Expose
        public String name;
        @Expose
        public String path;
    }


}
