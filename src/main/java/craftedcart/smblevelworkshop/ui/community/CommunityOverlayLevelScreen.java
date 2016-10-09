package craftedcart.smblevelworkshop.ui.community;

import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.community.CommunityLevel;
import craftedcart.smblevelworkshop.community.CommunityLevelFull;
import craftedcart.smblevelworkshop.community.CommunityRootData;
import craftedcart.smblevelworkshop.data.AppDataManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.resource.ResourceTexture;
import craftedcart.smblevelworkshop.ui.theme.DialogUITheme;
import craftedcart.smbworkshopexporter.util.LogHelper;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimateAnchor;
import io.github.craftedcart.fluidui.plugin.PluginSmoothAnimatePanelBackgroundColor;
import io.github.craftedcart.fluidui.util.EnumImageScaling;
import io.github.craftedcart.fluidui.util.EnumVAlignment;
import io.github.craftedcart.fluidui.util.UIColor;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GitHub;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.UnicodeFont;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 06/10/2016 (DD/MM/YYYY)
 */
public class CommunityOverlayLevelScreen extends FluidUIScreen {

    @NotNull private CommunityLevel level;
    @Nullable private String singleRepoName; //Null if the user is using an account instead of a single repo
    @NotNull private List<ResourceTexture> screenshots = new ArrayList<>();

    private boolean shouldLoadTextures = true;

    private final UnicodeFont HEADING_FONT;
    private final UnicodeFont SUBHEADING_FONT;

    public CommunityOverlayLevelScreen(@NotNull CommunityLevel level) {
        this.level = level;

        HEADING_FONT = FontCache.getUnicodeFont("Roboto-Regular", 24);
        SUBHEADING_FONT = FontCache.getUnicodeFont("Roboto-Regular", 20);

        setTheme(new DialogUITheme());
        initComponents();
    }

    private void initComponents() {
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
            mainPanel.setTopLeftPos(24, 24);
            mainPanel.setBottomRightPos(-24, -24);
            mainPanel.setTopLeftAnchor(0, 1);
            mainPanel.setBottomRightAnchor(1, 2);

            PluginSmoothAnimateAnchor mainPanelAnimAnchor = new PluginSmoothAnimateAnchor();
            mainPanelAnimAnchor.setTargetTopLeftAnchor(0, 0);
            mainPanelAnimAnchor.setTargetBottomRightAnchor(1, 1);
            mainPanel.addPlugin(mainPanelAnimAnchor);
        });
        backgroundPanel.addChildComponent("mainPanel", mainPanel);

        //<editor-fold desc="topBarPanel">
        final Panel topBarPanel = new Panel();
        topBarPanel.setOnInitAction(() -> {
            topBarPanel.setTopLeftPos(0, 0);
            topBarPanel.setBottomRightPos(0, 48);
            topBarPanel.setTopLeftAnchor(0, 0);
            topBarPanel.setBottomRightAnchor(1, 0);
        });
        mainPanel.addChildComponent("topBarPanel", topBarPanel);

        final Button closeButton = new Button();
        closeButton.setOnInitAction(() -> {
            closeButton.setTopLeftPos(32, 8);
            closeButton.setBottomRightPos(64, -8);
            closeButton.setTopLeftAnchor(0, 0);
            closeButton.setBottomRightAnchor(0, 1);
            closeButton.setTexture(ResourceManager.getTexture("image/close").getTexture());
            closeButton.setBackgroundIdleColor(UIColor.matGrey900());
            closeButton.setBackgroundActiveColor(UIColor.matGrey());
            closeButton.setBackgroundHitColor(UIColor.matGrey900());
        });
        closeButton.setOnLMBAction(() -> {
            assert parentComponent instanceof FluidUIScreen;
            unloadOGLTextures();
            ((FluidUIScreen) parentComponent).setOverlayUiScreen(null);
        });
        topBarPanel.addChildComponent("closeButton", closeButton);

        final Label titleLabel = new Label();
        titleLabel.setOnInitAction(() -> {
            titleLabel.setTopLeftPos(96, 0);
            titleLabel.setBottomRightPos(-24, 0);
            titleLabel.setTopLeftAnchor(0, 0);
            titleLabel.setBottomRightAnchor(1, 1);
            titleLabel.setTextColor(UIColor.matGrey900());
            titleLabel.setText(level.getName());
            titleLabel.setFont(FontCache.getUnicodeFont("Roboto-Regular", 24));
            titleLabel.setVerticalAlign(EnumVAlignment.centre);
        });
        topBarPanel.addChildComponent("titleLabel", titleLabel);
        //</editor-fold>

        final ListBox infoListBox = new ListBox();
        infoListBox.setOnInitAction(() -> {
            infoListBox.setTopLeftPos(0, 48);
            infoListBox.setBottomRightPos(0, 0);
            infoListBox.setTopLeftAnchor(0, 0);
            infoListBox.setBottomRightAnchor(0.5, 1);
        });
        mainPanel.addChildComponent("infoListBox", infoListBox);

        final ListBox screenshotListBox = new ListBox();
        screenshotListBox.setOnInitAction(() -> {
            screenshotListBox.setTopLeftPos(0, 48);
            screenshotListBox.setBottomRightPos(0, 0);
            screenshotListBox.setTopLeftAnchor(0.5, 0);
            screenshotListBox.setBottomRightAnchor(1, 1);
        });
        mainPanel.addChildComponent("screenshotListBox", screenshotListBox);

        addLevel(infoListBox);
        addScreenshots(screenshotListBox);
    }

    private void addLevel(ListBox parent) {
        final Panel descriptionPanel = new Panel();
        descriptionPanel.setOnInitAction(() -> {
            descriptionPanel.setTopLeftPos(0, 0);
            descriptionPanel.setBottomRightPos(0, 24);
        });
        parent.addChildComponent("descriptionPanel", descriptionPanel);

        final Label descriptionLabel = new Label();
        descriptionLabel.setOnInitAction(() -> {
            descriptionLabel.setTopLeftPos(24, 0);
            descriptionLabel.setBottomRightPos(-24, 0);
            descriptionLabel.setTopLeftAnchor(0, 0);
            descriptionLabel.setBottomRightAnchor(1, 1);
            descriptionLabel.setSoftWrap(true);
        });
        descriptionLabel.setOnWrappingChangedAction(() -> {
            descriptionPanel.height = 24 + new DialogUITheme().labelFont.getLineHeight() * descriptionLabel.wrapLines;
            parent.reorganizeChildComponents();
        });
        descriptionPanel.addChildComponent("descriptionLabel", descriptionLabel);

        final Panel infoPanel = new Panel();
        infoPanel.setOnInitAction(() -> {
            infoPanel.setTopLeftPos(0, 0);
            infoPanel.setBottomRightPos(0, 24);
        });
        parent.addChildComponent("infoPanel", infoPanel);

        final Label infoLabel = new Label();
        infoLabel.setOnInitAction(() -> {
            infoLabel.setTopLeftPos(24, 0);
            infoLabel.setBottomRightPos(-24, 0);
            infoLabel.setTopLeftAnchor(0, 0);
            infoLabel.setBottomRightAnchor(1, 1);
            infoLabel.setSoftWrap(true);
        });
        infoLabel.setOnWrappingChangedAction(() -> {
            infoPanel.height = 24 + new DialogUITheme().labelFont.getLineHeight() * infoLabel.wrapLines;
            parent.reorganizeChildComponents();
        });
        infoPanel.addChildComponent("infoLabel", infoLabel);

        //Get level and fill in blanks in another thread, so that the main thread isn't blocked
        new Thread(() -> {
            try {
                CommunityLevelFull levelFull = getLevelFull();

                //Convert Unix seconds to human readable time
                long unixSeconds = levelFull.getCreationTime();
                Date date = new Date(unixSeconds * 1000L); //Convert seconds to milliseconds
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); //The format of the date
                String formattedDate = sdf.format(date);

                descriptionLabel.setText(levelFull.getDescription());
                infoLabel.setText(String.format("%s: %s (%s)\n%s: %s\n%s: %s\n%s: %s\n%s: %d",
                        LangManager.getItem("creator"), levelFull.getUserDisplayName(), levelFull.getUsername(), //Created by
                        LangManager.getItem("levelId"), levelFull.getId(), //Level ID
                        LangManager.getItem("createdAt"), formattedDate, //Created at
                        LangManager.getItem("licence"), levelFull.getLicence(), //Licence
                        LangManager.getItem("suggestedReplacement"), levelFull.getSuggestedReplacement())); //Suggested stage replacement

            } catch (SQLException | IOException | SAXException | ParserConfigurationException e) {
                LogHelper.error(getClass(), "Error while getting level full");
                LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));
            }
        }, "GetLevelDataFull").start();

    }

    private CommunityLevelFull getLevelFull() throws SQLException, IOException, ParserConfigurationException, SAXException {
        File supportDir = AppDataManager.getAppSupportDirectory();
        File levelDir = new File(supportDir, String.format("community/users/%s/%s", level.getUsername(), level.getId()));
        File levelXML = new File(levelDir, "level.xml");

        singleRepoName = CommunityRootData.getDbManager().getUserSingleRepo(level.getUsername());

        if (levelXML.exists()) {
            if (levelXML.isDirectory()) {
                LogHelper.warn(getClass(), String.format("%s is a directory - It shouldn't be! - Deleting directory", levelXML.getAbsolutePath()));
                FileUtils.deleteDirectory(levelXML);

                downloadLevelXML();
            }
        } else {
            downloadLevelXML();
        }

        return CommunityLevelFull.getCommunityLevelFullFromXML(levelXML, level.getUsername(), level.getUserDisplayName());
    }

    private void downloadLevelXML() throws IOException, SQLException {
        File supportDir = AppDataManager.getAppSupportDirectory();
        File levelDir = new File(supportDir, String.format("community/users/%s/%s", level.getUsername(), level.getId()));
        File levelXML = new File(levelDir, "level.xml");

        AppDataManager.tryCreateDirectory(levelDir);

        String url;

        if (singleRepoName != null) {
            //It's a single repo
            url = String.format("https://raw.githubusercontent.com/%s/%s/%s/level.xml", level.getUsername(), singleRepoName, level.getId());
        } else {
            //It's a single account
            url = String.format("https://raw.githubusercontent.com/%s/%s/master/level.xml", level.getUsername(), level.getId());
        }

        LogHelper.info(getClass(), String.format("Downloading level.xml for %s - %s", level.getUsername(), level.getId()));

        FileUtils.copyURLToFile(new URL(url), levelXML, 30000, 30000);
    }

    private void addScreenshots(ListBox parent) {
        File supportDir = AppDataManager.getAppSupportDirectory();
        File screenshotDir = new File(supportDir, String.format("community/users/%s/%s/screenshot", level.getUsername(), level.getId()));

        new Thread(() -> {
            try {
                if (screenshotDir.exists()) {
                    if (!screenshotDir.isDirectory()) {
                        LogHelper.warn(getClass(), String.format("%s is a file - It shouldn't be! - Deleting file", screenshotDir.getAbsolutePath()));
                        if (!screenshotDir.delete()) {
                            LogHelper.error(getClass(), String.format("Failed to delete file %s", screenshotDir.getAbsolutePath()));
                        }

                        downloadScreenshots();
                    }
                } else {
                    downloadScreenshots();
                }

                for (File file : screenshotDir.listFiles()) {
                    if (file.getName().toUpperCase().endsWith(".JPG") && !file.isDirectory()) { //If it's a PNG image
                        if (!shouldLoadTextures) {
                            //Stop loading textures!
                            break;
                        }
                        ResourceTexture tex = new ResourceTexture("JPG", file);
                        screenshots.add(tex);

                        Image image = new Image();
                        image.setOnInitAction(() -> {
                            image.setTopLeftPos(0, 0);
                            image.setBottomRightPos(0, 300);
                            image.setImageScaling(EnumImageScaling.fit);
                            image.setTexture(tex.getTexture());
                        });
                        parent.addChildComponent(file.getName() + "Screenshot", image);
                    }
                }

            } catch (Exception e) {
                LogHelper.error(getClass(), "Error while getting screenshots");
                LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));

                //TODO: Check rate limit, and show error message if rate limit has been hit
            }
        }, "AddScreenshotThread").start();
    }

    private void downloadScreenshots() throws IOException {
        File supportDir = AppDataManager.getAppSupportDirectory();
        File screenshotDir = new File(supportDir, String.format("community/users/%s/%s/screenshot", level.getUsername(), level.getId()));

        List<GHContent> screenshots;

        GitHub gitHub = GitHub.connectAnonymously(); //TODO: If user is logged in, use their rate limit
        if (singleRepoName == null) {
            screenshots = gitHub.getUser(level.getUsername()).getRepository(level.getId()).getDirectoryContent("screenshot");
        } else {
            screenshots = gitHub.getUser(level.getUsername()).getRepository(singleRepoName).getDirectoryContent("screenshot", level.getId());
        }

        for (GHContent screenshot : screenshots) {
            if (!screenshot.getName().toUpperCase().endsWith(".JPG")) {
                LogHelper.warn(getClass(), String.format("Screenshot for %s - %s is not a JPG", level.getUsername(), level.getId()));
                continue;
            }

            String downloadURL = screenshot.getDownloadUrl();

            AppDataManager.tryCreateDirectory(screenshotDir);

            LogHelper.info(getClass(), String.format("Downloading screenshot for %s - %s from %s", level.getUsername(), level.getId(), downloadURL));
            FileUtils.copyURLToFile(new URL(downloadURL), new File(screenshotDir, screenshot.getName()), 30000, 30000);
        }
    }

    private void unloadOGLTextures() {
        shouldLoadTextures = false;

        GL11.glFinish();
        for (ResourceTexture tex : screenshots) {
            tex.getTexture().release();
        }
        GL11.glFlush();

        screenshots.clear();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (screenshots.size() > 0) {
            LogHelper.warn(getClass(), String.valueOf(screenshots.size()) +
                    " textures still left to unload right before being garbage collected! This shouldn't happen! - Unloading textures");

            GL11.glFinish();
            for (ResourceTexture tex : screenshots) {
                tex.getTexture().release();
            }
            GL11.glFlush();

            screenshots.clear();
        }
    }
}
