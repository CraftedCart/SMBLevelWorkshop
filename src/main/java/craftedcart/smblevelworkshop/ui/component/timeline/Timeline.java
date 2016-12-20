package craftedcart.smblevelworkshop.ui.component.timeline;

import craftedcart.smblevelworkshop.SMBLWSettings;
import craftedcart.smblevelworkshop.Window;
import craftedcart.smblevelworkshop.animation.AnimData;
import craftedcart.smblevelworkshop.level.ClientLevelData;
import craftedcart.smblevelworkshop.project.ProjectManager;
import craftedcart.smblevelworkshop.resource.LangManager;
import craftedcart.smblevelworkshop.resource.ResourceManager;
import craftedcart.smblevelworkshop.ui.DefaultUITheme;
import craftedcart.smblevelworkshop.ui.MainScreen;
import craftedcart.smblevelworkshop.util.MathUtils;
import io.github.craftedcart.fluidui.component.Button;
import io.github.craftedcart.fluidui.component.Label;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.component.TextField;
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
                        ProjectManager.getCurrentProject().clientLevelData.setMaxTime(newValue);
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
                updateMaxTime(ProjectManager.getCurrentProject().clientLevelData.getMaxTime());
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
            timeTextField.setInputRegexCheck("[0-9.]");
            timeTextField.setValue(df.format(0));
        });
        timeTextField.setOnReturnAction(() -> timeTextField.setSelected(false));
        timeTextField.setOnValueConfirmedAction(() -> {
            try {
                float newValue = Float.parseFloat(timeTextField.value);
                if (ProjectManager.getCurrentProject().clientLevelData != null) {
                    if (newValue >= 0) {
                        if (newValue <= ProjectManager.getCurrentProject().clientLevelData.getMaxTime()) {
                            ProjectManager.getCurrentProject().clientLevelData.setTimelinePos(newValue / ProjectManager.getCurrentProject().clientLevelData.getMaxTime());
                        } else {
                            mainScreen.notify(LangManager.getItem("numberMustBeLessThanMaxTime"), UIColor.matRed());
                        }
                    } else {
                        mainScreen.notify(LangManager.getItem("numberMustBeGreaterThanOrEqualToZero"), UIColor.matRed());
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
        posPanel.setTopLeftAnchor(percent, 0);
        posPanel.setBottomRightAnchor(percent, 1);
        posPanel.setRightText(df.format(percent * 100) + "%");
        posPanel.setLeftText(df.format(percent * ProjectManager.getCurrentProject().clientLevelData.getMaxTime()) + secondsSuffix);

        percentTextField.setValue(df.format(percent * 100));
        timeTextField.setValue(df.format(percent * ProjectManager.getCurrentProject().clientLevelData.getMaxTime()));
    }

    public void updateMaxTime(float maxTime) {
        maxTimeTextField.setValue(df.format(maxTime));
        posPanel.setLeftText(df.format(ProjectManager.getCurrentProject().clientLevelData.getTimelinePos() * maxTime) + secondsSuffix);
    }

    private class TimelinePlugin extends AbstractComponentPlugin {

        @Override
        public void onPreDraw() {
            //<editor-fold desc="Manage cursorPosPanel">
            if (linkedComponent.mouseOver) {
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
                    seconds = percent * ProjectManager.getCurrentProject().clientLevelData.getMaxTime();
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
            } else {
                cursorPosPanel.setVisible(false);
            }
            //</editor-fold>
        }

        @Override
        public void onClickChildComponent(int button, PosXY mousePos) {
            if (ProjectManager.getCurrentProject().clientLevelData == null) {
                mainScreen.notify(LangManager.getItem("noLevelLoaded"), UIColor.matRed());
            }
        }

    }

    private class TimelineUseablePlugin extends AbstractComponentPlugin {

        Texture keyframeTex;

        @Override
        public void onPostInit() {
            keyframeTex = ResourceManager.getTexture("image/keyframe").getTexture();
        }

        @Override
        public void onPreDraw() {
            if (objectAnimDataMap != null) {
                drawKeyframesBackgrounds();
                drawKeyframes();
            }
        }

        private void drawKeyframesBackgrounds() {
            for (Map.Entry<String, AnimData> entry : objectAnimDataMap.entrySet()) {
                String name = entry.getKey();
                AnimData ad = entry.getValue();

                if (ad.getPosXFrames().size() > 0) {
                    float low = ad.getPosXFrames().firstKey();
                    float high = ad.getPosXFrames().lastKey();

                    PosXY pos = linkedComponent.topLeftPx.add(0, 12);

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

                    PosXY pos = linkedComponent.topLeftPx.add(0, 28);

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

                    PosXY pos = linkedComponent.topLeftPx.add(0, 44);

                    if (selectedObjects.contains(name)) { //Draw selected objects with less transparency
                        UIColor.matBlue(0.25).bindColor();
                    } else {
                        UIColor.matBlue(0.05).bindColor();
                    }

                    UIUtils.drawQuad(pos.add(linkedComponent.width * low, 8), pos.add(linkedComponent.width * high, 12));
                }
            }
        }

        private void drawKeyframes() {
            for (Map.Entry<String, AnimData> entry : objectAnimDataMap.entrySet()) {
                String name = entry.getKey();

                for (Map.Entry<Float, Float> xEntry : entry.getValue().getPosXFrames().entrySet()) {
                    float time = xEntry.getKey();

                    PosXY pos = linkedComponent.topLeftPx.add(linkedComponent.width * time, 12);

                    if (selectedObjects.contains(name)) { //Draw selected objects with no transparency
                        UIColor.matRed().bindColor();
                    } else {
                        UIColor.matRed(0.1).bindColor();
                    }
                    UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeTex);
                }

                for (Map.Entry<Float, Float> yEntry : entry.getValue().getPosYFrames().entrySet()) {
                    float time = yEntry.getKey();

                    PosXY pos = linkedComponent.topLeftPx.add(linkedComponent.width * time, 28);

                    if (selectedObjects.contains(name)) { //Draw selected objects with no transparency
                        UIColor.matGreen().bindColor();
                    } else {
                        UIColor.matGreen(0.1).bindColor();
                    }
                    UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeTex);
                }

                for (Map.Entry<Float, Float> zEntry : entry.getValue().getPosZFrames().entrySet()) {
                    float time = zEntry.getKey();

                    PosXY pos = linkedComponent.topLeftPx.add(linkedComponent.width * time, 44);

                    if (selectedObjects.contains(name)) { //Draw selected objects with no transparency
                        UIColor.matBlue().bindColor();
                    } else {
                        UIColor.matBlue(0.1).bindColor();
                    }
                    UIUtils.drawTexturedQuad(pos.add(-10, 0), pos.add(10, 20), keyframeTex);
                }
            }
        }

    }

    public void setObjectAnimDataMap(@Nullable Map<String, AnimData> objectAnimDataMap) {
        this.objectAnimDataMap = objectAnimDataMap;
    }

    public void setSelectedObjects(Collection<String> selectedObjects) {
        this.selectedObjects = selectedObjects;
    }

}
