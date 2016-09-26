package craftedcart.smblevelworkshop.resource.model;

import com.owens.oobjloader.lwjgl.DisplayModel;
import craftedcart.smblevelworkshop.resource.IResource;
import craftedcart.smblevelworkshop.resource.ResourceShaderProgram;
import io.github.craftedcart.fluidui.util.UIColor;
import org.lwjgl.opengl.GL11;

/**
 * Created by CraftedCart on 25/02/2016 (DD/MM/YYYY)
 */
public class ResourceModel implements IResource {

    public DisplayModel scene;

    public ResourceModel(DisplayModel scene) {
        this.scene = scene;
    }

    public static void drawModel(ResourceModel m, ResourceShaderProgram shaderProgram, boolean setTexture) {
        m.scene.render(shaderProgram, setTexture);
    }

    public static void drawModel(ResourceModel m, ResourceShaderProgram shaderProgram, boolean setTexture, UIColor color) {
        m.scene.render(shaderProgram, setTexture, color);
    }

    public static void drawModelWireframe(ResourceModel m, ResourceShaderProgram shaderProgram, boolean setTexture) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        m.scene.render(shaderProgram, setTexture);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

}
