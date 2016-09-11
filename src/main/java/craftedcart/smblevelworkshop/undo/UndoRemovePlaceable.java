package craftedcart.smblevelworkshop.undo;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.MainScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author CraftedCart
 *         Created on 11/09/2016 (DD/MM/YYYY)
 */
public class UndoRemovePlaceable extends UndoCommand {

    @NotNull private String name;
    @NotNull private Placeable placeable;
    @NotNull private MainScreen mainScreen;

    public UndoRemovePlaceable(@NotNull ClientLevelData clientLevelData, MainScreen mainScreen, @NotNull String name, @NotNull Placeable placeable) {
        super(clientLevelData);

        this.name = name;
        this.placeable = placeable.getCopy();
        this.mainScreen = mainScreen;
    }

    @Override
    public void undo() {
        clientLevelData.getLevelData().addPlaceable(name, placeable.getCopy());
        mainScreen.outlinerListBox.addChildComponent(mainScreen.getOutlinerPlaceableComponent(name));
    }

    @Override
    public UndoCommand getRedoCommand() {
        return new UndoAddPlaceable(clientLevelData, mainScreen, name, placeable.getCopy());
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
