package com.hosco.nextcrm.callcenter.linphone.utils

import android.content.Context
import com.hosco.nextcrm.callcenter.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimeCrmUtils {
    fun convertTime(context: Context, time: String?): String? {
        var timeAgo = time
        try {
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            //            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            val format1 = SimpleDateFormat("dd/MM/yyyy")
            val past = format.parse(time)
            val now = Date()
            val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
            val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
            val days = TimeUnit.MILLISECONDS.toDays(now.time - past.time)
            timeAgo = if (seconds < 60) {
                seconds.toString()
            } else if (minutes < 60) {
                minutes.toString() + " " + context.resources.getString(R.string.txt_minute)
            } else if (hours < 24) {
                hours.toString() + " " + context.resources.getString(R.string.txt_hour)
            } else if (days < 8) {
                days.toString() + " " + context.resources.getString(R.string.txt_day)
            } else {
                format1.format(past)
            }
        } catch (j: Exception) {
            j.printStackTrace()
        }
        return timeAgo
    }

    fun calculateTime(context: Context, seconds: Int): String {
        val sec = seconds % 60
        val minutes = seconds % 3600 / 60
        val hours = seconds % 86400 / 3600
        val days = seconds / 86400
        if (days > 0)
            return "${days} ".plus(context.getString(R.string.txt_day)).plus(" ${hours} ")
                .plus(context.getString(R.string.txt_hour)).plus(" ${minutes} ")
                .plus(context.getString(R.string.txt_minute)).plus(" ${sec} ")
                .plus(context.getString(R.string.txt_second))
        else if (hours > 0)
            return "${hours} ".plus(context.getString(R.string.txt_hour)).plus(" ${minutes} ")
                .plus(context.getString(R.string.txt_minute)).plus(" ${sec} ")
                .plus(context.getString(R.string.txt_second))
        else if (minutes > 0)
            return "${minutes} "
                .plus(context.getString(R.string.txt_minute)).plus(" ${sec} ")
                .plus(context.getString(R.string.txt_second))
        else
            return " ${sec} "
                .plus(context.getString(R.string.txt_second))

        println("Day $days Hour $hours Minute $minutes Seconds $sec")
    }
}