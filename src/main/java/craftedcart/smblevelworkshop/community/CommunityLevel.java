package craftedcart.smblevelworkshop.community;

import craftedcart.smbworkshopexporter.util.LogHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 07/10/2016 (DD/MM/YYYY)
 */
public class CommunityLevel {

    private String username;
    private String userDisplayName;
    private String id;
    private String name;
    private String shortDescription;
    private long creationTime;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public static List<CommunityLevel> getCommunityLevelsFromXML(File xmlFile, String username, String userDisplayName)
            throws ParserConfigurationException, IOException, SAXException {
        List<CommunityLevel> levelList = new ArrayList<>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("levelList"); //TODO Check levelList version
        Node node = nodeList.item(0);
        Element element = (Element) node;

        NodeList levelRepoList = element.getElementsByTagName("levelRepo");


        //Add repos to creatorList
        for (int i = 0; i < levelRepoList.getLength(); i++) {
            Node repoNode = levelRepoList.item(i);

            Element levelRepoElement = (Element) repoNode;
            CommunityLevel level = new CommunityLevel();

            if (Objects.equals(username, "") ||
                    Objects.equals(levelRepoElement.getAttribute("id"), "") ||
                    Objects.equals(levelRepoElement.getAttribute("name"), "") ||
                    Objects.equals(levelRepoElement.getAttribute("creationTime"), "")) {

                //Attributes missing (Excluding shortDescription) - Skip this level
                LogHelper.warn(CommunityLevel.class,String.format("Attributes missing for level %s by %s - Skipping level",
                        username, levelRepoElement.getAttribute("id")));
                continue;
            }

            level.setUsername(username);
            level.setUserDisplayName(userDisplayName);
            level.setId(levelRepoElement.getAttribute("id"));
            level.setName(levelRepoElement.getAttribute("name"));
            level.setShortDescription(levelRepoElement.getAttribute("shortDescription"));
            try {
                level.setCreationTime(Long.valueOf(levelRepoElement.getAttribute("creationTime")));
            } catch (NumberFormatException e) {
                //Invalid time - Skip this level
                LogHelper.warn(CommunityLevel.class,String.format("Invalid time for level %s by %s - Skipping level",
                        username, levelRepoElement.getAttribute("id")));
                continue;
            }

            levelList.add(level);
        }

        return levelList;
    }

}
