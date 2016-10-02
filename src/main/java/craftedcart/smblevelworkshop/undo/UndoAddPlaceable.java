package craftedcart.smblevelworkshop.undo;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.MainScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 11/09/2016 (DD/MM/YYYY)
 */
public class UndoAddPlaceable extends UndoCommand {

    @NotNull private List<String> names;
    @NotNull private List<Placeable> placeables = new ArrayList<>();
    @NotNull private MainScreen mainScreen;

    public UndoAddPlaceable(@NotNull ClientLevelData clientLevelData, @NotNull MainScreen mainScreen, @NotNull List<String> names, @NotNull List<Placeable> placeables) {
        super(clientLevelData);

        this.names = new ArrayList<>(names);
        for (Placeable placeable : placeables) {
            this.placeables.add(placeable.getCopy());
        }
        this.mainScreen = mainScreen;
    }

    @Override
    public void undo() {
        for (String name : names) {
            clientLevelData.getLevelData().removePlaceable(name);
            clientLevelData.removeSelectedPlaceable(name);
            mainScreen.outlinerListBox.removeChildComponent(name + "OutlinerPlaceable");
        }
    }

    @Override
    public UndoCommand getRedoCommand() {
        List<Placeable> newList = new ArrayList<>();
        for (Placeable placeable : placeables) {
            newList.add(placeable.getCopy());
        }

        return new UndoRemovePlaceable(clientLevelData, mainScreen, names, newList);
    }

    @Nullable
    @Override
    public String getUndoMessage() {
        return LangManager.getItem("undoAddPlaceable");
    }

    @Nullable
    @Override
    public String getRedoMessage() {
        if (names.size() > 1) {
            return String.format(LangManager.getItem("redoAddPlaceablePlural"), names.size());
        } else {
            return LangManager.getItem("redoAddPlaceable");
        }
    }

}
