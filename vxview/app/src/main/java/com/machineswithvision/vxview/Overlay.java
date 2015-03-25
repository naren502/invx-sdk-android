//
//  Overlay.java
//
//  Created by Anthony Ashbrook on 25/03/2015.
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

public class Overlay implements Renderer {

    private static int MAX_POINTS = 50000;

    private FloatBuffer _vertexBuffer=null;
    private int _numVertices=0;

    float[] vertices=new float[MAX_POINTS];


    @Override
    public synchronized void onDrawFrame(GL10 gl) {

        if (_vertexBuffer==null) {
            ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length*8);
            bb.order(ByteOrder.nativeOrder());
            _vertexBuffer=bb.asFloatBuffer();
        }
        _vertexBuffer.position(0);

        // Clears the screen and depth buffer.
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // Tell GL about our vertex array
        gl.glVertexPointer(2/*size per vertex*/, GL10.GL_FLOAT, 0, _vertexBuffer);
        gl.glPointSize(1);
        gl.glDrawArrays(GL10.GL_POINTS, 0, _numVertices);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);// OpenGL docs.
        gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
        gl.glLoadIdentity();// OpenGL docs.
        gl.glOrthof(0,(float)width,(float)height, 0, 0, 1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
        gl.glLoadIdentity();// OpenGL docs.
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);  // OpenGL docs.
    }

    public synchronized void updateEdgeMap(int[] edgesX, int[] edgesY, int numEdges, float ws, float hs) {
        _vertexBuffer.position(0);
        _numVertices=0;
        for(int i=0;i<numEdges&&i<MAX_POINTS;i++) {
            _vertexBuffer.put((float)edgesX[i]*ws);
            _vertexBuffer.put((float)edgesY[i]*hs);
            _numVertices++;
        }
        _vertexBuffer.position(0);
    }

    public synchronized void updateEdgeMap() {
        _vertexBuffer.position(0);
        _numVertices = 0;
        for(int i=0;i<100;i++) {
            _vertexBuffer.put((float)i);
            _vertexBuffer.put((float)i);
            _numVertices++;
        }
        _vertexBuffer.position(0);
    }
}
