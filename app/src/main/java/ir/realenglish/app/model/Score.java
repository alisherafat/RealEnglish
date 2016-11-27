package ir.realenglish.app.model;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;

import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.presenter.UserService;

@Table(name = "scores")
public class Score extends Model {

    @Column(name = "scoreable_type", uniqueGroups = {"group1"},
            onUniqueConflicts = {Column.ConflictAction.REPLACE})
    public String scoreableType;

    @Column(name = "scoreable_id", uniqueGroups = {"group1"},
            onUniqueConflicts = {Column.ConflictAction.REPLACE})
    public int scoreableId;

    public static List<Score> getAll(String scoreableType) {
        return new Select()
                .from(Score.class)
                .where("scoreable_type = ?", scoreableType)
                .execute();
    }

    public static Score create(String type, int id) {
        Score score = new Score();
        score.scoreableType = type;
        score.scoreableId = id;
        score.save();
        return score;
    }

    public static void delete(String type, int id) {
        new Delete().from(Score.class)
                .where("scoreable_type = ?", type)
                .where("scoreable_id = ?", id)
                .execute();
    }

    public static boolean exists(String type, int id) {
        Score score = new Select()
                .from(Score.class)
                .where("scoreable_type = ?", type)
                .where("scoreable_id = ?", id)
                .executeSingle();
        if (score == null) {
            return false;
        }
        return true;
    }

    public static Score changeLike(Context context, View view, String type, int id, boolean like) {
        String score = like ? "5" : "-5";
        Ion.with(context).load("POST", EndPoints.SCORE_BASE)
                .setBodyParameter("type", type)
                .setBodyParameter("id", String.valueOf(id))
                .setBodyParameter("score", score)
                .setBodyParameter("api_token", UserService.getApiToken())
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
            }
        });

        if (like) {
            Snackbar.make(view, "Thanks for sending feedback", Snackbar.LENGTH_SHORT).show();
            return create(type, id);
        } else {
            delete(type, id);
            return null;
        }
    }
}
