package craftedcart.smblevelworkshop.asset;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.util.LogHelper;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class AssetStartPos implements IAsset {

    @NotNull
    @Override
    public String getName() {
        return "assetStartPos";
    }

    @NotNull
    @Override
    public ResourceModel getModel() {
        return ResourceManager.getModel("model/monkeyBall");
    }

    @NotNull
    @Override
    public UIColor getColor() {
        return UIColor.matBlue();
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

}
