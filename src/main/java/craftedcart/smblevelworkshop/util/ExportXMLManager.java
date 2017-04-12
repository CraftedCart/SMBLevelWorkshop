package craftedcart.smblevelworkshop.util;

import craftedcart.smblevelworkshop.asset.*;
import craftedcart.smblevelworkshop.level.LevelData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import java.nio.file.Path;
import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 11/04/2017 (DD/MM/YYYY)
 */
public class ExportXMLManager {

    public static final int[] CONFIG_VERSION = new int[]{1, 0, 0};

    public static void writeXMLConfig(LevelData levelData, File file) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootE = doc.createElement("superMonkeyBallStage");
        rootE.setAttribute("version", String.format("%d.%d.%d", CONFIG_VERSION[0], CONFIG_VERSION[1], CONFIG_VERSION[2]));
        doc.appendChild(rootE);

        for (File objFile : levelData.getModelObjSources()) {
            Element iElement = doc.createElement("modelImport");
            iElement.setAttribute("type", "OBJ");

            Path basePath = file.getParentFile().toPath();
            Path destPath = objFile.toPath();
            String relativePath = basePath.relativize(destPath).toString();

            iElement.setTextContent("//" + relativePath); //TODO: Allow toggle for absolute paths

            rootE.appendChild(iElement);
        }

        //Add start
        Map.Entry<String, Placeable> start = levelData.getStartPosEntry();
        Element startE = doc.createElement("start");
        startE.appendChild(getNameElement(doc, start.getKey()));
        startE.appendChild(getPosElement(doc, start.getValue().getPosition()));
        startE.appendChild(getRotElement(doc, start.getValue().getRotation()));
        rootE.appendChild(startE);

        //Add fallout
        Map.Entry<String, Placeable> falloutPlane = levelData.getFalloutYEntry();
        Element falloutPlaneE = doc.createElement("falloutPlane");
        falloutPlaneE.setAttribute("y", String.valueOf(falloutPlane.getValue().getPosition().y));
        rootE.appendChild(falloutPlaneE);

        //Add background objects
        for (String name : levelData.getBackgroundReservedItemGroup().getObjectNames()) {
            Element bgE = doc.createElement("backgroundModel");

            bgE.appendChild(getNameElement(doc, name));
            bgE.appendChild(getPosElement(doc, new PosXYZ(0, 0, 0))); //TODO: Changeable positions
            bgE.appendChild(getRotElement(doc, new PosXYZ(0, 0, 0))); //TODO: Changeable rotations

            rootE.appendChild(bgE);
        }

        //TODO: External background models

        for (Map.Entry<String, WSItemGroup> entry : levelData.getItemGroupMap().entrySet()) {
            //Skip the STAGE_RESERVED and BACKGROUND_RESERVED item groups - It's not really an item group, but just used for SMBLW
            if (entry.getKey().equals("STAGE_RESERVED") || entry.getKey().equals("BACKGROUND_RESERVED")) continue;

            WSItemGroup itemGroup = entry.getValue();

            Element igE = doc.createElement("itemGroup");

            //Rotation center
            Element rotCenterE = doc.createElement("rotationCenter");
            rotCenterE.appendChild(getPosElement(doc, itemGroup.getRotationCenter()));
            igE.appendChild(rotCenterE);

            //Initial rotation
            Element initialRotE = doc.createElement("initialRotation");
            initialRotE.appendChild(getPosElement(doc, itemGroup.getInitialRotation()));
            igE.appendChild(initialRotE);

            //Collision grid
            Element collisionGridE = doc.createElement("collisionGrid");

            Element collisionStartE = doc.createElement("start");
            collisionStartE.setAttribute("x", "-256.0"); //TODO: Make this changeable
            collisionStartE.setAttribute("y", "-256.0");
            collisionGridE.appendChild(collisionStartE);

            Element collisionStepE = doc.createElement("step");
            collisionStepE.setAttribute("x", "32.0"); //TODO: Make this changeable
            collisionStepE.setAttribute("y", "32.0");
            collisionGridE.appendChild(collisionStepE);

            Element collisionCountE = doc.createElement("count");
            collisionCountE.setAttribute("x", "16"); //TODO: Make this changeable
            collisionCountE.setAttribute("y", "16");
            collisionGridE.appendChild(collisionCountE);

            //TODO: Collision objects

            igE.appendChild(collisionGridE);

            //Add level models
            for (String name : itemGroup.getObjectNames()) {
                Element lmE = doc.createElement("levelModel");
                lmE.setTextContent(name);
                igE.appendChild(lmE);
            }

            //Add placeables
            for (Map.Entry<String, Placeable> pEntry : itemGroup.getPlaceables().entrySet()) {
                String name = pEntry.getKey();
                Placeable p = pEntry.getValue();

                String typeName = "";

                if (p.getAsset() instanceof AssetGoal) typeName = "goal";
                else if (p.getAsset() instanceof AssetBumper) typeName = "bumper";
                else if (p.getAsset() instanceof AssetJamabar) typeName = "jamabar";
                else if (p.getAsset() instanceof AssetBanana) typeName = "banana";
                else if (p.getAsset() instanceof AssetWormhole) typeName = "wormhole";
                else if (p.getAsset() instanceof AssetFalloutVolume) typeName = "falloutVolume";

                Element pE = doc.createElement(typeName);

                pE.appendChild(getNameElement(doc, name)); //Add name

                pE.appendChild(getPosElement(doc, p.getPosition())); //All assets listed above can be moved freely
                if (p.getAsset().canRotate()) pE.appendChild(getRotElement(doc, p.getRotation()));
                if (p.getAsset().canScale()) pE.appendChild(getSclElement(doc, p.getScale()));

                if (p.getAsset().getValidTypes() != null) {
                    //This asset has different types - add specified type to XML
                    String typeEnumName = "";

                    assert p.getAsset().getType() != null;
                    switch (p.getAsset().getType()) {
                        case "blueGoal":
                            typeEnumName = "BLUE";
                            break;
                        case "greenGoal":
                            typeEnumName = "GREEN";
                            break;
                        case "redGoal":
                            typeEnumName = "RED";
                            break;
                        case "singleBanana":
                            typeEnumName = "SINGLE";
                            break;
                        case "bunchBanana":
                            typeEnumName = "BUNCH";
                            break;
                    }

                    Element typeE = doc.createElement("type");
                    typeE.setTextContent(typeEnumName);
                    pE.appendChild(typeE);
                }

                if (p.getAsset() instanceof AssetWormhole) {
                    //Wormholes need destinationName specified
                    Element destE = doc.createElement("destinationName");
                    destE.setTextContent(((AssetWormhole) p.getAsset()).getDestinationName());
                    pE.appendChild(destE);
                }

                igE.appendChild(pE);
            }

            rootE.appendChild(igE);
        }

        //Write the file
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer t = tFactory.newTransformer();
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource src = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        t.transform(src, result);
    }

    private static Element getNameElement(Document doc, String name) {
        Element e = doc.createElement("name");
        e.setTextContent(name);
        return e;
    }

    private static Element getPosElement(Document doc, PosXYZ vec) {
        Element e = doc.createElement("position");
        e.setAttribute("x", String.valueOf(vec.x));
        e.setAttribute("y", String.valueOf(vec.y));
        e.setAttribute("z", String.valueOf(vec.z));
        return e;
    }

    private static Element getRotElement(Document doc, PosXYZ vec) {
        Element e = doc.createElement("rotation");
        e.setAttribute("x", String.valueOf(vec.x));
        e.setAttribute("y", String.valueOf(vec.y));
        e.setAttribute("z", String.valueOf(vec.z));
        return e;
    }

    private static Element getSclElement(Document doc, PosXYZ vec) {
        Element e = doc.createElement("scale");
        e.setAttribute("x", String.valueOf(vec.x));
        e.setAttribute("y", String.valueOf(vec.y));
        e.setAttribute("z", String.valueOf(vec.z));
        return e;
    }

}
