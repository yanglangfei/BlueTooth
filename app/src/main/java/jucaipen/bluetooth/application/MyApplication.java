package jucaipen.bluetooth.application;

import android.app.Application;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Administrator on 2017/3/13.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ShortcutBadger.applyCount(this,10);
    }
}
