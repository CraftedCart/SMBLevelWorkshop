package craftedcart.smblevelworkshop.community.sync;

import craftedcart.smblevelworkshop.community.CommunityRootData;
import craftedcart.smblevelworkshop.community.creator.CommunityRepo;
import craftedcart.smblevelworkshop.community.creator.CommunityUser;
import craftedcart.smblevelworkshop.community.creator.ICommunityCreator;
import craftedcart.smblevelworkshop.data.AppDataManager;
import craftedcart.smblevelworkshop.exception.SyncDatabasesException;
import craftedcart.smbworkshopexporter.util.LogHelper;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.FS;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class SyncManager {

    public static final String COMMUNITY_ROOT_URI = "https://github.com/CraftedCart/SMBLevelWorkshopCommunity.git";

    public static void syncDatabases() throws IOException, SyncDatabasesException, SAXException {
        ExecutorService cloneThreadPool = Executors.newFixedThreadPool(4);

        File communityDir = AppDataManager.getAppSupportDirectory();
        File rootDir = new File(communityDir, "community/root");
        File usersDir = new File(communityDir, "community/users");

        cloneOrPullRepo(rootDir, COMMUNITY_ROOT_URI);

        try {
            CommunityRootData.parseCreatorListXML(new File(rootDir, "CreatorList.xml"));
        } catch (ParserConfigurationException e) {
            LogHelper.error(SyncManager.class, "Error while parsing root/CreatorList.xml");
            LogHelper.error(SyncManager.class, "Aborting database sync");

            throw new SyncDatabasesException("Error while parsing root/CreatorList.xml", e);
        }

        try {
            CommunityRootData.parseAnnouncementsListXML(new File(rootDir, "Announcements.xml"));
        } catch (ParserConfigurationException e) {
            LogHelper.error(SyncManager.class, "Error while parsing root/Announcements.xml");
            LogHelper.error(SyncManager.class, "\n" + e + "\n" + LogHelper.stackTraceToString(e));

            //Failing to parse Announcements.xml should not stop syncing - so SyncDatabasesException is not thrown here
        }

        //TODO Parse Featured.xml

        //Clone root repos for each user
        LogHelper.info(SyncManager.class, "Cloning / resetting and pulling all user root repos"); //TODO update this string when single repos are supported

        List<Callable<Object>> toExecute = new ArrayList<>();

        for (ICommunityCreator creator : CommunityRootData.getCreatorList()) { //Loop through all creators
            if (creator instanceof CommunityUser) {
                //It's an entire user
                CommunityUser user = (CommunityUser) creator;

                File destDir = new File(usersDir, user.getUsername());
                AppDataManager.tryCreateDirectory(destDir);

                Callable thread = Executors.callable(new CloneSyncRootRepoThread(destDir, user.getUsername()));
                toExecute.add(thread);

            } else if (creator instanceof CommunityRepo) {
                //It's a single repo
                CommunityRepo repo = (CommunityRepo) creator;

                //TODO clone single repos on root branch
            }
        }

        //Invoke all clone threads
        try {
            cloneThreadPool.invokeAll(toExecute);
            LogHelper.info(SyncManager.class, "Done cloning / pulling all user repos!"); //TODO update this string when single repos are supported
        } catch (InterruptedException e) {
            LogHelper.error(SyncManager.class, "Error while cloning / pulling all user repos");
            LogHelper.error(SyncManager.class, "Aborting database sync");

            throw new SyncDatabasesException("Error while cloning / pulling all user repos", e);
        }

        cloneThreadPool.shutdown();
    }

    private static void cloneRepo(File destDir, String uri) throws GitAPIException {
        LogHelper.info(SyncManager.class, "Cloning " + uri + " into " + destDir.getAbsolutePath());

        Git git = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(destDir)
                .call();

        LogHelper.info(SyncManager.class, "Done cloning!");
    }

    /**
     * @param destDir Where to clone the repo / where the repo to pull is from
     * @param uri The remote URI of the repo
     * @throws SyncDatabasesException Thrown when a Git command fail
     * @throws IOException Thrown when cleaning a directory fails
     */
    public static void cloneOrPullRepo(File destDir, String uri) throws SyncDatabasesException, IOException {
        if (isGitRepo(destDir)) { //Check if the Git repo already exists
            try {
                FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder();
                repoBuilder.setMustExist(true);
                repoBuilder.setGitDir(new File(destDir, ".git"));
                Repository repo = repoBuilder.build();

                if (hasOneReference(repo)) {
                    //It's a good git repo
                    LogHelper.info(SyncManager.class, "Resetting and pulling from " + uri);
                    Git git = Git.wrap(repo);

                    try {
                        git.reset().setMode(ResetCommand.ResetType.HARD).call();

                        PullCommand pull = git.pull();
                        PullResult result = pull.call();

                        LogHelper.info(SyncManager.class, "Done pulling - Result: " + result.toString());
                    } catch (GitAPIException e) {
                        throw new SyncDatabasesException("Error while resetting and pulling from the Git repo at " + destDir.getAbsolutePath(), e);
                    }

                } else {
                    //It's a bad Git repo - Clean the directory and clone it again
                    FileUtils.cleanDirectory(destDir);

                    try {
                        cloneRepo(destDir, uri);
                    } catch (GitAPIException e) {
                        throw new SyncDatabasesException("Error while cloning from the Git repo at " + destDir.getAbsolutePath(), e);
                    }
                }

            } catch (IOException e) {
                LogHelper.error(SyncManager.class, "Error while building the Git repo at " + destDir.getAbsolutePath());
                LogHelper.error(SyncManager.class, "Re-syncing root repo");
                LogHelper.error(SyncManager.class, "\n" + e + "\n" + LogHelper.stackTraceToString(e));

                FileUtils.cleanDirectory(destDir);

                try {
                    cloneRepo(destDir, uri);
                } catch (GitAPIException e1) {
                    throw new SyncDatabasesException("Error while cloning from the Git repo at " + destDir.getAbsolutePath(), e1);
                }
            }
        } else {

            try {
                cloneRepo(destDir, uri);
            } catch (GitAPIException e) {
                throw new SyncDatabasesException("Error while cloning from the Git repo at " + destDir.getAbsolutePath(), e);
            }
        }
    }

    private static boolean isGitRepo(File dir) {
        return RepositoryCache.FileKey.isGitRepository(new File(dir, ".git"), FS.DETECTED);
    }

    /**
     * @return True if the repo has at least 1 non-null reference (Did it cloneRepo successfully?)
     */
    private static boolean hasOneReference(Repository repo) {
        for (Ref ref : repo.getAllRefs().values()) {
            if (ref.getObjectId() == null) {
                continue;
            }
            return true;
        }

        return false;
    }

    public static String getGitURL(String username, String repoName) {
        return String.format("https://github.com/%s/%s.git", username, repoName);
    }

}
