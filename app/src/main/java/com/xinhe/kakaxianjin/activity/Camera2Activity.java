package com.xinhe.kakaxianjin.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.*;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.xinhe.kakaxianjin.MyApplication;
import com.xinhe.kakaxianjin.R;
import com.xinhe.kakaxianjin.Utils.BitmapUtils;
import com.xinhe.kakaxianjin.Utils.Camera2Utils;
import com.xinhe.kakaxianjin.Utils.SPUtils;
import com.xinhe.kakaxianjin.bean.CardBean;
import com.xinhe.kakaxianjin.bean.InputBean;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Camera2 API. Android Lollipop 及以后版本的 Android 使用 Camera2 API.
 * <p>
 * 从https://github.com/googlesamples/android-Camera2Basic/blob/master/Application/src/main/java/
 * com/example/android/camera2basic/Camera2BasicFragment.java拷贝而来.
 * <p>
 * 进行了一些修改, 以文档注释的形式写出.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Activity extends AutoLayoutActivity {

    /**
     * finish()是否已调用过
     */
    volatile boolean mFinishCalled;

    /**
     * 最大允许的拍照尺寸（像素数）
     */
    long mMaxPicturePixels;

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link Log}.
     */
    static final String TAG = "Camera2BasicFragment";

    /**
     * Camera state: Showing camera preview.
     */
    static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    static final int MAX_PREVIEW_WIDTH = 1920;

    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    static final int MAX_PREVIEW_HEIGHT = 1080;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width, height);
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    String mCameraId;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    CameraDevice mCameraDevice;

    /**
     * The {@link Size} of camera preview.
     */
    Size mPreviewSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Toast.makeText(MyApplication.getApp(), "相机开启失败，再试一次吧", Toast.LENGTH_LONG).show();
            mFinishCalled = true;
            finish();
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    File mFile;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
        }

    };

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the sApp from exiting before closing the camera.
     */
    Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    boolean mFlashSupported;

    /**
     * Orientation of the camera sensor
     */
    int mSensorOrientation;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    /**
                     * 判断可以立即拍摄的autoFocusState增加到4种.
                     */
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState ||
                            CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        /**
                         * 判断可以立即拍摄的autoExposureState增加到4种.
                         */
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED ||
                                aeState == CaptureResult.CONTROL_AE_STATE_LOCKED ||
                                aeState == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            process(result);
        }

    };
    @BindView(R.id.texture_camera_preview)
    TextureView textureCameraPreview;
    @BindView(R.id.view_camera_dark0)
    View viewCameraDark0;
    @BindView(R.id.tv_camera_hint)
    TextView tvCameraHint;
    @BindView(R.id.view_camera_dark1)
    LinearLayout viewCameraDark1;
    @BindView(R.id.iv_camera_button)
    ImageView ivCameraButton;
    @BindView(R.id.iv_camera_off)
    ImageView ivCameraOff;

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                  int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        double minRatio = ((double) w) / ((double) h) * 0.95;
        double maxRatio = ((double) w) / ((double) h) * 1.05;
        for (Size option : choices) {
            double ratio = ((double) option.getWidth()) / ((double) option.getHeight());
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    /**
                     * 现在允许宽高比相对于16:9有正负5%的误差.
                     */
                    ratio >= minRatio && ratio <= maxRatio) {
                if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    @SuppressWarnings({"ConstantConditions", "SuspiciousNameCombination"})
    void setUpCameraOutputs(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) continue;

                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) continue;

                // For still image captures, we use the largest available size.
                /*Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());*/
                /**
                 * 替换了寻找最大尺寸的算法.
                 * 从OutputSizes中找到满足16:9比例，且像素数不超过3840*2160的最大Size.
                 * 若找不到，则选择满足16:9比例的最大Size（像素数可能超过3840*2160)，若仍找不到，返回最大Size。
                 */
                Size largest = Camera2Utils.findBestSize(map.getOutputSizes(ImageFormat.JPEG), mMaxPicturePixels);
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = getWindowManager().getDefaultDisplay().getRotation();
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270)
                            swappedDimensions = true;
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180)
                            swappedDimensions = true;
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) maxPreviewWidth = MAX_PREVIEW_WIDTH;

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) maxPreviewHeight = MAX_PREVIEW_HEIGHT;

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the camera.
     */
    @SuppressWarnings("MissingPermission")
    void openCamera(int width, int height) {
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(4, TimeUnit.SECONDS))
                throw new RuntimeException("Time out waiting to lock camera opening.");
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MyApplication.getApp(), "相机开启失败，再试一次吧", Toast.LENGTH_LONG).show();
            mFinishCalled = true;
            finish();
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = textureCameraPreview.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // The camera is already closed
                    if (null == mCameraDevice) return;

                    // When the session is ready, we start displaying the preview.
                    mCaptureSession = cameraCaptureSession;
                    try {
                        // Auto focus should be continuous for camera preview.
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // Flash is automatically enabled when necessary.
                        setAutoFlash(mPreviewRequestBuilder);

                        // Finally, we start displaying the camera preview.
                        mPreviewRequest = mPreviewRequestBuilder.build();
                        mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MyApplication.getApp(), "开启相机预览失败，再试一次吧", Toast.LENGTH_LONG).show();
                        mFinishCalled = true;
                        finish();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MyApplication.getApp(), "开启相机预览失败，再试一次吧", Toast.LENGTH_LONG).show();
                    mFinishCalled = true;
                    finish();
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MyApplication.getApp(), "开启相机预览失败，再试一次吧", Toast.LENGTH_LONG).show();
            mFinishCalled = true;
            finish();
        }
    }

    /**
     * Configures the necessary {@link Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    void configureTransform(int viewWidth, int viewHeight) {
        if (null == textureCameraPreview || null == mPreviewSize) return;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) viewHeight / mPreviewSize.getHeight(), (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureCameraPreview.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    void takePicture() {
        lockFocus();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    void captureStillPicture() {
        try {
            if (null == mCameraDevice) return;
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);

            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    Log.d(TAG, mFile.toString());
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        /**
         * 若相机支持自动开启/关闭闪光灯，则使用. 否则闪光灯总是关闭的.
         */
        if (mFlashSupported)
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static final String DEFAULT_PATH = "/sdcard/";
    private static final String DEFAULT_NAME = "default.jpg";
    private static final String DEFAULT_TYPE = "default";
    class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        final Image mImage;
        /**
         * The file we save the image into.
         */
        File file1;
        String photoBase64;
        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }
        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            try {
                if (mFile.exists()) mFile.delete();
                FileOutputStream output = new FileOutputStream(mFile);
                output.write(bytes);
                try {mImage.close();} catch (Exception ignored) {}
                try {output.close();} catch (Exception ignored) {}
                /**
                 * 拍照完成后返回MainActivity.
                 */
               file1=new File(mFile.toString());
                Flowable.just(file1)
                        //将File解码为Bitmap
                        .map(file -> BitmapUtils.compressToResolution(file, 1920 * 1080))
                        //裁剪Bitmap
                        .map(BitmapUtils::crop)
                        //将Bitmap写入文件
                        .map(bitmap -> BitmapUtils.writeBitmapToFile(bitmap, "mFile"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(file -> {
                            file1 = file;
                            Log.i("file-----",file1.getAbsolutePath());
                            MyApplication.mHandler.post(() -> {
                                mFinishCalled = true;
//                                AppApplication.destoryActivity("CarmeraResultActivity");
                                String camera = (String) SPUtils.get(Camera2Activity.this, "camera", "1");
                                if(camera==null||camera.equals("1")){
                                    SPUtils.put(Camera2Activity.this,"camera","ok");
                                    identifyPhoto(file1.getAbsolutePath(),photoBase64);
                                }
                            });
                            //清除该Uri的Fresco缓存. 若不清除，对于相同文件名的图片，Fresco会直接使用缓存而使得Drawee得不到更新.
                        });
               /* AppApplication.mHandler.post(() -> {
                    mFinishCalled = true;
                    File file=new File(file2.getAbsolutePath());
                    Bitmap bitmap = BitmapUtils.compressToResolution(file, 1920 * 1080);
                    BitmapUtils.crop(bitmap);
                    // setResult(200, getIntent().putExtra("file", mFile.toString()));
                    Log.i("file-----",file2.getAbsolutePath());
                    identifyPhoto(file2.getAbsolutePath(),photoBase64);

                });*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private Bitmap bitmap;
    private KProgressHUD hd;
    private void identifyPhoto(final String path, String base) {
        hd .show();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        bitmap = BitmapFactory.decodeFile(path, options);
        String base64 = bitmapToBase64(bitmap);
        InputBean.InputsBean.ImageBean imageBean= new InputBean.InputsBean.ImageBean();
        imageBean.setDataType(50);
        imageBean.setDataValue(base64);
        InputBean.InputsBean.ConfigureBean.DataValueBean dataValueBean=new InputBean.InputsBean.ConfigureBean.DataValueBean();
        dataValueBean.setSide("face");
        InputBean.InputsBean.ConfigureBean configureBean=new InputBean.InputsBean.ConfigureBean();
        configureBean.setDataType(50);
        configureBean.setDataValue("{\"side\":\"face\"}");
        InputBean.InputsBean bean=new InputBean.InputsBean();
        bean.setImage(imageBean);
        bean.setConfigure(configureBean);
        List<InputBean.InputsBean> list=new ArrayList<>();
        list.add(bean);
        InputBean inputBean=new InputBean();
        inputBean.setInputs(list);
        Gson gson=new Gson();
        String json = gson.toJson(inputBean);
        OkGo.<String>post("https://dm-51.data.aliyun.com/rest/160601/ocr/ocr_idcard.json")
                .tag(this)
                .headers("Authorization","APPCODE "+"a37dbd4d651b43c2a7e0c56b3f842b74")
                .headers("Content-Type", "application/json; charset=UTF-8")
                .upJson(json)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        hd.dismiss();
                        String s1 = s.replace("\"{", "{");
                        String s2 = s1.replace("\\", "");
                        int face = s2.indexOf("side");
                        String substring = s2.substring(0, face + 13);
                        String substring1 = s2.substring(face + 14, s2.length());
                        String s3=substring+substring1;
                        String replace = s3.replace("\"}}]}", "}}]}");
                        Gson gson1=new Gson();
                        CardBean cardBean = null;
                            cardBean = gson1.fromJson(replace, CardBean.class);
                            Log.i("dataValue---",cardBean.toString());
                            CardBean.OutputsBean.OutputValueBean.DataValueBean value = cardBean.getOutputs().get(0).getOutputValue().getDataValue();
                            if(cardBean.getOutputs().get(0).getOutputValue().getDataValue().isSuccess()){
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("carmera",value);
                                bundle.putString("path",path);
                                SPUtils.remove(Camera2Activity.this,"camera");
//                                startActivity(new Intent(Camera2Activity.this,CarmeraResultActivity.class).putExtra("bundle",bundle));
                                finish();
                            }else {
                                Toast.makeText(Camera2Activity.this,"解析失败,请重新扫描",Toast.LENGTH_LONG).show();
                                SPUtils.remove(Camera2Activity.this,"camera");

                            }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Camera2Activity.this,"解析失败,请重新扫描",Toast.LENGTH_LONG).show();
                                SPUtils.remove(Camera2Activity.this,"camera");
                                hd.dismiss();
                            }
                        });
                    }
                });

    }
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera2);
        ButterKnife.bind(this);
        mFile = new File(getIntent().getStringExtra("file"));
        tvCameraHint.setText(getIntent().getStringExtra("hint"));
        if (getIntent().getBooleanExtra("hideBounds", false)) {
            viewCameraDark0.setVisibility(View.INVISIBLE);
            viewCameraDark1.setVisibility(View.INVISIBLE);
        }
        mMaxPicturePixels = getIntent().getIntExtra("maxPicturePixels", 3840 * 2160);
        hd = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("识别中,请稍后...")
                .setDimAmount(0.5f);
        RxView.clicks(ivCameraButton)
                .throttleFirst(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> takePicture());

        ivCameraOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
/*    @Override
    protected int getContentViewResId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_new_camera;
    }

    @Override
    protected void preInitData() {
        mFile = new File(getIntent().getStringExtra("file"));
        tvCameraHint.setText(getIntent().getStringExtra("hint"));
        if (getIntent().getBooleanExtra("hideBounds", false)) {
            viewCameraDark0.setVisibility(View.INVISIBLE);
            viewCameraDark1.setVisibility(View.INVISIBLE);
        }
        mMaxPicturePixels = getIntent().getIntExtra("maxPicturePixels", 3840 * 2160);
        RxView.clicks(ivCameraButton)
                *//**
                 * 防止手抖连续多次点击造成错误
                 *//*
                .throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> takePicture());
    }*/

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (textureCameraPreview.isAvailable()) {
            openCamera(textureCameraPreview.getWidth(), textureCameraPreview.getHeight());
        } else {
            textureCameraPreview.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onBackPressed() {
        mFinishCalled = true;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(bitmap!=null){
            bitmap.recycle();
            bitmap=null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
        stopBackgroundThread();
        if (!mFinishCalled) finish();
    }
}