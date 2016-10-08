package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.theme.DefaultUITheme;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.component.ListBox;
import io.github.craftedcart.fluidui.component.TextButton;

import java.util.List;

/**
 * @author CraftedCart
 *         Created on 14/09/2016 (DD/MM/YYYY)
 */
public class TypeSelectorOverlayScreen extends FluidUIScreen {

    public TypeSelectorOverlayScreen(double mousePercentY, List<String> options) {
        init(mousePercentY, options);
    }

    private void init(double mousePercentY, List<String> options) {
        setTheme(new DefaultUITheme());

        final ListBox listBox = new ListBox();
        listBox.setOnInitAction(() -> {
            listBox.setTopLeftPos(-256, -38);
            listBox.setBottomRightPos(0, 38);
            listBox.setTopLeftAnchor(1, mousePercentY);
            listBox.setBottomRightAnchor(1, mousePercentY);
        });
        addChildComponent("listBox", listBox);

        for (String string : options) {
            final TextButton button = new TextButton();
            button.setOnInitAction(() -> {
                button.setText(LangManager.getItem(string));
                button.setTopLeftPos(0, 0);
                button.setBottomRightPos(0, 24);
            });
            button.setOnLMBAction(() -> selectType(string));
            listBox.addChildComponent(string + "TypeButton", button);
        }

    }

    private void selectType(String type) {
        assert parentComponent instanceof MainScreen;

        MainScreen mainScreen = (MainScreen) parentComponent;
        mainScreen.setTypeForSelectedPlaceables(type);

        mainScreen.setOverlayUiScreen(null);
    }

}
