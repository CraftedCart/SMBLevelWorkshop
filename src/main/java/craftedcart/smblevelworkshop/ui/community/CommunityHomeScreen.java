package craftedcart.smblevelworkshop.ui.community;

import craftedcart.smblevelworkshop.community.CommunityAnnouncement;
import craftedcart.smblevelworkshop.community.CommunityRootData;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.ui.DialogUITheme;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.Label;
import io.github.craftedcart.fluidui.component.ListBox;
import io.github.craftedcart.fluidui.component.Panel;
import org.newdawn.slick.UnicodeFont;

/**
 * @author CraftedCart
 *         Created on 06/10/2016 (DD/MM/YYYY)
 */
public class CommunityHomeScreen extends ListBox {

    private UnicodeFont headingFont;
    private UnicodeFont subHeadingFont;

    public CommunityHomeScreen() {
        init();
        postInit();

        headingFont = FontCache.getUnicodeFont("Roboto-Regular", 24);
        subHeadingFont = FontCache.getUnicodeFont("Roboto-Regular", 20);

        setTheme(new DialogUITheme());
        initComponents();
    }

    private void initComponents() {
        addAnnouncements(this);
    }

    private void addAnnouncements(ListBox parent) {
        final Label announcementsLabel = new Label();
        announcementsLabel.setOnInitAction(() -> {
            announcementsLabel.setTopLeftPos(0, 0);
            announcementsLabel.setBottomRightPos(0, 48);
            announcementsLabel.setFont(headingFont);
            announcementsLabel.setText(LangManager.getItem("announcements"));
        });
        parent.addChildComponent("announcementsLabel", announcementsLabel);

        int i = 0;
        for (CommunityAnnouncement announcement : CommunityRootData.getAnnouncementList()) {
            String title = announcement.getTitle();
            String body = announcement.getBody();

            final Label announcementTitleLabel = new Label();
            announcementTitleLabel.setOnInitAction(() -> {
                announcementTitleLabel.setTopLeftPos(0, 0);
                announcementTitleLabel.setBottomRightPos(0, 24);
                announcementTitleLabel.setFont(subHeadingFont);
                announcementTitleLabel.setText(title);
                announcementTitleLabel.setSoftWrap(true);
            });
            announcementTitleLabel.setOnWrappingChangedAction(() -> {
                announcementTitleLabel.height = 24 + (subHeadingFont.getLineHeight() * (announcementTitleLabel.wrapLines - 1));
                parent.reorganizeChildComponents();
            });
            parent.addChildComponent("announcementTitleLabel" + String.valueOf(i), announcementTitleLabel);

            final Label announcementBodyLabel = new Label();
            announcementBodyLabel.setOnInitAction(() -> {
                announcementBodyLabel.setTopLeftPos(0, 0);
                announcementBodyLabel.setBottomRightPos(0, 24);
                announcementBodyLabel.setText(body);
                announcementBodyLabel.setSoftWrap(true);
            });
            announcementBodyLabel.setOnWrappingChangedAction(() -> {
                announcementBodyLabel.height = 24 + (new DialogUITheme().labelFont).getLineHeight() * (announcementBodyLabel.wrapLines - 1);
                parent.reorganizeChildComponents();
            });
            parent.addChildComponent("announcementBodyLabel" + String.valueOf(i), announcementBodyLabel);

            i++;
        }
    }
}
