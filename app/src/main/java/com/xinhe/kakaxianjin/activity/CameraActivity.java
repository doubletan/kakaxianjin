package com.xinhe.kakaxianjin.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.view.camera.CameraSurfaceView;
import com.xinhe.kakaxianjin.view.camera.CameraTopRectView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends AppCompatActivity {

    @BindView(R.id.camera_SurfaceView)
    CameraSurfaceView cameraSurfaceView;
    @BindView(R.id.rect_OnCamera)
    CameraTopRectView rectOnCamera;
    @BindView(R.id.take_Pic)
    Button takePic;
    //图片路径
    private String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        //初始化
        setView();
    }

    private void setView() {
        Intent intent=getIntent();
        try {
            file=new File(new URI(intent.getStringExtra("file"))).toString();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    @OnClick({R.id.take_Pic})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.take_Pic:
                cameraSurfaceView.takePicture(file);
                break;
        }
    }
}
