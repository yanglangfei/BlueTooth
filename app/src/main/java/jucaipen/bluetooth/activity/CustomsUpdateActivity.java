package jucaipen.bluetooth.activity;

import android.support.v4.app.Fragment;

import com.kcode.lib.dialog.UpdateActivity;

import jucaipen.bluetooth.fragment.CustomsUpdateFragment;

/**
 * Created by Administrator on 2017/3/13.
 */

public class CustomsUpdateActivity extends UpdateActivity {

    @Override
    protected Fragment getUpdateDialogFragment() {
        return CustomsUpdateFragment.newInstance(mModel);
    }
}
