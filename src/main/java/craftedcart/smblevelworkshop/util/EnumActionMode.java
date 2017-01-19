package craftedcart.smblevelworkshop.util;

/**
 * @author CraftedCart
 *         Created on 08/09/2016 (DD/MM/YYYY)
 */
public enum EnumActionMode {
    NONE,
    GRAB_PLACEABLE,
    ROTATE_PLACEABLE,
    SCALE_PLACEABLE,
    GRAB_KEYFRAME,
    SCALE_KEYFRAME;

    public boolean isPlaceableMode() {
        int pos = ordinal();
        return pos == 1 || pos == 2 || pos == 3;
    }

}
