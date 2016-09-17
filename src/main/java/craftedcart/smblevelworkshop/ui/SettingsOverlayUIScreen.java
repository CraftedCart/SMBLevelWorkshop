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
    }

}
