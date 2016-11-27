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

import java.io.Serializable;
import java.util.List;

import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.presenter.UserService;

/**
 * Created by ALI on 7/26/2016.
 */
@Table(name = "favorites")
public class Favorite extends Model implements Serializable {

    @Column(name = "favoriteable_type", uniqueGroups = {"group1"},
            onUniqueConflicts = {Column.ConflictAction.REPLACE})
    public String favoritableType;

    @Column(name = "favoriteable_id", uniqueGroups = {"group1"},
            onUniqueConflicts = {Column.ConflictAction.REPLACE})
    public int favoriteableId;

    public static Favorite craate(String type, int favoriteableId) {
        Favorite favorite = new Favorite();
        favorite.favoritableType = type;
        favorite.favoriteableId = favoriteableId;
        favorite.save();
        return favorite;
    }

    public static void delete(String type, int favoriteableId) {
        new Delete().from(Favorite.class)
                .where("favoriteable_type = ?", type)
                .where("favoriteable_id = ?", favoriteableId)
                .execute();
    }

    public static List<Favorite> getAll(String favoriteableType) {
        return new Select()
                .from(Favorite.class)
                .where("favoriteable_type = ?", favoriteableType)
                .execute();
    }

    public static boolean exists(String type, int id) {
        Favorite favorite = new Select()
                .from(Favorite.class)
                .where("favoriteable_type = ?", type)
                .where("favoriteable_id = ?", id)
                .executeSingle();
        if (favorite == null) {
            return false;
        }
        return true;
    }

    public static int getAllCount() {
        return new Select("id").from(Favorite.class).count();
    }

    public static int getAllCount(String favoritableType) {
        return new Select("id").from(Favorite.class).where("favoriteable_type = ?", favoritableType).count();
    }

    public static Favorite changeFavoriteList(Context context, View view, String type, int remoteId, boolean add) {

        String method = add ? "POST" : "DELETE";
        Ion.with(context).load(method, EndPoints.FAVORITE_SEND)
                .setBodyParameter("type", type)
                .setBodyParameter("id", String.valueOf(remoteId))
                .setBodyParameter("api_token", UserService.getApiToken())
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {

            }
        });
        if (add) {
            Snackbar.make(view, "Adding to favorite list", Snackbar.LENGTH_SHORT).show();
            return craate(type, remoteId);
        }
        Snackbar.make(view, "Removing form favorite list", Snackbar.LENGTH_SHORT).show();
        delete(type, remoteId);
        return null;
    }


}
