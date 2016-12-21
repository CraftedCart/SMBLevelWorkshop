package craftedcart.smblevelworkshop.ui.component;

import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.resource.LangManager;
import io.github.craftedcart.fluidui.component.ListBox;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.util.UIColor;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 21/12/2016 (DD/MM/YYYY)
 */
public class InputOverlay extends Panel {

    private ListBox modKeyListBox;
    private ListBox keyListBox;

    @Override
    public void postInit() {
        super.postInit();

        modKeyListBox = new ListBox();
        modKeyListBox.setOnInitAction(() -> {
            modKeyListBox.setTopLeftPos(4, 4);
            modKeyListBox.setBottomRightPos(180, 0);
            modKeyListBox.setTopLeftAnchor(0, 0);
            modKeyListBox.setBottomRightAnchor(0, 1);
            modKeyListBox.setCanScroll(false);
            modKeyListBox.setBackgroundColor(UIColor.transparent());
        });
        addChildComponent("modKeyListBox", modKeyListBox);

        keyListBox = new ListBox();
        keyListBox.setOnInitAction(() -> {
            keyListBox.setTopLeftPos(184, 4);
            keyListBox.setBottomRightPos(364, 0);
            keyListBox.setTopLeftAnchor(0, 0);
            keyListBox.setBottomRightAnchor(0, 1);
            keyListBox.setCanScroll(false);
            keyListBox.setBackgroundColor(UIColor.transparent());
        });
        addChildComponent("keyListBox", keyListBox);
    }

    public void onKeyDownManual(int key, char keyChar) {
        super.onKeyDown(key, keyChar);

        if (Window.isModifierKey(key)) {
            if (!modKeyListBox.childComponents.containsKey(Objects.toString(key))) {
                KeyDisplay kd = new KeyDisplay(LangManager.getItem(Keyboard.getKeyName(key)));
                kd.setOnInitAction(() -> {
                    kd.setTopLeftPos(0, 0);
                    kd.setBottomRightPos(0, 32);
                });
                modKeyListBox.addChildComponent(Objects.toString(key), kd);
            } else {
                ((KeyDisplay) modKeyListBox.childComponents.get(Objects.toString(key))).stopTimer();
                ((KeyDisplay) modKeyListBox.childComponents.get(Objects.toString(key))).flash();
            }
        } else {
            if (!keyListBox.childComponents.containsKey(Objects.toString(key))) {
                KeyDisplay kd = new KeyDisplay(LangManager.getItem(Keyboard.getKeyName(key)));
                kd.setOnInitAction(() -> {
                    kd.setTopLeftPos(0, 0);
                    kd.setBottomRightPos(0, 32);
                });
                keyListBox.addChildComponent(Objects.toString(key), kd);
            } else {
                ((KeyDisplay) keyListBox.childComponents.get(Objects.toString(key))).stopTimer();
                ((KeyDisplay) keyListBox.childComponents.get(Objects.toString(key))).flash();
            }
        }
    }

    public void onKeyReleasedManual(int key, char keyChar) {
        super.onKeyReleased(key, keyChar);

        if (Window.isModifierKey(key)) {
            if (modKeyListBox.childComponents.containsKey(Objects.toString(key))) {
                ((KeyDisplay) modKeyListBox.childComponents.get(Objects.toString(key))).startTimer();
            }
        } else {
            if (keyListBox.childComponents.containsKey(Objects.toString(key))) {
                ((KeyDisplay) keyListBox.childComponents.get(Objects.toString(key))).startTimer();
            }
        }
    }
}
