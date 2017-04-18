package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.util.WSItemGroup;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.util.UIColor;

import java.util.Map;
import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 16/04/2017 (DD/MM/YYYY)
 */
public class ItemGroupSelectorOverlayScreen extends FluidUIScreen {

    public ItemGroupSelectorOverlayScreen(double mousePercentY, Map<String, WSItemGroup> itemGroupMap) {
        init(mousePercentY, itemGroupMap);
    }

    private void init(double mousePercentY, Map<String, WSItemGroup> itemGroupMap) {
        setTheme(new DialogUITheme());

        final ListBox listBox = new ListBox();
        listBox.setOnInitAction(() -> {
            listBox.setTopLeftPos(-256, -38);
            listBox.setBottomRightPos(0, 0);
            listBox.setTopLeftAnchor(1, mousePercentY);
            listBox.setBottomRightAnchor(1, 1);
        });
        addChildComponent("listBox", listBox);

        //Add item group manager button
        final TextButton igmButton = new TextButton();
        igmButton.setOnInitAction(() -> {
            igmButton.setText(LangManager.getItem("itemGroupManager"));
            igmButton.setTopLeftPos(0, 0);
            igmButton.setBottomRightPos(0, 24);
        });
        igmButton.setOnLMBAction(() -> {
            assert parentComponent instanceof FluidUIScreen;
            FluidUIScreen uiScreen = (FluidUIScreen) parentComponent;
            uiScreen.setOverlayUiScreen(new ManageItemGroupsOverlayUIScreen()); //Replace ItemGroupSelectorOverlayScreen with item group manager
        });
        listBox.addChildComponent("itemGroupManagerButton", igmButton);

        for (Map.Entry<String, WSItemGroup> entry : itemGroupMap.entrySet()) {
//            final TextButton button = new TextButton();
//            button.setOnInitAction(() -> {
//                button.setText(LangManager.getItem(entry.getKey()));
//                button.setTopLeftPos(0, 0);
//                button.setBottomRightPos(0, 24);
//            });
            final Button button = getItemGroup(entry.getKey(), entry.getValue());
            button.setOnLMBAction(() -> selectItemGroup(entry));
            if (Objects.equals(entry.getKey(), "STAGE_RESERVED") ||
                    (Objects.equals(entry.getKey(), "BACKGROUND_RESERVED") && ProjectManager.getCurrentClientLevelData().getSelectedObjects().size() == 0)) {
                button.setEnabled(false);
            }
            listBox.addChildComponent(entry.getKey() + "TypeButton", button);
        }

    }

    private void selectItemGroup(Map.Entry<String, WSItemGroup> entry) {
        assert parentComponent instanceof MainScreen;

        MainScreen mainScreen = (MainScreen) parentComponent;
        mainScreen.setItemGroupForSelectedPlaceables(entry.getKey());
        mainScreen.setItemGroupForSelectedObjects(entry.getKey());

        mainScreen.setOverlayUiScreen(null);
    }

    private Button getItemGroup(String name, WSItemGroup itemGroup) {
        UIColor col = itemGroup.getColor();

        final Button igButton = new Button();
        igButton.setOnInitAction(() -> {
            igButton.setTopLeftPos(0, 0);
            igButton.setBottomRightPos(0, 24);
            igButton.setBackgroundIdleColor(col.alpha(0.25));

            final Image colorImage = new Image();
            colorImage.setOnInitAction(() -> {
                colorImage.setTopLeftPos(0, 0);
                colorImage.setBottomRightPos(24, 0);
                colorImage.setTopLeftAnchor(0, 0);
                colorImage.setBottomRightAnchor(0, 1);
                colorImage.setTexture(ResourceManager.getTexture("image/circle").getTexture());
                colorImage.setColor(col);
            });
            igButton.addChildComponent("colorImage", colorImage);

            final Label igLabel = new Label();
            igLabel.setOnInitAction(() -> {
                igLabel.setTopLeftPos(28, 0);
                igLabel.setBottomRightPos(0, 0);
                igLabel.setTopLeftAnchor(0, 0);
                igLabel.setBottomRightAnchor(1, 1);
                igLabel.setText(name);
            });
            igButton.addChildComponent("igLabel", igLabel);
        });

        return igButton;
    }

}
