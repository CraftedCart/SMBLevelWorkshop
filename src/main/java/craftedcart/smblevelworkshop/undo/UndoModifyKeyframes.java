package craftedcart.smblevelworkshop.undo;

import craftedcart.smblevelworkshop.animation.AnimData;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.MainScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author CraftedCart
 *         Created on 11/09/2016 (DD/MM/YYYY)
 */
public class UndoModifyKeyframes extends UndoCommand {

    @NotNull private TreeMap<String /* name */, AnimData> animDataMap = new TreeMap<>();
    @NotNull private MainScreen mainScreen;

    public UndoModifyKeyframes(@NotNull ClientLevelData clientLevelData, @NotNull MainScreen mainScreen, @NotNull Set<String> animDataSet) {
        super(clientLevelData);

        for (String name : animDataSet) {
            animDataMap.put(name, clientLevelData.getLevelData().getObjectAnimData(name).getCopy());
        }

        this.mainScreen = mainScreen;
    }

    public UndoModifyKeyframes(@NotNull ClientLevelData clientLevelData, @NotNull MainScreen mainScreen, @NotNull TreeMap<String, AnimData> animDataMap) {
        super(clientLevelData);

        for (Map.Entry<String, AnimData> entry : animDataMap.entrySet()) {
            this.animDataMap.put(entry.getKey(), entry.getValue().getCopy());
        }

        this.mainScreen = mainScreen;
    }

    @Override
    public void undo() {
        clientLevelData.getLevelData().replaceObjectAnimDataMap(new TreeMap<>(animDataMap));
    }

    @Override
    public UndoCommand getRedoCommand() {
        TreeMap<String, AnimData> preUndoAnimDataMap = clientLevelData.getLevelData().getObjectAnimDataMapCopy();
        return new UndoModifyKeyframes(clientLevelData, mainScreen, preUndoAnimDataMap);
    }

    @Nullable
    @Override
    public String getUndoMessage() {
        return LangManager.getItem("undoModifyKeyframes");
    }

    @Nullable
    @Override
    public String getRedoMessage() {
        return LangManager.getItem("redoModifyKeyframes");
    }

}
