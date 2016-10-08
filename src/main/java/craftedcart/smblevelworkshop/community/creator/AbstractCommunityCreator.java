package craftedcart.smblevelworkshop.community.creator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public abstract class AbstractCommunityCreator {

    @NotNull protected String username;
    @Nullable protected String displayName;
    @Nullable protected String bioPath; //Typically "user/bio.txt"

    @NotNull
    public String getUsername() {
        return username;
    }

    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    public void setBioPath(@Nullable String bioPath) {
        this.bioPath = bioPath;
    }

    @Nullable
    public String getBioPath() {
        return bioPath;
    }

}
