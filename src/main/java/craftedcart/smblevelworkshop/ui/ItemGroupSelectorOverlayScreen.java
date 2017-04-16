package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.util.WSItemGroup;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.component.ListBox;
import io.github.craftedcart.fluidui.component.TextButton;

import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 16/04/2017 (DD/MM/YYYY)
 */
public class ItemGroupSelectorOverlayScreen extends FluidUIScreen {

    public ItemGroupSelectorOverlayScreen(double mousePercentY, Map<String, WSItemGroup> itemGroupMap) {
        init(mousePercentY, itemGroupMap);
    }

    private void init(double mousePercentY, Map<String, WSItemGroup> itemGroupMap) {
        setTheme(new DefaultUITheme());

        final ListBox listBox = new ListBox();
        listBox.setOnInitAction(() -> {
            listBox.setTopLeftPos(-256, -38);
            listBox.setBottomRightPos(0, 0);
            listBox.setTopLeftAnchor(1, mousePercentY);
            listBox.setBottomRightAnchor(1, 1);
        });
        addChildComponent("listBox", listBox);

        for (Map.Entry<String, WSItemGroup> entry : itemGroupMap.entrySet()) {
            final TextButton button = new TextButton();
            button.setOnInitAction(() -> {
                button.setText(LangManager.getItem(entry.getKey()));
                button.setTopLeftPos(0, 0);
                button.setBottomRightPos(0, 24);
            });
            button.setOnLMBAction(() -> selectItemGroup(entry));
            listBox.addChildComponent(entry.getKey() + "TypeButton", button);
        }

    }

    private void selectItemGroup(Map.Entry<String, WSItemGroup> entry) {
        assert parentComponent instanceof MainScreen;

        MainScreen mainScreen = (MainScreen) parentComponent;
        mainScreen.setItemGroupForSelectedPlaceables(entry.getKey());

        mainScreen.setOverlayUiScreen(null);
    }

}
