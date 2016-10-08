package craftedcart.smblevelworkshop.ui.community;

import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.community.CommunityRootData;
import craftedcart.smblevelworkshop.community.creator.AbstractCommunityCreator;
import craftedcart.smblevelworkshop.community.sync.SyncManager;
import craftedcart.smblevelworkshop.exception.SyncDatabasesException;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.ui.theme.DialogUITheme;
import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smbworkshopexporter.util.LogHelper;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.IUIScreen;
import io.github.craftedcart.fluidui.component.Button;
import io.github.craftedcart.fluidui.component.Label;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.util.EnumVAlignment;
import io.github.craftedcart.fluidui.util.UIColor;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author CraftedCart
 *         Created on 04/10/2016 (DD/MM/YYYY)
 */
public class CommunityScreen extends FluidUIScreen {

    private final CommunityHomeScreen homeScreen = new CommunityHomeScreen();

    public CommunityScreen() {
        init();
    }

    private void init() {

        final Panel mainPanel = new Panel();
        mainPanel.setOnInitAction(() -> {
            mainPanel.setTheme(new DialogUITheme());

            mainPanel.setTopLeftPos(0, 0);
            mainPanel.setBottomRightPos(0, 0);
            mainPanel.setTopLeftAnchor(0, 0);
            mainPanel.setBottomRightAnchor(1, 1);
        });
        addChildComponent("mainPanel", mainPanel);

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
            IUIScreen newScreen;
            if (ProjectManager.getCurrentProject().mainScreen != null) {
                newScreen = ProjectManager.getCurrentProject().mainScreen;
            } else {
                newScreen = new MainScreen();
            }

            Window.setUIScreen(newScreen);
        });
        topBarPanel.addChildComponent("closeButton", closeButton);

        final Label titleLabel = new Label();
        titleLabel.setOnInitAction(() -> {
            titleLabel.setTopLeftPos(96, 0);
            titleLabel.setBottomRightPos(-24, 0);
            titleLabel.setTopLeftAnchor(0, 0);
            titleLabel.setBottomRightAnchor(1, 1);
            titleLabel.setTextColor(UIColor.matGrey900());
            titleLabel.setText(LangManager.getItem("community"));
            titleLabel.setFont(FontCache.getUnicodeFont("Roboto-Regular", 24));
            titleLabel.setVerticalAlign(EnumVAlignment.centre);
        });
        topBarPanel.addChildComponent("titleLabel", titleLabel);

        final Button userButton = new Button();
        userButton.setOnInitAction(() -> {
            userButton.setTopLeftPos(-64, 8);
            userButton.setBottomRightPos(-32, -8);
            userButton.setTopLeftAnchor(1, 0);
            userButton.setBottomRightAnchor(1, 1);
            userButton.setTexture(ResourceManager.getTexture("image/user").getTexture());
            userButton.setTooltip(LangManager.getItem("userProfile"));
            userButton.setBackgroundIdleColor(UIColor.matGrey900());
            userButton.setBackgroundActiveColor(UIColor.matGrey());
            userButton.setBackgroundHitColor(UIColor.matGrey900());
        });
        userButton.setOnLMBAction(() -> {
            //TODO
        });
        topBarPanel.addChildComponent("userButton", userButton);

        final Button syncButton = new Button();
        syncButton.setOnInitAction(() -> {
            syncButton.setTopLeftPos(-120, 8);
            syncButton.setBottomRightPos(-88, -8);
            syncButton.setTopLeftAnchor(1, 0);
            syncButton.setBottomRightAnchor(1, 1);
            syncButton.setTexture(ResourceManager.getTexture("image/sync").getTexture());
            syncButton.setTooltip(LangManager.getItem("syncDatabases"));
            syncButton.setBackgroundIdleColor(UIColor.matGrey900());
            syncButton.setBackgroundActiveColor(UIColor.matGrey());
            syncButton.setBackgroundHitColor(UIColor.matGrey900());
        });
        syncButton.setOnLMBAction(this::syncDatabases);
        topBarPanel.addChildComponent("syncButton", syncButton);
        //</editor-fold>

        //Defined at class level
        homeScreen.setOnInitAction(() -> {
            homeScreen.setTopLeftPos(0, 48);
            homeScreen.setBottomRightPos(0, 0);
            homeScreen.setTopLeftAnchor(0, 0);
            homeScreen.setBottomRightAnchor(1, 1);
        });
        mainPanel.addChildComponent("homeScreen", homeScreen);

    }

    private void syncDatabases() {
        SyncProgressOverlayUIScreen syncOverlay = new SyncProgressOverlayUIScreen();

        syncOverlay.addTask("syncRoot", LangManager.getItem("syncRootTask"));

        setOverlayUiScreen(syncOverlay);

        new Thread(() -> {
            try {
                SyncManager sm = new SyncManager();

                sm.setOnRootSyncFinishAction(() -> {
                    syncOverlay.completeTask("syncRoot");

                    for (AbstractCommunityCreator creator : CommunityRootData.getCreatorList()) { //Add all creators to the overlay
                        syncOverlay.addTask("syncUser" + creator.getUsername(), String.format(LangManager.getItem("syncUserTask"), creator.getUsername()));
                    }
                });

                sm.setOnUserSyncBeginAction((username) -> syncOverlay.activateTask("syncUser" + username));
                sm.setOnUserSyncFinishAction((username) -> syncOverlay.completeTask("syncUser" + username));
                sm.setOnBuildCommunityDatabaseBeginAction(() -> syncOverlay.addTask("syncBuildCommunityDatabase", LangManager.getItem("syncBuildCommunityDatabase")));
                sm.setOnBuildCommunityDatabaseFinishAction(() -> syncOverlay.completeTask("syncBuildCommunityDatabase"));

                sm.syncDatabases();;

            } catch (IOException e) {
                LogHelper.error(getClass(), "IOException: Failed to sync databases");
                LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));

                //TODO: Display an error screen
            } catch (SyncDatabasesException e) {
                LogHelper.error(getClass(), "SyncDatabasesException: Failed to sync databases");
                LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));

                //TODO: Display an error screen
            } catch (SAXException e) {
                LogHelper.error(getClass(), "SAXException: Failed to sync databases");
                LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));

                //TODO: Display an error screen
            }

            setOverlayUiScreen(null);
            homeScreen.reload();
        }, "SyncThread").start();

    }

}

