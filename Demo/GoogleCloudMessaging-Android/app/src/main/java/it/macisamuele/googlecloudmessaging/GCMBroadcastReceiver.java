package it.macisamuele.googlecloudmessaging;

import com.android.google.gcm.GCMBaseBroadcastReceiver;
import com.android.google.gcm.GCMBaseIntentService;

/**
 * Created by samuele on 23/03/15.
 */
public class GCMBroadcastReceiver extends GCMBaseBroadcastReceiver {

    @Override
    public Class getIntentServiceHandlerClass() {
        return GCMIntentService.class;
    }
}
