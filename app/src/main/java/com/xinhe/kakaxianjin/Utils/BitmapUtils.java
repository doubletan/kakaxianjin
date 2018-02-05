package com.xinhe.kakaxianjin.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;

import com.xinhe.kakaxianjin.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class BitmapUtils {

    public static Bitmap cropStatusBar(Bitmap bitmap, int compressRatio) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / compressRatio, bitmap.getHeight() / compressRatio, false);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) return scaledBitmap;
        int statusBarHeight = CommonUtils.dp2px(24);
        return Bitmap.createBitmap(scaledBitmap, 0, statusBarHeight / compressRatio, scaledBitmap.getWidth(), scaledBitmap.getHeight() - statusBarHeight / compressRatio);
    }

    /**
     * 将拍照得到的图片按照取景框（亮色区域）的范围进行裁剪.
     * 对于1280x720的屏幕，裁剪起始点为坐标(52, 80)，裁剪后，位图尺寸为896x588.（由layout xml定义的布局计算得到）
     * 以上参数将按照图片的实际大小，进行等比例换算。
     * 设备有无虚拟导航栏均能裁剪到正确的区域。
     *
     * @param originalBitmap 拍照得到的Bitmap
     * @return 裁剪之后的Bitmap
     */
    public static Bitmap crop(Bitmap originalBitmap) {
        double originalWidth = originalBitmap.getWidth();
        double originalHeight = originalBitmap.getHeight();
        //图片真正的宽度除以设计图的宽(1280)，得到相对于设计图的缩放比，用于后续裁剪起点坐标和裁剪区域大小的计算
        double scaleX = originalWidth / 1280;
        //将虚拟导航栏的高度换算为1280x720设计图下所占的高度(px)，若设备没有虚拟导航栏，返回0.
        int navBarHeightPxIn1280x720Ui = CommonUtils.px2dp(CommonUtils.getNavigationBarHeightInPx()) * 3 /*1280x720的设计图下，1dp = 2px*/;
        //考虑到虚拟导航栏所占的高度，需要对scaleX进行修正，下面计算修正的倍乘系数
        double scaleXMultiplier = ((double) 1280) / ((double) (1280 - navBarHeightPxIn1280x720Ui));
        //修正scaleX，以保证在有无虚拟导航栏的情况下，裁剪区域均正确
        scaleX = scaleX * scaleXMultiplier;
        double scaleY = originalHeight / 720;
        //在1280x720的设计图上，裁剪起点坐标(52, 80)
        int x = (int) (52 * scaleX + 0.5);
        int y = (int) (80 * scaleY + 0.5);
        //在1280x720的设计图上，裁剪区域大小为896x588
        int width = (int) (896 * scaleX + 0.5);
        int height = (int) (588 * scaleY + 0.5);
        return Bitmap.createBitmap(originalBitmap, x, y, width, height);
    }

    /**
     * 若图片宽小于高，则逆时针旋转90° ; 否则，返回原图片.
     * 适用于调用系统相机进行拍照，且希望图片总是横向的场景；
     * Bitmap占用内存较大，建议先压缩到一个分辨率级别，再进行旋转。
     *
     * @param sourceBitmap 拍照得到的Bitmap
     * @return 若图片宽小于高，返回逆时针旋转90°后的Bitmap ; 否则，返回原Bitmap.
     */
    public static Bitmap rotate(Bitmap sourceBitmap) {
        int sourceWidth = sourceBitmap.getWidth();
        int sourceHeight = sourceBitmap.getHeight();
        //若原图的宽大于高，不需要旋转，直接返回原图
        if (sourceWidth >= sourceHeight) return sourceBitmap;
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceWidth, sourceHeight, matrix, true);
    }

    /**
     * 将图片文件压缩到所需的大小，返回位图对象.
     * 若原图尺寸小于需要压缩到的尺寸，则返回原图.
     * 该方法通过分辨率面积得到压缩比，因此不存在图片方向、宽高比的问题.
     *
     * @param filePath        File
     * @param reqSquarePixels 要压缩到的分辨率（面积）
     * @return 压缩后的Bitmap
     */
    public static Bitmap compressToResolution(File filePath, long reqSquarePixels) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath.toString(), options);
        double compressRatio = calculateCompressRatioBySquare(options, reqSquarePixels);
        //解析原图
        Bitmap fullBitmap = BitmapFactory.decodeFile(filePath.toString());
        //创建缩放的Bitmap。这里不用inSampleSize，inSampleSize基于向下采样，节省了Bitmap读入后占用的内存，但Bitmap本身的像素还是那么多，文件大小没有改变。
        return Bitmap.createScaledBitmap(fullBitmap, (int) (options.outWidth / compressRatio), (int) (options.outHeight / compressRatio), false);
    }

    /**
     * 计算压缩比.考虑了options的长宽比可能与req中的比例不同的情况.
     * 该方法通过分辨率面积得到压缩比，因此不存在图片方向、宽高比的问题.
     *
     * @param options         BitmapFactory.Options
     * @param reqSquarePixels 压缩后的分辨率面积
     * @return 计算得到的压缩比
     */
    public static double calculateCompressRatioBySquare(BitmapFactory.Options options, long reqSquarePixels) {
        //原图像素数
        long squarePixels = options.outWidth * options.outHeight;
        //若原图像素数少于需要压缩到的像素数，不压缩（压缩比返回1）
        if (squarePixels <= reqSquarePixels) return 1;
        //面积之比，即压缩比（长度之比）的平方
        double powwedScale = ((double) squarePixels) / ((double) reqSquarePixels);
        //开根号得压缩比
        return Math.sqrt(powwedScale);
    }

    /**
     * 将Bitmap写入文件，文件位于内置存储上该应用包名目录的cache子目录中.
     *
     * @param bitmap   要写入的Bitmap
     * @param fileName 文件名
     * @return 文件对应的File.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File writeBitmapToFile(Bitmap bitmap, String fileName) {
        //FileProvider中指定的目录
        File dir = new File(MyApplication.getApp().getCacheDir(), "images");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, fileName);
        FileOutputStream fos;
        try {
            if (file.exists()) file.delete();
            fos = new FileOutputStream(file);
            //JPEG为硬件加速，O(1)时间复杂度，而PNG为O(n)，速度要慢很多，WEBP不常用
            //90%的品质已高于超精细(85%)的标准，已非常精细
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            bitmap.recycle();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
