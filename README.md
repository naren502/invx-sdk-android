![invx](mwv-invx.png)

InVX SDK for Android Version 1.0.0
==================================

The InVX SDK enables the development of OpenVX applications for Android devices. The SDK comprises the invx library (pre-built shared libraries and header files) and an example OpenVX application, VXView. 

invx
----

The invx directory contains the pre-built shared libraries and header files for the invx inmplementation of the openvx API.

Consult VERSION.txt to confirm the bundled libraries release. 

vxview
------

The vxview directory contains the Android Studio project for VXView. VXView is an example OpenVX application that captures live video using the camera, processes frames using an OpenVX implementation of the Canny edge operator and then displays the output on screen.

To build and run VXView:

1. git clone the invx-sdk-android repository
2. Put a copy of the invx directory into vxview/app/src/main (or make a symbolic link to invx here)
3. Inside the invx directory you have just copied there is a sub-directory called ‘res' and inside this there is a sub-directory ‘raw'. Copy the directory ‘raw' to vxview/app/src/main/res
4. Open the project in Android Studio File->Open selecting the vxview directory
5. Either connect a device to your development machine using a USB cable or prepare an Android Virtual Device for testing.
6. You can now simply run the project. This will build the application, install it on your device or emulator and run it.



