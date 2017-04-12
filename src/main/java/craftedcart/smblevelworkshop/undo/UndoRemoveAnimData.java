package craftedcart.smblevelworkshop.undo;

import craftedcart.smblevelworkshop.animation.AnimData;
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
public class UndoRemoveAnimData extends UndoCommand {

    @NotNull private Map<String /* name */, AnimData> animDataMap = new HashMap<>();
    @NotNull private MainScreen mainScreen;

    public UndoRemoveAnimData(@NotNull ClientLevelData clientLevelData, @NotNull MainScreen mainScreen, @NotNull Set<String> animDataSet) {
        super(clientLevelData);

        for (String name : animDataSet) {
            animDataMap.put(name, clientLevelData.getLevelData().getObjectAnimData(name).getCopy());
        }

        this.mainScreen = mainScreen;
    }

    public UndoRemoveAnimData(@NotNull ClientLevelData clientLevelData, @NotNull MainScreen mainScreen, @NotNull Map<String, AnimData> animDataMap) {
        super(clientLevelData);

        for (Map.Entry<String, AnimData> entry : animDataMap.entrySet()) {
            this.animDataMap.put(entry.getKey(), entry.getValue().getCopy());
        }

        this.mainScreen = mainScreen;
    }

    @Override
    public void undo() {
        for (Map.Entry<String, AnimData> entry : animDataMap.entrySet()) {
            clientLevelData.getLevelData().setAnimData(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public UndoCommand getRedoCommand() {
        return new UndoAddAnimData(clientLevelData, mainScreen, animDataMap);
    }

    @Nullable
    @Override
    public String getUndoMessage() {
        return LangManager.getItem("undoRemoveAnimData");
    }

    @Nullable
    @Override
    public String getRedoMessage() {
        return LangManager.getItem("redoRemoveAnimData");
    }

}
