# Exercise Analyst Android Application

### Requirements
* Android mobile device with OS version >= Android 8.0 Oreo
* [MetaMotionR device](https://mbientlab.com/metamotionr/#:~:text=The%20MetaMotionR%20(MMR)%20is%20a,in%2Ddepth%20analysis%20and%20visualization.)

### Running
1. Get the app:
a) Get source code from the repository and build (repo uses git-lfs, so after cloning command: "git-lfs install && git-lfs pull" must be run)
b) alternatively - download a precompiled APK
2. Build
3. Deploy to a mobile device
4. Download [nRFConnect](https://play.google.com/store/apps/details?id=no.nordicsemi.android.mcp&hl=en&gl=US) application
5. Enable bluetooth and location on the mobile
6. Open nRFConnect app and scan for MetaWear devices
7. Save the MetaWear device's MAC address for later use
8. Open ExerciseAnalyst application
9. Type the saved MAC address
10. Press start MetaMotionService button (it also starts a new session)
11. Accept permissions request
12. Wait and observe toast messages
13. If no issues in Toast messages then the app is collecting data in background from the MetaWear device
14. To stop press Stop MetaMotionService button

### Development Hints
1. Android Studio provides Database Inspector which is very handy for Room Database checks
2. During development the Room Database schema might change, it can cause compatibility issues
a) Use debug button WIPE DB to nuke you database
b) If the above doesn't help then try increasing DB version in the code (MeasurementsDatabase class)

### Current state of the app
##### What's in the app:
* Basic app - activity
* Foreground service with PartialWakeLock for continuous data collection, which is connected with MetaMotionBLE service
* Sensors data collection: sensors factory with configuration
* Room database with RxJava handling of async operations
* Debug option to wipe DB
* Has necessary android framework boilerplate code, conforms with MVVM architectural design pattern

##### Known issues and limitations:
* App is not robustly tested against sudden state changes including rotations
* It stores data but does not provide any convenient way to retrieve it
* When user stops the service up to 1000 recent measurements will be lost. It is caused by measurements buffer which is not flushed on service termination
* There should be implemented feature for runtime BLE devices scanning and selection. Without scanning devices advertisements there might be a problem with connecting
* App has problems with connection to MetaMotion devices, those are resolved when nrfConnect app is used for advertisement scanning prior to using this app
* BLE is prone to sudden disconnections - it is not handled
* This version only collects data and stores it. It does not allow to tag it

### Troubleshooting
* If the app doesn't grant required permissions, grant them manually in the mobile's applications settings
* If there is a problem with connecting to a MetaWear device, try the belows:
  * Make sure Bluetooth and Location is enabled on the mobile
  * Run nRFConnect app and scan nearby devices prior to running this app
  * Make sure MAC address is correct
  * Make sure the MetaWear device is charged, the same for the mobile

### Contributing
* Please follow general Java coding conventions
* Please follow MVVM architectural design pattern which is the recommended way of creating Android applications
  * [Google's app architecture guide](https://developer.android.com/jetpack/guide)
  * [Create Android app with MVVM pattern simply using Android Architecture Component](https://medium.com/hongbeomi-dev/create-android-app-with-mvvm-pattern-simply-using-android-architecture-component-529d983eaabe)
