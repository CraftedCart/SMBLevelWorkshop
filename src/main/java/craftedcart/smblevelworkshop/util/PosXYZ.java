package craftedcart.smblevelworkshop.util;

/**
 * @author CraftedCart
 *         Created on 07/09/2016 (DD/MM/YYYY)
 */
public class PosXYZ {

    public double x;
    public double y;
    public double z;

    public PosXYZ() {}

    public PosXYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PosXYZ(craftedcart.smbworkshopexporter.util.Vec3f vec3f) {
        this.x = vec3f.x;
        this.y = vec3f.y;
        this.z = vec3f.z;
    }

    public PosXYZ add(PosXYZ pos) {
        return new PosXYZ(this.x + pos.x, this.y + pos.y, this.z + pos.z);
    }

    public PosXYZ add(double x, double y, double z) {
        return new PosXYZ(this.x + x, this.y + y, this.z + z);
    }

    public PosXYZ subtract(PosXYZ pos) {
        return new PosXYZ(this.x - pos.x, this.y - pos.y, this.z - pos.z);
    }

    public PosXYZ subtract(double x, double y, double z) {
        return new PosXYZ(this.x - x, this.y - y, this.z - z);
    }

    public PosXYZ multiply(double factor) {
        return new PosXYZ(x * factor, y * factor, z * factor);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PosXYZ) {
            PosXYZ posObj = (PosXYZ) obj;
            return posObj.x == x && posObj.y == y && posObj.z == z;
        } else {
            return false;
        }
    }

    public PosXYZ getCopy() {
        return new PosXYZ(x, y, z);
    }

}
