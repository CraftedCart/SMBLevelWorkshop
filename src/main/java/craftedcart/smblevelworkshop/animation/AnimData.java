package craftedcart.smblevelworkshop.animation;

import craftedcart.smblevelworkshop.util.LogHelper;
import craftedcart.smblevelworkshop.util.MathUtils;
import craftedcart.smblevelworkshop.util.PosXYZ;
import craftedcart.smblevelworkshop.util.QuickMapEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class AnimData {

    @NotNull protected PosXYZ rotationCenter = new PosXYZ();

    protected TreeMap<Float, Float> posXFrames = new TreeMap<>();
    protected TreeMap<Float, Float> posYFrames = new TreeMap<>();
    protected TreeMap<Float, Float> posZFrames = new TreeMap<>();

    public void setRotationCenter(PosXYZ rotationCenter) {
        this.rotationCenter = rotationCenter;
    }

    @NotNull
    public PosXYZ getRotationCenter() {
        return rotationCenter;
    }

    public NamedTransform getNamedTransformAtTime(float time, @Nullable String name) {
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

    public void removePosXFrame(float time) {
        posXFrames.remove(time);
    }

    public void removePosYFrame(float time) {
        posYFrames.remove(time);
    }

    public void removePosZFrame(float time) {
        posZFrames.remove(time);
    }

    public void movePosXFrame(float time, float newTime) {
        float pos = posXFrames.get(time);
        posXFrames.remove(time);
        posXFrames.put(newTime, pos);
    }

    public void movePosYFrame(float time, float newTime) {
        float pos = posYFrames.get(time);
        posYFrames.remove(time);
        posYFrames.put(newTime, pos);
    }

    public void movePosZFrame(float time, float newTime) {
        float pos = posZFrames.get(time);
        posZFrames.remove(time);
        posZFrames.put(newTime, pos);
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

    public void mergeWith(AnimData other) {
        posXFrames.putAll(other.posXFrames);
        posYFrames.putAll(other.posYFrames);
        posZFrames.putAll(other.posZFrames);

        //TODO: Rotation
    }

    public AnimData mergeWithCopy(AnimData other) {
        AnimData ad = getCopy();

        ad.posXFrames = new TreeMap<>(posXFrames);
        ad.posXFrames.putAll(other.posXFrames);
        ad.posYFrames = new TreeMap<>(posYFrames);
        ad.posYFrames.putAll(other.posYFrames);
        ad.posZFrames = new TreeMap<>(posZFrames);
        ad.posZFrames.putAll(other.posZFrames);

        //TODO: Rotation

        return ad;
    }

    public void moveFirstFrame(float percent) {
        Map.Entry<Float, Float> firstX = posXFrames.firstEntry();
        if (firstX != null) {
            posXFrames.remove(firstX.getKey());
            posXFrames.put(percent, firstX.getValue());
        }

        Map.Entry<Float, Float> firstY = posYFrames.firstEntry();
        if (firstY != null) {
            posYFrames.remove(firstY.getKey());
            posYFrames.put(percent, firstY.getValue());
        }

        Map.Entry<Float, Float> firstZ = posZFrames.firstEntry();
        if (firstZ != null) {
            posXFrames.remove(firstZ.getKey());
            posXFrames.put(percent, firstZ.getValue());
        }

        //TODO: Rotation
    }

    public AnimData getCopy() {
        PosXYZ rotationCenterClone = rotationCenter.getCopy();
        TreeMap<Float, Float> posXFramesClone = new TreeMap<>(posXFrames);
        TreeMap<Float, Float> posYFramesClone = new TreeMap<>(posYFrames);
        TreeMap<Float, Float> posZFramesClone = new TreeMap<>(posZFrames);

        AnimData animDataClone = new AnimData();
        animDataClone.rotationCenter = rotationCenterClone;
        animDataClone.posXFrames = posXFramesClone;
        animDataClone.posYFrames = posYFramesClone;
        animDataClone.posZFrames = posZFramesClone;

        return animDataClone;
    }

    public void clampKeyframeTimes() {
        NamedTransform time0 = getNamedTransformAtTime(0.0f, null);
        NamedTransform time1 = getNamedTransformAtTime(1.0f, null);

        if (posXFrames.size() > 0 && posXFrames.firstKey() < 0) {
            posXFrames.put(0.0f, (float) time0.getPosition().x);
        }
        if (posYFrames.size() > 0 && posYFrames.firstKey() < 0) {
            posYFrames.put(0.0f, (float) time0.getPosition().y);
        }
        if (posZFrames.size() > 0 && posZFrames.firstKey() < 0) {
            posZFrames.put(0.0f, (float) time0.getPosition().z);
        }

        if (posXFrames.size() > 0 && posXFrames.lastKey() > 1) {
            posXFrames.put(1.0f, (float) time1.getPosition().x);
        }
        if (posYFrames.size() > 0 && posYFrames.lastKey() > 1) {
            posYFrames.put(1.0f, (float) time1.getPosition().y);
        }
        if (posZFrames.size() > 0 && posZFrames.lastKey() > 1) {
            posZFrames.put(1.0f, (float) time1.getPosition().z);
        }

        posXFrames.headMap(0.0f, false).clear();
        posYFrames.headMap(0.0f, false).clear();
        posZFrames.headMap(0.0f, false).clear();
        posXFrames.tailMap(1.0f, false).clear();
        posYFrames.tailMap(1.0f, false).clear();
        posZFrames.tailMap(1.0f, false).clear();

        //TODO: Rotation
    }
}
