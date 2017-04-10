package craftedcart.smblevelworkshop;

import com.apple.eawt.Application;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.util.CrashHandler;
import craftedcart.smblevelworkshop.util.LogHelper;
import io.github.craftedcart.fluidui.IUIScreen;
import io.github.craftedcart.fluidui.util.PosXY;
import io.github.craftedcart.fluidui.util.UIUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.opengl.SharedDrawable;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageIOImageData;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author CraftedCart
 *         Created on 06/09/2016 (DD/MM/YYYY)
 */
public class Window {

    public static SharedDrawable drawable;
    public static IUIScreen uiScreen;

    public static boolean running = true;

    public static String openGLVersion = "OpenGL hasn't been initialized";

    public static void init() throws LWJGLException, IOException, FontFormatException {
        ResourceManager.preInit();

        Display.setDisplayMode(new org.lwjgl.opengl.DisplayMode(800, 500));
        Display.setResizable(true);
        Display.setTitle(ResourceManager.initResources.getString("smbLevelWorkshop"));
        PixelFormat pxFormat = new PixelFormat().withStencilBits(8).withSamples(4);
        Display.create(pxFormat);
        openGLVersion = GL11.glGetString(GL11.GL_VERSION);
        drawable = new SharedDrawable(Display.getDrawable());

        //Set icon
        LogHelper.info(SMBLevelWorkshop.class, "Setting icon");

        ByteBuffer icon16 = new ImageIOImageData().imageToByteBuffer(ImageIO.read(SMBLevelWorkshop.class.getResourceAsStream("/icon16.png")), false, false, null);
        ByteBuffer icon32 = new ImageIOImageData().imageToByteBuffer(ImageIO.read(SMBLevelWorkshop.class.getResourceAsStream("/icon32.png")), false, false, null);
        ByteBuffer icon128 = new ImageIOImageData().imageToByteBuffer(ImageIO.read(SMBLevelWorkshop.class.getResourceAsStream("/icon128.png")), false, false, null);

        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN")) {
            Display.setIcon(new ByteBuffer[] {icon16, icon32});
        } else if (OS.contains("MAC")) {
            Display.setIcon(new ByteBuffer[] {icon128});
            Application.getApplication().setDockIconImage(new ImageIcon(SMBLevelWorkshop.class.getResource("/icon128.png")).getImage());
        } else {
            Display.setIcon(new ByteBuffer[] {icon32});
        }

        new Thread(() -> {
            try {
                SMBLevelWorkshop.init();
            } catch (LWJGLException | SlickException | FontFormatException | IOException e) {
                CrashHandler.handleCrash(Thread.currentThread(), e, false);
                System.exit(1);
            }
        }, "initThread").start();

        GL11.glClearColor(33f / 256f, 33f / 256f, 33f / 256f, 1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glEnable(GL11.GL_CULL_FACE); //Cull back faces

        Display.setVSyncEnabled(true); //Enable VSync

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.UNCAUGHT_EXCEPTION_HANDLER); //Set the uncaught exception handler (Create a crash report)

        while (!Display.isCloseRequested() && running) { //Render loop
            try {
                renderLoop();
            } catch (Exception e) {
                CrashHandler.handleCrash(Thread.currentThread(), e, true); //Send it to the crash handler
            }
        }

        SMBLevelWorkshop.onQuit();

    }

    private static void renderLoop() {
        setMatrix();

        UIUtils.calcStuff();

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if (uiScreen != null) {
            Mouse.poll();
            while (Mouse.next()) {
                if (Mouse.getEventButtonState()) {
                    uiScreen.onClick(Mouse.getEventButton(), new PosXY(Mouse.getEventX(), Display.getHeight() - Mouse.getEventY()));
                } else {
                    uiScreen.onClickReleased(Mouse.getEventButton(), new PosXY(Mouse.getEventX(), Display.getHeight() - Mouse.getEventY()));
                }
            }

            Keyboard.poll();
            while (Keyboard.next()) {
                if (Keyboard.getEventKeyState()) {
                    uiScreen.onKeyDown(Keyboard.getEventKey(), Keyboard.getEventCharacter());
                } else {
                    uiScreen.onKeyReleased(Keyboard.getEventKey(), Keyboard.getEventCharacter());
                }
            }

            uiScreen.draw();

        }

        logOpenGLError("End of rendering");

        Display.update();
        Display.sync(60); //Cap to 60 FPS
    }

    public static void setMatrix() {
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    public static void setUIScreen(IUIScreen IUIScreen) {
        Window.uiScreen = IUIScreen;
    }

    public static boolean isCtrlOrCmdDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) ||
                Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA);
    }

    public static boolean isShiftDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    public static boolean isAltDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }

    public static void logOpenGLError(String location) {
        int error = GL11.glGetError();
        if (error != 0) {
            LogHelper.error(Window.class, "OpenGL Error #" + String.valueOf(error) + " - " + GLU.gluErrorString(error) + " @ " + location);
        }
    }

    public static boolean isModifierKey(int keyCode) {
        switch (keyCode) {
            case 29: //LCONTROL
            case 157: //RCONTROL
            case 56: //LMENU
            case 184: //RMENU
            case 42: //LSHIFT
            case 54: //RSHIFT
            case 219: //LMETA
            case 220: //RMETA
            case 196: //FUNCTION
            case 58: //CAPITAL
                return true;
            default:
                return false;
        }
    }

}
