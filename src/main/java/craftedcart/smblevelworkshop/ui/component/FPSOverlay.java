package craftedcart.smblevelworkshop.ui.component;

import craftedcart.smblevelworkshop.resource.LangManager;
import io.github.craftedcart.fluidui.component.Label;
import io.github.craftedcart.fluidui.component.ListBox;
import io.github.craftedcart.fluidui.component.Panel;
import io.github.craftedcart.fluidui.plugin.AbstractComponentPlugin;
import io.github.craftedcart.fluidui.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CraftedCart
 *         Created on 04/02/2017 (DD/MM/YYYY)
 */
public class FPSOverlay extends Panel {

    @Override
    public void postInit() {
        super.postInit();

        Label fpsLabel = new Label();
        fpsLabel.setOnInitAction(() -> {
            fpsLabel.setTopLeftPos(0, 0);
            fpsLabel.setBottomRightPos(0, 0);
            fpsLabel.setTopLeftAnchor(0, 0);
            fpsLabel.setBottomRightAnchor(1, 1);
            fpsLabel.addPlugin(new FPSLabelPlugin());
        });
        addChildComponent("fpsLabel", fpsLabel);
    }

    private class FPSLabelPlugin extends AbstractComponentPlugin {

        private String fpsLoc;
        private String avgLoc;
        private List<Double> deltaAvgList = new ArrayList<>();

        FPSLabelPlugin() {
            fpsLoc = LangManager.getItem("fps");
            avgLoc = LangManager.getItem("5sAverage");
        }

        @Override
        public void onPreDraw() {
            double delta = UIUtils.getDelta();
            double fps = 1 / delta;
            deltaAvgList.add(delta);

            double totalDeltaAvg = 0;
            for (double value : deltaAvgList) totalDeltaAvg += value;

            while (totalDeltaAvg > 5) { //5s Average
                totalDeltaAvg -= deltaAvgList.get(0);
                deltaAvgList.remove(0);
            }

            double fpsAvg = 1 / (totalDeltaAvg / deltaAvgList.size());

            ((Label) linkedComponent).setText(String.format("%s: %05.2f | %s %05.2f", fpsLoc, fps, avgLoc, fpsAvg));
        }
    }

}
