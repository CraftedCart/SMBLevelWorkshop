package craftedcart.smblevelworkshop.resource.model;

import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.lwjgl.VBO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 10/10/2016 (DD/MM/YYYY)
 */
public class OBJFacesByMaterial {

    public List<Face> faceList = new ArrayList<>();
    public VBO vbo;

    public void setFaceList(List<Face> faceList) {
        this.faceList = faceList;
    }

    public void setVbo(VBO vbo) {
        this.vbo = vbo;
    }
}
