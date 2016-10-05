package craftedcart.smblevelworkshop.community;

import craftedcart.smblevelworkshop.exception.SyncDatabasesException;
import craftedcart.smbworkshopexporter.util.LogHelper;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author CraftedCart
 *         Created on 05/10/2016 (DD/MM/YYYY)
 */
public class CloneRootRepoThread<Void> implements Callable {

    private String destDir;
    private Stromg

    public CloneRootRepoThread(String destDir, String gitURI) {

    }

    @Override
    public Void call() throws Exception {

        try {
            cloneOrPullRepo(destDir, gitURI);

        } catch (SyncDatabasesException | IOException e) {
            LogHelper.error(SyncManager.class, "Error while cloning " + user.getUsername() + "/root");
            LogHelper.error(SyncManager.class, "Ignoring user");
            LogHelper.error(SyncManager.class, "\n" + e + "\n" + LogHelper.stackTraceToString(e));

            //TODO: Remove user from local CreatorList.xml and CommunityRootData
        }

        return null;
    }

}
