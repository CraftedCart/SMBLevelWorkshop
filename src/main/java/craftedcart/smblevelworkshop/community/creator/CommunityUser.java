package craftedcart.smblevelworkshop.community.creator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class CommunityUser implements ICommunityCreator {

    @NotNull private String username;
    @Nullable private String displayName;

    public CommunityUser(@NotNull String username) {
        this.username = username;
    }

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

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CommunityUser && Objects.equals(username, ((CommunityUser) obj).getUsername());
    }

}
