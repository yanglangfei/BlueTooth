package jucaipen.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kcode.lib.UpdateWrapper;
import com.pixplicity.htmlcompat.HtmlCompat;

import jucaipen.bluetooth.activity.CustomsUpdateActivity;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Administrator on 2017/3/8.
 */

public class WelcomActivity extends AppCompatActivity {
    private TextView htmlTV;
    private UpdateWrapper wrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        initView();
        ShortcutBadger.applyCount(this,10);
    }

    private void initView() {
        htmlTV= (TextView) findViewById(R.id.htmlTV);
        String source = "<h1>title</h1><a href='www.baidu.com'>" +
                "<img src='http://p4.qhimg.com/dmfd/__90/t011c179a7b43e0d0b9.jpg'>" +
                "</a>";
        Spanned spanned = HtmlCompat.fromHtml(this, source, 0);
        htmlTV.setMovementMethod(LinkMovementMethod.getInstance());
        htmlTV.setText(spanned);
        wrapper=new UpdateWrapper.Builder(getApplicationContext())
                .setTime(1000)
                .setNotificationIcon(R.mipmap.ic_launcher)
                .setCustomsActivity(CustomsUpdateActivity.class)
                .setUrl("http://192.168.1.134:8080/Erp/tiqqian/updateApk").build();
    }

    public  void  onUpdate(View view){
        wrapper.start();
    }
}
