package craftedcart.smblevelworkshop.community.creator;

import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class CommunityUser implements ICommunityCreator {

    private String username;

    public CommunityUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
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
