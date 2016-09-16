package craftedcart.smblevelworkshop.util;

import craftedcart.smblevelworkshop.asset.*;
import craftedcart.smblevelworkshop.level.LevelData;

import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 11/09/2016 (DD/MM/YYYY)
 */
public class ExportManager {

    private static int bananaCount = 0;
    private static int bumperCount = 0;
    private static int goalCount = 0;
    private static int jamabarCount = 0;

    public static String getConfig(LevelData levelData) {
        StringBuilder sb = new StringBuilder();

        bananaCount = -1;
        bumperCount = -1;
        goalCount = -1;
        jamabarCount = -1;

        for (Map.Entry<String, Placeable> entry : levelData.getPlacedObjects().entrySet()) {
            Placeable placeable = entry.getValue();

            if (placeable.getAsset().canGrabX()) {
                appendAssetPrefix(placeable.getAsset(), sb, true); sb.append(". pos . x = ").append(placeable.getPosition().x).append("\r\n");
            }
            if (placeable.getAsset().canGrabY()) {
                appendAssetPrefix(placeable.getAsset(), sb, false); sb.append(". pos . y = ").append(placeable.getPosition().y).append("\r\n");
            }
            if (placeable.getAsset().canGrabZ()) {
                appendAssetPrefix(placeable.getAsset(), sb, false); sb.append(". pos . z = ").append(placeable.getPosition().z).append("\r\n");
            }

            if (placeable.getAsset().canRotate()) {
                appendAssetPrefix(placeable.getAsset(), sb, false); sb.append(". rot . x = ").append(placeable.getRotation().x).append("\r\n");
                appendAssetPrefix(placeable.getAsset(), sb, false); sb.append(". rot . y = ").append(placeable.getRotation().y).append("\r\n");
                appendAssetPrefix(placeable.getAsset(), sb, false); sb.append(". rot . z = ").append(placeable.getRotation().z).append("\r\n");
            }

            if (placeable.getAsset().canScale()) {
                appendAssetPrefix(placeable.getAsset(), sb, false); sb.append(". scl . x = ").append(placeable.getScale().x).append("\r\n");
                appendAssetPrefix(placeable.getAsset(), sb, false); sb.append(". scl . y = ").append(placeable.getScale().y).append("\r\n");
                appendAssetPrefix(placeable.getAsset(), sb, false); sb.append(". scl . z = ").append(placeable.getScale().z).append("\r\n");
            }

            if (placeable.getAsset().getValidTypes() != null) {
                appendAssetPrefix(placeable.getAsset(), sb, false); sb.append(". type . x = ").append(placeable.getAsset().getGameType()).append("\r\n");
            }
        }

        return sb.toString();
    }
    
    private static void appendAssetPrefix(IAsset asset, StringBuilder sb, boolean incrementCounter) {
        if (asset instanceof AssetBanana) {
            if (incrementCounter) {
                bananaCount++;
            }
            sb.append("banana [ ").append(bananaCount).append(" ] ");
        } else if (asset instanceof AssetBumper) {
            if (incrementCounter) {
                bumperCount++;
            }
            sb.append("bumper [ ").append(bumperCount).append(" ] ");
        } else if (asset instanceof AssetGoal) {
            if (incrementCounter) {
                goalCount++;
            }
            sb.append("goal [ ").append(goalCount).append(" ] ");
        } else if (asset instanceof AssetJamabar) {
            if (incrementCounter) {
                jamabarCount++;
            }
            sb.append("jamabar [ ").append(jamabarCount).append(" ] ");
        } else if (asset instanceof AssetStartPos) {
            sb.append("start [ 0 ] ");
        } else if (asset instanceof AssetFalloutY) {
            sb.append("fallout [ 0 ] ");
        }
    }

}
