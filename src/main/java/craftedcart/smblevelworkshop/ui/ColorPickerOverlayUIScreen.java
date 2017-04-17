package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.resource.LangManager;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.component.Button;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.component.TextButton;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimatePanelBackgroundColor;
import io.github.craftedcart.fluidui.uiaction.UIAction1;
import io.github.craftedcart.fluidui.util.AnchorPoint;
import io.github.craftedcart.fluidui.util.PosXY;
import io.github.craftedcart.fluidui.util.UIColor;
import org.lwjgl.opengl.Display;

/**
 * @author CraftedCart
 *         Created on 17/04/2017 (DD/MM/YYYY)
 */
public class ColorPickerOverlayUIScreen extends FluidUIScreen {

    private UIAction1<UIColor> callback;

    public ColorPickerOverlayUIScreen(PosXY mousePos, UIColor initialColor, UIAction1<UIColor> callback) {
        this.callback = callback;
        init(mousePos, initialColor);
    }

    private void init(PosXY mousePos, UIColor initialColor) {
        AnchorPoint mousePercent = new AnchorPoint(
                mousePos.x / Display.getWidth(),
                mousePos.y / Display.getHeight()
        );

        final Panel backgroundPanel = new Panel();
        backgroundPanel.setOnInitAction(() -> {
            backgroundPanel.setTheme(new DialogUITheme());

            backgroundPanel.setTopLeftPos(0, 0);
            backgroundPanel.setBottomRightPos(0, 0);
            backgroundPanel.setTopLeftAnchor(0, 0);
            backgroundPanel.setBottomRightAnchor(1, 1);
            backgroundPanel.setBackgroundColor(UIColor.pureBlack(0));


            PluginSmoothAnimatePanelBackgroundColor backgroundPanelAnimColor = new PluginSmoothAnimatePanelBackgroundColor();
            backgroundPanelAnimColor.setTargetBackgroundColor(UIColor.pureBlack(0.75));
            backgroundPanel.addPlugin(backgroundPanelAnimColor);
        });
        addChildComponent("backgroundPanel", backgroundPanel);

        final Panel mainPanel = new Panel();
        mainPanel.setOnInitAction(() -> {
            mainPanel.setTopLeftPos(-540, -128);
            mainPanel.setBottomRightPos(0, 0);
            mainPanel.setTopLeftAnchor(mousePercent);
            mainPanel.setBottomRightAnchor(mousePercent);
        });
        backgroundPanel.addChildComponent("mainPanel", mainPanel);

        final UIColor[] colorButtons = new UIColor[]{
                UIColor.matRed(),
                UIColor.matPink(),
                UIColor.matPurple(),
                UIColor.matDeepPurple(),
                UIColor.matIndigo(),
                UIColor.matBlue(),
                UIColor.matLightBlue(),
                UIColor.matCyan(),
                UIColor.matTeal(),
                UIColor.matGreen(),
                UIColor.matLightGreen(),
                UIColor.matLime(),
                UIColor.matYellow(),
                UIColor.matAmber(),
                UIColor.matOrange(),
                UIColor.matDeepOrange(),
                UIColor.matBrown(),
                UIColor.matGrey(),
                UIColor.matBlueGrey()
        };

        int i = 0;
        for (UIColor col : colorButtons) {
            mainPanel.addChildComponent("colBtn" + String.valueOf(i), getColorButton(col, i));
            i++;
        }

        final TextButton cancelButton = new TextButton();
        cancelButton.setOnInitAction(() -> {
            cancelButton.setTopLeftPos(-152, -48);
            cancelButton.setBottomRightPos(-24, -24);
            cancelButton.setTopLeftAnchor(1, 1);
            cancelButton.setBottomRightAnchor(1, 1);
            cancelButton.setText(LangManager.getItem("cancel"));
        });
        cancelButton.setOnLMBAction(() -> {
            assert parentComponent instanceof FluidUIScreen;
            ((FluidUIScreen) parentComponent).setOverlayUiScreen(null); //Hide on cancel
        });
        mainPanel.addChildComponent("cancelButton", cancelButton);

    }

    private void confirmColor(UIColor col) {
        assert parentComponent instanceof FluidUIScreen;
        ((FluidUIScreen) parentComponent).setOverlayUiScreen(null); //Hide on confirm

        if (callback != null) {
            callback.execute(col);
        }
    }

    private Button getColorButton(UIColor col, int i) {
        Button btn = new Button();
        btn.setOnInitAction(() -> {
            btn.setTopLeftPos(24 + (26 * i), 24);
            btn.setBottomRightPos(48 + (26 * i), 48);
            btn.setTopLeftAnchor(0, 0);
            btn.setBottomRightAnchor(0, 0);
            btn.setBackgroundIdleColor(col);
            btn.setBackgroundActiveColor(col.alpha(0.5));
        });
        btn.setOnLMBAction(() -> confirmColor(col));

        return btn;
    }

}

