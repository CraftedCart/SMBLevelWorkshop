package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.SMBLWSettings;
import craftedcart.smblevelworkshop.asset.AssetStartPos;
import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.undo.UndoAddPlaceable;
import craftedcart.smblevelworkshop.undo.UndoAssetTransform;
import craftedcart.smblevelworkshop.undo.UndoCommand;
import craftedcart.smblevelworkshop.undo.UndoRemovePlaceable;
import craftedcart.smblevelworkshop.util.EnumMode;
import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.asset.AssetManager;
import craftedcart.smblevelworkshop.asset.IAsset;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.resource.model.OBJLoader;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.util.ExportManager;
import craftedcart.smblevelworkshop.util.LogHelper;
import craftedcart.smblevelworkshop.util.PosXYZ;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.component.Component;
import io.github.craftedcart.fluidui.component.Image;
import io.github.craftedcart.fluidui.component.Label;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * @author CraftedCart
 * Created on 02/04/2016 (DD/MM/YYYY)
 */
public class MainScreen extends FluidUIScreen {

    //Level
    @Nullable private ClientLevelData clientLevelData;
    @NotNull private EnumMode mode = EnumMode.NONE;
    @NotNull private PosXYZ modeDirection = new PosXYZ(0, 1, 0);

    //Camera
    @NotNull private PosXYZ cameraPos = new PosXYZ();
    @NotNull private PosXY cameraRot = new PosXY();


    //UI
    private final Image modeCursor = new Image();
    private final Component mainUI = new Component();
    private final Label modeLabel = new Label();
    private final Label modeDirectionLabel = new Label();
    public final ListBox outlinerListBox = new ListBox();
    private final Panel notifPanel = new Panel();

    //Undo
    @NotNull private List<UndoCommand> undoCommandList = new ArrayList<>();
    @NotNull private List<UndoCommand> redoCommandList = new ArrayList<>();

    //Notifications
    private int notificationID = 0;


    public MainScreen() {
        init();
    }

    private void init() {

        try {
            Window.drawable.makeCurrent();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        //Defined at class level
        modeCursor.setOnInitAction(() -> {
            modeCursor.setTopLeftPos(-16, -16);
            modeCursor.setBottomRightPos(16, 16);
            modeCursor.setTopLeftAnchor(0.5, 0.5);
            modeCursor.setBottomRightAnchor(0.5, 0.5);
            modeCursor.setVisible(false);
            modeCursor.setTexture(ResourceManager.getTexture("image/modeEditCursor").getTexture());
            modeCursor.setColor(UIColor.matGreen());
        });
        addChildComponent("modeCursor", modeCursor);

        //Defined at class level
        mainUI.setOnInitAction(() -> {
            mainUI.setTopLeftPos(0, 0);
            mainUI.setBottomRightPos(0, 0);
            mainUI.setTopLeftAnchor(0, 0);
            mainUI.setBottomRightAnchor(1, 1);
            mainUI.setTheme(new DefaultUITheme());
        });
        addChildComponent("mainUI", mainUI);

        //<editor-fold desc="ImportObj TextButton">
        final TextButton importObjButton = new TextButton();
        importObjButton.setOnInitAction(() -> {
            importObjButton.setText(LangManager.getItem("importObj"));
            importObjButton.setTopLeftPos(-132, 4);
            importObjButton.setBottomRightPos(-4, 28);
            importObjButton.setTopLeftAnchor(0.5, 0);
            importObjButton.setBottomRightAnchor(0.5, 0);
        });
        importObjButton.setOnLMBAction(() -> importObj());
        mainUI.addChildComponent("importObjButton", importObjButton);
        //</editor-fold>

        //<editor-fold desc="Export TextButton">
        final TextButton exportButton = new TextButton();
        exportButton.setOnInitAction(() -> {
            exportButton.setText(LangManager.getItem("export"));
            exportButton.setTopLeftPos(4, 4);
            exportButton.setBottomRightPos(132, 28);
            exportButton.setTopLeftAnchor(0.5, 0);
            exportButton.setBottomRightAnchor(0.5, 0);
        });
        exportButton.setOnLMBAction(() -> export());
        mainUI.addChildComponent("exportButton", exportButton);
        //</editor-fold>

        //<editor-fold desc="Bottom Panel">
        final Panel bottomPanel = new Panel();
        bottomPanel.setOnInitAction(() -> {
            bottomPanel.setBackgroundColor(UIColor.matGrey900(0.75));
            bottomPanel.setTopLeftPos(260, -50);
            bottomPanel.setBottomRightPos(-260, -4);
            bottomPanel.setTopLeftAnchor(0, 1);
            bottomPanel.setBottomRightAnchor(1, 1);
        });
        mainUI.addChildComponent("bottomPanel", bottomPanel);
        //</editor-fold>

        //<editor-fold desc="Mode Label">
        //Defined at class level
        modeLabel.setOnInitAction(() -> {
            modeLabel.setTopLeftPos(4, 0);
            modeLabel.setBottomRightPos(-4, 24);
            modeLabel.setTopLeftAnchor(0, 0);
            modeLabel.setBottomRightAnchor(1, 0);
            modeLabel.setTextColor(UIColor.matWhite());
        });
        bottomPanel.addChildComponent("modeLabel", modeLabel);
        //</editor-fold>

        //<editor-fold desc="Mode Direction Label">
        //Defined at class level
        modeDirectionLabel.setOnInitAction(() -> {
            modeDirectionLabel.setTopLeftPos(4, 24);
            modeDirectionLabel.setBottomRightPos(-4, 48);
            modeDirectionLabel.setTopLeftAnchor(0, 0);
            modeDirectionLabel.setBottomRightAnchor(1, 0);
        });
        bottomPanel.addChildComponent("modeDirectionLabel", modeDirectionLabel);
        //</editor-fold>

        final Panel leftPanel = new Panel();
        leftPanel.setOnInitAction(() -> {
            leftPanel.setBackgroundColor(UIColor.matGrey900(0.75));
            leftPanel.setTopLeftPos(0, 0);
            leftPanel.setBottomRightPos(256, 0);
            leftPanel.setTopLeftAnchor(0, 0);
            leftPanel.setBottomRightAnchor(0, 1);
        });
        mainUI.addChildComponent("leftPanel", leftPanel);

        final Label addPlaceableLabel = new Label();
        addPlaceableLabel.setOnInitAction(() -> {
            addPlaceableLabel.setText(LangManager.getItem("addPlaceable"));
            addPlaceableLabel.setHorizontalAlign(EnumHAlignment.centre);
            addPlaceableLabel.setVerticalAlign(EnumVAlignment.centre);
            addPlaceableLabel.setTopLeftPos(4, 4);
            addPlaceableLabel.setBottomRightPos(-4, 28);
            addPlaceableLabel.setTopLeftAnchor(0, 0);
            addPlaceableLabel.setBottomRightAnchor(1, 0);
        });
        leftPanel.addChildComponent("addPlaceableLabel", addPlaceableLabel);

        final ListBox addPlaceableListBox = new ListBox();
        addPlaceableListBox.setOnInitAction(() -> {
            addPlaceableListBox.setBackgroundColor(UIColor.transparent());
            addPlaceableListBox.setTopLeftPos(0, 28);
            addPlaceableListBox.setBottomRightPos(0, 0);
            addPlaceableListBox.setTopLeftAnchor(0, 0);
            addPlaceableListBox.setBottomRightAnchor(1, 0.25);
        });
        leftPanel.addChildComponent("addPlaceableListBox", addPlaceableListBox);

        //<editor-fold desc="Add placeable buttons">
        for (IAsset asset : AssetManager.getAvaliableAssets()) {
            final TextButton placeableButton = new TextButton();
            placeableButton.setOnInitAction(() -> {
                placeableButton.setText(LangManager.getItem(asset.getName()));
                placeableButton.setTopLeftPos(0, 0);
                placeableButton.setBottomRightPos(0, 18);
            });
            placeableButton.setOnLMBAction(() -> addPlaceable(new Placeable(asset)));
            addPlaceableListBox.addChildComponent(asset.getName() + "AddPlaceableButton", placeableButton);
        }
        //</editor-fold>

        final Label outlinerLabel = new Label();
        outlinerLabel.setOnInitAction(() -> {
            outlinerLabel.setText(LangManager.getItem("outliner"));
            outlinerLabel.setHorizontalAlign(EnumHAlignment.centre);
            outlinerLabel.setVerticalAlign(EnumVAlignment.centre);
            outlinerLabel.setTopLeftPos(4, 4);
            outlinerLabel.setBottomRightPos(-4, 28);
            outlinerLabel.setTopLeftAnchor(0, 0.25);
            outlinerLabel.setBottomRightAnchor(1, 0.25);
        });
        leftPanel.addChildComponent("outlinerLabel", outlinerLabel);

        //Defined at class level
        outlinerListBox.setOnInitAction(() -> {
            outlinerListBox.setBackgroundColor(UIColor.transparent());
            outlinerListBox.setTopLeftPos(0, 28);
            outlinerListBox.setBottomRightPos(0, 0);
            outlinerListBox.setTopLeftAnchor(0, 0.25);
            outlinerListBox.setBottomRightAnchor(1, 1);
        });
        leftPanel.addChildComponent("outlinerListBox", outlinerListBox);

        final Panel rightPanel = new Panel();
        rightPanel.setOnInitAction(() -> {
            rightPanel.setBackgroundColor(UIColor.matGrey900(0.75));
            rightPanel.setTopLeftPos(-256, 0);
            rightPanel.setBottomRightPos(0, 0);
            rightPanel.setTopLeftAnchor(1, 0);
            rightPanel.setBottomRightAnchor(1, 1);
        });
        mainUI.addChildComponent("rightPanel", rightPanel);

        final Label propertiesLabel = new Label();
        propertiesLabel.setOnInitAction(() -> {
            propertiesLabel.setText(LangManager.getItem("properties"));
            propertiesLabel.setHorizontalAlign(EnumHAlignment.centre);
            propertiesLabel.setVerticalAlign(EnumVAlignment.centre);
            propertiesLabel.setTopLeftPos(4, 4);
            propertiesLabel.setBottomRightPos(-4, 28);
            propertiesLabel.setTopLeftAnchor(0, 0);
            propertiesLabel.setBottomRightAnchor(1, 0);
        });
        rightPanel.addChildComponent("propertiesLabel", propertiesLabel);

        //Defined at class level
        notifPanel.setOnInitAction(() -> {
            notifPanel.setTopLeftPos(0, 0);
            notifPanel.setBottomRightPos(0, 0);
            notifPanel.setTopLeftAnchor(0, 0);
            notifPanel.setBottomRightAnchor(1, 1);
            notifPanel.setBackgroundColor(UIColor.transparent());
        });
        mainUI.addChildComponent("notifPanel", notifPanel);

        try {
            Window.drawable.releaseContext();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void preDraw() {
        super.preDraw();

        if (clientLevelData != null) {
            //<editor-fold desc="Darken selected placeables in the outliner">
            for (Map.Entry<String, Component> entry : outlinerListBox.childComponents.entrySet()) {
                assert entry.getValue() instanceof TextButton;
                TextButton button = (TextButton) entry.getValue();

                if (clientLevelData.isPlaceableSelected(button.text)) {
                    button.setBackgroundIdleColor(UIColor.matBlue900());
                } else {
                    button.setBackgroundIdleColor(UIColor.matBlue());
                }
            }
            //</editor-fold>
        }

        if (Mouse.isButtonDown(2)) { //If MMB down
            //<editor-fold desc="Rotate camera on MMB & Move camera with MMB & WASDQE">
            cameraRot = cameraRot.add(UIUtils.getMouseDelta().toPosXY());

            //X
            if (cameraRot.x >= 360) {
                cameraRot.x -= 360;
            } else if (cameraRot.x <= -360) {
                cameraRot.x += 360;
            }

            //Y
            if (cameraRot.y > 90) {
                cameraRot.y = 90;
            } else if (cameraRot.y < -90) {
                cameraRot.y = -90;
            }

            PosXYZ forwardVector = new PosXYZ(Math.sin(Math.toRadians(cameraRot.x)), 0, -Math.cos(Math.toRadians(cameraRot.x)));
            PosXYZ rightVector = new PosXYZ(Math.sin(Math.toRadians(cameraRot.x + 90)), 0, -Math.cos(Math.toRadians(cameraRot.x + 90)));

            double speed = Window.isShiftDown() ? SMBLWSettings.cameraSprintSpeedMultiplier * SMBLWSettings.cameraSpeed : SMBLWSettings.cameraSpeed;

            if (Keyboard.isKeyDown(Keyboard.KEY_Q)) { //Q: Go Down
                cameraPos = cameraPos.add(0, -UIUtils.getDelta() * speed, 0);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_E)) { //E: Go Up
                cameraPos = cameraPos.add(0, UIUtils.getDelta() * speed, 0);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_W)) { //W: Go Forwards
                cameraPos = cameraPos.add(forwardVector.multiply(UIUtils.getDelta()).multiply(speed));
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_S)) { //S: Go Backwards
                cameraPos = cameraPos.subtract(forwardVector.multiply(UIUtils.getDelta()).multiply(speed));
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_D)) { //D: Go Right
                cameraPos = cameraPos.add(rightVector.multiply(UIUtils.getDelta()).multiply(speed));
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_A)) { //A: Go Left
                cameraPos = cameraPos.subtract(rightVector.multiply(UIUtils.getDelta()).multiply(speed));
            }
            //</editor-fold>
        } else if (clientLevelData != null) {
            if (mode == EnumMode.GRAB) {
                //<editor-fold desc="Grab">
                for (String key : clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = clientLevelData.getLevelData().getPlaceable(key);

                    if (Window.isShiftDown()) { //Precise movement with shift
                        placeable.setPosition(placeable.getPosition().add(modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity)));
                        placeable.setPosition(placeable.getPosition().add(modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity)));
                    } else {
                        placeable.setPosition(placeable.getPosition().add(modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity)));
                        placeable.setPosition(placeable.getPosition().add(modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity)));
                    }
                }
                //</editor-fold>
            } else if (mode == EnumMode.ROTATE) {
                //<editor-fold desc="Rotate">
                for (String key : clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = clientLevelData.getLevelData().getPlaceable(key);

                    if (placeable.getAsset().canRotate()) { //If can rotate
                        if (Window.isShiftDown()) { //Precise movement with shift
                            placeable.setRotation(placeable.getRotation().add(modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity)));
                            placeable.setRotation(placeable.getRotation().add(modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity)));
                        } else {
                            placeable.setRotation(placeable.getRotation().add(modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity)));
                            placeable.setRotation(placeable.getRotation().add(modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity)));
                        }
                    }
                }
                //</editor-fold>
            } else if (mode == EnumMode.SCALE) {
                //<editor-fold desc="Scale">
                for (String key : clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = clientLevelData.getLevelData().getPlaceable(key);

                    if (placeable.getAsset().canScale()) { //If can scale
                        if (Window.isShiftDown()) { //Precise movement with shift
                            placeable.setScale(placeable.getScale().add(modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity)));
                            placeable.setScale(placeable.getScale().add(modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity)));
                        } else {
                            placeable.setScale(placeable.getScale().add(modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity)));
                            placeable.setScale(placeable.getScale().add(modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity)));
                        }
                    }
                }
                //</editor-fold>
            }
        }


        //<editor-fold desc="Set Mode Label">
        String modeStringKey;
        switch (mode) {
            case NONE:
                modeStringKey = "none";
                break;
            case GRAB:
                modeStringKey = "grab";
                break;
            case ROTATE:
                modeStringKey = "rotate";
                break;
            case SCALE:
                modeStringKey = "scale";
                break;
            default:
                //This shouldn't happen
                modeStringKey = "invalid";
                break;
        }

        modeLabel.setText(String.format(LangManager.getItem("modeLabelFormat"), LangManager.getItem(modeStringKey)));
        //</editor-fold>

        //<editor-fold desc="Mode Direction Label">
        String modeDirectionString;
        if (modeDirection.equals(new PosXYZ(1, 0, 0))) {
            modeDirectionString = LangManager.getItem("axisX");
        } else if (modeDirection.equals(new PosXYZ(0, 1, 0))) {
            modeDirectionString = LangManager.getItem("axisY");
        } else if (modeDirection.equals(new PosXYZ(0, 0, 1))) {
            modeDirectionString = LangManager.getItem("axisZ");
        } else if (modeDirection.equals(new PosXYZ(1, 1, 1))) {
            modeDirectionString = LangManager.getItem("axisUniform");
        } else {
            modeDirectionString = String.format("%.2f, %.2f, %.2f", modeDirection.x, modeDirection.y, modeDirection.z);
        }

        modeDirectionLabel.setText(String.format(LangManager.getItem("modeDirectionLabelFormat"), modeDirectionString));
        //</editor-fold>

        if (mode == EnumMode.NONE) {
            modeCursor.setVisible(false);
        } else {
            modeCursor.setVisible(true);
        }

        if (Mouse.isButtonDown(2) ||
                mode != EnumMode.NONE) {
            if (!Mouse.isGrabbed()) {
                Mouse.setGrabbed(true);
            }
        } else {
            if (Mouse.isGrabbed()) {
                Mouse.setGrabbed(false);
            }
        }

    }

    @Override
    public void draw() {
        topLeftPos = new PosXY(0, 0);
        topLeftPx = new PosXY(0, 0);
        bottomRightPos = new PosXY(Display.getWidth(), Display.getHeight());
        bottomRightPx = new PosXY(Display.getWidth(), Display.getHeight());

        if (overlayUiScreen == null) {
            getParentMousePos();
        } else {
            mousePos = null;
        }

        preDraw();
        drawViewport();
        postDraw();

        if (overlayUiScreen != null) {
            overlayUiScreen.draw();
        }
    }

    private void drawViewport() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glPushMatrix();

        //<editor-fold desc="Setup the matrix">
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(90, Display.getWidth() / (float) Display.getHeight(), 0.01f, 1000f);
        //</editor-fold>

        GL11.glRotated(cameraRot.y, 1, 0, 0);
        GL11.glRotated(cameraRot.x, 0, 1, 0);

        GL11.glTranslated(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        //<editor-fold desc="Draw X line">
        UIColor.matRed().bindColor();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(-10000, 0, 0);
        GL11.glVertex3d(10000, 0, 0);
        GL11.glEnd();
        //</editor-fold>

        //<editor-fold desc="Draw Z line">
        UIColor.matBlue().bindColor();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(0, 0, -10000);
        GL11.glVertex3d(0, 0, 10000);
        GL11.glEnd();
        //</editor-fold>

        UIColor.pureWhite().bindColor();

        if (clientLevelData != null && clientLevelData.getLevelData().getModel() != null) {
            //<editor-fold desc="Draw model with wireframes">
            GL20.glUseProgram(ResourceManager.getShaderProgram("tileShaderProgram").getProgramID());
            ResourceModel.drawModel(clientLevelData.getLevelData().getModel());
            GL20.glUseProgram(0);

            GL11.glLineWidth(2);
            GL11.glColor4f(0, 0, 0, 1);
            ResourceModel.drawModelWireframe(clientLevelData.getLevelData().getModel());

            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GL11.glColor4f(0, 0, 0, 0.02f);
            ResourceModel.drawModelWireframe(clientLevelData.getLevelData().getModel());
            //</editor-fold>

            for (Map.Entry<String, Placeable> placeableEntry : clientLevelData.getLevelData().getPlacedObjects().entrySet()) {
                String name = placeableEntry.getKey();
                Placeable placeable = placeableEntry.getValue();
                ResourceModel model = placeable.getAsset().getModel();
                boolean isSelected = clientLevelData.isPlaceableSelected(name);

                GL11.glPushMatrix();

                GL11.glTranslated(placeable.getPosition().x, placeable.getPosition().y, placeable.getPosition().z);
                GL11.glRotated(placeable.getRotation().z, 0, 0, 1);
                GL11.glRotated(placeable.getRotation().y, 0, 1, 0);
                GL11.glRotated(placeable.getRotation().x, 1, 0, 0);
                GL11.glColor4d(placeable.getAsset().getColor().r, placeable.getAsset().getColor().g, placeable.getAsset().getColor().b, placeable.getAsset().getColor().a);

                GL11.glEnable(GL11.GL_DEPTH_TEST);

                GL11.glScaled(placeable.getScale().x, placeable.getScale().y, placeable.getScale().z);
                GL20.glUseProgram(ResourceManager.getShaderProgram("tileShaderProgram").getProgramID());
                ResourceModel.drawModel(model);
                GL20.glUseProgram(0);

                //<editor-fold desc="Draw blue wireframe and direction line if selected, else draw orange wireframe">
                if (isSelected) {
                    if (mode != EnumMode.NONE) {
                        GL11.glPushMatrix();

                        GL11.glRotated(-placeable.getRotation().x, 1, 0, 0);
                        GL11.glRotated(-placeable.getRotation().y, 0, 1, 0);
                        GL11.glRotated(-placeable.getRotation().z, 0, 0, 1);

                        if (modeDirection.equals(new PosXYZ(1, 0, 0))) {
                            //<editor-fold desc="Draw X line">
                            UIColor.matRed(0.75).bindColor();
                            GL11.glBegin(GL11.GL_LINES);
                            GL11.glVertex3d(-10000, 0, 0);
                            GL11.glVertex3d(10000, 0, 0);
                            GL11.glEnd();
                            //</editor-fold>
                        } else if (modeDirection.equals(new PosXYZ(0, 1, 0))) {
                            //<editor-fold desc="Draw Y line">
                            UIColor.matGreen(0.75).bindColor();
                            GL11.glBegin(GL11.GL_LINES);
                            GL11.glVertex3d(0, -10000, 0);
                            GL11.glVertex3d(0, 10000, 0);
                            GL11.glEnd();
                            //</editor-fold>
                        } else if (modeDirection.equals(new PosXYZ(0, 0, 1))) {
                            //<editor-fold desc="Draw Z line">
                            UIColor.matBlue(0.75).bindColor();
                            GL11.glBegin(GL11.GL_LINES);
                            GL11.glVertex3d(0, 0, -10000);
                            GL11.glVertex3d(0, 0, 10000);
                            GL11.glEnd();
                            //</editor-fold>
                        }

                        GL11.glPopMatrix();
                    }

                    UIColor.matBlue().bindColor();
                } else {
                    UIColor.matOrange().bindColor();
                }
                ResourceModel.drawModelWireframe(model);
                //</editor-fold>

                GL11.glDisable(GL11.GL_DEPTH_TEST);

                //<editor-fold desc="Draw blue wireframe if selected, else draw orange wireframe (Ignores depth test - Is semi transparent)">
                if (isSelected) {
                    UIColor.matBlue(0.05).bindColor();
                } else {
                    UIColor.matOrange(0.02).bindColor();
                }
                ResourceModel.drawModelWireframe(model);
                //</editor-fold>

                GL11.glPopMatrix();

            }
        }

        GL11.glPopMatrix();

        GL11.glColor3f(1, 1, 1);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        Window.setMatrix();
    }

    @Override
    public void onClick(int button, PosXY mousePos) {
        if (button == 0 && mode != EnumMode.NONE) { //LMB: Confirm action
            confirmModeAction();
        } else if (button == 1 && mode != EnumMode.NONE) { //RMB: Discard action
            discardModeAction();
        } else {
            super.onClick(button, mousePos);
        }
    }

    @Override
    public void onKey(int key, char keyChar) {
        if (overlayUiScreen != null) {
            overlayUiScreen.onKey(key, keyChar);
        } else if (!Mouse.isButtonDown(2)) { //If MMB not down
            if (key == Keyboard.KEY_F1) { //F1 to hide / show the ui
                mainUI.setVisible(!mainUI.isVisible());

            } else if (clientLevelData != null) {
                if (key == Keyboard.KEY_ESCAPE) {
                    discardModeAction();
                } else if (key == Keyboard.KEY_RETURN) {
                    confirmModeAction();

                } else if (mode == EnumMode.NONE) {
                    if (key == Keyboard.KEY_G) { //G: Grab
                        addUndoCommand(new UndoAssetTransform(clientLevelData, clientLevelData.getSelectedPlaceables()));
                        mode = EnumMode.GRAB;
                    } else if (key == Keyboard.KEY_R) { //R: Rotate
                        addUndoCommand(new UndoAssetTransform(clientLevelData, clientLevelData.getSelectedPlaceables()));
                        mode = EnumMode.ROTATE;
                    } else if (key == Keyboard.KEY_S) { //S: Scale
                        addUndoCommand(new UndoAssetTransform(clientLevelData, clientLevelData.getSelectedPlaceables()));
                        mode = EnumMode.SCALE;

                    } else if (key == Keyboard.KEY_Z) { //Ctrl / Cmd Z: Undo - Ctrl / Cmd Shift Z: Redo
                        if (Window.isCtrlOrCmdDown()) {
                            if (Window.isShiftDown()) {
                                //Redo
                                redo();
                            } else {
                                //Undo
                                undo();
                            }
                        }
                    }

                } else if (key == Keyboard.KEY_X) { //X Axis
                    modeDirection = new PosXYZ(1, 0, 0);
                    modeCursor.setColor(UIColor.matRed());
                } else if (key == Keyboard.KEY_Y) { //Y Axis
                    modeDirection = new PosXYZ(0, 1, 0);
                    modeCursor.setColor(UIColor.matGreen());
                } else if (key == Keyboard.KEY_Z) { //Z Axis
                    modeDirection = new PosXYZ(0, 0, 1);
                    modeCursor.setColor(UIColor.matBlue());
                } else if (key == Keyboard.KEY_U) { //XYZ (Uniform)
                    modeDirection = new PosXYZ(1, 1, 1);
                    modeCursor.setColor(UIColor.matWhite());


                } else {
                    super.onKey(key, keyChar);
                }
            }
        }
    }

    private void confirmModeAction() {
        mode = EnumMode.NONE;
        assert clientLevelData != null;
    }

    private void discardModeAction() {
        mode = EnumMode.NONE;
        undo();
    }

    private void notify(String message) {
        for (Map.Entry<String, Component> entry : notifPanel.childComponents.entrySet()) {
            assert entry.getValue().plugins.get(1) instanceof NotificationPlugin;
            ((NotificationPlugin) entry.getValue().plugins.get(1)).time = 1.5;
        }

        final Panel panel = new Panel();
        panel.setOnInitAction(() -> {
            panel.setTopLeftPos(260, -80);
            panel.setBottomRightPos(-260, -56);
            panel.setTopLeftAnchor(0, 1.2);
            panel.setBottomRightAnchor(1, 1.2);
            panel.setBackgroundColor(UIColor.matGrey900(0.75));
        });
        PluginSmoothAnimateAnchor animateAnchor = new PluginSmoothAnimateAnchor();
        panel.addPlugin(animateAnchor);
        panel.addPlugin(new NotificationPlugin(animateAnchor));
        notifPanel.addChildComponent("notificationPanel" + String.valueOf(notificationID), panel);

        final Label label = new Label();
        label.setOnInitAction(() -> {
            label.setTopLeftPos(4, 0);
            label.setBottomRightPos(-4, 0);
            label.setTopLeftAnchor(0, 0);
            label.setBottomRightAnchor(1, 1);
            label.setText(message);
        });
        panel.addChildComponent("label", label);

        notificationID++;
    }

    private void addPlaceable(Placeable placeable) {
        if (clientLevelData != null) {
            String name = clientLevelData.getLevelData().addPlaceable(placeable);
            clientLevelData.clearSelectedPlaceables();
            clientLevelData.addSelectedPlaceable(name);

            addUndoCommand(new UndoAddPlaceable(clientLevelData, this, name, placeable));

            outlinerListBox.addChildComponent(getOutlinerPlaceableComponent(name));
        } else {
            notify(LangManager.getItem("noLevelLoaded"));
        }
    }

    private void removePlaceable(String name) {
        if (clientLevelData != null) {

            addUndoCommand(new UndoRemovePlaceable(clientLevelData, this, name, clientLevelData.getLevelData().getPlaceable(name)));

            clientLevelData.removeSelectedPlaceable(name);
            clientLevelData.getLevelData().removePlaceable(name);

            outlinerListBox.removeChildComponent(name + "OutlinerPlaceable");
        } else {
            notify(LangManager.getItem("noLevelLoaded"));
        }
    }

    private void newLevelData(FileInputStream fileInputStream) throws IOException {
        //Clear outliner list box
        outlinerListBox.clearChildComponents();

        //Reset undo history
        undoCommandList.clear();
        redoCommandList.clear();

        clientLevelData = new ClientLevelData();
        clientLevelData.getLevelData().setModel(OBJLoader.loadModel(fileInputStream));

        Placeable startPosPlaceable = new Placeable(new AssetStartPos());
        startPosPlaceable.setPosition(new PosXYZ(0, 1, 0));
        String name = clientLevelData.getLevelData().addPlaceable(startPosPlaceable);
        clientLevelData.addSelectedPlaceable(name);

        try {
            Window.drawable.makeCurrent();

            outlinerListBox.addChildComponent(getOutlinerPlaceableComponent(name));

            Window.drawable.releaseContext();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    private void addUndoCommand(UndoCommand undoCommand) {
        undoCommandList.add(undoCommand);
        redoCommandList.clear();
    }

    private void undo() {
        if (undoCommandList.size() > 0) {
            redoCommandList.add(undoCommandList.get(undoCommandList.size() - 1).getRedoCommand());
            undoCommandList.get(undoCommandList.size() - 1).undo();
            notify(undoCommandList.get(undoCommandList.size() - 1).getUndoMessage());
            undoCommandList.remove(undoCommandList.size() - 1);
        }

    }

    private void redo() {
        if (redoCommandList.size() > 0) {
            undoCommandList.add(redoCommandList.get(redoCommandList.size() - 1).getRedoCommand());
            redoCommandList.get(redoCommandList.size() - 1).undo();
            notify(redoCommandList.get(redoCommandList.size() - 1).getRedoMessage());
            redoCommandList.remove(redoCommandList.size() - 1);
        }
    }

    public Component getOutlinerPlaceableComponent(String name) {
        final TextButton placeableButton = new TextButton();
        placeableButton.setOnInitAction(() -> {
            placeableButton.setTopLeftPos(0, 0);
            placeableButton.setBottomRightPos(0, 18);
            placeableButton.setText(name);
        });
        placeableButton.setOnLMBAction(() -> {
            assert clientLevelData != null;

            if (Window.isShiftDown()) { //Add to selection on shift
                clientLevelData.addSelectedPlaceable(name);
            } else {
                clientLevelData.clearSelectedPlaceables();
                clientLevelData.addSelectedPlaceable(name);
            }
        });
        placeableButton.setName(name + "OutlinerPlaceable");

        return placeableButton;
    }

    private void importObj() {
        new Thread(() -> {
            FileDialog fd = new FileDialog((Frame) null);
            fd.setMode(FileDialog.LOAD);
            fd.setFilenameFilter((dir, filename) -> filename.toUpperCase().endsWith(".OBJ"));
            fd.setVisible(true);

            File[] files = fd.getFiles();
            if (files != null && files.length > 0) {
                File file = files[0];
                LogHelper.info(getClass(), "Opening file: " + file.getAbsolutePath());

                try {
                    newLevelData(new FileInputStream(file));
                } catch (IOException e) {
                    LogHelper.error(getClass(), "Failed to open file");
                    LogHelper.error(getClass(), e);
                }
            }
        }, "ObjFileOpenThread").start();
    }

    private void export() {
        if (clientLevelData != null) {
            new Thread(() -> {
                FileDialog fd = new FileDialog((Frame) null);
                fd.setMode(FileDialog.SAVE);
                fd.setFile("config.txt");
                fd.setVisible(true);

                File[] files = fd.getFiles();
                if (files != null && files.length > 0) {
                    File file = files[0];
                    LogHelper.info(getClass(), "Exporting file: " + file.getAbsolutePath());

                    String exportContents = ExportManager.getConfig(clientLevelData.getLevelData());

                    try {
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        writer.write(exportContents);
                        writer.close();
                    } catch (IOException e) {
                        LogHelper.error(getClass(), "Error while exporting");
                        LogHelper.error(getClass(), e);
                    }
                }
            }, "ExportThread").start();
        } else {
            notify(LangManager.getItem("noLevelLoaded"));
        }
    }

}
