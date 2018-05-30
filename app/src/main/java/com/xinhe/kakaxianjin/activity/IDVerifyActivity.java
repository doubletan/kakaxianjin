package com.xinhe.kakaxianjin.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.Base64Utils;
import com.xinhe.kakaxianjin.Utils.BitmapUtils;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.LogcatUtil;
import com.xinhe.kakaxianjin.bean.IdMessage;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class IDVerifyActivity extends BaseActivity {

    @BindView(R.id.my_toolbar_tv)
    TextView myToolbarTv;
    @BindView(R.id.id_verify_iv)
    ImageView idVerifyIv;
    @BindView(R.id.id_verify_btn)
    Button idVerifyBtn;
    @BindView(R.id.id_verify_ll)
    LinearLayout idVerifyLl;
    private File mFile;

    private File file;
    private Uri origUri;
    private Bitmap bitmap;
    private KProgressHUD hd;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idverify);
        try {
            ButterKnife.bind(this);

            //初始化
            setViews();
            //初始化toobar
            initActionBar();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
    }

    private void setViews() {
        Intent intent = getIntent();
        count = intent.getIntExtra("count", 0);
        if (1 == count) {
            idVerifyLl.setVisibility(View.GONE);
        }
    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbarTv.setText("信息认证");
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.id_verify_iv, R.id.id_verify_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_verify_iv:
                //权限检查
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA
                            },
                            Constants.PERMISSION_CAMERA);
                    return;
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
                    return;
                }

                takeForPicture();

//                Intent intent = new Intent(IDVerifyActivity.this, Camera2Activity.class);
//                mFile = CommonUtils.createImageFile("mFile");
//                //文件保存的路径和名称
//                intent.putExtra("file", mFile.toString());
//                //拍照时的提示文本
//                intent.putExtra("hint", "请将证件放入框内。将裁剪图片，只保留框内区域的图像");
//                //是否使用整个画面作为取景区域(全部为亮色区域)
//                intent.putExtra("hideBounds", false);
//                //最大允许的拍照尺寸（像素数）
//                intent.putExtra("maxPicturePixels", 3840 * 2160);
//                //startActivityForResult(intent, AppApplication.TAKE_PHOTO_CUSTOM);
//                startActivity(intent);
                break;
            case R.id.id_verify_btn:
                if (null == bitmap) {
                    Toast.makeText(this,"请先点击图片拍照",Toast.LENGTH_SHORT).show();
                } else {
                    submit();
                }
                break;
        }
    }

    //提交
    private void submit() {
        if (!DeviceUtil.IsNetWork(this)) {
            Toast.makeText(this, "网络异常", Toast.LENGTH_LONG).show();
            return;
        }

        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("验证中...")
                .setDimAmount(0.5f)
                .show();

        String base64 = Base64Utils.bitmapToBase64(bitmap);
        Map<String, String> map = new HashMap<>();
        map.put("image", base64);
        map.put("token", MyApplication.token);
        JSONObject jsonObject = new JSONObject(map);


        OkGo.post(Constants.commonURL + Constants.distinguish)
                .tag(this)
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        LogcatUtil.printLogcat(s);
                        Gson gson = new Gson();
                        IdMessage idMessage = gson.fromJson(s, IdMessage.class);

                        if (idMessage.getError_code() == 0) {
                            Toast.makeText(IDVerifyActivity.this, "验证成功", Toast.LENGTH_LONG).show();
                            //是否要跳转
                            if (count==0){
                                startActivity(new Intent(IDVerifyActivity.this, CardActivity.class));
                            }
                            finish();
                        } else if (idMessage.getError_code() == 2) {
                            //token过期，重新登录
                            startActivity(new Intent(IDVerifyActivity.this, LoginActivity.class));
                            Toast.makeText(IDVerifyActivity.this, idMessage.getError_message(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(IDVerifyActivity.this, idMessage.getError_message(), Toast.LENGTH_LONG).show();
                        }

                        hd.dismiss();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(IDVerifyActivity.this, "请求失败", Toast.LENGTH_LONG).show();
                        hd.dismiss();
                        super.onError(call, response, e);
                    }
                });

    }

    /**
     * 调用相机
     */
    private void takeForPicture() {

        try {
            String storageState = Environment.getExternalStorageState();
            File vFile;
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                vFile = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/kakaxianjin/");//图片位置
                if (!vFile.exists()) {
                    vFile.mkdirs();
                }
            } else {
                Toast.makeText(IDVerifyActivity.this, "未挂载sdcard", Toast.LENGTH_LONG).show();
                return;
            }

            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new
                    Date()) + ".jpg";

            file = new File(vFile, fileName);
            //拍照所存路径
            origUri = Uri.fromFile(file);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Build.VERSION.SDK_INT > 23) {//7.0及以上
                origUri = FileProvider.getUriForFile(IDVerifyActivity.this, Constants.fileprovider, new File(vFile, fileName));
                grantUriPermission(getPackageName(), origUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
            } else {//7.0以下
                intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
            }
            startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PICETURE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //拍完照后处理
    private void takePictureResult(int resultCode) {
        if (resultCode == RESULT_OK) {

            if (origUri != null) {

                try {
                    bitmap = BitmapUtils.getBitmapFormUri(IDVerifyActivity.this, origUri);
                } catch (IOException e) {
                    ExceptionUtil.handleException(e);
                }

            }
            idVerifyIv.setImageBitmap(bitmap);
        } else {
            //点击了file按钮，必须有一个返回值，否则会卡死
            Toast.makeText(this, "拍照失败", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 105:
                takePictureResult(resultCode);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "相机权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
            case 103:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "手机储存权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
