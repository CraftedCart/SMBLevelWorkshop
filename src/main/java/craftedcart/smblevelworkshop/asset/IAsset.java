package craftedcart.smblevelworkshop.asset;

import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.util.LogHelper;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public interface IAsset extends Cloneable {

    @NotNull public String getName();
    @NotNull public ResourceModel getModel();

    @NotNull
    default public UIColor getColor() {
        return UIColor.pureWhite();
    }

    /**
     * @return Null if this asset has no types, else it returns an array of type keys (That can be localized)
     */
    @Nullable
    default public String[] getValidTypes() {
        return null;
    }

    @Nullable
    default public String getType() {
        return null;
    }

    @Nullable
    default public String getGameType() {
        return null;
    }

    default public boolean canRotate() {
        return true;
    }

    default public boolean canScale() {
        return true;
    }

    public IAsset getCopy();

}
