package craftedcart.smblevelworkshop.undo;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.MainScreen;
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
    @NotNull private MainScreen mainScreen;

    public UndoAddPlaceable(@NotNull ClientLevelData clientLevelData, MainScreen mainScreen, @NotNull String name, @NotNull Placeable placeable) {
        super(clientLevelData);

        this.name = name;
        this.placeable = placeable.getCopy();
        this.mainScreen = mainScreen;
    }

    @Override
    public void undo() {
        clientLevelData.getLevelData().removePlaceable(name);
        clientLevelData.removeSelectedPlaceable(name);
        mainScreen.outlinerListBox.removeChildComponent(name + "OutlinerPlaceable");
    }

    @Override
    public UndoCommand getRedoCommand() {
        return new UndoRemovePlaceable(clientLevelData, mainScreen, name, placeable.getCopy());
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
