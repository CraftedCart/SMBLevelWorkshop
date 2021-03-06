package craftedcart.smblevelworkshop.ui.component.transform;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.undo.UndoAssetTransform;
import craftedcart.smblevelworkshop.util.ITransformable;
import io.github.craftedcart.fluidui.component.TextField;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class PlaceableRotationTextFields extends RotationTextFields {

    public PlaceableRotationTextFields(@NotNull MainScreen mainScreen, @Nullable TextField nextTextField) {
        super(mainScreen, nextTextField);
    }

    @Nullable
    @Override
    protected Double valueConfirmedParseNumber(String value, List<ITransformable> transformablesToPopulate) {
        double newValue;

        try {
            newValue = Double.parseDouble(value);

            assert ProjectManager.getCurrentClientLevelData() != null;

            mainScreen.addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentClientLevelData(),
                    ProjectManager.getCurrentClientLevelData().getSelectedPlaceables()));

            for (String name : ProjectManager.getCurrentClientLevelData().getSelectedPlaceables()) {
                Placeable placeable = ProjectManager.getCurrentLevelData().getPlaceable(name);
                transformablesToPopulate.add(placeable);
            }

            return newValue;
        } catch (NumberFormatException e) {
            mainScreen.sendNotif(LangManager.getItem("invalidNumber"), UIColor.matRed());
        }

        return null; //Failed to parse the number
    }

    @Override
    public void postValuesChanged() {
        mainScreen.updatePropertiesPlaceablesPanel();
    }

}
