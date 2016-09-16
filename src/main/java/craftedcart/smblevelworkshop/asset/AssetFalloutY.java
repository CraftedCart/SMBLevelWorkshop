package craftedcart.smblevelworkshop.asset;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.ResourceShaderProgram;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.util.LogHelper;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class AssetFalloutY implements IAsset {

    @NotNull
    @Override
    public String getName() {
        return "assetFalloutY";
    }

    @NotNull
    @Override
    public ResourceModel getModel() {
        return ResourceManager.getModel("model/falloutPlane");
    }

    @NotNull
    @Override
    public UIColor getColor() {
        return UIColor.matRed();
    }

    @Override
    public boolean canGrabX() {
        return false;
    }

    @Override
    public boolean canGrabZ() {
        return false;
    }

    @Override
    public boolean canRotate() {
        return false;
    }

    @Override
    public boolean canScale() {
        return false;
    }

    @Override
    public IAsset getCopy() {
        try {
            return (IAsset) clone();
        } catch (CloneNotSupportedException e) {
            LogHelper.error(getClass(), "Failed to clone IAsset");
            LogHelper.error(getClass(), e);
            return null;
        }
    }

    @NotNull
    @Override
    public ResourceShaderProgram getShaderProgram() {
        return ResourceManager.getShaderProgram("texShaderProgram");
    }
}
