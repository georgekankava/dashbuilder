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

import com.github.gwtbootstrap.client.ui.*;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.Bootstrap;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.events.SortHandler;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.Table.Options;
import org.dashbuilder.model.dataset.sort.SortOrder;
import org.dashbuilder.model.displayer.TableDisplayer;

public class GoogleTableViewer extends GoogleViewer<TableDisplayer> {

    protected int pageSize = 20;
    protected int currentPage = 1;
    protected int numberOfRows = 0;
    protected int numberOfPages = 1;

    protected boolean showTotalRowsHint = true;
    protected boolean showTotalPagesHint = true;

    HorizontalPanel pagerPanel = new HorizontalPanel();
    private SortInfo sortInfo = new SortInfo();
    final private PagerInterval pagerInterval = new PagerInterval();

    @Override
    public String getPackage() {
        return Table.PACKAGE;
    }

    @Override
    public Widget createChart() {
        pageSize = dataDisplayer.getPageSize();
        numberOfRows = dataSetHandler.getDataSetMetadata().getNumberOfRows();
        numberOfPages = ((numberOfRows - 1) / pageSize) + 1;
        if (currentPage > numberOfPages) {
            currentPage = 1;
        }

        pagerInterval.setNumberOfPages(numberOfPages);

        final DataTable dataTable = createTable();
        Table table = new Table( dataTable, createOptions() );
        table.addSortHandler( new SortHandler() {
            @Override public void onSort( SortEvent sortEvent ) {
                String columnId = dataTable.getColumnId(sortEvent.getColumn());
                setSortOrder(columnId, sortInfo.getSortOrder( columnId ));
                redraw();
            }
        } );

        HTML titleHtml = new HTML();
        if (dataDisplayer.isTitleVisible()) {
            titleHtml.setText(dataDisplayer.getTitle());
        }

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(titleHtml);
        verticalPanel.add(table);
        verticalPanel.add(pagerPanel);
        createTablePager();
        googleViewer = table;
        return verticalPanel;
    }

    @Override
    public void draw() {
        this.setPageLimits();
        super.draw();
    }

    @Override
    public void redraw() {
        this.setPageLimits();
        this.createTablePager();
        super.redraw();
    }

    public void setShowTotalPagesHint(boolean showTotalPagesHint) {
        this.showTotalPagesHint = showTotalPagesHint;
    }

    public void setShowTotalRowsHint(boolean showTotalRowsHint) {
        this.showTotalRowsHint = showTotalRowsHint;
    }

    protected void createTablePager() {
        pagerPanel.clear();
        pagerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        Pagination pagination = new Pagination();
        pagination.setSize(Pagination.PaginationSize.NORMAL);
        pagination.setAlignment(Bootstrap.Pagination.LEFT.toString());

        for (int i = pagerInterval.getLeftMostPageNumber(); i <= pagerInterval.getRightMostPageNumber(); i++) {
            NavLink pageLink = new NavLink(Integer.toString(i));
            final Integer _currentPage = Integer.valueOf(i);
            if (currentPage != i) {
                pageLink.setActive(false);
                pageLink.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        gotoPage(_currentPage.intValue());
                    }
                });
            } else {
                pageLink.setActive(true);
            }
            pagination.add(pageLink);
        }

        Icon leftPageIcon = new Icon(IconType.ANGLE_LEFT);
        leftPageIcon.setIconSize(IconSize.LARGE);
        leftPageIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        leftPageIcon.sinkEvents(Event.ONCLICK);
        leftPageIcon.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                gotoPage(currentPage - 1);
            }
        }, ClickEvent.getType());
        Tooltip leftPageTooltip = new Tooltip("Go to previous page");
        leftPageTooltip.add(leftPageIcon);

        Icon rightPageIcon = new Icon(IconType.ANGLE_RIGHT);
        rightPageIcon.setIconSize(IconSize.LARGE);
        rightPageIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        rightPageIcon.sinkEvents(Event.ONCLICK);
        rightPageIcon.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                gotoPage(currentPage + 1);
            }
        }, ClickEvent.getType());
        Tooltip rightPageTooltip = new Tooltip("Go to next page");
        rightPageTooltip.add(rightPageIcon);

        Icon firstPageIcon = new Icon(IconType.DOUBLE_ANGLE_LEFT);
        firstPageIcon.setIconSize(IconSize.LARGE);
        firstPageIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        firstPageIcon.sinkEvents(Event.ONCLICK);
        firstPageIcon.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                gotoPage(1);
            }
        }, ClickEvent.getType());
        Tooltip firstPageTooltip = new Tooltip("Go to first page");
        firstPageTooltip.add(firstPageIcon);

        Icon lastPageIcon = new Icon(IconType.DOUBLE_ANGLE_RIGHT);
        lastPageIcon.setIconSize(IconSize.LARGE);
        lastPageIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        lastPageIcon.sinkEvents(Event.ONCLICK);
        lastPageIcon.addHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                gotoPage(numberOfPages);
            }
        }, ClickEvent.getType());
        Tooltip lastPageTooltip = new Tooltip("Go to last page");
        lastPageTooltip.add(lastPageIcon);

        Label totalPages = null;
        StringBuilder sb = new StringBuilder();
        if ( showTotalPagesHint ) {
            sb.append( "Pages " );
            sb.append( pagerInterval.getLeftMostPageNumber() ).append("-").append( pagerInterval.getRightMostPageNumber() );
            sb.append( " of ").append( pagerInterval.getNumberOfPages() );
            totalPages = new Label( sb.toString() );
        }
        Label totalRows = null;
        sb = new StringBuilder();
        if ( showTotalRowsHint ) {
            sb.append( "Rows " );
            int currentRowsShown = currentPage * pageSize > numberOfRows ? numberOfRows : currentPage * pageSize;
            sb.append( ( ( currentPage - 1 ) * pageSize) + 1 ).append("-").append( currentRowsShown );
            sb.append( " of ").append( numberOfRows );
            totalRows = new Label( sb.toString() );
        }

        if ( numberOfPages > 1 && currentPage != 1 ) pagerPanel.add( firstPageTooltip );
        pagerPanel.add( new SpacerWidget() );
        if ( currentPage != 1 ) pagerPanel.add( leftPageTooltip );
        pagerPanel.add( new SpacerWidget() );
        if ( numberOfPages > 1 ) pagerPanel.add( pagination );
        pagerPanel.add( new SpacerWidget() );
        if ( currentPage != numberOfPages ) pagerPanel.add( rightPageTooltip );
        pagerPanel.add( new SpacerWidget() );
        if ( numberOfPages > 1 && currentPage != numberOfPages ) pagerPanel.add( lastPageTooltip );

        boolean both = showTotalPagesHint && showTotalRowsHint;
        if ( showTotalPagesHint || showTotalRowsHint ) {
            pagerPanel.add( new SpacerWidget() );
            pagerPanel.add( new SpacerWidget() );
            if ( totalPages != null && numberOfPages > 1 ) pagerPanel.add( totalPages );
            if ( both ) pagerPanel.add( new SpacerWidget() );
            if ( totalRows != null ) pagerPanel.add( totalRows );
        }
    }

    private void setPageLimits() {
        // Draw only the data subset corresponding to the current page.
        int pageSize = dataDisplayer.getPageSize();
        int offset = (currentPage - 1) * pageSize;
        dataSetHandler.limitDataSetRows(offset, pageSize);
    }

    private void gotoPage(int pageNumber) {
        pagerInterval.setCurrentPage(pageNumber);
        currentPage = pageNumber;
        this.redraw();
    }

    private Options createOptions() {
        Options options = Options.create();
        options.setSort( Options.Policy.EVENT );
        options.setPageSize(dataDisplayer.getPageSize());
        options.setShowRowNumber(false);
        return options;
    }

    private class PagerInterval {

        private int numberOfPages;
        // Amount of pages to be shown in the pager, default 10
        private int pageIntervalSize = 10;
        private int pageIntervalShift = 5;
        private int leftMostPageNumber;
        private int rightMostPageNumber;
        private int currentPage = 1;

        private PagerInterval() {
            leftMostPageNumber = 1;
            rightMostPageNumber = leftMostPageNumber + pageIntervalSize - 1;
        }

        private void setNumberOfPages(int numberOfPages) {
            this.numberOfPages = numberOfPages;
            calculateInterval();
        }

        private void setCurrentPage(int currentPage) {
            if (currentPage <= numberOfPages) {
                this.currentPage = currentPage;
                calculateInterval();
            }
        }

        private int getLeftMostPageNumber() {
            return leftMostPageNumber;
        }

        private int getRightMostPageNumber() {
            return rightMostPageNumber;
        }

        private int getNumberOfPages() {
            return numberOfPages;
        }

        private void setPageIntervalSize(int pageIntervalSize) {
            this.pageIntervalSize = pageIntervalSize;
            calculateInterval();
        }

        private void setPageIntervalShift(int pageIntervalShift) {
            this.pageIntervalShift = pageIntervalShift;
            calculateInterval();
        }

        private void calculateInterval() {
            if (numberOfPages <= pageIntervalSize) {
                leftMostPageNumber = 1;
                rightMostPageNumber = numberOfPages;
            } else {
                int _right = currentPage + pageIntervalShift;
                int _left = currentPage - pageIntervalShift + 1;

                if (_right > rightMostPageNumber) {
                    rightMostPageNumber = _right > numberOfPages ? numberOfPages : _right;
                    leftMostPageNumber = rightMostPageNumber - pageIntervalSize + 1;
                } else if (_left < leftMostPageNumber) {
                    leftMostPageNumber = _left < 1 ? 1 : _left;
                    rightMostPageNumber = leftMostPageNumber + pageIntervalSize - 1;
                }
            }
        }
    }

    private class SpacerWidget extends HTML {
        private SpacerWidget() {
            super("&nbsp;&nbsp;");
        }
    }

    private static class SortInfo {
        private String columnId;
        private SortOrder sortOrder;
        private SortInfo() {}
        private SortOrder getSortOrder( String columnId ) {
            if ( this.columnId == null || !this.columnId.equalsIgnoreCase( columnId ) ) {
                this.columnId = columnId;
                this.sortOrder = SortOrder.ASCENDING;
            } else {    //columnId != null && columnId == columnId) --> 'invert' order
                switch (this.sortOrder) {
                    case UNSPECIFIED: this.sortOrder = SortOrder.ASCENDING; break;
                    case ASCENDING: this.sortOrder = SortOrder.DESCENDING; break;
                    case DESCENDING: this.sortOrder = SortOrder.ASCENDING; break;
                    default: this.sortOrder = SortOrder.ASCENDING;
                }
            }
            return sortOrder;
        }
    }
}