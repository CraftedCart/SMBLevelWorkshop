package craftedcart.smblevelworkshop;

import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.ui.DefaultUITheme;
import craftedcart.smblevelworkshop.ui.LoadingScreen;
import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.util.CrashHandler;
import craftedcart.smblevelworkshop.util.LogHelper;
import io.github.craftedcart.fluidui.FontCache;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.SlickException;

import java.awt.*;
import java.io.IOException;

/**
 * @author CraftedCart
 * Created on 28/08/2016 (DD/MM/YYYY)
 */
public class SMBLevelWorkshop {

    public static void main(String[] args) {
        if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
            LogHelper.info(SMBLevelWorkshop.class, "Running on Linux - Calling XInitThreads()");
            System.loadLibrary("fixXInitThreads");
        }

        LogHelper.info(SMBLevelWorkshop.class, "SMB Level workshop launched");

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.UNCAUGHT_EXCEPTION_HANDLER_NO_GUI); //Set the uncaught exception handler (Create a crash report)

        try {
            Window.init();
        } catch (LWJGLException | FontFormatException | IOException e) {
            LogHelper.error(Window.class, e);
            e.printStackTrace();
        }
    }

    public static void init() throws LWJGLException, IOException, FontFormatException, SlickException {
        Window.drawable.makeCurrent();

        FontCache.registerAWTFont("Roboto-Regular", SMBLevelWorkshop.class.getResourceAsStream("/Roboto-Regular.ttf"));
        FontCache.registerUnicodeFont("Roboto-Regular", 24);
        FontCache.registerUnicodeFont("Roboto-Regular", 16);

        Window.uiScreen = new LoadingScreen(); //Show the loading screen

        //Load resources
        LogHelper.info(SMBLevelWorkshop.class, "Loading resources");
        LoadingScreen.headerMessage = ResourceManager.initResources.getString("loadingResources"); //Set the loading screen's header message
        ResourceManager.queueVanillaResources();
        ResourceManager.registerAllResources();

//        LogHelper.info(SMBLevelWorkshop.class, "Initializing SoundSystem");
//        LoadingScreen.headerMessage = ResourceManager.initResources.getString("initSoundSystem");
//        LoadingScreen.infoMessage = "";
//        LoadingScreen.progress = -1;
//        try {
//            AudioUtils.init(); //Init audio stuff
//        } catch (SoundSystemException e) {
//            LogHelper.error(SMBLevelWorkshop.class, "Error while initializing AudioUtils\n" + CrashHandler.getStackTraceString(e));
//        }

        Keyboard.enableRepeatEvents(true);

        ProjectManager.setCurrentProject(new Project());

        MainScreen mainScreen = new MainScreen();
        mainScreen.setTheme(new DefaultUITheme());
        Window.setUIScreen(mainScreen);
    }

    public static void onQuit() {
//        AudioUtils.cleanup();

        Display.destroy();
    }

}
