package craftedcart.smblevelworkshop.resource.model;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by CraftedCart on 25/02/2016 (DD/MM/YYYY)
 */
public class OBJLoader {

    public static ResourceModel loadModel(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        ResourceModel m = new ResourceModel();

        String line;
        while ((line = reader.readLine()) != null) {

            if (line.startsWith("v ")) { //Vertex

                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);

                m.vertices.add(new Vector3f(x, y, z));

            } else if (line.startsWith("vn ")) { //Vertex Normal

                float x = Float.valueOf(line.split(" ")[1]);
                float y = Float.valueOf(line.split(" ")[2]);
                float z = Float.valueOf(line.split(" ")[3]);

                m.normals.add(new Vector3f(x, y, z));

            } else if (line.startsWith("vt ")) { //Vertex UV Texture Mapping

                float x = Float.valueOf(line.split(" ")[1]);
                float y = -Float.valueOf(line.split(" ")[2]);

                m.textures.add(new Vector2f(x, y));

            } else if (line.startsWith("f ")) {

                Vector3f vertexIndicies = new Vector3f(
                        Float.valueOf(line.split(" ")[1].split("/")[0]),
                        Float.valueOf(line.split(" ")[2].split("/")[0]),
                        Float.valueOf(line.split(" ")[3].split("/")[0])
                );

                Vector3f textureIndices;

                if (!line.split(" ")[1].split("/")[1].isEmpty()) {
                    textureIndices= new Vector3f(
                            Float.valueOf(line.split(" ")[1].split("/")[1]),
                            Float.valueOf(line.split(" ")[2].split("/")[1]),
                            Float.valueOf(line.split(" ")[3].split("/")[1])
                    );
                } else {
                    textureIndices = new Vector3f(0, 0, 0);
                }

                Vector3f normalIndicies = new Vector3f(
                        Float.valueOf(line.split(" ")[1].split("/")[2]),
                        Float.valueOf(line.split(" ")[2].split("/")[2]),
                        Float.valueOf(line.split(" ")[3].split("/")[2])
                );

                m.faces.add(new Face(vertexIndicies, textureIndices, normalIndicies));

            }

        }

        reader.close();

        return m;

    }

}
