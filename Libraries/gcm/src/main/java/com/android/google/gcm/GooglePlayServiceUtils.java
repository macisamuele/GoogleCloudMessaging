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
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GooglePlayServiceUtils {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = GooglePlayServiceUtils.class.getSimpleName();

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't and {@code showErrorDialog} is trye, display a dialog that
     * allows users to download the APK from the Google Play Store or
     * enable it in the device's system settings.
     *
     * @param activity        activity's context
     * @param showErrorDialog true if you want to show the error dialog if GooglePlay Services are not available
     * @return the state of the PlayServices
     */
    public static boolean checkPlayServices(Activity activity, boolean showErrorDialog) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                if (showErrorDialog) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    Log.i(TAG, "Recoverable error, you should show the dialog.");
                }
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
     * @param activity activity's context
     * @return the state of the PlayServices
     */
    public static boolean checkPlayServices(Activity activity) {
        return checkPlayServices(activity, true);
    }

}
