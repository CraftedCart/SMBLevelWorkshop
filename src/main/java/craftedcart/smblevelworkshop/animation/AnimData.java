package craftedcart.smblevelworkshop.animation;

import craftedcart.smblevelworkshop.util.MathUtils;
import craftedcart.smblevelworkshop.util.PosXYZ;
import craftedcart.smblevelworkshop.util.QuickMapEntry;
import craftedcart.smbworkshopexporter.ConfigAnimData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
@Deprecated
public class AnimData {

    @NotNull protected PosXYZ rotationCenter = new PosXYZ();

    @Deprecated protected TreeMap<Float, Float> posXFrames = new TreeMap<>();
    @Deprecated protected TreeMap<Float, Float> posYFrames = new TreeMap<>();
    @Deprecated protected TreeMap<Float, Float> posZFrames = new TreeMap<>();

    @Deprecated protected TreeMap<Float, Float> rotXFrames = new TreeMap<>();
    @Deprecated protected TreeMap<Float, Float> rotYFrames = new TreeMap<>();
    @Deprecated protected TreeMap<Float, Float> rotZFrames = new TreeMap<>();

    @Deprecated
    public AnimData() {}

    /**
     * @param cad ConfigAnimData (NOT A COPY CONSTRUCTOR! WILL NOT COPY THE CONFIG ANIM DATA!)
     */
    @Deprecated
    public AnimData(ConfigAnimData cad) {
        rotationCenter = new PosXYZ(cad.getRotationCenter().x, cad.getRotationCenter().y, cad.getRotationCenter().z);

        posXFrames = cad.getPosXFrames();
        posYFrames = cad.getPosYFrames();
        posZFrames = cad.getPosZFrames();

        rotXFrames = cad.getRotXFrames();
        rotYFrames = cad.getRotYFrames();
        rotZFrames = cad.getRotZFrames();
    }

    @Deprecated
    public void setRotationCenter(@NotNull PosXYZ rotationCenter) {
        this.rotationCenter = rotationCenter;
    }

    @NotNull
    @Deprecated
    public PosXYZ getRotationCenter() {
        return rotationCenter;
    }

    @Deprecated
    public NamedTransform getNamedTransformAtTime(float time, @Nullable String name) {
        NamedTransform transform = new NamedTransform(name);

        //<editor-fold desc="Pos">
        Map.Entry<Float, Float> floorPosX = posXFrames.floorEntry(time);
        Map.Entry<Float, Float> floorPosY = posYFrames.floorEntry(time);
        Map.Entry<Float, Float> floorPosZ = posZFrames.floorEntry(time);

        Map.Entry<Float, Float> ceilPosX = posXFrames.ceilingEntry(time);
        Map.Entry<Float, Float> ceilPosY = posYFrames.ceilingEntry(time);
        Map.Entry<Float, Float> ceilPosZ = posZFrames.ceilingEntry(time);

        if (floorPosX == null) floorPosX = new QuickMapEntry<>(0.0f, 0.0f);
        if (floorPosY == null) floorPosY = new QuickMapEntry<>(0.0f, 0.0f);
        if (floorPosZ == null) floorPosZ = new QuickMapEntry<>(0.0f, 0.0f);

        if (ceilPosX == null) ceilPosX = floorPosX;
        if (ceilPosY == null) ceilPosY = floorPosY;
        if (ceilPosZ == null) ceilPosZ = floorPosZ;

        float percentPosX = 0.0f;
        float percentPosY = 0.0f;
        float percentPosZ = 0.0f;

        if (ceilPosX.getKey() != 0 && !Objects.equals(floorPosX.getKey(), ceilPosX.getKey())) percentPosX = (time - floorPosX.getKey()) / (ceilPosX.getKey() - floorPosX.getKey());
        if (ceilPosY.getKey() != 0 && !Objects.equals(floorPosY.getKey(), ceilPosY.getKey())) percentPosY = (time - floorPosY.getKey()) / (ceilPosY.getKey() - floorPosY.getKey());
        if (ceilPosZ.getKey() != 0 && !Objects.equals(floorPosZ.getKey(), ceilPosZ.getKey())) percentPosZ = (time - floorPosZ.getKey()) / (ceilPosZ.getKey() - floorPosZ.getKey());

        double posX = MathUtils.lerp(floorPosX.getValue(), ceilPosX.getValue(), MathUtils.cubicEaseInOut(percentPosX));
        double posY = MathUtils.lerp(floorPosY.getValue(), ceilPosY.getValue(), MathUtils.cubicEaseInOut(percentPosY));
        double posZ = MathUtils.lerp(floorPosZ.getValue(), ceilPosZ.getValue(), MathUtils.cubicEaseInOut(percentPosZ));

        transform.setPosition(new PosXYZ(posX, posY, posZ));
        //</editor-fold>

        //<editor-fold desc="Rot">
        Map.Entry<Float, Float> floorRotX = rotXFrames.floorEntry(time);
        Map.Entry<Float, Float> floorRotY = rotYFrames.floorEntry(time);
        Map.Entry<Float, Float> floorRotZ = rotZFrames.floorEntry(time);

        Map.Entry<Float, Float> ceilRotX = rotXFrames.ceilingEntry(time);
        Map.Entry<Float, Float> ceilRotY = rotYFrames.ceilingEntry(time);
        Map.Entry<Float, Float> ceilRotZ = rotZFrames.ceilingEntry(time);

        if (floorRotX == null) floorRotX = new QuickMapEntry<>(0.0f, 0.0f);
        if (floorRotY == null) floorRotY = new QuickMapEntry<>(0.0f, 0.0f);
        if (floorRotZ == null) floorRotZ = new QuickMapEntry<>(0.0f, 0.0f);

        if (ceilRotX == null) ceilRotX = floorRotX;
        if (ceilRotY == null) ceilRotY = floorRotY;
        if (ceilRotZ == null) ceilRotZ = floorRotZ;

        float percentRotX = 0.0f;
        float percentRotY = 0.0f;
        float percentRotZ = 0.0f;

        if (ceilRotX.getKey() != 0 && !Objects.equals(floorRotX.getKey(), ceilRotX.getKey())) percentRotX = (time - floorRotX.getKey()) / (ceilRotX.getKey() - floorRotX.getKey());
        if (ceilRotY.getKey() != 0 && !Objects.equals(floorRotY.getKey(), ceilRotY.getKey())) percentRotY = (time - floorRotY.getKey()) / (ceilRotY.getKey() - floorRotY.getKey());
        if (ceilRotZ.getKey() != 0 && !Objects.equals(floorRotZ.getKey(), ceilRotZ.getKey())) percentRotZ = (time - floorRotZ.getKey()) / (ceilRotZ.getKey() - floorRotZ.getKey());

        double rotX = MathUtils.lerp(floorRotX.getValue(), ceilRotX.getValue(), MathUtils.cubicEaseInOut(percentRotX));
        double rotY = MathUtils.lerp(floorRotY.getValue(), ceilRotY.getValue(), MathUtils.cubicEaseInOut(percentRotY));
        double rotZ = MathUtils.lerp(floorRotZ.getValue(), ceilRotZ.getValue(), MathUtils.cubicEaseInOut(percentRotZ));

        transform.setRotation(new PosXYZ(rotX, rotY, rotZ));
        //</editor-fold>

        return transform;
    }

    //Pos
    @Deprecated
    public void setPosXFrame(float time, float pos) {
        posXFrames.put(time, pos);
    }

    @Deprecated
    public void setPosYFrame(float time, float pos) {
        posYFrames.put(time, pos);
    }

    @Deprecated
    public void setPosZFrame(float time, float pos) {
        posZFrames.put(time, pos);
    }

    @Deprecated
    public void removePosXFrame(float time) {
        posXFrames.remove(time);
    }

    @Deprecated
    public void removePosYFrame(float time) {
        posYFrames.remove(time);
    }

    @Deprecated
    public void removePosZFrame(float time) {
        posZFrames.remove(time);
    }

    @Deprecated
    public TreeMap<Float, Float> getPosXFrames() {
        return posXFrames;
    }

    @Deprecated
    public TreeMap<Float, Float> getPosYFrames() {
        return posYFrames;
    }

    @Deprecated
    public TreeMap<Float, Float> getPosZFrames() {
        return posZFrames;
    }
    
    //Rot
    @Deprecated
    public void setRotXFrame(float time, float rot) {
        rotXFrames.put(time, rot);
    }

    @Deprecated
    public void setRotYFrame(float time, float rot) {
        rotYFrames.put(time, rot);
    }

    @Deprecated
    public void setRotZFrame(float time, float rot) {
        rotZFrames.put(time, rot);
    }

    @Deprecated
    public void removeRotXFrame(float time) {
        rotXFrames.remove(time);
    }

    @Deprecated
    public void removeRotYFrame(float time) {
        rotYFrames.remove(time);
    }

    @Deprecated
    public void removeRotZFrame(float time) {
        rotZFrames.remove(time);
    }

    @Deprecated
    public TreeMap<Float, Float> getRotXFrames() {
        return rotXFrames;
    }

    @Deprecated
    public TreeMap<Float, Float> getRotYFrames() {
        return rotYFrames;
    }

    @Deprecated
    public TreeMap<Float, Float> getRotZFrames() {
        return rotZFrames;
    }

    @Deprecated
    public void mergeWith(AnimData other) {
        posXFrames.putAll(other.posXFrames);
        posYFrames.putAll(other.posYFrames);
        posZFrames.putAll(other.posZFrames);

        rotXFrames.putAll(other.rotXFrames);
        rotYFrames.putAll(other.rotYFrames);
        rotZFrames.putAll(other.rotZFrames);
    }

    @Deprecated
    public AnimData mergeWithCopy(AnimData other) {
        AnimData ad = getCopy();

        ad.posXFrames = new TreeMap<>(posXFrames);
        ad.posXFrames.putAll(other.posXFrames);
        ad.posYFrames = new TreeMap<>(posYFrames);
        ad.posYFrames.putAll(other.posYFrames);
        ad.posZFrames = new TreeMap<>(posZFrames);
        ad.posZFrames.putAll(other.posZFrames);

        ad.rotXFrames = new TreeMap<>(rotXFrames);
        ad.rotXFrames.putAll(other.rotXFrames);
        ad.rotYFrames = new TreeMap<>(rotYFrames);
        ad.rotYFrames.putAll(other.rotYFrames);
        ad.rotZFrames = new TreeMap<>(rotZFrames);
        ad.rotZFrames.putAll(other.rotZFrames);

        return ad;
    }

    @Deprecated
    public void moveFirstFrame(float percent) {
        //Pos
        Map.Entry<Float, Float> firstPosX = posXFrames.firstEntry();
        if (firstPosX != null) {
            posXFrames.remove(firstPosX.getKey());
            posXFrames.put(percent, firstPosX.getValue());
        }

        Map.Entry<Float, Float> firstPosY = posYFrames.firstEntry();
        if (firstPosY != null) {
            posYFrames.remove(firstPosY.getKey());
            posYFrames.put(percent, firstPosY.getValue());
        }

        Map.Entry<Float, Float> firstPosZ = posZFrames.firstEntry();
        if (firstPosZ != null) {
            posXFrames.remove(firstPosZ.getKey());
            posXFrames.put(percent, firstPosZ.getValue());
        }

        //Rot
        Map.Entry<Float, Float> firstRotX = rotXFrames.firstEntry();
        if (firstRotX != null) {
            rotXFrames.remove(firstRotX.getKey());
            rotXFrames.put(percent, firstRotX.getValue());
        }

        Map.Entry<Float, Float> firstRotY = rotYFrames.firstEntry();
        if (firstRotY != null) {
            rotYFrames.remove(firstRotY.getKey());
            rotYFrames.put(percent, firstRotY.getValue());
        }

        Map.Entry<Float, Float> firstRotZ = rotZFrames.firstEntry();
        if (firstRotZ != null) {
            rotXFrames.remove(firstRotZ.getKey());
            rotXFrames.put(percent, firstRotZ.getValue());
        }
    }

    @Deprecated
    public AnimData getCopy() {
        PosXYZ rotationCenterClone = rotationCenter.getCopy();

        TreeMap<Float, Float> posXFramesClone = new TreeMap<>(posXFrames);
        TreeMap<Float, Float> posYFramesClone = new TreeMap<>(posYFrames);
        TreeMap<Float, Float> posZFramesClone = new TreeMap<>(posZFrames);

        TreeMap<Float, Float> rotXFramesClone = new TreeMap<>(rotXFrames);
        TreeMap<Float, Float> rotYFramesClone = new TreeMap<>(rotYFrames);
        TreeMap<Float, Float> rotZFramesClone = new TreeMap<>(rotZFrames);

        AnimData animDataClone = new AnimData();

        animDataClone.rotationCenter = rotationCenterClone;

        animDataClone.posXFrames = posXFramesClone;
        animDataClone.posYFrames = posYFramesClone;
        animDataClone.posZFrames = posZFramesClone;

        animDataClone.rotXFrames = rotXFramesClone;
        animDataClone.rotYFrames = rotYFramesClone;
        animDataClone.rotZFrames = rotZFramesClone;

        return animDataClone;
    }

    @Deprecated
    public void clampKeyframeTimes() {
        NamedTransform time0 = getNamedTransformAtTime(0.0f, null);
        NamedTransform time1 = getNamedTransformAtTime(1.0f, null);

        //Pos
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
        
        //Rot
        if (rotXFrames.size() > 0 && rotXFrames.firstKey() < 0) {
            rotXFrames.put(0.0f, (float) time0.getRotation().x);
        }
        if (rotYFrames.size() > 0 && rotYFrames.firstKey() < 0) {
            rotYFrames.put(0.0f, (float) time0.getRotation().y);
        }
        if (rotZFrames.size() > 0 && rotZFrames.firstKey() < 0) {
            rotZFrames.put(0.0f, (float) time0.getRotation().z);
        }

        if (rotXFrames.size() > 0 && rotXFrames.lastKey() > 1) {
            rotXFrames.put(1.0f, (float) time1.getRotation().x);
        }
        if (rotYFrames.size() > 0 && rotYFrames.lastKey() > 1) {
            rotYFrames.put(1.0f, (float) time1.getRotation().y);
        }
        if (rotZFrames.size() > 0 && rotZFrames.lastKey() > 1) {
            rotZFrames.put(1.0f, (float) time1.getRotation().z);
        }

        posXFrames.headMap(0.0f, false).clear();
        posYFrames.headMap(0.0f, false).clear();
        posZFrames.headMap(0.0f, false).clear();
        posXFrames.tailMap(1.0f, false).clear();
        posYFrames.tailMap(1.0f, false).clear();
        posZFrames.tailMap(1.0f, false).clear();

        rotXFrames.headMap(0.0f, false).clear();
        rotYFrames.headMap(0.0f, false).clear();
        rotZFrames.headMap(0.0f, false).clear();
        rotXFrames.tailMap(1.0f, false).clear();
        rotYFrames.tailMap(1.0f, false).clear();
        rotZFrames.tailMap(1.0f, false).clear();
    }

}
