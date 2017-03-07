package jucaipen.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter adapter;
    private static final String TAG = "MainActivity";
    private List<BluetoothDevice>  devices=new ArrayList<>();
    private TextView blueBlack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        blueBlack= (TextView) findViewById(R.id.blueBlack);
        //监测手机是否支持蓝牙功能   清单文件设置 uses-feature 就不需要判断
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "手机不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }

        //1 根据 BluetoothManager 获取
        BluetoothManager bm= (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        adapter=bm.getAdapter();
        //获取默认的手机蓝牙
        adapter=BluetoothAdapter.getDefaultAdapter();
        if(adapter==null){
            Toast.makeText(this, "没有发现蓝牙模块", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(!adapter.isEnabled()){
            //直接开启蓝牙
             adapter.enable();
/*
            // Intent 隐式意图开启蓝牙   有提示
            Intent blue=new Intent();
            blue.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blue,100);*/
        }
        scanLeDevice(true);

    }


    /**
     * @param enable
     *       扫描设备
     */
    private void scanLeDevice(boolean enable) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2){
              if(enable){
                  devices.clear();
                  new Handler().postDelayed(new Runnable() {
                      @Override
                      public void run() {
                          adapter.stopLeScan(mLeScanCallback);
                      }
                  },1000*60*2);
                  adapter.startLeScan(mLeScanCallback);
              }else {
                  Toast.makeText(this, "设备不可用", Toast.LENGTH_SHORT).show();
              }
        }

    }

    private  BluetoothAdapter.LeScanCallback mLeScanCallback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (device != null) {
                            if (!TextUtils.isEmpty(device.getName())) {
                                if (!devices.contains(device)) {
                                    devices.add(device);
                                    String name = device.getName();
                                    String address = device.getAddress();
                                    ParcelUuid[] uuids = device.getUuids();
                                    Log.i(TAG, "监测到蓝牙设备:" + name+" address="+address);
                                    blueBlack.append(name+"\n");

                                    /*if(name.equals("LIFE-82AE")){
                                        //连接指定的蓝牙设备
                                        device.createBond();
                                       // BluetoothGatt bluetoothGatt = device.connectGatt(MainActivity.this, false, GattCallback);


                                    }*/
                                }
                            }
                        }
                    }
                });
            }else {
                Toast.makeText(MainActivity.this, "蓝牙版本过低", Toast.LENGTH_SHORT).show();
            }
        }
    };


    public BluetoothGattCallback GattCallback=new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            //连接状态发生改变
            if(newState== BluetoothProfile.STATE_DISCONNECTED){
                //设备中断
                gatt.close();
            }else  if(newState==BluetoothProfile.STATE_CONNECTED){
                //设备连接成功

            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            //发现新设备

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //回调响应读操作
            if(status==BluetoothGatt.GATT_SUCCESS){
                //主动读取Characteristic  中的数据

            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            //回调响应写操作

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            //当Characteristic里面的数据发生改变的时候

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            //报告描述符读操作的结果


        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            //指示描述符写操作的结果

        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            //当可靠的写事务已完成时调用


        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            //报告远程设备连接的RSSI


        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            //指示给定设备连接的MTU已更改

        }
    };


}
