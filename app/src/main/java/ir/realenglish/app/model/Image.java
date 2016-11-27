package ir.realenglish.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

//@Table(name = "Images")
public class Image implements Serializable {
    @Expose
    @SerializedName("id")
    public int remoteId;

    @Expose
    //@Column(name = "remotePath")
    public String name;

    @Expose
    @SerializedName("created_at")
    public String timestamp;

 //   @Column(name = "imageable_type")
    public String imageableType;

 //   @Column(name = "imageable_id")
    public long imageable_id;

}

