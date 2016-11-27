package ir.realenglish.app.presenter;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import ir.realenglish.app.BuildConfig;
import ir.realenglish.app.app.PrefKey;
import ir.realenglish.app.utils.Utils;

/**
 * Created by ALI-PC on 5/25/2016.
 */
public class MainController {
    public static void checkWhatsNew(Context context) {
        if (!PrefKey.hasSeenWhatsNew(BuildConfig.VERSION_CODE)) {
            if (Utils.assetExists(context, "vc/version" + BuildConfig.VERSION_CODE + ".txt")) {
                String info = Utils.readFileFromAssets(context, "vc/version3.txt");
                if (info == null) return;
                new MaterialDialog.Builder(context)
                        .title("what's new in " + BuildConfig.VERSION_NAME)
                        .content(info)
                        .positiveText("ok").show();
            }
            PrefKey.setHasSeenWhatsNew(BuildConfig.VERSION_CODE);
            PrefKey.clearPreWhatsNew();
        }
    }
}
