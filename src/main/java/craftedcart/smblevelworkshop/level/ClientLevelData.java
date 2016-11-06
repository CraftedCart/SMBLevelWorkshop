package craftedcart.smblevelworkshop.level;

import craftedcart.smblevelworkshop.animation.AnimData;
import craftedcart.smblevelworkshop.animation.NamedTransform;
import craftedcart.smblevelworkshop.util.PosXYZ;
import io.github.craftedcart.fluidui.uiaction.UIAction;
import io.github.craftedcart.fluidui.uiaction.UIAction1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class ClientLevelData {

    @NotNull private LevelData levelData = new LevelData();

    private Set<String> selectedPlaceables = new HashSet<>();
    @Nullable private UIAction onSelectedPlaceablesChanged;

    private Set<String> selectedObjects = new HashSet<>();
    @Nullable private UIAction onSelectedObjectsChanged;
    private Set<String> hiddenObjects = new HashSet<>();
    @Nullable private UIAction onHiddenObjectsChanged;

    private Set<String> selectedExternalBackgroundObjects = new HashSet<>();
    @Nullable private UIAction onSelectedExternalBackgroundObjectsChanged;

    /**
     * Timeline position as a percentage
     */
    private float timelinePos = 0.0f;
    @Nullable private UIAction1<Float> onTimelinePosChanged;
    private float maxTime = 60.0f;
    private float playbackSpeed = 0.0f;

    @NotNull private Map<String, AnimData> currentFrameObjectAnimDataMap = new HashMap<>();

    public void setLevelData(@NotNull LevelData levelData) {
        this.levelData = levelData;
    }

    @NotNull
    public LevelData getLevelData() {
        return levelData;
    }

    public void addSelectedPlaceable(String name) {
        if (!selectedPlaceables.contains(name)) {
            selectedPlaceables.add(name);
            if (onSelectedPlaceablesChanged != null) {
                onSelectedPlaceablesChanged.execute();
            }
        }
    }

    public void removeSelectedPlaceable(String name) {
        if (selectedPlaceables.contains(name)) {
            selectedPlaceables.remove(name);
            if (onSelectedPlaceablesChanged != null) {
                onSelectedPlaceablesChanged.execute();
            }
        }
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
        if (onSelectedPlaceablesChanged != null) {
            onSelectedPlaceablesChanged.execute();
        }
    }

    public void clearSelectedPlaceables() {
        if (selectedPlaceables.size() != 0) {
            selectedPlaceables.clear();
            if (onSelectedPlaceablesChanged != null) {
                onSelectedPlaceablesChanged.execute();
            }
        }
    }

    public Set<String> getSelectedPlaceables() {
        return selectedPlaceables;
    }

    public void setOnSelectedPlaceablesChanged(@Nullable UIAction onSelectedPlaceablesChanged) {
        this.onSelectedPlaceablesChanged = onSelectedPlaceablesChanged;
    }

    public void addSelectedObject(String name) {
        if (!selectedObjects.contains(name)) {
            selectedObjects.add(name);
            if (onSelectedObjectsChanged != null) {
                onSelectedObjectsChanged.execute();
            }
        }
    }

    public void removeSelectedObject(String name) {
        if (selectedObjects.contains(name)) {
            selectedObjects.remove(name);
            if (onSelectedObjectsChanged != null) {
                onSelectedObjectsChanged.execute();
            }
        }
    }

    public boolean isObjectSelected(String name) {
        return selectedObjects.contains(name);
    }

    public void toggleSelectedObject(String name) {
        if (isObjectSelected(name)) {
            removeSelectedObject(name);
        } else {
            addSelectedObject(name);
        }
        if (onSelectedObjectsChanged != null) {
            onSelectedObjectsChanged.execute();
        }
    }

    public void clearSelectedObjects() {
        if (selectedObjects.size() != 0) {
            selectedObjects.clear();
            if (onSelectedObjectsChanged != null) {
                onSelectedObjectsChanged.execute();
            }
        }
    }

    public Set<String> getSelectedObjects() {
        return selectedObjects;
    }

    public void setOnSelectedObjectsChanged(@Nullable UIAction onSelectedObjectsChanged) {
        this.onSelectedObjectsChanged = onSelectedObjectsChanged;
    }

    public void addSelectedExternalBackgroundObject(String name) {
        if (!selectedExternalBackgroundObjects.contains(name)) {
            selectedExternalBackgroundObjects.add(name);
            if (onSelectedExternalBackgroundObjectsChanged != null) {
                onSelectedExternalBackgroundObjectsChanged.execute();
            }
        }
    }

    public void removeSelectedExternalBackgroundObject(String name) {
        if (selectedExternalBackgroundObjects.contains(name)) {
            selectedExternalBackgroundObjects.remove(name);
            if (onSelectedExternalBackgroundObjectsChanged != null) {
                onSelectedExternalBackgroundObjectsChanged.execute();
            }
        }
    }

    public boolean isExternalBackgroundObjectSelected(String name) {
        return selectedExternalBackgroundObjects.contains(name);
    }

    public void toggleSelectedExternalBackgroundObject(String name) {
        if (isExternalBackgroundObjectSelected(name)) {
            removeSelectedExternalBackgroundObject(name);
        } else {
            addSelectedExternalBackgroundObject(name);
        }
        if (onSelectedExternalBackgroundObjectsChanged != null) {
            onSelectedExternalBackgroundObjectsChanged.execute();
        }
    }

    public void clearSelectedExternalBackgroundObjects() {
        if (selectedExternalBackgroundObjects.size() != 0) {
            selectedExternalBackgroundObjects.clear();
            if (onSelectedExternalBackgroundObjectsChanged != null) {
                onSelectedExternalBackgroundObjectsChanged.execute();
            }
        }
    }

    public Set<String> getSelectedExternalBackgroundObjects() {
        return selectedExternalBackgroundObjects;
    }

    public void setOnSelectedExternalBackgroundObjectsChanged(@Nullable UIAction onSelectedExternalBackgroundObjectsChanged) {
        this.onSelectedExternalBackgroundObjectsChanged = onSelectedExternalBackgroundObjectsChanged;
    }

    public void addHiddenObject(String name) {
        if (!hiddenObjects.contains(name)) {
            hiddenObjects.add(name);
            if (onHiddenObjectsChanged != null) {
                onHiddenObjectsChanged.execute();
            }
        }
    }

    public void removeHiddenObject(String name) {
        if (hiddenObjects.contains(name)) {
            hiddenObjects.remove(name);
            if (onHiddenObjectsChanged != null) {
                onHiddenObjectsChanged.execute();
            }
        }
    }

    public boolean isObjectHidden(String name) {
        return hiddenObjects.contains(name);
    }

    public void toggleHiddenObject(String name) {
        if (isObjectHidden(name)) {
            removeHiddenObject(name);
        } else {
            addHiddenObject(name);
        }
        if (onHiddenObjectsChanged != null) {
            onHiddenObjectsChanged.execute();
        }
    }

    public void clearHiddenObjects() {
        if (hiddenObjects.size() != 0) {
            hiddenObjects.clear();
            if (onHiddenObjectsChanged != null) {
                onHiddenObjectsChanged.execute();
            }
        }
    }

    public Set<String> getHiddenObjects() {
        return hiddenObjects;
    }

    public void setOnHiddenObjectsChanged(@Nullable UIAction onHiddenObjectsChanged) {
        this.onHiddenObjectsChanged = onHiddenObjectsChanged;
    }

    public void setTimelinePos(float timelinePos) {
        if (timelinePos != this.timelinePos) {
            this.timelinePos = timelinePos;
            if (onTimelinePosChanged != null) {
                onTimelinePosChanged.execute(timelinePos);
            }

            currentFrameObjectAnimDataMap.clear(); //Clear non-keyframed changes
        }
    }

    public float getTimelinePos() {
        return timelinePos;
    }

    public void setOnTimelinePosChanged(@Nullable UIAction1<Float> onTimelinePosChanged) {
        this.onTimelinePosChanged = onTimelinePosChanged;
    }

    public void setPlaybackSpeed(float playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
    }

    public float getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setMaxTime(float maxTime) {
        this.maxTime = maxTime;
    }

    public float getMaxTime() {
        return maxTime;
    }

    public void update(float deltaTime) {
        float newPos = timelinePos + (deltaTime / maxTime * playbackSpeed);

        if (newPos > 1) {
            setTimelinePos(1);
            setPlaybackSpeed(0);
        } else if (newPos < 0) {
            setTimelinePos(0);
            setPlaybackSpeed(0);
        } else {
            setTimelinePos(newPos);
        }
    }

    public boolean doesCurrentFrameObjectHaveAnimData(String name) {
        return currentFrameObjectAnimDataMap.containsKey(name);
    }

    public void addCurrentFrameAnimData(Set<String> selectedObjects) {
        for (String name : selectedObjects) {
            if (!currentFrameObjectAnimDataMap.containsKey(name)) { //If the object doesn't already have animation data
                currentFrameObjectAnimDataMap.put(name, new AnimData());
            }
        }
    }

    public AnimData getCurrentFrameObjectAnimData(String name) {
        return currentFrameObjectAnimDataMap.get(name);
    }

    public void clearCurrentFrameObjectAnimData() {
        currentFrameObjectAnimDataMap.clear();
    }

    public NamedTransform getObjectNamedTransform(String name, float time) {
        AnimData animData = new AnimData();

        if (getLevelData().doesObjectHaveAnimData(name)) {
            animData.mergeWith(getLevelData().getObjectAnimData(name));
        }

        if (doesCurrentFrameObjectHaveAnimData(name)) {
            animData.mergeWith(getCurrentFrameObjectAnimData(name));
        }

        return animData.getNamedTransformAtTime(time, name);
    }

}
