package craftedcart.smblevelworkshop.util;

import craftedcart.smblevelworkshop.asset.Placeable;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author CraftedCart
 *         Created on 11/04/2017 (DD/MM/YYYY)
 */
public class WSItemGroup {

    @NotNull private UIColor color = UIColor.matGrey(); //Used in the UI

    @NotNull private Set<String> objectNames = new HashSet<>();
    @NotNull private Map<String, Placeable> placeables = new HashMap<>();
    @NotNull private PosXYZ rotationCenter = new PosXYZ();
    @NotNull private PosXYZ initialRotation = new PosXYZ();

    public WSItemGroup() {}

    public WSItemGroup(@NotNull UIColor color) {
        this.color = color;
    }

    @NotNull
    public UIColor getColor() {
        return color;
    }

    public void setColor(@NotNull UIColor color) {
        this.color = color;
    }

    @NotNull
    public Map<String, Placeable> getPlaceables() {
        return placeables;
    }

    public boolean hasPlaceable(String name) {
        return placeables.containsKey(name);
    }

    public void removePlaceable(String name) {
        placeables.remove(name);
    }

    public Placeable replacePlaceable(String name, Placeable placeable) {
        return placeables.replace(name, placeable);
    }

    @Nullable
    public Placeable getPlaceable(String name) {
        return placeables.get(name);
    }

    public void clearPlaceables() {
        placeables.clear();
    }

    /**
     * Will be added to item group 0
     *
     * @param name The unique name for the placeable
     * @param placeable The placeable object to add
     * @return The name of the placeable
     */
    public String addPlaceable(String name, Placeable placeable) {
        placeables.put(name, placeable);
        return name;
    }

    @NotNull
    public PosXYZ getRotationCenter() {
        return rotationCenter;
    }

    public void setRotationCenter(@NotNull PosXYZ rotationCenter) {
        this.rotationCenter = rotationCenter;
    }

    @NotNull
    public PosXYZ getInitialRotation() {
        return initialRotation;
    }

    public void setInitialRotation(@NotNull PosXYZ initialRotation) {
        this.initialRotation = initialRotation;
    }

    public void addObject(String name) {
        objectNames.add(name);
    }

    public void addObjects(Collection<String> collection) {
        objectNames.addAll(collection);
    }

    public void removeObject(String name) {
        objectNames.remove(name);
    }

    public void removeObjects(Collection<String> collection) {
        objectNames.removeAll(collection);
    }


    public void clearObjects() {
        objectNames.clear();
    }

    @NotNull
    public Set<String> getObjectNames() {
        return objectNames;
    }

}
