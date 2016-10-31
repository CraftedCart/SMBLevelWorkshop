package craftedcart.smblevelworkshop.ui.component;

import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.util.PosXY;
import io.github.craftedcart.fluidui.util.UIColor;
import io.github.craftedcart.fluidui.util.UIUtils;
import org.jetbrains.annotations.NotNull;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.TextureImpl;

/**
 * @author CraftedCart
 *         Created on 31/10/2016 (DD/MM/YYYY)
 */
public class TimelinePlayhead extends Panel {

    private UnicodeFont font;
    @NotNull private String rightText = "";
    @NotNull private String leftText = "";

    public TimelinePlayhead() {
        super();
        font = FontCache.getUnicodeFont("Roboto-Regular", 12);
    }

    @Override
    public void draw() {
        super.draw();

        UIUtils.drawQuad(
                new PosXY(bottomRightPx.x, topLeftPx.y),
                new PosXY(bottomRightPx.x, topLeftPx.y + 16),
                new PosXY(bottomRightPx.x + 38, topLeftPx.y + 16),
                new PosXY(bottomRightPx.x + 62, topLeftPx.y),
                backgroundColor
        );

        UIUtils.drawQuad(
                topLeftPx,
                new PosXY(topLeftPx.x, topLeftPx.y + 16),
                new PosXY(topLeftPx.x - 38, topLeftPx.y + 16),
                new PosXY(topLeftPx.x - 62, topLeftPx.y),
                backgroundColor
        );

        UIColor textCol = UIColor.matWhite();
        //Right text
        font.drawString((float) bottomRightPx.x, (float) topLeftPx.y, rightText, new Color((float) textCol.r, (float) textCol.g, (float) textCol.b, (float) textCol.a));
        //Left text
        font.drawString((float) topLeftPx.x - 40, (float) topLeftPx.y, leftText, new Color((float) textCol.r, (float) textCol.g, (float) textCol.b, (float) textCol.a));
        TextureImpl.bindNone();
    }

    public void setRightText(@NotNull String rightText) {
        this.rightText = rightText;
    }

    public void setLeftText(@NotNull String leftText) {
        this.leftText = leftText;
    }
}
