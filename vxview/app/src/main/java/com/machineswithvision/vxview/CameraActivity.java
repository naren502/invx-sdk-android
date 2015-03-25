//
//  CameraActivity.java
//
//  Created by Anthony Ashbrook on 24/03/2015.
//
//  Copyright (c) 2015 Machines with Vision. All rights reserved.
//
// THE MATERIALS ARE PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
// CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
// MATERIALS OR THE USE OR OTHER DEALINGS IN THE MATERIALS.
//

package com.machineswithvision.vxview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.machineswithvision.openvx.JOVX;

import java.io.IOException;
import java.nio.ByteBuffer;

public class CameraActivity extends Activity {
    private static final String TAG = "CameraActivity";

    static {
        System.loadLibrary("NDK");
    }

    // State
    private boolean weHaveBeenResumed = false;
    private boolean existingSurfaceHasSize = false;
    private boolean cameraIsActive = false;
    private int preWidth;
    private int preHeight;
    private int disWidth;
    private int disHeight;

    // Components
    private SurfaceHolder holder;
    private Camera camera;
    private Overlay _overlay;

    private boolean focusAuto = false;
    private boolean focused = true;

    private Object contAutoFocusCallback = null;

    private static final String FOCUS_CONTINUOUS_AUTO = "continuous-picture";

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        JOVX.jovxReleaseContext();
    }

    /**
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JOVX.jovxCreateContext();

        // Create a fixed-orientation fullscreen view with no title
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_camera);

        final SurfaceView surface = (SurfaceView)findViewById(R.id.surface_preview);
        holder = surface.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            /**
             *
             */
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                synchronized (CameraActivity.this) {
                    if (weHaveBeenResumed) {
                        createCamera();
                        if (Log.isLoggable(TAG, Log.DEBUG))
                            Log.d(TAG, "Surface created - creating camera");
                    } else {
                        if (Log.isLoggable(TAG, Log.DEBUG))
                            Log.d(TAG, "Surface created - not yet resumed so don't create the camera");
                    }
                }
            }

            /**
             *
             */
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (Log.isLoggable(TAG, Log.DEBUG))
                    Log.d(TAG, "Surface changed: " + format + "," + width + "," + height);
                synchronized (CameraActivity.this) {
                    if (cameraIsActive) {
                        if (Log.isLoggable(TAG, Log.WARN))
                            Log.w(TAG, "Scanner is already active - ignoring!");
                    } else {
                        // We always expect the reader screen to run fixed in landscape mode.
                        // In some circs the moto milestone running 2.1 reverses the width/height
                        if (width < height) {
                            if (Log.isLoggable(TAG, Log.WARN))
                                Log.w(TAG, "Width and height appear to be swapped - swapping back!");
                            int tmp = width;
                            width = height;
                            height = tmp;
                        }

                        disWidth = width;
                        disHeight = height;

                        if (weHaveBeenResumed) {
                            startCamera();
                        } else {
                            if (Log.isLoggable(TAG, Log.DEBUG))
                                Log.d(TAG, "Not yet resumed so don't start the camera yet!");
                        }

                        existingSurfaceHasSize = true;
                    }
                }
            }

            /**
             *
             */
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Surface destroyed");
                existingSurfaceHasSize = false;

                if (cameraIsActive) {
                    if (Log.isLoggable(TAG, Log.WARN))
                        Log.w(TAG, "We should never get here with an active camera!");
                }
            }
        });

        GLSurfaceView glView = new GLSurfaceView(this);
        glView.setZOrderMediaOverlay(true);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        _overlay=new Overlay();
        glView.setRenderer(_overlay);
        LayoutParams layout=new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
        addContentView(glView, layout);
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices (such as the camera), etc.
     */
    @Override
    protected void onResume() {
        if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "onResume()");
        super.onResume();

        synchronized (this) {
            weHaveBeenResumed = true;

            if (existingSurfaceHasSize && !cameraIsActive) {
                if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Surface already exists so create and start the camera.");
                createCamera();
                startCamera();
            } else {
                if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "No surface yet so don't create the camera.");
            }
        }
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into the background, but has not (yet) been killed. The counterpart to onResume().
     */
    @Override
    protected void onPause() {
        if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "onPause()");
        super.onPause();

        // Stop camera - synchronized to avoid races
        synchronized (this) {
            weHaveBeenResumed = false;
            stopAndReleaseCamera();
        }
    }

    /**
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    /**
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    private void createCamera() {
        if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "createCamera()");

        camera = Camera.open();

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            throw new RuntimeException("Failed to set camera preview surface!", e);
        }
    }

    /**
     *
     */
    private void startCamera() {
        if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "startScanner()");

        Camera.Parameters parameters = camera.getParameters();

        //Attempt to set up camera with VGA preview frame output or as close as possible
        ResolutionOptions preOptions = new ResolutionOptions(parameters.get("preview-size-values"));
        if (preOptions.getSize()>0) {
            ResolutionOptions.Option selected = preOptions.leastDifference(240, 320);
            preWidth = selected.getWidth();
            preHeight = selected.getHeight();
            Log.d(TAG, "Preview setup as "+preWidth+"x"+preHeight);
        } else {
            // Make sure we are multiples of 8
            preWidth = (preWidth>>3)<<3;
            preHeight = (preHeight>>3)<<3;
        }
        parameters.setPreviewSize(preWidth, preHeight);

        parameters.setFocusMode(FOCUS_CONTINUOUS_AUTO);

        if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Preview setup as "+preWidth+"x"+preHeight);

        try{
            camera.setParameters(parameters);
        }catch(Exception e) {
            //continuous autofocus not supported, set macro instead
            focusAuto = true;
            try{
                parameters.setFocusMode(Parameters.FOCUS_MODE_MACRO);
                camera.setParameters(parameters);
            }catch(Exception e2) {
                //macro not supported either, use normal autofocus triggered by user's tap
                parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
                camera.setParameters(parameters);
            }
        }
        camera.startPreview();
        cameraIsActive=true;

        //this doesn't exist in older APIs, but we only use it on newer APIs
        //Set 'focused' boolean to indicate whether we are in the middle of a continuous autofocus cycle
        //If we are in the middle of a cycle, image is likely to be unfocused - therefore we wait until the end of the cycle
        if(!focusAuto) {
            contAutoFocusCallback = new Camera.AutoFocusMoveCallback() {

                @Override
                public void onAutoFocusMoving(boolean start, Camera camera) {
                    if(start) {
                        focused = false;
                    }else{
                        focused = true;
                    }
                }
            };
            camera.setAutoFocusMoveCallback((Camera.AutoFocusMoveCallback) contAutoFocusCallback);
        }

        camera.setPreviewCallback(previewCallback);
    }

    /**
     *
     */
    private void stopAndReleaseCamera() {
        if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "stopScannerReleaseCamera()");

        cameraIsActive=false;
        if (camera!=null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera=null;
        } else {
            if (Log.isLoggable(TAG, Log.WARN)) Log.w(TAG, "No camera to stop - can't have been built yet.");
        }
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] yuv, Camera camera) {

            ByteBuffer buffer = ByteBuffer.allocateDirect(preWidth*preHeight);
            buffer.put(yuv,0,preWidth*preHeight);
            Log.d(TAG, "Calling processBytes");
            JOVX.processBytes(buffer, preWidth, preHeight, 1);
            Log.d(TAG, "Done processBytes");
            buffer.clear();
            buffer.get(yuv,0,preWidth*preHeight);

            int[] ex = new int[50000];
            int[] ey = new int[50000];
            int numEdges = 0;
            for(int y=0;y<preHeight;y++) {
                for(int x=0;x<preWidth;x++) {
                    if (yuv[x+y*preWidth]!=0) {
                        ex[numEdges]=x;
                        ey[numEdges]=y;
                        numEdges++;
                    }
                }
            }

            float ws=(float)disWidth/(float)preWidth;
            float hs=(float)disHeight/(float)preHeight;
            _overlay.updateEdgeMap(ex,ey,numEdges,ws,hs);
        }
    };
}
