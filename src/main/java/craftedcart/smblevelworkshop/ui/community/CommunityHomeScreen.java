package craftedcart.smblevelworkshop.ui.community;

import craftedcart.smblevelworkshop.community.CommunityAnnouncement;
import craftedcart.smblevelworkshop.community.CommunityRootData;
import craftedcart.smblevelworkshop.community.sync.SyncManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.theme.DialogUITheme;
import craftedcart.smbworkshopexporter.util.LogHelper;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.util.EnumVAlignment;
import io.github.craftedcart.fluidui.util.UIColor;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.newdawn.slick.UnicodeFont;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 06/10/2016 (DD/MM/YYYY)
 */
public class CommunityHomeScreen extends ListBox {

    private final UnicodeFont HEADING_FONT;
    private final UnicodeFont SUBHEADING_FONT;

    public CommunityHomeScreen() {
        init();
        postInit();

        HEADING_FONT = FontCache.getUnicodeFont("Roboto-Regular", 24);
        SUBHEADING_FONT = FontCache.getUnicodeFont("Roboto-Regular", 20);

        setTheme(new DialogUITheme());
        initComponents();
    }

    public void reload() {
        clearChildComponents();
        initComponents();
    }

    private void initComponents() {
        try {
            if (CommunityRootData.getDbManager().isDatabaseOk()) {
                addAnnouncements(this);
                addNewestLevels(this);

                return;
            }
        } catch (SQLException e) {
            LogHelper.error(getClass(), "Error while checking if the database is ok");
            LogHelper.error(getClass(), "\n" + e + "\n" + LogHelper.stackTraceToString(e));
        }

        //The method didn't return, so an error occured
        //TODO: Show error screen
    }

    private void addAnnouncements(ListBox parent) {
        final Panel announcementsTtlePanel = new Panel();
        announcementsTtlePanel.setOnInitAction(() -> {
            announcementsTtlePanel.setTopLeftPos(0, 0);
            announcementsTtlePanel.setBottomRightPos(0, 36);
            announcementsTtlePanel.setBackgroundColor(UIColor.matBlue());
        });
        parent.addChildComponent("announcementsTtlePanel", announcementsTtlePanel);

        final Label announcementsLabel = new Label();
        announcementsLabel.setOnInitAction(() -> {
            announcementsLabel.setTopLeftPos(24, 0);
            announcementsLabel.setBottomRightPos(-24, 0);
            announcementsLabel.setTopLeftAnchor(0, 0);
            announcementsLabel.setBottomRightAnchor(1, 1);
            announcementsLabel.setFont(HEADING_FONT);
            announcementsLabel.setText(LangManager.getItem("announcements"));
            announcementsLabel.setVerticalAlign(EnumVAlignment.centre);
            announcementsLabel.setTextColor(UIColor.matWhite());
        });
        announcementsTtlePanel.addChildComponent("announcementsLabel", announcementsLabel);

        int i = 0;
        for (CommunityAnnouncement announcement : CommunityRootData.getAnnouncementList()) {
            String title = announcement.getTitle();
            String body = announcement.getBody();

            final Panel announcementPanel = new Panel();
            announcementPanel.setOnInitAction(() -> {
                announcementPanel.setTopLeftPos(0, 0);
                announcementPanel.setBottomRightPos(0, 48);
                announcementPanel.setBackgroundColor(UIColor.transparent());
            });
            parent.addChildComponent("announcementPanel" + String.valueOf(i), announcementPanel);

            final Label announcementTitleLabel = new Label();
            announcementTitleLabel.setOnInitAction(() -> {
                announcementTitleLabel.setTopLeftPos(24, 0);
                announcementTitleLabel.setBottomRightPos(-24, 24);
                announcementTitleLabel.setTopLeftAnchor(0, 0);
                announcementTitleLabel.setBottomRightAnchor(1, 0);
                announcementTitleLabel.setFont(SUBHEADING_FONT);
                announcementTitleLabel.setText(title);
                announcementTitleLabel.setSoftWrap(true);
            });
            announcementPanel.addChildComponent("announcementTitleLabel", announcementTitleLabel);

            final Label announcementBodyLabel = new Label();
            announcementBodyLabel.setOnInitAction(() -> {
                announcementBodyLabel.setTopLeftPos(24, 24);
                announcementBodyLabel.setBottomRightPos(-24, 48);
                announcementBodyLabel.setTopLeftAnchor(0, 0);
                announcementBodyLabel.setBottomRightAnchor(1, 0);
                announcementBodyLabel.setText(body);
                announcementBodyLabel.setSoftWrap(true);
            });
            announcementPanel.addChildComponent("announcementBodyLabel", announcementBodyLabel);

            //Wrapping changed actions
            announcementTitleLabel.setOnWrappingChangedAction(() -> {
                announcementTitleLabel.height = 24 + (SUBHEADING_FONT.getLineHeight() * (announcementTitleLabel.wrapLines - 1));
                announcementBodyLabel.setTopLeftPos(24, 24 + (SUBHEADING_FONT.getLineHeight() * (announcementTitleLabel.wrapLines - 1)));
                announcementPanel.height = 24 + (SUBHEADING_FONT.getLineHeight() * (announcementTitleLabel.wrapLines - 1)) +
                        24 + (new DialogUITheme().labelFont).getLineHeight() * (announcementBodyLabel.wrapLines - 1);
                parent.reorganizeChildComponents();
            });

            announcementBodyLabel.setOnWrappingChangedAction(() -> {
                announcementBodyLabel.height = 24 + (new DialogUITheme().labelFont).getLineHeight() * (announcementBodyLabel.wrapLines - 1);
                announcementPanel.height = 24 + (SUBHEADING_FONT.getLineHeight() * (announcementTitleLabel.wrapLines - 1)) +
                        24 + (new DialogUITheme().labelFont).getLineHeight() * (announcementBodyLabel.wrapLines - 1);
                parent.reorganizeChildComponents();
            });

            i++;
        }

        final Component announcementsSpacer = new Component();
        announcementsSpacer.setOnInitAction(() -> {
            announcementsSpacer.setTopLeftPos(0, 0);
            announcementsSpacer.setBottomRightPos(0, 48);
        });
        parent.addChildComponent("announcementsSpacer", announcementsSpacer);
    }

    private void addNewestLevels(ListBox parent) {
        try {
            CommunityLevelList newestLevelsList = new CommunityLevelList(LangManager.getItem("newestLevels"), CommunityRootData.getDbManager().getNewestLevels(0, 50));
            newestLevelsList.setOnInitAction(() -> {
                newestLevelsList.setTopLeftPos(0, 0);
            });
            newestLevelsList.setOnSizeChangedAction(parent::reorganizeChildComponents);
            parent.addChildComponent("newestLevelsList", newestLevelsList);

        } catch (SQLException e) {
            LogHelper.error(SyncManager.class, "Error while getting newest levels");
            LogHelper.error(SyncManager.class, "\n" + e + "\n" + LogHelper.stackTraceToString(e));

            //TODO: Display error message
        }
    }

}
