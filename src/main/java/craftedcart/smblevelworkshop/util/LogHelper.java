package craftedcart.smblevelworkshop.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 * Created on 14/03/2016 (DD/MM/YYYY)
 */
public class LogHelper {

    public static List<LogEntry> log = new ArrayList<>();

    private static void log(Class clazz, Level logLevel, Object object) {
        LogManager.getLogger(clazz).log(logLevel, object);
        log.add(new LogEntry(clazz, logLevel, object));
        if (log.size() > 1024) {
            log.remove(0);
        }
    }

    public static void all(Class clazz, Object object) {
        log(clazz, Level.ALL, object);
    }

    public static void fatal(Class clazz, Object object) {
        log(clazz, Level.FATAL, object);
    }

    public static void error(Class clazz, Object object) {
        log(clazz, Level.ERROR, object);
    }

    public static void warn(Class clazz, Object object) {
        log(clazz, Level.WARN, object);
    }

    public static void info(Class clazz, Object object) {
        log(clazz, Level.INFO, object);
    }

    public static void debug(Class clazz, Object object) {
        log(clazz, Level.DEBUG, object);
    }

    public static void trace(Class clazz, Object object) {
        log(clazz, Level.TRACE, object);
    }

    public static void off(Class clazz, Object object) {
        log(clazz, Level.OFF, object);
    }

    public static String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("    at ");
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static class LogEntry {

        public Class clazz;
        public Level logLevel;
        public Object object;

        public LogEntry(Class clazz, Level logLevel, Object object) {
            this.clazz = clazz;
            this.logLevel = logLevel;
            this.object = object;
        }

    }

}
