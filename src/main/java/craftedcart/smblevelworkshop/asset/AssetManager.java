package craftedcart.smblevelworkshop.asset;

/**
 * @author CraftedCart
 *         Created on 10/09/2016 (DD/MM/YYYY)
 */
public class AssetManager {

    private static final IAsset[] avaliableAssets = new IAsset[]{
        new AssetBanana(),
        new AssetBumper(),
        new AssetGoal(),
        new AssetJamabar(),
        new AssetWormhole(),
        new AssetFalloutVolume()
    };

    public static IAsset[] getAvaliableAssets() {
        return avaliableAssets;
    }

}
