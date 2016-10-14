package craftedcart.smblevelworkshop.ui.component;

import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.ui.DefaultUITheme;
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

    public OutlinerObject(String objectName) {
        super();

        this.objectName = objectName;

        setTheme(new DefaultUITheme());
        initComponents();
    }

    private void initComponents() {
        setName(objectName + "OutlinerObject");
        setBackgroundColor(UIColor.transparent());

        objectButton.setOnInitAction(() -> {
            objectButton.setTopLeftPos(0, 0);
            objectButton.setBottomRightPos(0, 0);
            objectButton.setTopLeftAnchor(0, 0);
            objectButton.setBottomRightAnchor(1, 1);
            objectButton.setText(objectName);
        });
        objectButton.setOnLMBAction(() -> {
            assert ProjectManager.getCurrentProject().clientLevelData != null;

            if (Window.isShiftDown()) { //Toggle selection on shift
                ProjectManager.getCurrentProject().clientLevelData.toggleSelectedObject(objectName);
            } else {
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedExternalBackgroundObjects();
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedObjects();
                ProjectManager.getCurrentProject().clientLevelData.addSelectedObject(objectName);
            }
        });
        addChildComponent("objectButton", objectButton);
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
