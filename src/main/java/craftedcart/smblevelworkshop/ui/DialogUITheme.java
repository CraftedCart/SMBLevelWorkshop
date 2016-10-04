package craftedcart.smblevelworkshop.ui;

import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.theme.UITheme;
import io.github.craftedcart.fluidui.util.UIColor;

/**
 * @author CraftedCart
 * Created on 05/03/2016 (DD/MM/YYYY)
 */
public class DialogUITheme extends UITheme {

    public DialogUITheme() {
        panelBackgroundColor = UIColor.matWhite();
        labelFont = FontCache.getUnicodeFont("Roboto-Regular", 16);
        tooltipFont = FontCache.getUnicodeFont("Roboto-Regular", 16);
        labelTextColor = UIColor.matGrey900();
        scrollbarThickness = 2;
    }

}
