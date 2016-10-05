package craftedcart.smblevelworkshop.community.creator;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class CommunityRepo implements ICommunityCreator {

    private String username;
    private String repoName;

    public CommunityRepo(String username, String repoName) {
        this.username = username;
        this.repoName = repoName;
    }

}
