import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class DateTimeTools {
    public static long secondsBetween(LocalDateTime timeA, LocalDateTime timeB) {
        Duration d = Duration.between(timeA, timeB);
        return d.toSeconds();
    }

    public static String dateToString(LocalDateTime t) {
        int[] needsParsing = {
            t.getMonthValue(), t.getDayOfMonth(), t.getHour(), t.getMinute(), t.getSecond()
        };

        String[] parsed = new String[needsParsing.length+1];
        parsed[0] = String.valueOf(t.getYear()); //year needs no formatting unless its like 300 AD or something but that won't happen cos the internet did not exist then
        
        for (int i = 1; i < parsed.length; i++) {
            if (needsParsing[i-1] < 10) {
                parsed[i] = "0" + needsParsing[i-1]; //for example, seconds value is 3 so turns it into "03"
            } else {
                parsed[i] = String.valueOf(needsParsing[i-1]);
            }
        }

        //yyyy-MM-dd HH:mm:ss, 24h format
        return String.format(
            "%s-%s-%s %s:%s:%s", parsed[0], parsed[1], parsed[2], parsed[3], parsed[4], parsed[5]
        );
    }

    public static LocalDateTime stringToDate(String s) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(s, format);
    }
}