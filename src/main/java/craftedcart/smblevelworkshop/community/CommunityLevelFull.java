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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 09/10/2016 (DD/MM/YYYY)
 */
public class CommunityLevelFull extends CommunityLevel {

    private String description;
    private String licence;
    private int suggestedReplacement;
    private Map<String, String> stageNames = new HashMap<>();

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getLicence() {
        return licence;
    }

    public void setSuggestedReplacement(int suggestedReplacement) {
        this.suggestedReplacement = suggestedReplacement;
    }

    public int getSuggestedReplacement() {
        return suggestedReplacement;
    }

    public void setStageNames(Map<String, String> stageNames) {
        this.stageNames = stageNames;
    }

    public Map<String, String> getStageNames() {
        return stageNames;
    }

    public static CommunityLevelFull getCommunityLevelFullFromXML(File xmlFile, String username, String userDisplayName)
            throws ParserConfigurationException, IOException, SAXException {
        CommunityLevelFull level = new CommunityLevelFull();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("level"); //TODO Check level version
        Node node = nodeList.item(0);
        Element element = (Element) node;

        level.setUsername(username);
        level.setUserDisplayName(userDisplayName);
        level.setId(element.getElementsByTagName("id").item(0).getTextContent());
        level.setName(element.getElementsByTagName("name").item(0).getTextContent());
        level.setDescription(element.getElementsByTagName("description").item(0).getTextContent());
        try {
            level.setCreationTime(Long.parseLong(element.getElementsByTagName("creationTime").item(0).getTextContent()));
        } catch (NumberFormatException e) {
            LogHelper.error(CommunityLevelFull.class, String.format("Creation time for %s - %s is not an integer!", level.getUsername(), level.getId()));
        }
        level.setLicence(element.getElementsByTagName("licence").item(0).getTextContent());
        try {
            level.setSuggestedReplacement(Integer.parseInt(element.getElementsByTagName("suggestedReplacement").item(0).getTextContent()));
        } catch (NumberFormatException e) {
            LogHelper.error(CommunityLevelFull.class, String.format("Suggested replacement for %s - %s is not an integer!", level.getUsername(), level.getId()));
        }

        //Add locales
        Node stageNamesRootNode = element.getElementsByTagName("stageNames").item(0);
        NodeList stageNamesNodes = ((Element) stageNamesRootNode).getElementsByTagName("stageName");

        for (int i = 0; i < stageNamesNodes.getLength(); i++) {
            Node nameNode = stageNamesNodes.item(i);
            Element nameElement = (Element) nameNode;

            String localeID = nameElement.getAttribute("locale");

            if (!Objects.equals(localeID, "")) {
                level.getStageNames().put(localeID, nameNode.getTextContent());
            } else {
                LogHelper.error(CommunityLevelFull.class, String.format("No locale specified for stageName entry in %s - %s", level.getUsername(), level.getId()));
            }
        }

        return level;
    }

}
