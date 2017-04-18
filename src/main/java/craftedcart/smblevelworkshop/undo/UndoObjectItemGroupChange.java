package craftedcart.smblevelworkshop.undo;

import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author CraftedCart
 *         Created on 18/04/2017 (DD/MM/YYYY)
 */
public class UndoObjectItemGroupChange extends UndoCommand {

    @NotNull private Map<String, String> placedObjectsSnapshot = new HashMap<>(); //Object name mapped to item group name

    public UndoObjectItemGroupChange(@NotNull ClientLevelData clientLevelData, @NotNull Set<String> selectedObjects) {
        super(clientLevelData);

        for (String name : selectedObjects) {
            placedObjectsSnapshot.put(name, ProjectManager.getCurrentLevelData().getObjectItemGroupName(name));
        }
    }

    @Override
    public void undo() {
        for (Map.Entry<String, String> entry : placedObjectsSnapshot.entrySet()) {
            clientLevelData.getLevelData().changeObjectItemGroup(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public UndoCommand getRedoCommand() {
        return new UndoObjectItemGroupChange(clientLevelData, clientLevelData.getSelectedPlaceables());
    }

    @Nullable
    @Override
    public String getUndoMessage() {
        return LangManager.getItem("undoItemGroupChange");
    }

    @Nullable
    @Override
    public String getRedoMessage() {
        return LangManager.getItem("redoItemGroupChange");
    }

}
