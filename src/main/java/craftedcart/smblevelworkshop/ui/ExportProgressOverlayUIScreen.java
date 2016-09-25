package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.resource.LangManager;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimatePanelBackgroundColor;
import io.github.craftedcart.fluidui.uiaction.UIAction;
import io.github.craftedcart.fluidui.util.UIColor;
import org.jetbrains.annotations.Nullable;

/**
 * @author CraftedCart
 *         Created on 24/09/2016 (DD/MM/YYYY)
 */
public class ExportProgressOverlayUIScreen extends FluidUIScreen {

    private final ListBox listBox = new ListBox();
    private final TextButton okButton = new TextButton();

    @Nullable private UIAction onPreDrawAction;

    public ExportProgressOverlayUIScreen() {
        init();
    }

    private void init() {

        final Panel backgroundPanel = new Panel();
        backgroundPanel.setOnInitAction(() -> {
            backgroundPanel.setTheme(new DialogUITheme());

            backgroundPanel.setTopLeftPos(0, 0);
            backgroundPanel.setBottomRightPos(0, 0);
            backgroundPanel.setTopLeftAnchor(0, 0);
            backgroundPanel.setBottomRightAnchor(1, 1);
            backgroundPanel.setBackgroundColor(UIColor.transparent());
        });
        addChildComponent("backgroundPanel", backgroundPanel);

        final Panel mainPanel = new Panel();
        mainPanel.setOnInitAction(() -> {
            mainPanel.setTopLeftPos(-256, -256);
            mainPanel.setBottomRightPos(256, 256);
            mainPanel.setTopLeftAnchor(0.5, 1.5);
            mainPanel.setBottomRightAnchor(0.5, 1.5);

            PluginSmoothAnimateAnchor mainPanelAnimAnchor = new PluginSmoothAnimateAnchor();
            mainPanelAnimAnchor.setTargetTopLeftAnchor(0.5, 0.5);
            mainPanelAnimAnchor.setTargetBottomRightAnchor(0.5, 0.5);
            mainPanel.addPlugin(mainPanelAnimAnchor);
        });
        backgroundPanel.addChildComponent("mainPanel", mainPanel);

        final Label exportLabel = new Label();
        exportLabel.setOnInitAction(() -> {
            exportLabel.setTopLeftPos(24, 24);
            exportLabel.setBottomRightPos(-24, 72);
            exportLabel.setTopLeftAnchor(0, 0);
            exportLabel.setBottomRightAnchor(1, 0);
            exportLabel.setTextColor(UIColor.matGrey900());
            exportLabel.setText(LangManager.getItem("export"));
            exportLabel.setFont(FontCache.getUnicodeFont("Roboto-Regular", 24));
        });
        mainPanel.addChildComponent("exportLabel", exportLabel);

        //Defined at class level
        listBox.setOnInitAction(() -> {
            listBox.setTopLeftPos(24, 72);
            listBox.setBottomRightPos(-24, -72);
            listBox.setTopLeftAnchor(0, 0);
            listBox.setBottomRightAnchor(1, 1);
        });
        mainPanel.addChildComponent("listBox", listBox);

        //Defined at class level
        okButton.setOnInitAction(() -> {
            okButton.setEnabled(false);
            okButton.setTopLeftPos(-152, -48);
            okButton.setBottomRightPos(-24, -24);
            okButton.setTopLeftAnchor(1, 1);
            okButton.setBottomRightAnchor(1, 1);
            okButton.setText(LangManager.getItem("ok"));
        });
        okButton.setOnLMBAction(() -> {
            assert parentComponent.parentComponent instanceof FluidUIScreen;
            ((FluidUIScreen) parentComponent.parentComponent).setOverlayUiScreen(null); //Hide on OK
        });
        mainPanel.addChildComponent("okButton", okButton);

    }

    public void addTask(String taskID, String taskText) {
        final Panel taskPanel = new Panel();
        taskPanel.setOnInitAction(() -> {
            taskPanel.setTopLeftPos(0, 0);
            taskPanel.setBottomRightPos(0, 24);
            taskPanel.setBackgroundColor(UIColor.matGrey900());
        });
        PluginSmoothAnimatePanelBackgroundColor animBg = new PluginSmoothAnimatePanelBackgroundColor();
        animBg.setTargetBackgroundColor(UIColor.matGrey900());
        taskPanel.addPlugin(animBg);
        listBox.addChildComponent(taskID, taskPanel);

        final Label label = new Label();
        label.setOnInitAction(() -> {
            label.setTopLeftPos(4, 0);
            label.setBottomRightPos(-4, 0);
            label.setTopLeftAnchor(0, 0);
            label.setBottomRightAnchor(1, 1);
            label.setText(taskText);
            label.setTextColor(UIColor.matWhite());
        });
        taskPanel.addChildComponent("label", label);
    }

    public ProgressBar addProgressTask(String taskID, String taskText) {
        final Panel taskPanel = new Panel();
        taskPanel.setOnInitAction(() -> {
            taskPanel.setTopLeftPos(0, 0);
            taskPanel.setBottomRightPos(0, 30);
            taskPanel.setBackgroundColor(UIColor.matGrey900());
        });
        PluginSmoothAnimatePanelBackgroundColor animBg = new PluginSmoothAnimatePanelBackgroundColor();
        animBg.setTargetBackgroundColor(UIColor.matGrey900());
        taskPanel.addPlugin(animBg);
        listBox.addChildComponent(taskID, taskPanel);

        final Label label = new Label();
        label.setOnInitAction(() -> {
            label.setTopLeftPos(4, 0);
            label.setBottomRightPos(-4, 24);
            label.setTopLeftAnchor(0, 0);
            label.setBottomRightAnchor(1, 0);
            label.setText(taskText);
            label.setTextColor(UIColor.matWhite());
        });
        taskPanel.addChildComponent("label", label);

        final ProgressBar progressBar = new ProgressBar();
        progressBar.setOnInitAction(() -> {
            progressBar.setTopLeftPos(4, -4);
            progressBar.setBottomRightPos(-4, -2);
            progressBar.setTopLeftAnchor(0, 1);
            progressBar.setBottomRightAnchor(1, 1);
            progressBar.setForegroundColor(UIColor.matWhite());
        });
        taskPanel.addChildComponent("progressBar", progressBar);

        return progressBar;
    }

    public void activateTask(String taskID) {
        assert childComponents.get(taskID).plugins.get(0) instanceof PluginSmoothAnimatePanelBackgroundColor;
        ((PluginSmoothAnimatePanelBackgroundColor) listBox.childComponents.get(taskID).plugins.get(0)).setTargetBackgroundColor(UIColor.matBlue());
    }

    public void completeTask(String taskID) {
        assert childComponents.get(taskID).plugins.get(0) instanceof PluginSmoothAnimatePanelBackgroundColor;
        ((PluginSmoothAnimatePanelBackgroundColor) listBox.childComponents.get(taskID).plugins.get(0)).setTargetBackgroundColor(UIColor.matGreen());
    }

    public void errorTask(String taskID) {
        assert childComponents.get(taskID).plugins.get(0) instanceof PluginSmoothAnimatePanelBackgroundColor;
        Component component = listBox.childComponents.get(taskID);
        ((PluginSmoothAnimatePanelBackgroundColor) component.plugins.get(0)).setTargetBackgroundColor(UIColor.matRed());

        assert component.childComponents.get("label") instanceof Label;
        Label label = (Label) component.childComponents.get("label");
        label.setText(LangManager.getItem("exportErrorPrefix") + label.text);
    }

    public void finish() {
        okButton.setEnabled(true);
    }

    @Override
    public void preDraw() {
        super.preDraw();

        if (onPreDrawAction != null) {
            onPreDrawAction.execute();
        }
    }

    public void setOnPreDrawAction(@Nullable UIAction onPreDrawAction) {
        this.onPreDrawAction = onPreDrawAction;
    }

}
