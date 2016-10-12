package craftedcart.smblevelworkshop.resource.model;

import craftedcart.smblevelworkshop.resource.IResource;
import craftedcart.smblevelworkshop.resource.ResourceShaderProgram;
import io.github.craftedcart.fluidui.util.UIColor;
import org.lwjgl.opengl.GL11;

/**
 * Created by CraftedCart on 25/02/2016 (DD/MM/YYYY)
 */
public class ResourceModel implements IResource {

    public OBJScene scene;

    public ResourceModel(OBJScene scene) {
        this.scene = scene;
    }

    public void drawModel(ResourceShaderProgram shaderProgram, boolean setTexture) {
        scene.renderAll(shaderProgram, setTexture);
    }

    public void drawModel(ResourceShaderProgram shaderProgram, boolean setTexture, UIColor color) {
        scene.renderAll(shaderProgram, setTexture, color);
    }

    public void drawModelObject(ResourceShaderProgram shaderProgram, boolean setTexture, String name) {
        scene.renderObjectByName(shaderProgram, setTexture, name);
    }

    public void drawModelWireframe(ResourceShaderProgram shaderProgram, boolean setTexture) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        scene.renderAll(shaderProgram, setTexture);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public void drawModelObjectWireframe(ResourceShaderProgram shaderProgram, boolean setTexture, String name) {
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        scene.renderObjectByName(shaderProgram, setTexture, name);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public boolean hasObject(String name) {
        return scene.hasObject(name);
    }

//    public static void drawModel(ResourceModel m, ResourceShaderProgram shaderProgram, boolean setTexture) {
//        m.scene.render(shaderProgram, setTexture);
//    }
//
//    public static void drawModel(ResourceModel m, ResourceShaderProgram shaderProgram, boolean setTexture, UIColor color) {
//        m.scene.render(shaderProgram, setTexture, color);
//    }
//
//    public static void drawModelWireframe(ResourceModel m, ResourceShaderProgram shaderProgram, boolean setTexture) {
//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
//        m.scene.render(shaderProgram, setTexture);
//        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
//    }

}
