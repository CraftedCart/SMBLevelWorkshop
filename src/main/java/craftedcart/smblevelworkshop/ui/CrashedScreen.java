package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.util.LogHelper;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.util.PosXY;
import io.github.craftedcart.fluidui.util.UIColor;
import io.github.craftedcart.fluidui.util.UIUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;

import java.util.List;

/**
 * @author CraftedCart
 * Created on 13/05/2016 (DD/MM/YYYY)
 * Friday the 13th D:
 */
public class CrashedScreen extends FluidUIScreen {

    public CrashedScreen(String stackTrace) {
        this(stackTrace, true);
    }

    public CrashedScreen(String stackTrace, boolean animateIn) {

        Mouse.setGrabbed(false);

        UIColor.matGrey900().bindClearColor();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        RedXComponent redXComponent = new RedXComponent();
        redXComponent.setOnInitAction(() -> {
            redXComponent.setTopLeftPos(24, 24);
            redXComponent.setBottomRightPos(88, 88);
            redXComponent.setTopLeftAnchor(animateIn ? 1 : 0, 0);
            redXComponent.setBottomRightAnchor(animateIn ? 1 : 0, 0);

            if (animateIn) {
                PluginSmoothAnimateAnchor animateAnchor = new PluginSmoothAnimateAnchor();
                animateAnchor.setTargetTopLeftAnchor(0, 0);
                animateAnchor.setTargetBottomRightAnchor(0, 0);
                redXComponent.addPlugin(animateAnchor);
            }
        });
        addChildComponent("redXComponent", redXComponent);

        Label crashedLabel = new Label();
        crashedLabel.setOnInitAction(() -> {
            crashedLabel.setTopLeftPos(112, 24);
            crashedLabel.setBottomRightPos(-24, 88);
            crashedLabel.setTopLeftAnchor(animateIn ? 1 : 0, 0);
            crashedLabel.setBottomRightAnchor(animateIn ? 2 : 1, 0);
            crashedLabel.setFont(FontCache.getUnicodeFont("Roboto-Regular", 48));
            crashedLabel.setText(ResourceManager.initResources.getString("crashed"));
            crashedLabel.setTextColor(UIColor.matWhite());

            if (animateIn) {
                PluginSmoothAnimateAnchor animateAnchor = new PluginSmoothAnimateAnchor();
                animateAnchor.setTargetTopLeftAnchor(0, 0);
                animateAnchor.setTargetBottomRightAnchor(1, 0);
                crashedLabel.addPlugin(animateAnchor);
            }
        });
        addChildComponent("crashedLabel", crashedLabel);

        double listBoxTopPosY = 112;
        double listBoxBottomPosY = -72;

        ListBox logListBox = new ListBox();
        logListBox.setOnInitAction(() -> {
            logListBox.setTopLeftPos(24, listBoxTopPosY);
            logListBox.setBottomRightPos(-24, listBoxBottomPosY);
            logListBox.setTopLeftAnchor(animateIn ? 1 : 0, 0);
            logListBox.setBottomRightAnchor(animateIn ? 2 : 1, 1);
            logListBox.setBackgroundColor(UIColor.matGrey300(0.05));

            if (animateIn) {
                PluginSmoothAnimateAnchor animateAnchor = new PluginSmoothAnimateAnchor();
                animateAnchor.setTargetTopLeftAnchor(0, 0);
                animateAnchor.setTargetBottomRightAnchor(1, 1);
                logListBox.addPlugin(animateAnchor);
            }
        });
        addChildComponent("logListBox", logListBox);

        addLogToListBox(logListBox, LogHelper.log);

        Component spacerComponent = new Component();
        spacerComponent.setOnInitAction(() -> {
            spacerComponent.setTopLeftPos(0, 0);
            spacerComponent.setBottomRightPos(0, 24);
        });
        logListBox.addChildComponent("spacerComponent", spacerComponent);

        addStackTraceToListBox(logListBox, stackTrace);

        //Scroll to the bottom
        logListBox.scrollOffset = -(logListBox.heightOfAllChildren - (Display.getHeight() - (listBoxTopPosY - listBoxBottomPosY)));
        logListBox.smoothedScrollOffset = -(logListBox.heightOfAllChildren - (Display.getHeight() - (listBoxTopPosY - listBoxBottomPosY)));

        TextButton quitButton = new TextButton();
        quitButton.setOnInitAction(() -> {
            quitButton.setTopLeftPos(24, -48);
            quitButton.setBottomRightPos(-24, -24);
            quitButton.setTopLeftAnchor(animateIn ? 1 : 0, 1);
            quitButton.setBottomRightAnchor(animateIn ? 2 : 1, 1);
            quitButton.setText(ResourceManager.initResources.getString("quit"));
            quitButton.setFont(FontCache.getUnicodeFont("Roboto-Regular", 16));

            if (animateIn) {
                PluginSmoothAnimateAnchor animateAnchor = new PluginSmoothAnimateAnchor();
                animateAnchor.setTargetTopLeftAnchor(0, 1);
                animateAnchor.setTargetBottomRightAnchor(1, 1);
                quitButton.addPlugin(animateAnchor);
            }
        });
        quitButton.setOnLMBAction(() -> Window.running = false);
        addChildComponent("quitButton", quitButton);

    }

    @Override
    public void draw() {
        UIColor.matGrey900().bindClearColor();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.draw();
    }

    private void addStackTraceToListBox(ListBox logListBox, String stackTrace) {

        UnicodeFont font = FontCache.getUnicodeFont("Roboto-Regular", 16);

        String[] stackTraceArray = stackTrace.replace("\u0009", "    ").split("\n"); //Replace tabs with 4 spaces and split by \n

        int i = 0;
        for (String item : stackTraceArray) {

            Panel itemPanel = new Panel();
            itemPanel.setOnInitAction(() -> {
                itemPanel.setTopLeftPos(0, 0);
                itemPanel.setBottomRightPos(0, font.getHeight(item));
                itemPanel.setBackgroundColor(getColorOfStackTraceElement(item));

            });
            logListBox.addChildComponent(String.format("stackTraceItem%d", i), itemPanel);

            Label label = new Label();
            label.setOnInitAction(() -> {
                label.setTopLeftPos(24, 0);
                label.setBottomRightPos(0, 0);
                label.setBottomRightAnchor(1, 1);
                label.setFont(font);
                label.setText(item);
                label.setTextColor(UIColor.matWhite());
            });
            itemPanel.addChildComponent("label", label);

            i++;
        }

    }

    private void addLogToListBox(ListBox logListBox, List<LogHelper.LogEntry> log) {

        UnicodeFont font = FontCache.getUnicodeFont("Roboto-Regular", 16);

        int i = 0;
        for (LogHelper.LogEntry item : log) {

            Panel itemPanel = new Panel();
            itemPanel.setOnInitAction(() -> {
                itemPanel.setTopLeftPos(0, 0);
                itemPanel.setBottomRightPos(0, font.getHeight(String.valueOf(item.object)));
                itemPanel.setBackgroundColor(getColorOfLogLevel(item.logLevel));

            });
            logListBox.addChildComponent(String.format("logItem%d", i), itemPanel);

            Label label = new Label();
            label.setOnInitAction(() -> {
                label.setTopLeftPos(24, 0);
                label.setBottomRightPos(0, 0);
                label.setBottomRightAnchor(1, 1);
                label.setFont(font);
                label.setText(String.valueOf(item.object).replace("\u0009", "    "));
                label.setTextColor(UIColor.matWhite());
            });
            itemPanel.addChildComponent("label", label);

            i++;
        }

    }

    private UIColor getColorOfStackTraceElement(String element) {

        if (element.contains("Exception") || element.contains("Error")) {
            return UIColor.matRed(0.2);
        } else if (element.contains("craftedcart.smblevelworkshop")) {
            return UIColor.matBlue(0.2);
        } else if (element.contains("craftedcart")) {
            return UIColor.matBlueGrey(0.2);
        } else {
            return UIColor.matGrey300(0.2);
        }

    }

    private UIColor getColorOfLogLevel(Level logLevel) {

        if (logLevel == Level.FATAL) {
            return UIColor.matRed(0.2);
        } else if (logLevel == Level.ERROR) {
            return UIColor.matOrange(0.2);
        } else if (logLevel == Level.WARN) {
            return UIColor.matYellow(0.2);
        } else if (logLevel == Level.DEBUG || logLevel == Level.TRACE) {
            return UIColor.matGrey300(0.1);
        } else {
            return UIColor.matGrey300(0.2);
        }

    }

}

class RedXComponent extends Panel {

    @Override
    public void componentDraw() {

        //<editor-fold desc="Draw the circle">
        PosXY centre = new PosXY(topLeftPx.x + width / 2, topLeftPx.y + width / 2);

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        UIColor.matRed().bindColor();

        for (float i = 0; i < 2; i += 0.04f) { //25 Points
            PosXY circlePoint = new PosXY(
                    centre.x + width / 2 * Math.cos(i * Math.PI),
                    centre.y + width / 2 * Math.sin(i * Math.PI)
            );

            GL11.glVertex2d(circlePoint.x, circlePoint.y);
        }

        GL11.glEnd();
        //</editor-fold>

        UIColor.matWhite().bindColor();

        UIUtils.drawQuad(
                topLeftPx.add(width / 4, width / 4 + 24),
                topLeftPx.add(width / 4 + 24, width / 4),
                bottomRightPx.subtract(width / 4, width / 4 + 24),
                bottomRightPx.subtract(width / 4 + 24, width / 4)
        );

        UIUtils.drawQuad(
                new PosXY(bottomRightPx.x, topLeftPx.y).add(-(width / 4), width / 4 + 24),
                new PosXY(bottomRightPx.x, topLeftPx.y).add(-(width / 4 + 24), width / 4),
                new PosXY(topLeftPx.x, bottomRightPx.y).subtract(-(width / 4), width / 4 + 24),
                new PosXY(topLeftPx.x, bottomRightPx.y).subtract(-(width / 4 + 24), width / 4)
        );

    }
}
