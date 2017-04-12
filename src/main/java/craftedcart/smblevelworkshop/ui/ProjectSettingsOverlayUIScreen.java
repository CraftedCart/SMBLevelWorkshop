package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimatePanelBackgroundColor;
import io.github.craftedcart.fluidui.uiaction.UIAction;
import io.github.craftedcart.fluidui.util.UIColor;

import java.io.File;

/**
 * @author CraftedCart
 *         Created on 12/04/2017 (DD/MM/YYYY)
 */
public class ProjectSettingsOverlayUIScreen extends FluidUIScreen {

    public ProjectSettingsOverlayUIScreen() {
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
            settingsLabel.setText(LangManager.getItem("projectSettings"));
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
        //Model sources
        listBox.addChildComponent("modelSourcesLabel", getLabel(LangManager.getItem("modelSources")));
        for (File file : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModelObjSources()) {
            String filepath = file.getAbsolutePath();
            listBox.addChildComponent(filepath + "ModelSourceLabel", getLabel(filepath));
        }

        //Manage item groups button
        listBox.addChildComponent("manageItemGroupsButton", getTextButton(LangManager.getItem("manageItemGroups"), this::manageItemGroups));
    }

    private void manageItemGroups() {
        setOverlayUiScreen(new ManageItemGroupsOverlayUIScreen());
    }

    private Label getLabel(String text) {
        final Label label = new Label();
        label.setOnInitAction(() -> {
            label.setTopLeftPos(0, 0);
            label.setBottomRightPos(0, 24);
            label.setText(text);
        });
        return label;
    }

    private TextButton getTextButton(String text, UIAction lmbAction) {
        final TextButton textButton = new TextButton();
        textButton.setOnInitAction(() -> {
            textButton.setTopLeftPos(0, 0);
            textButton.setBottomRightPos(0, 24);
            textButton.setText(text);
        });
        textButton.setOnLMBAction(lmbAction);
        return textButton;
    }

}
