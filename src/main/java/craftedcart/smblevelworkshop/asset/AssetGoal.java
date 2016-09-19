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
public class AssetGoal implements IAsset {

    private String type = "blueGoal";

    @NotNull
    @Override
    public String getName() {
        return "assetGoal";
    }

    @NotNull
    @Override
    public ResourceModel getModel() {
        return ResourceManager.getModel("model/GOAL");
    }

    @NotNull
    @Override
    public UIColor getColor() {
        switch (type) {
            case "blueGoal":
                return UIColor.matBlue();
            case "greenGoal":
                return UIColor.matGreen();
            case "redGoal":
                return UIColor.matRed();
            default:
                //This shouldn't happen!
                return UIColor.pureWhite();
        }
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
        return new String[]{"blueGoal", "greenGoal", "redGoal"};
    }

    @Nullable
    @Override
    public String getGameType() {
        switch (type) {
            case "blueGoal":
                return "B";
            case "greenGoal":
                return "G";
            case "redGoal":
                return "R";
            default:
                //This shouldn't happen!
                return "B";
        }
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

    @Override
    public boolean isOpaque() {
        return false;
    }
}
