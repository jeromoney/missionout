package com.beaterboofs.missionout

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.beaterboofs.missionout.FirestoreRemoteDataSource.sendTokenToServer
import com.beaterboofs.missionout.Util.SharedPrefUtil.getToken
import com.beaterboofs.missionout.Util.SharedPrefUtil.setToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MissionFirebaseMessagingService : FirebaseMessagingService() {
    private val CHANNEL_ID = "1234"


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.alarm_channel_name)
            val descriptionText = getString(R.string.alarm_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        // Uh Oh -- FCM received so there must be a mission.
        // Display a notification, play sound and allow user to dismiss sound
        createNotificationChannel()
        val header = p0.data["description"]
        val body = p0.data["action"]
        val docId = p0.data["docId"]
        val notificationId = 12344
        // Create an explicit intent for an Activity in your app
        // TODO - create deep link to detail page
        val uri = "missionout://www.beaterboofs.com/${docId}"


        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        val pendingIntent = PendingIntent.getActivity(baseContext, 0, notificationIntent,0)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // play sound
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val channel : NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            channel = NotificationChannel(CHANNEL_ID, "some name", NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.GRAY
                enableLights(true)
                description = "SOME DESCRIPTION"
            }
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.setSound(defaultSoundUri, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle(header)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(pendingIntent)
            .setSound(defaultSoundUri)


        // show notifcation
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }



    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val oldToken = getToken(applicationContext)
        setToken(applicationContext, token)
        val userUID = FirebaseAuth.getInstance().uid!!
        GlobalScope.launch {
            sendTokenToServer(userUID, token, oldToken)
        }
    }
}