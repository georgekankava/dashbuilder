/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.renderer.google.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface GoogleDisplayerConstants extends Messages {

    public static final GoogleDisplayerConstants INSTANCE = GWT.create( GoogleDisplayerConstants.class );

    String common_Categories();

    String common_Series();

    String common_Values();

    String common_Value();

    String common_Locations();

    String common_Rows();

    String common_Columns();

    String common_noData();

    String googleDisplayer_resetAnchor();

    String googleBubbleDisplayer_XAxis();

    String googleBubbleDisplayer_YAxis();

    String googleBubbleDisplayer_BubbleColor();

    String googleBubbleDisplayer_BubbleSize();

    String googleTableDisplayer_gotoFirstPage();

    String googleTableDisplayer_gotoPreviousPage();

    String googleTableDisplayer_gotoNextPage();

    String googleTableDisplayer_gotoLastPage();

    String googleTableDisplayer_pages( String leftMostPageNumber, String rightMostPageNumber, String totalPages);

    String googleTableDisplayer_rows( String from, String to, String totalRows);

}
