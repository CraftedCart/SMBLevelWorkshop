package craftedcart.smblevelworkshop.resource.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 10/10/2016 (DD/MM/YYYY)
 */
public class OBJObject {

    public List<OBJFacesByMaterial> facesByMaterialList = new ArrayList<>();

    public void addFacesByMaterial(OBJFacesByMaterial mat) {
        facesByMaterialList.add(mat);
    }

}
