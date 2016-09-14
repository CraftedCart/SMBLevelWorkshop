package craftedcart.smblevelworkshop.asset;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.util.LogHelper;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class AssetBumper implements IAsset {

    @NotNull
    @Override
    public String getName() {
        return "assetBumper";
    }

    @NotNull
    @Override
    public ResourceModel getModel() {
        return ResourceManager.getModel("model/mb_bumper");
    }

    @NotNull
    @Override
    public UIColor getColor() {
        return UIColor.matOrange();
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
