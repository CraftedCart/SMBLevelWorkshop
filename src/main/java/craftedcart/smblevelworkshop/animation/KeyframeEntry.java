package craftedcart.smblevelworkshop.animation;

/**
 * @author CraftedCart
 *         Created on 01/01/2017 (DD/MM/YYYY)
 */
public class KeyframeEntry {

    private String objectName;
    private float time;

    public KeyframeEntry(String objectName, float time) {
        this.objectName = objectName;
        this.time = time;
    }

    public String getObjectName() {
        return objectName;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
