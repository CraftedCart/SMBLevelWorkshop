package craftedcart.smblevelworkshop.community.sync;

import craftedcart.smblevelworkshop.community.CommunityRootData;
import craftedcart.smblevelworkshop.community.creator.CommunityRepo;
import craftedcart.smblevelworkshop.community.creator.CommunityUser;
import craftedcart.smblevelworkshop.community.creator.AbstractCommunityCreator;
import craftedcart.smblevelworkshop.data.AppDataManager;
import craftedcart.smblevelworkshop.exception.SyncDatabasesException;
import craftedcart.smbworkshopexporter.util.LogHelper;
import io.github.craftedcart.fluidui.uiaction.UIAction1;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

/**
 * @author CraftedCart
 *         Created on 05/10/2016 (DD/MM/YYYY)
 */
public class CloneSyncRootThread implements Runnable {

    private File destDir;
    private AbstractCommunityCreator user;
    private UIAction1<Boolean> onFinishAction;

    public CloneSyncRootThread(File destDir, AbstractCommunityCreator user, UIAction1<Boolean> onFinishAction) {
        this.destDir = destDir;
        this.user = user;
        this.onFinishAction = onFinishAction;
    }

    @Override
    public void run() {
        String username = user.getUsername();

        try {
            if (user instanceof CommunityUser) {
                SyncManager.cloneOrPullRepo(destDir, SyncManager.getGitURL(username, "root")); //Clone / pull repo
            } else if (user instanceof CommunityRepo) {
                SyncManager.cloneOrPullRepo(destDir, SyncManager.getGitURL(username, ((CommunityRepo) user).getRepoName()), "root"); //Clone / pull repo, branch "root"
            }

            //<editor-fold desc="Parse user/userProfile.xml">
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(destDir, "user/userProfile.xml"));

            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("userProfile"); //TODO Check userProfile version
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;

                if (element.getElementsByTagName("displayName").getLength() > 0) {
                    String displayName = element.getElementsByTagName("displayName").item(0).getTextContent();
                    user.setDisplayName(displayName);
                }

                if (element.getElementsByTagName("bioPath").getLength() > 0) {
                    String bioPath = element.getElementsByTagName("bioPath").item(0).getTextContent();
                    user.setBioPath(bioPath);
                }

            }

            if (onFinishAction != null) {
                onFinishAction.execute(true);
            }
            //</editor-fold>

        } catch (SyncDatabasesException | IOException | ParserConfigurationException | SAXException e) {
            LogHelper.error(SyncManager.class, "Ignoring user");
            LogHelper.error(SyncManager.class, "\n" + e + "\n" + LogHelper.stackTraceToString(e));

            File communityDir = AppDataManager.getAppSupportDirectory();
            File creatorXMLFile = new File(communityDir, "community/root/CreatorList.xml");

            try {
                CommunityRootData.removeCreator(creatorXMLFile, new CommunityUser(username));
            } catch (ParserConfigurationException | IOException | SAXException | TransformerException e1) {
                LogHelper.error(SyncManager.class, "Error while removing user " + username);
                LogHelper.error(SyncManager.class, "\n" + e1 + "\n" + LogHelper.stackTraceToString(e1));
            }

            if (onFinishAction != null) {
                onFinishAction.execute(false);
            }
        }
    }

}
