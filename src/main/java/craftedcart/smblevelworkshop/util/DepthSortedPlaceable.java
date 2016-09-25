package craftedcart.smblevelworkshop.util;

import craftedcart.smblevelworkshop.asset.Placeable;

import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 25/09/2016 (DD/MM/YYYY)
 */
public class DepthSortedPlaceable {
    public double depth;
    public Map.Entry<String, Placeable> entry;

    public DepthSortedPlaceable(double depth, Map.Entry<String, Placeable> entry) {
        this.depth = depth;
        this.entry = entry;
    }
}
