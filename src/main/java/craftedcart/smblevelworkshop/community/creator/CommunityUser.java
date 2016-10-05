package craftedcart.smblevelworkshop.community.creator;

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

}
