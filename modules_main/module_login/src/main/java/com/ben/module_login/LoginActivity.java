package com.ben.module_login;

import android.hardware.camera2.DngCreator;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.ben.lib_arouter.DNRouter;
import com.ben.router_annotation.Extra;
import com.ben.router_annotation.Router;

import androidx.appcompat.app.AppCompatActivity;

@Router(path = "/login/testLogin")
public class LoginActivity extends AppCompatActivity {

    @Extra(name = "subTitle")
    public String subbbTitle;
    // 有name，则把name作为key；
    // 如果没有设置name，那么以参数的命名做为key
//    @Extra
//    String subTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        DNRouter.getInstance().inject(this);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvSubTitle = findViewById(R.id.tv_sub_title);
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        if (!TextUtils.isEmpty(subbbTitle)) {
            tvSubTitle.setText(subbbTitle);
        }

        findViewById(R.id.iv_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DNRouter.getInstance().build("/main/testMain").navigation(LoginActivity.this);
                finish();
            }
        });
    }

}