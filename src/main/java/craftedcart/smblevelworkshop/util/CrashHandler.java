package craftedcart.smblevelworkshop.util;

import craftedcart.smblevelworkshop.SMBLevelWorkshop;
import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.project.ProjectManager;
import org.lwjgl.Sys;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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

    public static final Thread.UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER = CrashHandler::handleCrash;

    private static JFrame cleanupFrame;
    private static JTextArea cleanupTextArea;

    public static void handleCrash(Thread t, Throwable e) {
        Thread.setDefaultUncaughtExceptionHandler(null); //In case something bad happens, don't enter an infinite loop

        LogHelper.fatal(CrashHandler.class, generateCrashLog(t, e));

        LogHelper.info(CrashHandler.class, "Uncaught exception - Now handling crash!");

        SMBLevelWorkshop.shouldQuitJVM = false; //Don't auto quit JVM because the Display was destroyed

        createCrashCleanupUI();

        new Thread(() -> {
            if (Window.drawable != null) {
                LogHelper.info(CrashHandler.class, "Attempting to stop update loop");
                updateCrashCleanupUI("Attempting to stop update loop");
                Window.running = false;
            }

            if (ProjectManager.getCurrentLevelData() != null) {
                LogHelper.info(CrashHandler.class, "Attempting to save current project to home directory");
                updateCrashCleanupUI("Attempting to save current project to home directory");

                String dateTimeStr = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ssZ").format(new Date());
                File crashProject = new File(System.getProperty("user.home"), "SMBLevelWorkshop-CrashProject-" + dateTimeStr + ".xml");

                try {
                    ExportXMLManager.writeXMLConfig(ProjectManager.getCurrentLevelData(), crashProject);
                    LogHelper.info(CrashHandler.class, "Saved crash project to " + crashProject.getPath());
                    updateCrashCleanupUI("Saved crash project to " + crashProject.getPath());
                } catch (ParserConfigurationException | TransformerException e1) {
                    LogHelper.error(CrashHandler.class, "Error saving current project\n" + getStackTraceString(e1));
                    updateCrashCleanupUI("Error saving current project\n" + getStackTraceString(e1));
                }
            }

            destroyCrashCleanupUI();
            showCrashUI();
        }, "CrashHandlerThread").start();

    }

    private static void createCrashCleanupUI() {
        cleanupFrame = new JFrame("SMB Level Workshop Crash Handler");
        cleanupFrame.setSize(800, 300);
        cleanupFrame.setLayout(new BorderLayout());

        cleanupTextArea = new JTextArea();
        cleanupTextArea.setEditable(false);

        cleanupTextArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(cleanupTextArea);
        cleanupFrame.add(scrollPane);

        cleanupFrame.setVisible(true);
    }

    private static void updateCrashCleanupUI(String text) {
        SwingUtilities.invokeLater(() -> cleanupTextArea.append(text + "\n"));
    }

    private static void destroyCrashCleanupUI() {
        cleanupFrame.setVisible(false);
        cleanupFrame = null;
        cleanupTextArea = null;
    }

    private static void showCrashUI() {
        JFrame frame = new JFrame("SMB Level Workshop Crash Handler");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JTextArea logTextArea = new JTextArea();
        logTextArea.setEditable(false);

        StringBuilder logSB = new StringBuilder();
        for (LogHelper.LogEntry entry : LogHelper.log) {
            logSB.append(entry.logLevel.name()).append(" - ").append(entry.clazz.getCanonicalName()).append(" - ").append(entry.object).append("\r\n");
        }

        logTextArea.setText(logSB.toString());
        logTextArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        frame.add(scrollPane);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SMBLevelWorkshop.shouldQuitJVM = true;
                SMBLevelWorkshop.onQuit();
            }
        });

        frame.setVisible(true);

        //Scroll to bottom
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
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
                String.format("Heap Size: %d B (%.2f MiB)\n", heapSize, heapSize / 1048576d) +
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
