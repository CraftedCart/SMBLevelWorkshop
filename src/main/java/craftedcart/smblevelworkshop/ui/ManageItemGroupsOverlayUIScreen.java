package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.util.WSItemGroup;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimatePanelBackgroundColor;
import io.github.craftedcart.fluidui.uiaction.UIAction1;
import io.github.craftedcart.fluidui.util.PosXY;
import io.github.craftedcart.fluidui.util.UIColor;

import java.util.Map;
import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 12/04/2017 (DD/MM/YYYY)
 */
public class ManageItemGroupsOverlayUIScreen extends FluidUIScreen {

    public ManageItemGroupsOverlayUIScreen() {
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
            settingsLabel.setText(LangManager.getItem("itemGroupManager"));
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
        
        final TextButton newButton = new TextButton();
        newButton.setOnInitAction(() -> {
            newButton.setTopLeftPos(-152, 24);
            newButton.setBottomRightPos(-24, 48);
            newButton.setTopLeftAnchor(1, 0);
            newButton.setBottomRightAnchor(1, 0);
            newButton.setText(LangManager.getItem("newItemGroup"));
        });
        newButton.setOnLMBAction(() -> newItemGroup(listBox));
        mainPanel.addChildComponent("newButton", newButton);

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

    private void newItemGroup(ListBox listbox) {
        ProjectManager.getCurrentLevelData().addItemGroup();
        populateListBox(listbox);
    }

    private void populateListBox(ListBox listBox) {
        listBox.clearChildComponents();

        assert ProjectManager.getCurrentClientLevelData() != null;
        for (Map.Entry<String, WSItemGroup> entry : ProjectManager.getCurrentLevelData().getItemGroupMap().entrySet()) {
            listBox.addChildComponent(entry.getKey() + "ItemGroup", getItemGroup(listBox, entry.getKey(), entry.getValue()));
        }
    }

    private Panel getItemGroup(ListBox listBox, String name, WSItemGroup itemGroup) {
        UIColor col = itemGroup.getColor();

        final Panel igPanel = new Panel();
        igPanel.setOnInitAction(() -> {
            igPanel.setTopLeftPos(0, 0);
            igPanel.setBottomRightPos(0, 24);
            igPanel.setBackgroundColor(col.alpha(0.25));

            final Label igLabel = new Label();
            igLabel.setOnInitAction(() -> {
                igLabel.setTopLeftPos(4, 0);
                igLabel.setBottomRightPos(0, 0);
                igLabel.setTopLeftAnchor(0, 0);
                igLabel.setBottomRightAnchor(1, 1);
                igLabel.setText(name);
            });
            igPanel.addChildComponent("igLabel", igLabel);

            if (Objects.equals(name, "STAGE_RESERVED") || Objects.equals(name, "BACKGROUND_RESERVED")) {
                //Show locked if the item group is reserved

                final Image lockedImage = new Image();
                lockedImage.setOnInitAction(() -> {
                    lockedImage.setTopLeftPos(-24, 0);
                    lockedImage.setBottomRightPos(0, 0);
                    lockedImage.setTopLeftAnchor(1, 0);
                    lockedImage.setBottomRightAnchor(1, 1);
                    lockedImage.setTexture(ResourceManager.getTexture("image/lock").getTexture());
                    lockedImage.setColor(col);
                });
                igPanel.addChildComponent("lockedImage", lockedImage);

            } else {
                //Add edit actions to non reserved item groups

                final Button deleteButton = new Button();
                deleteButton.setOnInitAction(() -> {
                    deleteButton.setTopLeftPos(-24, 0);
                    deleteButton.setBottomRightPos(0, 0);
                    deleteButton.setTopLeftAnchor(1, 0);
                    deleteButton.setBottomRightAnchor(1, 1);
                    deleteButton.setTexture(ResourceManager.getTexture("image/close").getTexture());
                    deleteButton.setBackgroundIdleColor(col);
                    deleteButton.setBackgroundActiveColor(col.alpha(0.5));
//                deleteButton.setBackgroundHitColor(col.alpha(0.25));
                });
                igPanel.addChildComponent("deleteButton", deleteButton);

                final Button colorButton = new Button();
                colorButton.setOnInitAction(() -> {
                    colorButton.setTopLeftPos(-48, 0);
                    colorButton.setBottomRightPos(-24, 0);
                    colorButton.setTopLeftAnchor(1, 0);
                    colorButton.setBottomRightAnchor(1, 1);
                    colorButton.setTexture(ResourceManager.getTexture("image/circle").getTexture());
                    colorButton.setBackgroundIdleColor(col);
                    colorButton.setBackgroundActiveColor(col.alpha(0.5));
//                colorButton.setBackgroundHitColor(col.alpha(0.25));
                });
                colorButton.setOnLMBAction(() -> showColorPicker(listBox, mousePos, itemGroup));
                igPanel.addChildComponent("colorButton", colorButton);

                final Button renameButton = new Button();
                renameButton.setOnInitAction(() -> {
                    renameButton.setTopLeftPos(-72, 0);
                    renameButton.setBottomRightPos(-48, 0);
                    renameButton.setTopLeftAnchor(1, 0);
                    renameButton.setBottomRightAnchor(1, 1);
                    renameButton.setTexture(ResourceManager.getTexture("image/rename").getTexture());
                    renameButton.setBackgroundIdleColor(col);
                    renameButton.setBackgroundActiveColor(col.alpha(0.5));
//                renameButton.setBackgroundHitColor(col.alpha(0.25));
                });
                igPanel.addChildComponent("renameButton", renameButton);

            }
        });

        return igPanel;
    }

    private void showColorPicker(ListBox listBox, PosXY mousePos, WSItemGroup itemGroup) {
        setOverlayUiScreen(new ColorPickerOverlayUIScreen(mousePos, itemGroup.getColor(), color -> {
            itemGroup.setColor(color);
            populateListBox(listBox);
        }));
    }

}
