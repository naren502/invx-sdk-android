//
//  JOVX.java
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
