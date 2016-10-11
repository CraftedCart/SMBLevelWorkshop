package craftedcart.smblevelworkshop.resource.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 10/10/2016 (DD/MM/YYYY)
 */
public class OBJObject {

    public String name;
    public List<OBJFacesByMaterial> facesByMaterialList = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public void addFacesByMaterial(OBJFacesByMaterial mat) {
        facesByMaterialList.add(mat);
    }

}
