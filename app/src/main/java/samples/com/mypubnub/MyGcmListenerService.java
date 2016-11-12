package samples.com.mypubnub;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        JSONObject jsonObject = new JSONObject();
        final Set<String> keys = data.keySet();
        Bundle notif = null;
        for (String key : keys) {
            try {
                if (key.equals("notification")) {
                    notif = data.getBundle(key);
                }
                if (key.equals("title")) {
                    jsonObject.put("title", data.getString("title"));
                }
                if (key.equals("body")) {
                    jsonObject.put("text", data.getString("body"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (notif != null) {
            Log.d(TAG, "onMessageReceived: " + notif.getString("body"));
            try {
                jsonObject.put("text", notif.getString("body"));
                jsonObject.put("title", notif.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "Sending JSON:"+jsonObject.toString());

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(jsonObject);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(JSONObject message) {


        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notificationIntent.putExtra("extras",message.optString("extra").toString());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setContentTitle(message.optString("title"))
                .setContentText(message.optString("text"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}