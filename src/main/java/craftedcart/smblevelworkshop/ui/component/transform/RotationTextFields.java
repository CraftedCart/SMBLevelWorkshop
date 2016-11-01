package craftedcart.smblevelworkshop.ui.component.transform;

import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.util.EnumAxis;
import craftedcart.smblevelworkshop.util.ITransformable;
import craftedcart.smblevelworkshop.util.MathUtils;
import craftedcart.smblevelworkshop.util.PosXYZ;
import io.github.craftedcart.fluidui.component.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CraftedCart
 *         Created on 01/11/2016 (DD/MM/YYYY)
 */
public abstract class RotationTextFields extends XYZTextFields {

    private boolean shouldNormalizeRotation = true;

    public RotationTextFields(@NotNull MainScreen mainScreen, @Nullable TextField nextTextField) {
        super(mainScreen, nextTextField);
    }

    @Override
    public void valueChanged(PosXYZ newValue, EnumAxis axis, List<ITransformable> transformables) {
        if (shouldNormalizeRotation) {
            newValue = MathUtils.normalizeRotation(newValue);
        }

        if (axis == EnumAxis.X) {
            for (ITransformable transformable : transformables) {
                if (transformable.canRotate()) {
                    transformable.setRotation(new PosXYZ(newValue.x, transformable.getRotation().y, transformable.getRotation().z));
                }
            }
        } else if (axis == EnumAxis.Y) {
            for (ITransformable transformable : transformables) {
                if (transformable.canRotate()) {
                    transformable.setRotation(new PosXYZ(transformable.getRotation().x, newValue.y, transformable.getRotation().z));
                }
            }
        } else if (axis == EnumAxis.Z) {
            for (ITransformable transformable : transformables) {
                if (transformable.canRotate()) {
                    transformable.setRotation(new PosXYZ(transformable.getRotation().x, transformable.getRotation().y, newValue.z));
                }
            }
        }
    }

    public void setShouldNormalizeRotation(boolean shouldNormalizeRotation) {
        this.shouldNormalizeRotation = shouldNormalizeRotation;
    }

}
