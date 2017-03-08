package jucaipen.bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jucaipen.bluetooth.R;
import jucaipen.bluetooth.adapter.BlueAdapter;
import jucaipen.bluetooth.receiver.BluetoothReceiver;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {
    private BluetoothAdapter adapter;
    private static final String TAG = "ServerActivity";
    private List<BluetoothDevice> devices = new ArrayList<>();
    private ListView blueBlack;
    // 00001101-0000-1000-8000-00805F9B34FB
    private final UUID MY_UUID = UUID.fromString("db764ac8-4b08-7f25-aafe-59d03c27bae3");
    // 字符串符合UUID的格式即可
    public final String NAME = "Bluetooth_Socket";
    private EditText etMsg;
    private BlueAdapter blueAdapter;
    private  boolean isStop=false;

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 200) {
                //连接成功
                Toast.makeText(ServerActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                btnMsg.setEnabled(true);
                receiverMsg();
            }else if(what==100){
                //接受到消息
                String message= (String) msg.obj;
                Toast.makeText(ServerActivity.this, "接受到消息："+message, Toast.LENGTH_SHORT).show();
            }

        }
    };
    private OutputStream outputStream;
    private Button btnMsg;
    private DataOutputStream dos;
    private BluetoothServerSocket serverSocket;
    private DataInputStream dis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        initView();
        initData();
    }


    /**
     *   接受消息线程
     */
    public void receiverMsg(){
        new Thread(){
            @Override
            public void run() {
                while (!isStop){
                    try {
                        String msg = dis.readUTF();
                        mHandle.obtainMessage(100,msg).sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }


    private void initData() {
        //监测手机是否支持蓝牙功能   清单文件设置 uses-feature 就不需要判断
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "手机不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }

        //1 根据 BluetoothManager 获取
        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        adapter = bm.getAdapter();
        //获取默认的手机蓝牙
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Toast.makeText(this, "没有发现蓝牙模块", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!adapter.isEnabled()) {
            //直接开启蓝牙
            adapter.disable();
/*
            // Intent 隐式意图开启蓝牙   有提示
            Intent blue=new Intent();
            blue.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(blue,100);*/
        }
        adapter.enable();
        initServer();


        //获取已经配对的蓝牙设备
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();


        //设置蓝牙可被搜索到  必须在打开后执行
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //可自定义可见时间(s)，最大长度为3600。最小为0，表示始终可见。默认120，超过3600会被设为120
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);


        /**
         * 异步搜索蓝牙设备——广播接收
         */
        // 找到设备的广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        BluetoothReceiver receiver = new BluetoothReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //搜索到蓝牙设备
                // 从intent中获取设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && !devices.contains(device)) {
                    devices.add(device);
                }
                blueAdapter.notifyDataSetChanged();
            }
        };
        // 注册广播
        registerReceiver(receiver, filter);
        // 搜索完成的广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 注册广播
        registerReceiver(receiver, filter);
        scanLeDevice(true);
    }

    private void initView() {
        blueBlack = (ListView) findViewById(R.id.blueBlack);
        blueAdapter = new BlueAdapter(devices, this);
        blueBlack.setAdapter(blueAdapter);
        etMsg = (EditText) findViewById(R.id.etMsg);
        btnMsg = (Button) findViewById(R.id.btnMsg);
        btnMsg.setEnabled(false);
        btnMsg.setOnClickListener(this);
    }


    /**
     * @param enable 扫描设备
     */
    private void scanLeDevice(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (enable) {
                devices.clear();
                adapter.startDiscovery();
            } else {
                Toast.makeText(this, "设备不可用", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onClick(View v) {
            try {
                String msg= etMsg.getText().toString();
                if(dos!=null&&!TextUtils.isEmpty(msg)) {
                    dos.writeUTF(msg);
                    dos.flush();
                    Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
                    etMsg.setText("");
                }
            } catch (IOException e) {
                e.printStackTrace();
        }

    }


    /**
     *   连接server
     */
    public  void initServer(){
       new Thread(){
           @Override
           public void run() {
               try {
                   serverSocket = adapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                   BluetoothSocket socket = serverSocket.accept();
                   if(socket!=null){
                       //连接成功
                       InputStream inputStream = socket.getInputStream();
                        dis = new DataInputStream(inputStream);
                       outputStream=socket.getOutputStream();
                       dos=new DataOutputStream(outputStream);
                       mHandle.obtainMessage(200).sendToTarget();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }

           }
       }.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop=true;
    }
}
