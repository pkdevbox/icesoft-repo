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
 * Code Modification 2: [ADD BRIEF DESCRIPTION HERE]
 * Contributors: ______________________
 * Contributors: ______________________
 */
package org.icefaces.ace.component.dataexporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.columngroup.ColumnGroup;
import org.icefaces.ace.component.row.Row;
import org.icefaces.ace.component.expansiontoggler.ExpansionToggler;
import org.icefaces.ace.component.excludefromexport.ExcludeFromExport;
import org.icefaces.ace.component.celleditor.CellEditor;

public abstract class Exporter {

	protected SpanningRows spanningRows = this.new SpanningRows();

    public abstract String export(FacesContext facesContext, DataTable table,
		                    	String outputFileName, boolean pageOnly, int[] excludedColumnIndexes,
			                    String encodingType, MethodExpression preProcessor,
			                    MethodExpression postProcessor, boolean includeHeaders, boolean includeFooters, boolean selectedRowsOnly) throws IOException;

	
	protected List<UIColumn> getColumnsToExport(UIData table, int[] excludedColumns) {
        List<UIColumn> columns = new ArrayList<UIColumn>();
        int columnIndex = -1;

        for (UIComponent child : table.getChildren()) {
            if (child instanceof UIColumn) {
				if (shouldExcludeFromExport(child)) continue;
                UIColumn column = (UIColumn) child;
                columnIndex++;

				boolean hasExpansionToggler = false;
                for (UIComponent columnChild : column.getChildren())
					if (columnChild instanceof ExpansionToggler) hasExpansionToggler = true;
				if (hasExpansionToggler) continue;
				
				if (column.isRendered()) {
					if (excludedColumns == null || Arrays.binarySearch(excludedColumns, columnIndex) < 0) {
						columns.add(column);
					}
				}
            }
        }
        return columns;
    }
	
	protected ColumnGroup getColumnGroupHeader(UIData table) {
	
		for (UIComponent child : table.getChildren()) {
			if (child instanceof ColumnGroup) {
				if (shouldExcludeFromExport(child)) continue;
				ColumnGroup columnGroup = (ColumnGroup) child;
				if (columnGroup.getType() != null && columnGroup.getType().equalsIgnoreCase("header"))
					return columnGroup;
			}
		}
		
		return null;
	}
	
	protected List<Row> getRows(ColumnGroup columnGroup) {
		List<Row> rows = new ArrayList<Row>();
		
		for (UIComponent child : columnGroup.getChildren()) {
			if (child instanceof Row) {
				if (shouldExcludeFromExport(child)) continue;
				Row row = (Row) child;
				rows.add(row);
			}
		}
		
		return rows;
	}
	
	protected List<UIColumn> getRowColumnsToExport(Row row, UIData table, int[] excludedColumns) {
        List<UIColumn> columns = new ArrayList<UIColumn>();
		ArrayList<UIColumn> rowColumns = new ArrayList<UIColumn>();
        int columnIndex = -1;
		int rowColumnIndex = 0;

		SpanningRow spanningRow = spanningRows.getNextRow(); // fetch the set of previous columns that span to this row
        for (UIComponent child : row.getChildren()) {
			if (child instanceof UIColumn) {
				if (shouldExcludeFromExport(child)) continue;
				UIColumn uiColumn = (UIColumn) child;
				if (uiColumn.isRendered()) {
					if (uiColumn instanceof Column) {

						if (spanningRow != null) { // add previous columns that span multiple rows
							int added = spanningRow.addColumnsTo(rowColumns, rowColumnIndex);
							rowColumnIndex = rowColumnIndex + added;
						}
						Column column = (Column) uiColumn;
						int colspan = column.getColspan();
						int rowspan = column.getRowspan();
						for (int i = 0; i < colspan; i++) {
							rowColumns.add(column);
							for (int j = 1; j < rowspan; j++) { // register which columns span multiple rows
								spanningRows.addColumn(column, j-1, rowColumnIndex);
							}
							rowColumnIndex++;
						}
					} else {
						rowColumns.add(uiColumn);
						rowColumnIndex++;
					}
				}
			}
		}
		
		if (spanningRow != null) spanningRow.addColumnsTo(rowColumns, rowColumnIndex);
		rowColumnIndex = -1;
		
		for (UIComponent child : table.getChildren()) {
            if (child instanceof UIColumn) {
				if (shouldExcludeFromExport(child)) continue;
                UIColumn column = (UIColumn) child;
                columnIndex++;

				boolean hasExpansionToggler = false;
                for (UIComponent columnChild : column.getChildren())
					if (columnChild instanceof ExpansionToggler) hasExpansionToggler = true;
				if (hasExpansionToggler) continue;
				
				if (column.isRendered()) {
					rowColumnIndex++;
					if (excludedColumns == null || Arrays.binarySearch(excludedColumns, columnIndex) < 0) {
						if (rowColumnIndex < rowColumns.size()) {
							UIColumn rowColumn = rowColumns.get(rowColumnIndex);
							if (rowColumn != null) {
								columns.add(rowColumn);
								continue;
							}
						}
						columns.add(new UIColumn());
					}
				}
            }
        }
        return columns;
    }

    protected boolean hasColumnFooter(List<UIColumn> columns) {
        for (UIColumn column : columns) {
            if (column.getFooter() != null) return true;
			if (column instanceof Column) {
				if (((Column) column).getFooterText() != null) return true;
			}
		}

        return false;
    }
	
	protected boolean shouldExcludeFromExport(UIComponent component) {
	
		for (UIComponent child : component.getChildren()) {
            if (child instanceof ExcludeFromExport) {	
				if (child.isRendered()) return true;
			}
		}
		return false;
	}

    protected String exportValue(FacesContext context, UIComponent component) {
		if (shouldExcludeFromExport(component)) return "";
		if (component instanceof CellEditor) {
			UIComponent facet = component.getFacet("output");
			if (facet != null) {
				component = facet;
			} else {
				facet = component.getFacet("input");
				if (facet != null) {
					component = facet;
				}				
			}
        }
		if (component instanceof HtmlCommandLink) {
            HtmlCommandLink link = (HtmlCommandLink) component;
            Object value = link.getValue();

            if (value != null) return String.valueOf(value);
            else {
                //export first value holder
                for (UIComponent child : link.getChildren())
                    if (child instanceof ValueHolder)
                        return exportValue(context, child);
                return null;
            }

        } else if (component instanceof EditableValueHolder) {
			EditableValueHolder editableValueHolder = (EditableValueHolder) component;
            Object value = editableValueHolder.getValue();
			
			if (value == null) return "";
			
            else if (editableValueHolder.getConverter() != null)
                return editableValueHolder.getConverter().getAsString(context, component, value);

            Class<?> valueType;
            ValueExpression expr = component.getValueExpression("value");
            if (expr != null) if ((valueType = expr.getType(context.getELContext())) != null) {
                Converter converterForType = context.getApplication().createConverter(valueType);
                if (converterForType != null) return converterForType.getAsString(context, component, value);
            }
			
			return value.toString();

        } else if (component instanceof ValueHolder) {
			ValueHolder valueHolder = (ValueHolder) component;
			Object value = valueHolder.getValue();

            if (value == null) return "";

            else if (valueHolder.getConverter() != null)
                return valueHolder.getConverter().getAsString(context, component, value);

            Class<?> valueType;
            ValueExpression expr = component.getValueExpression("value");
            if (expr != null) if ((valueType = expr.getType(context.getELContext())) != null) {
                Converter converterForType = context.getApplication().createConverter(valueType);
                if (converterForType != null) return converterForType.getAsString(context, component, value);
            }

			return value.toString();
        }
        
		String ret = "";
        //This would get the plain texts on UIInstructions when using Facelets
        String value = component.toString();
        if (value != null) {
			value = value.trim();
			String objectReference = component.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(component));
			if (!value.equals(objectReference)) ret = value;
		}
		
		if (component.getChildren().size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (UIComponent child : component.getChildren()) {
				builder.append(exportValue(context, child));
			}
			ret += builder.toString();
		}
        return ret;
    }


    protected enum ColumnType {
        HEADER("header"),
        FOOTER("footer");
        private final String facet;
        ColumnType(String facet) {
            this.facet = facet;
        }
        public String facet() {
            return facet;
        }
        @Override
        public String toString() {
            return facet;
        }
    }
	
	protected class SpanningRows {
	
		protected ArrayList<SpanningRow> rows = new ArrayList<SpanningRow>();
		protected int currentIndex = 0;
		
		protected SpanningRow getNextRow() {
			if (currentIndex < rows.size()) {
				int index = currentIndex;
				currentIndex++;
				return rows.get(index);
			} else {
				return null;
			}
		}
		
		protected void addColumn(UIColumn column, int to, int index) {
			if ((currentIndex + to) >= rows.size()) {
				rows.add(currentIndex + to, new SpanningRow());
			}
			rows.get(currentIndex + to).addColumn(column, index);
		}
	}
	
	protected static class SpanningRow {
	
		protected ArrayList<SpanningColumn> spanningColumns = new ArrayList<SpanningColumn>();
		
		protected int addColumnsTo(ArrayList<UIColumn> columns, int index) {
			boolean found = false;
			int myIndex = index;
			int added = 0;
			do {
				found = false;
				for (int i = 0; i < spanningColumns.size(); i++) {
					SpanningColumn spanningColumn = spanningColumns.get(i);
					if (spanningColumn.index == myIndex) {
						columns.add(spanningColumn.column);
						added++;
						myIndex++; // try to add the next one
						found = true;
					}
				}
			} while (found);
			return added;
		}
		
		protected void addColumn(UIColumn column, int index) {
			SpanningColumn spanningColumn = new SpanningColumn();
			spanningColumn.column = column;
			spanningColumn.index = index;
			spanningColumns.add(spanningColumn);
		}
	}
	
	protected static class SpanningColumn {
	
		protected UIColumn column;
		protected int index;
	}
}
