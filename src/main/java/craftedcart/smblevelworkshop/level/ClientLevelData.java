package craftedcart.smblevelworkshop.level;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class ClientLevelData {

    @NotNull private LevelData levelData = new LevelData();

    private Set<String> selectedPlaceables = new HashSet<>();

    public void setLevelData(LevelData levelData) {
        this.levelData = levelData;
    }

    public LevelData getLevelData() {
        return levelData;
    }

    public void addSelectedPlaceable(String name) {
        selectedPlaceables.add(name);
    }

    public void removeSelectedPlaceable(String name) {
        selectedPlaceables.remove(name);
    }

    public boolean isPlaceableSelected(String name) {
        return selectedPlaceables.contains(name);
    }

    public void toggleSelectedPlaceable(String name) {
        if (isPlaceableSelected(name)) {
            removeSelectedPlaceable(name);
        } else {
            addSelectedPlaceable(name);
        }
    }

    public void clearSelectedPlaceables() {
        selectedPlaceables.clear();
    }

    public Set<String> getSelectedPlaceables() {
        return selectedPlaceables;
    }
}
