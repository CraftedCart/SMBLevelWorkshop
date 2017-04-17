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
public class UndoAddAnimData extends UndoCommand {

    @NotNull private Map<String /* name */, AnimData> animDataMap = new HashMap<>();
    @NotNull private MainScreen mainScreen;

    public UndoAddAnimData(@NotNull ClientLevelData clientLevelData, @NotNull MainScreen mainScreen, @NotNull Set<String> animDataSet) {
        super(clientLevelData);

        for (String name : animDataSet) {
            animDataMap.put(name, clientLevelData.getLevelData().getObjectAnimData(name).getCopy());
        }

        this.mainScreen = mainScreen;
    }

    public UndoAddAnimData(@NotNull ClientLevelData clientLevelData, @NotNull MainScreen mainScreen, @NotNull Map<String, AnimData> animDataMap) {
        super(clientLevelData);

        for (Map.Entry<String, AnimData> entry : animDataMap.entrySet()) {
            this.animDataMap.put(entry.getKey(), entry.getValue().getCopy());
        }

        this.mainScreen = mainScreen;
    }

    @Override
    public void undo() {
        for (Map.Entry<String, AnimData> entry : animDataMap.entrySet()) {
            clientLevelData.getLevelData().removeAnimData(Collections.singleton(entry.getKey()));

            if (ProjectManager.getCurrentClientLevelData().doesCurrentFrameObjectHaveAnimData(entry.getKey())) {
                ProjectManager.getCurrentClientLevelData().removeCurrentFrameObjectAnimData(entry.getKey());
            }
        }
    }

    @Override
    public UndoCommand getRedoCommand() {
        TreeMap<String, AnimData> preUndoAnimDataMap = clientLevelData.getLevelData().getObjectAnimDataMapCopy();
        return new UndoRemoveAnimData(clientLevelData, mainScreen, preUndoAnimDataMap);
    }

    @Nullable
    @Override
    public String getUndoMessage() {
        return LangManager.getItem("undoAddAnimData");
    }

    @Nullable
    @Override
    public String getRedoMessage() {
        return LangManager.getItem("redoAddAnimData");
    }

}
