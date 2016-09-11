package craftedcart.smblevelworkshop.resource;

import craftedcart.smblevelworkshop.Window;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.File;
import java.io.FileInputStream;

/**
 * @author CraftedCart
 * Created on 28/03/2016 (DD/MM/YYYY)
 */
public class ResourceTexture implements IResource {

    private Texture texture;

    public ResourceTexture(String type, File file) throws Exception {
        Window.drawable.makeCurrent();
        FileInputStream fis = new FileInputStream(file);
        try {
            texture = TextureLoader.getTexture(type, fis);
        } catch (Exception e) {
            throw new Exception(e);
        }
        fis.close();
        Window.drawable.releaseContext();
    }

    public Texture getTexture() {
        return texture;
    }

    public int getWidth() {
        return texture.getImageWidth();
    }

    public int getHeight() {
        return texture.getImageHeight();
    }

}
