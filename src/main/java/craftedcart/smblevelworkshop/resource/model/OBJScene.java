package craftedcart.smblevelworkshop.resource.model;

import craftedcart.smblevelworkshop.resource.ResourceShaderProgram;
import io.github.craftedcart.fluidui.util.UIColor;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @author CraftedCart
 *         Created on 10/10/2016 (DD/MM/YYYY)
 */
public class OBJScene {

    private List<OBJObject> objectList;

    public void setObjectList(List<OBJObject> objectList) {
        this.objectList = objectList;
    }

    public void renderAll(ResourceShaderProgram shaderProgram, boolean setTexture) {
        for (OBJObject object : objectList) {
            for (OBJFacesByMaterial facesByMaterial : object.facesByMaterialList) {
                facesByMaterial.vbo.render(shaderProgram, setTexture);
            }
        }
    }

    public void renderAll(ResourceShaderProgram shaderProgram, boolean setTexture, UIColor color) {
        for (OBJObject object : objectList) {
            for (OBJFacesByMaterial facesByMaterial : object.facesByMaterialList) {
                facesByMaterial.vbo.render(shaderProgram, setTexture, color);
            }
        }
    }

    public void unloadAll() {
        for (OBJObject object : objectList) {
            for (OBJFacesByMaterial facesByMaterial : object.facesByMaterialList) {
                GL11.glDeleteTextures(facesByMaterial.vbo.getTextureId());
                facesByMaterial.vbo.destroy();
            }
        }
    }

    public List<OBJObject> getObjectList() {
        return objectList;
    }

}
