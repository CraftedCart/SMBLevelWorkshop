package craftedcart.smblevelworkshop.resource.model;

import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.builder.Face;
import com.owens.oobjloader.builder.FaceVertex;
import com.owens.oobjloader.builder.Material;
import com.owens.oobjloader.lwjgl.DisplayModel;
import com.owens.oobjloader.lwjgl.TextureLoader;
import com.owens.oobjloader.lwjgl.VBO;
import com.owens.oobjloader.lwjgl.VBOFactory;
import com.owens.oobjloader.parser.Parse;
import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.util.LogHelper;
import org.lwjgl.LWJGLException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by CraftedCart on 25/02/2016 (DD/MM/YYYY)
 */
public class OBJLoader {

    public static boolean isLastObjTriangulated;

    public static ResourceModel loadModel(String filePath) throws IOException {

        try {
            Window.drawable.makeCurrent();
        } catch (LWJGLException e) {
            LogHelper.error(OBJLoader.class, e);
        }

        LogHelper.info(OBJLoader.class, "Parsing OBJ file");

        Build builder = new Build();

        try {
            new Parse(builder, filePath); //Constructor does work
        } catch (IOException e) {
            LogHelper.error(OBJLoader.class, "Error while loading OBJ");
            LogHelper.error(OBJLoader.class, e);
        }

//        LogHelper.info(OBJLoader.class, "Splitting OBJ file faces into list of faces per material");
//        ArrayList<ArrayList<Face>> facesByTextureList = createFaceListsByMaterial(builder);

        LogHelper.info(OBJLoader.class, "Splitting OBJ file faces into list of faces per object");
        ArrayList<ArrayList<Face>> facesByObjectList = createFaceListsByObject(builder);

        List<OBJObject> objectList = new ArrayList<>();

        for (ArrayList<Face> faceList : facesByObjectList) { //Split objects by material
            ArrayList<ArrayList<Face>> facesByMaterialList = createFaceListsByMaterial(faceList);

            OBJObject object = new OBJObject();

            for (ArrayList<Face> faceMatList : facesByMaterialList) {
                OBJFacesByMaterial facesByMaterial = new OBJFacesByMaterial();
                facesByMaterial.setFaceList(faceMatList);
                object.addFacesByMaterial(facesByMaterial);
            }

            objectList.add(object);
        }

        TextureLoader textureLoader = new TextureLoader();
        int defaultTextureID = 0;
//        if (defaultTextureMaterial != null) {
//            log.log(INFO, "Loading default texture =" + defaultTextureMaterial);
//            defaultTextureID = setUpDefaultTexture(textureLoader, defaultTextureMaterial);
//            log.log(INFO, "Done loading default texture =" + defaultTextureMaterial);
//        }
//        if (defaultTextureID == -1) {
//            BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
//            Graphics g = img.getGraphics();
//            g.setColor(Color.BLUE);
//            g.fillRect(0, 0, 256, 256);
//            g.setColor(Color.RED);
//            for (int loop = 0; loop < 256; loop++) {
//                g.drawLine(loop, 0, loop, 255);
//                g.drawLine(0, loop, 255, loop);
//            }
//            defaultTextureID = textureLoader.convertToTexture(img);
//        }
        int currentTextureID;

        OBJScene scene = new OBJScene();

        scene.setObjectList(objectList);

        for (OBJObject object : objectList) {
            for (OBJFacesByMaterial facesByMaterial : object.facesByMaterialList) {
                List<Face> faceList = facesByMaterial.faceList;

                if (faceList.isEmpty()) {
                    LogHelper.error(OBJLoader.class, "Got an empty face list. That shouldn't be possible.");
                    continue;
                }
                LogHelper.info(OBJLoader.class, "Getting material " + faceList.get(0).material);
                currentTextureID = getMaterialID(faceList.get(0).material, defaultTextureID, builder, textureLoader);
                LogHelper.info(OBJLoader.class, "Splitting any quads and throwing any faces with > 4 vertices.");
                ArrayList<Face> triangleList = splitQuads(faceList);
                LogHelper.info(OBJLoader.class, "Calculating any missing vertex normals.");
                calcMissingVertexNormals(triangleList);
                LogHelper.info(OBJLoader.class, "Ready to build VBO of " + triangleList.size() + " triangles");

                if (triangleList.size() <= 0) {
                    continue;
                }
                LogHelper.info(OBJLoader.class, "Building VBO");

                VBO vbo = VBOFactory.build(currentTextureID, faceList.get(0).material, triangleList);

                LogHelper.info(OBJLoader.class, "Adding VBO with texture id " + currentTextureID + ", with " + triangleList.size() + " triangles to OBJFacesByMaterial");
                facesByMaterial.setVbo(vbo);
            }
        }

        LogHelper.info(OBJLoader.class, "Done loading OBJ");

        try {
            Window.drawable.releaseContext();
        } catch (LWJGLException e) {
            LogHelper.error(OBJLoader.class, e);
        }

        return new ResourceModel(scene);
    }

    //Iterate over face list from builder, and break it up into a set of face lists by material, i.e. each for each face list, all faces in that specific list use the same material
    private static ArrayList<ArrayList<Face>> createFaceListsByMaterial(ArrayList<Face> faceList) {
        ArrayList<ArrayList<Face>> facesByTextureList;
        facesByTextureList = new ArrayList<>();
        Material currentMaterial = null;
        ArrayList<Face> currentFaceList;
        currentFaceList = new ArrayList<>();
        for (Face face : faceList) {
            if (face.material != currentMaterial) {
                if (!currentFaceList.isEmpty()) {
                    String matName = null;
                    if (currentMaterial != null) {
                        matName = currentMaterial.name;
                    }

                    LogHelper.trace(OBJLoader.class, "Adding list of " + currentFaceList.size() + " triangle faces with material " + matName + " to our list of lists of faces.");
                    facesByTextureList.add(currentFaceList);
                }

                String faceMatName = null;
                if (face.material != null) {
                    faceMatName = face.material.name;
                }

                LogHelper.trace(OBJLoader.class, "Creating new list of faces for material " + faceMatName);
                currentMaterial = face.material;
                currentFaceList = new ArrayList<>();
            }
            currentFaceList.add(face);
        }
        if (!currentFaceList.isEmpty()) {
            String matName = null;
            if (currentMaterial != null) {
                matName = currentMaterial.name;
            }

            LogHelper.trace(OBJLoader.class, "Adding list of " + currentFaceList.size() + " triangle faces with material " + matName + " to our list of lists of faces.");
            facesByTextureList.add(currentFaceList);
        }
        return facesByTextureList;
    }

    private static ArrayList<ArrayList<Face>> createFaceListsByObject(Build builder) {
        ArrayList<ArrayList<Face>> facesByObjectList = new ArrayList<>();
        String currentObjectName = null;
        ArrayList<Face> currentFaceList = new ArrayList<>();

        for (Face face : builder.faces) {
            if (!Objects.equals(face.objectName, currentObjectName)) {
                if (!currentFaceList.isEmpty()) {
                    LogHelper.trace(OBJLoader.class, "Adding list of " + currentFaceList.size() + " triangle faces of object " + currentObjectName + " to our list of lists of faces.");
                    facesByObjectList.add(currentFaceList);
                }

                LogHelper.trace(OBJLoader.class, "Creating new list of faces for object " + face.objectName);
                currentObjectName = face.objectName;
                currentFaceList = new ArrayList<>();
            }

            currentFaceList.add(face);
        }

        if (!currentFaceList.isEmpty()) {
            LogHelper.trace(OBJLoader.class, "Adding list of " + currentFaceList.size() + " triangle faces of object " + currentObjectName + " to our list of lists of faces.");
            facesByObjectList.add(currentFaceList);
        }

        return facesByObjectList;
    }


    // Get the specified Material, bind it as a texture, and return the OpenGL ID.  Returns the default texture ID if we can't
    // load the new texture, or if the material is a non texture and hence we ignore it.
    private static int getMaterialID(Material material, int defaultTextureID, Build builder, TextureLoader textureLoader) {
        int currentTextureID;
        if (material == null) {
            currentTextureID = defaultTextureID;
        } else if (material.mapKdFilename == null) {
            currentTextureID = defaultTextureID;
        } else {
            try {
                File objFile = new File(builder.objFilename);
                File mapKdFile = new File(objFile.getParent(), material.mapKdFilename);
                LogHelper.info(OBJLoader.class, "Trying to load  " + mapKdFile.getAbsolutePath());
                currentTextureID = textureLoader.load(mapKdFile.getAbsolutePath());
            } catch (IOException ex) {
                LogHelper.error(OBJLoader.class, "Failed to get material ID");
                LogHelper.info(OBJLoader.class, "Got an exception trying to load  texture material = " + material.mapKdFilename + " , ex=" + ex);
                ex.printStackTrace();
                LogHelper.info(OBJLoader.class, "Using default texture ID = " + defaultTextureID);
                currentTextureID = defaultTextureID;
            }
        }
        return currentTextureID;
    }

    // VBOFactory can only handle triangles, not faces with more than 3 vertices.  There are much better ways to 'triangulate' polygons, that
    // can be used on polygons with more than 4 sides, but for this simple test code justsplit quads into two triangles
    // and drop all polygons with more than 4 vertices.  (I was originally just dropping quads as well but then I kept ending up with nothing
    // left to display. :-)  Or at least, not much. )
    private static ArrayList<Face> splitQuads(List<Face> faceList) {
        ArrayList<Face> triangleList = new ArrayList<>();
        int countTriangles = 0;
        int countQuads = 0;
        int countNGons = 0;
        for (Face face : faceList) {
            if (face.vertices.size() == 3) {
                countTriangles++;
                triangleList.add(face);
            } else if (face.vertices.size() == 4) {
                countQuads++;
                FaceVertex v1 = face.vertices.get(0);
                FaceVertex v2 = face.vertices.get(1);
                FaceVertex v3 = face.vertices.get(2);
                FaceVertex v4 = face.vertices.get(3);
                Face f1 = new Face();
                f1.map = face.map;
                f1.material = face.material;
                f1.add(v1);
                f1.add(v2);
                f1.add(v3);
                triangleList.add(f1);
                Face f2 = new Face();
                f2.map = face.map;
                f2.material = face.material;
                f2.add(v1);
                f2.add(v3);
                f2.add(v4);
                triangleList.add(f2);
            } else {
                countNGons++;
            }
        }
        int texturedCount = 0;
        int normalCount = 0;
        for (Face face : triangleList) {
            if ((face.vertices.get(0).n != null)
                    && (face.vertices.get(1).n != null)
                    && (face.vertices.get(2).n != null)) {
                normalCount++;
            }
            if ((face.vertices.get(0).t != null)
                    && (face.vertices.get(1).t != null)
                    && (face.vertices.get(2).t != null)) {
                texturedCount++;
            }
        }
        LogHelper.info(OBJLoader.class, "Building VBO, originally " + faceList.size() + " faces, of which originally " + countTriangles + " triangles, " + countQuads + " quads,  and  " + countNGons + " n-polygons with more than 4 vertices that were dropped.");
        LogHelper.info(OBJLoader.class, "Triangle list has " + triangleList.size() + " rendered triangles of which " + normalCount + " have normals for all vertices and " + texturedCount + " have texture coords for all vertices.");

        isLastObjTriangulated = !(countQuads > 0 || countNGons > 0); //Check if OBJ was triangulated

        return triangleList;
    }

    //@TODO: This is a crappy way to calculate vertex normals if we are missing said normals.  I just wanted
    //something that would add normals since my simple VBO creation code expects them.  There are better ways
    //to generate normals,  especially given that the .obj file allows specification of "smoothing groups".
    private static void calcMissingVertexNormals(ArrayList<Face> triangleList) {
        for (Face face : triangleList) {
            face.calculateTriangleNormal();
            for (int loopv = 0; loopv < face.vertices.size(); loopv++) {
                FaceVertex fv = face.vertices.get(loopv);
                if (face.vertices.get(0).n == null) {
                    FaceVertex newFv = new FaceVertex();
                    newFv.v = fv.v;
                    newFv.t = fv.t;
                    newFv.n = face.faceNormal;
                    face.vertices.set(loopv, newFv);
                }
            }
        }
    }

}
