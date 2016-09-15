package craftedcart.smblevelworkshop.resource.model;

import com.owens.oobjloader.lwjgl.DisplayModel;
import craftedcart.smblevelworkshop.resource.IResource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CraftedCart on 25/02/2016 (DD/MM/YYYY)
 */
public class ResourceModel implements IResource {

    public DisplayModel scene;

    public ResourceModel(DisplayModel scene) {
        this.scene = scene;
    }

    public static void drawModel(ResourceModel m) {
        m.scene.render();
    }

}
