package craftedcart.smblevelworkshop.data;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.util.LogHelper;

import java.io.File;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class AppDataManager {

    public static void createAppSupportDirectories() {
        tryCreateDirectory(getAppSupportDirectory());
        createAppSupportSubdirectoies();
    }

    public static void createAppSupportSubdirectoies() {
        File communityDir = new File(getAppSupportDirectory(), "community");
        File communityRootDir = new File(communityDir, "root");
        File communityUsersDir = new File(communityDir, "users");

        tryCreateDirectory(communityDir);
        tryCreateDirectory(communityRootDir);
        tryCreateDirectory(communityUsersDir);
    }

    private static File getAppSupportDirectory() {
        String workingDirectory;
        String os = (System.getProperty("os.name")).toUpperCase();

        if (os.contains("WIN")) {
            //If on Windows, get the Application Data folder
            workingDirectory = System.getenv("AppData") + "\\SMBLevelWorkshop";
        } else if (os.contains("MAC OS X")) {
            //If we are on a Mac, goto the "Application Support" directory
            workingDirectory = System.getProperty("user.home") + "/Library/Application Support/SMBLevelWorkshop";
        } else {
            //Assume Linux or some other distro. Use the ~/.local/share home folder
            workingDirectory = System.getProperty("user.home") + "/.local/share/SMBLevelWorkshop";
        }

        return new File(workingDirectory);
    }

    private static void tryCreateDirectory(File directory) {
        if ((!directory.isDirectory() && directory.mkdirs()) || directory.isDirectory()) {
            return;
        }
        LogHelper.fatal(ResourceManager.class, String.format("Failed to create directories \"%s\"", directory.toString()));
        throw new RuntimeException(String.format("Failed to create directories \"%s\"", directory.toString()));
    }

}
