package craftedcart.smblevelworkshop.util;

import java.util.Comparator;

/**
 * @author CraftedCart
 *         Created on 25/09/2016 (DD/MM/YYYY)
 */
public class DepthComparator implements Comparator<DepthSortedPlaceable> {

    @Override
    public int compare(DepthSortedPlaceable o1, DepthSortedPlaceable o2) {
        if (o1.depth > o2.depth) {
            return 1;
        } else if (o1.depth < o2.depth) {
            return -1;
        } else {
            return 0;
        }
    }

}
