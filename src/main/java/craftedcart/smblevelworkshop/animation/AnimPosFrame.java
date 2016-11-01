package craftedcart.smblevelworkshop.animation;

import craftedcart.smblevelworkshop.util.ITransformable;
import craftedcart.smblevelworkshop.util.PosXYZ;

/**
 * @author CraftedCart
 *         Created on 01/11/2016 (DD/MM/YYYY)
 */
public class AnimPosFrame implements ITransformable {

    private PosXYZ position;

    @Override
    public void setPosition(PosXYZ pos) {
        this.position = pos;
    }

    @Override
    public PosXYZ getPosition() {
        return position;
    }

}
