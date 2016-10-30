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
public class PositionTextFields extends XYZTextFields {

    public PositionTextFields(@NotNull MainScreen mainScreen, @Nullable TextField nextTextField) {
        super(mainScreen, nextTextField);
    }

    @Override
    public void valueChanged(PosXYZ newValue, EnumAxis axis, List<Placeable> placeables) {
        if (axis == EnumAxis.X) {
            for (Placeable placeable : placeables) {
                if (placeable.getAsset().canGrabX()) {
                    placeable.setPosition(new PosXYZ(newValue.x, placeable.getPosition().y, placeable.getPosition().z));
                }
            }
        } else if (axis == EnumAxis.Y) {
            for (Placeable placeable : placeables) {
                if (placeable.getAsset().canGrabY()) {
                    placeable.setPosition(new PosXYZ(placeable.getPosition().x, newValue.y, placeable.getPosition().z));
                }
            }
        } else if (axis == EnumAxis.Z) {
            for (Placeable placeable : placeables) {
                if (placeable.getAsset().canGrabZ()) {
                    placeable.setPosition(new PosXYZ(placeable.getPosition().x, placeable.getPosition().y, newValue.z));
                }
            }
        }
    }

}
