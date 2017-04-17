package craftedcart.smblevelworkshop.ui.component;

import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.ResourceTexture;
import craftedcart.smblevelworkshop.ui.DefaultUITheme;
import io.github.craftedcart.fluidui.component.Button;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.component.TextButton;
import io.github.craftedcart.fluidui.util.UIColor;

/**
 * @author CraftedCart
 *         Created on 14/10/2016 (DD/MM/YYYY)
 */
public class OutlinerObject extends Panel {

    private String objectName;

    //UI
    private final TextButton objectButton = new TextButton();
    private final Button visibilityButton = new Button();

    //Textures
    private ResourceTexture visibiltyTex;
    private ResourceTexture visibiltyOffTex;

    public OutlinerObject(String objectName) {
        super();

        this.objectName = objectName;

        setTheme(new DefaultUITheme());

        visibiltyTex = ResourceManager.getTexture("image/visibility");
        visibiltyOffTex = ResourceManager.getTexture("image/visibilityOff");

        initComponents();
    }

    private void initComponents() {
        setName(objectName + "OutlinerObject");
        setBackgroundColor(UIColor.transparent());

        objectButton.setOnInitAction(() -> {
            objectButton.setTopLeftPos(0, 0);
            objectButton.setBottomRightPos(-22, 0);
            objectButton.setTopLeftAnchor(0, 0);
            objectButton.setBottomRightAnchor(1, 1);
            objectButton.setText(objectName);
        });
        objectButton.setOnLMBAction(() -> {
            assert ProjectManager.getCurrentClientLevelData() != null;

            if (Window.isShiftDown()) { //Toggle selection on shift
                ProjectManager.getCurrentClientLevelData().toggleSelectedObject(objectName);
            } else {
                ProjectManager.getCurrentClientLevelData().clearSelectedExternalBackgroundObjects();
                ProjectManager.getCurrentClientLevelData().clearSelectedObjects();
                ProjectManager.getCurrentClientLevelData().addSelectedObject(objectName);
            }
        });
        addChildComponent("objectButton", objectButton);

        visibilityButton.setOnInitAction(() -> {
            visibilityButton.setTopLeftPos(-20, 0);
            visibilityButton.setBottomRightPos(-2, 0);
            visibilityButton.setTopLeftAnchor(1, 0);
            visibilityButton.setBottomRightAnchor(1, 1);
            visibilityButton.setTexture(visibiltyTex.getTexture());
        });
        visibilityButton.setOnLMBAction(() -> {
            assert ProjectManager.getCurrentClientLevelData() != null;

            ClientLevelData cld = ProjectManager.getCurrentClientLevelData();
            assert cld != null;
            cld.toggleHiddenObject(objectName);

            if (cld.isObjectHidden(objectName)) {
                visibilityButton.setTexture(visibiltyOffTex.getTexture());
            } else {
                visibilityButton.setTexture(visibiltyTex.getTexture());
            }

        });
        addChildComponent("visibilityButton", visibilityButton);
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
        setName(objectName + "OutlinerObject");
    }

    public String getObjectName() {
        return objectName;
    }

    public void setButtonColor(UIColor color) {
        objectButton.setBackgroundIdleColor(color);
    }

}
