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
    private int _preWidth = -1;
    private int _preHeight = -1;

    public OutputView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon);

        if (mBitmap==null)
            if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "cstr: mBitmap IS null");
        else
            if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "cstr: mBitmap IS NOT null");

    }

    public void setSize(int width, int height) {
        _preWidth = width;
        _preHeight = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "draw called");
        if (mBitmap!=null) {
            if (_preHeight==-1 && _preWidth==-1)
                canvas.drawBitmap(mBitmap, 0, 0, null);
            else {
                Matrix matrix = new Matrix();
                matrix.postScale(
                        (float)_preWidth/(float)mBitmap.getWidth(),
                        (float)_preHeight/(float)mBitmap.getHeight());
                canvas.drawBitmap(mBitmap, matrix, null);
            }
        }
        super.onDraw(canvas);
    }
}
