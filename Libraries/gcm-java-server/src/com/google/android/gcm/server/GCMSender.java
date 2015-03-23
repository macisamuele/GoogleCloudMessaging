package com.google.android.gcm.server;


/**
 * Singleton Interface of a GCM Sender
 */
public class GCMSender {

    private static Sender instance;

    /**
     * Return the {@code Sender} instance stored
     * 
     * @throws IllegalStateException if the instance wasn't already defined with the
     *             {@code getInstance(String)}
     */
    public static Sender getInstance() {
        if (instance != null) {
            return instance;
        }
        else {
            throw new IllegalStateException(
                    "Cannot extract instance if it is not yet initialized with the method getInstance(String)");
        }
    }

    /**
     * Return the {@code Sender} instance stored, if there are any stored instance it initialize the
     * instance with the {@code senderKey}
     * 
     * @param senderKey API KEY of the Cloud Messaging service (further information in
     *            http://developer.android.com/google/gcm/http.html)
     */
    public static Sender getInstance(String senderKey) {
        if (instance == null) {
            instance = new Sender(senderKey);
        }
        return instance;
    }
}
