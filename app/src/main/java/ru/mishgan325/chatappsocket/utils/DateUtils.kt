import android.util.Log
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField

fun formatTimestampToTime(timestamp: String): String {
    return try {
        val formatter = DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
            .optionalEnd()
            .toFormatter()

        val utcDateTime = LocalDateTime.parse(timestamp, formatter)
        val zonedUtc = utcDateTime.atZone(ZoneOffset.UTC)
        val localDateTime = zonedUtc.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

        val now = LocalDate.now()
        val messageDate = localDateTime.toLocalDate()

        when {
            messageDate == now -> localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            messageDate.year == now.year -> localDateTime.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm"))
            else -> localDateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
        }
    } catch (e: DateTimeParseException) {
        Log.e("DateUtils", e.toString())
        timestamp
    }
}
