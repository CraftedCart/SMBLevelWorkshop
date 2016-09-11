package craftedcart.smblevelworkshop.asset;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class AssetJamabar implements IAsset {

    @NotNull
    @Override
    public String getName() {
        return "assetJamabar";
    }

    @NotNull
    @Override
    public ResourceModel getModel() {
        return ResourceManager.getModel("model/mb_jamabar");
    }

    @NotNull
    @Override
    public UIColor getColor() {
        return UIColor.matRed();
    }
}
