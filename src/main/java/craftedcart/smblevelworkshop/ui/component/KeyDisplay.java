package craftedcart.smblevelworkshop.ui.component;

import craftedcart.smblevelworkshop.util.MathUtils;
import io.github.craftedcart.fluidui.component.Label;
import io.github.craftedcart.fluidui.component.ListBox;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.theme.UITheme;
import io.github.craftedcart.fluidui.util.EnumVAlignment;
import io.github.craftedcart.fluidui.util.UIColor;
import io.github.craftedcart.fluidui.util.UIUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author CraftedCart
 *         Created on 21/12/2016 (DD/MM/YYYY)
 */
public class KeyDisplay extends Panel {

    private String string;
    private Label label;
    private float timerPercent = -1; //-1 = Disable the timer
    private float flashTimerPercent = 1;

    public KeyDisplay(String string) {
        super();

        this.string = string;
    }

    @Override
    public void postInit() {
        super.postInit();

        label = new Label();
        label.setOnInitAction(() -> {
            label.setTopLeftPos(0, 0);
            label.setBottomRightPos(0, 0);
            label.setTopLeftAnchor(0, 0);
            label.setBottomRightAnchor(1, 1);
            label.setVerticalAlign(EnumVAlignment.centre);
            label.setText(string);
            label.setFont(theme.headerFont);
        });
        addChildComponent("label", label);
    }

    @Override
    public void draw() {
        super.draw();

        if (flashTimerPercent > 0) {
            flashTimerPercent -= UIUtils.getDelta() * 4; //Lasts for 0.25 seconds
            flashTimerPercent = Math.max(flashTimerPercent, 0);

            label.setTextColor(MathUtils.lerpUIColor(UIColor.matWhite(), UIColor.matBlue(), flashTimerPercent).alpha(timerPercent == -1 ? 1 : timerPercent));
        }

        if (timerPercent != -1) {
            timerPercent -= UIUtils.getDelta(); //Last for 1 second
            label.setTextColor(label.textColor.alpha(timerPercent));

            if (timerPercent <= 0.0f) { //Self destruct!
                assert parentComponent instanceof ListBox;
                ((ListBox) parentComponent).removeChildComponent(name);
            }
        }
    }

    @Override
    public void setTheme(@NotNull UITheme theme) {
        super.setTheme(theme);

        backgroundColor = theme.panelBackgroundColor.alpha(0.25);
    }

    public void startTimer() {
        timerPercent = 1;
    }

    public void stopTimer() {
        timerPercent = -1;
        label.setTextColor(label.textColor.alpha(1));
    }

    public void flash() {
        flashTimerPercent = 1;
    }
}
