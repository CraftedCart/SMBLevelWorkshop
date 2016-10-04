package craftedcart.smblevelworkshop;

import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.util.EnumMode;
import craftedcart.smblevelworkshop.util.PosXYZ;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class Project {

    @Nullable public ClientLevelData clientLevelData;
    @NotNull public EnumMode mode = EnumMode.NONE;
    @NotNull public PosXYZ modeDirection = new PosXYZ(0, 1, 0);

}
