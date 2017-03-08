package jucaipen.bluetooth.receiver;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017/3/8.
 *
 *     监听蓝牙搜索
 */

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(BluetoothDevice.ACTION_FOUND.equals(action)){
            //搜索到蓝牙设备
            // 从intent中获取设备
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);


        }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            //搜索完成

        }
    }
}
