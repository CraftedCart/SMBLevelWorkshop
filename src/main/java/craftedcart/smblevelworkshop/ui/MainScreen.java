package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.SMBLWSettings;
import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.asset.*;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.level.LevelData;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.ResourceShaderProgram;
import craftedcart.smblevelworkshop.resource.model.OBJLoader;
import craftedcart.smblevelworkshop.resource.model.OBJObject;
import craftedcart.smblevelworkshop.resource.model.ResourceModel;
import craftedcart.smblevelworkshop.ui.component.OutlinerObject;
import craftedcart.smblevelworkshop.ui.component.PositionTextFields;
import craftedcart.smblevelworkshop.ui.component.RotationTextFields;
import craftedcart.smblevelworkshop.ui.component.ScaleTextFields;
import craftedcart.smblevelworkshop.undo.*;
import craftedcart.smblevelworkshop.util.*;
import craftedcart.smblevelworkshop.util.LogHelper;
import craftedcart.smblevelworkshop.util.MathUtils;
import craftedcart.smbworkshopexporter.ConfigData;
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
    private final Panel notifPanel = new Panel();

    private EnumObjectMode objectMode = EnumObjectMode.PLACEABLE_EDIT;

    //UI: Left Panel
    public final Panel addPlaceablePanel = new Panel();
    public final Panel outlinerPlaceablesPanel = new Panel();
    public final ListBox outlinerPlaceablesListBox = new ListBox();
    public final Panel outlinerObjectsPanel = new Panel();
    public final ListBox outlinerObjectsListBox = new ListBox();

    private final TextButton importObjButton = new TextButton();
    private final TextButton importConfigButton = new TextButton();
    private final TextButton exportButton = new TextButton();
    private final TextButton settingsButton = new TextButton();

    //UI: Placeable Properties
    private final ListBox propertiesPlaceablesListBox = new ListBox();
    private final ListBox propertiesObjectsListBox = new ListBox();

    private final PositionTextFields scaleTextFields = new PositionTextFields(this, null);
    private final RotationTextFields rotationTextFields = new RotationTextFields(this, scaleTextFields.getFirstTextField());
    private final ScaleTextFields positionTextFields = new ScaleTextFields(this, rotationTextFields.getFirstTextField());

    private final TextButton typeButton = new TextButton();
    @Nullable private List<String> typeList = null;

    //UI: Object Properties
    private final CheckBox backgroundObjectCheckBox = new CheckBox();

    //UI: Text Fields
    private final Set<TextField> textFields = new HashSet<>();

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
    private final Object outlinerPlaceablesListBoxLock = new Object();
    private final Object outlinerObjectsListBoxLock = new Object();
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

        //<editor-fold desc="Placeables / Objects mode buttons">
        final TextButton outlinerPlaceablesTabButton = new TextButton();
        final TextButton outlinerObjectsTabButton = new TextButton();

        //Defined above
        outlinerPlaceablesTabButton.setOnInitAction(() -> {
            outlinerPlaceablesTabButton.setTopLeftPos(0, 0);
            outlinerPlaceablesTabButton.setBottomRightPos(-1, 24);
            outlinerPlaceablesTabButton.setTopLeftAnchor(0, 0);
            outlinerPlaceablesTabButton.setBottomRightAnchor(0.5, 0);
            outlinerPlaceablesTabButton.setText(LangManager.getItem("placeables"));
            outlinerPlaceablesTabButton.setBackgroundIdleColor(UIColor.matBlue900());
        });
        outlinerPlaceablesTabButton.setOnLMBAction(() -> {
            outlinerPlaceablesTabButton.setBackgroundIdleColor(UIColor.matBlue900());
            outlinerObjectsTabButton.setBackgroundIdleColor(UIColor.matBlue());

            addPlaceablePanel.setVisible(true);
            outlinerPlaceablesPanel.setVisible(true);
            outlinerObjectsPanel.setVisible(false);

            propertiesPlaceablesListBox.setVisible(true);
            propertiesObjectsListBox.setVisible(false);

            if (ProjectManager.getCurrentProject() != null && ProjectManager.getCurrentProject().clientLevelData != null) {
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedObjects();
            }

            objectMode = EnumObjectMode.PLACEABLE_EDIT;
        });
        leftPanel.addChildComponent("outlinerPlaceablesTabButton", outlinerPlaceablesTabButton);

        //Defined above
        outlinerObjectsTabButton.setOnInitAction(() -> {
            outlinerObjectsTabButton.setTopLeftPos(1, 0);
            outlinerObjectsTabButton.setBottomRightPos(0, 24);
            outlinerObjectsTabButton.setTopLeftAnchor(0.5, 0);
            outlinerObjectsTabButton.setBottomRightAnchor(1, 0);
            outlinerObjectsTabButton.setText(LangManager.getItem("objects"));
            outlinerObjectsTabButton.setBackgroundIdleColor(UIColor.matBlue());
        });
        outlinerObjectsTabButton.setOnLMBAction(() -> {
            outlinerPlaceablesTabButton.setBackgroundIdleColor(UIColor.matBlue());
            outlinerObjectsTabButton.setBackgroundIdleColor(UIColor.matBlue900());

            addPlaceablePanel.setVisible(false);
            outlinerPlaceablesPanel.setVisible(false);
            outlinerObjectsPanel.setVisible(true);

            propertiesPlaceablesListBox.setVisible(false);
            propertiesObjectsListBox.setVisible(true);

            if (ProjectManager.getCurrentProject() != null && ProjectManager.getCurrentProject().clientLevelData != null) {
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedPlaceables();
            }

            objectMode = EnumObjectMode.OBJECT_EDIT;
        });
        leftPanel.addChildComponent("outlinerObjectsTabButton", outlinerObjectsTabButton);
        //</editor-fold>

        //Defined at class level
        addPlaceablePanel.setOnInitAction(() -> {
            addPlaceablePanel.setTopLeftPos(0, 24);
            addPlaceablePanel.setBottomRightPos(0, 0);
            addPlaceablePanel.setTopLeftAnchor(0, 0);
            addPlaceablePanel.setBottomRightAnchor(1, 0.25);
            addPlaceablePanel.setBackgroundColor(UIColor.transparent());
        });
        leftPanel.addChildComponent("addPlaceablePanel", addPlaceablePanel);

        final Label addPlaceableLabel = new Label();
        addPlaceableLabel.setOnInitAction(() -> {
            addPlaceableLabel.setText(LangManager.getItem("addPlaceable"));
            addPlaceableLabel.setHorizontalAlign(EnumHAlignment.centre);
            addPlaceableLabel.setVerticalAlign(EnumVAlignment.centre);
            addPlaceableLabel.setTopLeftPos(4, 0);
            addPlaceableLabel.setBottomRightPos(-4, 24);
            addPlaceableLabel.setTopLeftAnchor(0, 0);
            addPlaceableLabel.setBottomRightAnchor(1, 0);
        });
        addPlaceablePanel.addChildComponent("addPlaceableLabel", addPlaceableLabel);

        final ListBox addPlaceableListBox = new ListBox();
        addPlaceableListBox.setOnInitAction(() -> {
            addPlaceableListBox.setBackgroundColor(UIColor.transparent());
            addPlaceableListBox.setTopLeftPos(0, 24);
            addPlaceableListBox.setBottomRightPos(0, 0);
            addPlaceableListBox.setTopLeftAnchor(0, 0);
            addPlaceableListBox.setBottomRightAnchor(1, 1);
        });
        addPlaceablePanel.addChildComponent("addPlaceableListBox", addPlaceableListBox);

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

        //<editor-fold desc="Outliner placeables panel">
        //Defined at class level
        outlinerPlaceablesPanel.setOnInitAction(() -> {
            outlinerPlaceablesPanel.setTopLeftPos(0, 0);
            outlinerPlaceablesPanel.setBottomRightPos(0, 0);
            outlinerPlaceablesPanel.setTopLeftAnchor(0, 0.25);
            outlinerPlaceablesPanel.setBottomRightAnchor(1, 1);
            outlinerPlaceablesPanel.setBackgroundColor(UIColor.transparent());
        });
        leftPanel.addChildComponent("outlinerPlaceablesPanel", outlinerPlaceablesPanel);

        final Label outlinerPlaceablesLabel = new Label();
        outlinerPlaceablesLabel.setOnInitAction(() -> {
            outlinerPlaceablesLabel.setText(LangManager.getItem("outliner"));
            outlinerPlaceablesLabel.setHorizontalAlign(EnumHAlignment.centre);
            outlinerPlaceablesLabel.setVerticalAlign(EnumVAlignment.centre);
            outlinerPlaceablesLabel.setTopLeftPos(4, 0);
            outlinerPlaceablesLabel.setBottomRightPos(-4, 24);
            outlinerPlaceablesLabel.setTopLeftAnchor(0, 0);
            outlinerPlaceablesLabel.setBottomRightAnchor(1, 0);
        });
        outlinerPlaceablesPanel.addChildComponent("outlinerPlaceablesLabel", outlinerPlaceablesLabel);

        //Defined at class level
        outlinerPlaceablesListBox.setOnInitAction(() -> {
            outlinerPlaceablesListBox.setBackgroundColor(UIColor.transparent());
            outlinerPlaceablesListBox.setTopLeftPos(0, 24);
            outlinerPlaceablesListBox.setBottomRightPos(0, 0);
            outlinerPlaceablesListBox.setTopLeftAnchor(0, 0);
            outlinerPlaceablesListBox.setBottomRightAnchor(1, 1);
        });
        outlinerPlaceablesPanel.addChildComponent("outlinerPlaceablesListBox", outlinerPlaceablesListBox);
        //</editor-fold>

        //<editor-fold desc="Outliner objects panel">
        //Defined at class level
        outlinerObjectsPanel.setOnInitAction(() -> {
            outlinerObjectsPanel.setTopLeftPos(0, 24);
            outlinerObjectsPanel.setBottomRightPos(0, 0);
            outlinerObjectsPanel.setTopLeftAnchor(0, 0);
            outlinerObjectsPanel.setBottomRightAnchor(1, 1);
            outlinerObjectsPanel.setBackgroundColor(UIColor.transparent());
            outlinerObjectsPanel.setVisible(false); //Hidden by default
        });
        leftPanel.addChildComponent("outlinerObjectsPanel", outlinerObjectsPanel);

        final Label outlinerObjectsLabel = new Label();
        outlinerObjectsLabel.setOnInitAction(() -> {
            outlinerObjectsLabel.setText(LangManager.getItem("outliner"));
            outlinerObjectsLabel.setHorizontalAlign(EnumHAlignment.centre);
            outlinerObjectsLabel.setVerticalAlign(EnumVAlignment.centre);
            outlinerObjectsLabel.setTopLeftPos(4, 0);
            outlinerObjectsLabel.setBottomRightPos(-4, 24);
            outlinerObjectsLabel.setTopLeftAnchor(0, 0);
            outlinerObjectsLabel.setBottomRightAnchor(1, 0);
        });
        outlinerObjectsPanel.addChildComponent("outlinerObjectsLabel", outlinerObjectsLabel);

        //Defined at class level
        outlinerObjectsListBox.setOnInitAction(() -> {
            outlinerObjectsListBox.setBackgroundColor(UIColor.transparent());
            outlinerObjectsListBox.setTopLeftPos(0, 24);
//            outlinerObjectsListBox.setBottomRightPos(0, -24); //TODO: When external backgrounds are figured out, uncomment this
            outlinerObjectsListBox.setBottomRightPos(0, 0);
            outlinerObjectsListBox.setTopLeftAnchor(0, 0);
            outlinerObjectsListBox.setBottomRightAnchor(1, 1);
        });
        outlinerObjectsPanel.addChildComponent("outlinerObjectsListBox", outlinerObjectsListBox);
        //</editor-fold>

//        final TextField addExternalBackgroundObjectTextField = new TextField();
//        addExternalBackgroundObjectTextField.setOnInitAction(() -> {
//            addExternalBackgroundObjectTextField.setTopLeftPos(0, -24);
//            addExternalBackgroundObjectTextField.setBottomRightPos(0, 0);
//            addExternalBackgroundObjectTextField.setTopLeftAnchor(0, 1);
//            addExternalBackgroundObjectTextField.setBottomRightAnchor(1, 1);
//            addExternalBackgroundObjectTextField.setPlaceholder(LangManager.getItem("addExternalBackgroundObject"));
//            addExternalBackgroundObjectTextField.setBackgroundColor(UIColor.transparent());
//        });
//        addExternalBackgroundObjectTextField.setOnReturnAction(() -> {
//            if (ProjectManager.getCurrentProject() != null && ProjectManager.getCurrentProject().clientLevelData != null) {
//                if (!Objects.equals(addExternalBackgroundObjectTextField.value, "")) {
//                    if (!ProjectManager.getCurrentProject().clientLevelData.getLevelData().isObjectBackgroundExternal(addExternalBackgroundObjectTextField.text) &&
//                            !ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().hasObject(addExternalBackgroundObjectTextField.text)) {
//                        ProjectManager.getCurrentProject().clientLevelData.getLevelData().addBackgroundExternalObject(addExternalBackgroundObjectTextField.text);
//
//                        outlinerObjectsListBox.addChildComponent(getOutlinerExternalBackgroundObjectComponent(addExternalBackgroundObjectTextField.text));
//                    } else {
//                        notify(LangManager.getItem("alreadyObject"), UIColor.matRed());
//                    }
//
//                    addExternalBackgroundObjectTextField.setValue(""); //Clear text field
//                } else {
//                    //Text field is blank
//                    notify(LangManager.getItem("noObjectSpecified"), UIColor.matRed());
//                }
//            } else {
//                notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
//            }
//        });
//        outlinerObjectsPanel.addChildComponent("addExternalBackgroundObjectTextField", addExternalBackgroundObjectTextField);
//        textFields.add(addExternalBackgroundObjectTextField);

        final Panel rightPanel = new Panel();
        rightPanel.setOnInitAction(() -> {
            rightPanel.setBackgroundColor(UIColor.matGrey900(0.75));
            rightPanel.setTopLeftPos(-256, 0);
            rightPanel.setBottomRightPos(0, 0);
            rightPanel.setTopLeftAnchor(1, 0);
            rightPanel.setBottomRightAnchor(1, 1);
        });
        mainUI.addChildComponent("rightPanel", rightPanel);

        final ListBox actionsListBox = new ListBox();
        actionsListBox.setOnInitAction(() -> {
            actionsListBox.setTopLeftPos(0, 0);
            actionsListBox.setBottomRightPos(0, 104);
            actionsListBox.setTopLeftAnchor(0, 0);
            actionsListBox.setBottomRightAnchor(1, 0);
            actionsListBox.setBackgroundColor(UIColor.transparent());
        });
        rightPanel.addChildComponent("actionsListBox", actionsListBox);

        //<editor-fold desc="ImportObj TextButton">
        //Defined at class level
        importObjButton.setOnInitAction(() -> {
            importObjButton.setText(LangManager.getItem("importObj"));
            importObjButton.setTopLeftPos(0, 0);
            importObjButton.setBottomRightPos(0, 24);
        });
        importObjButton.setOnLMBAction(this::importObj);
        actionsListBox.addChildComponent("importObjButton", importObjButton);
        //</editor-fold>

        //<editor-fold desc="ImportConfig TextButton">
        //Defined at class level
        importConfigButton.setOnInitAction(() -> {
            importConfigButton.setText(LangManager.getItem("importConfig"));
            importConfigButton.setTopLeftPos(0, 0);
            importConfigButton.setBottomRightPos(0, 24);
        });
        importConfigButton.setOnLMBAction(this::importConfig);
        actionsListBox.addChildComponent("importConfigButton", importConfigButton);
        //</editor-fold>

        //<editor-fold desc="Export TextButton">
        //Defined at class level
        exportButton.setOnInitAction(() -> {
            exportButton.setText(LangManager.getItem("export"));
            exportButton.setTopLeftPos(0, 0);
            exportButton.setBottomRightPos(0, 24);
        });
        exportButton.setOnLMBAction(this::export);
        actionsListBox.addChildComponent("exportButton", exportButton);
        //</editor-fold>

        //<editor-fold desc="Settings TextButton">
        //Defined at class level
        settingsButton.setOnInitAction(() -> {
            settingsButton.setText(LangManager.getItem("settings"));
            settingsButton.setTopLeftPos(0, 0);
            settingsButton.setBottomRightPos(0, 24);
        });
        settingsButton.setOnLMBAction(this::showSettings);
        actionsListBox.addChildComponent("settingsButton", settingsButton);
        //</editor-fold>

        //<editor-fold desc="Placeable Properties">
        //Defined at class level
        propertiesPlaceablesListBox.setOnInitAction(() -> {
            propertiesPlaceablesListBox.setTopLeftPos(0, 104);
            propertiesPlaceablesListBox.setBottomRightPos(0, 0);
            propertiesPlaceablesListBox.setTopLeftAnchor(0, 0);
            propertiesPlaceablesListBox.setBottomRightAnchor(1, 1);
            propertiesPlaceablesListBox.setBackgroundColor(UIColor.transparent());
        });
        rightPanel.addChildComponent("propertiesPlaceablesListBox", propertiesPlaceablesListBox);

        final Label placeablesPropertiesLabel = new Label();
        placeablesPropertiesLabel.setOnInitAction(() -> {
            placeablesPropertiesLabel.setText(LangManager.getItem("properties"));
            placeablesPropertiesLabel.setHorizontalAlign(EnumHAlignment.centre);
            placeablesPropertiesLabel.setVerticalAlign(EnumVAlignment.centre);
            placeablesPropertiesLabel.setTopLeftPos(0, 0);
            placeablesPropertiesLabel.setBottomRightPos(0, 28);
            placeablesPropertiesLabel.setTopLeftAnchor(0, 0);
            placeablesPropertiesLabel.setBottomRightAnchor(1, 0);
        });
        propertiesPlaceablesListBox.addChildComponent("placeablesPropertiesLabel", placeablesPropertiesLabel);

        final Label positionLabel = new Label();
        positionLabel.setOnInitAction(() -> {
            positionLabel.setText(LangManager.getItem("position"));
            positionLabel.setVerticalAlign(EnumVAlignment.centre);
            positionLabel.setTopLeftPos(0, 0);
            positionLabel.setBottomRightPos(0, 24);
        });
        propertiesPlaceablesListBox.addChildComponent("positionLabel", positionLabel);

        //Defined at class level
        positionTextFields.setOnInitAction(() -> {
            positionTextFields.setTopLeftPos(0, 0);
            positionTextFields.setBottomRightPos(0, 76);
        });
        propertiesPlaceablesListBox.addChildComponent("positionTextFields", positionTextFields);

        final Label rotationLabel = new Label();
        rotationLabel.setOnInitAction(() -> {
            rotationLabel.setText(LangManager.getItem("rotation"));
            rotationLabel.setVerticalAlign(EnumVAlignment.centre);
            rotationLabel.setTopLeftPos(0, 0);
            rotationLabel.setBottomRightPos(0, 24);
        });
        propertiesPlaceablesListBox.addChildComponent("rotationLabel", rotationLabel);

        //Defined at class level
        rotationTextFields.setOnInitAction(() -> {
            rotationTextFields.setTopLeftPos(0, 0);
            rotationTextFields.setBottomRightPos(0, 76);
        });
        propertiesPlaceablesListBox.addChildComponent("rotationTextFields", rotationTextFields);

        final Label scaleLabel = new Label();
        scaleLabel.setOnInitAction(() -> {
            scaleLabel.setText(LangManager.getItem("scale"));
            scaleLabel.setVerticalAlign(EnumVAlignment.centre);
            scaleLabel.setTopLeftPos(0, 0);
            scaleLabel.setBottomRightPos(0, 24);
        });
        propertiesPlaceablesListBox.addChildComponent("scaleLabel", scaleLabel);

        //Defined at class level
        scaleTextFields.setOnInitAction(() -> {
            scaleTextFields.setTopLeftPos(0, 0);
            scaleTextFields.setBottomRightPos(0, 76);
        });
        propertiesPlaceablesListBox.addChildComponent("scaleTextFields", scaleTextFields);

        final Label typeLabel = new Label();
        typeLabel.setOnInitAction(() -> {
            typeLabel.setText(LangManager.getItem("type"));
            typeLabel.setVerticalAlign(EnumVAlignment.centre);
            typeLabel.setTopLeftPos(0, 0);
            typeLabel.setBottomRightPos(0, 24);
        });
        propertiesPlaceablesListBox.addChildComponent("typeLabel", typeLabel);

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
        propertiesPlaceablesListBox.addChildComponent("typeButton", typeButton);
        //</editor-fold>

        //<editor-fold desc="Object Properties">
        //Defined at class level
        propertiesObjectsListBox.setOnInitAction(() -> {
            propertiesObjectsListBox.setTopLeftPos(0, 104);
            propertiesObjectsListBox.setBottomRightPos(0, 0);
            propertiesObjectsListBox.setTopLeftAnchor(0, 0);
            propertiesObjectsListBox.setBottomRightAnchor(1, 1);
            propertiesObjectsListBox.setBackgroundColor(UIColor.transparent());
            propertiesObjectsListBox.setVisible(false);
        });
        rightPanel.addChildComponent("propertiesObjectsListBox", propertiesObjectsListBox);

        final Label objectsPropertiesLabel = new Label();
        objectsPropertiesLabel.setOnInitAction(() -> {
            objectsPropertiesLabel.setText(LangManager.getItem("properties"));
            objectsPropertiesLabel.setHorizontalAlign(EnumHAlignment.centre);
            objectsPropertiesLabel.setVerticalAlign(EnumVAlignment.centre);
            objectsPropertiesLabel.setTopLeftPos(0, 0);
            objectsPropertiesLabel.setBottomRightPos(0, 28);
            objectsPropertiesLabel.setTopLeftAnchor(0, 0);
            objectsPropertiesLabel.setBottomRightAnchor(1, 0);
        });
        propertiesObjectsListBox.addChildComponent("objectsPropertiesLabel", objectsPropertiesLabel);

        final Panel backgroundObjectPanel = new Panel();
        backgroundObjectPanel.setOnInitAction(() -> {
            backgroundObjectPanel.setTopLeftPos(0, 0);
            backgroundObjectPanel.setBottomRightPos(0, 24);
            backgroundObjectPanel.setBackgroundColor(UIColor.transparent());
        });
        propertiesObjectsListBox.addChildComponent("backgroundObjectPanel", backgroundObjectPanel);

        final Label backgroundObjectLabel = new Label();
        backgroundObjectLabel.setOnInitAction(() -> {
            backgroundObjectLabel.setTopLeftPos(4, 0);
            backgroundObjectLabel.setBottomRightPos(-24, 0);
            backgroundObjectLabel.setTopLeftAnchor(0, 0);
            backgroundObjectLabel.setBottomRightAnchor(1, 1);
            backgroundObjectLabel.setText(LangManager.getItem("backgroundObject"));
        });
        backgroundObjectPanel.addChildComponent("backgroundObjectLabel", backgroundObjectLabel);

        //Defined at class level
        backgroundObjectCheckBox.setOnInitAction(() -> {
            backgroundObjectCheckBox.setTopLeftPos(-24, 0);
            backgroundObjectCheckBox.setBottomRightPos(0, 0);
            backgroundObjectCheckBox.setTopLeftAnchor(1, 0);
            backgroundObjectCheckBox.setBottomRightAnchor(1, 1);
            backgroundObjectCheckBox.setEnabled(false);
            backgroundObjectCheckBox.setTexture(ResourceManager.getTexture("image/checkBoxTick").getTexture());
        });
        backgroundObjectCheckBox.setOnLMBAction(() -> {
            if (ProjectManager.getCurrentProject() != null && ProjectManager.getCurrentProject().clientLevelData != null) {
                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                    if (backgroundObjectCheckBox.getValue()) {
                        ProjectManager.getCurrentProject().clientLevelData.getLevelData().addBackgroundObject(name);
                    } else {
                        ProjectManager.getCurrentProject().clientLevelData.getLevelData().removeBackgroundObject(name);
                    }

                    updateOutlinerObjectsPanel();
                }
            } else {
                notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed()); //You really shouldn't be able to toggle the checkbox when no level is loaded, but just in case
            }
        });
        backgroundObjectPanel.addChildComponent("backgroundObjectCheckBox", backgroundObjectCheckBox);
        //</editor-fold>

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
            if (ProjectManager.getCurrentProject().mode == EnumActionMode.GRAB) {
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
            } else if (ProjectManager.getCurrentProject().mode == EnumActionMode.ROTATE) {
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
                            placeable.setRotation(MathUtils.normalizeRotation(placeable.getRotation()
                                    .add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseShiftSensitivity))));
                            placeable.setRotation(MathUtils.normalizeRotation(placeable.getRotation()
                                    .add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelShiftSensitivity))));
                        } else {
                            placeable.setRotation(MathUtils.normalizeRotation(placeable.getRotation()
                                    .add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDelta().x * SMBLWSettings.modeMouseSensitivity))));
                            placeable.setRotation(MathUtils.normalizeRotation(placeable.getRotation()
                                    .add(ProjectManager.getCurrentProject().modeDirection.multiply(UIUtils.getMouseDWheel() * SMBLWSettings.modeMouseWheelSensitivity))));
                        }
                    }
                }
                //</editor-fold>
            } else if (ProjectManager.getCurrentProject().mode == EnumActionMode.SCALE) {
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

            if (ProjectManager.getCurrentProject().mode != EnumActionMode.NONE) {
                updatePropertiesPlaceablesPanel();
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

        if (ProjectManager.getCurrentProject().mode == EnumActionMode.NONE) {
            modeCursor.setVisible(false);
        } else {
            modeCursor.setVisible(true);
        }

        if (Mouse.isButtonDown(2) ||
                ProjectManager.getCurrentProject().mode != EnumActionMode.NONE) {
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

            GL11.glLineWidth(2);

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

            GL11.glLineWidth(4);

            UIColor.pureWhite().bindColor();

            if (ProjectManager.getCurrentProject().clientLevelData != null && ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel() != null) {
                //<editor-fold desc="Draw model with wireframes">
                GL11.glEnable(GL11.GL_DEPTH_TEST);

                ResourceShaderProgram currentShaderProgram = getCurrentShader();
                boolean useTextures = isCurrentShaderTextured();

                if (!SMBLWSettings.showTextures) {
                    UIColor.pureWhite().bindColor();
                }

                GL20.glUseProgram(currentShaderProgram.getProgramID());
                for (OBJObject object : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().scene.getObjectList()) {
                    if (!ProjectManager.getCurrentProject().clientLevelData.isObjectHidden(object.name)) {
                        ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().drawModelObject(currentShaderProgram, useTextures, object.name);
                    }
                }
                GL20.glUseProgram(0);

                Window.logOpenGLError("After MainScreen.drawViewport() - Drawing model filled");

                if (SMBLWSettings.showAllWireframes) {
                    GL11.glColor4f(0, 0, 0, 1);
                    ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().drawModelWireframe(null, false);

                    Window.logOpenGLError("After MainScreen.drawViewport() - Drawing model wireframe (Depth test on)");

                    GL11.glDisable(GL11.GL_DEPTH_TEST);

                    GL11.glColor4f(0, 0, 0, 0.01f);
                    ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().drawModelWireframe(null, false);

                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                    Window.logOpenGLError("After MainScreen.drawViewport() - Drawing model wireframe (Depth test off)");
                }
                //</editor-fold>

                //<editor-fold desc="Draw placeables">
                List<DepthSortedPlaceable> depthSortedMap = new ArrayList<>();

                synchronized (ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects()) {

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

                }

                Collections.sort(depthSortedMap, new DepthComparator());

                for (DepthSortedPlaceable placeableEntry : depthSortedMap) {
                    String name = placeableEntry.entry.getKey();
                    Placeable placeable = placeableEntry.entry.getValue();
                    boolean isSelected = ProjectManager.getCurrentProject().clientLevelData.isPlaceableSelected(name);

                    drawPlaceable(placeable, isSelected);

                }
                //</editor-fold>

                //<editor-fold desc="Draw selected stuff">
                drawSelectedPlaceables(ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables(),
                        ProjectManager.getCurrentProject().clientLevelData.getLevelData());

                //<editor-fold desc="Draw selected objects">
                UIColor.matBlue().bindColor();
                UIUtils.drawWithStencilOutside(
                        () -> {
                            for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                                ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().drawModelObject(null, false, name);
                            }
                        },
                        () -> {
                            GL11.glDisable(GL11.GL_DEPTH_TEST);
                            for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                                ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().drawModelObjectWireframe(null, false, name);
                            }
                            GL11.glEnable(GL11.GL_DEPTH_TEST);
                        });

                Window.logOpenGLError("After MainScreen.drawViewport() - Drawing model selection wireframe (Depth test on)");

//                GL11.glDisable(GL11.GL_DEPTH_TEST);
//                UIColor.matBlue(0.05).bindColor();
//                for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
//                    ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().drawModelObjectWireframe(null, false, name);
//                }
//                GL11.glEnable(GL11.GL_DEPTH_TEST);

                Window.logOpenGLError("After MainScreen.drawViewport() - Drawing model selection wireframe (Depth test off)");
                //</editor-fold>
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
        model.drawModel(placeable.getAsset().getShaderProgram(), placeable.getAsset().isShaderTextured(), placeable.getAsset().getColor());
        GL20.glUseProgram(0);

        Window.logOpenGLError("After MainScreen.drawPlaceable() - Drawing placeable " + name + " filled");

        //<editor-fold desc="Draw blue wireframe and direction line if selected, else draw orange wireframe">
        GL11.glLineWidth(2);

        if (isSelected) {
            if (ProjectManager.getCurrentProject().mode != EnumActionMode.NONE) {
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

        if (SMBLWSettings.showAllWireframes) {
            model.drawModelWireframe(null, false);
        }
        //</editor-fold>

        Window.logOpenGLError("After MainScreen.drawPlaceable() - Drawing placeable " + name + " wireframe (Depth test on)");

        GL11.glDisable(GL11.GL_DEPTH_TEST);

//        //<editor-fold desc="Draw blue wireframe if selected, else draw orange wireframe (Ignores depth test - Is semi transparent)">
//        if (isSelected) {
//            UIColor.matBlue(0.05).bindColor();
//        } else {
//            UIColor.matOrange(0.02).bindColor();
//        }
//
//        if (SMBLWSettings.showAllWireframes || isSelected) {
//            model.drawModelWireframe(null, false);
//        }
//        //</editor-fold>

        Window.logOpenGLError("After MainScreen.drawPlaceable() - Drawing placeable " + name + " wireframe (Depth test off)");

        GL11.glPopMatrix();
    }

    private void drawSelectedPlaceables(Collection<String> placeables, LevelData levelData) {
        UIColor.matBlue().bindColor();
        UIUtils.drawWithStencilOutside(
                () -> {
                    for (String name : placeables) {
                        Placeable placeable = levelData.getPlaceable(name);

                        GL11.glPushMatrix();

                        GL11.glTranslated(placeable.getPosition().x, placeable.getPosition().y, placeable.getPosition().z);
                        GL11.glRotated(placeable.getRotation().z, 0, 0, 1);
                        GL11.glRotated(placeable.getRotation().y, 0, 1, 0);
                        GL11.glRotated(placeable.getRotation().x, 1, 0, 0);
                        GL11.glScaled(placeable.getScale().x, placeable.getScale().y, placeable.getScale().z);

                        placeable.getAsset().getModel().drawModel(null, false);

                        GL11.glPopMatrix();
                    }
                },
                () -> {
                    GL11.glLineWidth(4);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    for (String name : placeables) {
                        Placeable placeable = levelData.getPlaceable(name);

                        GL11.glPushMatrix();

                        GL11.glTranslated(placeable.getPosition().x, placeable.getPosition().y, placeable.getPosition().z);
                        GL11.glRotated(placeable.getRotation().z, 0, 0, 1);
                        GL11.glRotated(placeable.getRotation().y, 0, 1, 0);
                        GL11.glRotated(placeable.getRotation().x, 1, 0, 0);
                        GL11.glScaled(placeable.getScale().x, placeable.getScale().y, placeable.getScale().z);

                        placeable.getAsset().getModel().drawModelWireframe(null, false);

                        GL11.glPopMatrix();
                    }
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                });
    }

    @Override
    public void onClick(int button, PosXY mousePos) {
        if (button == 0 && ProjectManager.getCurrentProject().mode != EnumActionMode.NONE) { //LMB: Confirm action
            confirmModeAction();
        } else if (button == 1 && ProjectManager.getCurrentProject().mode != EnumActionMode.NONE) { //RMB: Discard action
            discardModeAction();
        } else {
            super.onClick(button, mousePos);
        }
    }

    @Override
    public void onKey(int key, char keyChar) {

        if (overlayUiScreen != null) {
            overlayUiScreen.onKey(key, keyChar);
        } else {

            boolean isTextFieldSelected = false;
            for (TextField textField : textFields) {
                if (textField.isSelected) {
                    isTextFieldSelected = true;
                    break;
                }
            }

            if (!isTextFieldSelected) {
                if (!Mouse.isButtonDown(2)) { //If MMB not down
                    if (key == Keyboard.KEY_F1) { //F1 to hide / show the ui
                        mainUI.setVisible(!mainUI.isVisible());

                    } else if (ProjectManager.getCurrentProject().clientLevelData != null) {
                        if (ProjectManager.getCurrentProject().mode == EnumActionMode.NONE) {
                            if (key == Keyboard.KEY_G) { //G: Grab
                                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));
                                ProjectManager.getCurrentProject().mode = EnumActionMode.GRAB;
                            } else if (key == Keyboard.KEY_R) { //R: Rotate
                                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));
                                ProjectManager.getCurrentProject().mode = EnumActionMode.ROTATE;
                            } else if (key == Keyboard.KEY_S) { //S: Scale
                                addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));
                                ProjectManager.getCurrentProject().mode = EnumActionMode.SCALE;

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

                                        synchronized (outlinerPlaceablesListBoxLock) {
                                            outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(newPlaceableName));
                                        }

                                        updateOutlinerPlaceablesPanel();

                                        ProjectManager.getCurrentProject().clientLevelData.addSelectedPlaceable(newPlaceableName); //Select duplicated placeables
                                    }
                                }

                                if (duplicated > 0) {
                                    addUndoCommand(new UndoAddPlaceable(ProjectManager.getCurrentProject().clientLevelData, this, new ArrayList<>(newPlaceables.keySet()), new ArrayList<>(newPlaceables.values())));

                                    //Grab after duplicating
                                    addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData, ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));
                                    ProjectManager.getCurrentProject().mode = EnumActionMode.GRAB;
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
            } else {
                super.onKey(key, keyChar);

                if (key == Keyboard.KEY_ESCAPE) { //Deselect text fields on escape
                    for (TextField textField : textFields) {
                        textField.setSelected(false);
                    }
                }
            }
        }

    }

    private void confirmModeAction() {
        ProjectManager.getCurrentProject().mode = EnumActionMode.NONE;
        assert ProjectManager.getCurrentProject().clientLevelData != null;
        updatePropertiesPlaceablesPanel();
        deltaX = 0; //Reset deltaX when no ProjectManager.getCurrentProject().mode is active
    }

    private void discardModeAction() {
        ProjectManager.getCurrentProject().mode = EnumActionMode.NONE;
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

            synchronized (outlinerPlaceablesListBoxLock) {
                outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(name));
            }

            updateOutlinerPlaceablesPanel();
        } else {
            notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
        }
    }

    private void removePlaceable(String name) {
        if (ProjectManager.getCurrentProject().clientLevelData != null) {

            addUndoCommand(new UndoRemovePlaceable(ProjectManager.getCurrentProject().clientLevelData, this, Collections.singletonList(name), Collections.singletonList(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name))));

            ProjectManager.getCurrentProject().clientLevelData.removeSelectedPlaceable(name);
            ProjectManager.getCurrentProject().clientLevelData.getLevelData().removePlaceable(name);

            synchronized (outlinerPlaceablesListBoxLock) {
                outlinerPlaceablesListBox.removeChildComponent(name + "OutlinerPlaceable");
            }
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

                synchronized (outlinerPlaceablesListBoxLock) {
                    outlinerPlaceablesListBox.removeChildComponent(name + "OutlinerPlaceable");
                }
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
                    synchronized (outlinerPlaceablesListBoxLock) {
                        outlinerPlaceablesListBox.clearChildComponents();
                    }

                    //Reset undo history
                    undoCommandList.clear();
                    redoCommandList.clear();
                }

                if (ProjectManager.getCurrentProject().clientLevelData != null && ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel() != null) {
                    //Unload textures and VBOs
                    ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().scene.unloadAll();
                }

                if (!replace) {
                    ProjectManager.getCurrentProject().clientLevelData = new ClientLevelData();
                    ProjectManager.getCurrentProject().clientLevelData.setOnSelectedPlaceablesChanged(this::onSelectedPlaceablesChanged);
                    ProjectManager.getCurrentProject().clientLevelData.setOnSelectedObjectsChanged(this::onSelectedObjectsChanged);
                    ProjectManager.getCurrentProject().clientLevelData.setOnSelectedExternalBackgroundObjectsChanged(this::onSelectedExternalBackgroundObjectsChanged);
                }

                ResourceModel model = OBJLoader.loadModel(file.getPath());
                ProjectManager.getCurrentProject().clientLevelData.getLevelData().setModel(model);
                ProjectManager.getCurrentProject().clientLevelData.getLevelData().setModelObjSource(file);

                synchronized (ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects()) {

                    Placeable startPosPlaceable;
                    String startPosPlaceableName = null;
                    Placeable falloutYPlaceable;
                    String falloutYPlaceableName = null;
                    if (!replace) {
                        startPosPlaceable = new Placeable(new AssetStartPos());
                        startPosPlaceable.setPosition(new PosXYZ(0, 1, 0));
                        startPosPlaceableName = ProjectManager.getCurrentProject().clientLevelData.getLevelData().addPlaceable(startPosPlaceable);

                        if (objectMode == EnumObjectMode.PLACEABLE_EDIT) {
                            ProjectManager.getCurrentProject().clientLevelData.addSelectedPlaceable(startPosPlaceableName);
                        }

                        falloutYPlaceable = new Placeable(new AssetFalloutY());
                        falloutYPlaceable.setPosition(new PosXYZ(0, -10, 0));
                        falloutYPlaceableName = ProjectManager.getCurrentProject().clientLevelData.getLevelData().addPlaceable(falloutYPlaceable);

                    }

                    Window.drawable.makeCurrent();

                    if (!replace) {
                        synchronized (outlinerPlaceablesListBoxLock) {
                            outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(startPosPlaceableName));
                            outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(falloutYPlaceableName));
                        }

                        updateOutlinerPlaceablesPanel();
                    }

                }

                if (replace) {
                    //Remove selected objects if they no longer exist in the new OBJ
                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                        if (!ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().hasObject(name)) {
                            ProjectManager.getCurrentProject().clientLevelData.removeSelectedObject(name);
                        }
                    }

                    //Remove background objects if they no longer exist in the new OBJ
                    synchronized (ProjectManager.getCurrentProject().clientLevelData.getLevelData().getBackgroundObjects()) {
                        for (String name : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getBackgroundObjects()) {
                            if (!ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().hasObject(name)) {
                                ProjectManager.getCurrentProject().clientLevelData.getLevelData().removeBackgroundObject(name);
                            }
                        }
                    }
                } else {
                    ProjectManager.getCurrentProject().clientLevelData.clearSelectedObjects();
                }

                if (!OBJLoader.isLastObjTriangulated) {
                    setOverlayUiScreen(new DialogOverlayUIScreen(LangManager.getItem("warning"), LangManager.getItem("notTriangulated")));
                }

                synchronized (outlinerObjectsListBoxLock) {
                    outlinerObjectsListBox.clearChildComponents();

                    ProjectManager.getCurrentProject().clientLevelData.clearHiddenObjects(); //Unhide all objects

                    for (OBJObject object : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModel().scene.getObjectList()) {
                        outlinerObjectsListBox.addChildComponent(getOutlinerObjectComponent(object.name));
                    }
                }

                if (replace) {
                    //Replace all external background objects in the objects outliner that were removed
                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getLevelData().getBackgroundExternalObjects()) {
                        outlinerObjectsListBox.addChildComponent(getOutlinerExternalBackgroundObjectComponent(name));
                    }
                }

                GL11.glFlush();

                Window.drawable.releaseContext();
            } catch (LWJGLException e) {
                LogHelper.error(getClass(), e);
            }

            preventRendering = false;
        }
    }

    public void addUndoCommand(UndoCommand undoCommand) {
        undoCommandList.add(undoCommand);
        redoCommandList.clear();
    }

    private void undo() {
        if (undoCommandList.size() > 0) {
            redoCommandList.add(undoCommandList.get(undoCommandList.size() - 1).getRedoCommand());
            undoCommandList.get(undoCommandList.size() - 1).undo();
            notify(undoCommandList.get(undoCommandList.size() - 1).getUndoMessage());
            undoCommandList.remove(undoCommandList.size() - 1);

            updatePropertiesPlaceablesPanel();
        }

    }

    private void redo() {
        if (redoCommandList.size() > 0) {
            undoCommandList.add(redoCommandList.get(redoCommandList.size() - 1).getRedoCommand());
            redoCommandList.get(redoCommandList.size() - 1).undo();
            notify(redoCommandList.get(redoCommandList.size() - 1).getRedoMessage());
            redoCommandList.remove(redoCommandList.size() - 1);

            updatePropertiesPlaceablesPanel();
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
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedExternalBackgroundObjects();
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedPlaceables();
                ProjectManager.getCurrentProject().clientLevelData.addSelectedPlaceable(name);
            }
        });
        placeableButton.setName(name + "OutlinerPlaceable");

        return placeableButton;
    }

    public Component getOutlinerObjectComponent(String name) {
        final OutlinerObject outlinerObject = new OutlinerObject(name);
        outlinerObject.setOnInitAction(() -> {
            outlinerObject.setTopLeftPos(0, 0);
            outlinerObject.setBottomRightPos(0, 18);
        });

        return outlinerObject;
    }

    public Component getOutlinerExternalBackgroundObjectComponent(String name) {
        final TextButton objectButton = new TextButton();
        objectButton.setOnInitAction(() -> {
            objectButton.setTopLeftPos(0, 0);
            objectButton.setBottomRightPos(0, 18);
            objectButton.setText(name);
            objectButton.setBackgroundIdleColor(UIColor.matPink());
        });
        objectButton.setOnLMBAction(() -> {
            assert ProjectManager.getCurrentProject().clientLevelData != null;

            if (Window.isShiftDown()) { //Toggle selection on shift
                ProjectManager.getCurrentProject().clientLevelData.toggleSelectedExternalBackgroundObject(name);
            } else {
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedExternalBackgroundObjects();
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedObjects();
                ProjectManager.getCurrentProject().clientLevelData.addSelectedExternalBackgroundObject(name);
            }
        });
        objectButton.setName(name + "OutlinerObject");

        return objectButton;
    }

    private void importObj() {
        if (!isLoadingProject) {
            new Thread(() -> {
                isLoadingProject = true;
                importObjButton.setEnabled(false);
                importConfigButton.setEnabled(false);
                exportButton.setEnabled(false);
                settingsButton.setEnabled(false);
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

                        updateOutlinerObjectsPanel();
                    } catch (IOException e) {
                        LogHelper.error(getClass(), "Failed to open file");
                        LogHelper.error(getClass(), e);
                    }
                }
                settingsButton.setEnabled(true);
                exportButton.setEnabled(true);
                importConfigButton.setEnabled(true);
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
        updatePropertiesPlaceablesPanel();
        updateOutlinerPlaceablesPanel();
    }

    public void updatePropertiesPlaceablesPanel() {
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
                positionTextFields.setXEnabled(true);
            } else {
                positionTextFields.setXEnabled(false);
                positionTextFields.setXValue(0);
            }
            if (canGrabY) {
                positionTextFields.setYEnabled(true);
            } else {
                positionTextFields.setYEnabled(false);
                positionTextFields.setYValue(0);
            }
            if (canGrabZ) {
                positionTextFields.setZEnabled(true);
            } else {
                positionTextFields.setZEnabled(false);
                positionTextFields.setZValue(0);
            }

            positionTextFields.setXValue(posAvgX);
            positionTextFields.setYValue(posAvgY);
            positionTextFields.setZValue(posAvgZ);
        } else {
            positionTextFields.setXEnabled(false);
            positionTextFields.setYEnabled(false);
            positionTextFields.setZEnabled(false);

            positionTextFields.setXValue(0);
            positionTextFields.setYValue(0);
            positionTextFields.setZValue(0);
        }

        if (selectedCount != 0 && canRotate) {
            rotAvgX = rotAvgX / (double) selectedCount;
            rotAvgY = rotAvgY / (double) selectedCount;
            rotAvgZ = rotAvgZ / (double) selectedCount;

            rotationTextFields.setXEnabled(true);
            rotationTextFields.setYEnabled(true);
            rotationTextFields.setZEnabled(true);

            rotationTextFields.setXValue(rotAvgX);
            rotationTextFields.setYValue(rotAvgY);
            rotationTextFields.setZValue(rotAvgZ);
        } else {
            rotationTextFields.setXEnabled(false);
            rotationTextFields.setYEnabled(false);
            rotationTextFields.setZEnabled(false);

            rotationTextFields.setXValue(0);
            rotationTextFields.setYValue(0);
            rotationTextFields.setZValue(0);
        }

        if (selectedCount != 0 && canScale) {
            sclAvgX = sclAvgX / (double) selectedCount;
            sclAvgY = sclAvgY / (double) selectedCount;
            sclAvgZ = sclAvgZ / (double) selectedCount;

            scaleTextFields.setXEnabled(true);
            scaleTextFields.setYEnabled(true);
            scaleTextFields.setZEnabled(true);

            scaleTextFields.setXValue(sclAvgX);
            scaleTextFields.setYValue(sclAvgY);
            scaleTextFields.setZValue(sclAvgZ);
        } else {
            scaleTextFields.setXEnabled(false);
            scaleTextFields.setYEnabled(false);
            scaleTextFields.setZEnabled(false);

            scaleTextFields.setXValue(1);
            scaleTextFields.setYValue(1);
            scaleTextFields.setZValue(1);
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

    private void onSelectedObjectsChanged() {
        updatePropertiesObjectsPanel();
        updateOutlinerObjectsPanel();
    }

    private void updatePropertiesObjectsPanel() {
        if (ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects().size() > 0) {

            //Enable UI components
            backgroundObjectCheckBox.setEnabled(true);

            boolean areBackgroundObjects = true;

            for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                if (!ProjectManager.getCurrentProject().clientLevelData.getLevelData().isObjectBackground(name)) {
                    //At least one selected object isn't marked as in the background
                    areBackgroundObjects = false;

                    break;
                }
            }

            backgroundObjectCheckBox.setValue(areBackgroundObjects);

        } else {
            backgroundObjectCheckBox.setEnabled(false);
        }
    }

    private void updateOutlinerPlaceablesPanel() {
        if (ProjectManager.getCurrentProject().clientLevelData != null) {
            //<editor-fold desc="Darken selected placeables in the outliner">
            synchronized (outlinerPlaceablesListBoxLock) {
                for (Map.Entry<String, Component> entry : outlinerPlaceablesListBox.childComponents.entrySet()) {
                    assert entry.getValue() instanceof TextButton;
                    TextButton button = (TextButton) entry.getValue();

                    //TODO: Using button.text feels hacky - Extend TextButton and add id field
                    if (ProjectManager.getCurrentProject().clientLevelData.isPlaceableSelected(button.text)) {
                        button.setBackgroundIdleColor(UIColor.matBlue900());
                    } else {
                        button.setBackgroundIdleColor(UIColor.matBlue());
                    }
                }
            }
            //</editor-fold>
        }
    }

    private void updateOutlinerObjectsPanel() {
        //<editor-fold desc="Darken selected object in the outliner">
        synchronized (outlinerObjectsListBoxLock) {
            for (Map.Entry<String, Component> entry : outlinerObjectsListBox.childComponents.entrySet()) {
                assert entry.getValue() instanceof OutlinerObject;
                OutlinerObject outlinerObject = (OutlinerObject) entry.getValue();

                outlinerObject.setButtonColor(getOutlinerObjectColor(outlinerObject.getObjectName()));
            }
        }
        //</editor-fold>
    }

    private void onSelectedExternalBackgroundObjectsChanged() {
        updatePropertiesObjectsPanel();
        updateOutlinerObjectsPanel();
    }

    private UIColor getOutlinerObjectColor(String name) {
        if (ProjectManager.getCurrentProject().clientLevelData.getLevelData().isObjectBackground(name)) {
            if (ProjectManager.getCurrentProject().clientLevelData.isObjectSelected(name)) {
                return UIColor.matPurple900();
            } else {
                return UIColor.matPurple();
            }
        } else if (ProjectManager.getCurrentProject().clientLevelData.getLevelData().isObjectBackgroundExternal(name)) {
            if (ProjectManager.getCurrentProject().clientLevelData.isExternalBackgroundObjectSelected(name)) {
                return UIColor.matPink900();
            } else {
                return UIColor.matPink();
            }
        } else {
            if (ProjectManager.getCurrentProject().clientLevelData.isObjectSelected(name)) {
                return UIColor.matBlue900();
            } else {
                return UIColor.matBlue();
            }
        }
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

        updatePropertiesPlaceablesPanel();
    }

    private void showSettings() {
        setOverlayUiScreen(new SettingsOverlayUIScreen());
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

    private void importConfig() {
        if (ProjectManager.getCurrentProject() != null && ProjectManager.getCurrentProject().clientLevelData != null) {
            if (!isLoadingProject) {
                new Thread(() -> {
                    isLoadingProject = true;
                    importObjButton.setEnabled(false);
                    importConfigButton.setEnabled(false);
                    exportButton.setEnabled(false);
                    settingsButton.setEnabled(false);
                    FileDialog fd = new FileDialog((Frame) null);
                    fd.setMode(FileDialog.LOAD);
                    fd.setFilenameFilter((dir, filename) -> filename.toUpperCase().endsWith(".TXT"));
                    fd.setVisible(true);

                    File[] files = fd.getFiles();
                    if (files != null && files.length > 0) {
                        File file = files[0];
                        LogHelper.info(getClass(), "Opening file: " + file.getAbsolutePath());

                        try {
                            ConfigData configData = new ConfigData();
                            configData.parseConfig(file);

                            ClientLevelData cld = ProjectManager.getCurrentProject().clientLevelData;
                            LevelData ld = cld.getLevelData();

                            synchronized (ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlacedObjects()) {

                                cld.clearSelectedPlaceables();
                                ld.clearPlacedObjects();
                                outlinerPlaceablesListBox.clearChildComponents();

                                //Add start pos
                                if (configData.startList.size() > 0) {
                                    ConfigData.Start start = configData.startList.entrySet().iterator().next().getValue();
                                    Placeable startPlaceable = new Placeable(new AssetStartPos());
                                    startPlaceable.setPosition(new PosXYZ(start.posX, start.posY, start.posZ));
                                    startPlaceable.setRotation(new PosXYZ(start.rotX, start.rotY, start.rotZ));
                                    String name = ld.addPlaceable(startPlaceable);
                                    outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(name));
                                } else {
                                    //No start found - use default start
                                    Placeable startPlaceable = new Placeable(new AssetStartPos());
                                    startPlaceable.setPosition(new PosXYZ(0, 1, 0));
                                    String name = ld.addPlaceable(startPlaceable);
                                    outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(name));
                                }

                                //Add fallout y
                                Placeable falloutPlaceable = new Placeable(new AssetFalloutY());
                                falloutPlaceable.setPosition(new PosXYZ(0, configData.falloutPlane, 0));
                                String falloutName = ld.addPlaceable(falloutPlaceable);
                                outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(falloutName));

                                //Add goals
                                for (Map.Entry<String, ConfigData.Goal> entry : configData.goalList.entrySet()) {
                                    ConfigData.Goal goal = entry.getValue();
                                    Placeable goalPlaceable = new Placeable(new AssetGoal());
                                    goalPlaceable.setPosition(new PosXYZ(goal.posX, goal.posY, goal.posZ));
                                    goalPlaceable.setRotation(new PosXYZ(goal.rotX, goal.rotY, goal.rotZ));

                                    String type = "blueGoal";
                                    //Type 0 = blueGoal
                                    if (goal.type == 1) {
                                        type = "greenGoal";
                                    } else if (goal.type == 2) {
                                        type = "redGoal";
                                    }

                                    goalPlaceable.getAsset().setType(type);
                                    String name = ld.addPlaceable(goalPlaceable);
                                    outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(name));
                                }

                                //Add bumpers
                                for (Map.Entry<String, ConfigData.Bumper> entry : configData.bumperList.entrySet()) {
                                    ConfigData.Bumper bumper = entry.getValue();
                                    Placeable bumperPlaceable = new Placeable(new AssetBumper());
                                    bumperPlaceable.setPosition(new PosXYZ(bumper.posX, bumper.posY, bumper.posZ));
                                    bumperPlaceable.setRotation(new PosXYZ(bumper.rotX, bumper.rotY, bumper.rotZ));
                                    bumperPlaceable.setScale(new PosXYZ(bumper.sclX, bumper.sclY, bumper.sclZ));

                                    String name = ld.addPlaceable(bumperPlaceable);
                                    outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(name));
                                }

                                //Add jamabars
                                for (Map.Entry<String, ConfigData.Jamabar> entry : configData.jamabarList.entrySet()) {
                                    ConfigData.Jamabar jamabar = entry.getValue();
                                    Placeable jamabarPlaceable = new Placeable(new AssetJamabar());
                                    jamabarPlaceable.setPosition(new PosXYZ(jamabar.posX, jamabar.posY, jamabar.posZ));
                                    jamabarPlaceable.setRotation(new PosXYZ(jamabar.rotX, jamabar.rotY, jamabar.rotZ));
                                    jamabarPlaceable.setScale(new PosXYZ(jamabar.sclX, jamabar.sclY, jamabar.sclZ));

                                    String name = ld.addPlaceable(jamabarPlaceable);
                                    outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(name));
                                }

                                //Add bananas
                                for (Map.Entry<String, ConfigData.Banana> entry : configData.bananaList.entrySet()) {
                                    ConfigData.Banana banana = entry.getValue();
                                    Placeable bananaPlaceable = new Placeable(new AssetBanana());
                                    bananaPlaceable.setPosition(new PosXYZ(banana.posX, banana.posY, banana.posZ));

                                    String type = "singleBanana";
                                    //Type 0 = singleBanana
                                    if (banana.type == 1) {
                                        type = "bunchBanana";
                                    }

                                    bananaPlaceable.getAsset().setType(type);
                                    String name = ld.addPlaceable(bananaPlaceable);
                                    outlinerPlaceablesListBox.addChildComponent(getOutlinerPlaceableComponent(name));
                                }

                                //TODO: Backgrounds

                            }

                            updateOutlinerPlaceablesPanel();
                        } catch (IOException e) {
                            LogHelper.error(getClass(), "Failed to open file");
                            LogHelper.error(getClass(), e);
                        }
                    }
                    settingsButton.setEnabled(true);
                    exportButton.setEnabled(true);
                    importConfigButton.setEnabled(true);
                    importObjButton.setEnabled(true);
                    isLoadingProject = false;
                }, "ObjFileOpenThread").start();
            } else {
                LogHelper.warn(getClass(), "Tried importing OBJ when already importing OBJ");
            }
        } else {
            notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
        }
    }

}
