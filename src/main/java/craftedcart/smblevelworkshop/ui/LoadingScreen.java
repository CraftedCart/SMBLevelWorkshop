package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.ResourceTexture;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.IUIScreen;
import io.github.craftedcart.fluidui.util.PosXY;
import io.github.craftedcart.fluidui.util.UIColor;
import io.github.craftedcart.fluidui.util.UIUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author CraftedCart
 * Created on 01/04/2016 (DD/MM/YYYY)
 */
public class LoadingScreen implements IUIScreen {

    @NotNull public static String headerMessage = "";
    @NotNull public static String infoMessage = "";
    /**
     * A number between 0 and 1
     * Set to -1 to disable the progress bar
     */
    public static double progress = 0;

    @Nullable public static Map<String, UIColor> debugMessagesOverlay;

    @Nullable private UnicodeFont headerFont = FontCache.getUnicodeFont("Roboto-Regular", 24);
    @Nullable private UnicodeFont infoFont = FontCache.getUnicodeFont("Roboto-Regular", 16);

    public LoadingScreen() {
        UIColor.matWhite().bindClearColor();
    }

    public void draw() {
        //<editor-fold desc="Draw the background">
        ResourceTexture texture = ResourceManager.getTexture("_loadBackground", false);
        if (texture != null) {
            double texRatio = texture.getHeight() / (double) texture.getWidth();
            int height;
            int width;
            if (Display.getHeight() / (double) Display.getWidth() > texRatio) {
                height = Display.getHeight();
                width = (int) (height / texRatio);
            } else {
                width = Display.getWidth();
                height = (int) (width * texRatio);
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            UIColor.pureWhite().bindColor();
            UIUtils.drawTexturedQuad(
                    new PosXY(Display.getWidth() / 2 - width / 2, Display.getHeight() / 2 - height / 2),
                    new PosXY(Display.getWidth() / 2 + width / 2, Display.getHeight() / 2 + height / 2),
                    texture.getTexture());
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }
        //</editor-fold>

        //<editor-fold desc="Draw debugMessagesOverlay">
        if (infoFont != null && debugMessagesOverlay != null) {
            UIUtils.drawQuad(
                    new PosXY(0, 0),
                    new PosXY(Display.getWidth(), Display.getHeight()),
                    UIColor.matGrey900(0.75));

            Map<String, UIColor> duplicateMap = new LinkedHashMap<>();
            duplicateMap.putAll(debugMessagesOverlay);
            ListIterator<Map.Entry<String, UIColor>> iterator = new ArrayList<>(duplicateMap.entrySet()).listIterator(duplicateMap.size());

            int currentHeight = 24;

            while (iterator.hasPrevious()) {
                Map.Entry<String, UIColor> entry = iterator.previous();
                UIUtils.drawString(infoFont, 24, currentHeight, UIUtils.wrapString(infoFont, Display.getWidth() - 48, entry.getKey()), entry.getValue());
                currentHeight += infoFont.getLineHeight();
                for (char c : UIUtils.wrapString(infoFont, Display.getWidth() - 48, entry.getKey()).toCharArray()) {
                    if (c == '\n') {
                        currentHeight += infoFont.getLineHeight();
                    }
                }
            }
        }
        //</editor-fold>

        UIColor.matBlueGrey().bindColor();
        UIUtils.drawQuad(
                new PosXY(0, Display.getHeight() - 110),
                new PosXY(Display.getWidth(), Display.getHeight()));

        UIUtils.drawQuadGradientVertical(
                new PosXY(0, Display.getHeight() - 114),
                new PosXY(Display.getWidth(), Display.getHeight() - 110),
                UIColor.matGrey900(0), UIColor.matGrey900());

        if (headerFont != null) {
            UIUtils.drawString(headerFont, 24, Display.getHeight() - 96, headerMessage, UIColor.matWhite());
        }

        if (infoFont != null) {
            if (progress == -1) {
                UIUtils.drawString(infoFont, 24, Display.getHeight() - 52, infoMessage, UIColor.matWhite());
            } else {
                UIUtils.drawString(infoFont, 24, Display.getHeight() - 52, String.format("%05.2f%% %s", progress * 100, infoMessage), UIColor.matWhite());
            }
        }

        //<editor-fold desc="Draw progress bar">
        if (progress != -1) {
            UIColor.matBlueGrey700().bindColor();
            UIUtils.drawQuad(
                    new PosXY(24, Display.getHeight() - 28),
                    new PosXY(Display.getWidth() - 24, Display.getHeight() - 24));
            UIColor.matBlueGrey300().bindColor();
            UIUtils.drawQuad(
                    new PosXY(24, Display.getHeight() - 28),
                    new PosXY((Display.getWidth() - 48) * progress + 24, Display.getHeight() - 24));
        }
        //</editor-fold>
    }

}
