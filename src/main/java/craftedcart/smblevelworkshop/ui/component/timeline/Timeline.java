package craftedcart.smblevelworkshop.ui.component.timeline;

import craftedcart.smblevelworkshop.SMBLWSettings;
import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.animation.AnimData;
import craftedcart.smblevelworkshop.animation.BufferedAnimData;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.ui.DefaultUITheme;
import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.util.MathUtils;
import io.github.craftedcart.fluidui.component.*;
import io.github.craftedcart.fluidui.plugin.AbstractComponentPlugin;
import io.github.craftedcart.fluidui.util.EnumHAlignment;
import io.github.craftedcart.fluidui.util.PosXY;
import io.github.craftedcart.fluidui.util.UIColor;
import io.github.craftedcart.fluidui.util.UIUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author CraftedCart
 *         Created on 30/10/2016 (DD/MM/YYYY)
 */
public class Timeline extends Panel {

    private static final double TIMELINE_PADDING = 24;

    private static final int POS_X_KEYFRAME_Y_POS = 12;
    private static final int POS_Y_KEYFRAME_Y_POS = 28;
    private static final int POS_Z_KEYFRAME_Y_POS = 44;

    private static final int ROT_X_KEYFRAME_Y_POS = 60;
    private static final int ROT_Y_KEYFRAME_Y_POS = 76;
    private static final int ROT_Z_KEYFRAME_Y_POS = 92;

    private MainScreen mainScreen;

    private TimelinePlayhead posPanel;
    private TimelinePlayhead cursorPosPanel;
    private TextField percentTextField;
    private TextField maxTimeTextField;
    private TextField timeTextField;

    private DecimalFormat df;

    private String secondsSuffix; //Used for caching instead of fetching every frame

    @Nullable private Map<String, AnimData> objectAnimDataMap;
    private Collection<String> selectedObjects = new HashSet<>();

    public Timeline(@NotNull MainScreen mainScreen) {
        init(mainScreen);
        postInit();
    }

    public void init(MainScreen mainScreen) {
        setTheme(new DefaultUITheme());

        super.init();

        this.mainScreen = mainScreen;

        df = new DecimalFormat("0.00");

        posPanel = new TimelinePlayhead();
        cursorPosPanel = new TimelinePlayhead();
        percentTextField = new TextField();
        maxTimeTextField = new TextField();
        timeTextField = new TextField();

        secondsSuffix = LangManager.getItem("secondsAbbr");

        final Panel timelinePanel = new Panel();
        timelinePanel.setOnInitAction(() -> {
            timelinePanel.setTopLeftPos(0, 0);
            timelinePanel.setBottomRightPos(0, -24);
            timelinePanel.setTopLeftAnchor(0, 0);
            timelinePanel.setBottomRightAnchor(1, 1);
            timelinePanel.setBackgroundColor(UIColor.transparent());
        });
        timelinePanel.addPlugin(new TimelinePlugin());
        addChildComponent("timelinePanel", timelinePanel);

        final Image posImage = new Image();
        posImage.setOnInitAction(() -> {
            posImage.setTopLeftPos(0, POS_Y_KEYFRAME_Y_POS - 2);
            posImage.setBottomRightPos(24, POS_Y_KEYFRAME_Y_POS + 22);
            posImage.setTopLeftAnchor(0, 0);
            posImage.setBottomRightAnchor(0, 0);
            posImage.setTexture(ResourceManager.getTexture("image/arrowAll").getTexture());
        });
        addChildComponent("posImage", posImage);

        final Image rotImage = new Image();
        rotImage.setOnInitAction(() -> {
            rotImage.setTopLeftPos(0, ROT_Y_KEYFRAME_Y_POS - 2);
            rotImage.setBottomRightPos(24, ROT_Y_KEYFRAME_Y_POS + 22);
            rotImage.setTopLeftAnchor(0, 0);
            rotImage.setBottomRightAnchor(0, 0);
            rotImage.setTexture(ResourceManager.getTexture("image/rotate").getTexture());
        });
        addChildComponent("rotImage", rotImage);

        final Panel timelineUseablePanel = new Panel();
        timelineUseablePanel.setOnInitAction(() -> {
            timelineUseablePanel.setTopLeftPos(TIMELINE_PADDING, 0);
            timelineUseablePanel.setBottomRightPos(-TIMELINE_PADDING, 0);
            timelineUseablePanel.setTopLeftAnchor(0, 0);
            timelineUseablePanel.setBottomRightAnchor(1, 1);
            timelineUseablePanel.setBackgroundColor(UIColor.matGrey(0.25));
        });
        timelineUseablePanel.addPlugin(new TimelineUseablePlugin());
        timelinePanel.addChildComponent("timelineUseablePanel", timelineUseablePanel);

        //Defined at class level
        posPanel.setOnInitAction(() -> {
            posPanel.setTopLeftPos(-1, 0);
            posPanel.setBottomRightPos(1, 0);
            posPanel.setTopLeftAnchor(0, 0);
            posPanel.setBottomRightAnchor(0, 1);
            posPanel.setBackgroundColor(UIColor.matRed());
            posPanel.setRightText(df.format(0) + "%");
            posPanel.setLeftText(df.format(0) + secondsSuffix);
        });
        timelineUseablePanel.addChildComponent("posPanel", posPanel);

        //Defined at class level
        cursorPosPanel.setOnInitAction(() -> {
            cursorPosPanel.setTopLeftPos(-1, 0);
            cursorPosPanel.setBottomRightPos(1, 0);
            cursorPosPanel.setTopLeftAnchor(0, 0);
            cursorPosPanel.setBottomRightAnchor(0, 1);
            cursorPosPanel.setBackgroundColor(UIColor.matRed(0.5));
            cursorPosPanel.setVisible(false);
        });
        timelineUseablePanel.addChildComponent("cursorPosPanel", cursorPosPanel);

        final Panel headingPanel = new Panel();
        headingPanel.setOnInitAction(() -> {
            headingPanel.setTopLeftPos(0, -24);
            headingPanel.setBottomRightPos(0, 0);
            headingPanel.setTopLeftAnchor(0, 1);
            headingPanel.setBottomRightAnchor(1, 1);
            headingPanel.setBackgroundColor(UIColor.matGrey900(0.75));
        });
        addChildComponent("headingPanel", headingPanel);

        final Label headingLabel = new Label();
        headingLabel.setOnInitAction(() -> {
            headingLabel.setTopLeftPos(-256, 0);
            headingLabel.setBottomRightPos(256, 0);
            headingLabel.setTopLeftAnchor(0.5, 0);
            headingLabel.setBottomRightAnchor(0.5, 1);
            headingLabel.setHorizontalAlign(EnumHAlignment.centre);
            headingLabel.setText(LangManager.getItem("timeline"));
        });
        headingPanel.addChildComponent("headingLabel", headingLabel);

        final Button toStartButton = new Button();
        toStartButton.setOnInitAction(() -> {
            toStartButton.setTopLeftPos(0, 0);
            toStartButton.setBottomRightPos(24, 0);
            toStartButton.setTopLeftAnchor(0, 0);
            toStartButton.setBottomRightAnchor(0, 1);
            toStartButton.setTexture(ResourceManager.getTexture("image/toStart").getTexture());
        });
        toStartButton.setOnLMBAction(() -> {
            if (ProjectManager.getCurrentProject().clientLevelData != null) {
                ProjectManager.getCurrentProject().clientLevelData.setTimelinePos(0.0f);
            } else {
                mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }
        });
        headingPanel.addChildComponent("toStartButton", toStartButton);

        final Button fastRewindButton = new Button();
        fastRewindButton.setOnInitAction(() -> {
            fastRewindButton.setTopLeftPos(24, 0);
            fastRewindButton.setBottomRightPos(48, 0);
            fastRewindButton.setTopLeftAnchor(0, 0);
            fastRewindButton.setBottomRightAnchor(0, 1);
            fastRewindButton.setTexture(ResourceManager.getTexture("image/fastRewind").getTexture());
        });
        fastRewindButton.setOnLMBAction(() -> {
                ClientLevelData cld = ProjectManager.getCurrentProject().clientLevelData;
            if (cld != null) {
                cld.setPlaybackSpeed(cld.getPlaybackSpeed() - 2.0f);
            } else {
                mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }
        });
        headingPanel.addChildComponent("fastRewindButton", fastRewindButton);

        final Button rewindButton = new Button();
        rewindButton.setOnInitAction(() -> {
            rewindButton.setTopLeftPos(48, 0);
            rewindButton.setBottomRightPos(72, 0);
            rewindButton.setTopLeftAnchor(0, 0);
            rewindButton.setBottomRightAnchor(0, 1);
            rewindButton.setTexture(ResourceManager.getTexture("image/rewind").getTexture());
        });
        rewindButton.setOnLMBAction(() -> {
            ClientLevelData cld = ProjectManager.getCurrentProject().clientLevelData;
            if (cld != null) {
                cld.setPlaybackSpeed(-1.0f);
            } else {
                mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }
        });
        headingPanel.addChildComponent("rewindButton", rewindButton);

        final Button pauseButton = new Button();
        pauseButton.setOnInitAction(() -> {
            pauseButton.setTopLeftPos(72, 0);
            pauseButton.setBottomRightPos(96, 0);
            pauseButton.setTopLeftAnchor(0, 0);
            pauseButton.setBottomRightAnchor(0, 1);
            pauseButton.setTexture(ResourceManager.getTexture("image/pause").getTexture());
        });
        pauseButton.setOnLMBAction(() -> {
            ClientLevelData cld = ProjectManager.getCurrentProject().clientLevelData;
            if (cld != null) {
                cld.setPlaybackSpeed(0.0f);
            } else {
                mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }
        });
        headingPanel.addChildComponent("pauseButton", pauseButton);

        final Button playButton = new Button();
        playButton.setOnInitAction(() -> {
            playButton.setTopLeftPos(96, 0);
            playButton.setBottomRightPos(120, 0);
            playButton.setTopLeftAnchor(0, 0);
            playButton.setBottomRightAnchor(0, 1);
            playButton.setTexture(ResourceManager.getTexture("image/play").getTexture());
        });
        playButton.setOnLMBAction(() -> {
            ClientLevelData cld = ProjectManager.getCurrentProject().clientLevelData;
            if (cld != null) {
                cld.setPlaybackSpeed(1.0f);
            } else {
                mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }
        });
        headingPanel.addChildComponent("playButton", playButton);

        final Button fastForwardButton = new Button();
        fastForwardButton.setOnInitAction(() -> {
            fastForwardButton.setTopLeftPos(120, 0);
            fastForwardButton.setBottomRightPos(144, 0);
            fastForwardButton.setTopLeftAnchor(0, 0);
            fastForwardButton.setBottomRightAnchor(0, 1);
            fastForwardButton.setTexture(ResourceManager.getTexture("image/fastForward").getTexture());
        });
        fastForwardButton.setOnLMBAction(() -> {
            ClientLevelData cld = ProjectManager.getCurrentProject().clientLevelData;
            if (cld != null) {
                cld.setPlaybackSpeed(cld.getPlaybackSpeed() + 2.0f);
            } else {
                mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }
        });
        headingPanel.addChildComponent("fastForwardButton", fastForwardButton);

        final Button toEndButton = new Button();
        toEndButton.setOnInitAction(() -> {
            toEndButton.setTopLeftPos(144, 0);
            toEndButton.setBottomRightPos(168, 0);
            toEndButton.setTopLeftAnchor(0, 0);
            toEndButton.setBottomRightAnchor(0, 1);
            toEndButton.setTexture(ResourceManager.getTexture("image/toEnd").getTexture());
        });
        toEndButton.setOnLMBAction(() -> {
            if (ProjectManager.getCurrentProject().clientLevelData != null) {
                ProjectManager.getCurrentProject().clientLevelData.setTimelinePos(1.0f);
            } else {
                mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }
        });
        headingPanel.addChildComponent("toEndButton", toEndButton);

        final Label percentLabel = new Label();
        percentLabel.setOnInitAction(() -> {
            percentLabel.setTopLeftPos(-24, 0);
            percentLabel.setBottomRightPos(0, 0);
            percentLabel.setTopLeftAnchor(1, 0);
            percentLabel.setBottomRightAnchor(1, 1);
            percentLabel.setText("%");
        });
        headingPanel.addChildComponent("percentLabel", percentLabel);

        //Defined at class level
        percentTextField.setOnInitAction(() -> {
            percentTextField.setTopLeftPos(-88, 0);
            percentTextField.setBottomRightPos(-24, 0);
            percentTextField.setTopLeftAnchor(1, 0);
            percentTextField.setBottomRightAnchor(1, 1);
            percentTextField.setInputRegexCheck("[0-9.]");
            percentTextField.setValue(df.format(0));
        });
        percentTextField.setOnReturnAction(() -> percentTextField.setSelected(false));
        percentTextField.setOnValueConfirmedAction(() -> {
            try {
                float newValue = Float.parseFloat(percentTextField.value);
                if (ProjectManager.getCurrentProject().clientLevelData != null) {
                    ProjectManager.getCurrentProject().clientLevelData.setTimelinePos(newValue / 100.0f);
                } else {
                    mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
                }
            } catch (NumberFormatException e) {
                mainScreen.notify(LangManager.getItem("invalidNumber"), UIColor.matRed());
            }

            if (ProjectManager.getCurrentProject().clientLevelData != null) {
                updatePercent(ProjectManager.getCurrentProject().clientLevelData.getTimelinePos());
            }
        });
        mainScreen.addTextField(percentTextField);
        headingPanel.addChildComponent("percentTextField", percentTextField);

        final Label secondsLabel = new Label();
        secondsLabel.setOnInitAction(() -> {
            secondsLabel.setTopLeftPos(-112, 0);
            secondsLabel.setBottomRightPos(-88, 0);
            secondsLabel.setTopLeftAnchor(1, 0);
            secondsLabel.setBottomRightAnchor(1, 1);
            secondsLabel.setText(secondsSuffix);
        });
        headingPanel.addChildComponent("secondsLabel", secondsLabel);

        //Defined at class level
        maxTimeTextField.setOnInitAction(() -> {
            maxTimeTextField.setTopLeftPos(-176, 0);
            maxTimeTextField.setBottomRightPos(-112, 0);
            maxTimeTextField.setTopLeftAnchor(1, 0);
            maxTimeTextField.setBottomRightAnchor(1, 1);
            maxTimeTextField.setInputRegexCheck("[0-9.]");
            maxTimeTextField.setValue(df.format(0));
        });
        maxTimeTextField.setOnReturnAction(() -> maxTimeTextField.setSelected(false));
        maxTimeTextField.setOnValueConfirmedAction(() -> {
            try {
                float newValue = Float.parseFloat(maxTimeTextField.value);
                if (ProjectManager.getCurrentProject().clientLevelData != null) {
                    if (newValue > 0) {
                        ProjectManager.getCurrentProject().clientLevelData.getLevelData().setMaxTime(newValue);
                    } else {
                        mainScreen.notify(LangManager.getItem("numberMustBeGreaterThanZero"), UIColor.matRed());
                    }
                } else {
                    mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
                }
            } catch (NumberFormatException e) {
                mainScreen.notify(LangManager.getItem("invalidNumber"), UIColor.matRed());
            }

            if (ProjectManager.getCurrentProject().clientLevelData != null) {
                updateMaxAndLeadInTime(ProjectManager.getCurrentProject().clientLevelData.getLevelData().getMaxTime(),
                        ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime());
            }
        });
        mainScreen.addTextField(maxTimeTextField);
        headingPanel.addChildComponent("maxTimeTextField", maxTimeTextField);

        final Label secondsOutOfLabel = new Label();
        secondsOutOfLabel.setOnInitAction(() -> {
            secondsOutOfLabel.setTopLeftPos(-200, 0);
            secondsOutOfLabel.setBottomRightPos(-176, 0);
            secondsOutOfLabel.setTopLeftAnchor(1, 0);
            secondsOutOfLabel.setBottomRightAnchor(1, 1);
            secondsOutOfLabel.setText(LangManager.getItem("secondsOutOfAbbr"));
        });
        headingPanel.addChildComponent("secondsOutOfLabel", secondsOutOfLabel);

        //Defined at class level
        timeTextField.setOnInitAction(() -> {
            timeTextField.setTopLeftPos(-264, 0);
            timeTextField.setBottomRightPos(-200, 0);
            timeTextField.setTopLeftAnchor(1, 0);
            timeTextField.setBottomRightAnchor(1, 1);
            timeTextField.setInputRegexCheck("[0-9.-]");
            timeTextField.setValue(df.format(0));
        });
        timeTextField.setOnReturnAction(() -> timeTextField.setSelected(false));
        timeTextField.setOnValueConfirmedAction(() -> {
            try {
                float newValue = Float.parseFloat(timeTextField.value);
                if (ProjectManager.getCurrentProject().clientLevelData != null) {
                    if (newValue >= -ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime()) {
                        if (newValue <= ProjectManager.getCurrentProject().clientLevelData.getLevelData().getMaxTime()) {
                            ProjectManager.getCurrentProject().clientLevelData.setTimelinePosSeconds(newValue);
                        } else {
                            mainScreen.notify(LangManager.getItem("numberMustBeLessThanMaxTime"), UIColor.matRed());
                        }
                    } else {
                        mainScreen.notify(String.format(LangManager.getItem("numberMustBeGreaterThanOrEqualTo"),
                                -ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime()), UIColor.matRed());
                    }
                } else {
                    mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
                }
            } catch (NumberFormatException e) {
                mainScreen.notify(LangManager.getItem("invalidNumber"), UIColor.matRed());
            }

            if (ProjectManager.getCurrentProject().clientLevelData != null) {
                updatePercent(ProjectManager.getCurrentProject().clientLevelData.getTimelinePos());
            }
        });
        mainScreen.addTextField(timeTextField);
        headingPanel.addChildComponent("timeTextField", timeTextField);

    }

    public void updatePercent(Float percent) {
        float seconds =
                percent *
                        (ProjectManager.getCurrentProject().clientLevelData.getLevelData().getMaxTime() + ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime()) -
                        ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime();

        posPanel.setTopLeftAnchor(percent, 0);
        posPanel.setBottomRightAnchor(percent, 1);
        posPanel.setRightText(df.format(percent * 100) + "%");
        posPanel.setLeftText(df.format(seconds) + secondsSuffix);

        percentTextField.setValue(df.format(percent * 100));
        timeTextField.setValue(df.format(
                percent *
                (ProjectManager.getCurrentProject().clientLevelData.getLevelData().getMaxTime() + ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime()) -
                ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime()
        ));
    }

    public void updateMaxAndLeadInTime(float maxTime, float leadInTime) {
        maxTimeTextField.setValue(df.format(maxTime));
        posPanel.setLeftText(df.format(ProjectManager.getCurrentProject().clientLevelData.getTimelinePos() * (maxTime + leadInTime) - leadInTime) + secondsSuffix);
    }

    private class TimelinePlugin extends AbstractComponentPlugin {

        private PosXY selectionStartPos = new PosXY();

        @Override
        public void onPreDraw() {
            if (linkedComponent.mouseOver) {

                if (Mouse.isButtonDown(1)) { //If RMB down

                    //Calculate top left and bottom right pos of selection
                    PosXY topLeftPos = new PosXY(
                            Math.min(selectionStartPos.x, linkedComponent.mousePos.x),
                            Math.min(selectionStartPos.y, linkedComponent.mousePos.y));
                    PosXY bottomRightPos = new PosXY(
                            Math.max(selectionStartPos.x, linkedComponent.mousePos.x),
                            Math.max(selectionStartPos.y, linkedComponent.mousePos.y));
                    
                    //Draw selection box
                    UIUtils.drawQuad(topLeftPos, new PosXY(bottomRightPos.x, bottomRightPos.y), UIColor.matBlue(0.25));
                    UIUtils.drawQuad(topLeftPos, new PosXY(bottomRightPos.x, topLeftPos.y + 2), UIColor.matBlue());
                    UIUtils.drawQuad(topLeftPos, new PosXY(topLeftPos.x + 2, bottomRightPos.y), UIColor.matBlue());
                    UIUtils.drawQuad(new PosXY(bottomRightPos.x - 2, topLeftPos.y),
                            new PosXY(bottomRightPos.x, bottomRightPos.y), UIColor.matBlue());
                    UIUtils.drawQuad(new PosXY(topLeftPos.x, bottomRightPos.y - 2), bottomRightPos, UIColor.matBlue());

                    cursorPosPanel.setVisible(false);

                } else {
                    //<editor-fold desc="Manage cursorPosPanel">
                    float percent = 0;
                    if (linkedComponent.mousePos != null) {
                        percent = (float) MathUtils.clamp(((linkedComponent.mousePos.x - linkedComponent.topLeftPx.x) /
                                        (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                0, 1);

                        if (Window.isAltDown()) { //Alt to snap - Shift for precision
                            float snapTo = Window.isShiftDown() ? SMBLWSettings.animSnapShift : SMBLWSettings.animSnap;
                            float roundMultiplier = 1.0f / snapTo;
                            percent = Math.round(percent * roundMultiplier) / roundMultiplier; //newTimeSnapped
                        }
                    }

                    double seconds = 0;
                    if (ProjectManager.getCurrentProject().clientLevelData != null) {
                        seconds =
                                percent *
                                (ProjectManager.getCurrentProject().clientLevelData.getLevelData().getMaxTime()
                                        + ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime()) -
                                        ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime();
                    }

                    cursorPosPanel.setTopLeftAnchor(percent, 0);
                    cursorPosPanel.setBottomRightAnchor(percent, 1);
                    cursorPosPanel.setRightText(df.format(percent * 100) + "%");
                    cursorPosPanel.setLeftText(df.format(seconds) + secondsSuffix);
                    cursorPosPanel.setVisible(true);

                    if (Mouse.isButtonDown(0)) { //If LMB down
                        if (ProjectManager.getCurrentProject().clientLevelData != null) {
                            ProjectManager.getCurrentProject().clientLevelData.setTimelinePos(percent);
                        }
                    }
                    //</editor-fold>
                }

            } else {
                cursorPosPanel.setVisible(false);
            }
        }

        @Override
        public void onClickChildComponent(int button, PosXY mousePos) {
            if (ProjectManager.getCurrentProject().clientLevelData == null) {
                mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }

            if (button == 1) { //RMB: Start selection
                startSelection(mousePos);
            }
        }

        @Override
        public void onClick(int button, PosXY mousePos) {
            if (button == 1) { //RMB: Start selection
                startSelection(mousePos);
            }
        }

        @Override
        public void onClickReleased(int button, PosXY mousePos) {
            if (button == 1) { //RMB released: End selection
                endSelection(mousePos);
            }
        }

        @Override
        public void onClickChildComponentReleased(int button, PosXY mousePos) {
            if (button == 1) { //RMB released: End selection
                endSelection(mousePos);
            }
        }

        private void startSelection(PosXY mousePos) {
            selectionStartPos = mousePos;
            if (!Window.isShiftDown() && ProjectManager.getCurrentProject().clientLevelData != null) { //Deselect all keyframes if shift not down
                ProjectManager.getCurrentProject().clientLevelData.clearSelectedKeyframes();
            }
        }

        private void endSelection(PosXY mousePos) {
            if (ProjectManager.getCurrentProject().clientLevelData != null) {
                PosXY topLeftPos = new PosXY(
                        Math.min(selectionStartPos.x, mousePos.x),
                        Math.min(selectionStartPos.y, mousePos.y));
                PosXY bottomRightPos = new PosXY(
                        Math.max(selectionStartPos.x, mousePos.x),
                        Math.max(selectionStartPos.y, mousePos.y));

                //<editor-fold desc="Pos X">
                if (MathUtils.isInRange(linkedComponent.topLeftPx.y + POS_X_KEYFRAME_Y_POS + 10, topLeftPos.y, bottomRightPos.y)) { //Are X pos keyframes in selection Y range
                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                        if (ProjectManager.getCurrentProject().clientLevelData.getLevelData().doesObjectHaveAnimData(name)) {

                            float minPercent = (float) MathUtils.clamp(((topLeftPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            float maxPercent = (float) MathUtils.clamp(((bottomRightPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            ProjectManager.getCurrentProject().clientLevelData.selectPosXKeyframesInRange(name, minPercent, maxPercent);
                        }
                    }
                }
                //</editor-fold>

                //<editor-fold desc="Pos Y">
                if (MathUtils.isInRange(linkedComponent.topLeftPx.y + POS_Y_KEYFRAME_Y_POS + 10, topLeftPos.y, bottomRightPos.y)) { //Are Y pos keyframes in selection Y range
                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                        if (ProjectManager.getCurrentProject().clientLevelData.getLevelData().doesObjectHaveAnimData(name)) {

                            float minPercent = (float) MathUtils.clamp(((topLeftPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            float maxPercent = (float) MathUtils.clamp(((bottomRightPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            ProjectManager.getCurrentProject().clientLevelData.selectPosYKeyframesInRange(name, minPercent, maxPercent);
                        }
                    }
                }
                //</editor-fold>

                //<editor-fold desc="Pos Z">
                if (MathUtils.isInRange(linkedComponent.topLeftPx.y + POS_Z_KEYFRAME_Y_POS + 10, topLeftPos.y, bottomRightPos.y)) { //Are Y pos keyframes in selection Y range
                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                        if (ProjectManager.getCurrentProject().clientLevelData.getLevelData().doesObjectHaveAnimData(name)) {

                            float minPercent = (float) MathUtils.clamp(((topLeftPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            float maxPercent = (float) MathUtils.clamp(((bottomRightPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            ProjectManager.getCurrentProject().clientLevelData.selectPosZKeyframesInRange(name, minPercent, maxPercent);
                        }
                    }
                }
                //</editor-fold>

                //<editor-fold desc="Rot X">
                if (MathUtils.isInRange(linkedComponent.topLeftPx.y + ROT_X_KEYFRAME_Y_POS + 10, topLeftPos.y, bottomRightPos.y)) { //Are X rot keyframes in selection Y range
                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                        if (ProjectManager.getCurrentProject().clientLevelData.getLevelData().doesObjectHaveAnimData(name)) {

                            float minPercent = (float) MathUtils.clamp(((topLeftPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            float maxPercent = (float) MathUtils.clamp(((bottomRightPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            ProjectManager.getCurrentProject().clientLevelData.selectRotXKeyframesInRange(name, minPercent, maxPercent);
                        }
                    }
                }
                //</editor-fold>

                //<editor-fold desc="Rot Y">
                if (MathUtils.isInRange(linkedComponent.topLeftPx.y + ROT_Y_KEYFRAME_Y_POS + 10, topLeftPos.y, bottomRightPos.y)) { //Are Y rot keyframes in selection Y range
                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                        if (ProjectManager.getCurrentProject().clientLevelData.getLevelData().doesObjectHaveAnimData(name)) {

                            float minPercent = (float) MathUtils.clamp(((topLeftPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            float maxPercent = (float) MathUtils.clamp(((bottomRightPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            ProjectManager.getCurrentProject().clientLevelData.selectRotYKeyframesInRange(name, minPercent, maxPercent);
                        }
                    }
                }
                //</editor-fold>

                //<editor-fold desc="Rot Z">
                if (MathUtils.isInRange(linkedComponent.topLeftPx.y + ROT_Z_KEYFRAME_Y_POS + 10, topLeftPos.y, bottomRightPos.y)) { //Are Y rot keyframes in selection Y range
                    for (String name : ProjectManager.getCurrentProject().clientLevelData.getSelectedObjects()) {
                        if (ProjectManager.getCurrentProject().clientLevelData.getLevelData().doesObjectHaveAnimData(name)) {

                            float minPercent = (float) MathUtils.clamp(((topLeftPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            float maxPercent = (float) MathUtils.clamp(((bottomRightPos.x - linkedComponent.topLeftPx.x) /
                                            (linkedComponent.width - (TIMELINE_PADDING * 2))) - TIMELINE_PADDING / linkedComponent.width,
                                    0, 1);

                            ProjectManager.getCurrentProject().clientLevelData.selectRotZKeyframesInRange(name, minPercent, maxPercent);
                        }
                    }
                }
                //</editor-fold>
            }
        }

    }

    private class TimelineUseablePlugin extends AbstractComponentPlugin {

        private Texture keyframeTex;
        private Texture keyframeSelectionTex;

        @Override
        public void onPostInit() {
            keyframeTex = ResourceManager.getTexture("image/keyframe").getTexture();
            keyframeSelectionTex = ResourceManager.getTexture("image/keyframeSelected").getTexture();
        }

        @Override
        public void onPreDraw() {
            if (objectAnimDataMap != null) {
                drawKeyframesBackgrounds();
                drawKeyframes();
            }
            drawBlocks();
        }

        /**
         * Draws the line between the first and last keyframes
         */
        private void drawKeyframesBackgrounds() {
            for (Map.Entry<String, AnimData> entry : objectAnimDataMap.entrySet()) {
                String name = entry.getKey();
                AnimData ad = entry.getValue();
                Map<String, BufferedAnimData> animDataBufferMap = ProjectManager.getCurrentProject().clientLevelData.getAnimDataBufferMap();
                if (animDataBufferMap.containsKey(entry.getKey())) {
                    ad = ad.mergeWithCopy(animDataBufferMap.get(entry.getKey()).getTransformedAnimData());
                }

                //<editor-fold desc="Pos">
                if (ad.getPosXFrames().size() > 0) {
                    float low = ad.getPosXFrames().firstKey();
                    float high = ad.getPosXFrames().lastKey();

                    PosXY pos = linkedComponent.topLeftPx.add(0, POS_X_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with less transparency
                        UIColor.matRed(0.25).bindColor();
                    } else {
                        UIColor.matRed(0.05).bindColor();
                    }

                    UIUtils.drawQuad(pos.add(linkedComponent.width * low, 8), pos.add(linkedComponent.width * high, 12));
                }

                if (ad.getPosYFrames().size() > 0) {
                    float low = ad.getPosYFrames().firstKey();
                    float high = ad.getPosYFrames().lastKey();

                    PosXY pos = linkedComponent.topLeftPx.add(0, POS_Y_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with less transparency
                        UIColor.matGreen(0.25).bindColor();
                    } else {
                        UIColor.matGreen(0.05).bindColor();
                    }

                    UIUtils.drawQuad(pos.add(linkedComponent.width * low, 8), pos.add(linkedComponent.width * high, 12));
                }

                if (ad.getPosZFrames().size() > 0) {
                    float low = ad.getPosZFrames().firstKey();
                    float high = ad.getPosZFrames().lastKey();

                    PosXY pos = linkedComponent.topLeftPx.add(0, POS_Z_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with less transparency
                        UIColor.matBlue(0.25).bindColor();
                    } else {
                        UIColor.matBlue(0.05).bindColor();
                    }

                    UIUtils.drawQuad(pos.add(linkedComponent.width * low, 8), pos.add(linkedComponent.width * high, 12));
                }
                //</editor-fold>

                //<editor-fold desc="Rot">
                if (ad.getRotXFrames().size() > 0) {
                    float low = ad.getRotXFrames().firstKey();
                    float high = ad.getRotXFrames().lastKey();

                    PosXY rot = linkedComponent.topLeftPx.add(0, ROT_X_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with less transparency
                        UIColor.matRed(0.25).bindColor();
                    } else {
                        UIColor.matRed(0.05).bindColor();
                    }

                    UIUtils.drawQuad(rot.add(linkedComponent.width * low, 8), rot.add(linkedComponent.width * high, 12));
                }

                if (ad.getRotYFrames().size() > 0) {
                    float low = ad.getRotYFrames().firstKey();
                    float high = ad.getRotYFrames().lastKey();

                    PosXY rot = linkedComponent.topLeftPx.add(0, ROT_Y_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with less transparency
                        UIColor.matGreen(0.25).bindColor();
                    } else {
                        UIColor.matGreen(0.05).bindColor();
                    }

                    UIUtils.drawQuad(rot.add(linkedComponent.width * low, 8), rot.add(linkedComponent.width * high, 12));
                }

                if (ad.getRotZFrames().size() > 0) {
                    float low = ad.getRotZFrames().firstKey();
                    float high = ad.getRotZFrames().lastKey();

                    PosXY rot = linkedComponent.topLeftPx.add(0, ROT_Z_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with less transparency
                        UIColor.matBlue(0.25).bindColor();
                    } else {
                        UIColor.matBlue(0.05).bindColor();
                    }

                    UIUtils.drawQuad(rot.add(linkedComponent.width * low, 8), rot.add(linkedComponent.width * high, 12));
                }
                //</editor-fold>
            }
        }

        /**
         * Draws the keyframe textures
         */
        private void drawKeyframes() {
            for (Map.Entry<String, AnimData> entry : objectAnimDataMap.entrySet()) {
                String name = entry.getKey();
                AnimData ad = entry.getValue();

                Map<String, BufferedAnimData> animDataBufferMap = ProjectManager.getCurrentProject().clientLevelData.getAnimDataBufferMap();
                if (animDataBufferMap.containsKey(entry.getKey())) {
                    ad = ad.mergeWithCopy(animDataBufferMap.get(entry.getKey()).getTransformedAnimData());
                }

                //<editor-fold desc="Pos">
                for (Map.Entry<Float, Float> xEntry : ad.getPosXFrames().entrySet()) {
                    float time = xEntry.getKey();

                    PosXY pos = linkedComponent.topLeftPx.add(linkedComponent.width * time, POS_X_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with no transparency
                        UIColor.matRed().bindColor();
                        UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeTex);

                        if (ProjectManager.getCurrentProject().clientLevelData.isPosXKeyframeSelected(time)) { //Draw selection tex if selected
                            UIColor.matYellow().bindColor();
                            UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeSelectionTex);
                        }

                    } else {
                        UIColor.matRed(0.1).bindColor();
                        UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeTex);
                    }

                }

                for (Map.Entry<Float, Float> yEntry : ad.getPosYFrames().entrySet()) {
                    float time = yEntry.getKey();

                    PosXY pos = linkedComponent.topLeftPx.add(linkedComponent.width * time, POS_Y_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with no transparency
                        UIColor.matGreen().bindColor();
                        UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeTex);

                        if (ProjectManager.getCurrentProject().clientLevelData.isPosYKeyframeSelected(time)) { //Draw selection tex if selected
                            UIColor.matYellow().bindColor();
                            UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeSelectionTex);
                        }
                    } else {
                        UIColor.matGreen(0.1).bindColor();
                        UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeTex);
                    }
                }

                for (Map.Entry<Float, Float> zEntry : ad.getPosZFrames().entrySet()) {
                    float time = zEntry.getKey();

                    PosXY pos = linkedComponent.topLeftPx.add(linkedComponent.width * time, POS_Z_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with no transparency
                        UIColor.matBlue().bindColor();
                        UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeTex);

                        if (ProjectManager.getCurrentProject().clientLevelData.isPosZKeyframeSelected(time)) { //Draw selection tex if selected
                            UIColor.matYellow().bindColor();
                            UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeSelectionTex);
                        }
                    } else {
                        UIColor.matBlue(0.1).bindColor();
                        UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeTex);
                    }
                }
                //</editor-fold>

                //<editor-fold desc="Rot">
                for (Map.Entry<Float, Float> xEntry : ad.getRotXFrames().entrySet()) {
                    float time = xEntry.getKey();

                    PosXY rot = linkedComponent.topLeftPx.add(linkedComponent.width * time, ROT_X_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with no transparency
                        UIColor.matRed().bindColor();
                        UIUtils.drawTexturedQuad(rot.add(-10, 0), rot.add(10, 20), keyframeTex);

                        if (ProjectManager.getCurrentProject().clientLevelData.isRotXKeyframeSelected(time)) { //Draw selection tex if selected
                            UIColor.matYellow().bindColor();
                            UIUtils.drawTexturedQuad(rot.add(-10, 0), rot.add(10, 20), keyframeSelectionTex);
                        }

                    } else {
                        UIColor.matRed(0.1).bindColor();
                        UIUtils.drawTexturedQuad(rot.add(-10, 0), rot.add(10, 20), keyframeTex);
                    }

                }

                for (Map.Entry<Float, Float> yEntry : ad.getRotYFrames().entrySet()) {
                    float time = yEntry.getKey();

                    PosXY rot = linkedComponent.topLeftPx.add(linkedComponent.width * time, ROT_Y_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with no transparency
                        UIColor.matGreen().bindColor();
                        UIUtils.drawTexturedQuad(rot.add(-10, 0), rot.add(10, 20), keyframeTex);

                        if (ProjectManager.getCurrentProject().clientLevelData.isRotYKeyframeSelected(time)) { //Draw selection tex if selected
                            UIColor.matYellow().bindColor();
                            UIUtils.drawTexturedQuad(rot.add(-10, 0), rot.add(10, 20), keyframeSelectionTex);
                        }
                    } else {
                        UIColor.matGreen(0.1).bindColor();
                        UIUtils.drawTexturedQuad(rot.add(-10, 0), rot.add(10, 20), keyframeTex);
                    }
                }

                for (Map.Entry<Float, Float> zEntry : ad.getRotZFrames().entrySet()) {
                    float time = zEntry.getKey();

                    PosXY rot = linkedComponent.topLeftPx.add(linkedComponent.width * time, ROT_Z_KEYFRAME_Y_POS);

                    if (selectedObjects.contains(name)) { //Draw selected objects with no transparency
                        UIColor.matBlue().bindColor();
                        UIUtils.drawTexturedQuad(rot.add(-10, 0), rot.add(10, 20), keyframeTex);

                        if (ProjectManager.getCurrentProject().clientLevelData.isRotZKeyframeSelected(time)) { //Draw selection tex if selected
                            UIColor.matYellow().bindColor();
                            UIUtils.drawTexturedQuad(rot.add(-10, 0), rot.add(10, 20), keyframeSelectionTex);
                        }
                    } else {
                        UIColor.matBlue(0.1).bindColor();
                        UIUtils.drawTexturedQuad(rot.add(-10, 0), rot.add(10, 20), keyframeTex);
                    }
                }
                //</editor-fold>
            }
        }

        private void drawBlocks() {
            //Pos and rot separator
            UIUtils.drawQuad(
                    new PosXY(linkedComponent.topLeftPx.x, linkedComponent.topLeftPx.y + POS_Z_KEYFRAME_Y_POS + 17),
                    new PosXY(linkedComponent.bottomRightPx.x, linkedComponent.topLeftPx.y + ROT_X_KEYFRAME_Y_POS + 3),
                    UIColor.matWhite(0.75)
            );

            //Time 0 separator
            float timeZeroPercent = 6.0f / 66.0f;
            if (ProjectManager.getCurrentProject() != null && ProjectManager.getCurrentProject().clientLevelData != null) {
                timeZeroPercent = ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime() /
                        (ProjectManager.getCurrentProject().clientLevelData.getLevelData().getLeadInTime() + ProjectManager.getCurrentProject().clientLevelData.getLevelData().getMaxTime());
            }
            double timeZeroPx = linkedComponent.topLeftPx.x + (linkedComponent.width * timeZeroPercent);

            UIUtils.drawQuad(
                    new PosXY(timeZeroPx - 1, linkedComponent.topLeftPx.y),
                    new PosXY(timeZeroPx + 1, linkedComponent.bottomRightPx.y),
                    UIColor.matWhite(0.75)
            );

            //Playhead block
            UIUtils.drawQuad(
                    new PosXY(linkedComponent.topLeftPx.x, linkedComponent.topLeftPx.y),
                    new PosXY(linkedComponent.bottomRightPx.x, linkedComponent.topLeftPx.y + 16),
                    UIColor.matRed(0.25)
            );
        }

    }

    public void setObjectAnimDataMap(@Nullable Map<String, AnimData> objectAnimDataMap) {
        this.objectAnimDataMap = objectAnimDataMap;
    }

    public void setSelectedObjects(Collection<String> selectedObjects) {
        this.selectedObjects = selectedObjects;
    }

}
