package craftedcart.smblevelworkshop.resource;

import craftedcart.smblevelworkshop.Window;
import org.lwjgl.opengl.GL20;

/**
 * @author CraftedCart
 * Created on 06/04/2016 (DD/MM/YYYY)
 */
public class ResourceShaderProgram implements IResource {

    private int programID;

    public ResourceShaderProgram(ResourceShader vertShader, ResourceShader fragShader) throws Exception {
        Window.drawable.makeCurrent();

        try {
            programID = GL20.glCreateProgram();
            GL20.glAttachShader(programID, vertShader.getShaderID());
            GL20.glAttachShader(programID, fragShader.getShaderID());
            GL20.glLinkProgram(programID);
            GL20.glValidateProgram(programID);
        } catch (NullPointerException e) {
            throw new Exception(e);
        }

        Window.drawable.releaseContext();
    }

    public int getProgramID() {
        return programID;
    }
}
