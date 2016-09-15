package craftedcart.smblevelworkshop.resource;

import craftedcart.smblevelworkshop.exception.GLSLCompileException;
import craftedcart.smblevelworkshop.util.LogHelper;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static craftedcart.smblevelworkshop.Window.*;

/**
 * @author CraftedCart
 * Created on 06/04/2016 (DD/MM/YYYY)
 */
public class ResourceShader implements IResource {

    private int shaderID;

    /**
     * @param shaderType The OpenGL id for the type of shader ({@link GL20#GL_VERTEX_SHADER} or {@link GL20#GL_FRAGMENT_SHADER})
     * @param file the file where the shader is stored
     */
    public ResourceShader(int shaderType, File file) throws IOException, LWJGLException, GLSLCompileException {
        drawable.makeCurrent();

        shaderID = GL20.glCreateShader(shaderType);
        StringBuilder source = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        String line;
        while ((line = reader.readLine()) != null) {
            source.append(line).append('\n');
        }

        reader.close();

        GL20.glShaderSource(shaderID, source);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) { //If it failed to compile
            LogHelper.error(getClass(), String.format("Failed to compile the shader at \"%s\"", file.getPath()));

            IntBuffer logLength = ByteBuffer.allocateDirect(8).asIntBuffer();
            GL20.glGetShaderi(shaderID, GL20.GL_INFO_LOG_LENGTH);

            throw new GLSLCompileException(GL20.glGetShaderInfoLog(shaderID, logLength.get(0))); //Get the error and throw an exception
        }

        drawable.releaseContext();
    }

    public int getShaderID() {
        return shaderID;
    }
}
