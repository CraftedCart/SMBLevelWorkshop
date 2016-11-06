package craftedcart.smblevelworkshop.animation;

import craftedcart.smblevelworkshop.util.MathUtils;
import craftedcart.smblevelworkshop.util.PosXYZ;
import craftedcart.smblevelworkshop.util.QuickMapEntry;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class AnimData {

    private PosXYZ rotationCenter;

    private TreeMap<Float, Float> posXFrames = new TreeMap<>();
    private TreeMap<Float, Float> posYFrames = new TreeMap<>();
    private TreeMap<Float, Float> posZFrames = new TreeMap<>();

    public void setRotationCenter(PosXYZ rotationCenter) {
        this.rotationCenter = rotationCenter;
    }

    public PosXYZ getRotationCenter() {
        return rotationCenter;
    }

    public NamedTransform getNamedTransformAtTime(float time, String name) {
        NamedTransform transform = new NamedTransform(name);

        Map.Entry<Float, Float> floorX = posXFrames.floorEntry(time);
        Map.Entry<Float, Float> floorY = posYFrames.floorEntry(time);
        Map.Entry<Float, Float> floorZ = posZFrames.floorEntry(time);

        Map.Entry<Float, Float> ceilX = posXFrames.ceilingEntry(time);
        Map.Entry<Float, Float> ceilY = posYFrames.ceilingEntry(time);
        Map.Entry<Float, Float> ceilZ = posZFrames.ceilingEntry(time);

        if (floorX == null) floorX = new QuickMapEntry<>(0.0f, 0.0f);
        if (floorY == null) floorY = new QuickMapEntry<>(0.0f, 0.0f);
        if (floorZ == null) floorZ = new QuickMapEntry<>(0.0f, 0.0f);

        if (ceilX == null) ceilX = floorX;
        if (ceilY == null) ceilY = floorY;
        if (ceilZ == null) ceilZ = floorZ;

        float percentX = 0.0f;
        float percentY = 0.0f;
        float percentZ = 0.0f;

        if (ceilX.getKey() != 0 && !Objects.equals(floorX.getKey(), ceilX.getKey())) percentX = (time - floorX.getKey()) / (ceilX.getKey() - floorX.getKey());
        if (ceilY.getKey() != 0 && !Objects.equals(floorY.getKey(), ceilY.getKey())) percentY = (time - floorY.getKey()) / (ceilY.getKey() - floorY.getKey());
        if (ceilZ.getKey() != 0 && !Objects.equals(floorZ.getKey(), ceilZ.getKey())) percentZ = (time - floorZ.getKey()) / (ceilZ.getKey() - floorZ.getKey());

        double x = MathUtils.lerp(floorX.getValue(), ceilX.getValue(), percentX);
        double y = MathUtils.lerp(floorY.getValue(), ceilY.getValue(), percentY);
        double z = MathUtils.lerp(floorZ.getValue(), ceilZ.getValue(), percentZ);

        transform.setPosition(new PosXYZ(x, y, z));

        return transform;
    }

    public void setPosXFrame(float time, float pos) {
        posXFrames.put(time, pos);
    }

    public void setPosYFrame(float time, float pos) {
        posYFrames.put(time, pos);
    }

    public void setPosZFrame(float time, float pos) {
        posZFrames.put(time, pos);
    }

    public void mergeWith(AnimData other) {
        posXFrames.putAll(other.posXFrames);
        posYFrames.putAll(other.posYFrames);
        posZFrames.putAll(other.posZFrames);
    }

    public TreeMap<Float, Float> getPosXFrames() {
        return posXFrames;
    }

    public TreeMap<Float, Float> getPosYFrames() {
        return posYFrames;
    }

    public TreeMap<Float, Float> getPosZFrames() {
        return posZFrames;
    }

}
