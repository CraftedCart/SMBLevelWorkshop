package craftedcart.smblevelworkshop.level;

import craftedcart.smblevelworkshop.animation.AnimData;
import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

/**
 * @author CraftedCart
 *         Created on 08/09/2016 (DD/MM/YYYY)
 */
public class LevelData {

    @Nullable private ResourceModel model;
    @Nullable private File modelObjSource;
    @NotNull private Map<String, Placeable> placedObjects = new HashMap<>();
    @NotNull private Set<String> backgroundObjects = new HashSet<>();
    @NotNull private Set<String> backgroundExternalObjects = new HashSet<>();
    @NotNull private TreeMap<String, AnimData> objectAnimDataMap = new TreeMap<>();
    private float leadInTime = 6.0f;
    private float maxTime = 60.0f;

    public void setModel(@Nullable ResourceModel model) {
        this.model = model;
    }

    @Nullable
    public ResourceModel getModel() {
        return model;
    }

    public void setModelObjSource(@Nullable File modelObjSource) {
        this.modelObjSource = modelObjSource;
    }

    @Nullable
    public File getModelObjSource() {
        return modelObjSource;
    }

    @NotNull
    public Map<String, Placeable> getPlacedObjects() {
        return placedObjects;
    }

    /**
     * @param name The unique name for the placeable
     * @param placeable The placeable object to add
     * @return The name of the placeable
     */
    public String addPlaceable(String name, Placeable placeable) {
        placedObjects.put(name, placeable);
        return name;
    }

    /**
     * @param placeable The placeable object to add
     * @return The name of the placeable
     */
    public String addPlaceable(Placeable placeable) {
        String name = LangManager.getItem(placeable.getAsset().getName()) + " 1";

        int i = 1;
        while (placedObjects.containsKey(name)) {
            i++;
            name = LangManager.getItem(placeable.getAsset().getName()) + " " + String.valueOf(i);
        }

        placedObjects.put(name, placeable);
        return name;
    }

    public void removePlaceable(String name) {
        placedObjects.remove(name);
    }

    public Placeable getPlaceable(String name) {
        return placedObjects.get(name);
    }

    public void replacePlaceable(String name, Placeable placeable) {
        placedObjects.replace(name, placeable);
    }

    public void clearPlacedObjects() {
        placedObjects.clear();
    }

    public void addBackgroundObject(String name) {
        backgroundObjects.add(name);
    }

    public void removeBackgroundObject(String name) {
        if (backgroundObjects.contains(name)) {
            backgroundObjects.remove(name);
        }
    }

    public boolean isObjectBackground(String name) {
        return backgroundObjects.contains(name);
    }

    public void toggleBackgroundObject(String name) {
        if (isObjectBackground(name)) {
            removeBackgroundObject(name);
        } else {
            addBackgroundObject(name);
        }
    }

    public void clearBackgroundObjects() {
        backgroundObjects.clear();
    }

    @NotNull
    public Set<String> getBackgroundObjects() {
        return backgroundObjects;
    }

    public void addBackgroundExternalObject(String name) {
        backgroundExternalObjects.add(name);
    }

    public void removeBackgroundExternalObject(String name) {
        if (backgroundExternalObjects.contains(name)) {
            backgroundExternalObjects.remove(name);
        }
    }

    public boolean isObjectBackgroundExternal(String name) {
        return backgroundExternalObjects.contains(name);
    }

    public void toggleBackgroundExternalObject(String name) {
        if (isObjectBackgroundExternal(name)) {
            removeBackgroundExternalObject(name);
        } else {
            addBackgroundExternalObject(name);
        }
    }

    public void clearBackgroundExternalObjects() {
        backgroundExternalObjects.clear();
    }

    @NotNull
    public Set<String> getBackgroundExternalObjects() {
        return backgroundExternalObjects;
    }

    public boolean doesObjectHaveAnimData(String name) {
        return objectAnimDataMap.containsKey(name);
    }

    public void addAnimData(Set<String> selectedObjects) {
        for (String name : selectedObjects) {
            if (!objectAnimDataMap.containsKey(name)) { //If the object doesn't already have animation data
                objectAnimDataMap.put(name, new AnimData());
            }
        }
    }

    public void setAnimData(String name, AnimData animData) {
        objectAnimDataMap.put(name, animData);
    }

    public AnimData getObjectAnimData(String name) {
        return objectAnimDataMap.get(name);
    }

    @NotNull
    public TreeMap<String, AnimData> getObjectAnimDataMap() {
        return objectAnimDataMap;
    }

    public void removeAnimData(Set<String> selectedObjects) {
        for (String name : selectedObjects) {
            if (objectAnimDataMap.containsKey(name)) { //If the object doesn't already have animation data
                objectAnimDataMap.remove(name);
            }
        }
    }

    public void replaceObjectAnimDataMap(TreeMap<String, AnimData> newMap) {
        objectAnimDataMap.clear();
        objectAnimDataMap.putAll(newMap);
    }

    /**
     * Thread safe
     *
     * @return A deeper clone
     */
    public TreeMap<String, AnimData> getObjectAnimDataMapCopy() {
        TreeMap<String, AnimData> objectAnimDataMapClone = new TreeMap<>(objectAnimDataMap);
        TreeMap<String, AnimData> deeperCloneMap = new TreeMap<>();

        for (Map.Entry<String, AnimData> entry : objectAnimDataMapClone.entrySet()) {
            deeperCloneMap.put(entry.getKey(), entry.getValue().getCopy());
        }

        return deeperCloneMap;
    }

    public void clearAnimData() {
        objectAnimDataMap.clear();
    }

    public float getLeadInTime() {
        return leadInTime;
    }

    public void setLeadInTime(float leadInTime) {
        this.leadInTime = leadInTime;
    }

    public void setMaxTime(float maxTime) {
        this.maxTime = maxTime;
    }

    public float getMaxTime() {
        return maxTime;
    }

}
