package com.ben.dnarouter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.ben.lib_arouter.DNRouter;
import com.ben.router_annotation.Router;

import androidx.appcompat.app.AppCompatActivity;

@Router(path = "/app/testWelcome")
public class WelcomeActivity extends AppCompatActivity {

    private String title = "大家好！";
    private String subTitle = "欢迎来到动脑音乐播放大厅！";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                skip();
            }
        }, 2000);
    }

    private void skip() {
        DNRouter.getInstance().build("/login/testLogin").withString("title", title).withString("subTitle", subTitle).navigation(this);
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

}