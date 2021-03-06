package craftedcart.smblevelworkshop.animation;

import craftedcart.smblevelworkshop.util.ITransformable;
import craftedcart.smblevelworkshop.util.PosXYZ;

/**
 * @author CraftedCart
 *         Created on 03/11/2016 (DD/MM/YYYY)
 */
public class NamedTransform implements ITransformable {

    private String name;
    private PosXYZ position = new PosXYZ();
    private PosXYZ rotation = new PosXYZ();

    public NamedTransform(String name) {
        this.name = name;
    }

    @Override
    public void setPosition(PosXYZ position) {
        this.position = position;
    }

    @Override
    public PosXYZ getPosition() {
        return position;
    }

    @Override
    public void setRotation(PosXYZ rotation) {
        this.rotation = rotation;
    }

    @Override
    public PosXYZ getRotation() {
        return rotation;
    }

    public String getName() {
        return name;
    }

}
