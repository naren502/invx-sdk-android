//
//  ResolutionOptions.java
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import android.util.Log;
/**
 * Created by Anthony on 19/02/2015.
 */
public class ResolutionOptions {
    // Too see all logging use "adb shell setprop log.tag.ResolutionOptions DEBUG" - default is WARN and above
    private static final String TAG = "ResolutionOptions";

    private static final Pattern COMMA = Pattern.compile(",");

    private Option[] options;

    public ResolutionOptions(String resolutionList) {
        if (Log.isLoggable(TAG, Log.DEBUG)) Log.d(TAG, "Initialised with: "+resolutionList);

        if (resolutionList==null) {
            options = new Option[0];
        } else {
            List<Option> temp = new LinkedList<Option>();

            for (String size : COMMA.split(resolutionList)) {
                size = size.trim();

                int xPos = size.indexOf('x');
                int width = Integer.parseInt(size.substring(0, xPos));
                int height = Integer.parseInt(size.substring(xPos+1));

                temp.add(new Option(width,height));
            }

            if (temp.size()>=2 && temp.get(0).getWidth()<temp.get(temp.size()-1).getWidth()) {
                Collections.reverse(temp);
            }

            options = temp.toArray(new Option[temp.size()]);
        }
    }

    public int getSize() {
        return options.length;
    }

    public Option biggestWithin(int width, int height) {
        Option result = null;

        for (int i=0; i<options.length && result==null; i+=1) {
            if (options[i].getWidth()<=width && options[i].getHeight()<=height) {
                result = options[i];
            }
        }

        return result;
    }

    public Option leastDifference(int width, int height) {
        Option result = null;
        int bestDiff = Integer.MAX_VALUE;

        for (int i=0; i<options.length; i+=1) {
            int newDiff = Math.abs(options[i].getWidth()-width)+Math.abs(options[i].getHeight()-height);
            if (newDiff<bestDiff) {
                bestDiff=newDiff;
                result = options[i];
            }
        }

        return result;
    }

    public static class Option {
        private int width;
        private int height;

        public Option(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
