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

import java.nio.ByteBuffer;

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

            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes);
            processBytes(buffer);
            buffer.clear();
            buffer.get(bytes);

            for(int i=0;i<width*height;i++) {
                int Y = ((int)bytes[i])&0xFF;
                int pixel = 0xFF000000 | (Y<<16) | (Y<< 8) | Y;
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

    private native void processBytes(ByteBuffer buffer);
}
