package com.machineswithvision.vxview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Anthony on 19/02/2015.
 */
public class OutputView extends View {
    private static final String TAG = "OutputView";

    private Bitmap mBitmap = null;
    private int[] pixels = null;
    private int _screenWidth = 0;
    private int _screenHeight = 0;

    public OutputView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);

        if (mBitmap==null)
            if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "cstr: mBitmap IS null");
        else
            if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "cstr: mBitmap IS NOT null");

    }

    public void setScreenSize(int screenWidth, int screenHeight) {
        _screenWidth = screenWidth;
        _screenHeight = screenHeight;
    }

    public void setImage(byte[] bytes, int width, int height) {
        synchronized (mBitmap) {
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                pixels = new int[width * height];
            } else if (mBitmap.getWidth() != width || mBitmap.getHeight() != height) {
                mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                pixels = new int[width * height];
            }
            decodeYUV(pixels, bytes, width, height);
            for(int i=0;i<width*height;i++) {
                int pixel = pixels[i];
                int A = (pixel>>24)&0xFF;
                int R = (pixel>>16)&0xFF;
                int G = (pixel>>8)&0xFF;
                int B = pixel&0xFF;
                pixel = A<<24 | (R<<16) | (B<< 8) | G;
                pixels[i] = pixel;
            }
            mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (mBitmap) {
            if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "draw called");
            if (mBitmap != null) {
                Matrix matrix= new Matrix();
                matrix.setScale(
                        (float)_screenWidth/(float)mBitmap.getWidth(),
                        (float)_screenHeight/(float)mBitmap.getHeight());
                canvas.drawBitmap(mBitmap, matrix, null);
            }
            super.onDraw(canvas);
        }
    }

    // http://stackoverflow.com/questions/1893072/getting-frames-from-video-image-in-android
    // decode Y, U, and V values on the YUV 420 buffer described as YCbCr_422_SP by Android
    // David Manpearl 081201
    public void decodeYUV(int[] out, byte[] fg, int width, int height) {
        int sz = width * height;
        int i, j;
        int Y, Cr = 0, Cb = 0;
        for (j = 0; j < height; j++) {
            int pixPtr = j * width;
            final int jDiv2 = j >> 1;

            for (i = 0; i < width; i++) {
                Y = fg[pixPtr];
                if (Y < 0)
                    Y += 255;
                if ((i & 0x1) != 1) {
                    final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
                    Cb = fg[cOff];
                    if (Cb < 0)
                        Cb += 127;
                    else
                        Cb -= 128;
                    Cr = fg[cOff + 1];
                    if (Cr < 0)
                        Cr += 127;
                    else
                        Cr -= 128;
                }
                int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
                if (R < 0)
                    R = 0;
                else if (R > 255)
                    R = 255;
                int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
                        + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
                if (G < 0)
                    G = 0;
                else if (G > 255)
                    G = 255;
                int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
                if (B < 0)
                    B = 0;
                else if (B > 255)
                    B = 255;
                out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
            }
        }
    }

}
