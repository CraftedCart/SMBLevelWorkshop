package craftedcart.smblevelworkshop.util;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class MathUtils {

    public static PosXYZ normalizeRotation(PosXYZ rot) {
        while (rot.x >= 360) {
            rot.x -= 360;
        }

        while (rot.y >= 360) {
            rot.y -= 360;
        }

        while (rot.z >= 360) {
            rot.z -= 360;
        }

        while (rot.x < 0) {
            rot.x += 360;
        }

        while (rot.y < 0) {
            rot.y += 360;
        }

        while (rot.z < 0) {
            rot.z += 360;
        }

        return rot;
    }

}
