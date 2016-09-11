package craftedcart.smblevelworkshop.ui;

import io.github.craftedcart.fluidui.plugin.AbstractComponentPlugin;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.util.UIUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author CraftedCart
 *         Created on 11/09/2016 (DD/MM/YYYY)
 */
public class NotificationPlugin extends AbstractComponentPlugin {

    @NotNull private PluginSmoothAnimateAnchor animateAnchor;

    public double time = 0;

    public NotificationPlugin(@NotNull PluginSmoothAnimateAnchor animateAnchor) {
        this.animateAnchor = animateAnchor;
    }

    @Override
    public void onPostInit() {
        animateAnchor.setTargetTopLeftAnchor(0, 1);
        animateAnchor.setTargetBottomRightAnchor(1, 1);
    }

    @Override
    public void onPreDraw() {
        time += UIUtils.getDelta();

        if (time >= 5) { //Remove after 5s
            assert linkedComponent.parentComponent != null;
            linkedComponent.parentComponent.childComponents.remove(linkedComponent.name);
        } else if (time >= 1.5) { //Anim out after 1.5s
            animateAnchor.setTargetTopLeftAnchor(0, 1.2);
            animateAnchor.setTargetBottomRightAnchor(1, 1.2);
        }
    }
}
