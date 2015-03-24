invx-sdk-android Version 1.0
============================

The InVX SDK enables the development of OpenVX applications for Android devices. The SDK comprises the invx library (pre-built shared libraries and header files) and an example OpenVX application, VXView. 

invx
----

The invx directory contains the pre-built shared libraries and header files for the invx inmplementation of the openvx API. 

vxview
------

The vxview directory contains the Android Studio project for VXView. VXView is an example OpenVX application that captures live video using the camera, processes frames using an OpenVX implementation of the Canny edge operator and then displays the output on screen.

To build and run VXView:

1. git clone the invx-sdk-android repository
2. Put a copy of the invx directory into vxview/app/src/main/jin (or make a symbolic link to invx here)
3. Open the project in Android Studio File->Open and select the vxview directory
4. Run the app with Run->Run 'app'

[comment on behaviour of the emulator]
