package craftedcart.smblevelworkshop.ui.component.transform;

import craftedcart.smblevelworkshop.animation.AnimData;
import craftedcart.smblevelworkshop.animation.NamedTransform;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.util.EnumAxis;
import craftedcart.smblevelworkshop.util.ITransformable;
import craftedcart.smblevelworkshop.util.PosXYZ;
import io.github.craftedcart.fluidui.component.TextField;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 01/11/2016 (DD/MM/YYYY)
 */
public class ObjectPositionTextFields extends PositionTextFields {

    public ObjectPositionTextFields(@NotNull MainScreen mainScreen, @Nullable TextField nextTextField) {
        super(mainScreen, nextTextField);
    }

    @Nullable
    @Override
    protected Double valueConfirmedParseNumber(String value, List<ITransformable> transformablesToPopulate) {
        double newValue;

        try {
            newValue = Double.parseDouble(value);

            assert ProjectManager.getCurrentProject().clientLevelData != null;

            for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {

                if (!ProjectManager.getCurrentProject().clientLevelData.doesCurrentFrameObjectHaveAnimData(name)) {
                    ProjectManager.getCurrentProject().clientLevelData.addCurrentFrameAnimData(Collections.singleton(name));
                }

                transformablesToPopulate.add(
                        ProjectManager.getCurrentProject().clientLevelData.getLevelData().getObjectAnimData(name).getNamedTransformAtTime(
                                ProjectManager.getCurrentProject().clientLevelData.getTimelinePos(),
                                name
                        )
                );
            }

            return newValue;
        } catch (NumberFormatException e) {
            mainScreen.sendNotif(LangManager.getItem("invalidNumber"), UIColor.matRed());
        }

        return null; //Failed to parse the number
    }

    @Override
    public void valueChanged(PosXYZ newValue, EnumAxis axis, List<ITransformable> transformables) {
        super.valueChanged(newValue, axis, transformables);

        float time = ProjectManager.getCurrentProject().clientLevelData.getTimelinePos();

        for (ITransformable transformable : transformables) {
            assert transformable instanceof NamedTransform;
            String name = ((NamedTransform) transformable).getName();
            if (!ProjectManager.getCurrentProject().clientLevelData.doesCurrentFrameObjectHaveAnimData(name)) {
                ProjectManager.getCurrentProject().clientLevelData.addCurrentFrameAnimData(Collections.singleton(name));
            }

            AnimData animData = ProjectManager.getCurrentProject().clientLevelData.getCurrentFrameObjectAnimData(name);

            if (axis == EnumAxis.X) {
                animData.setPosXFrame(time, (float) newValue.x);
            } else if (axis == EnumAxis.Y) {
                animData.setPosYFrame(time, (float) newValue.y);
            } else if (axis == EnumAxis.Z) {
                animData.setPosZFrame(time, (float) newValue.z);
            }

         }
    }

    @Override
    public void postValuesChanged() {
        mainScreen.updatePropertiesObjectsPanel();
    }

}
