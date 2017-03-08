package jucaipen.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import jucaipen.bluetooth.activity.ClientActivity;
import jucaipen.bluetooth.activity.ServerActivity;

/**
 * Created by Administrator on 2017/3/8.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public  void  onClient(View view){
        startActivity(new Intent(this, ClientActivity.class));
    }


    public  void  onServer(View view){
        startActivity(new Intent(this, ServerActivity.class));
    }

}
