package craftedcart.smblevelworkshop.undo;

import craftedcart.smblevelworkshop.level.ClientLevelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author CraftedCart
 *         Created on 11/09/2016 (DD/MM/YYYY)
 */
public abstract class UndoCommand {

    @NotNull protected ClientLevelData clientLevelData;

    public UndoCommand(@NotNull ClientLevelData clientLevelData) {
        this.clientLevelData = clientLevelData;
    }

    public abstract void undo();

    /**
     * Called before undoing
     */
    public abstract UndoCommand getRedoCommand();

    @Nullable public abstract String getUndoMessage();
    @Nullable public abstract String getRedoMessage();

}
