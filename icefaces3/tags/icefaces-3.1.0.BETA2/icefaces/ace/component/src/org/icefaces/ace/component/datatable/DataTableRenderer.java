/*
 * Original Code Copyright Prime Technology.
 * Subsequent Code Modifications Copyright 2011-2012 ICEsoft Technologies Canada Corp. (c)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE THIS CODE HAS BEEN MODIFIED FROM ORIGINAL FORM
 *
 * Subsequent Code Modifications have been made and contributed by ICEsoft Technologies Canada Corp. (c).
 *
 * Code Modification 1: Integrated with ICEfaces Advanced Component Environment.
 * Contributors: ICEsoft Technologies Canada Corp. (c)
 *
 * Code Modification 2: Improved Scrollable DataTable Column Sizing - ICE-7028
 * Contributors: Nils Lundquist
 */
package org.icefaces.ace.component.datatable;

import org.icefaces.ace.component.celleditor.CellEditor;
import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.columngroup.ColumnGroup;
import org.icefaces.ace.component.panelexpansion.PanelExpansion;
import org.icefaces.ace.component.row.Row;
import org.icefaces.ace.component.rowexpansion.RowExpansion;
import org.icefaces.ace.component.tableconfigpanel.TableConfigPanel;
import org.icefaces.ace.model.table.*;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.ace.util.HTML;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.*;

@MandatoryResourceComponent(tagName="dataTable", value="org.icefaces.ace.component.datatable.DataTable")
public class DataTableRenderer extends CoreRenderer {
    @Override
	public void decode(FacesContext context, UIComponent component) {
        DataTable table = (DataTable) component;

        // Deferred selection may occur on any request
        if (table.isSelectionEnabled())
            DataTableDecoder.decodeSelection(context, table);

        // Other features will occurs as individual requests
        if (table.isFilterRequest(context))
            DataTableDecoder.decodeFilters(context, table);

        else if (table.isSortRequest(context))
            DataTableDecoder.decodeSortRequest(context, table, null, null);

        else if (table.isPaginationRequest(context))
            DataTableDecoder.decodePageRequest(context, table);

        else if (table.isColumnReorderRequest(context))
            DataTableDecoder.decodeColumnReorderRequest(context, table);

        else if (table.isTableConfigurationRequest(context))
            DataTableDecoder.decodeTableConfigurationRequest(context, table);

        decodeBehaviors(context, component);
	}

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {}

    @Override
    public boolean getRendersChildren() { return true; }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException{
        DataTable table = (DataTable) component;

        if (table.isPaginator())
            table.calculatePage();

        if (table.isSortOrderChanged())
            table.processSorting();

        if (table.isFilterValueChanged())
            table.setFilteredData(table.processFilters(context));

        // Force regeneration of data model pre-render
        table.setModel(null);

        if (table.isScrollingRequest(context))
            encodeLiveRows(context, table);
        else
            encodeEntirety(context, table);
    }

    private void encodeEntirety(FacesContext context, DataTable table) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);
        String style = null;
        String styleClass;
        String paginatorPosition = table.getPaginatorPosition();
        String containerClass = DataTableConstants.CONTAINER_CLASS;
        boolean hasPaginator = table.isPaginator();
        boolean staticHeaders = table.isScrollable() && table.isStaticHeaders();
        table.getStateMap(); // init statemap while row index == -1


        if (staticHeaders)
            containerClass += " " + DataTableConstants.SCROLLABLE_CONTAINER_CLASS;

        if ((styleClass = table.getStyleClass()) != null)
            containerClass += " " + styleClass;


        writer.startElement(HTML.DIV_ELEM, table);
        writer.writeAttribute(HTML.ID_ATTR, clientId, HTML.ID_ATTR);
        writer.writeAttribute(HTML.CLASS_ATTR, containerClass, "styleClass");

        if ((style = table.getStyle()) != null)
            writer.writeAttribute(HTML.STYLE_ELEM, style, HTML.STYLE_ELEM);

        encodeFacet(context, table, table.getHeader(), DataTableConstants.HEADER_CLASS);

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("bottom"))
            encodePaginatorMarkup(context, table, "top");

        encodeConfigPanel(context, table);
        encodeTable(context, table, staticHeaders);

        if (hasPaginator && !paginatorPosition.equalsIgnoreCase("top"))
            encodePaginatorMarkup(context, table, "bottom");

        encodeFacet(context, table, table.getFooter(), DataTableConstants.FOOTER_CLASS);

        if (table.isSelectionEnabled())
            encodeSelectionAndDeselectionHolder(context, table);

        encodeScript(context, table);

        // Avoid sharing cached stateMap with other iterative instances
        table.clearCachedStateMap();

        if ("true".equals(context.getExternalContext().getInitParameter("ForceFullTableDOMUpdates"))) {
            writer.startElement(HTML.DIV_ELEM, null);
            writer.writeAttribute(HTML.STYLE_ATTR, "display:none;",null);
            writer.writeText(table.getForcedUpdateCounter(), null);
            writer.endElement(HTML.DIV_ELEM);
        }

        writer.endElement(HTML.DIV_ELEM);
    }

    private void encodeTable(FacesContext context, DataTable table, boolean staticHeaders) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        List<Column> columns = table.getColumns();

        if (!staticHeaders) {
            Integer height;

            writer.startElement(HTML.DIV_ELEM, null);
            if (table.isScrollable() && (height = table.getScrollHeight()) != null)
                writer.writeAttribute(HTML.STYLE_ELEM, "max-height:" + height + "px; overflow:auto;", null);
            writer.startElement(HTML.TABLE_ELEM, null);
        }

        DataTableHeadRenderer.encodeTableHead(context, table, columns, staticHeaders);
        encodeTableBody(context, table, columns, staticHeaders);
        DataTableFootRenderer.encodeTableFoot(context, table, columns, staticHeaders);

        if (!staticHeaders) {
            writer.endElement(HTML.TABLE_ELEM);
            writer.endElement(HTML.DIV_ELEM);
        }
    }

    private void encodeTableBody(FacesContext context, DataTable table, List<Column> columns, boolean scrollable) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String rowIndexVar = table.getRowIndexVar();
        String clientId = table.getClientId(context);

        if (scrollable) {
            writer.startElement(HTML.DIV_ELEM, null);
            String scrollClass = DataTableConstants.SCROLLABLE_X_CLASS + " " + DataTableConstants.SCROLLABLE_BODY_CLASS;
            writer.writeAttribute(HTML.CLASS_ATTR, scrollClass, null);
            writer.writeAttribute(HTML.STYLE_ELEM, "max-height:" + table.getScrollHeight() + "px", null);
            writer.startElement(HTML.TABLE_ELEM, null);

            if (table.hasHeaders()) {
                table.setInDuplicateSegment(true);
                DataTableHeadRenderer.encodeTableHead(context, table, columns, false);
                table.setInDuplicateSegment(false);
            }
        }

        if (table.isLazy()) table.loadLazyData();

        int rows = table.getRows();
        int first = table.getFirst();
        int page = table.getPage();
        int rowCount = table.getRowCount();
        int rowCountToRender = rows == 0 ? rowCount : rows;
        boolean renderedTopVisible = false;
        boolean hasData = rowCount > 0;

        String tbodyClass = hasData ? DataTableConstants.DATA_CLASS : DataTableConstants.EMPTY_DATA_CLASS;

        writer.startElement(HTML.TBODY_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, tbodyClass, null);

        Map<Object, List<String>> rowToSelectedFieldsMap = table.getRowToSelectedFieldsMap();

        if (hasData)
            for (int i = first; i < (first + rowCountToRender); i++) {
                if (DataTableRowRenderer.encodeRow(context, table, columns, rowToSelectedFieldsMap, clientId, i, null, rowIndexVar, renderedTopVisible))
                    renderedTopVisible = true;
            }
        else encodeEmptyMessage(table, writer, columns);

        writer.endElement(HTML.TBODY_ELEM);
        table.setRowIndex(-1);
        if (rowIndexVar != null) context.getExternalContext().getRequestMap().remove(rowIndexVar);

        if (scrollable) {
            table.setInDuplicateSegment(true);
            DataTableFootRenderer.encodeTableFoot(context, table, columns, false);
            table.setInDuplicateSegment(false);

            writer.endElement(HTML.TABLE_ELEM);
            writer.endElement(HTML.DIV_ELEM);
        }
    }

    private void encodeFacet(FacesContext context, DataTable table, UIComponent facet, String styleClass) throws IOException {
        if (facet == null) return;
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement(HTML.DIV_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, null);

        facet.encodeAll(context);
        writer.endElement(HTML.DIV_ELEM);
    }

    private void encodeScript(FacesContext context, DataTable table) throws IOException{
        ResponseWriter writer = context.getResponseWriter();
		String clientId = table.getClientId(context);
        String filterEvent = table.getFilterEvent();
        UIComponent form = ComponentUtils.findParentForm(context, table);
        JSONBuilder json = new JSONBuilder();

        if (form == null) 
            throw new FacesException("DataTable : \"" + clientId + "\" must be inside a form element.");

        final boolean paging = table.isPaginator();
        final boolean select = table.isSelectionEnabled();
        final boolean dblSelect = select && table.isDoubleClickSelect();
        final boolean ajaxSelect = select && table.hasSelectionClientBehaviour() || (table.getRowSelectListener() != null) || (table.getRowUnselectListener() != null);
        final boolean rowExp = table.getRowExpansion() != null;
        final boolean pnlExp = table.getPanelExpansion() != null;
        final boolean clkHdrSrt = table.isClickableHeaderSorting();
        final boolean resize = table.isResizableColumns();
        final boolean reorder = table.isReorderableColumns();
        final boolean snglSrt = table.isSingleSort();
        final boolean disable = table.isDisabled();
        final boolean scroll = table.isScrollable();
        final boolean hiddenScrollableSizing = table.isHiddenScrollableSizing();
        final boolean height = scroll && table.getScrollHeight() != Integer.MIN_VALUE;
        final boolean scrollIE8Like7 = Boolean.parseBoolean(context.getExternalContext().getInitParameter("org.icefaces.ace.datatable.scroll.ie8like7"));
        final boolean noIEHover = Boolean.parseBoolean(context.getExternalContext().getInitParameter("org.icefaces.ace.datatable.selection.noiehover"));

        json.beginMap();
        json.entry("formId", form.getClientId(context));        
        json.entry("filterEvent", filterEvent);
        json.entryNonNullValue("configPanel", table.getTableConfigPanel());
        if (paging) encodePaginatorConfig(context, json, table);
        if (pnlExp) json.entry("panelExpansion", true);
        if (rowExp) json.entry("rowExpansion", true);
        if (clkHdrSrt) json.entry("clickableHeaderSorting", true);        
        if (height) json.entry("height", table.getScrollHeight());
        if (select) json.entry("selectionMode", table.getSelectionMode());
        if (dblSelect) json.entry("dblclickSelect", true);
        if (ajaxSelect) json.entry("instantSelect", true);
        if (resize) json.entry("resizableColumns", true);
        if (reorder) json.entry("reorderableColumns", true);
        if (snglSrt) json.entry("singleSort", true);
        if (disable) json.entry("disable", true);
        if (noIEHover) json.entry("noiehover",true);
        if (scroll) {
            json.entry("scrollable", true);
            json.entry("liveScroll", table.isLiveScroll());
            json.entry("scrollStep", table.getRows());
            json.entry("scrollLimit", table.getRowCount());
            json.entry("scrollIE8Like7", scrollIE8Like7);
            if (!hiddenScrollableSizing) json.entry("disableHiddenSizing",true);
        }

        encodeClientBehaviors(context, table, json);

        json.endMap();

        String widgetVar = this.resolveWidgetVar(table);
		writer.startElement(HTML.SCRIPT_ELEM, table);
		writer.writeAttribute(HTML.TYPE_ATTR, "text/javascript", null);        
        writer.write("var " + widgetVar + " = new ice.ace.DataTable('" + clientId + "', " + json + ");");
		writer.endElement(HTML.SCRIPT_ELEM);
	}

    private void encodePaginatorConfig(FacesContext context, JSONBuilder scriptJson, DataTable table) throws IOException {
        JSONBuilder configJson = new JSONBuilder();
        String clientId = table.getClientId(context);
        String paginatorPosition = table.getPaginatorPosition();
        String paginatorContainers = paginatorPosition.equalsIgnoreCase("both")
            ? "'" + clientId + "_paginatortop','" + clientId + "_paginatorbottom'"
            : "'" + clientId + "_paginator" + paginatorPosition + "'";

        boolean disabled = table.isDisabled();
        String template = table.getPaginatorTemplate() ;
        String rowCounts= table.getRowsPerPageTemplate();
        String currPgTemplate = table.getCurrentPageReportTemplate();
        boolean notAlwaysVis = !table.isPaginatorAlwaysVisible();

        configJson.beginMap();
        configJson.entry("rowsPerPage", table.getRows());
        configJson.entry("totalRecords", table.getRowCount());
        configJson.entry("initialPage", table.getPage());
        configJson.entry("containers", "[" + paginatorContainers + "]", true);
        configJson.entryNonNullValue("template", template);
        configJson.entryNonNullValue("pageReportTemplate", currPgTemplate);
        if (notAlwaysVis) configJson.entry("alwaysVisible",false);
        if (disabled) configJson.entry("pageLinks", 1);
        else configJson.entry("pageLinks", table.getPageCount());

        if (rowCounts != null) {
            String[] rowCountArray = rowCounts.split(",");
            if (rowCountArray.length > 0) {
                configJson.beginArray("rowsPerPageOptions");
                for (String i : rowCountArray)
                    configJson.item(Integer.parseInt(i.trim()));
                configJson.endArray();
            }
        }

        configJson.endMap();

        scriptJson.entry("paginator", "new YAHOO.widget.Paginator(" + configJson + ")", true);
    }

    private void encodeConfigPanel(FacesContext context, DataTable table) throws IOException {
        for (UIComponent child : table.getChildren()) {
            if (child instanceof TableConfigPanel) child.encodeAll(context);
        }
    }

    private void encodeEmptyMessage(DataTable table, ResponseWriter writer, List<Column> columns) throws IOException {
        writer.startElement(HTML.TR_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.ROW_CLASS, null);
        writer.startElement(HTML.TD_ELEM, null);
        writer.writeAttribute(HTML.COLSPAN_ATTR, columns.size(), null);

        String emptyMessage;
        if ((emptyMessage = table.getEmptyMessage()) != null)
            writer.write(emptyMessage);

        writer.endElement(HTML.TD_ELEM);
        writer.endElement(HTML.TR_ELEM);
    }

    private void encodePaginatorMarkup(FacesContext context, DataTable table, String position) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = table.getClientId(context);

        String styleClass = "ui-paginator ui-paginator-" + position + " ui-widget-header";

        if (!position.equals("top") && table.getFooter() == null)
            styleClass = styleClass + " ui-corner-bl ui-corner-br";
        else if (!position.equals("bottom") && table.getHeader() == null)
            styleClass = styleClass + " ui-corner-tl ui-corner-tr";

        writer.startElement(HTML.DIV_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, clientId + "_paginator" + position, null);
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, null);

        TableConfigPanel panel = table.findTableConfigPanel(context);
        if (panel != null && panel.getType().equals("paginator-button"))
            DataTableHeadRenderer.encodeConfigPanelLaunchButton(writer, table, false);

        writer.endElement(HTML.DIV_ELEM);
    }

    private void encodeSelectionAndDeselectionHolder(FacesContext context, DataTable table) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String id = table.getClientId(context) + "_selection";

		writer.startElement(HTML.INPUT_ELEM, null);
		writer.writeAttribute(HTML.TYPE_ATTR, "hidden", null);
		writer.writeAttribute(HTML.ID_ATTR, id, null);
		writer.writeAttribute(HTML.NAME_ATTR, id, null);
        writer.endElement(HTML.INPUT_ELEM);


        id = table.getClientId(context) + "_deselection";
        writer.startElement(HTML.INPUT_ELEM, null);
        writer.writeAttribute(HTML.TYPE_ATTR, "hidden", null);
        writer.writeAttribute(HTML.ID_ATTR, id, null);
        writer.writeAttribute(HTML.NAME_ATTR, id, null);
        writer.endElement(HTML.INPUT_ELEM);
	}

    private void encodeLiveRows(FacesContext context, DataTable table) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        int scrollOffset = Integer.parseInt(params.get(table.getClientId(context) + "_scrollOffset"));
        String clientId = table.getClientId(context);
        String rowIndexVar = table.getRowIndexVar();

        Map<Object, List<String>> rowToSelectedFieldsMap = table.getRowToSelectedFieldsMap();
        Boolean topVisibleRowRendered = false;
        for (int i = scrollOffset; i < (scrollOffset + table.getRows()); i++) {
            if (DataTableRowRenderer.encodeRow(context, table, table.getColumns(), rowToSelectedFieldsMap, clientId, i, null, rowIndexVar, topVisibleRowRendered))
                topVisibleRowRendered = true;
        }
    }
}
