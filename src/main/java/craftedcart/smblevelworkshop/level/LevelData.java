package craftedcart.smblevelworkshop.level;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.util.PosXYZ;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 08/09/2016 (DD/MM/YYYY)
 */
public class LevelData {

    @Nullable private ResourceModel model;
    @Nullable private File modelObjSource;
    @NotNull private Map<String, Placeable> placedObjects = new HashMap<>();

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

}
