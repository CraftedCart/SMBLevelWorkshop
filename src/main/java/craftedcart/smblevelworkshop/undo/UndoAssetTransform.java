package craftedcart.smblevelworkshop.undo;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.resource.LangManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author CraftedCart
 *         Created on 11/09/2016 (DD/MM/YYYY)
 */
public class UndoAssetTransform extends UndoCommand {

    @NotNull private Map<String, Placeable> placedObjectsSnapshot = new HashMap<>();

    public UndoAssetTransform(@NotNull ClientLevelData clientLevelData, @NotNull Set<String> selectedPlaceables) {
        super(clientLevelData);

        for (String name : selectedPlaceables) {
            placedObjectsSnapshot.put(name, clientLevelData.getLevelData().getPlaceable(name).getCopy());
        }
    }

    @Override
    public void undo() {
        for (Map.Entry<String, Placeable> entry : placedObjectsSnapshot.entrySet()) {
            clientLevelData.getLevelData().replacePlaceable(entry.getKey(), entry.getValue().getCopy());
        }
    }

    @Override
    public UndoCommand getRedoCommand() {
        return new UndoAssetTransform(clientLevelData, clientLevelData.getSelectedPlaceables());
    }

    @Nullable
    @Override
    public String getUndoMessage() {
        return LangManager.getItem("undoTransform");
    }

    @Nullable
    @Override
    public String getRedoMessage() {
        return LangManager.getItem("redoTransform");
    }

}
