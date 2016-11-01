package craftedcart.smblevelworkshop.util;

/**
 * @author CraftedCart
 *         Created on 01/11/2016 (DD/MM/YYYY)
 */
public interface ITransformable {

    default void setPosition(PosXYZ position) { throw new UnsupportedOperationException(); }
    default PosXYZ getPosition() { throw new UnsupportedOperationException(); }

    default boolean canMoveX() { return true; }
    default boolean canMoveY() { return true; }
    default boolean canMoveZ() { return true; }

    default void setRotation(PosXYZ rotation) { throw new UnsupportedOperationException(); }
    default PosXYZ getRotation() { throw new UnsupportedOperationException(); }

    default boolean canRotate() { return true; }

    default void setScale(PosXYZ scale) { throw new UnsupportedOperationException(); }
    default PosXYZ getScale() { throw new UnsupportedOperationException(); }

    default boolean canScale() { return true; }

}
