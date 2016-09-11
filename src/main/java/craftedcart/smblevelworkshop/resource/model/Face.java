package craftedcart.smblevelworkshop.resource.model;

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by CraftedCart on 25/02/2016 (DD/MM/YYYY)
 */
public class Face {

    public Vector3f vertex;
    public Vector3f texture;
    public Vector3f normal;

    Face(Vector3f vertex, Vector3f texture, Vector3f normal) {
        this.vertex = vertex;
        this.texture = texture;
        this.normal = normal;
    }

}