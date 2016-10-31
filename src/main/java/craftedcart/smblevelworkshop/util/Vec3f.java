package craftedcart.smblevelworkshop.util;

/**
 * @author CraftedCart
 *         Created on 07/09/2016 (DD/MM/YYYY)
 */
public class Vec3f {

    public float x;
    public float y;
    public float z;

    public Vec3f() {}

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f add(Vec3f pos) {
        return new Vec3f(this.x + pos.x, this.y + pos.y, this.z + pos.z);
    }

    public Vec3f add(float x, float y, float z) {
        return new Vec3f(this.x + x, this.y + y, this.z + z);
    }

    public Vec3f subtract(Vec3f pos) {
        return new Vec3f(this.x - pos.x, this.y - pos.y, this.z - pos.z);
    }

    public Vec3f subtract(float x, float y, float z) {
        return new Vec3f(this.x - x, this.y - y, this.z - z);
    }

    public Vec3f multiply(float factor) {
        return new Vec3f(x * factor, y * factor, z * factor);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3f) {
            Vec3f posObj = (Vec3f) obj;
            return posObj.x == x && posObj.y == y && posObj.z == z;
        } else {
            return false;
        }
    }

}
