package utils.convertors

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

object TimeUtils {
    fun getCurrentTimeInMillis(): Long {
        val zoneId = ZoneId.of("Asia/Kolkata") // IST timezone
        return ZonedDateTime.now(zoneId).toInstant().toEpochMilli()
    }
    // Function to get a string representation of the current date and time in IST
    fun getDateTimeString(time:Long): String {
        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(time), TimeZone.getDefault().toZoneId());
        val formatter = DateTimeFormatter.ofPattern("h:mm a d MMMM yyyy")
        return localDateTime.format(formatter)
    }
}
