package craftedcart.smblevelworkshop.ui.component.transform;

import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.util.EnumAxis;
import craftedcart.smblevelworkshop.util.ITransformable;
import craftedcart.smblevelworkshop.util.PosXYZ;
import io.github.craftedcart.fluidui.component.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CraftedCart
 *         Created on 01/11/2016 (DD/MM/YYYY)
 */
public abstract class PositionTextFields extends XYZTextFields {

    public PositionTextFields(@NotNull MainScreen mainScreen, @Nullable TextField nextTextField) {
        super(mainScreen, nextTextField);
    }

    @Override
    public void valueChanged(PosXYZ newValue, EnumAxis axis, List<ITransformable> transformables) {
        if (axis == EnumAxis.X) {
            for (ITransformable transformable : transformables) {
                if (transformable.canMoveX()) {
                    transformable.setPosition(new PosXYZ(newValue.x, transformable.getPosition().y, transformable.getPosition().z));
                }
            }
        } else if (axis == EnumAxis.Y) {
            for (ITransformable transformable : transformables) {
                if (transformable.canMoveY()) {
                    transformable.setPosition(new PosXYZ(transformable.getPosition().x, newValue.y, transformable.getPosition().z));
                }
            }
        } else if (axis == EnumAxis.Z) {
            for (ITransformable transformable : transformables) {
                if (transformable.canMoveZ()) {
                    transformable.setPosition(new PosXYZ(transformable.getPosition().x, transformable.getPosition().y, newValue.z));
                }
            }
        }
    }

}
