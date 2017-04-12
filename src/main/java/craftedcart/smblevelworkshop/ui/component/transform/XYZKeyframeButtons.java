package craftedcart.smblevelworkshop.ui.component.transform;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.ui.DefaultUITheme;
import craftedcart.smblevelworkshop.util.EnumAxis;
import io.github.craftedcart.fluidui.component.Button;
import io.github.craftedcart.fluidui.component.ListBox;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.uiaction.UIAction1;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.Nullable;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class XYZKeyframeButtons extends Panel {

    private ListBox listBox;

    private Button xButton;
    private Button yButton;
    private Button zButton;

    @Nullable private UIAction1<EnumAxis> onKeyframeActivatedAction;

    public XYZKeyframeButtons() {
        init();
        postInit();
    }

    public void init() {
        super.init();

        setTheme(new DefaultUITheme());

        listBox = new ListBox();
        xButton = new Button();
        yButton = new Button();
        zButton = new Button();

        setBackgroundColor(UIColor.transparent());
        listBox.setUseStencil(false);

        listBox.setOnInitAction(() -> {
            listBox.setTopLeftPos(0, 0);
            listBox.setBottomRightPos(0, 0);
            listBox.setTopLeftAnchor(0, 0);
            listBox.setBottomRightAnchor(1, 1);
            listBox.setBackgroundColor(UIColor.transparent());
            listBox.setCanScroll(false);
            listBox.scrollbarThickness = 0;
        });
        addChildComponent("listBox", listBox);

        //<editor-fold desc="Position X Text Field">
        //Defined at class level
        xButton.setOnInitAction(() -> {
            xButton.setTexture(ResourceManager.getTexture("image/keyframe").getTexture());
            xButton.setTopLeftPos(0, 0);
            xButton.setBottomRightPos(0, 24);
        });
        xButton.setOnLMBAction(() -> onKeyframeActivated(EnumAxis.X));
        listBox.addChildComponent("xButton", xButton);
        //</editor-fold>

        //<editor-fold desc="Position Y Text Field">
        //Defined at class level
        yButton.setOnInitAction(() -> {
            yButton.setTexture(ResourceManager.getTexture("image/keyframe").getTexture());
            yButton.setTopLeftPos(0, 0);
            yButton.setBottomRightPos(0, 24);
        });
        yButton.setOnLMBAction(() -> onKeyframeActivated(EnumAxis.Y));
        listBox.addChildComponent("yButton", yButton);
        //</editor-fold>

        //<editor-fold desc="Position Z Text Field">
        //Defined at class level
        zButton.setOnInitAction(() -> {
            zButton.setTexture(ResourceManager.getTexture("image/keyframe").getTexture());
            zButton.setTopLeftPos(0, 0);
            zButton.setBottomRightPos(0, 24);
        });
        zButton.setOnLMBAction(() -> onKeyframeActivated(EnumAxis.Z));
        listBox.addChildComponent("zButton", zButton);
        //</editor-fold>
    }

    public void setXEnabled(boolean enabled) {
        xButton.setEnabled(enabled);
    }

    public void setYEnabled(boolean enabled) {
        yButton.setEnabled(enabled);
    }

    public void setZEnabled(boolean enabled) {
        zButton.setEnabled(enabled);
    }

    private void onKeyframeActivated(EnumAxis axis) {
        if (onKeyframeActivatedAction != null) {
            onKeyframeActivatedAction.execute(axis);
        }
    }

    public void setOnKeyframeActivatedAction(@Nullable UIAction1<EnumAxis> onKeyframeActivatedAction) {
        this.onKeyframeActivatedAction = onKeyframeActivatedAction;
    }

}
