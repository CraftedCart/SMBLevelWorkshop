package craftedcart.smblevelworkshop.community.creator;

import java.util.Objects;

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

    public String getUsername() {
        return username;
    }

    public String getRepoName() {
        return repoName;
    }

    @Override
    public int hashCode() {
        return username.hashCode() * repoName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CommunityRepo && Objects.equals(username, ((CommunityRepo) obj).getUsername()) && Objects.equals(username, ((CommunityRepo) obj).getRepoName());
    }

}
