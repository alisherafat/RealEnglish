package ir.realenglish.app.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import ir.realenglish.app.R;
import ir.realenglish.app.view.AccountActivity;

public class DialogHelper {



    public static void showSimpleDialog(Context context, String content) {
        new MaterialDialog.Builder(context)
                .content(content)
                .positiveText(R.string.ok)
                .show();
    }

    public static void showConnectionFailed(Context context) {
        new MaterialDialog.Builder(context)
                .iconRes(R.mipmap.ic_wifi_error)
                .limitIconToDefaultSize()
                .title("Connection Failed!")
                .content(" Check your internet connection and try again... ")
                .positiveText("ok")
                .show();
    }




    public static MaterialDialog showIndeterminateProgressDialog(Context context, String title, boolean isHorizontal) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(title)
                .content("Please wait...")
                .progress(true, 0)
                .progressIndeterminateStyle(isHorizontal).build();
        return dialog;
    }

    public static void score(Context context, String title) {
        new MaterialDialog.Builder(context)
                .title(title)
                .iconRes(R.mipmap.score)
                .limitIconToDefaultSize()
                .positiveText("ok").show();
    }

    public static void showPremium(final Context context) {
        new MaterialDialog.Builder(context)
                .title("Don't wait any longer! ")
                .content("If you want to get full access to the course, please upgrade to VIP member account")
                .positiveText("  OK  ")
                .negativeText("NO, THANKS")
                .iconRes(R.mipmap.ic_launcher)
                .limitIconToDefaultSize()
                .positiveColorRes(R.color.material_red_400)
                .negativeColorRes(R.color.material_red_400)
                .titleColorRes(R.color.material_red_400)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.material_teal_a400)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(
                            @NonNull MaterialDialog materialDialog,
                            @NonNull DialogAction dialogAction) {
                        context.startActivity(new Intent(context, AccountActivity.class));
                    }
                })
                .show();
    }

    public static void like(Context context, int likeCount) {
        new MaterialDialog.Builder(context)
                .iconRes(R.mipmap.ic_like)
                .limitIconToDefaultSize()
                .title("LIKE  + " + likeCount)
                .content("Thanks for taking time to give us your feedback.")
                .positiveText("ok")
                .show();
    }

    public static void showYouHaveAll(Context context) {
        new MaterialDialog.Builder(context)
                .title("You've got all lessons!")
                .content("You'll get a notification as soon as the new lesson comes out!")
                .positiveText("ok")
                .show();
    }


    public static void showServerIsDown(Context context) {
        new MaterialDialog.Builder(context)
                .iconRes(R.mipmap.ic_error)
                .limitIconToDefaultSize()
                .title("Oops!")
                .content("Server is temporarily down!\n Try later please...")
                .positiveText("ok")
                .show();

    }

}
