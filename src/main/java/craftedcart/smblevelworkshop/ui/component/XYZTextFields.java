package craftedcart.smblevelworkshop.ui.component;

import craftedcart.smblevelworkshop.asset.Placeable;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.DefaultUITheme;
import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.undo.UndoAssetTransform;
import craftedcart.smblevelworkshop.util.EnumAxis;
import craftedcart.smblevelworkshop.util.PosXYZ;
import io.github.craftedcart.fluidui.component.ListBox;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.component.TextField;
import io.github.craftedcart.fluidui.util.EnumVAlignment;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public abstract class XYZTextFields extends Panel {

    @NotNull private MainScreen mainScreen;
    @Nullable private TextField nextTextField;

    private ListBox listBox;

    private TextField xTextField;
    private TextField yTextField;
    private TextField zTextField;

    private final DecimalFormat df = new DecimalFormat("0.00");


    public XYZTextFields(@NotNull MainScreen mainScreen, @Nullable TextField nextTextField) {
        this.mainScreen = mainScreen;
        this.nextTextField = nextTextField;

        init();
        postInit();
    }

    public void init() {
        super.init();

        setTheme(new DefaultUITheme());

        listBox = new ListBox();
        xTextField = new TextField();
        yTextField = new TextField();
        zTextField = new TextField();

        setBackgroundColor(UIColor.transparent());
        listBox.setUseStencil(false);

        listBox.setOnInitAction(() -> {
            listBox.setTopLeftPos(0, 0);
            listBox.setBottomRightPos(0, 0);
            listBox.setTopLeftAnchor(0, 0);
            listBox.setBottomRightAnchor(1, 1);
            listBox.setBackgroundColor(UIColor.transparent());
            listBox.setCanScroll(false);
            listBox.scrollbarThickness = 0;
        });
        addChildComponent("listBox", listBox);

        //<editor-fold desc="Position X Text Field">
        //Defined at class level
        xTextField.setOnInitAction(() -> {
            xTextField.setValue("0.00");
            xTextField.cursorPos = xTextField.value.length();
            xTextField.setVerticalAlign(EnumVAlignment.centre);
            xTextField.setTopLeftPos(0, 0);
            xTextField.setBottomRightPos(0, 24);
            xTextField.setBackgroundColor(UIColor.matRed900());
            xTextField.setInputRegexCheck("[0-9.-]");
            xTextField.setEnabled(false);
        });
        xTextField.setOnSelectedAction(() -> xTextField.cursorPos = xTextField.value.length());
        xTextField.setOnTabAction(() -> {
            xTextField.setSelected(false);
            yTextField.setSelected(true);
        });
        xTextField.setOnReturnAction(() -> xTextField.setSelected(false));
        xTextField.setOnValueConfirmedAction(() -> {
            List<Placeable> placeables = new ArrayList<>();
            Double newValue = parseNumberAndAddUndoCommandAndPopulatePlaceablesList(xTextField.value, placeables);
            if (newValue != null) {
                valueChanged(new PosXYZ(newValue, 0, 0), EnumAxis.X, placeables);
            }

            mainScreen.updatePropertiesPlaceablesPanel();
        });
        listBox.addChildComponent("xTextField", xTextField);
        //</editor-fold>

        //<editor-fold desc="Position Y Text Field">
        //Defined at class level
        yTextField.setOnInitAction(() -> {
            yTextField.setValue("0.00");
            yTextField.cursorPos = yTextField.value.length();
            yTextField.setVerticalAlign(EnumVAlignment.centre);
            yTextField.setTopLeftPos(0, 0);
            yTextField.setBottomRightPos(0, 24);
            yTextField.setBackgroundColor(UIColor.matGreen900());
            yTextField.setInputRegexCheck("[0-9.-]");
            yTextField.setEnabled(false);
        });
        yTextField.setOnSelectedAction(() -> yTextField.cursorPos = yTextField.value.length());
        yTextField.setOnTabAction(() -> {
            yTextField.setSelected(false);
            yTextField.setSelected(true);
        });
        yTextField.setOnReturnAction(() -> yTextField.setSelected(false));
        yTextField.setOnValueConfirmedAction(() -> {
            List<Placeable> placeables = new ArrayList<>();
            Double newValue = parseNumberAndAddUndoCommandAndPopulatePlaceablesList(yTextField.value, placeables);
            if (newValue != null) {
                valueChanged(new PosXYZ(0, newValue, 0), EnumAxis.Y, placeables);
            }

            mainScreen.updatePropertiesPlaceablesPanel();
        });
        listBox.addChildComponent("yTextField", yTextField);
        //</editor-fold>

        //<editor-fold desc="Position Z Text Field">
        //Defined at class level
        zTextField.setOnInitAction(() -> {
            zTextField.setValue("0.00");
            zTextField.cursorPos = zTextField.value.length();
            zTextField.setVerticalAlign(EnumVAlignment.centre);
            zTextField.setTopLeftPos(0, 0);
            zTextField.setBottomRightPos(0, 24);
            zTextField.setBackgroundColor(UIColor.matBlue900());
            zTextField.setInputRegexCheck("[0-9.-]");
            zTextField.setEnabled(false);
        });
        zTextField.setOnSelectedAction(() -> zTextField.cursorPos = zTextField.value.length());
        zTextField.setOnTabAction(() -> {
            zTextField.setSelected(false);
            if (nextTextField != null) {
                nextTextField.setSelected(true);
            }
        });
        zTextField.setOnReturnAction(() -> zTextField.setSelected(false));
        zTextField.setOnValueConfirmedAction(() -> {
            List<Placeable> placeables = new ArrayList<>();
            Double newValue = parseNumberAndAddUndoCommandAndPopulatePlaceablesList(zTextField.value, placeables);
            if (newValue != null) {
                valueChanged(new PosXYZ(0, 0, newValue), EnumAxis.Z, placeables);
            }

            mainScreen.updatePropertiesPlaceablesPanel();
        });
        listBox.addChildComponent("zTextField", zTextField);
        //</editor-fold>
    }

    @Nullable
    private Double parseNumberAndAddUndoCommandAndPopulatePlaceablesList(String value, List<Placeable> placeablesToPopulate) {
        double newValue;

        try {
            newValue = Double.parseDouble(value);

            assert ProjectManager.getCurrentProject().clientLevelData != null;

            mainScreen.addUndoCommand(new UndoAssetTransform(ProjectManager.getCurrentProject().clientLevelData,
                    ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()));

            for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedPlaceables()) {
                Placeable placeable = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getPlaceable(name);
                placeablesToPopulate.add(placeable);
            }

            return newValue;
        } catch (NumberFormatException e) {
            mainScreen.notify(LangManager.getItem("invalidNumber"), UIColor.matRed());
        }

        return null; //Failed to parse the number
    }

    public void setXEnabled(boolean enabled) {
        xTextField.setEnabled(enabled);
    }

    public void setYEnabled(boolean enabled) {
        yTextField.setEnabled(enabled);
    }

    public void setZEnabled(boolean enabled) {
        zTextField.setEnabled(enabled);
    }

    public void setXValue(double value) {
        xTextField.setValue(df.format(value));
    }

    public void setYValue(double value) {
        yTextField.setValue(df.format(value));
    }

    public void setZValue(double value) {
        zTextField.setValue(df.format(value));
    }

    public TextField getFirstTextField() {
        return xTextField;
    }

    public abstract void valueChanged(PosXYZ newValue, EnumAxis axis, List<Placeable> placeables);

}
