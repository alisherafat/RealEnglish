package ir.realenglish.app.app;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.prashantsolanki.secureprefmanager.SecurePrefManagerInit;

import ir.realenglish.app.utils.gcm.GcmUtils;

public class MyApp extends Application {


    private static MyApp mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initialize();
    }


    public static synchronized MyApp getInstance() {
        return mInstance;
    }

    private void initialize() {

        /*
        for (File file : Config.getDirectories()) {
            file.mkdirs();
        }
        */

        ActiveAndroid.initialize(this);

      //  Iconics.registerFont(new GoogleMaterial());

        GcmUtils.init(this);
        new SecurePrefManagerInit.Initializer(this)
                .useEncryption(true)
                .initialize();

    }


}