package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.NotificationPreferences
import com.example.data.model.Trip

object Ra7alatNotificationManager {
    private const val CHANNEL_ID = "ra7alat_channel"
    private const val CHANNEL_NAME = "إشعارات رحلات الجزائر"
    private const val CHANNEL_DESC = "تنبيهات الرحلات الجديدة والتحديثات"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    fun matchesPreferences(trip: Trip, prefs: NotificationPreferences, userEmail: String = ""): Boolean {
        if (!prefs.notificationsEnabled) return false

        // Do not notify user about their own trip
        if (userEmail.isNotBlank() && trip.organizerEmail.isNotBlank() &&
            trip.organizerEmail.trim().equals(userEmail.trim(), ignoreCase = true)
        ) {
            return false
        }

        // 1. Wilayas de départ souhaitées
        // If empty: all wilayas are accepted
        if (prefs.departureWilayas.isNotEmpty()) {
            val tripDepClean = AlgeriaConstants.cleanWilayaName(trip.departureWilaya).trim()
            val matchesDep = prefs.departureWilayas.any { w ->
                val cleanW = AlgeriaConstants.cleanWilayaName(w).trim()
                tripDepClean.contains(cleanW, ignoreCase = true) ||
                        cleanW.contains(tripDepClean, ignoreCase = true) ||
                        trip.departureWilaya.contains(cleanW, ignoreCase = true) ||
                        cleanW.contains(trip.departureWilaya, ignoreCase = true) ||
                        trip.departureCity.contains(cleanW, ignoreCase = true)
            }
            if (!matchesDep) return false
        }

        // 2. Types de voyages souhaités
        // If empty: all types are accepted
        if (prefs.tripTypes.isNotEmpty()) {
            val tripTypeNorm = trip.tripType.replace(Regex("[^\\p{L}\\p{Nd}\\s]"), "").trim()
            val matchesType = prefs.tripTypes.any { t ->
                val cleanT = t.replace(Regex("[^\\p{L}\\p{Nd}\\s]"), "").trim()
                val parts = cleanT.split(" ").filter { it.length > 2 }
                parts.any { p -> tripTypeNorm.contains(p, ignoreCase = true) } ||
                        tripTypeNorm.contains(cleanT, ignoreCase = true) ||
                        cleanT.contains(tripTypeNorm, ignoreCase = true)
            }
            if (!matchesType) return false
        }

        // 3. Budget maximum (DZD)
        val maxPrice = prefs.maxPrice
        if (maxPrice != null && maxPrice > 0.0 && trip.priceDzd > maxPrice) {
            return false
        }

        return true
    }

    fun sendTripNotification(context: Context, trip: Trip) {
        initNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_TRIP_ID", trip.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            trip.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val priceFormatted = if (trip.priceDzd > 0.0) "${trip.priceDzd.toInt()} دج" else "مجانية"
        val notifTitle = "رحلة جديدة: ${trip.title} 🌄"
        val notifContent = "انطلاقاً من ${trip.departureWilaya} | ${trip.tripType} | $priceFormatted"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notifTitle)
            .setContentText(notifContent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$notifContent\nالوجهة: ${trip.destinationWilaya} • التاريخ: ${trip.departureDate}\nاضغط للاطلاع على التفاصيل والحجز فوراً!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(trip.id.toInt(), builder.build())
        } catch (ignored: SecurityException) {
        }
    }

    fun sendTestNotification(context: Context, prefs: NotificationPreferences) {
        initNotificationChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val depSummary = if (prefs.departureWilayas.isEmpty()) "جميع الولايات" else prefs.departureWilayas.joinToString(", ") { AlgeriaConstants.cleanWilayaName(it) }
        val typesSummary = if (prefs.tripTypes.isEmpty()) "كافة الأنواع" else prefs.tripTypes.joinToString(", ")
        val priceSummary = if (prefs.maxPrice == null || prefs.maxPrice <= 0.0) "أي ميزانية" else "حتى ${prefs.maxPrice.toInt()} دج"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("تنبيهات رحلات الجزائر نشطة بنجاح 🔔🇩🇿")
            .setContentText("انطلاق: $depSummary • $typesSummary ($priceSummary)")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("✅ تفضيلات التنبيهات المخصصة نشطة الآن بنجاح:\n• مكان الانطلاق: $depSummary\n• نوع الرحلات: $typesSummary\n• الحد الأقصى للميزانية: $priceSummary\n\nستصلك إشعارات بالرحلات المطابقة فور نشرها!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(9999, builder.build())
        } catch (ignored: SecurityException) {
        }
    }
}
