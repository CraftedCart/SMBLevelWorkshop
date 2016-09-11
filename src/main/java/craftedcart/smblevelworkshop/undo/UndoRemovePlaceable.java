package craftedcart.smblevelworkshop.undo;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.resource.LangManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author CraftedCart
 *         Created on 11/09/2016 (DD/MM/YYYY)
 */
public class UndoRemovePlaceable extends UndoCommand {

    @NotNull private String name;
    @NotNull private Placeable placeable;

    public UndoRemovePlaceable(@NotNull ClientLevelData clientLevelData, @NotNull String name, @NotNull Placeable placeable) {
        super(clientLevelData);

        this.name = name;
        this.placeable = placeable.getCopy();
    }

    @Override
    public void undo() {
        clientLevelData.getLevelData().addPlaceable(name, placeable.getCopy());
    }

    @Override
    public UndoCommand getRedoCommand() {
        return new UndoAddPlaceable(clientLevelData, name, placeable.getCopy());
    }

    @Nullable
    @Override
    public String getUndoMessage() {
        return LangManager.getItem("undoRemovePlaceable");
    }

    @Nullable
    @Override
    public String getRedoMessage() {
        return LangManager.getItem("redoRemovePlaceable");
    }

}
