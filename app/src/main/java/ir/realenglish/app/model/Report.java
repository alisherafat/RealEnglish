package ir.realenglish.app.model;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.presenter.UserService;

/**
 * Created by ALI on 7/26/2016.
 */
public class Report {

    public static void showDialog(final Context context, final View viewClicked, final String type, final int remoteId) {
        if (!UserService.grantWithSnackBar(context, viewClicked)) return;

        String[] items = new String[]{"Obscene content", "Improper religious content"
                , "Hateful or abusive content", "Other objection"};
        new MaterialDialog.Builder(context)
                .content("The reason you find this content objectionable:")
                .items(items)
                .positiveText("submit")
                .negativeText("cancel")
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == -1)
                            return false;
                        Ion.with(context).load("POST", EndPoints.REPORT_SEND)
                                .setBodyParameter("model", type)
                                .setBodyParameter("id", String.valueOf(remoteId))
                                .setBodyParameter("type", String.valueOf((which + 1)))
                                .setBodyParameter("api_token", UserService.getApiToken())
                                .asString().setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                            }
                        });
                        Snackbar.make(viewClicked, " Sending report... ", Snackbar.LENGTH_SHORT).show();
                        return true;
                    }
                })
                .show();
    }
}
