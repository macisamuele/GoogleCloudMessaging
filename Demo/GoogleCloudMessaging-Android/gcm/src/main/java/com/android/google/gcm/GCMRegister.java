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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Utility class that allows to simplify the procedures of registration, checking and storing of the
 * registration id for the GCM service.
 */
public class GCMRegister {

    static final String TAG = GCMRegister.class.getSimpleName();

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private static SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(GCMRegister.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * If result is empty, the app needs to register.
     *
     * @param context application's context
     * @return registration ID, or empty string if there is no existing registration ID.
     */
    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     *
     * @param context application's context
     * @param senderID project number assigned to your project from Google platform
     * @param onRegisterCallback callback to allow code execution after the registration ({@code null} to execute nothing)
     */
    public static void register(@NonNull final Context context, @NonNull final String senderID, final OnRegisterCallback onRegisterCallback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context.getApplicationContext());
                    String regid = gcm.register(senderID);
                    Log.d(TAG, "Device registered, registration ID=" + regid);
                    storeRegistrationId(context, regid);
                    if(onRegisterCallback != null) {
                        onRegisterCallback.onRegister(regid);
                    }
                } catch (IOException ex) {
                    Log.d(TAG, "Error :" + ex.getMessage());
                }
                return null;
            }
        }.execute(null, null, null);
    }

    /**
     * Unregisters the application from GCM servers asynchronously.
     * Delete the stored registration ID and the app versionCode from
     * the application's shared preferences.
     *
     * @param context application's context
     * @param onUnregisterCallback callback to allow code execution after the un-registration ({@code null} to execute nothing)
     */
    public static void unregister(@NonNull final Context context, final OnUnregisterCallback onUnregisterCallback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context.getApplicationContext());
                    gcm.unregister();
                    removeRegistrationId(context);
                    Log.d(TAG, "Device unregistered");
                    if(onUnregisterCallback != null) {
                        onUnregisterCallback.onUnregister();
                    }
                } catch (IOException ex) {
                    Log.d(TAG, "Error :" + ex.getMessage());
                }
                return null;
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    /**
     * Remove the stored registration ID and the app versionCode from the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     */
    private static void removeRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PROPERTY_REG_ID);
        editor.remove(PROPERTY_APP_VERSION);
        editor.apply();
    }

    /**
     * @param context application's context.
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e); // should never happen
        }
    }

    /**
     * Callback for the registration phase
     */
    public interface OnRegisterCallback {
        /**
         * Perform any kind of operation after the successful registration.
         * Do not care about the permanent storing of the information, it is already performed
         *
         * @param registrationID registration identifier assigned from the Google Platform
         */
        public void onRegister(String registrationID);
    }

    /**
     * Callback for the registration phase
     */
    public interface OnUnregisterCallback {
        /**
         * Perform any kind of operation after the successful unregistration.
         * Do not care about the permanent storing of the information, it is already performed
         */
        public void onUnregister();
    }
}
