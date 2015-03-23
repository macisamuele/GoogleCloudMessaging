/**
 * Copyright 2015 Samuele Maci (macisamuele@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.google.gcm;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * An Application wrapper that perform the required operations to initialize the device with
 * the GoogleCloudMessaging infrastructure.
 *
 * It automatically checks the availability of Play Services and if there is any
 * registration ID defined it perform the registration request.
 *
 * In order to fully exploit the GCM service you should follow the initial setup like explained
 * in <a href="http://developer.android.com/google/gcm/gcm.html">GCM</a>.
 *
 * You need to extends two abstract classes:
 * <ol>
 *     <li>
 *         {@link GCMBaseBroadcastReceiver}: receives the
 *         broadcast messages and forward them to the {@code IntentService} class
 *     </li>
 *     <li>
 *         {@link GCMBaseIntentService}: that defines the
 *         operations to be performed when a GCM message is received
 *     </li>
 * </ol>
 *
 * Modify the build.gradle file adding your
 * AndroidManifest requirements:
 * <ol>
 *     <li>
 *         {@code minSdkVersion} should be greater than 8 (for GooglePlay Services)
 *     </li>
 *     <li>
 *         Custom permission: only this app can receive its messages
 *         <pre>
 *             {code <permission android:name="PACKAGE_NAME.permission.C2D_MESSAGE"
 *                      android:protectionLevel="signature" />}
 *         </pre>
 *         where PACKAGE_NAME is the package of your application (attribute of the manifest file)
 *     </li>
 *     <li>
 *         Permissions:
 *         <ol>
 *             <li>
 *                 {@code android.permission.INTERNET} to allow connection to Google Services
 *             </li>
 *             <li>
 *                 {@code android.permission.GET_ACCOUNTS} since is required a Google account
 *             </li>
 *             <li>
 *                 {@code android.permission.WAKE_LOCK} Keeps the processor from sleeping when a message is received.
 *             </li>
 *             <li>
 *                 {@code com.google.android.c2dm.permission.RECEIVE} to allow to register and receive data messages
 *             </li>
 *             <li>
 *                 {@code PACKAGE_NAME.permission.C2D_MESSAGE} to allow the receiving of messages
 *             </li>
 *         </ol>
 *     </li>
 *     <li>
 *         Meta-Data into application tag
 *         <pre>
 *             {@code <meta-data android:name="com.google.android.gms.version"
 *                  android:value="@integer/google_play_services_version" />}
 *         </pre>
 *     </li>
 *     <li>
 *         Receiver tag
 *         <pre>
 *              {@code <receiver android:name="BROADCAST_RECEIVER_CLASS"
 *                  android:permission="com.google.android.c2dm.permission.SEND">
 *                          <intent-filter>
 *                              <action android:name="com.google.android.c2dm.intent.RECEIVE" />
 *                              <category android:name="PACKAGE_NAME" />
 *                          </intent-filter>
 *                      </receiver>}
 *         </pre>
 *         where BROADCAST_RECEIVER_CLASS is the extended class of {@code GCMBaseBroadcastReceiver}
 *         and PACKAGE_NAME is the package of your application (attribute of the manifest file)
 *     </li>
 *     <li>
 *         Service tag
 *         {@code <service android:name="INTENT_SERVICE_CLASS" />}
 *         where INTENT_SERVICE_CLASS is the extended class of {@code GCMBaseIntentService}
 *     </li>
 * </ol>
 */
/* Manifest skeleton
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="PACKAGE_NAME" android:versionCode="1" android:versionName="1.0">
    <uses-sdk android:minSdkVersion="8" android:targetSdkVersion="TARGET_VERSION" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <permission android:name="PACKAGE_NAME.permission.C2D_MESSAGE" android:protectionLevel="signature" />
    <uses-permission android:name="android.gcm.demo.app.permission.C2D_MESSAGE" />
    <uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
    <application
        android:name="GCMReadyApplication_CLASS">
        <meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" />

        <activity android:name=".Activity_CLASS">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <receiver android:name="GcmBroadcastReceiver_CLASS" android:permission="com.google.android.c2dm.permission.SEND">
            <intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <category android:name="android.gcm.demo.app" />
            </intent-filter>
        </receiver>
        <service android:name="IntentService_CLASS" />
    </application>
</manifest>
 */
public class GCMReadyApplication extends Application {

    private static final String TAG = GCMReadyApplication.class.getSimpleName();

    public GCMReadyApplication() {
        registerActivityLifecycleCallbacks(acttivityLifeCicleCallback);
    }

    private final ActivityLifecycleCallbacks acttivityLifeCicleCallback = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
            if (GooglePlayServiceUtils.checkPlayServices(activity)) {
                String regid = GCMRegister.getRegistrationId(getApplicationContext());
                Log.d(TAG, regid);
                if (regid.isEmpty()) {
                    GCMRegister.register(getApplicationContext(), BuildConfig.SENDER_ID_GCM);
                }
            } else {
                Log.i(TAG, "No valid Google Play Services APK found.");
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            GooglePlayServiceUtils.checkPlayServices(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };

}
