package craftedcart.smblevelworkshop.asset;

import craftedcart.smblevelworkshop.util.ITransformable;
import craftedcart.smblevelworkshop.util.LogHelper;
import craftedcart.smblevelworkshop.util.PosXYZ;
import craftedcart.smbworkshopexporter.placeables.*;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class Placeable implements Cloneable, ITransformable {

    @NotNull private IAsset asset;

    @NotNull private PosXYZ position = new PosXYZ();
    @NotNull private PosXYZ rotation = new PosXYZ();
    @NotNull private PosXYZ scale = new PosXYZ(1, 1, 1);

    public Placeable(@NotNull IAsset asset) {
        this.asset = asset;
    }

    public Placeable(@NotNull Goal goal) {
        asset = new AssetGoal();
        position = new PosXYZ(goal.pos);
        rotation = new PosXYZ(goal.rot);

        String type;
        switch (goal.type) {
            case BLUE:
                type = "blueGoal";
                break;
            case GREEN:
                type = "greenGoal";
                break;
            case RED:
                type = "redGoal";
                break;
            default:
                //This shouldn't happen! Default to blue
                type = "blueGoal";
        }

        asset.setType(type);
    }

    public Placeable(@NotNull Bumper bumper) {
        asset = new AssetBumper();
        position = new PosXYZ(bumper.pos);
        rotation = new PosXYZ(bumper.rot);
        scale = new PosXYZ(bumper.scl);
    }

    public Placeable(@NotNull Jamabar jamabar) {
        asset = new AssetJamabar();
        position = new PosXYZ(jamabar.pos);
        rotation = new PosXYZ(jamabar.rot);
        scale = new PosXYZ(jamabar.scl);
    }

    public Placeable(@NotNull Banana banana) {
        asset = new AssetBanana();
        position = new PosXYZ(banana.pos);

        String type;
        switch (banana.type) {
            case SINGLE:
                type = "singleBanana";
                break;
            case BUNCH:
                type = "bunchBanana";
                break;
            default:
                //This shouldn't happen! Default to single
                type = "singleBanana";
        }

        asset.setType(type);
    }

    public Placeable(@NotNull Wormhole wormhole) {
        AssetWormhole aWormhole = new AssetWormhole();
        asset = aWormhole;
        position = new PosXYZ(wormhole.pos);
        rotation = new PosXYZ(wormhole.rot);
        aWormhole.setDestinationName(wormhole.destinationName);
    }

    public void setAsset(@NotNull IAsset asset) {
        this.asset = asset;
    }

    @NotNull
    public IAsset getAsset() {
        return asset;
    }

    public void setPosition(@NotNull PosXYZ position) {
        this.position = position;
    }

    @NotNull
    public PosXYZ getPosition() {
        return position;
    }

    @Override
    public boolean canMoveX() {
        return getAsset().canGrabX();
    }

    @Override
    public boolean canMoveY() {
        return getAsset().canGrabY();
    }

    @Override
    public boolean canMoveZ() {
        return getAsset().canGrabZ();
    }

    @Override
    public boolean canRotate() {
        return getAsset().canRotate();
    }

    @Override
    public boolean canScale() {
        return getAsset().canScale();
    }

    public void setRotation(@NotNull PosXYZ rotation) {
        this.rotation = rotation;
    }

    @NotNull
    public PosXYZ getRotation() {
        return rotation;
    }

    public void setScale(@NotNull PosXYZ scale) {
        this.scale = scale;
    }

    @NotNull
    public PosXYZ getScale() {
        return scale;
    }

    public Placeable getCopy() {
        try {
            IAsset newAsset = asset.getCopy();
            Placeable newPlaceable = (Placeable) clone();
            newPlaceable.setAsset(newAsset);
            return newPlaceable;
        } catch (CloneNotSupportedException e) {
            LogHelper.error(getClass(), "Failed to clone Placeable");
            LogHelper.error(getClass(), e);
            return null;
        }
    }

}
