package com.snad.kommute

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BubbleMetadata
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.Person
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.snad.kommute.ui.KommuteActivity

internal object KommuteNotification {

    private const val CHANNEL_ID = "kommute_notification"

    fun send(context: Context) {

        val intentFlag = when {
            Build.VERSION.SDK_INT >= 33 -> FLAG_MUTABLE or FLAG_UPDATE_CURRENT
            Build.VERSION.SDK_INT >= 23 -> FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
            else -> FLAG_UPDATE_CURRENT
        }

        val activityIntent = Intent(context, KommuteActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(context, 0, activityIntent, intentFlag)

        val bubbleData = BubbleMetadata.Builder(
            bubbleIntent,
            IconCompat.createWithResource(context, R.drawable.ic_kommute_notification_icon)
        )
            .setDesiredHeight(600)
            .build()

        val person = Person.Builder()
            .setName(context.getString(R.string.kommute_notification_name))
            .setImportant(true)
            .build()

        createShortcut(context, person)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentIntent(bubbleIntent)
            .setContentTitle(context.getString(R.string.kommute_notification_name))
            .setContentText(context.getString(R.string.kommute_notification_text))
            .setSmallIcon(R.drawable.ic_kommute_notification_icon)
            .setBubbleMetadata(bubbleData)
            .setOnlyAlertOnce(true)
            .addPerson(person)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setStyle(NotificationCompat.MessagingStyle(person))
            .setShortcutId(person.name.toString())
            .setLocusId(LocusIdCompat(person.name.toString()))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.kommute_notification_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.kommute_notification_channel_description)
            }

            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(R.id.network_sniffer_notification_id, notification)
    }

    private fun createShortcut(context: Context, person: Person) {
        val shortcutId = person.name.toString()

        val shortcut = ShortcutInfoCompat.Builder(context, shortcutId)
            .setLocusId(LocusIdCompat(shortcutId))
            .setShortLabel(shortcutId)
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_kommute_notification_icon))
            .setLongLived(true)
            .setPerson(person)
            .setIsConversation()
            .setIntent(Intent(context, KommuteActivity::class.java).setAction(Intent.ACTION_VIEW))
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    }
}
