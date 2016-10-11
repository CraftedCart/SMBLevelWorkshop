package craftedcart.smblevelworkshop.resource.model;

import craftedcart.smblevelworkshop.resource.ResourceShaderProgram;
import io.github.craftedcart.fluidui.util.UIColor;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Objects;

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

    public void renderObjectByName(ResourceShaderProgram shaderProgram, boolean setTexture, String name) {
        for (OBJObject object : objectList) {
            if (Objects.equals(object.name, name)) {
                for (OBJFacesByMaterial facesByMaterial : object.facesByMaterialList) {
                    facesByMaterial.vbo.render(shaderProgram, setTexture);
                }

                break;
            }
        }
    }

    public void renderObjectByName(ResourceShaderProgram shaderProgram, boolean setTexture, UIColor color, String name) {
        for (OBJObject object : objectList) {
            if (Objects.equals(object.name, name)) {
                for (OBJFacesByMaterial facesByMaterial : object.facesByMaterialList) {
                    facesByMaterial.vbo.render(shaderProgram, setTexture, color);
                }

                break;
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

    public boolean hasObject(String name) {
        for (OBJObject object : objectList) {
            if (Objects.equals(object.name, name)) {
                return true;
            }
        }

        //No object with a matching name was found
        return false;
    }

}
