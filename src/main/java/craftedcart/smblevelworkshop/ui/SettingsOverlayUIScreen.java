package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.SMBLWSettings;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimatePanelBackgroundColor;
import io.github.craftedcart.fluidui.util.UIColor;

/**
 * @author CraftedCart
 *         Created on 17/09/2016 (DD/MM/YYYY)
 */
public class SettingsOverlayUIScreen extends FluidUIScreen {

    public SettingsOverlayUIScreen() {
        init();
    }

    private void init() {

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
            mainPanel.setTopLeftPos(64, 64);
            mainPanel.setBottomRightPos(-64, -64);
            mainPanel.setTopLeftAnchor(0, 1);
            mainPanel.setBottomRightAnchor(1, 2);

            PluginSmoothAnimateAnchor mainPanelAnimAnchor = new PluginSmoothAnimateAnchor();
            mainPanelAnimAnchor.setTargetTopLeftAnchor(0, 0);
            mainPanelAnimAnchor.setTargetBottomRightAnchor(1, 1);
            mainPanel.addPlugin(mainPanelAnimAnchor);
        });
        backgroundPanel.addChildComponent("mainPanel", mainPanel);

        final Label settingsLabel = new Label();
        settingsLabel.setOnInitAction(() -> {
            settingsLabel.setTopLeftPos(24, 24);
            settingsLabel.setBottomRightPos(-24, 72);
            settingsLabel.setTopLeftAnchor(0, 0);
            settingsLabel.setBottomRightAnchor(1, 0);
            settingsLabel.setTextColor(UIColor.matGrey900());
            settingsLabel.setText(LangManager.getItem("settings"));
            settingsLabel.setFont(FontCache.getUnicodeFont("Roboto-Regular", 24));
        });
        mainPanel.addChildComponent("settingsLabel", settingsLabel);

        final ListBox listBox = new ListBox();
        listBox.setOnInitAction(() -> {
            listBox.setTopLeftPos(24, 72);
            listBox.setBottomRightPos(-24, -72);
            listBox.setTopLeftAnchor(0, 0);
            listBox.setBottomRightAnchor(1, 1);
        });
        mainPanel.addChildComponent("listBox", listBox);

        populateListBox(listBox);

        final TextButton okButton = new TextButton();
        okButton.setOnInitAction(() -> {
            okButton.setTopLeftPos(-152, -48);
            okButton.setBottomRightPos(-24, -24);
            okButton.setTopLeftAnchor(1, 1);
            okButton.setBottomRightAnchor(1, 1);
            okButton.setText(LangManager.getItem("ok"));
        });
        okButton.setOnLMBAction(() -> {
            assert parentComponent instanceof FluidUIScreen;
            ((FluidUIScreen) parentComponent).setOverlayUiScreen(null); //Hide on OK
        });
        mainPanel.addChildComponent("okButton", okButton);

    }

    private void populateListBox(ListBox listBox) {
        //<editor-fold desc="Show textures">
        final Panel showTexturesPanel = new Panel();
        showTexturesPanel.setOnInitAction(() -> {
            showTexturesPanel.setTopLeftPos(0, 0);
            showTexturesPanel.setBottomRightPos(0, 24);
            showTexturesPanel.setBackgroundColor(UIColor.transparent());
        });
        listBox.addChildComponent("showTexturesPanel", showTexturesPanel);

        final Label showTexturesLabel = new Label();
        showTexturesLabel.setOnInitAction(() -> {
            showTexturesLabel.setTopLeftPos(0, 0);
            showTexturesLabel.setBottomRightPos(-24, 0);
            showTexturesLabel.setTopLeftAnchor(0, 0);
            showTexturesLabel.setBottomRightAnchor(1, 1);
            showTexturesLabel.setText(LangManager.getItem("settingShowTextures"));
        });
        showTexturesPanel.addChildComponent("showTexturesLabel", showTexturesLabel);

        final CheckBox showTexturesCheckBox = new CheckBox();
        showTexturesCheckBox.setOnInitAction(() -> {
            showTexturesCheckBox.setTopLeftPos(-24, 0);
            showTexturesCheckBox.setBottomRightPos(0, 0);
            showTexturesCheckBox.setTopLeftAnchor(1, 0);
            showTexturesCheckBox.setBottomRightAnchor(1, 1);
            showTexturesCheckBox.setValue(SMBLWSettings.showTextures);
            showTexturesCheckBox.setTexture(ResourceManager.getTexture("image/checkBoxTick").getTexture());
        });
        showTexturesCheckBox.setOnLMBAction(() -> SMBLWSettings.showTextures = showTexturesCheckBox.value);
        showTexturesPanel.addChildComponent("showTexturesCheckBox", showTexturesCheckBox);
        //</editor-fold>

        //<editor-fold desc="Is unlit">
        final Panel isUnlitPanel = new Panel();
        isUnlitPanel.setOnInitAction(() -> {
            isUnlitPanel.setTopLeftPos(0, 0);
            isUnlitPanel.setBottomRightPos(0, 24);
            isUnlitPanel.setBackgroundColor(UIColor.transparent());
        });
        listBox.addChildComponent("isUnlitPanel", isUnlitPanel);

        final Label isUnlitLabel = new Label();
        isUnlitLabel.setOnInitAction(() -> {
            isUnlitLabel.setTopLeftPos(0, 0);
            isUnlitLabel.setBottomRightPos(-24, 0);
            isUnlitLabel.setTopLeftAnchor(0, 0);
            isUnlitLabel.setBottomRightAnchor(1, 1);
            isUnlitLabel.setText(LangManager.getItem("settingIsUnlit"));
        });
        isUnlitPanel.addChildComponent("isUnlitLabel", isUnlitLabel);

        final CheckBox isUnlitCheckBox = new CheckBox();
        isUnlitCheckBox.setOnInitAction(() -> {
            isUnlitCheckBox.setTopLeftPos(-24, 0);
            isUnlitCheckBox.setBottomRightPos(0, 0);
            isUnlitCheckBox.setTopLeftAnchor(1, 0);
            isUnlitCheckBox.setBottomRightAnchor(1, 1);
            isUnlitCheckBox.setValue(SMBLWSettings.isUnlit);
            isUnlitCheckBox.setTexture(ResourceManager.getTexture("image/checkBoxTick").getTexture());
        });
        isUnlitCheckBox.setOnLMBAction(() -> SMBLWSettings.isUnlit = isUnlitCheckBox.value);
        isUnlitPanel.addChildComponent("isUnlitCheckBox", isUnlitCheckBox);
        //</editor-fold>

        //<editor-fold desc="Show all wireframes">
        final Panel showAllWireframesPanel = new Panel();
        showAllWireframesPanel.setOnInitAction(() -> {
            showAllWireframesPanel.setTopLeftPos(0, 0);
            showAllWireframesPanel.setBottomRightPos(0, 24);
            showAllWireframesPanel.setBackgroundColor(UIColor.transparent());
        });
        listBox.addChildComponent("showAllWireframesPanel", showAllWireframesPanel);

        final Label showAllWireframesLabel = new Label();
        showAllWireframesLabel.setOnInitAction(() -> {
            showAllWireframesLabel.setTopLeftPos(0, 0);
            showAllWireframesLabel.setBottomRightPos(-24, 0);
            showAllWireframesLabel.setTopLeftAnchor(0, 0);
            showAllWireframesLabel.setBottomRightAnchor(1, 1);
            showAllWireframesLabel.setText(LangManager.getItem("settingShowAllWireframes"));
        });
        showAllWireframesPanel.addChildComponent("showAllWireframesLabel", showAllWireframesLabel);

        final CheckBox showAllWireframesCheckBox = new CheckBox();
        showAllWireframesCheckBox.setOnInitAction(() -> {
            showAllWireframesCheckBox.setTopLeftPos(-24, 0);
            showAllWireframesCheckBox.setBottomRightPos(0, 0);
            showAllWireframesCheckBox.setTopLeftAnchor(1, 0);
            showAllWireframesCheckBox.setBottomRightAnchor(1, 1);
            showAllWireframesCheckBox.setValue(SMBLWSettings.showAllWireframes);
            showAllWireframesCheckBox.setTexture(ResourceManager.getTexture("image/checkBoxTick").getTexture());
        });
        showAllWireframesCheckBox.setOnLMBAction(() -> SMBLWSettings.showAllWireframes = showAllWireframesCheckBox.value);
        showAllWireframesPanel.addChildComponent("showAllWireframesCheckBox", showAllWireframesCheckBox);
        //</editor-fold>

        //<editor-fold desc="Show on screen camera controls">
        final Panel showOnScreenCameraControlsPanel = new Panel();
        showOnScreenCameraControlsPanel.setOnInitAction(() -> {
            showOnScreenCameraControlsPanel.setTopLeftPos(0, 0);
            showOnScreenCameraControlsPanel.setBottomRightPos(0, 24);
            showOnScreenCameraControlsPanel.setBackgroundColor(UIColor.transparent());
        });
        listBox.addChildComponent("showOnScreenCameraControlsPanel", showOnScreenCameraControlsPanel);

        final Label showOnScreenCameraControlsLabel = new Label();
        showOnScreenCameraControlsLabel.setOnInitAction(() -> {
            showOnScreenCameraControlsLabel.setTopLeftPos(0, 0);
            showOnScreenCameraControlsLabel.setBottomRightPos(-24, 0);
            showOnScreenCameraControlsLabel.setTopLeftAnchor(0, 0);
            showOnScreenCameraControlsLabel.setBottomRightAnchor(1, 1);
            showOnScreenCameraControlsLabel.setText(LangManager.getItem("settingShowOnScreenCameraControls"));
        });
        showOnScreenCameraControlsPanel.addChildComponent("showOnScreenCameraControlsLabel", showOnScreenCameraControlsLabel);

        final CheckBox showOnScreenCameraControlsCheckBox = new CheckBox();
        showOnScreenCameraControlsCheckBox.setOnInitAction(() -> {
            showOnScreenCameraControlsCheckBox.setTopLeftPos(-24, 0);
            showOnScreenCameraControlsCheckBox.setBottomRightPos(0, 0);
            showOnScreenCameraControlsCheckBox.setTopLeftAnchor(1, 0);
            showOnScreenCameraControlsCheckBox.setBottomRightAnchor(1, 1);
            showOnScreenCameraControlsCheckBox.setValue(SMBLWSettings.showOnScreenCameraControls);
            showOnScreenCameraControlsCheckBox.setTexture(ResourceManager.getTexture("image/checkBoxTick").getTexture());
        });
        showOnScreenCameraControlsCheckBox.setOnLMBAction(() -> SMBLWSettings.showOnScreenCameraControls = showOnScreenCameraControlsCheckBox.value);
        showOnScreenCameraControlsPanel.addChildComponent("showOnScreenCameraControlsCheckBox", showOnScreenCameraControlsCheckBox);
        //</editor-fold>
    }

}
