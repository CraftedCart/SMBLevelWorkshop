package craftedcart.smblevelworkshop.community.creator;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class CommunityUser extends AbstractCommunityCreator {

    public CommunityUser(@NotNull String username) {
        this.username = username;
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
