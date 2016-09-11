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
public class UndoAddPlaceable extends UndoCommand {

    @NotNull private String name;
    @NotNull private Placeable placeable;

    public UndoAddPlaceable(@NotNull ClientLevelData clientLevelData, @NotNull String name, @NotNull Placeable placeable) {
        super(clientLevelData);

        this.name = name;
        this.placeable = placeable.getCopy();
    }

    @Override
    public void undo() {
        clientLevelData.getLevelData().removePlaceable(name);
        clientLevelData.removeSelectedPlaceable(name);
    }

    @Override
    public UndoCommand getRedoCommand() {
        return new UndoRemovePlaceable(clientLevelData, name, placeable.getCopy());
    }

    @Nullable
    @Override
    public String getUndoMessage() {
        return LangManager.getItem("undoAddPlaceable");
    }

    @Nullable
    @Override
    public String getRedoMessage() {
        return LangManager.getItem("redoAddPlaceable");
    }

}
