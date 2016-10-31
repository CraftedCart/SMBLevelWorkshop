package craftedcart.smblevelworkshop.resource.model;

import craftedcart.smblevelworkshop.util.Vec3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 10/10/2016 (DD/MM/YYYY)
 */
public class OBJObject {

    public String name;
    public List<OBJFacesByMaterial> facesByMaterialList = new ArrayList<>();
    private Vec3f centerPoint;

    public void setName(String name) {
        this.name = name;
    }

    public void addFacesByMaterial(OBJFacesByMaterial mat) {
        facesByMaterialList.add(mat);
    }

    public void setCenterPoint(Vec3f centerPoint) {
        this.centerPoint = centerPoint;
    }

    public Vec3f getCenterPoint() {
        return centerPoint;
    }

}
