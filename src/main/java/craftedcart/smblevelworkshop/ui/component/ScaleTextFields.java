package craftedcart.smblevelworkshop.ui.component;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.util.EnumAxis;
import craftedcart.smblevelworkshop.util.PosXYZ;
import io.github.craftedcart.fluidui.component.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class ScaleTextFields extends XYZTextFields {

    public ScaleTextFields(@NotNull MainScreen mainScreen, @Nullable TextField nextTextField) {
        super(mainScreen, nextTextField);
    }

    @Override
    public void valueChanged(PosXYZ newValue, EnumAxis axis, List<Placeable> placeables) {
        if (axis == EnumAxis.X) {
            for (Placeable placeable : placeables) {
                if (placeable.getAsset().canScale()) {
                    placeable.setScale(new PosXYZ(newValue.x, placeable.getScale().y, placeable.getScale().z));
                }
            }
        } else if (axis == EnumAxis.Y) {
            for (Placeable placeable : placeables) {
                if (placeable.getAsset().canScale()) {
                    placeable.setScale(new PosXYZ(placeable.getScale().x, newValue.y, placeable.getScale().z));
                }
            }
        } else if (axis == EnumAxis.Z) {
            for (Placeable placeable : placeables) {
                if (placeable.getAsset().canScale()) {
                    placeable.setScale(new PosXYZ(placeable.getScale().x, placeable.getScale().y, newValue.z));
                }
            }
        }
    }

}
