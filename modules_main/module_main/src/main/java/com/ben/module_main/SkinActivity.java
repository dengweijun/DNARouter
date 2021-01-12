package com.ben.module_main;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ben.lib_common.utils.skin.Skin;
import com.ben.lib_common.utils.skin.SkinUtils;
import com.ben.skinsupport.SkinManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class SkinActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivColor1, ivColor2, ivColor3, ivColor4;
    private TextView tvState1, tvState2, tvState3, tvState4;
    private TextView[] tvStates;

    /**
     * 从服务器拉取的皮肤表
     */
    List<Skin> skins = new ArrayList<>();

    int skinMode = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
        initViews();
        addSkins();
    }

    private void addSkins() {
        skins.add(new Skin("97e434d74319c8fa0bd6138aedaa2f5f", "purple.skin", "skinapp_purple.apk"));
        skins.add(new Skin("37488e5c4d43da7f728e1d99f147a9e9", "blue.skin", "skinapp_blue.apk"));
        skins.add(new Skin("57652e8eac2092dbb4f384ce82e63839", "black.skin", "skinapp_black.apk"));

        String skinPath = SkinManager.getInstance().getSkinPath();
        Log.i("TAG", "skinPath=" + skinPath);
        if (TextUtils.isEmpty(skinPath)) {
            skinMode = 1;
        } else if (skinPath.contains("purple.skin")) {
            skinMode = 2;
        } else if (skinPath.contains("blue.skin")) {
            skinMode = 3;
        } else if (skinPath.contains("black.skin")) {
            skinMode = 4;
        }
        setSelectView(skinMode);
    }

    private void initViews() {
        TextView tv = findViewById(R.id.tv_main_title);
        tv.setText("个性换肤");

        ivColor1 = findViewById(R.id.iv_color1);
        ivColor2 = findViewById(R.id.iv_color2);
        ivColor3 = findViewById(R.id.iv_color3);
        ivColor4 = findViewById(R.id.iv_color4);
        tvState1 = findViewById(R.id.tv_state1);
        tvState2 = findViewById(R.id.tv_state2);
        tvState3 = findViewById(R.id.tv_state3);
        tvState4 = findViewById(R.id.tv_state4);
        tvStates = new TextView[]{tvState1, tvState2, tvState3, tvState4};

        findViewById(R.id.iv_close).setOnClickListener(this);
        ivColor1.setOnClickListener(this);
        ivColor2.setOnClickListener(this);
        ivColor3.setOnClickListener(this);
        ivColor4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_close) {
            finish();

        } else if (i == R.id.iv_color1) {
            changeSkin(1);

        } else if (i == R.id.iv_color2) {
            changeSkin(2);

        } else if (i == R.id.iv_color3) {
            changeSkin(3);

        } else if (i == R.id.iv_color4) {
            changeSkin(4);

        }
    }

    private void setSelectView(int mode) {
        for (TextView tv : tvStates) {
            tv.setVisibility(View.GONE);
        }
        switch (mode) {
            case 1:
                tvState1.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvState2.setVisibility(View.VISIBLE);
                break;
            case 3:
                tvState3.setVisibility(View.VISIBLE);
                break;
            case 4:
                tvState4.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void changeSkin(int mode) {
        if (skinMode == mode) return;
        setSelectView(mode);
        skinMode = mode;
        Skin skin = null;
        switch (mode) {
            case 1:
                skin = null;
                break;
            case 2:
                skin = skins.get(0);
                break;
            case 3:
                skin = skins.get(1);
                break;
            case 4:
                skin = skins.get(2);
                break;
        }
        if (skin != null) {
            selectSkin(skin);
            SkinManager.getInstance().loadSkin(skin.path);
        } else {
            SkinManager.getInstance().loadSkin(null);
        }
    }

    /**
     * 下载皮肤包
     */
    private void selectSkin(Skin skin) {
        File theme = new File(getFilesDir(), "theme");
        if (theme.exists() && theme.isFile()) {
            theme.delete();
        }
        theme.mkdirs();
        File skinFile = skin.getSkinFile(theme);
        if (skinFile.exists()) {
            Log.i("TAG", "皮肤已存在,开始换肤");
            return;
        }
        Log.i("TAG", "皮肤不存在,开始下载");
        FileOutputStream fos = null;
        InputStream is = null;
        // 临时文件
        File tempSkin = new File(skinFile.getParentFile(), skin.name + ".temp");
        try {
            fos = new FileOutputStream(tempSkin);
            // 假设下载皮肤包
            is = getAssets().open(skin.url);
            byte[] bytes = new byte[10240];
            int len;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            // 下载成功，将皮肤包信息insert已下载数据库
            Log.i("TAG", "皮肤包下载完成开始校验");
            // 皮肤包的md5校验 防止下载文件损坏(但是会减慢速度。从数据库查询已下载皮肤表数据库中保留md5字段)
            String md5 = SkinUtils.getSkinMD5(tempSkin);
            Log.i("TAG", "md5=" + md5);
            if (TextUtils.equals(md5, skin.md5)) {
                Log.i("TAG", "校验成功,修改文件名。");
                tempSkin.renameTo(skinFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tempSkin.delete();
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}