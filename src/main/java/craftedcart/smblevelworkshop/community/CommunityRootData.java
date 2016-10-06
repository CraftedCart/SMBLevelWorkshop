package craftedcart.smblevelworkshop.community;

import craftedcart.smblevelworkshop.community.creator.CommunityRepo;
import craftedcart.smblevelworkshop.community.creator.CommunityUser;
import craftedcart.smblevelworkshop.community.creator.ICommunityCreator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class CommunityRootData {

    private static List<ICommunityCreator> creatorList = new ArrayList<>();
    private static List<CommunityAnnouncement> announcementList = new ArrayList<>();

    public static void parseCreatorListXML(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        creatorList.clear();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("creatorList"); //TODO Check creatorList version
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Element element = (Element) node;

            NodeList userList = element.getElementsByTagName("user");
            NodeList repoList = element.getElementsByTagName("repo");

            //Add users to creatorList
            for (int j = 0; j < userList.getLength(); j++) {
                Node userNode = userList.item(i);
                Element userElement = (Element) userNode;
                creatorList.add(new CommunityUser(userElement.getAttribute("username")));
            }

            //Add repos to creatorList
            for (int j = 0; j < repoList.getLength(); j++) {
                Node repoNode = repoList.item(i);
                Element repoElement = (Element) repoNode;
                creatorList.add(new CommunityRepo(repoElement.getAttribute("username"), repoElement.getAttribute("repoName")));
            }
        }

    }

    public static void removeCreator(File xmlFile, ICommunityCreator creator) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        creatorList.remove(creator);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("creatorList"); //TODO Check creatorList version
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Element element = (Element) node;

            if (creator instanceof CommunityUser) {
                NodeList userList = element.getElementsByTagName("user");
                String username = ((CommunityUser) creator).getUsername();

                for (int j = 0; j < userList.getLength(); j++) {
                    Node userNode = userList.item(i);
                    Element userElement = (Element) userNode;

                    if (Objects.equals(userElement.getAttribute("username"), username)) {
                        userElement.removeChild(userNode);

                        getXMLString(doc);
                        break;
                    }
                }

                throw new NullPointerException("CommunityUser not found in XML file"); //We iterated through the whole XML without finding the user

            } else if (creator instanceof CommunityRepo) {
                NodeList repoList = element.getElementsByTagName("repo");
                //TODO Single repos
            }

        }
    }

    public static void parseAnnouncementsListXML(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        announcementList.clear();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("announcements"); //TODO check announcements version
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Element element = (Element) node;

            NodeList entryList = element.getElementsByTagName("entry");

            for (int j = 0; j < entryList.getLength(); j++) {
                String title = ((Element) entryList.item(j)).getElementsByTagName("title").item(0).getTextContent();
                String body = ((Element) entryList.item(j)).getElementsByTagName("body").item(0).getTextContent();

                announcementList.add(new CommunityAnnouncement(title, body));
            }
        }
    }

    public static String getXMLString(Document doc) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        return result.getWriter().toString();
    }

    public static List<ICommunityCreator> getCreatorList() {
        return creatorList;
    }

    public static List<CommunityAnnouncement> getAnnouncementList() {
        return announcementList;
    }

}
