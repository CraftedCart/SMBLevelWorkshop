package craftedcart.smblevelworkshop.animation;

import craftedcart.smblevelworkshop.util.MathUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author CraftedCart
 *         Created on 02/01/2017 (DD/MM/YYYY)
 */
public class BufferedAnimData extends AnimData {

    protected float keyframeBufferTranslation = 0.0f;
    protected float keyframeBufferScale = 1.0f;
    protected float keyframeBufferScaleCenter = 0.0f;

    @Nullable protected Float snapToTranslation = null;

    public TreeMap<Float, Float> getFramesBufferTransformed(TreeMap<Float, Float> original) {
        TreeMap<Float, Float> transformedMap = new TreeMap<>();

        float keyframeBufferTranslationSnapped = getSnappedTranslationValue(keyframeBufferTranslation);

        for (Map.Entry<Float, Float> entry : original.entrySet()) {
            transformedMap.put(
                    ((entry.getKey() + keyframeBufferTranslationSnapped + keyframeBufferScaleCenter) * keyframeBufferScale) - keyframeBufferScaleCenter,
                    entry.getValue());
        }

        return transformedMap;
    }

    public TreeMap<Float, Float> getPosXFramesTransformed() {
        return getFramesBufferTransformed(posXFrames);
    }

    public TreeMap<Float, Float> getPosYFramesTransformed() {
        return getFramesBufferTransformed(posYFrames);
    }

    public TreeMap<Float, Float> getPosZFramesTransformed() {
        return getFramesBufferTransformed(posZFrames);
    }

    public TreeMap<Float, Float> getRotXFramesTransformed() {
        return getFramesBufferTransformed(rotXFrames);
    }

    public TreeMap<Float, Float> getRotYFramesTransformed() {
        return getFramesBufferTransformed(rotYFrames);
    }

    public TreeMap<Float, Float> getRotZFramesTransformed() {
        return getFramesBufferTransformed(rotZFrames);
    }

    public AnimData getTransformedAnimData() {
        AnimData ad = new AnimData();

        ad.setRotationCenter(rotationCenter);

        ad.posXFrames = getPosXFramesTransformed();
        ad.posYFrames = getPosYFramesTransformed();
        ad.posZFrames = getPosZFramesTransformed();

        ad.rotXFrames = getRotXFramesTransformed();
        ad.rotYFrames = getRotYFramesTransformed();
        ad.rotZFrames = getRotZFramesTransformed();

        return ad;
    }

    public float getKeyframeBufferTranslation() {
        return keyframeBufferTranslation;
    }

    public float getKeyframeBufferScale() {
        return keyframeBufferScale;
    }

    public float getKeyframeBufferScaleCenter() {
        return keyframeBufferScaleCenter;
    }

    public void setKeyframeBufferTranslation(float keyframeBufferTranslation) {
        this.keyframeBufferTranslation = keyframeBufferTranslation;
    }

    public void setKeyframeBufferScale(float keyframeBufferScale) {
        this.keyframeBufferScale = keyframeBufferScale;
    }

    public void setKeyframeBufferScaleCenter(float keyframeBufferScaleCenter) {
        this.keyframeBufferScaleCenter = keyframeBufferScaleCenter;
    }

    public void setSnapToTranslation(@Nullable Float snapToTranslation) {
        this.snapToTranslation = snapToTranslation;
    }

    public float getSnappedTranslationValue(float in) {
        if (snapToTranslation == null) {
            return in;
        } else {
            return MathUtils.snapTo(in, snapToTranslation);
        }
    }
}
