package craftedcart.smblevelworkshop.ui.community;

import craftedcart.smblevelworkshop.community.CommunityLevel;
import craftedcart.smblevelworkshop.ui.theme.DialogUITheme;
import io.github.craftedcart.fluidui.FluidUIScreen;
import io.github.craftedcart.fluidui.FontCache;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.uiaction.UIAction;
import io.github.craftedcart.fluidui.util.EnumVAlignment;
import io.github.craftedcart.fluidui.util.UIColor;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.eclipse.jgit.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.newdawn.slick.UnicodeFont;

import java.util.List;
import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 08/10/2016 (DD/MM/YYYY)
 *
 *         This will set its own bottom right pos!
 */
public class CommunityLevelList extends ListBox {

    public String heading;
    public List<CommunityLevel> levelList;
    @Nullable public UIAction onSizeChangedAction;
    @NotNull FluidUIScreen uiScreen;

    private final UnicodeFont HEADING_FONT;
    private final UnicodeFont SUBHEADING_FONT;

    public static final double LEVEL_ENTRY_HEIGHT = 72;

    public CommunityLevelList(String heading, List<CommunityLevel> levelList, @NotNull FluidUIScreen uiScreen) {
        init();
        postInit();

        this.heading = heading;
        this.levelList = levelList;
        this.uiScreen = uiScreen;

        HEADING_FONT = FontCache.getUnicodeFont("Roboto-Regular", 24);
        SUBHEADING_FONT = FontCache.getUnicodeFont("Roboto-Regular", 20);

        setTheme(new DialogUITheme());
//        setCanScroll(false);
        initComponents();
    }

    public void reload() {
        clearChildComponents();
        initComponents();
    }

    private void initComponents() {
        setCanScroll(false);

        addLevels(this);

        if (bottomRightPos != null) {
            setBottomRightPos(bottomRightPos.x, bottomRightPos.y + 36 + LEVEL_ENTRY_HEIGHT * levelList.size());
        } else {
            setBottomRightPos(0, 36 + LEVEL_ENTRY_HEIGHT * levelList.size());
        }

        preDraw(); //Recalculate height

        if (onSizeChangedAction != null) {
            onSizeChangedAction.execute();
        }
    }

    private void addLevels(ListBox parent) {
        final Panel headingPanel = new Panel();
        headingPanel.setOnInitAction(() -> {
            headingPanel.setTopLeftPos(0, 0);
            headingPanel.setBottomRightPos(0, 36);
            headingPanel.setBackgroundColor(UIColor.matBlue());
        });
        parent.addChildComponent("headingPanel", headingPanel);

        final Label headingLabel = new Label();
        headingLabel.setOnInitAction(() -> {
            headingLabel.setTopLeftPos(24, 0);
            headingLabel.setBottomRightPos(-24, 0);
            headingLabel.setTopLeftAnchor(0, 0);
            headingLabel.setBottomRightAnchor(1, 1);
            headingLabel.setFont(HEADING_FONT);
            headingLabel.setText(heading);
            headingLabel.setVerticalAlign(EnumVAlignment.centre);
            headingLabel.setTextColor(UIColor.matWhite());
        });
        headingPanel.addChildComponent("headingLabel", headingLabel);

        int i = 0;
        for (CommunityLevel level : levelList) {
            String title = level.getName();
            String shortDescription = level.getShortDescription();
            String creator = String.format("%s (%s)", level.getUserDisplayName(), level.getUsername());

            final Button levelButton = new Button();
            levelButton.setOnInitAction(() -> {
                levelButton.setTopLeftPos(0, 0);
                levelButton.setBottomRightPos(0, LEVEL_ENTRY_HEIGHT);
                levelButton.setBackgroundIdleColor(UIColor.transparent());
                levelButton.setBackgroundActiveColor(UIColor.matGrey(0.3));
                levelButton.setBackgroundHitColor(UIColor.matGrey(0.5));
            });
            levelButton.setOnLMBAction(() -> uiScreen.setOverlayUiScreen(new CommunityOverlayLevelScreen(level)));
            parent.addChildComponent("levelButton" + String.valueOf(i), levelButton);

            final Label levelTitleLabel = new Label();
            levelTitleLabel.setOnInitAction(() -> {
                levelTitleLabel.setTopLeftPos(24, 0);
                levelTitleLabel.setBottomRightPos(-24, 24);
                levelTitleLabel.setTopLeftAnchor(0, 0);
                levelTitleLabel.setBottomRightAnchor(1, 0);
                levelTitleLabel.setFont(SUBHEADING_FONT);
                levelTitleLabel.setText(title);
            });
            levelButton.addChildComponent("levelTitleLabel", levelTitleLabel);

            final Label levelShortDescLabel = new Label();
            levelShortDescLabel.setOnInitAction(() -> {
                levelShortDescLabel.setTopLeftPos(24, 24);
                levelShortDescLabel.setBottomRightPos(-24, 48);
                levelShortDescLabel.setTopLeftAnchor(0, 0);
                levelShortDescLabel.setBottomRightAnchor(1, 0);
                levelShortDescLabel.setText(shortDescription);
                levelShortDescLabel.setTextColor(UIColor.matGrey900(0.6));
            });
            levelButton.addChildComponent("levelShortDescLabel", levelShortDescLabel);

            final Label levelCreatorLabel = new Label();
            levelCreatorLabel.setOnInitAction(() -> {
                levelCreatorLabel.setTopLeftPos(24, 48);
                levelCreatorLabel.setBottomRightPos(-24, 72);
                levelCreatorLabel.setTopLeftAnchor(0, 0);
                levelCreatorLabel.setBottomRightAnchor(1, 0);
                levelCreatorLabel.setText(creator);
            });
            levelButton.addChildComponent("levelCreatorLabel", levelCreatorLabel);

            i++;
        }
    }

    public void setOnSizeChangedAction(UIAction onSizeChangedAction) {
        this.onSizeChangedAction = onSizeChangedAction;
    }

}
