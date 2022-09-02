package com.snad.kommute

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Person
import android.content.Context
import android.content.Intent
import android.content.LocusId
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.snad.kommute.ui.KommuteActivity
import com.snad.kommute.R

internal object KommuteNotification {

    private const val CHANNEL_ID = "kommute_notification"

    fun send(context: Context) {

        val activityIntent = Intent(context, KommuteActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(context, 0, activityIntent, FLAG_IMMUTABLE)

        val bubbleData = Notification.BubbleMetadata.Builder(
            bubbleIntent,
            Icon.createWithResource(context, R.drawable.ic_notification_icon)
        )
            .setDesiredHeight(600)
            .build()

        val person = Person.Builder()
            .setName(context.getString(R.string.notification_name))
            .setImportant(true)
            .build()

        createShortcut(context, person)

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setContentIntent(bubbleIntent)
            .setContentTitle(context.getString(R.string.notification_name))
            .setContentText(context.getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setBubbleMetadata(bubbleData)
            .setOnlyAlertOnce(true)
            .addPerson(person)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setStyle(Notification.MessagingStyle(person))
            .setShortcutId(person.name.toString())
            .setLocusId(LocusId(person.name.toString()))
            .build()

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.notification_channel_description)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(R.id.network_sniffer_notification_id, notification)
    }

    private fun createShortcut(context: Context, person: Person) {
        val shortcutId = person.name.toString()

        val shortcut = ShortcutInfo.Builder(context, shortcutId)
            .setLocusId(LocusId(shortcutId))
            .setShortLabel(shortcutId)
            .setIcon(Icon.createWithResource(context, R.drawable.ic_notification_icon))
            .setLongLived(true)
            .setPerson(person)
            .setCategories(setOf(ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION))
            .setIntent(Intent(context, KommuteActivity::class.java).setAction(Intent.ACTION_VIEW))
            .build()

        val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
        shortcutManager.addDynamicShortcuts(listOf(shortcut))
    }
}
