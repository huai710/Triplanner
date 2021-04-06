package com.shashipage.triplanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CircleImgView extends AppCompatImageView {
    private Paint paint = new Paint();

    public CircleImgView(Context context) {
        super(context);
    }
    public CircleImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        int width = getWidth();
        //一定要強轉成float 不然有可能因為精度不夠 出現 scale為0 的錯誤
        float scale = (float) width / (float) bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //將原始圖像裁剪成正方形 
    private Bitmap dealRawBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
//獲取寬度
        int minWidth = width > height ? height : width;
//計算正方形的範圍
        int leftTopX = (width - minWidth) / 2;
        int leftTopY = (height - minWidth) / 2;
//裁剪成正方形
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, leftTopX, leftTopY, minWidth, minWidth, null, false);
        return scaleBitmap(newBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap rawBitmap = ((BitmapDrawable) drawable).getBitmap();

//處理Bitmap 轉成正方形
            Bitmap newBitmap = dealRawBitmap(rawBitmap);
//將newBitmap 轉換成圓形
            Bitmap circleBitmap = toRoundCorner(newBitmap, 14);

            final Rect rect = new Rect(0, 0, circleBitmap.getWidth(), circleBitmap.getHeight());
            paint.reset();
//繪製到畫布上
            canvas.drawBitmap(circleBitmap, rect, rect, paint);
        } else {
            super.onDraw(canvas);
        }
    }

    private Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

//指定為 ARGB_4444 可以減小圖片大小
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }



}