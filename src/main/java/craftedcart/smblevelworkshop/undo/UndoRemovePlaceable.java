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
public class UndoRemovePlaceable extends UndoCommand {

    @NotNull private List<String> names;
    @NotNull private List<Placeable> placeables = new ArrayList<>();
    @NotNull private MainScreen mainScreen;

    public UndoRemovePlaceable(@NotNull ClientLevelData clientLevelData, @NotNull MainScreen mainScreen, @NotNull List<String> names, @NotNull List<Placeable> placeables) {
        super(clientLevelData);

        this.names = new ArrayList<>(names);
        for (Placeable placeable : placeables) {
            this.placeables.add(placeable.getCopy());
        }
        this.mainScreen = mainScreen;
    }

    @Override
    public void undo() {
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            Placeable placeable = placeables.get(i);
            clientLevelData.getLevelData().addPlaceable(name, placeable.getCopy());
            mainScreen.outlinerListBox.addChildComponent(mainScreen.getOutlinerPlaceableComponent(name));
        }
    }

    @Override
    public UndoCommand getRedoCommand() {
        List<Placeable> newList = new ArrayList<>();
        for (Placeable placeable : placeables) {
            newList.add(placeable.getCopy());
        }

        return new UndoAddPlaceable(clientLevelData, mainScreen, names, newList);
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
