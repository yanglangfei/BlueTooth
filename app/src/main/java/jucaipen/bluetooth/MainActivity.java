package jucaipen.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter adapter;
    private static final String TAG = "MainActivity";
    private List<BluetoothDevice>  devices=new ArrayList<>();
    private TextView blueBlack;
    // 00001101-0000-1000-8000-00805F9B34FB
    private  final  UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // 字符串符合UUID的格式即可
    public final String NAME = "Bluetooth_Socket";
    private  Handler mHandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if(what==100){
                String message= (String) msg.obj;
                Toast.makeText(MainActivity.this, "接收到消息："+message, Toast.LENGTH_SHORT).show();
            }

        }
    };

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
             adapter.disable();
/*
            // Intent 隐式意图开启蓝牙   有提示
            Intent blue=new Intent();
            blue.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blue,100);*/
        }
        adapter.enable();

        //设置蓝牙可被搜索到  必须在打开后执行
        Intent discoverableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //可自定义可见时间(s)，最大长度为3600。最小为0，表示始终可见。默认120，超过3600会被设为120
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
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

                                    if(name.equals("LIFE-82AE")){
                                        //连接指定的蓝牙设备
                                        new ListenerReadMessage(adapter,NAME,MY_UUID,mHandle).start();
                                    try {
                                        //UUID  实际上是一个格式8-4-4-4-12的字符串
                                        //UUID相当于Socket的端口，蓝牙地址相当于Socket的IP
                                        BluetoothSocket toServiceRecord = device.createRfcommSocketToServiceRecord(MY_UUID);
                                        toServiceRecord.connect();
                                        Log.i(TAG,"client：连接成功====");
                                        OutputStream outputStream = toServiceRecord.getOutputStream();
                                        DataOutputStream dos=new DataOutputStream(outputStream);
                                        dos.writeUTF("连接成功");
                                        dos.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    }
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


      class  ListenerReadMessage extends  Thread {

          private Handler mHandle;
          private BluetoothServerSocket bluetoothServerSocket;
          private BluetoothSocket socket;

          public ListenerReadMessage(BluetoothAdapter adapter, String name, UUID my_uuid, Handler mHandle) {
              try {
                  bluetoothServerSocket = adapter.listenUsingRfcommWithServiceRecord(name, my_uuid);
                  this.mHandle = mHandle;
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }

          @Override
          public void run() {
              //开启service 监听接收消息
              if (bluetoothServerSocket != null) {
                  try {
                      socket = bluetoothServerSocket.accept();
                      Log.i(TAG,"server:连接成功.....");

                      InputStream inputStream = socket.getInputStream();
                      OutputStream outputStream = socket.getOutputStream();
                      DataOutputStream dos = new DataOutputStream(outputStream);
                      DataInputStream dis = new DataInputStream(inputStream);

                      //无限获取消息
                      while (true) {
                          String msg = dis.readUTF();
                          if (mHandle != null && !TextUtils.isEmpty(msg)) {
                              mHandle.obtainMessage(100, msg).sendToTarget();
                          }
                      }

                  } catch (IOException e) {
                      e.printStackTrace();
                  }

              }
          }
      }

}
