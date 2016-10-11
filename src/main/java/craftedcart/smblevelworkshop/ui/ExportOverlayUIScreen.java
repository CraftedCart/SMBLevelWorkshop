package craftedcart.smblevelworkshop.ui;

import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.util.ExportManager;
import craftedcart.smblevelworkshop.util.LogHelper;
import craftedcart.smbworkshopexporter.*;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.Label;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimatePanelBackgroundColor;
import io.github.craftedcart.fluidui.uiaction.UIAction;
import io.github.craftedcart.fluidui.util.UIColor;
import org.lwjgl.LWJGLException;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
            mainPanel.setTopLeftPos(-256, -150);
            mainPanel.setBottomRightPos(256, 150);
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
            listBox.scrollbarThickness = 0;
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

        final TextButton exportLzCompressedSmb1Button = new TextButton();
        exportLzCompressedSmb1Button.setOnInitAction(() -> {
            exportLzCompressedSmb1Button.setTopLeftPos(0, 0);
            exportLzCompressedSmb1Button.setBottomRightPos(0, 24);
            exportLzCompressedSmb1Button.setText(LangManager.getItem("exportLzCompressedSmb1"));
            exportLzCompressedSmb1Button.setTooltip(LangManager.getItem("backgroundNotYetSupported"));
        });
        exportLzCompressedSmb1Button.setOnLMBAction(() -> exportLzCompressed(new SMB1LZExporter()));
        listBox.addChildComponent("exportLzCompressedSmb1Button", exportLzCompressedSmb1Button);

        final TextButton exportLzRawSmb1Button = new TextButton();
        exportLzRawSmb1Button.setOnInitAction(() -> {
            exportLzRawSmb1Button.setTopLeftPos(0, 0);
            exportLzRawSmb1Button.setBottomRightPos(0, 24);
            exportLzRawSmb1Button.setText(LangManager.getItem("exportLzRawSmb1"));
            exportLzRawSmb1Button.setTooltip(LangManager.getItem("backgroundNotYetSupported"));
        });
        exportLzRawSmb1Button.setOnLMBAction(() -> exportLzRaw(new SMB1LZExporter()));
        listBox.addChildComponent("exportLzRawSmb1Button", exportLzRawSmb1Button);

        final TextButton exportLzCompressedSmb2Button = new TextButton();
        exportLzCompressedSmb2Button.setOnInitAction(() -> {
            exportLzCompressedSmb2Button.setTopLeftPos(0, 0);
            exportLzCompressedSmb2Button.setBottomRightPos(0, 24);
            exportLzCompressedSmb2Button.setText(LangManager.getItem("exportLzCompressedSmb2"));
            exportLzCompressedSmb2Button.setTooltip(LangManager.getItem("backgroundNotYetSupported"));
        });
        exportLzCompressedSmb2Button.setOnLMBAction(() -> exportLzCompressed(new SMB2LZExporter()));
        listBox.addChildComponent("exportLzCompressedSmb2Button", exportLzCompressedSmb2Button);

        final TextButton exportLzRawSmb2Button = new TextButton();
        exportLzRawSmb2Button.setOnInitAction(() -> {
            exportLzRawSmb2Button.setTopLeftPos(0, 0);
            exportLzRawSmb2Button.setBottomRightPos(0, 24);
            exportLzRawSmb2Button.setText(LangManager.getItem("exportLzRawSmb2"));
            exportLzRawSmb2Button.setTooltip(LangManager.getItem("backgroundNotYetSupported"));
        });
        exportLzRawSmb2Button.setOnLMBAction(() -> exportLzRaw(new SMB2LZExporter()));
        listBox.addChildComponent("exportLzRawSmb2Button", exportLzRawSmb2Button);
    }

    private void hideMainPanel() {
        enableClicking = false;
        mainPanelAnimAnchor.setTargetTopLeftAnchor(0.5, -0.5);
        mainPanelAnimAnchor.setTargetBottomRightAnchor(0.5, -0.5);
    }

    private void showMainPanel() {
        enableClicking = true;
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
            progScreen.addTask("exportWriteConfig", LangManager.getItem("exportWriteConfig"));

            try {
                Window.drawable.releaseContext();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            LogHelper.info(getClass(), "Exporting config file: " + file.getAbsolutePath());

            progScreen.activateTask("exportGenConfig");

            assert ProjectManager.getCurrentProject().clientLevelData != null;
            String exportContents = ExportManager.getConfig(ProjectManager.getCurrentProject().clientLevelData.getLevelData());

            progScreen.completeTask("exportGenConfig");
            progScreen.activateTask("exportWriteConfig");

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(exportContents);
                writer.close();
                progScreen.completeTask("exportWriteConfig");
            } catch (IOException e) {
                LogHelper.error(getClass(), "Error while exporting");
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportWriteConfig");
            }

            progScreen.finish();

        }, /* onCanceledAction */ this::showMainPanel);
    }

    private void exportLzRaw(AbstractLzExporter lzExporter) {
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
            progScreen.addTask("exportWriteConfig", LangManager.getItem("exportWriteConfig"));
            progScreen.addTask("exportGenObjData", LangManager.getItem("exportGenObjData"));
            progScreen.addTask("exportGenConfigData", LangManager.getItem("exportGenConfigData"));
            ProgressBar exportGenCfgLzRawProg = progScreen.addProgressTask("exportGenCfgLzRaw", LangManager.getItem("exportGenCfgLzRaw"));
            ProgressBar exportGenColLzRawProg = progScreen.addProgressTask("exportGenColLzRaw", LangManager.getItem("exportGenColLzRaw"));
            ProgressBar exportGenLzRawProg = progScreen.addProgressTask("exportGenLzRaw", LangManager.getItem("exportGenLzRaw"));

            try {
                Window.drawable.releaseContext();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            LogHelper.info(getClass(), "Exporting config file: " + file.getAbsolutePath());

            progScreen.activateTask("exportGenConfig");

            assert ProjectManager.getCurrentProject().clientLevelData != null;
            String exportContents = ExportManager.getConfig(ProjectManager.getCurrentProject().clientLevelData.getLevelData());

            progScreen.completeTask("exportGenConfig");
            progScreen.activateTask("exportWriteConfig");

            File tempConfigFile;

            try {
                tempConfigFile = File.createTempFile("SMBLevelWorkshopExportConfig", ".txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempConfigFile));
                writer.write(exportContents);
                writer.close();
                progScreen.completeTask("exportWriteConfig");
            } catch (IOException e) {
                LogHelper.error(getClass(), "Error while exporting");
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportWriteConfig");
                progScreen.finish();
                return;
            }

            progScreen.completeTask("exportWriteConfig");
            progScreen.activateTask("exportGenObjData");

            LogHelper.info(getClass(), "Parsing OBJ File...");
            ModelData modelData = new ModelData();
            try {
                modelData.parseObj(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModelObjSource());
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
            progScreen.activateTask("exportGenCfgLzRaw");

            progScreen.setOnPreDrawAction(() -> {
                if (lzExporter.cfgBytesToWrite != 0) {
                    exportGenCfgLzRawProg.setValue((double) lzExporter.cfgBytesWritten / lzExporter.cfgBytesToWrite);
                } else {
                    exportGenCfgLzRawProg.setValue(1);
                }
                if (lzExporter.colBytesToWrite != 0) {
                    exportGenColLzRawProg.setValue((double) lzExporter.colBytesWritten / lzExporter.colBytesToWrite);
                } else {
                    exportGenColLzRawProg.setValue(1);
                }
                if (lzExporter.lzBytesToWrite != 0) {
                    exportGenLzRawProg.setValue((double) lzExporter.lzBytesWritten / lzExporter.lzBytesToWrite);
                } else {
                    exportGenLzRawProg.setValue(1);
                }
            });

            lzExporter.setTaskDoneAction((enumLZExportTask) -> {
                switch (enumLZExportTask) {
                    case EXPORT_CONFIG:
                        progScreen.completeTask("exportGenCfgLzRaw");
                        progScreen.activateTask("exportGenColLzRaw");
                        break;
                    case EXPORT_COLLISION:
                        progScreen.completeTask("exportGenColLzRaw");
                        progScreen.activateTask("exportGenLzRaw");
                        break;
                }
            });

            LogHelper.info(getClass(), "Writing raw LZ file...");
            try {
                lzExporter.writeRawLZ(modelData, configData, file);
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    LogHelper.error(getClass(), "Raw LZ file not found!");
                } else {
                    LogHelper.error(getClass(), "Raw LZ file: IOException");
                }
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportGenLzRaw");
                progScreen.finish();
                return;
            }

            progScreen.setOnPreDrawAction(null);
            exportGenCfgLzRawProg.setValue(1);
            exportGenColLzRawProg.setValue(1);
            exportGenLzRawProg.setValue(1);

            progScreen.completeTask("exportGenLzRaw");

            progScreen.finish();

        }, /* onCanceledAction */ this::showMainPanel);
    }

    private void exportLzCompressed(AbstractLzExporter lzExporter) {
        hideMainPanel();
        askFileLocation(LangManager.getItem("exportLzCompressedDefaultName"), (file) -> {
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
            progScreen.addTask("exportWriteConfig", LangManager.getItem("exportWriteConfig"));
            progScreen.addTask("exportGenObjData", LangManager.getItem("exportGenObjData"));
            progScreen.addTask("exportGenConfigData", LangManager.getItem("exportGenConfigData"));
            ProgressBar exportGenCfgLzRawProg = progScreen.addProgressTask("exportGenCfgLzRaw", LangManager.getItem("exportGenCfgLzRaw"));
            ProgressBar exportGenColLzRawProg = progScreen.addProgressTask("exportGenColLzRaw", LangManager.getItem("exportGenColLzRaw"));
            ProgressBar exportGenLzRawProg = progScreen.addProgressTask("exportGenLzRaw", LangManager.getItem("exportGenLzRaw"));
            ProgressBar exportReadLzRawProg = progScreen.addProgressTask("exportReadLzRaw", LangManager.getItem("exportReadLzRaw"));
            ProgressBar exportCompressLzRawProg = progScreen.addProgressTask("exportCompressLzRaw", LangManager.getItem("exportCompressLzRaw"));
            ProgressBar exportWriteLzProg = progScreen.addProgressTask("exportWriteLz", LangManager.getItem("exportWriteLz"));

            try {
                Window.drawable.releaseContext();
            } catch (LWJGLException e) {
                e.printStackTrace();
            }

            LogHelper.info(getClass(), "Exporting config file: " + file.getAbsolutePath());

            progScreen.activateTask("exportGenConfig");

            assert ProjectManager.getCurrentProject().clientLevelData != null;
            String exportContents = ExportManager.getConfig(ProjectManager.getCurrentProject().clientLevelData.getLevelData());

            progScreen.completeTask("exportGenConfig");
            progScreen.activateTask("exportWriteConfig");

            File tempConfigFile;

            try {
                tempConfigFile = File.createTempFile("SMBLevelWorkshopExportConfig", ".txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempConfigFile));
                writer.write(exportContents);
                writer.close();
                progScreen.completeTask("exportWriteConfig");
            } catch (IOException e) {
                LogHelper.error(getClass(), "Error while exporting");
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportWriteConfig");
                progScreen.finish();
                return;
            }

            progScreen.completeTask("exportWriteConfig");
            progScreen.activateTask("exportGenObjData");

            LogHelper.info(getClass(), "Parsing OBJ File...");
            ModelData modelData = new ModelData();
            try {
                modelData.parseObj(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getModelObjSource());
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
            progScreen.activateTask("exportGenCfgLzRaw");

            progScreen.setOnPreDrawAction(() -> {
                if (lzExporter.cfgBytesToWrite != 0) {
                    exportGenCfgLzRawProg.setValue((double) lzExporter.cfgBytesWritten / lzExporter.cfgBytesToWrite);
                } else {
                    exportGenCfgLzRawProg.setValue(1);
                }
                if (lzExporter.colBytesToWrite != 0) {
                    exportGenColLzRawProg.setValue((double) lzExporter.colBytesWritten / lzExporter.colBytesToWrite);
                } else {
                    exportGenColLzRawProg.setValue(1);
                }
                if (lzExporter.lzBytesToWrite != 0) {
                    exportGenLzRawProg.setValue((double) lzExporter.lzBytesWritten / lzExporter.lzBytesToWrite);
                } else {
                    exportGenLzRawProg.setValue(1);
                }
            });

            lzExporter.setTaskDoneAction((enumLZExportTask) -> {
                switch (enumLZExportTask) {
                    case EXPORT_CONFIG:
                        progScreen.completeTask("exportGenCfgLzRaw");
                        progScreen.activateTask("exportGenColLzRaw");
                        break;
                    case EXPORT_COLLISION:
                        progScreen.completeTask("exportGenColLzRaw");
                        progScreen.activateTask("exportGenLzRaw");
                        break;
                }
            });

            File tempRawLzFile;

            LogHelper.info(getClass(), "Writing raw LZ file...");
            try {
                tempRawLzFile = File.createTempFile("SMBLevelWorkshopExportRawLz", ".lz.raw");
                lzExporter.writeRawLZ(modelData, configData, tempRawLzFile);
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    LogHelper.error(getClass(), "Raw LZ file not found!");
                } else {
                    LogHelper.error(getClass(), "Raw LZ file: IOException");
                }
                LogHelper.error(getClass(), e);
                progScreen.errorTask("exportGenLzRaw");
                progScreen.finish();
                return;
            }

            progScreen.setOnPreDrawAction(null);
            exportGenCfgLzRawProg.setValue(1);
            exportGenColLzRawProg.setValue(1);
            exportGenLzRawProg.setValue(1);

            progScreen.completeTask("exportGenLzRaw");
            progScreen.activateTask("exportReadLzRaw");

            LogHelper.info(getClass(), "Reading raw LZ file...");
            final List<Byte> contents = new ArrayList<>();
            try {
                RandomAccessFile raf = new RandomAccessFile(tempRawLzFile, "r");
                while (raf.getFilePointer() < raf.length()) {
                    contents.add(raf.readByte());
                    exportReadLzRawProg.setValue((double) raf.getFilePointer() / raf.length()); //TODO Only call this every frame
                }

                if (!tempRawLzFile.delete()) {
                    LogHelper.warn(getClass(), "Failed to delete temporary file: " + tempConfigFile.getAbsolutePath());
                }
            } catch (IOException e) {
                LogHelper.error(getClass(), "Raw LZ file: IOException");
                LogHelper.error(getClass(), e);
            }

            exportReadLzRawProg.setValue(1);
            progScreen.completeTask("exportReadLzRaw");
            progScreen.activateTask("exportCompressLzRaw");

            LogHelper.info(getClass(), "Compressing raw LZ file...");
            final Byte[] byteArray = contents.toArray(new Byte[contents.size()]);

            LZCompressor compressor = new LZCompressor();

            progScreen.setOnPreDrawAction(() -> {
                if (compressor.progressMax != 0) {
                    exportCompressLzRawProg.setValue((double) compressor.progress / compressor.progressMax);
                } else {
                    exportCompressLzRawProg.setValue(1);
                }
            });

            List<Byte> bl = compressor.compress(byteArray); //Compress the raw lz

            progScreen.completeTask("exportCompressLzRaw");
            progScreen.activateTask("exportWriteLz");

            try {
                LogHelper.info(getClass(), "Writing LZ file...");
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                int i = 0;
                int size = bl.size();
                for (Byte c : bl) {
                    raf.write(c);
                    exportWriteLzProg.setValue((double) i / size); //TODO Only call this every frame
                    i++;
                }
                raf.close();
            } catch (IOException e) {
                LogHelper.error(getClass(), "LZ file: IOException");
                LogHelper.error(getClass(), e);

                progScreen.errorTask("exportWriteLz");
                progScreen.finish();
            }

            exportWriteLzProg.setValue(1);

            progScreen.completeTask("exportWriteLz");
            progScreen.finish();


        }, /* onCanceledAction */ this::showMainPanel);
    }

}

interface UIActionFile {
    void execute(File file);
}
