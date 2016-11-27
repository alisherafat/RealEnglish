package ir.realenglish.app.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.FilePart;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ir.realenglish.app.app.Config;
import ir.realenglish.app.app.EndPoints;

//@Table(name = "Posts")
public class Post implements Serializable {

    @Expose
    private List<Item> items;

    private List<Comment> comments;

    @Expose(serialize = false)
    //  @Column(name = "remote_id", unique = true, index = true,
    //          onUniqueConflict = Column.ConflictAction.REPLACE)
    @SerializedName("id")
    public int remoteId = -1;

    @Expose
    // @Column(name = "Title")
    public String title;

    @Expose
    //  @Column(name = "Description")
    public String description;

    @Expose(serialize = false)
    //  @Column(name = "Score")
    public int score;

    @Expose(serialize = false)
    public User user = new User();

    //  @Column(name = "timestamp", index = true)
    //private Date timestamp;

    @Expose
    public String timestamp;


    @Expose
    public List<Tag> tags = new ArrayList<>();

    //@Expose
    // public List<Item> items = new ArrayList<>();
    public int getImageCount() {
        int count = 0;
        for (Item item : this.items) {
            if (item.type == 2) {
                count++;
            }
        }
        return count;
    }

    public int getAudioCount() {
        int count = 0;
        for (Item item : this.items) {
            if (item.type == 3) {
                count++;
            }
        }
        return count;
    }

    public List<Part> getUploadItems() {
        List<Part> files = new ArrayList<>();
        int i = 0, j = 0;
        for (Item item : this.items) {
            if (item.type == 2 && item.localPath != null) {
                i++;
                files.add(new FilePart(item.localName, new File(item.localPath)));
            } else if (item.type == 3 && item.localPath != null) {
                j++;
                files.add(new FilePart(item.localName, new File(item.localPath)));
            }
        }
        return files;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        if (this.items == null) return new ArrayList<>();
        return this.items;
    }

    public List<String> getReadyFiles() {
        List<String> readyFiles = new ArrayList<>();
        if (this.items == null) return readyFiles;
        if (this.remoteId < 1) return readyFiles;


        File file = new File(Config.DIR_POST + "/" + this.remoteId);

        if (!file.exists()) return readyFiles;

        for (File item : file.listFiles()) {
            if (item.isFile()) {
                readyFiles.add(item.getName());
            }
        }
        return readyFiles;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String object = gson.toJson(this);
        return object;
    }

    public static Post fromJson(JsonObject object) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(object, Post.class);
    }

    public void getComments(Context context, int page, final OnCommentsReceiveListener listener) {
        Ion.with(context).load("GET", EndPoints.POST_COMMENTS.replace("_R_", "" + this.remoteId)).setBodyParameter("page", "" + page)
                .as(new TypeToken<List<Comment>>() {
                }).setCallback(new FutureCallback<List<Comment>>() {
            @Override
            public void onCompleted(Exception e, List<Comment> result) {
                if (e == null && result != null) {
                    listener.onReceive(result);
                }
            }
        });
    }


    public interface OnCommentsReceiveListener {
        void onReceive(List<Comment> newComments);
    }

    /*
    public List<Tag> tags() {
        List<Tag> tags = new Select()
                .from(Tag.class)
                .innerJoin(IdiomTag.class).on("Tags.id = Idiom_Tag.id")
                .where("Idiom_Tag.Idiom = ?", this.getId())
                .execute();
        return tags;
    }
    public static List<Idiom> getAllRemoteIds() {
        return new Select(new String[]{"Id,remote_id"}).from(Idiom.class).execute();
    }

*/

    public static class Item implements Serializable {

        @Expose
        @SerializedName("id")
        public int remoteId = -1;

        @Expose(serialize = false, deserialize = true)
        @SerializedName("post_id")
        public int postId = -1;

        @Expose(serialize = false, deserialize = true)
        public int size;

        @Expose public int type, sort;

        @Expose public String body;


        public String localPath;

        @Expose(deserialize = false) public String localName;


    }

}
