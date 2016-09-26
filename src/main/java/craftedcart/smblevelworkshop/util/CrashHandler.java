package craftedcart.smblevelworkshop.util;

import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.ui.CrashedScreen;
import org.lwjgl.Sys;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 14/05/2016 (DD/MM/YYYY)
 */
public class CrashHandler {

    public static final Thread.UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER = (t, e) -> CrashHandler.handleCrash(t, e, true);
    public static final Thread.UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER_NO_GUI = (t, e) -> CrashHandler.handleCrash(t, e, false);

    public static void handleCrash(Thread t, Throwable e, boolean showGui) {
        LogHelper.fatal(CrashHandler.class, generateCrashLog(t, e));

//        LogHelper.info(CrashHandler.class, "Cleaning up after crash...");

        if (showGui) {
            try {
                Window.drawable.makeCurrent();
                Window.uiScreen = new CrashedScreen(getStackTraceString(e));
            } catch (Exception e1) {
                LogHelper.error(CrashHandler.class, "Error in CrashHandler\n" + getStackTraceString(e1));
            }
        }
    }

    public static String getStackTraceString(Throwable e) {
        //Get the stack trace as a string
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String generateCrashLog(Thread t, Throwable e) {

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");

        List<String> jvmArgs = runtimeMxBean.getInputArguments();
        StringBuilder builder = new StringBuilder("    ");
        for (String aValue : jvmArgs) builder.append(aValue).append("\n    ");
        if (jvmArgs.size() > 0) {builder.deleteCharAt(builder.length() - 5);} else {builder.append("None");}
        String jvmArgsString = builder.toString();

        String lwjglVersion = Sys.getVersion();
        long processors = Runtime.getRuntime().availableProcessors();
        long heapSize = Runtime.getRuntime().maxMemory();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
        Date dateObj = new Date();
        String time = df.format(dateObj); //Get the date and time

        return "A fatal error has been thrown within SMB Level Workshop\n" +
                "\n" +
                "===============[ BEGIN CRASH REPORT ]===============\n" +
                "\n" +
                "SMB Level Workshop has crashed!\n" +
                "===============================\n" +
                "\n" +
                String.format("Time: %s\n", time) +
                "\n" +
                "System Information\n" +
                "==================\n" +
                "\n" +
                String.format("Operating System Name: %s\n", osName) +
                String.format("Operating System Version: %s\n", osVersion) +
                String.format("Operating System Architecture: %s\n", osArch) +
                String.format("Java Version: %s\n", javaVersion) +
                String.format("Java Vendor: %s\n", javaVendor) +
                String.format("JVM Arguments:\n%s\n", jvmArgsString) +
                String.format("LWJGL Version: %s\n", lwjglVersion) +
                String.format("OpenGL Version: %s\n", Window.openGLVersion) +
                String.format("Available Processors: %d\n", processors) +
                String.format("Heap Size: %d b (%.2f MiB)\n", heapSize, heapSize / 1048576d) +
                "\n" +
                "Crash Details\n" +
                "=============\n" +
                "\n" +
                String.format("Causing thread: %d \"%s\"\n", t.getId(), t.getName()) +
                "\n" +
                "Stack Trace\n" +
                "===========\n" +
                "\n" +
                String.format("%s\n", getStackTraceString(e)) +
                "\n" +
                "===============[ END CRASH REPORT ]===============\n";
    }

}
