package craftedcart.smblevelworkshop.ui.component;

import craftedcart.smblevelworkshop.resource.ResourceManager;
import io.github.craftedcart.fluidui.component.Button;
import io.github.craftedcart.fluidui.util.UIColor;
import io.github.craftedcart.fluidui.util.UIUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

/**
 * @author CraftedCart
 *         Created on 12/04/2017 (DD/MM/YYYY)
 */
public class ItemButton extends Button {

    private String id;
    private boolean selected;
    @NotNull private UIColor itemGroupCol = UIColor.matGrey();
    private Texture colTex;

    @Override
    public void init() {
        super.init();
        colTex = ResourceManager.getTexture("image/circle").getTexture();
    }

    @Override
    public void componentDraw() {
        UIUtils.drawQuad(topLeftPx, bottomRightPx, UIColor.pureBlack());

        UIColor buttonBackgroundColor; //The background color of the button
        if (mouseOver && isEnabled) { //If the mouse is over the button
            if (Mouse.isButtonDown(0)) { //If LMB down
                buttonBackgroundColor = backgroundHitColor;
            } else { //LMB not down
                buttonBackgroundColor = backgroundActiveColor;
            }
        } else { //Mouse not over
//            buttonBackgroundColor = backgroundIdleColor;
            double alpha = selected ? 0.25 : 0.5;
            buttonBackgroundColor = itemGroupCol.alpha(alpha);
        }

        if (texture == null) {
            UIUtils.drawQuad(topLeftPx, bottomRightPx, buttonBackgroundColor);
        } else {
            buttonBackgroundColor.bindColor();
            if (textureSlice9 == null) {
                UIUtils.drawTexturedQuad(topLeftPx, bottomRightPx, texture);
            } else {
                UIUtils.drawTexturedQuad(topLeftPx, bottomRightPx, texture, textureSlice9);
            }
        }

        UIUtils.drawString(theme.labelFont, topLeftPx.add(18, -2), id, UIColor.matWhite()); //Draw ID label

        itemGroupCol.bindColor();
        UIUtils.drawTexturedQuad(topLeftPx, topLeftPx.add(18, 18), colTex);
    }

    public void setItemGroupCol(@NotNull UIColor itemGroupCol) {
        this.itemGroupCol = itemGroupCol;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
