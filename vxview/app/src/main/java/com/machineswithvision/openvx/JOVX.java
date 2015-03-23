package com.machineswithvision.openvx;

import java.nio.ByteBuffer;

/**
 * Created by Anthony on 18/03/15.
 */
public class JOVX {
    public static native void jovxCreateContext();
    public static native void processBytes(ByteBuffer buffer, int width, int height, int depth);
    public static native void jovxReleaseContext();
}
