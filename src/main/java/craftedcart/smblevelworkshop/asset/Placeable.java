package craftedcart.smblevelworkshop.asset;

import craftedcart.smblevelworkshop.util.PosXYZ;
import org.jetbrains.annotations.NotNull;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class Placeable {

    @NotNull private IAsset asset;

    @NotNull private PosXYZ position = new PosXYZ();
    @NotNull private PosXYZ rotation = new PosXYZ();
    @NotNull private PosXYZ scale = new PosXYZ(1, 1, 1);

    public Placeable(IAsset asset) {
        this.asset = asset;
    }

    public void setAsset(IAsset asset) {
        this.asset = asset;
    }

    public IAsset getAsset() {
        return asset;
    }

    public void setPosition(PosXYZ position) {
        this.position = position;
    }

    public PosXYZ getPosition() {
        return position;
    }

    public void setRotation(PosXYZ rotation) {
        this.rotation = rotation;
    }

    public PosXYZ getRotation() {
        return rotation;
    }

    public void setScale(PosXYZ scale) {
        this.scale = scale;
    }

    public PosXYZ getScale() {
        return scale;
    }

}
