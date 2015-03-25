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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Generalization of the {@code GcmBroadcastReceiver} provided with the Android Demo on GCM.
 * It allows to define a correct broadcast receiver for the GCM service simply defining the name of the class
 * that extends the {@code GCMBaseBroadcastReceiver}.
 * See <a href="http://developer.android.com/google/gcm/client.html#sample-receive">Google's Implementation</a>
 * Example of use:
 * <pre>{@code
 * import android.gcm.GCMBaseBroadcastReceiver;
 * public class GcmBroadcastReceiver extends GCMBaseBroadcastReceiver {
 *      &#64;Override
 *      public String getIntentServiceHandlerName() {
 *          return IntentService.class.getName();
 *      }
 * }
 * }</pre>
 */
public abstract class GCMBaseBroadcastReceiver extends WakefulBroadcastReceiver {

    /**
     * @return the class that will operates as IntentService (extension of {@code GCMBaseIntentService})
     */
    public abstract Class getIntentServiceHandlerClass();

    @Override
    public final void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), getIntentServiceHandlerClass().getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
