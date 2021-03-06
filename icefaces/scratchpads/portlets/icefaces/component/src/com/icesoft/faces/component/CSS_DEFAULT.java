/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.faces.component;

/**
 * Created by IntelliJ IDEA. User: rmayhew Date: Apr 21, 2006 Time: 11:31:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class CSS_DEFAULT {
    //------------------------------------------------------------------
    // CSS style naming rules:
    // 1. All styles start with 'ice'
    // 2. Don't end with Class (redundant)
    // 3. Look for existing abbreviations, and reuse them
    // 4. Try to keep new abbreviations to 3 letters
    //------------------------------------------------------------------

    public static final String COMMAND_BTN_DEFAULT_STYLE_CLASS =
            "iceBtn"; // Originally was "iceButton"
    public static final String COMMAND_LINK_DEFAULT_STYLE_CLASS =
            "iceLink"; // Originally was "iceLink"
    public static final String INPUT_SECRET_DEFAULT_STYLE_CLASS =
            "iceInputtext"; // Originally was "iceInputtext"
    public static final String INPUT_TEXT_DEFAULT_STYLE_CLASS =
            "iceInputtext"; // Originally was "iceInputtext"
    public static final String INPUT_TEXT_AREA_DEFAULT_STYLE_CLASS =
            "iceTextarea"; // Originally was "iceTextarea"
    public static final String OUTPUT_LABEL_DEFAULT_STYLE_CLASS =
            "iceOutputText"; // Originally was "iceOuputtext"
    public static final String OUTPUT_LINK_DEFAULT_STYLE_CLASS =
            "iceOutputLink"; // Originally was "iceOuputLink"
    public static final String OUTPUT_TEXT_DEFAULT_STYLE_CLASS =
            "iceOutputText"; // Originally was "iceOuputtext"
    public static final String SELECT_BOOLEAN_CHECKBOX_DEFAULT_STYLE_CLASS =
            "iceBooleanCheckbox";  // Originally was "iceBooleanCheckbox"
    public static final String SELECT_MANY_CHECKBOX_DEFAULT_STYLE_CLASS =
            "iceCheckbox"; // Originally was "iceCheckbox"
    public static final String SELECT_MANY_LISTBOX_DEFAULT_STYLE_CLASS =
            "iceSelect"; // Originally was "iceSelect"
    public static final String SELECT_MANY_MENU_DEFAULT_STYLE_CLASS =
            "iceSelect";// Originally was "iceSelect"
    public static final String SELECT_ONE_LISTBOX_DEFAULT_STYLE_CLASS =
            "iceSelect"; // Originally was "iceSelect"
    public static final String SELECT_ONE_MENU_DEFAULT_STYLE_CLASS =
            "iceSelect"; // Originally was "iceSelect"
    public static final String SELECT_ONE_RADIO_DEFAULT_STYLE_CLASS =
            "iceRadioBtn"; // Originally was "iceRadiobutton"
    public static final String TABLE_OUTLINE_CLASS =
            "iceDataTblOutline"; // Originally was "dataTableOutlineClass"
    public static final String TABLE_HEADER_CLASS =
            "iceTblHeader";   // Originally was "headerClass"
    public static final String TABLE_FOOTER_CLASS =
            "iceTblFooter";   // Originally was "headerClass"
    public static final String TABLE_ROW_CLASS1 =
            "iceTblRow1"; // Originally was "rowClasses1"
    public static final String TABLE_ROW_CLASS2 =
            "iceTblRow2"; // Originally was "rowClasses2"
    public static final String TABLE_COLUMN_CLASSES =
            "column";  // Originally was "columnClasses"
    public static final String PANEL_GROUP_DEFAULT_STYLE_CLASS = "icePnlGrp";
    public static final String PANEL_GRID_DEFAULT_STYLE_CLASS = "icePnlGrd";
    public static final String ROW = "Row";
    public static final String COLUMN = "Column";
    public static final String HEADER = "Header";
    public static final String FOOTER = "Footer";
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_STYLE_CLASS =
            "iceOutConStat";  // Originally was "outputConnectionStatusStyleClass"
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_INACTIVE_CLASS =
            "Inactive"; // Originally was "outputConnectionStatusInactiveClass"
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_ACTIVE_CLASS =
            "Active"; // Originally was "outputConnectionStatusActiveClass"
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_CAUTION_CLASS =
            "Caution"; // Originally was "outputConnectionStatusCautionClass"
    public static final String OUTPUT_CONNECTION_STATUS_DEFAULT_DISCONNECT_CLASS =
            "Disconnect"; // Originally was "outputConnectionStatusDisconnectClass"
    public static final String PANEL_BORDER_DEFAULT = "icePage";
    public static final String PANEL_BORDER_DEFAULT_NORTH_CLASS =
            "North"; // Originally was "pageNorth"
    public static final String PANEL_BORDER_DEFAULT_WEST_CLASS =
            "West"; // Originally was "pageWest"
    public static final String PANEL_BORDER_DEFAULT_EAST_CLASS =
            "East"; // Originally was "pageEast"
    public static final String PANEL_BORDER_DEFAULT_CENTER_CLASS =
            "Center"; // Originally was "pageCenter"
    public static final String PANEL_BORDER_DEFAULT_SOUTH_CLASS =
            "South";  // Originally was "pageSouth"
    public static final String COMMAND_SORT_HEADER_STYLE_CLASS = "iceCmdSrtHdr";
    public static final String FORM_STYLE_CLASS = "iceForm";
    public static final String GRAPHIC_IMAGE_STYLE_CLASS = "iceGphImg";
    public static final String MESSAGE_STYLE_CLASS = "iceMsg";
    public static final String MESSAGES_STYLE_CLASS = "iceMsgs";
    public static final String ERROR_STYLE_CLASS = "Error";
    public static final String FATAL_STYLE_CLASS = "Fatal";
    public static final String INFO_STYLE_CLASS = "Info";
    public static final String WARN_STYLE_CLASS = "Warn";
    public static final String OUTPUT_CHART_DEFAULT_STYLE_CLASS =
            "iceOutputChart";
    public static final String OUTPUT_FORMAT_DEFAULT_STYLE_CLASS =
            "iceOutputFormat";
    //public static final String PANEL_BORDER_DEFAULT_STYLE_CLASS = "Style"; // Originally was "pageStyleClass"

    public static final String POPUP_BASE =
            "icePanelPopup"; // Originally was "panelPopup"
    public static final String POPUP_DEFAULT_HEADER_CLASS =
            "Header"; // Originally was "panelPopupHeader"
    public static final String POPUP_DEFAULT_BODY_CLASS =
            "Body"; // Originally was "panelPopupBody"

    public static final String PANEL_TAB_DEFAULT_STYLECLASS =
            "icePanelTab"; // Originally was "panelTabClass"

    public static final String PANEL_TAB_SET_DEFAULT_TAB_SET =
            "iceTabSet";  // Originally was "tabSet"
    public static final String PANEL_TAB_SET_DEFAULT_TABONCLASS =
            "TabOn";// Originally was "TabOnClass"
    public static final String PANEL_TAB_SET_DEFAULT_TABOVERCLASS =
            "TabOver";// Originally was "tabOverClass"
    public static final String PANEL_TAB_SET_DEFAULT_TABOFFCLASS =
            "TabOff";// Originally was "TabOffClass"
    public static final String PANEL_TAB_SET_DEFAULT_TABSPACER =
            "TabSpacer";// Originally was "TabSpacer"
    public static final String PANEL_TAB_SET_DEFAULT_TABPANEL =
            "TabPnl";// Originally was "TabPanel"
    public static final String PANEL_TAB_HEADER_ICON_DEFAULT_CLASS = "HdrIcon";
    public static final String PANEL_TAB_HEADER_LABEL_DEFAULT_CLASS =
            "HdrLabel";
    public static final String PANEL_TAB_SET_DEFAULT_LEFT = "Left";
    public static final String PANEL_TAB_SET_DEFAULT_RIGHT = "Right";
    public static final String PANEL_TAB_SET_DEFAULT_MIDDLE = "Middle";
    public static final String PANEL_TAB_SET_DEFAULT_TOP = "Top";
    public static final String PANEL_TAB_SET_DEFAULT_BOTTOM = "Bottom";
    public static final String PANEL_SERIES_DEFAULT_CLASS = "icePnlSeries";
    public static final String POSITIONED_PANEL_DEFAULT_CLASS = "icePosPnl";

    public static final String ICE_FILE_UPLOAD_DEFAULT_BUTTON_CLASS =
            "Button"; // Originally was "Button"
    public static final String ICE_FILE_UPLOAD_DEFAULT_INPUT_TEXT_CLASS =
            "Text"; // Originally was "Text"
    public static final String ICE_FILE_UPLOAD_BASE_CLASS =
            "iceFileUpload";  // Originally was "fileUpload"
    public static final String OUTPUT_PROGRESS_BASE_CLASS =
            "iceOutputProgress"; // Originally was "outputProgress"
    public static final String OUTPUT_PROGRESS_INDERERMINATE_ACTIVE_CLASS =
            "IndeterminateActiveClass"; // Originally was "IndeterminateActiveClass"
    public static final String OUTPUT_PROGRESS_INDEREMINATE_INACTIVE_CLASS =
            "IndeterminateInactiveClass"; // Originally was "IndeterminateInactiveClass"
    public static final String OUTPUT_PROGRESS_TEXT_STYLE_CLASS =
            "Text";   // Originally was "Text"
    public static final String OUTPUT_PROGRESS_BG_STYLE_CLASS =
            "Background"; // Originally was "Background"
    public static final String OUTPUT_PROGRESS_FILL_STYLE_CLASS =
            "Fill";  // Originally was "Fill"
    public static String MENU_BAR_STYLE =
            "iceMenuBar";// Originally was "menuBar" // currently unused
    public static String MENU_HORIZONTAL_STYLE =
            "iceMenu"; // Originally was "menu"
    public static String SUBMENU_STYLE =
            "iceSubMenu"; // Originally was "submenu"
    public static String SUBMENU_ROW_STYLE =
            "iceSubMenuRow"; // Originally was "submenuRow"
    public static String SUBMENU_ROW_STYLE_DISABLED =
            "iceSubMenuRow-dis"; // Originally was "submenuRow-dis"
    public static String SUBMENU_ITEM_STYLE =
            "iceSubMenuItem";// Originally was "submenuItem" // currently unused
    public static String SUBMENU_ITEM2_STYLE =
            "iceSubMenuItem2"; // Originally was "submenuItem2"// currently unused
    public static String MENU_VERTICAL_STYLE =
            "iceMenu_vertical"; // Originally was "menu_vertical"
    public static String MENU_VERTICAL_ITEM_STYLE =
            "iceMenu_verticalItem"; // Originally was "menu_verticalItem"
    public static String MENU_VERTICAL_ITEM_2_STYLE =
            "iceMenu_verticalItem2";// Originally was "menu_verticalItem2"
    public static String SUBMENU_VERTICAL_STYLE =
            "iceSubMenu_vertical";// Originally was "submenu_vertical"
    public static String MENU_VERTICAL_SUBITEM_STYLE =
            "iceMenu_vertical_subItem"; // Originally was "menu_vertical_subItem"
    public static final String MENU_VERTICAL_SUBITEM_STYLE_DISABLED =
            "iceMenu_vertical_subItem-dis";// Originally was "menu_vertical_subItem-dis"
    public static String MENU_VERTICAL_SUBITEM2_STYLE =
            "iceMenu_vertical_subItem2";// Originally was "menu_vertical_subItem2"
    public static String SUBMENU_DIVIDER_VERTICAL_STYLE =
            "iceSubMenuDividerVert";  // Originally was "submenuDividerVert"
    public static String SUBMENU_DIVIDER_STYLE =
            "iceSubMenuDivider";  // Originally was "submenuDivider"
    public static String SUBMENU_INDICATOR_STYLE =
            "iceSubMenuIndicator"; // Originally was "submenuIndicator"
    public static final String STYLE_TREEROW =
            "iceTreeRow";// Originally was "treerow"
    public static final String TREE_DEFAULT_STYLE_CLASS = "iceTree";
    /**
     * Calendar Constants. Now based on calendar style name
     * <p/>
     * The default style class name for the row containing the month, year and
     * navigation buttons.
     */
    public final static String DEFAULT_CALENDAR = "iceCal";

    public final static String DEFAULT_YEARMONTHHEADER_CLASS =
            "MonthYear";  // iceCalMonthYear Originally was "monthYearRowClass"
    /**
     * The default style class name for the row containg the names of the days
     * of the week.
     */
    public final static String DEFAULT_WEEKHEADER_CLASS =
            "Week"; // iceCalWeek Originally was "weekRowClass"
    /**
     * The default style class name for the cell containing the currently
     * selected day.
     */
    public final static String DEFAULT_CURRENTDAYCELL_CLASS =
            "Cur";  // iceCalCur Originally was "currentDayCellClass"
    /**
     * The default style class name for the cells containing the days of the
     * month.
     */
    public final static String DEFAULT_DAYCELL_CLASS =
            "Day"; // iceCalDay Originally was "dayCellClass"
    /**
     * The default style class name for the outline of the calendar.
     */
    public final static String DEFAULT_CALENDAROUTLINE_CLASS =
            "Outline"; // iceCalOutline Originally was "calendarOutlineClass"
    /**
     * The default style class name for the input text field part of the date
     * picker
     */
    public final static String DEFAULT_CALENDARINPUT_CLASS =
            "Input"; // Did not have an original name


    public static final String DEFAULT_SELECT_INPUT = "iceSelInp";
    /**
     * The default style calss name for this components input text element.
     */
    public static final String DEFAULT_SELECT_INPUT_TEXT_CLASS =
            "Text";  // Originally was "autoCompleteInputTextClass"
    /**
     * the default style class name for this components list element.
     */
    public static final String DEFAULT_SELECT_INPUT_LIST_CLASS =
            "List";   // Originally was "autoCompleteListClass"
    /**
     * The default style class name for this components list row elements.
     */
    public static final String DEFAULT_SELECT_INPUT_ROW_CLASS =
            "Row";// Originally was "autoCompleteRowClass"
    /**
     * The default style class name for this components list selected row
     * element.
     */
    public static final String DEFAULT_SELECT_INPUT_SELECTED_ROW_CLASS =
            "SelRow"; // Originally was "autoCompleteSelectedRowClass"

    // DataPaginator
    public static final String DATA_PAGINATOR_BASE = "iceDtdPgr";
    public static final String DATA_PAGINATOR_SCROLL_BUTTON_CELL_CLASS =
            "ScrBtn"; // Originally was "scrollButtonCellClass"
    public static final String OUTLINE_CLASS =
            "ScrOut"; // Originally was "dataScrollerOutlineClass"
    public static final String PAGINATOR_ACTIVE_COLUMN_CLASS =
            "ScrCol"; // Originally was "paginatorActiveColumnClass"
    public static final String PAGINATOR_COLUMN_CLASS =
            "Col"; // Originally was "paginatorColumnClass"
    public static final String PAGINATOR_TABLE_CLASS =
            "Tbl"; // Originally was "paginatorTableClass"

    public static final String PANEL_STACK_BASE = "icePnlStk";
    public static final String PANEL_STACK_ROW = "Row";
    public static final String PANEL_STACK_COL = "Col";
    // position constants

    // Row Selection Constants
    public static final String ROW_SELECTION_BASE = "iceRowSel";
    public static final String ROW_SELECTION_SELECTED = "Selected";
    public static final String ROW_SELECTION_MOUSE_OVER = "MouseOver";
    
    // PanelAccordion constants
    public static final String PANEL_ACCORDION_DEFAULT_STYLE_CLASS =
            "iceAccordion";
    public static final String PANEL_ACCORDION_HEADER =
            "Header";
    public static final String PANEL_ACCORDION_CONTENT =
            "Content";
    public static final String PANEL_ACCORDION_CONTAINER =
            "Container";
    public static final String PANEL_ACCORDION_STATE_COLLAPSED =
            "_collapsed";
    public static final String PANEL_ACCORDION_STATE_DISABLED =
            "_dis";
}