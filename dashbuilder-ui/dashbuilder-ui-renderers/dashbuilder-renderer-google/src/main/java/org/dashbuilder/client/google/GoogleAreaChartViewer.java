/**
 * Copyright (C) 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.google;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.visualizations.AreaChart;
import com.google.gwt.visualization.client.visualizations.AreaChart.Options;
import org.dashbuilder.model.displayer.AreaChartDisplayer;

public class GoogleAreaChartViewer extends GoogleXAxisChartViewer<AreaChartDisplayer> {

    @Override
    public String getPackage() {
        return AreaChart.PACKAGE;
    }

    @Override
    public Widget createChart() {
        AreaChart chart = new AreaChart(createTable(), createOptions());
        chart.addSelectHandler(createSelectHandler(chart));
        HTML titleHtml = new HTML();
        if (dataDisplayer.isTitleVisible()) {
            titleHtml.setText(dataDisplayer.getTitle());
        }

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(titleHtml);
        verticalPanel.add(chart);
        googleViewer = chart;
        return verticalPanel;
    }

    private Options createOptions() {
        Options options = Options.create();
        options.setWidth(dataDisplayer.getWidth());
        options.setHeight(dataDisplayer.getHeight());
        return options;
    }
}