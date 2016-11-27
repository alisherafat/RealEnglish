package ir.realenglish.app.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ALI on 8/29/2016.
 */
public class Test implements Serializable {
    @Expose
    @SerializedName("id")
    public int remoteId = -1;

    @Expose
    public String title;

    @Expose
    public String[] options;

    @Expose
    public int answer;

    public int userAnswer = -1;

    public boolean mustShowResult;
}
