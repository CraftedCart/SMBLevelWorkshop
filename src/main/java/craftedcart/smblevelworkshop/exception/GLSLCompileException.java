package craftedcart.smblevelworkshop.exception;

/**
 * @author CraftedCart
 * Created on 06/04/2016 (DD/MM/YYYY)
 */
public class GLSLCompileException extends Exception {

    public GLSLCompileException(String message) {
        super("\n" + message);
    }

}
