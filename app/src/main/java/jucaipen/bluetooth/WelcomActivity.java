package jucaipen.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.pixplicity.htmlcompat.HtmlCompat;

/**
 * Created by Administrator on 2017/3/8.
 */

public class WelcomActivity extends AppCompatActivity {
    private TextView htmlTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        initView();
    }

    private void initView() {
        htmlTV= (TextView) findViewById(R.id.htmlTV);
        String source = "<h1>title</h1><a href='www.baidu.com'>" +
                "<img src='http://p4.qhimg.com/dmfd/__90/t011c179a7b43e0d0b9.jpg'>" +
                "</a>";
        Spanned spanned = HtmlCompat.fromHtml(this, source, 0);
        htmlTV.setMovementMethod(LinkMovementMethod.getInstance());
        htmlTV.setText(spanned);
    }
}
