package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.util.ExportManager;
import craftedcart.smblevelworkshop.util.LogHelper;
import craftedcart.smbworkshopexporter.ConfigData;
import craftedcart.smbworkshopexporter.LZExporter;
import craftedcart.smbworkshopexporter.ModelData;
import craftedcart.smbworkshopexporter.SMBWorkshopExporter;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.component.Label;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimatePanelBackgroundColor;
import io.github.craftedcart.fluidui.uiaction.UIAction;
import io.github.craftedcart.fluidui.util.UIColor;
import org.lwjgl.LWJGLException;

import java.awt.*;
import java.io.*;

/**
 * @author CraftedCart
 *         Created on 24/09/2016 (DD/MM/YYYY)
 */
public class ExportOverlayUIScreen extends FluidUIScreen {

    private PluginSmoothAnimateAnchor mainPanelAnimAnchor = new PluginSmoothAnimateAnchor();

    public ExportOverlayUIScreen() {
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
            backgroundPanel.setBackgroundColor(UIColor.pureBlack(0));


            PluginSmoothAnimatePanelBackgroundColor backgroundPanelAnimColor = new PluginSmoothAnimatePanelBackgroundColor();
            backgroundPanelAnimColor.setTargetBackgroundColor(UIColor.pureBlack(0.75));
            backgroundPanel.addPlugin(backgroundPanelAnimColor);
        });
        addChildComponent("backgroundPanel", backgroundPanel);

        final Panel mainPanel = new Panel();
        mainPanel.setOnInitAction(() -> {
            mainPanel.setTopLeftPos(-256, -128);
            mainPanel.setBottomRightPos(256, 128);
            mainPanel.setTopLeftAnchor(0.5, 1.5);
            mainPanel.setBottomRightAnchor(0.5, 1.5);

            //Defined at class level
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

        final ListBox listBox = new ListBox();
        listBox.setOnInitAction(() -> {
            listBox.setTopLeftPos(24, 72);
            listBox.setBottomRightPos(-24, -72);
            listBox.setTopLeftAnchor(0, 0);
            listBox.setBottomRightAnchor(1, 1);
        });
        mainPanel.addChildComponent("listBox", listBox);

        populateListBox(listBox);

        final TextButton cancelButton = new TextButton();
        cancelButton.setOnInitAction(() -> {
            cancelButton.setTopLeftPos(-152, -48);
            cancelButton.setBottomRightPos(-24, -24);
            cancelButton.setTopLeftAnchor(1, 1);
            cancelButton.setBottomRightAnchor(1, 1);
            cancelButton.setText(LangManager.getItem("cancel"));
        });
        cancelButton.setOnLMBAction(() -> {
            assert parentComponent instanceof FluidUIScreen;
            ((FluidUIScreen) parentComponent).setOverlayUiScreen(null); //Hide on OK
        });
        mainPanel.addChildComponent("cancelButton", cancelButton);

    }

    private void populateListBox(ListBox listBox) {
        final TextButton exportConfigButton = new TextButton();
        exportConfigButton.setOnInitAction(() -> {
            exportConfigButton.setTopLeftPos(0, 0);
            exportConfigButton.setBottomRightPos(0, 24);
            exportConfigButton.setText(LangManager.getItem("exportConfig"));
        });
        exportConfigButton.setOnLMBAction(this::exportConfig);
        listBox.addChildComponent("exportConfigButton", exportConfigButton);

        final TextButton exportLzRawButton = new TextButton();
        exportLzRawButton.setOnInitAction(() -> {
            exportLzRawButton.setTopLeftPos(0, 0);
            exportLzRawButton.setBottomRightPos(0, 24);
            exportLzRawButton.setText(LangManager.getItem("exportLzRaw"));
        });
        exportLzRawButton.setOnLMBAction(this::exportLzRaw);
        listBox.addChildComponent("exportLzRawButton", exportLzRawButton);
    }

    private void hideMainPanel() {
        mainPanelAnimAnchor.setTargetTopLeftAnchor(0.5, -0.5);
        mainPanelAnimAnchor.setTargetBottomRightAnchor(0.5, -0.5);
    }

    private void showMainPanel() {
        mainPanelAnimAnchor.setTargetTopLeftAnchor(0.5, 0.5);
        mainPanelAnimAnchor.setTargetBottomRightAnchor(0.5, 0.5);
    }

    private void askFileLocation(String defaultFileName, UIActionFile onSuccessAction, UIAction onCanceledAction) {
        new Thread(() -> {
            FileDialog fd = new FileDialog((Frame) null);
            fd.setMode(FileDialog.SAVE);
            fd.setFile(defaultFileName);
            fd.setVisible(true);
            File[] files = fd.getFiles();
            if (files != null && files.length > 0) {
                onSuccessAction.execute(files[0]);
            } else {
                onCanceledAction.execute();
            }
        }, "ExportThread").start();

    }

    private MainScreen getMainScreen() {
        assert parentComponent instanceof MainScreen;
        return (MainScreen) parentComponent;
    }

    private void exportConfig() {
        hideMainPanel();
        askFileLocation("config.txt", (file) -> {
            //onSuccessAction

            try {
                craftedcart.smblevelworkshop.Window.drawable.makeCurrent();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            ExportProgressOverlayUIScreen progScreen = new ExportProgressOverlayUIScreen();
            setOverlayUiScreen(progScreen);

            //Add tasks to progScreen
            progScreen.addTask("exportGenConfig", LangManager.getItem("exportGenConfig"));
            progScreen.addTask("exportSaveConfig", LangManager.getItem("exportSaveConfig"));

            try {
                Window.drawable.releaseContext();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            LogHelper.info(getClass(), "Exporting config file: " + file.getAbsolutePath());

            progScreen.activateTask("exportGenConfig");

            assert getMainScreen().clientLevelData != null;
            String exportContents = ExportManager.getConfig(getMainScreen().clientLevelData.getLevelData());

            progScreen.completeTask("exportGenConfig");
            progScreen.activateTask("exportSaveConfig");

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(exportContents);
                writer.close();
                progScreen.completeTask("exportSaveConfig");
            } catch (IOException e) {
                LogHelper.error(getClass(), "Error while exporting");
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportSaveConfig");
            }

            progScreen.finish();

        }, /* onCanceledAction */ this::showMainPanel);
    }

    private void exportLzRaw() {
        hideMainPanel();
        askFileLocation(LangManager.getItem("exportLzRawDefaultName"), (file) -> {
            //onSuccessAction

            try {
                craftedcart.smblevelworkshop.Window.drawable.makeCurrent();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            ExportProgressOverlayUIScreen progScreen = new ExportProgressOverlayUIScreen();
            setOverlayUiScreen(progScreen);

            //Add tasks to progScreen
            progScreen.addTask("exportGenConfig", LangManager.getItem("exportGenConfig"));
            progScreen.addTask("exportSaveConfig", LangManager.getItem("exportSaveConfig"));
            progScreen.addTask("exportGenObjData", LangManager.getItem("exportGenObjData"));
            progScreen.addTask("exportGenConfigData", LangManager.getItem("exportGenConfigData"));
            progScreen.addTask("exportGenLzRaw", LangManager.getItem("exportGenLzRaw"));

            try {
                Window.drawable.releaseContext();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            LogHelper.info(getClass(), "Exporting config file: " + file.getAbsolutePath());

            progScreen.activateTask("exportGenConfig");

            assert getMainScreen().clientLevelData != null;
            String exportContents = ExportManager.getConfig(getMainScreen().clientLevelData.getLevelData());

            progScreen.completeTask("exportGenConfig");
            progScreen.activateTask("exportSaveConfig");

            File tempConfigFile;

            try {
                tempConfigFile = File.createTempFile("SMBLevelWorkshopExportConfig", ".txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempConfigFile));
                writer.write(exportContents);
                writer.close();
                progScreen.completeTask("exportSaveConfig");
            } catch (IOException e) {
                LogHelper.error(getClass(), "Error while exporting");
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportSaveConfig");
                progScreen.finish();
                return;
            }

            progScreen.completeTask("exportSaveConfig");
            progScreen.activateTask("exportGenObjData");

            LogHelper.info(getClass(), "Parsing OBJ File...");
            ModelData modelData = new ModelData();
            try {
                modelData.parseObj(getMainScreen().clientLevelData.getLevelData().getModelObjSource());
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    LogHelper.error(getClass(), "OBJ file not found!");
                } else {
                    LogHelper.error(getClass(), "OBJ file: IOException");
                }
                LogHelper.error(getClass(), e);

                progScreen.errorTask("exportGenObjData");
                progScreen.finish();
                return;
            } catch (NumberFormatException e) {
                LogHelper.error(getClass(), "OBJ file: Invalid number!");
                LogHelper.error(getClass(), e);
                
                progScreen.errorTask("exportGenObjData");
                progScreen.finish();
                return;
            }

            progScreen.completeTask("exportGenObjData");
            progScreen.activateTask("exportGenConfigData");

            LogHelper.info(getClass(), "Parsing Config File...");
            ConfigData configData = new ConfigData();
            try {
                configData.parseConfig(tempConfigFile);
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    LogHelper.fatal(getClass(), "Config file not found!");
                } else {
                    LogHelper.fatal(getClass(), "Config file: IOException");
                }
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportGenConfigData");
                progScreen.finish();
                return;
            } catch (NumberFormatException e) {
                LogHelper.error(getClass(), "Config file: Invalid number!");
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportGenConfigData");
                progScreen.finish();
                return;
            } catch (IllegalStateException e) {
                LogHelper.error(getClass(), "Config file: Invalid pattern!");
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportGenConfigData");
                progScreen.finish();
                return;
            }

            if (!tempConfigFile.delete()) {
                LogHelper.warn(getClass(), "Failed to delete temporary file: " + tempConfigFile.getAbsolutePath());
            }

            progScreen.completeTask("exportGenConfigData");
            progScreen.activateTask("exportGenLzRaw");

            LogHelper.info(getClass(), "Writing LZ file...");
            try {
                LZExporter.writeLZ(modelData, configData, file);
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    LogHelper.error(getClass(), "LZ file not found!");
                } else {
                    LogHelper.error(getClass(), "LZ file: IOException");
                }
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportGenLzRaw");
                progScreen.finish();
                return;
            }

            progScreen.completeTask("exportGenLzRaw");

            progScreen.finish();

        }, /* onCanceledAction */ this::showMainPanel);
    }

}

interface UIActionFile {
    void execute(File file);
}
