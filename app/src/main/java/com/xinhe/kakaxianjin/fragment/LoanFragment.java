package com.xinhe.kakaxianjin.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.Constants;
import com.xinhe.kakaxianjin.Utils.DeviceUtil;
import com.xinhe.kakaxianjin.Utils.ExceptionUtil;
import com.xinhe.kakaxianjin.Utils.GetPathFromUri4kitkat;
import com.xinhe.kakaxianjin.Utils.PopupWindowUtil;
import com.xinhe.kakaxianjin.Utils.Utill;
import com.xinhe.kakaxianjin.biz.update.UpdateService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tantan on 2018/1/15.
 */

public class LoanFragment extends Fragment {

    @BindView(R.id.loan_fragment_top)
    LinearLayout loanFragmentTop;
    @BindView(R.id.loan_fragment_refresh_iv)
    ImageView loanFragmentRefreshIv;
    @BindView(R.id.common_web_bar)
    ProgressBar commonWebBar;
    @BindView(R.id.loan_fragment_web)
    WebView loanFragmentWeb;
    Unbinder unbinder;
    private View view;
    private ValueCallback mFilePathCallback;
    private File vFile;
    private Uri origUri;
    private String myUrl=Constants.loan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            view = View.inflate(getActivity(), R.layout.loan_fragment, null);
            unbinder = ButterKnife.bind(this, view);
//            // 设置顶部控件不占据状态栏
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                LinearLayout barLl = (LinearLayout) view.findViewById(R.id.loan_fragment_top);
//                barLl.setVisibility(View.VISIBLE);
//                LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) barLl.getLayoutParams();
//                ll.height = Utill.getStatusBarHeight(getContext());
//                ll.width = RelativeLayout.LayoutParams.MATCH_PARENT;
//                barLl.setLayoutParams(ll);
//            }
            //初始化
            initViews();
            //监听
            setListener();
        } catch (Exception e) {
            ExceptionUtil.handleException(e);
        }
        return view;
    }

    private void setListener() {
        loanFragmentWeb.setOnKeyListener(new View.OnKeyListener() {
                     public boolean onKey(View v, int keyCode, KeyEvent event) {
                                 if ((keyCode == KeyEvent.KEYCODE_BACK) && loanFragmentWeb.canGoBack()) {
                                     if(event.getAction()==KeyEvent.ACTION_DOWN){ //只处理一次
                                         loanFragmentWeb.goBack();
                                     }
                                        return true;
                                    }
                                 return false;
                            }

                 });
    }


    //初始化
    private void initViews() {


        // 设置WebView属性，能够执行Javascript脚本
        loanFragmentWeb.getSettings().setJavaScriptEnabled(true);// 是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞

        // 有些网页webview不能加载
        loanFragmentWeb.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);// 设置js可以直接打开窗口，如window.open()，默认为false
        loanFragmentWeb.getSettings().setSupportZoom(true);// 是否可以缩放，默认true
        loanFragmentWeb.getSettings().setBuiltInZoomControls(false);// 是否显示缩放按钮，默认false
        loanFragmentWeb.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        loanFragmentWeb.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        loanFragmentWeb.getSettings().setAppCacheEnabled(true);// 是否使用缓存
        loanFragmentWeb.getSettings().setDomStorageEnabled(true);// DOM Storage
//		loanFragmentWeb.getSettings().setUseWideViewPort(true);
//		loanFragmentWeb.getSettings().setDatabaseEnabled(true);
//		loanFragmentWeb.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//		loanFragmentWeb.getSettings().setBlockNetworkImage(true);
//		loanFragmentWeb.getSettings().setAllowFileAccess(true);
//		loanFragmentWeb.getSettings().setSaveFormData(false);
//		loanFragmentWeb.getSettings().setLoadsImagesAutomatically(true);
//        loanFragmentWeb.getSettings().setSupportMultipleWindows(true);
//        loanFragmentWeb.getSettings().setLoadWithOverviewMode(true);
//        loanFragmentWeb.getSettings().setUseWideViewPort(true);

        if (Build.VERSION.SDK_INT >= 21) {
            loanFragmentWeb.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        /**
         * 设置获取位置
         */
        //启用数据库
        loanFragmentWeb.getSettings().setDatabaseEnabled(true);
        //设置定位的数据库路径
        String dir = getContext().getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        loanFragmentWeb.getSettings().setGeolocationDatabasePath(dir);
        //启用地理定位
        loanFragmentWeb.getSettings().setGeolocationEnabled(true);
        //开启DomStorage缓存
        loanFragmentWeb.getSettings().setDomStorageEnabled(true);


        loanFragmentWeb.setWebChromeClient(new WebChromeClient() {
            //加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根

                if (newProgress == 100) {
                    commonWebBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    commonWebBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    commonWebBar.setProgress(newProgress);//设置进度值
                }
            }

            //获取位置
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            /**
             * h5打开相机或相册
             */

            //5.0+
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                try {
                    showMyDialog();
                    mFilePathCallback = filePathCallback;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }


            // Andorid 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                try {
                    showMyDialog();
                    mFilePathCallback = uploadFile;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Andorid 3.0 +
            public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
                try {
                    showMyDialog();
                    mFilePathCallback = uploadFile;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Android 3.0
            public void openFileChooser(ValueCallback<Uri> uploadFile) {
                try {
                    showMyDialog();
                    mFilePathCallback = uploadFile;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        loanFragmentWeb.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                if (url.endsWith(".apk")) {//判断是否是.apk结尾的文件路径
                    if (DeviceUtil.isWifiAvailable(getContext())) {
                        UpdateService.Builder.create(url).build(getContext());
                    } else {
                        final AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.CustomDialog).create();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                        Window window = alertDialog.getWindow();
                        window.setContentView(R.layout.dialog_two);
                        TextView tv1 = (TextView) window.findViewById(R.id.integral_exchange_tips1_tv);
                        tv1.setText("亲，您现在是非wifi状态下，确定要下载吗？");
                        RelativeLayout rl2 = (RelativeLayout) window.findViewById(R.id.integral_exchange_tips1_rl1);
                        RelativeLayout rl3 = (RelativeLayout) window.findViewById(R.id.integral_exchange_tips1_rl2);
                        rl2.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        rl3.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                UpdateService.Builder.create(url).build(getContext());
                                alertDialog.dismiss();
                            }
                        });
                    }

                }
            }
        });

        loanFragmentWeb.setWebViewClient(new loanFragmentWebClient());

        if (!DeviceUtil.IsNetWork(getContext())) {
            Toast.makeText(getContext(),"网络异常",Toast.LENGTH_LONG).show();
        } else {
            loanFragmentWeb.loadUrl(myUrl);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        loanFragmentWeb.destroy();
        unbinder.unbind();
    }

    @OnClick(R.id.loan_fragment_refresh_iv)
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.loan_fragment_refresh_iv:
                if (!DeviceUtil.IsNetWork(getContext())) {
                    Toast.makeText(getContext(),"网络异常",Toast.LENGTH_LONG).show();
                } else {
                    loanFragmentWeb.loadUrl(myUrl);
                }
            break;
        }
    }

    private class loanFragmentWebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            WebView.HitTestResult hitTestResult = view.getHitTestResult();
            if (!TextUtils.isEmpty(url) && hitTestResult == null) {
                view.loadUrl(url);
                return true;
            } else if (url.contains("platformapi/startapp")) {
                try {
                    Intent intent;
                    intent = Intent.parseUri(url,
                            Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    // intent.setSelector(null);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "请安装最新版支付宝", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (url.contains("weixin://wap/pay?")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "请安装最新版微信", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (url.contains("mqqapi://forward/url?")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "请安装最新版QQ", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (url.contains("tmast://appdetails?")) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "请安装最新版应用宝", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (!(url.contains("http://") || url.contains("https://"))) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    return false;
                }
                return true;
            }
//            else {
//                view.loadUrl(url);
//            }
            return false;
        }
    }


    private void showMyDialog() {
        View rootView = getLayoutInflater().inflate(R.layout.activity_main, null);
        PopupWindowUtil.showPopupWindow(getContext(), rootView, "相机", "文件", "取消",
                new PopupWindowUtil.onPupupWindowOnClickListener() {
                    @Override
                    public void onFirstButtonClick() {
                        int flag1 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
                        int flag2 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (PackageManager.PERMISSION_GRANTED != flag1 || PackageManager.PERMISSION_GRANTED != flag2) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_CAMERA_PERMISSION);
                            cancelFilePathCallback();
                        } else {
                            takeForPicture();
                        }
                    }

                    @Override
                    public void onSecondButtonClick() {
                        int flag2 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (PackageManager.PERMISSION_GRANTED != flag2) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_READ_EXTERNAL_PERMISSION);
                            cancelFilePathCallback();
                        } else {
                            takeForPhoto();
                        }
                    }

                    @Override
                    public void onCancleButtonClick() {
                        cancelFilePathCallback();
                    }
                });
    }

    /**
     * 调用相册
     */
    private void takeForPhoto() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), Constants.REQUEST_CODE_PICK_PHOTO);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), Constants.REQUEST_CODE_PICK_PHOTO);
        }
    }


    /**
     * 调用相机
     */
    private void takeForPicture() {

        try {
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                vFile = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/xianjindai/");//图片位置
                if (!vFile.exists()) {
                    vFile.mkdirs();
                }
            } else {
                Toast.makeText(getContext(), "未挂载sdcard", Toast.LENGTH_LONG).show();
                return;
            }

            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new
                    Date()) + ".jpg";

            Uri uri = Uri.fromFile(new File(vFile, fileName));

            //拍照所存路径
            origUri = uri;

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (Build.VERSION.SDK_INT > 23) {//7.0及以上
//            Uri contentUri = getUriForFile(MainActivity.this, "com.xinhe.crame", picturefile);
//            grantUriPermission(getPackageName(),contentUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
//        } else {//7.0以下
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picturefile));
//        }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
            startActivityForResult(intent, Constants.REQUEST_CODE_TAKE_PICETURE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cancelFilePathCallback() {
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    private void takePhotoResult(int resultCode, Intent data) {
        if (mFilePathCallback != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(getContext(), result);
                Uri uri = Uri.fromFile(new File(path));
                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{uri});
                } else {
                    mFilePathCallback.onReceiveValue(uri);
                }

            } else {
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }
    }

    private void takePictureResult(int resultCode) {
        if (mFilePathCallback != null) {
            if (resultCode == RESULT_OK) {

                if (Build.VERSION.SDK_INT > 18) {
                    mFilePathCallback.onReceiveValue(new Uri[]{origUri});
                } else {
                    mFilePathCallback.onReceiveValue(origUri);
                }
            } else {
                //点击了file按钮，必须有一个返回值，否则会卡死
                mFilePathCallback.onReceiveValue(null);
                mFilePathCallback = null;
            }
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 105:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getContext(), "拍照权限被拒绝", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 105:
                takePictureResult(resultCode);
                break;

            case 106:
                takePhotoResult(resultCode, data);

                break;
        }
    }



}
