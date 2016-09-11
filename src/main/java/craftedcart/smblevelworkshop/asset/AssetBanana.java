package craftedcart.smblevelworkshop.asset;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class AssetBanana implements IAsset {

    private String type = "singleBanana";

    @NotNull
    @Override
    public String getName() {
        return "assetBanana";
    }

    @NotNull
    @Override
    public ResourceModel getModel() {
        switch (type) {
            case "singleBanana":
                return ResourceManager.getModel("model/OBJ_BANANA_01_LOD_150");
            case "bunchBanana":
                return ResourceManager.getModel("model/OBJ_BANANA_02_LOD_100");
            default:
                //This shouldn't happen
                return ResourceManager.getModel("model/invalidAsset");
        }
    }

    @NotNull
    @Override
    public UIColor getColor() {
        return UIColor.matYellow();
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String[] getValidTypes() {
        return new String[] {"singleBanana", "bunchBanana"};
    }
}
