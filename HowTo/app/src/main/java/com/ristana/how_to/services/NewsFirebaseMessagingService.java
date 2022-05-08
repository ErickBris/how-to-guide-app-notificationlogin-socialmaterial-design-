package com.ristana.how_to.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ristana.how_to.R;
import com.ristana.how_to.manager.PrefManager;
import com.ristana.how_to.ui.activity.GuideActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hsn on 28/03/2017.
 */
public class NewsFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    Bitmap bitmap;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        //Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.

        //message will contain the Push Message

        //If the key AnotherActivity has  value as True then when the user taps on notification, in the app AnotherActivity will be opened.
        //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.
        //To get a Bitmap image from the URL received
        String id = remoteMessage.getData().get("id");
        String title = remoteMessage.getData().get("title");
        String time = remoteMessage.getData().get("time");
        String category=  remoteMessage.getData().get("category");
        String imageUri = remoteMessage.getData().get("image");
        PrefManager prf = new PrefManager(getApplicationContext());
        if (!prf.getString("notifications").equals("false")){
            sendNotification(id,title, imageUri,time,category);
        }
    }

    private void sendNotification(String id,String title, String  imageUri, String time,String category) {


        Bitmap image = getBitmapfromUrl(imageUri);
        Intent intent = new Intent(this, GuideActivity.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));


        intent.putExtra("id_guide",id);
        intent.putExtra("image_guide",imageUri);
        intent.putExtra("time_guide",time);
        intent.putExtra("title_guide",title);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.custom_notification);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);


        remoteViews.setImageViewBitmap(R.id.image_view_notif_item,image);
        remoteViews.setTextViewText(R.id.text_View_title_notif_item,title);
        remoteViews.setTextViewText(R.id.text_view_time_notif_item,time);
        if (category==null){
            remoteViews.setTextViewText(R.id.text_view_category_notif_item,getResources().getString(R.string.global));
        }else{
            remoteViews.setTextViewText(R.id.text_view_category_notif_item,category);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(image)/*Notification icon image*/
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setCustomBigContentView(remoteViews)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(Integer.parseInt(id) /* ID of notification */, notificationBuilder.build());
    }
    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }


}