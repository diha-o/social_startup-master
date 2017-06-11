package pc.dd.sex_startup.Main;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import pc.dd.sex_startup.Main.Helpers.FontsOverride;

/**
 * Created by UserData on 09.07.2016.
 */

public class sexApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Roboto.ttf");
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Roboto-Medium.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Roboto.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Roboto-Thin.ttf");


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }
}
