package craftedcart.smblevelworkshop.ui;

import com.owens.oobjloader.lwjgl.VBO;
import craftedcart.smblevelworkshop.SMBLWSettings;
import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.asset.*;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.ResourceShaderProgram;
import craftedcart.smblevelworkshop.resource.model.OBJLoader;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.ui.community.CommunityScreen;
import craftedcart.smblevelworkshop.ui.theme.DefaultUITheme;
import craftedcart.smblevelworkshop.undo.*;
import craftedcart.smblevelworkshop.util.*;
import craftedcart.smblevelworkshop.util.LogHelper;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.IUIScreen;
import io.github.craftedcart.fluidui.component.Component;
import io.github.craftedcart.fluidui.component.Image;
import io.github.craftedcart.fluidui.component.Label;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.component.TextField;
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
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

/**
 * @author CraftedCart
 * Created on 02/04/2016 (DD/MM/YYYY)
 */
public class MainScreen extends FluidUIScreen {

    //Camera
    @NotNull private PosXYZ cameraPos = new PosXYZ(5, 5, 5);
    @NotNull private PosXY cameraRot = new PosXY(-45, 35);

    //UI
    private final Image modeCursor = new Image();
    private final Component mainUI = new Component();
    private final Label modeLabel = new Label();
    private final Label modeDirectionLabel = new Label();
    public final ListBox outlinerListBox = new ListBox();
    private final Panel notifPanel = new Panel();

    private final TextButton importObjButton = new TextButton();
    private final TextButton exportButton = new TextButton();
    private final TextButton settingsButton = new TextButton();
    private final TextButton communityButton = new TextButton();

    //UI: Properties
    private final TextField positionXTextField = new TextField();
    private final TextField positionYTextField = new TextField();
    private final TextField positionZTextField = new TextField();

    private final TextField rotationXTextField = new TextField();
    private final TextField rotationYTextField = new TextField();
    private final TextField rotationZTextField = new TextField();

    private final TextField scaleXTextField = new TextField();
    private final TextField scaleYTextField = new TextField();
    private final TextField scaleZTextField = new TextField();

    private final TextButton typeButton = new TextButton();
    @Nullable private List<String> typeList = null;

    //Undo
    @NotNull private List<UndoCommand> undoCommandList = new ArrayList<>();
    @NotNull private List<UndoCommand> redoCommandList = new ArrayList<>();

    private boolean preventRendering = false; //Used when unloading textures and VBOs
    private boolean isLoadingProject = false; //Just in case disabling the button is too slow

    //Notifications
    private int notificationID = 0;

    //Mouse & Scroll Wheel Delta (For snapping)
    private double deltaX = 0;

    //Locks
    private final Object renderingLock = new Object();


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

        //<editor-fold desc="ProjectManager.getCurrentProject().mode Label">
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

        //<editor-fold desc="ProjectManager.getCurrentProject().mode Direction Label">
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
            placeableButton.setOnLMBAction(() -> addPlaceable(new Placeable(asset.getCopy())));
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

        final ListBox rightListBox = new ListBox();
        rightListBox.setOnInitAction(() -> {
            rightListBox.setBackgroundColor(UIColor.matGrey900(0.75));
            rightListBox.setTopLeftPos(-256, 0);
            rightListBox.setBottomRightPos(0, 0);
            rightListBox.setTopLeftAnchor(1, 0);
            rightListBox.setBottomRightAnchor(1, 1);
        });
        mainUI.addChildComponent("rightListBox", rightListBox);

        //<editor-fold desc="ImportObj TextButton">
        //Defined at class level
        importObjButton.setOnInitAction(() -> {
            importObjButton.setText(LangManager.getItem("importObj"));
            importObjButton.setTopLeftPos(0, 0);
            importObjButton.setBottomRightPos(0, 24);
        });
        importObjButton.setOnLMBAction(this::importObj);
        rightListBox.addChildComponent("importObjButton", importObjButton);
        //</editor-fold>

        //<editor-fold desc="Export TextButton">
        //Defined at class level
        exportButton.setOnInitAction(() -> {
            exportButton.setText(LangManager.getItem("export"));
            exportButton.setTopLeftPos(0, 0);
            exportButton.setBottomRightPos(0, 24);
        });
        exportButton.setOnLMBAction(this::export);
        rightListBox.addChildComponent("exportButton", exportButton);
        //</editor-fold>

        //<editor-fold desc="Community TextButton">
        //Defined at class level
        communityButton.setOnInitAction(() -> {
            communityButton.setText(LangManager.getItem("community"));
            communityButton.setTopLeftPos(0, 0);
            communityButton.setBottomRightPos(0, 24);
        });
        communityButton.setOnLMBAction(this::showCommunity);
        rightListBox.addChildComponent("communityButton", communityButton);
        //</editor-fold>

        //<editor-fold desc="Settings TextButton">
        //Defined at class level
        settingsButton.setOnInitAction(() -> {
            settingsButton.setText(LangManager.getItem("settings"));
            settingsButton.setTopLeftPos(0, 0);
            settingsButton.setBottomRightPos(0, 24);
        });
        settingsButton.setOnLMBAction(this::showSettings);
        rightListBox.addChildComponent("settingsButton", settingsButton);
        //</editor-fold>

        final Panel propertiesLabelPanel = new Panel();
        propertiesLabelPanel.setOnInitAction(() -> {
            propertiesLabelPanel.setTopLeftPos(0, 0);
            propertiesLabelPanel.setBottomRightPos(0, 28);
        });
        rightListBox.addChildComponent("propertiesLabelPanel", propertiesLabelPanel);

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
        propertiesLabelPanel.addChildComponent("propertiesLabel", propertiesLabel);

        final Label positionLabel = new Label();
        positionLabel.setOnInitAction(() -> {
            positionLabel.setText(LangManager.getItem("position"));
            positionLabel.setVerticalAlign(EnumVAlignment.centre);
            positionLabel.setTopLeftPos(0, 0);
            positionLabel.setBottomRightPos(0, 24);
        });
        rightListBox.addChildComponent("positionLabel", positionLabel);

        //<editor-fold desc="Position X Text Field">
        //Defined at class level
        positionXTextField.setOnInitAction(() -> {
            positionXTextField.setValue("0.00");
            positionXTextField.cursorPos = positionXTextField.value.length();
            positionXTextField.setVerticalAlign(EnumVAlignment.centre);
            positionXTextField.setTopLeftPos(0, 0);
            positionXTextField.setBottomRightPos(0, 24);
            positionXTextField.setBackgroundColor(UIColor.matRed900());
            positionXTextField.setInputRegexCheck("[0-9.-]");
            positionXTextField.setEnabled(false);
        });
        positionXTextField.setOnSelectedAction(() -> positionXTextField.cursorPos = positionXTextField.value.length());
        positionXTextField.setOnTabAction(() -> {
            positionXTextField.setSelected(false);
            positionYTextField.setSelected(true);
        });
        positionXTextField.setOnReturnAction(() -> positionXTextField.setSelected(false));
        positionXTextField.setOnValueConfirmedAction(() -> {
            double newValue;

            //<editor-fold desc="Parse the number">
            try {
                newValue = Double.parseDouble(positionXTextField.value);

                assert ProjectManager.getCurrentProject().clientLevelData != null;

                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                    if (placeable.getAsset().canGrabX()) {
                        placeable.setPosition(new PosXYZ(newValue, placeable.getPosition().y, placeable.getPosition().z));
                    }
                }
            } catch (NumberFormatException e) {
                notify(LangManager.getItem("invalidNumber"));
                try {
                    newValue = Double.parseDouble(positionXTextField.prevValue);
                } catch (NumberFormatException e1) {
                    LogHelper.error(getClass(), "prevValue was not a number!");
                    LogHelper.error(getClass(), e1);
                    newValue = 0;

                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                    for (Map.Entry<String, Placeable> entry : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects().entrySet()) {
                        Placeable placeable = entry.getValue();
                        if (placeable.getAsset().canGrabX()) {
                            placeable.setPosition(new PosXYZ(newValue, placeable.getPosition().y, placeable.getPosition().z));
                        }
                    }}
            }
            //</editor-fold>

            updatePropertiesPanel();
        });
        rightListBox.addChildComponent("positionXTextField", positionXTextField);
        //</editor-fold>

        //<editor-fold desc="Position Y Text Field">
        //Defined at class level
        positionYTextField.setOnInitAction(() -> {
            positionYTextField.setValue("0.00");
            positionYTextField.cursorPos = positionYTextField.value.length();
            positionYTextField.setVerticalAlign(EnumVAlignment.centre);
            positionYTextField.setTopLeftPos(0, 0);
            positionYTextField.setBottomRightPos(0, 24);
            positionYTextField.setBackgroundColor(UIColor.matGreen900());
            positionYTextField.setInputRegexCheck("[0-9.-]");
            positionYTextField.setEnabled(false);
        });
        positionYTextField.setOnSelectedAction(() -> positionYTextField.cursorPos = positionYTextField.value.length());
        positionYTextField.setOnTabAction(() -> {
            positionYTextField.setSelected(false);
            positionZTextField.setSelected(true);
        });
        positionYTextField.setOnReturnAction(() -> positionYTextField.setSelected(false));
        positionYTextField.setOnValueConfirmedAction(() -> {
            double newValue;

            //<editor-fold desc="Parse the number">
            try {
                newValue = Double.parseDouble(positionYTextField.value);

                assert ProjectManager.getCurrentProject().clientLevelData != null;

                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                    if (placeable.getAsset().canGrabY()) {
                        placeable.setPosition(new PosXYZ(placeable.getPosition().x, newValue, placeable.getPosition().z));
                    }
                }
            } catch (NumberFormatException e) {
                notify(LangManager.getItem("invalidNumber"));
                try {
                    newValue = Double.parseDouble(positionYTextField.prevValue);
                } catch (NumberFormatException e1) {
                    LogHelper.error(getClass(), "prevValue was not a number!");
                    LogHelper.error(getClass(), e1);
                    newValue = 0;

                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                        Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                        placeable.setPosition(new PosXYZ(placeable.getPosition().x, newValue, placeable.getPosition().z));
                    }}
            }
            //</editor-fold>

            updatePropertiesPanel();
        });
        rightListBox.addChildComponent("positionYTextField", positionYTextField);
        //</editor-fold>

        //<editor-fold desc="Position Z Text Field">
        //Defined at class level
        positionZTextField.setOnInitAction(() -> {
            positionZTextField.setValue("0.00");
            positionZTextField.cursorPos = positionZTextField.value.length();
            positionZTextField.setVerticalAlign(EnumVAlignment.centre);
            positionZTextField.setTopLeftPos(0, 0);
            positionZTextField.setBottomRightPos(0, 24);
            positionZTextField.setBackgroundColor(UIColor.matBlue900());
            positionZTextField.setInputRegexCheck("[0-9.-]");
            positionZTextField.setEnabled(false);
        });
        positionZTextField.setOnSelectedAction(() -> positionZTextField.cursorPos = positionZTextField.value.length());
        positionZTextField.setOnTabAction(() -> {
            positionZTextField.setSelected(false);
            rotationXTextField.setSelected(true);
        });
        positionZTextField.setOnReturnAction(() -> positionZTextField.setSelected(false));
        positionZTextField.setOnValueConfirmedAction(() -> {
            double newValue;

            //<editor-fold desc="Parse the number">
            try {
                newValue = Double.parseDouble(positionZTextField.value);

                assert ProjectManager.getCurrentProject().clientLevelData != null;

                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                    if (placeable.getAsset().canGrabZ()) {
                        placeable.setPosition(new PosXYZ(placeable.getPosition().x, placeable.getPosition().y, newValue));
                    }
                }
            } catch (NumberFormatException e) {
                notify(LangManager.getItem("invalidNumber"));
                try {
                    newValue = Double.parseDouble(positionZTextField.prevValue);
                } catch (NumberFormatException e1) {
                    LogHelper.error(getClass(), "prevValue was not a number!");
                    LogHelper.error(getClass(), e1);
                    newValue = 0;

                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                    for (Map.Entry<String, Placeable> entry : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects().entrySet()) {
                        Placeable placeable = entry.getValue();
                        placeable.setPosition(new PosXYZ(placeable.getPosition().x, newValue, placeable.getPosition().z));
                    }}
            }
            //</editor-fold>

            updatePropertiesPanel();
        });
        rightListBox.addChildComponent("positionZTextField", positionZTextField);
        //</editor-fold>

        final Label rotationLabel = new Label();
        rotationLabel.setOnInitAction(() -> {
            rotationLabel.setText(LangManager.getItem("rotation"));
            rotationLabel.setVerticalAlign(EnumVAlignment.centre);
            rotationLabel.setTopLeftPos(0, 0);
            rotationLabel.setBottomRightPos(0, 24);
        });
        rightListBox.addChildComponent("rotationLabel", rotationLabel);

        //<editor-fold desc="Rotation X Text Field">
        //Defined at class level
        rotationXTextField.setOnInitAction(() -> {
            rotationXTextField.setValue("0.00");
            rotationXTextField.cursorPos = rotationXTextField.value.length();
            rotationXTextField.setVerticalAlign(EnumVAlignment.centre);
            rotationXTextField.setTopLeftPos(0, 0);
            rotationXTextField.setBottomRightPos(0, 24);
            rotationXTextField.setBackgroundColor(UIColor.matRed900());
            rotationXTextField.setInputRegexCheck("[0-9.-]");
            rotationXTextField.setEnabled(false);
        });
        rotationXTextField.setOnSelectedAction(() -> rotationXTextField.cursorPos = rotationXTextField.value.length());
        rotationXTextField.setOnTabAction(() -> {
            rotationXTextField.setSelected(false);
            rotationYTextField.setSelected(true);
        });
        rotationXTextField.setOnReturnAction(() -> rotationXTextField.setSelected(false));
        rotationXTextField.setOnValueConfirmedAction(() -> {
            double newValue;

            //<editor-fold desc="Parse the number">
            try {
                newValue = Double.parseDouble(rotationXTextField.value);

                assert ProjectManager.getCurrentProject().clientLevelData != null;

                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                    if (placeable.getAsset().canRotate()) {
                        placeable.setRotation(normalizeRotation(new PosXYZ(newValue, placeable.getRotation().y, placeable.getRotation().z)));
                    }
                }
            } catch (NumberFormatException e) {
                notify(LangManager.getItem("invalidNumber"));
                try {
                    newValue = Double.parseDouble(rotationXTextField.prevValue);
                } catch (NumberFormatException e1) {
                    LogHelper.error(getClass(), "prevValue was not a number!");
                    LogHelper.error(getClass(), e1);
                    newValue = 0;

                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                    for (Map.Entry<String, Placeable> entry : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects().entrySet()) {
                        Placeable placeable = entry.getValue();
                        placeable.setRotation(new PosXYZ(newValue, placeable.getRotation().y, placeable.getRotation().z));
                    }}
            }
            //</editor-fold>

            updatePropertiesPanel();
        });
        rightListBox.addChildComponent("rotationXTextField", rotationXTextField);
        //</editor-fold>

        //<editor-fold desc="Rotation Y Text Field">
        //Defined at class level
        rotationYTextField.setOnInitAction(() -> {
            rotationYTextField.setValue("0.00");
            rotationYTextField.cursorPos = rotationYTextField.value.length();
            rotationYTextField.setVerticalAlign(EnumVAlignment.centre);
            rotationYTextField.setTopLeftPos(0, 0);
            rotationYTextField.setBottomRightPos(0, 24);
            rotationYTextField.setBackgroundColor(UIColor.matGreen900());
            rotationYTextField.setInputRegexCheck("[0-9.-]");
            rotationYTextField.setEnabled(false);
        });
        rotationYTextField.setOnSelectedAction(() -> rotationYTextField.cursorPos = rotationYTextField.value.length());
        rotationYTextField.setOnTabAction(() -> {
            rotationYTextField.setSelected(false);
            rotationZTextField.setSelected(true);
        });
        rotationYTextField.setOnReturnAction(() -> rotationYTextField.setSelected(false));
        rotationYTextField.setOnValueConfirmedAction(() -> {
            double newValue;

            //<editor-fold desc="Parse the number">
            try {
                newValue = Double.parseDouble(rotationYTextField.value);

                assert ProjectManager.getCurrentProject().clientLevelData != null;

                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                    if (placeable.getAsset().canRotate()) {
                        placeable.setRotation(normalizeRotation(new PosXYZ(placeable.getRotation().x, newValue, placeable.getRotation().z)));
                    }
                }
            } catch (NumberFormatException e) {
                notify(LangManager.getItem("invalidNumber"));
                try {
                    newValue = Double.parseDouble(rotationYTextField.prevValue);
                } catch (NumberFormatException e1) {
                    LogHelper.error(getClass(), "prevValue was not a number!");
                    LogHelper.error(getClass(), e1);
                    newValue = 0;

                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                        Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                        placeable.setRotation(new PosXYZ(placeable.getRotation().x, newValue, placeable.getRotation().z));
                    }}
            }
            //</editor-fold>

            updatePropertiesPanel();
        });
        rightListBox.addChildComponent("rotationYTextField", rotationYTextField);
        //</editor-fold>

        //<editor-fold desc="Rotation Z Text Field">
        //Defined at class level
        rotationZTextField.setOnInitAction(() -> {
            rotationZTextField.setValue("0.00");
            rotationZTextField.cursorPos = rotationZTextField.value.length();
            rotationZTextField.setVerticalAlign(EnumVAlignment.centre);
            rotationZTextField.setTopLeftPos(0, 0);
            rotationZTextField.setBottomRightPos(0, 24);
            rotationZTextField.setBackgroundColor(UIColor.matBlue900());
            rotationZTextField.setInputRegexCheck("[0-9.-]");
            rotationZTextField.setEnabled(false);
        });
        rotationZTextField.setOnSelectedAction(() -> rotationZTextField.cursorPos = rotationZTextField.value.length());
        rotationZTextField.setOnTabAction(() -> {
            rotationZTextField.setSelected(false);
            scaleXTextField.setSelected(true);
        });
        rotationZTextField.setOnReturnAction(() -> rotationZTextField.setSelected(false));
        rotationZTextField.setOnValueConfirmedAction(() -> {
            double newValue;

            //<editor-fold desc="Parse the number">
            try {
                newValue = Double.parseDouble(rotationZTextField.value);

                assert ProjectManager.getCurrentProject().clientLevelData != null;

                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                    if (placeable.getAsset().canRotate()) {
                        placeable.setRotation(normalizeRotation(new PosXYZ(placeable.getRotation().x, placeable.getRotation().y, newValue)));
                    }
                }
            } catch (NumberFormatException e) {
                notify(LangManager.getItem("invalidNumber"));
                try {
                    newValue = Double.parseDouble(rotationZTextField.prevValue);
                } catch (NumberFormatException e1) {
                    LogHelper.error(getClass(), "prevValue was not a number!");
                    LogHelper.error(getClass(), e1);
                    newValue = 0;

                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                    for (Map.Entry<String, Placeable> entry : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects().entrySet()) {
                        Placeable placeable = entry.getValue();
                        placeable.setRotation(new PosXYZ(placeable.getRotation().x, newValue, placeable.getRotation().z));
                    }}
            }
            //</editor-fold>

            updatePropertiesPanel();
        });
        rightListBox.addChildComponent("rotationZTextField", rotationZTextField);
        //</editor-fold>

        final Label scaleLabel = new Label();
        scaleLabel.setOnInitAction(() -> {
            scaleLabel.setText(LangManager.getItem("scale"));
            scaleLabel.setVerticalAlign(EnumVAlignment.centre);
            scaleLabel.setTopLeftPos(0, 0);
            scaleLabel.setBottomRightPos(0, 24);
        });
        rightListBox.addChildComponent("scaleLabel", scaleLabel);

        //<editor-fold desc="Scale X Text Field">
        //Defined at class level
        scaleXTextField.setOnInitAction(() -> {
            scaleXTextField.setValue("0.00");
            scaleXTextField.cursorPos = scaleXTextField.value.length();
            scaleXTextField.setVerticalAlign(EnumVAlignment.centre);
            scaleXTextField.setTopLeftPos(0, 0);
            scaleXTextField.setBottomRightPos(0, 24);
            scaleXTextField.setBackgroundColor(UIColor.matRed900());
            scaleXTextField.setInputRegexCheck("[0-9.-]");
            scaleXTextField.setEnabled(false);
        });
        scaleXTextField.setOnSelectedAction(() -> scaleXTextField.cursorPos = scaleXTextField.value.length());
        scaleXTextField.setOnTabAction(() -> {
            scaleXTextField.setSelected(false);
            scaleYTextField.setSelected(true);
        });
        scaleXTextField.setOnReturnAction(() -> scaleXTextField.setSelected(false));
        scaleXTextField.setOnValueConfirmedAction(() -> {
            double newValue;

            //<editor-fold desc="Parse the number">
            try {
                newValue = Double.parseDouble(scaleXTextField.value);

                assert ProjectManager.getCurrentProject().clientLevelData != null;

                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                    if (placeable.getAsset().canScale()) {
                        placeable.setScale(new PosXYZ(newValue, placeable.getScale().y, placeable.getScale().z));
                    }
                }
            } catch (NumberFormatException e) {
                notify(LangManager.getItem("invalidNumber"));
                try {
                    newValue = Double.parseDouble(scaleXTextField.prevValue);
                } catch (NumberFormatException e1) {
                    LogHelper.error(getClass(), "prevValue was not a number!");
                    LogHelper.error(getClass(), e1);
                    newValue = 0;

                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                    for (Map.Entry<String, Placeable> entry : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects().entrySet()) {
                        Placeable placeable = entry.getValue();
                        placeable.setScale(new PosXYZ(newValue, placeable.getScale().y, placeable.getScale().z));
                    }}
            }
            //</editor-fold>

            updatePropertiesPanel();
        });
        rightListBox.addChildComponent("scaleXTextField", scaleXTextField);
        //</editor-fold>

        //<editor-fold desc="Scale Y Text Field">
        //Defined at class level
        scaleYTextField.setOnInitAction(() -> {
            scaleYTextField.setValue("0.00");
            scaleYTextField.cursorPos = scaleYTextField.value.length();
            scaleYTextField.setVerticalAlign(EnumVAlignment.centre);
            scaleYTextField.setTopLeftPos(0, 0);
            scaleYTextField.setBottomRightPos(0, 24);
            scaleYTextField.setBackgroundColor(UIColor.matGreen900());
            scaleYTextField.setInputRegexCheck("[0-9.-]");
            scaleYTextField.setEnabled(false);
        });
        scaleYTextField.setOnSelectedAction(() -> scaleYTextField.cursorPos = scaleYTextField.value.length());
        scaleYTextField.setOnTabAction(() -> {
            scaleYTextField.setSelected(false);
            scaleZTextField.setSelected(true);
        });
        scaleYTextField.setOnReturnAction(() -> scaleYTextField.setSelected(false));
        scaleYTextField.setOnValueConfirmedAction(() -> {
            double newValue;

            //<editor-fold desc="Parse the number">
            try {
                newValue = Double.parseDouble(scaleYTextField.value);

                assert ProjectManager.getCurrentProject().clientLevelData != null;

                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                    if (placeable.getAsset().canScale()) {
                        placeable.setScale(new PosXYZ(placeable.getScale().x, newValue, placeable.getScale().z));
                    }
                }
            } catch (NumberFormatException e) {
                notify(LangManager.getItem("invalidNumber"));
                try {
                    newValue = Double.parseDouble(scaleYTextField.prevValue);
                } catch (NumberFormatException e1) {
                    LogHelper.error(getClass(), "prevValue was not a number!");
                    LogHelper.error(getClass(), e1);
                    newValue = 0;

                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                        Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                        placeable.setScale(new PosXYZ(placeable.getScale().x, newValue, placeable.getScale().z));
                    }}
            }
            //</editor-fold>

            updatePropertiesPanel();
        });
        rightListBox.addChildComponent("scaleYTextField", scaleYTextField);
        //</editor-fold>

        //<editor-fold desc="Scale Z Text Field">
        //Defined at class level
        scaleZTextField.setOnInitAction(() -> {
            scaleZTextField.setValue("0.00");
            scaleZTextField.cursorPos = scaleZTextField.value.length();
            scaleZTextField.setVerticalAlign(EnumVAlignment.centre);
            scaleZTextField.setTopLeftPos(0, 0);
            scaleZTextField.setBottomRightPos(0, 24);
            scaleZTextField.setBackgroundColor(UIColor.matBlue900());
            scaleZTextField.setInputRegexCheck("[0-9.-]");
            scaleZTextField.setEnabled(false);
        });
        scaleZTextField.setOnSelectedAction(() -> scaleZTextField.cursorPos = scaleZTextField.value.length());
        scaleZTextField.setOnTabAction(() -> scaleZTextField.setSelected(false));
        scaleZTextField.setOnReturnAction(() -> scaleZTextField.setSelected(false));
        scaleZTextField.setOnValueConfirmedAction(() -> {
            double newValue;

            //<editor-fold desc="Parse the number">
            try {
                newValue = Double.parseDouble(scaleZTextField.value);

                assert ProjectManager.getCurrentProject().clientLevelData != null;

                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                    if (placeable.getAsset().canScale()) {
                        placeable.setScale(new PosXYZ(placeable.getScale().x, placeable.getScale().y, newValue));
                    }
                }
            } catch (NumberFormatException e) {
                notify(LangManager.getItem("invalidNumber"));
                try {
                    newValue = Double.parseDouble(scaleZTextField.prevValue);
                } catch (NumberFormatException e1) {
                    LogHelper.error(getClass(), "prevValue was not a number!");
                    LogHelper.error(getClass(), e1);
                    newValue = 0;

                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

                    for (Map.Entry<String, Placeable> entry : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects().entrySet()) {
                        Placeable placeable = entry.getValue();
                        placeable.setScale(new PosXYZ(placeable.getScale().x, newValue, placeable.getScale().z));
                    }}
            }
            //</editor-fold>

            updatePropertiesPanel();
        });
        rightListBox.addChildComponent("scaleZTextField", scaleZTextField);
        //</editor-fold>

        final Label typeLabel = new Label();
        typeLabel.setOnInitAction(() -> {
            typeLabel.setText(LangManager.getItem("type"));
            typeLabel.setVerticalAlign(EnumVAlignment.centre);
            typeLabel.setTopLeftPos(0, 0);
            typeLabel.setBottomRightPos(0, 24);
        });
        rightListBox.addChildComponent("typeLabel", typeLabel);

        //Defined at class level
        typeButton.setOnInitAction(() -> {
            typeButton.setText(LangManager.getItem("noTypes"));
            typeButton.setEnabled(false);
            typeButton.setTopLeftPos(0, 0);
            typeButton.setBottomRightPos(0, 24);
        });
        typeButton.setOnLMBAction(() -> {
            assert mousePos != null;
            setOverlayUiScreen(getTypeSelectorOverlayScreen(mousePos.y));
        });
        rightListBox.addChildComponent("typeButton", typeButton);

        //Defined at class level
        notifPanel.setOnInitAction(() -> {
            notifPanel.setTopLeftPos(0, 0);
            notifPanel.setBottomRightPos(0, 0);
            notifPanel.setTopLeftAnchor(0, 0);
            notifPanel.setBottomRightAnchor(1, 1);
            notifPanel.setBackgroundColor(UIColor.transparent());
        });
        mainUI.addChildComponent("notifPanel", notifPanel);

        Window.logOpenGLError("After MainScreen.init()");

        try {
            Window.drawable.releaseContext();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void preDraw() {
        super.preDraw();

        Window.logOpenGLError("After MainScreen.super.preDraw()");

        if (ProjectManager.getCurrentProject().clientLevelData != null) {
            //<editor-fold desc="Darken selected placeables in the outliner">
            for (Map.Entry<String, Component> entry : outlinerListBox.childComponents.entrySet()) {
                assert entry.getValue() instanceof TextButton;
                TextButton button = (TextButton) entry.getValue();

                if (ProjectManager.getCurrentProject().clientLevelData.isPlaceableSelected(button.text)) {
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
        } else if (ProjectManager.getCurrentProject().clientLevelData != null) {
            if (ProjectManager.getCurrentProject().mode == EnumMode.GRAB) {
                //<editor-fold desc="Grab">

                for (String key : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(key);

                    if ((ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(1, 0, 0)) && placeable.getAsset().canGrabX()) ||
                            (ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(0, 1, 0)) && placeable.getAsset().canGrabY()) ||
                            (ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(0, 0, 1)) && placeable.getAsset().canGrabZ()) ||
                            (placeable.getAsset().canGrabX() && placeable.getAsset().canGrabY()) && placeable.getAsset().canGrabZ()) { //If can grab in selected direction
                        if (Window.isAltDown()) { //Snap with Alt
                            if (Window.isShiftDown()) {
                                deltaX += UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity;
                                deltaX += UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity;
                            } else {
                                deltaX += UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity;
                                deltaX += UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity;
                            }

                            if (deltaX >= SMBLWSettings.grabSnap || deltaX <= -SMBLWSettings.grabSnap) {
                                placeable.setPosition(placeable.getPosition().add(ProjectManager.getCurrentProject().modeDirection.multiply(
                                        SMBLWSettings.grabSnap * Math.round(deltaX / SMBLWSettings.grabSnap))));

                                deltaX = deltaX % SMBLWSettings.grabSnap;
                            }
                        } else if (Window.isShiftDown()) { //Precise movement with Shift
                            placeable.setPosition(placeable.getPosition().add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity)));
                            placeable.setPosition(placeable.getPosition().add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity)));
                        } else {
                            placeable.setPosition(placeable.getPosition().add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity)));
                            placeable.setPosition(placeable.getPosition().add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity)));
                        }
                    }
                }
                //</editor-fold>
            } else if (ProjectManager.getCurrentProject().mode == EnumMode.ROTATE) {
                //<editor-fold desc="Rotate">
                for (String key : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(key);

                    if (placeable.getAsset().canRotate()) { //If can rotate
                        if (Window.isAltDown()) { //Snap with Alt
                            if (Window.isShiftDown()) {
                                deltaX += UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity;
                                deltaX += UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity;
                            } else {
                                deltaX += UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity;
                                deltaX += UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity;
                            }

                            if (deltaX >= SMBLWSettings.rotationSnap || deltaX <= -SMBLWSettings.rotationSnap) {
                                placeable.setRotation(placeable.getRotation().add(ProjectManager.getCurrentProject().modeDirection.multiply(
                                        SMBLWSettings.rotationSnap * Math.round(deltaX / SMBLWSettings.rotationSnap))));

                                deltaX = deltaX % SMBLWSettings.rotationSnap;
                            }
                        } else if (Window.isShiftDown()) { //Precise movement with Shift
                            placeable.setRotation(normalizeRotation(placeable.getRotation()
                                    .add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity))));
                            placeable.setRotation(normalizeRotation(placeable.getRotation()
                                    .add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity))));
                        } else {
                            placeable.setRotation(normalizeRotation(placeable.getRotation()
                                    .add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity))));
                            placeable.setRotation(normalizeRotation(placeable.getRotation()
                                    .add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity))));
                        }
                    }
                }
                //</editor-fold>
            } else if (ProjectManager.getCurrentProject().mode == EnumMode.SCALE) {
                //<editor-fold desc="Scale">
                for (String key : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                    Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(key);

                    if (placeable.getAsset().canScale()) { //If can scale
                        if (Window.isAltDown()) { //Snap with Alt
                            if (Window.isShiftDown()) {
                                deltaX += UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity;
                                deltaX += UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity;
                            } else {
                                deltaX += UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity;
                                deltaX += UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity;
                            }

                            if (deltaX >= SMBLWSettings.scaleSnap || deltaX <= -SMBLWSettings.scaleSnap) {
                                placeable.setScale(placeable.getScale().add(ProjectManager.getCurrentProject().modeDirection.multiply(
                                        SMBLWSettings.scaleSnap * Math.round(deltaX / SMBLWSettings.scaleSnap))));

                                deltaX = deltaX % SMBLWSettings.scaleSnap;
                            }
                        } else if (Window.isShiftDown()) { //Precise movement with shift
                            placeable.setScale(placeable.getScale().add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity)));
                            placeable.setScale(placeable.getScale().add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity)));
                        } else {
                            placeable.setScale(placeable.getScale().add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity)));
                            placeable.setScale(placeable.getScale().add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity)));
                        }
                    }
                }
                //</editor-fold>
            }

            if (ProjectManager.getCurrentProject().mode != EnumMode.NONE) {
                updatePropertiesPanel();
            }
        }


        //<editor-fold desc="Set ProjectManager.getCurrentProject().mode Label">
        String modeStringKey;
        switch (ProjectManager.getCurrentProject().mode) {
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

        //<editor-fold desc="ProjectManager.getCurrentProject().mode Direction Label">
        String modeDirectionString;
        if (ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(1, 0, 0))) {
            modeDirectionString = LangManager.getItem("axisX");
        } else if (ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(0, 1, 0))) {
            modeDirectionString = LangManager.getItem("axisY");
        } else if (ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(0, 0, 1))) {
            modeDirectionString = LangManager.getItem("axisZ");
        } else if (ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(1, 1, 1))) {
            modeDirectionString = LangManager.getItem("axisUniform");
        } else {
            modeDirectionString = String.format("%.2f, %.2f, %.2f", ProjectManager.getCurrentProject().modeDirection.x, ProjectManager.getCurrentProject().modeDirection.y, ProjectManager.getCurrentProject().modeDirection.z);
        }

        modeDirectionLabel.setText(String.format(LangManager.getItem("modeDirectionLabelFormat"), modeDirectionString));
        //</editor-fold>

        if (ProjectManager.getCurrentProject().mode == EnumMode.NONE) {
            modeCursor.setVisible(false);
        } else {
            modeCursor.setVisible(true);
        }

        if (Mouse.isButtonDown(2) ||
                ProjectManager.getCurrentProject().mode != EnumMode.NONE) {
            if (!Mouse.isGrabbed()) {
                Mouse.setGrabbed(true);
            }
        } else {
            if (Mouse.isGrabbed()) {
                Mouse.setGrabbed(false);
            }
        }

        Window.logOpenGLError("After MainScreen.preDraw()");

    }

    @Override
    public void draw() {
        Window.logOpenGLError("Before MainScreen.draw()");

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
        if (!preventRendering) {
            drawViewport();
        }
        postDraw();

        if (overlayUiScreen != null) {
            overlayUiScreen.draw();
        }

        Window.logOpenGLError("After MainScreen.draw()");
    }

    private void drawViewport() {
        synchronized (renderingLock) {
            Window.logOpenGLError("Before MainScreen.drawViewport()");

            GL11.glEnable(GL11.GL_DEPTH_TEST);

            //<editor-fold desc="Setup the matrix">
            GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GLU.gluPerspective(90, Display.getWidth() / (float) Display.getHeight(), 0.01f, 1000f);
            //</editor-fold>

            GL11.glPushMatrix();

            Window.logOpenGLError("After MainScreen.drawViewport() - Matrix setup");

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

            Window.logOpenGLError("After MainScreen.drawViewport() - Drawing global X & Z lines");

            UIColor.pureWhite().bindColor();

            if (ProjectManager.getCurrentProject().clientLevelData != null && ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel() != null) {
                //<editor-fold desc="Draw opaque placeables">
                for (Map.Entry<String, Placeable> placeableEntry : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects().entrySet()) {
                    String name = placeableEntry.getKey();
                    Placeable placeable = placeableEntry.getValue();
                    boolean isSelected = ProjectManager.getCurrentProject().clientLevelData.isPlaceableSelected(name);

                    if (!placeable.getAsset().isOpaque()) {
                        continue;
                    }

                    drawPlaceable(placeable, isSelected);

                }
                //</editor-fold>

                //<editor-fold desc="Draw model with wireframes">
                GL11.glEnable(GL11.GL_DEPTH_TEST);

                ResourceShaderProgram currentShaderProgram = getCurrentShader();
                boolean useTextures = isCurrentShaderTextured();

                if (!SMBLWSettings.showTextures) {
                    UIColor.pureWhite().bindColor();
                }

                GL20.glUseProgram(currentShaderProgram.getProgramID());
                ResourceModel.drawModel(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel(), currentShaderProgram, useTextures);
                GL20.glUseProgram(0);

                Window.logOpenGLError("After MainScreen.drawViewport() - Drawing model filled");

                GL11.glLineWidth(2);
                if (SMBLWSettings.showAllWireframes) {
                    GL11.glColor4f(0, 0, 0, 1);
                    ResourceModel.drawModelWireframe(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel(), null, false);

                    Window.logOpenGLError("After MainScreen.drawViewport() - Drawing model wireframe (Depth test on)");

                    GL11.glDisable(GL11.GL_DEPTH_TEST);

                    GL11.glColor4f(0, 0, 0, 0.01f);
                    ResourceModel.drawModelWireframe(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel(), null, false);

                    Window.logOpenGLError("After MainScreen.drawViewport() - Drawing model wireframe (Depth test off)");
                }
                //</editor-fold>

                //<editor-fold desc="Draw placeables with transparency">
                List<DepthSortedPlaceable> depthSortedMap = new ArrayList<>();

                for (Map.Entry<String, Placeable> placeableEntry : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects().entrySet()) {
                    Placeable placeable = placeableEntry.getValue();

                    double distance;

                    if (placeable.getAsset() instanceof AssetFalloutY) {
                        distance = getDistance(cameraPos, new PosXYZ(cameraPos.x, placeable.getPosition().y, cameraPos.z));
                    } else {
                        distance = getDistance(cameraPos, placeable.getPosition());
                    }

                    depthSortedMap.add(new DepthSortedPlaceable(distance, placeableEntry));
                }

                Collections.sort(depthSortedMap, new DepthComparator());

                for (DepthSortedPlaceable placeableEntry : depthSortedMap) {
                    String name = placeableEntry.entry.getKey();
                    Placeable placeable = placeableEntry.entry.getValue();
                    boolean isSelected = ProjectManager.getCurrentProject().clientLevelData.isPlaceableSelected(name);

                    if (placeable.getAsset().isOpaque()) {
                        continue;
                    }

                    drawPlaceable(placeable, isSelected);

                }
                //</editor-fold>
            }

            GL11.glPopMatrix();

            GL11.glColor3f(1, 1, 1);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            Window.setMatrix();

            Window.logOpenGLError("After MainScreen.drawViewport()");
        }
    }

    private void drawPlaceable(Placeable placeable, boolean isSelected) {
        ResourceModel model = placeable.getAsset().getModel();

        GL11.glPushMatrix();

        GL11.glTranslated(placeable.getPosition().x, placeable.getPosition().y, placeable.getPosition().z);
        GL11.glRotated(placeable.getRotation().z, 0, 0, 1);
        GL11.glRotated(placeable.getRotation().y, 0, 1, 0);
        GL11.glRotated(placeable.getRotation().x, 1, 0, 0);

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glScaled(placeable.getScale().x, placeable.getScale().y, placeable.getScale().z);
        GL20.glUseProgram(placeable.getAsset().getShaderProgram().getProgramID());
        ResourceModel.drawModel(model, placeable.getAsset().getShaderProgram(), placeable.getAsset().isShaderTextured(), placeable.getAsset().getColor());
        GL20.glUseProgram(0);

        Window.logOpenGLError("After MainScreen.drawPlaceable() - Drawing placeable " + name + " filled");

        //<editor-fold desc="Draw blue wireframe and direction line if selected, else draw orange wireframe">
        if (isSelected) {
            if (ProjectManager.getCurrentProject().mode != EnumMode.NONE) {
                GL11.glPushMatrix();

                GL11.glRotated(-placeable.getRotation().x, 1, 0, 0);
                GL11.glRotated(-placeable.getRotation().y, 0, 1, 0);
                GL11.glRotated(-placeable.getRotation().z, 0, 0, 1);

                if (ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(1, 0, 0))) {
                    //<editor-fold desc="Draw X line">
                    UIColor.matRed(0.75).bindColor();
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex3d(-10000, 0, 0);
                    GL11.glVertex3d(10000, 0, 0);
                    GL11.glEnd();
                    //</editor-fold>
                } else if (ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(0, 1, 0))) {
                    //<editor-fold desc="Draw Y line">
                    UIColor.matGreen(0.75).bindColor();
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex3d(0, -10000, 0);
                    GL11.glVertex3d(0, 10000, 0);
                    GL11.glEnd();
                    //</editor-fold>
                } else if (ProjectManager.getCurrentProject().modeDirection.equals(new PosXYZ(0, 0, 1))) {
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

        if (SMBLWSettings.showAllWireframes || isSelected) {
            ResourceModel.drawModelWireframe(model, null, false);
        }
        //</editor-fold>

        Window.logOpenGLError("After MainScreen.drawPlaceable() - Drawing placeable " + name + " wireframe (Depth test on)");

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        //<editor-fold desc="Draw blue wireframe if selected, else draw orange wireframe (Ignores depth test - Is semi transparent)">
        if (isSelected) {
            UIColor.matBlue(0.05).bindColor();
        } else {
            UIColor.matBlue(0.02).bindColor();
        }

        if (SMBLWSettings.showAllWireframes || isSelected) {
            ResourceModel.drawModelWireframe(model, null, false);
        }
        //</editor-fold>

        Window.logOpenGLError("After MainScreen.drawPlaceable() - Drawing placeable " + name + " wireframe (Depth test off)");

        GL11.glPopMatrix();
    }

    @Override
    public void onClick(int button, PosXY mousePos) {
        if (button == 0 && ProjectManager.getCurrentProject().mode != EnumMode.NONE) { //LMB: Confirm action
            confirmModeAction();
        } else if (button == 1 && ProjectManager.getCurrentProject().mode != EnumMode.NONE) { //RMB: Discard action
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

            } else if (ProjectManager.getCurrentProject().clientLevelData != null) {
                if (ProjectManager.getCurrentProject().mode == EnumMode.NONE) {
                    if (key == Keyboard.KEY_G) { //G: Grab
                        addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));
                        ProjectManager.getCurrentProject().mode = EnumMode.GRAB;
                    } else if (key == Keyboard.KEY_R) { //R: Rotate
                        addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));
                        ProjectManager.getCurrentProject().mode = EnumMode.ROTATE;
                    } else if (key == Keyboard.KEY_S) { //S: Scale
                        addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));
                        ProjectManager.getCurrentProject().mode = EnumMode.SCALE;

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
                    } else if (key == Keyboard.KEY_Y) {
                        if (Window.isCtrlOrCmdDown()) {
                            redo();
                        }

                    } else if (key == Keyboard.KEY_DELETE) { //Delete: Remove placeables
                        if (ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables().size() > 0) {

                            List<String> toDelete = new ArrayList<>();

                            for (String name : new HashSet<>(ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables())) {
                                if (!(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name).getAsset() instanceof AssetStartPos) && //Don't delete the start pos
                                        !(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name).getAsset() instanceof AssetFalloutY)) { //Don't delete the fallout Y
                                    toDelete.add(name);
                                }
                            }

                            removePlaceables(toDelete);

                            if (toDelete.size() > 1) {
                                notify(String.format(LangManager.getItem("placeableRemovedPlural"), toDelete.size()));
                            } else {
                                notify(LangManager.getItem("placeableRemoved"));
                            }

                        } else {
                            notify(LangManager.getItem("nothingSelected"));
                        }

                    } else if (key == Keyboard.KEY_D && Window.isCtrlOrCmdDown()) { //Ctrl / Cmd D: Duplicate
                        Set<String> selectedPlaceables = new HashSet<>(ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables());
                        ProjectManager.getCurrentProject().clientLevelData.clearSelectedPlaceables();

                        Map<String, Placeable> newPlaceables = new HashMap<>();

                        int duplicated = 0;

                        for (String name : selectedPlaceables) {
                            Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                            if (!(placeable.getAsset() instanceof AssetStartPos) &&
                                    !(placeable.getAsset() instanceof AssetFalloutY)) { //If the placeable isn't the start pos or fallout Y

                                duplicated++;

                                Placeable newPlaceable = placeable.getCopy();
                                String newPlaceableName = ProjectManager.getCurrentProject().clientLevelData.getLevelData().addPlaceable(newPlaceable);
                                newPlaceables.put(newPlaceableName, newPlaceable);
                                outlinerListBox.addChildComponent(getOutlinerPlaceableComponent(newPlaceableName));

                                ProjectManager.getCurrentProject().clientLevelData.addSelectedPlaceable(newPlaceableName); //Select duplicated placeables
                            }
                        }

                        if (duplicated > 0) {
                            addUndoCommand(new UndoAddPlaceable(ProjectManager.getCurrentProject().clientLevelData, this, new ArrayList<>(newPlaceables.keySet()), new ArrayList<>(newPlaceables.values())));

                            //Grab after duplicating
                            addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));
                            ProjectManager.getCurrentProject().mode = EnumMode.GRAB;
                        }
                    } else {
                        super.onKey(key, keyChar);
                    }

                } else if (key == Keyboard.KEY_X) { //X Axis
                    ProjectManager.getCurrentProject().modeDirection = new PosXYZ(1, 0, 0);
                    modeCursor.setColor(UIColor.matRed());
                } else if (key == Keyboard.KEY_Y) { //Y Axis
                    ProjectManager.getCurrentProject().modeDirection = new PosXYZ(0, 1, 0);
                    modeCursor.setColor(UIColor.matGreen());
                } else if (key == Keyboard.KEY_Z) { //Z Axis
                    ProjectManager.getCurrentProject().modeDirection = new PosXYZ(0, 0, 1);
                    modeCursor.setColor(UIColor.matBlue());
                } else if (key == Keyboard.KEY_U) { //XYZ (Uniform)
                    ProjectManager.getCurrentProject().modeDirection = new PosXYZ(1, 1, 1);
                    modeCursor.setColor(UIColor.matWhite());

                } else if (key == Keyboard.KEY_ESCAPE) {
                    discardModeAction();
                } else if (key == Keyboard.KEY_RETURN) {
                    confirmModeAction();

                } else {
                    super.onKey(key, keyChar);
                }
            } else {
                super.onKey(key, keyChar);
            }
        }
    }

    private void confirmModeAction() {
        ProjectManager.getCurrentProject().mode = EnumMode.NONE;
        assert ProjectManager.getCurrentProject().clientLevelData != null;
        updatePropertiesPanel();
        deltaX = 0; //Reset deltaX when no ProjectManager.getCurrentProject().mode is active
    }

    private void discardModeAction() {
        ProjectManager.getCurrentProject().mode = EnumMode.NONE;
        undo();
        deltaX = 0; //Reset deltaX when no ProjectManager.getCurrentProject().mode is active
    }

    public void notify(String message) {
        notify(message, UIColor.matGrey900());
    }

    public void notify(String message, UIColor color) {
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
            panel.setBackgroundColor(color.alpha(0.75));
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
        if (ProjectManager.getCurrentProject().clientLevelData != null) {
            String name = ProjectManager.getCurrentProject().clientLevelData.getLevelData().addPlaceable(placeable);
            ProjectManager.getCurrentProject().clientLevelData.clearSelectedPlaceables();
            ProjectManager.getCurrentProject().clientLevelData.addSelectedPlaceable(name);

            addUndoCommand(new UndoAddPlaceable(ProjectManager.getCurrentProject().clientLevelData, this, Collections.singletonList(name), Collections.singletonList(placeable)));

            outlinerListBox.addChildComponent(getOutlinerPlaceableComponent(name));
        } else {
            notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
        }
    }

    private void removePlaceable(String name) {
        if (ProjectManager.getCurrentProject().clientLevelData != null) {

            addUndoCommand(new UndoRemovePlaceable(ProjectManager.getCurrentProject().clientLevelData, this, Collections.singletonList(name), Collections.singletonList(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name))));

            ProjectManager.getCurrentProject().clientLevelData.removeSelectedPlaceable(name);
            ProjectManager.getCurrentProject().clientLevelData.getLevelData().removePlaceable(name);

            outlinerListBox.removeChildComponent(name + "OutlinerPlaceable");
        } else {
            notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
        }
    }

    private void removePlaceables(List<String> names) {
        if (ProjectManager.getCurrentProject().clientLevelData != null) {

            List<Placeable> placeables = new ArrayList<>();
            for (String name : names) {
                placeables.add(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name));
            }

            assert ProjectManager.getCurrentProject().clientLevelData != null;
            addUndoCommand(new UndoRemovePlaceable(ProjectManager.getCurrentProject().clientLevelData, this, names, placeables));

            for (String name : names) {
                ProjectManager.getCurrentProject().clientLevelData.removeSelectedPlaceable(name);
                ProjectManager.getCurrentProject().clientLevelData.getLevelData().removePlaceable(name);

                outlinerListBox.removeChildComponent(name + "OutlinerPlaceable");
            }
        } else {
            notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
        }
    }

    private void newLevelData(File file, boolean replace) throws IOException {
        synchronized (renderingLock) {
            try {
                preventRendering = true;

                Window.drawable.makeCurrent();

                GL11.glFinish();

                if (!replace) {
                    //Clear outliner list box
                    outlinerListBox.clearChildComponents();

                    //Reset undo history
                    undoCommandList.clear();
                    redoCommandList.clear();
                }

                if (ProjectManager.getCurrentProject().clientLevelData != null && ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel() != null) {
                    //Unload textures and VBOs
                    for (VBO vbo : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().scene.vboList) {
                        GL11.glDeleteTextures(vbo.getTextureId());
                        vbo.destroy();
                    }
                }

                if (!replace) {
                    ProjectManager.getCurrentProject().clientLevelData = new ClientLevelData();
                    ProjectManager.getCurrentProject().clientLevelData.setOnSelectedPlaceablesChanged(this::onSelectedPlaceablesChanged);
                }

                ResourceModel model = OBJLoader.loadModel(file.getPath());
                ProjectManager.getCurrentProject().clientLevelData.getLevelData().setModel(model);
                ProjectManager.getCurrentProject().clientLevelData.getLevelData().setModelObjSource(file);

                Placeable startPosPlaceable;
                String startPosPlaceableName = null;
                Placeable falloutYPlaceable;
                String falloutYPlaceableName = null;
                if (!replace) {
                    startPosPlaceable = new Placeable(new AssetStartPos());
                    startPosPlaceable.setPosition(new PosXYZ(0, 1, 0));
                    startPosPlaceableName = ProjectManager.getCurrentProject().clientLevelData.getLevelData().addPlaceable(startPosPlaceable);
                    ProjectManager.getCurrentProject().clientLevelData.addSelectedPlaceable(startPosPlaceableName);

                    falloutYPlaceable = new Placeable(new AssetFalloutY());
                    falloutYPlaceable.setPosition(new PosXYZ(0, -10, 0));
                    falloutYPlaceableName = ProjectManager.getCurrentProject().clientLevelData.getLevelData().addPlaceable(falloutYPlaceable);

                }

                Window.drawable.makeCurrent();

                if (!replace) {
                    outlinerListBox.addChildComponent(getOutlinerPlaceableComponent(startPosPlaceableName));
                    outlinerListBox.addChildComponent(getOutlinerPlaceableComponent(falloutYPlaceableName));
                }

                if (!OBJLoader.isLastObjTriangulated) {
                    setOverlayUiScreen(new DialogOverlayUIScreen(LangManager.getItem("warning"), LangManager.getItem("notTriangulated")));
                }

                GL11.glFlush();

                Window.drawable.releaseContext();
            } catch (LWJGLException e) {
                LogHelper.error(getClass(), e);
            }

            preventRendering = false;
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

            updatePropertiesPanel();
        }

    }

    private void redo() {
        if (redoCommandList.size() > 0) {
            undoCommandList.add(redoCommandList.get(redoCommandList.size() - 1).getRedoCommand());
            redoCommandList.get(redoCommandList.size() - 1).undo();
            notify(redoCommandList.get(redoCommandList.size() - 1).getRedoMessage());
            redoCommandList.remove(redoCommandList.size() - 1);

            updatePropertiesPanel();
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
            assert ProjectManager.getCurrentProject().clientLevelData != null;

            if (Window.isShiftDown()) { //Toggle selection on shift
                ProjectManager.getCurrentProject().clientLevelData.toggleSelectedPlaceable(name);
            } else {
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedPlaceables();
                ProjectManager.getCurrentProject().clientLevelData.addSelectedPlaceable(name);
            }
        });
        placeableButton.setName(name + "OutlinerPlaceable");

        return placeableButton;
    }

    private void importObj() {
        if (!isLoadingProject) {
            new Thread(() -> {
                isLoadingProject = true;
                importObjButton.setEnabled(false);
                exportButton.setEnabled(false);
                settingsButton.setEnabled(false);
                communityButton.setEnabled(false);
                FileDialog fd = new FileDialog((Frame) null);
                fd.setMode(FileDialog.LOAD);
                fd.setFilenameFilter((dir, filename) -> filename.toUpperCase().endsWith(".OBJ"));
                fd.setVisible(true);

                File[] files = fd.getFiles();
                if (files != null && files.length > 0) {
                    File file = files[0];
                    LogHelper.info(getClass(), "Opening file: " + file.getAbsolutePath());

                    try {
                        if (ProjectManager.getCurrentProject().clientLevelData != null) {
                            AskReplaceObjOverlayUIScreen dialog = new AskReplaceObjOverlayUIScreen();
                            setOverlayUiScreen(dialog);
                            boolean shouldRepalce = dialog.waitForShouldReplaceResponse();
                            newLevelData(file, shouldRepalce);
                        } else {
                            newLevelData(file, false);
                        }
                    } catch (IOException e) {
                        LogHelper.error(getClass(), "Failed to open file");
                        LogHelper.error(getClass(), e);
                    }
                }
                communityButton.setEnabled(true);
                settingsButton.setEnabled(true);
                exportButton.setEnabled(true);
                importObjButton.setEnabled(true);
                isLoadingProject = false;
            }, "ObjFileOpenThread").start();
        } else {
            LogHelper.warn(getClass(), "Tried importing OBJ when already importing OBJ");
        }
    }

    private void export() {
        if (!isLoadingProject) {
            if (ProjectManager.getCurrentProject().clientLevelData != null) {
                setOverlayUiScreen(new ExportOverlayUIScreen());
            } else {
                notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }
        }
    }

    private void onSelectedPlaceablesChanged() {
        updatePropertiesPanel();
    }

    private void updatePropertiesPanel() {
        DecimalFormat df = new DecimalFormat("0.00");

        double posAvgX = 0;
        boolean canGrabX = false;
        double posAvgY = 0;
        boolean canGrabY = false;
        double posAvgZ = 0;
        boolean canGrabZ = false;

        double rotAvgX = 0;
        double rotAvgY = 0;
        double rotAvgZ = 0;
        boolean canRotate = false;

        double sclAvgX = 0;
        double sclAvgY = 0;
        double sclAvgZ = 0;
        boolean canScale = false;

        assert ProjectManager.getCurrentProject().clientLevelData != null;

        Class<?> selectedIAsset;
        if (ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables().size() > 0) {
            selectedIAsset = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables().iterator().next()).getAsset().getClass();
        } else {
            selectedIAsset = null;
        }

        for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
            Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);

            if (placeable.getAsset().canGrabX()) {
                canGrabX = true;
                posAvgX += placeable.getPosition().x;
            }
            if (placeable.getAsset().canGrabY()) {
                canGrabY = true;
                posAvgY += placeable.getPosition().y;
            }
            if (placeable.getAsset().canGrabZ()) {
                canGrabZ = true;
                posAvgZ += placeable.getPosition().z;
            }

            if (placeable.getAsset().canRotate()) {
                canRotate = true;

                rotAvgX += placeable.getRotation().x;
                rotAvgY += placeable.getRotation().y;
                rotAvgZ += placeable.getRotation().z;
            }

            if (placeable.getAsset().canScale()) {
                canScale = true;

                sclAvgX += placeable.getScale().x;
                sclAvgY += placeable.getScale().y;
                sclAvgZ += placeable.getScale().z;
            } else {
                sclAvgX += 1;
                sclAvgY += 1;
                sclAvgZ += 1;
            }

            if (selectedIAsset != null && !placeable.getAsset().getClass().isAssignableFrom(selectedIAsset)) {
                selectedIAsset = null;
            }
        }

        int selectedCount = ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables().size();

        if (selectedCount != 0) {
            posAvgX = posAvgX / (double) selectedCount;
            posAvgY = posAvgY / (double) selectedCount;
            posAvgZ = posAvgZ / (double) selectedCount;

            if (canGrabX) {
                positionXTextField.setEnabled(true);
            } else {
                positionXTextField.setEnabled(false);
                positionXTextField.setValue("0.00");
            }
            if (canGrabY) {
                positionYTextField.setEnabled(true);
            } else {
                positionYTextField.setEnabled(false);
                positionYTextField.setValue("0.00");
            }
            if (canGrabZ) {
                positionZTextField.setEnabled(true);
            } else {
                positionZTextField.setEnabled(false);
                positionZTextField.setValue("0.00");
            }

            positionXTextField.setValue(df.format(posAvgX));
            positionYTextField.setValue(df.format(posAvgY));
            positionZTextField.setValue(df.format(posAvgZ));
        } else {
            positionXTextField.setEnabled(false);
            positionYTextField.setEnabled(false);
            positionZTextField.setEnabled(false);

            positionXTextField.setValue("0.00");
            positionYTextField.setValue("0.00");
            positionZTextField.setValue("0.00");
        }

        if (selectedCount != 0 && canRotate) {
            rotAvgX = rotAvgX / (double) selectedCount;
            rotAvgY = rotAvgY / (double) selectedCount;
            rotAvgZ = rotAvgZ / (double) selectedCount;

            rotationXTextField.setEnabled(true);
            rotationYTextField.setEnabled(true);
            rotationZTextField.setEnabled(true);

            rotationXTextField.setValue(df.format(rotAvgX));
            rotationYTextField.setValue(df.format(rotAvgY));
            rotationZTextField.setValue(df.format(rotAvgZ));
        } else {
            rotationXTextField.setEnabled(false);
            rotationYTextField.setEnabled(false);
            rotationZTextField.setEnabled(false);

            rotationXTextField.setValue("0.00");
            rotationYTextField.setValue("0.00");
            rotationZTextField.setValue("0.00");
        }

        if (selectedCount != 0 && canScale) {
            sclAvgX = sclAvgX / (double) selectedCount;
            sclAvgY = sclAvgY / (double) selectedCount;
            sclAvgZ = sclAvgZ / (double) selectedCount;

            scaleXTextField.setEnabled(true);
            scaleYTextField.setEnabled(true);
            scaleZTextField.setEnabled(true);

            scaleXTextField.setValue(df.format(sclAvgX));
            scaleYTextField.setValue(df.format(sclAvgY));
            scaleZTextField.setValue(df.format(sclAvgZ));
        } else {
            scaleXTextField.setEnabled(false);
            scaleYTextField.setEnabled(false);
            scaleZTextField.setEnabled(false);

            scaleXTextField.setValue("1.00");
            scaleYTextField.setValue("1.00");
            scaleZTextField.setValue("1.00");
        }

        if (selectedIAsset != null) {
            String[] types = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables().iterator().next()).getAsset().getValidTypes();

            if (types != null) {
                typeList = Arrays.asList(types);
                typeButton.setText(LangManager.getItem(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables().iterator().next()).getAsset().getType()));
                typeButton.setEnabled(true);
            } else {
                typeList = null;
                typeButton.setText(LangManager.getItem("noTypes"));
                typeButton.setEnabled(false);
            }
        } else {
            typeList = null;
            typeButton.setText(LangManager.getItem("noTypes"));
            typeButton.setEnabled(false);
        }

    }

    private PosXYZ normalizeRotation(PosXYZ rot) {
        while (rot.x >= 360) {
            rot.x -= 360;
        }

        while (rot.y >= 360) {
            rot.y -= 360;
        }

        while (rot.z >= 360) {
            rot.z -= 360;
        }

        while (rot.x < 0) {
            rot.x += 360;
        }

        while (rot.y < 0) {
            rot.y += 360;
        }

        while (rot.z < 0) {
            rot.z += 360;
        }

        return rot;
    }

    private IUIScreen getTypeSelectorOverlayScreen(double mouseY) {
        final double mousePercentY = mouseY / Display.getHeight();

        return new TypeSelectorOverlayScreen(mousePercentY, typeList);
    }

    public void setTypeForSelectedPlaceables(String type) {
        boolean changed = false;

        assert ProjectManager.getCurrentProject().clientLevelData != null;
        for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
            Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);

            if (!Objects.equals(placeable.getAsset().getType(), type)) {
                changed = true;
            }
        }

        if (changed) {
            addUndoCommand(new UndoAssetTypeChange(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));
        }

        for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
            Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);

            placeable.getAsset().setType(type);
        }

        updatePropertiesPanel();
    }

    private void showSettings() {
        setOverlayUiScreen(new SettingsOverlayUIScreen());
    }

    private void showCommunity() {
        ProjectManager.getCurrentProject().mainScreen = this;
        Window.setUIScreen(new CommunityScreen());
    }

    private ResourceShaderProgram getCurrentShader() {
        if (SMBLWSettings.showTextures) {
            if (SMBLWSettings.isUnlit) {
                return ResourceManager.getShaderProgram("texUnlitShaderProgram");
            } else {
                return ResourceManager.getShaderProgram("texShaderProgram");
            }
        } else {
            if (SMBLWSettings.isUnlit) {
                return ResourceManager.getShaderProgram("colUnlitShaderProgram");
            } else {
                return ResourceManager.getShaderProgram("colShaderProgram");
            }
        }
    }

    private boolean isCurrentShaderTextured() {
        return SMBLWSettings.showTextures;
    }

    private double getDistance(PosXYZ p1, PosXYZ p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2) + Math.pow(p2.z - p1.z, 2));
    }

}
