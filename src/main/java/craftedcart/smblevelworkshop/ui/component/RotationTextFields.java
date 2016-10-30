package craftedcart.smblevelworkshop.ui.component;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.util.EnumAxis;
import craftedcart.smblevelworkshop.util.MathUtils;
import craftedcart.smblevelworkshop.util.PosXYZ;
import io.github.craftedcart.fluidui.component.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class RotationTextFields extends XYZTextFields {

    private boolean shouldNormalizeRotation = true;

    public RotationTextFields(@NotNull MainScreen mainScreen, @Nullable TextField nextTextField) {
        super(mainScreen, nextTextField);
    }

    @Override
    public void valueChanged(PosXYZ newValue, EnumAxis axis, List<Placeable> placeables) {
        if (shouldNormalizeRotation) {
            newValue = MathUtils.normalizeRotation(newValue);
        }

        if (axis == EnumAxis.X) {
            for (Placeable placeable : placeables) {
                if (placeable.getAsset().canRotate()) {
                    placeable.setRotation(new PosXYZ(newValue.x, placeable.getRotation().y, placeable.getRotation().z));
                }
            }
        } else if (axis == EnumAxis.Y) {
            for (Placeable placeable : placeables) {
                if (placeable.getAsset().canRotate()) {
                    placeable.setRotation(new PosXYZ(placeable.getRotation().x, newValue.y, placeable.getRotation().z));
                }
            }
        } else if (axis == EnumAxis.Z) {
            for (Placeable placeable : placeables) {
                if (placeable.getAsset().canRotate()) {
                    placeable.setRotation(new PosXYZ(placeable.getRotation().x, placeable.getRotation().y, newValue.z));
                }
            }
        }
    }

    public void setShouldNormalizeRotation(boolean shouldNormalizeRotation) {
        this.shouldNormalizeRotation = shouldNormalizeRotation;
    }

}
