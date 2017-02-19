package craftedcart.smblevelworkshop.util;

import craftedcart.smblevelworkshop.animation.AnimData;
import craftedcart.smblevelworkshop.animation.NamedTransform;
import craftedcart.smblevelworkshop.asset.*;
import craftedcart.smblevelworkshop.level.LevelData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author CraftedCart
 *         Created on 11/09/2016 (DD/MM/YYYY)
 */
public class ExportManager {

    private static int bananaCount;
    private static int bumperCount;
    private static int goalCount;
    private static int jamabarCount;
    private static int backgroundCount;
    private static int animCount;
    private static int frameCount;

    public static void writeConfig(LevelData levelData, File configFile) throws IOException {
        String configContents = getConfig(levelData);

        BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
        writer.write(configContents);
        writer.close();

        for (Map.Entry<String, AnimData> entry : levelData.getObjectAnimDataMap().entrySet()) {
            File animFile = new File(configFile.getParentFile(), "anim-" + entry.getKey() + ".txt");

            String animConfigContents = getAnimConfig(entry.getValue(), levelData);

            BufferedWriter animWriter = new BufferedWriter(new FileWriter(animFile));
            animWriter.write(animConfigContents);
            animWriter.close();
        }
    }

    public static String getConfig(LevelData levelData) {
        StringBuilder sb = new StringBuilder();

        bananaCount = -1;
        bumperCount = -1;
        goalCount = -1;
        jamabarCount = -1;
        backgroundCount = -1;
        animCount = -1;

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

            sb.append("\r\n");
        }

        for (String name : levelData.getBackgroundObjects()) {
            appendBackgroundPrefix(sb, true); sb.append(". x . x = ").append(name).append("\r\n");
        }

        for (String name : levelData.getBackgroundExternalObjects()) {
            appendBackgroundPrefix(sb, true); sb.append(". x . x = ").append(name).append("\r\n");
        }
        sb.append("\r\n");

        sb.append("maxtime [ 0 ] . x . x = ").append(levelData.getMaxTime()).append("\r\n");
        sb.append("leadintime [ 0 ] . x . x = ").append(levelData.getLeadInTime()).append("\r\n");

        sb.append("\r\n");

        for (Map.Entry<String, AnimData> entry : levelData.getObjectAnimDataMap().entrySet()) {
            appendAnimPrefix(sb, true); sb.append(". file . x = ").append("anim-").append(entry.getKey()).append(".txt").append("\r\n");
            appendAnimPrefix(sb, false); sb.append(". name . x = ").append(entry.getKey()).append("\r\n");
            appendAnimPrefix(sb, false); sb.append(". center . x = ").append(entry.getValue().getRotationCenter().x).append("\r\n");
            appendAnimPrefix(sb, false); sb.append(". center . y = ").append(entry.getValue().getRotationCenter().y).append("\r\n");
            appendAnimPrefix(sb, false); sb.append(". center . z = ").append(entry.getValue().getRotationCenter().z).append("\r\n");
            sb.append("\r\n");
        }
        sb.append("\r\n");

        return sb.toString();
    }

    private static String getAnimConfig(AnimData animData, LevelData levelData) {
        StringBuilder sb = new StringBuilder();

        frameCount = -1;

        TreeSet<Float> frames = new TreeSet<>();

        frames.add(0.0f);
        frames.add(1.0f);

        for (Float time : animData.getPosXFrames().keySet()) {
            frames.add(time);
        }

        for (Float time : animData.getPosYFrames().keySet()) {
            frames.add(time);
        }

        for (Float time : animData.getPosZFrames().keySet()) {
            frames.add(time);
        }

        for (Float time : animData.getRotXFrames().keySet()) {
            frames.add(time);
        }

        for (Float time : animData.getRotYFrames().keySet()) {
            frames.add(time);
        }

        for (Float time : animData.getRotZFrames().keySet()) {
            frames.add(time);
        }

        for (float time : frames) {
            NamedTransform transform = animData.getNamedTransformAtTime(time, null);

            appendFramePrefix(sb, true); sb.append(". pos . x = ").append(transform.getPosition().x).append("\r\n");
            appendFramePrefix(sb, false); sb.append(". pos . y = ").append(transform.getPosition().y).append("\r\n");
            appendFramePrefix(sb, false); sb.append(". pos . z = ").append(transform.getPosition().z).append("\r\n");

            appendFramePrefix(sb, false); sb.append(". rot . x = ").append(transform.getRotation().x).append("\r\n");
            appendFramePrefix(sb, false); sb.append(". rot . y = ").append(transform.getRotation().y).append("\r\n");
            appendFramePrefix(sb, false); sb.append(". rot . z = ").append(transform.getRotation().z).append("\r\n");

            appendFramePrefix(sb, false); sb.append(". time . x = ").append(time * (levelData.getLeadInTime() + levelData.getMaxTime())).append("\r\n");
            sb.append("\r\n");
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

    private static void appendBackgroundPrefix(StringBuilder sb, boolean incrementCounter) {
        if (incrementCounter) {
            backgroundCount++;
        }
        sb.append("background [ ").append(backgroundCount).append(" ] ");
    }

    private static void appendAnimPrefix(StringBuilder sb, boolean incrementCounter) {
        if (incrementCounter) {
            animCount++;
        }
        sb.append("animobj [ ").append(animCount).append(" ] ");
    }

    private static void appendFramePrefix(StringBuilder sb, boolean incrementCounter) {
        if (incrementCounter) {
            frameCount++;
        }
        sb.append("frame [ ").append(frameCount).append(" ] ");
    }

}
