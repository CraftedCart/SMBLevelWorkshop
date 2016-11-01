package craftedcart.smblevelworkshop.animation;

import craftedcart.smblevelworkshop.util.PosXYZ;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class AnimData {

    private PosXYZ rotationCenter;

    private Map<Float, AnimPosFrame> posFrames = new HashMap<>();

    public void setRotationCenter(PosXYZ rotationCenter) {
        this.rotationCenter = rotationCenter;
    }

    public PosXYZ getRotationCenter() {
        return rotationCenter;
    }

    public void setPosFrame(float pos, AnimPosFrame posFrame) {
        posFrames.put(pos, posFrame);
    }

    public AnimPosFrame getPosFrame(float pos) {
        return posFrames.get(pos);
    }

}
