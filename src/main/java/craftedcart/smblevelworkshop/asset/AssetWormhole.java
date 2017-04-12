package craftedcart.smblevelworkshop.asset;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.ResourceShaderProgram;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.util.LogHelper;
import org.jetbrains.annotations.NotNull;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class AssetWormhole implements IAsset {

    @NotNull private String destinationName = "";

    @NotNull
    @Override
    public String getName() {
        return "assetWormhole";
    }

    @NotNull
    @Override
    public ResourceModel getModel() {
        return ResourceManager.getModel("model/wormhole");
    }

    @Override
    public boolean canScale() {
        return false;
    }

    @NotNull
    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(@NotNull String destinationName) {
        this.destinationName = destinationName;
    }

    @NotNull
    @Override
    public ResourceShaderProgram getShaderProgram() {
        return ResourceManager.getShaderProgram("texShaderProgram");
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

}
