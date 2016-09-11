package craftedcart.smblevelworkshop.resource.model;

import craftedcart.smblevelworkshop.resource.IResource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CraftedCart on 25/02/2016 (DD/MM/YYYY)
 */
public class ResourceModel implements IResource {

    public List<Vector3f> vertices = new ArrayList<Vector3f>();
    public List<Vector2f> textures = new ArrayList<Vector2f>();
    public List<Vector3f> normals = new ArrayList<Vector3f>();
    public List<Face> faces = new ArrayList<Face>();

    ResourceModel() {}

    public static void drawModel(ResourceModel m) {

        //TODO: Use more efficient rendering

        GL11.glBegin(GL11.GL_TRIANGLES);
        {
            for (Face face : m.faces) {

                //Normal 1
                Vector3f n1 = m.normals.get((int) face.normal.x - 1);
                GL11.glNormal3f(n1.x, n1.y, n1.z);

                //Texture 1
                if (m.textures.size() > 0) {
                    Vector2f t1 = m.textures.get((int) face.texture.x - 1);
                    GL11.glTexCoord2f(t1.x, t1.y);
                }

                //Vertex 1
                Vector3f v1 = m.vertices.get((int) face.vertex.x - 1);
                GL11.glVertex3f(v1.x, v1.y, v1.z);

                //Normal 2
                Vector3f n2 = m.normals.get((int) face.normal.y - 1);
                GL11.glNormal3f(n2.x, n2.y, n2.z);

                //Texture 2
                if (m.textures.size() > 0) {
                    Vector2f t2 = m.textures.get((int) face.texture.y - 1);
                    GL11.glTexCoord2f(t2.x, t2.y);
                }

                //Vertex 2
                Vector3f v2 = m.vertices.get((int) face.vertex.y - 1);
                GL11.glVertex3f(v2.x, v2.y, v2.z);

                //Normal 3
                Vector3f n3 = m.normals.get((int) face.normal.z - 1);
                GL11.glNormal3f(n3.x, n3.y, n3.z);

                //Texture 3
                if (m.textures.size() > 0) {
                    Vector2f t3 = m.textures.get((int) face.texture.z - 1);
                    GL11.glTexCoord2f(t3.x, t3.y);
                }

                //Vertex 3
                Vector3f v3 = m.vertices.get((int) face.vertex.z - 1);
                GL11.glVertex3f(v3.x, v3.y, v3.z);

            }
        }
        GL11.glEnd();

    }

    public static void drawModelWireframe(ResourceModel m) {

        //TODO: Use more efficient rendering

        GL11.glBegin(GL11.GL_LINES);
        {
            for (Face face : m.faces) {

                //Vertex 1
                Vector3f v1 = m.vertices.get((int) face.vertex.x - 1);
                GL11.glVertex3f(v1.x, v1.y, v1.z);

                //Vertex 2
                Vector3f v2 = m.vertices.get((int) face.vertex.y - 1);
                GL11.glVertex3f(v2.x, v2.y, v2.z);

                //Vertex 1
                Vector3f v3 = m.vertices.get((int) face.vertex.x - 1);
                GL11.glVertex3f(v3.x, v3.y, v3.z);

                //Vertex 3
                Vector3f v4 = m.vertices.get((int) face.vertex.z - 1);
                GL11.glVertex3f(v4.x, v4.y, v4.z);

                //Vertex 2
                Vector3f v5 = m.vertices.get((int) face.vertex.y - 1);
                GL11.glVertex3f(v5.x, v5.y, v5.z);

                //Vertex 3
                Vector3f v6 = m.vertices.get((int) face.vertex.z - 1);
                GL11.glVertex3f(v6.x, v6.y, v6.z);

            }
        }
        GL11.glEnd();

    }

}
