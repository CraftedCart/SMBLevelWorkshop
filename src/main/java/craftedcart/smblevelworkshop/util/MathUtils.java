package craftedcart.smblevelworkshop.util;

import craftedcart.smblevelworkshop.SMBLWSettings;
import craftedcart.smblevelworkshop.Window;
import io.github.craftedcart.fluidui.util.UIColor;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class MathUtils {

    public static double lerp(double a, double b, double f) {
        return a + f * (b - a);
    }

    /**
     * Linearly interpolates between two {@link UIColor}s
     *
     * @param a The starting {@link UIColor}
     * @param b The ending {@link UIColor}
     * @param f The percentage between the two {@link UIColor}
     * @return The {@link UIColor} which if f% between a and b
     */
    public static UIColor lerpUIColor(UIColor a, UIColor b, float f) {
        return new UIColor(a.r * 255 + f * (b.r * 255 - a.r * 255),
                a.g * 255 + f * (b.g * 255 - a.g * 255),
                a.b * 255 + f * (b.b * 255 - a.b * 255),
                a.a * 255 + f * (b.a * 255 - a.a * 255));
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

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

    public static boolean isInRange(double val, double min, double max) {
        return val >= min && val <= max;
    }

    public static float snapTo(float val, float snapTo) {
        float roundMultiplier = 1.0f / snapTo;
        return Math.round(val * roundMultiplier) / roundMultiplier;
    }

    /**
     * @param t A value from 0 to 1
     * @return Quadratic interpolated value between 0 and 1
     */
    public static float quadraticEaseInOut(float t) {
        return t < 0.5f ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }

}
