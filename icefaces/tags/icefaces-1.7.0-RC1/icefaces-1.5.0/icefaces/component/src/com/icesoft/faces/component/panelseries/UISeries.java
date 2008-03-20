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

package com.icesoft.faces.component.panelseries;

import com.icesoft.faces.component.tree.TreeDataModel;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlForm;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;
import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.ResultDataModel;
import javax.faces.model.ResultSetDataModel;
import javax.faces.model.ScalarDataModel;
import javax.servlet.jsp.jstl.sql.Result;
import javax.swing.tree.DefaultTreeModel;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * This is an extended version of UIData, which allows any UISeries type of
 * component to have any type of children, it is not restricted to use the
 * column component as a first child.
 */
public class UISeries extends HtmlDataTable {
    public static final String COMPONENT_TYPE = "com.icesoft.faces.series";
    public static final String RENDERER_TYPE = "com.icesoft.faces.seriesRenderer";    
    protected transient DataModel dataModel = null;
    private int rowIndex = -1;
    protected Map savedChildren = new HashMap();
    private String var = null;


    public UISeries() {
        super();
        setRendererType(RENDERER_TYPE);
    }

    /**
     * @see javax.faces.component.UIData#isRowAvailable()
     */
    public boolean isRowAvailable() {
        return (getDataModel().isRowAvailable());
    }


    /**
     * @see javax.faces.component.UIData#getRowCount()
     */
    public int getRowCount() {
        return (getDataModel().getRowCount());
    }


    /**
     * @see javax.faces.component.UIData#getRowData()
     */
    public Object getRowData() {
        return (getDataModel().getRowData());
    }


    /**
     * @see javax.faces.component.UIData#getRowIndex()
     */
    public int getRowIndex() {
        return (this.rowIndex);
    }

    public void setRowIndex(int rowIndex) {
        FacesContext facesContext = getFacesContext();
        // Save current state for the previous row index
        saveChildrenState(facesContext);
        // remove or load the current row data as a request scope attribute        
        processCurrentRowData(facesContext, rowIndex);
        // Reset current state information for the new row index
        restoreChildrenState(facesContext);
    }

    private void processCurrentRowData(FacesContext facesContext,
                                       int rowIndex) {
        this.rowIndex = rowIndex;
        DataModel model = getDataModel();
        model.setRowIndex(rowIndex);

        if (var != null) {
            Map requestMap = facesContext.getExternalContext().getRequestMap();
            if (rowIndex == -1) {
                loadRowToRequestMap(requestMap, false);
            } else if (isRowAvailable()) {
                loadRowToRequestMap(requestMap, true);
            } else {
                loadRowToRequestMap(requestMap, false);
            }
        }
    }

    private void loadRowToRequestMap(Map requestMap, boolean loadRow) {
        if (loadRow) {
            requestMap.put(var, getRowData());
        } else {
            requestMap.remove(var);
        }
    }


    /**
     * @see javax.faces.component.UIData#getVar()
     */
    public String getVar() {
        return (this.var);
    }


    /**
     * @see javax.faces.component.UIData#setVar(Stringvar)
     */
    public void setVar(String var) {
        this.var = var;
    }

    /**
     * @see javax.faces.component.UIData#setValue(Objectvalue)
     */
    public void setValue(Object value) {
        this.dataModel = null;
        super.setValue(value);
    }


    public void setValueBinding(String name, ValueBinding binding) {
        if ("value".equals(name)) {
            this.dataModel = null;
        } else if ("var".equals(name) || "rowIndex".equals(name)) {
            throw new IllegalArgumentException();
        }
        super.setValueBinding(name, binding);
    }


    /**
     * @see javax.faces.component.UIData#getClientId(FacesContextcontext)
     */
    public String getClientId(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        String baseClientId = super.getClientId(context);
        if (getRowIndex() >= 0) {
            //this extra if is to produce the same ids among myfaces and sunri
            //myfaces uses the getRowIndex() and SunRI directly using the rowIndex 
            //variable inside its getClientId()
            if (!baseClientId.endsWith(
                    "" + NamingContainer.SEPARATOR_CHAR + getRowIndex())) {
                return (baseClientId + NamingContainer.SEPARATOR_CHAR +
                        getRowIndex());
            }
            return (baseClientId);
        } else {
            return (baseClientId);
        }
    }


    /**
     * @see javax.faces.component.UIData#queueEvent(FacesEventevent)
     */
    public void queueEvent(FacesEvent event) {
        super.queueEvent(new RowEvent(this, event, getRowIndex()));
    }


    /**
     * @see javax.faces.component.UIData#broadcast(FacesEventevent)
     */
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if (!(event instanceof RowEvent)) {
            super.broadcast(event);
            return;
        }

        // fire row specific event
        ((RowEvent) event).broadcast();
        return;
    }


    /**
     * @see javax.faces.component.UIData#encodeBegin(FacesContextcontext)
     */
    public void encodeBegin(FacesContext context) throws IOException {
        dataModel = null;
        if (!keepSaved(context)) {
            savedChildren = new HashMap();
        }
        super.encodeBegin(context);
    }


    public void processDecodes(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (!isRendered()) {
            return;
        }

        dataModel = null;
        if (null == savedChildren || !keepSaved(context)) {
            savedChildren = new HashMap();
        }

        iterate(context, PhaseId.APPLY_REQUEST_VALUES);
        decode(context);
    }

    public void processValidators(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (!isRendered()) {
            return;
        }
        if (isNestedWithinUIData()) {
            dataModel = null;
        }
        iterate(context, PhaseId.PROCESS_VALIDATIONS);
    }


    public void processUpdates(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        if (!isRendered()) {
            return;
        }
        if (isNestedWithinUIData()) {
            dataModel = null;
        }
        iterate(context, PhaseId.UPDATE_MODEL_VALUES);
    }

    /**
     * <p>Return the DataModel containing the Objects that will be iterated over
     * when this component is rendered.</p>
     *
     * @return
     */
    private DataModel getDataModel() {
        if (null != this.dataModel) {
            return (dataModel);
        }

        Object currentValue = getValue();

        if (null == currentValue) {
            this.dataModel = new ListDataModel(Collections.EMPTY_LIST);
        } else if (currentValue instanceof DataModel) {
            this.dataModel = (DataModel) currentValue;
        } else if (currentValue instanceof List) {
            this.dataModel = new ListDataModel((List) currentValue);
        } else if (Object[].class.isAssignableFrom(currentValue.getClass())) {
            this.dataModel = new ArrayDataModel((Object[]) currentValue);
        } else if (currentValue instanceof ResultSet) {
            this.dataModel = new ResultSetDataModel((ResultSet) currentValue);
        } else if (currentValue instanceof Result) {
            this.dataModel = new ResultDataModel((Result) currentValue);
        } else if (currentValue instanceof DefaultTreeModel) {
            this.dataModel = new TreeDataModel((DefaultTreeModel) currentValue);
        } else {
            this.dataModel = new ScalarDataModel(currentValue);
        }

        return (dataModel);
    }

    protected void iterate(FacesContext facesContext, PhaseId phase) {
        // clear rowIndex
        setRowIndex(-1);

        int rowsProcessed = 0;
        int currentRowIndex = getFirst() - 1;
        int displayedRows = getRows();
        // loop over dataModel processing each row once
        while (1 == 1) {
            // break if we have processed the number of rows requested
            if ((displayedRows > 0) && (++rowsProcessed > displayedRows)) {
                break;
            }
            // process the row at currentRowIndex
            setRowIndex(++currentRowIndex);
            // break if we've moved past the last row
            if (!isRowAvailable()) {
                break;
            }
            // loop over children and facets
            Iterator children = getFacetsAndChildren();
            while (children.hasNext()) {
                UIComponent child = (UIComponent) children.next();
                if (phase == PhaseId.APPLY_REQUEST_VALUES) {
                    child.processDecodes(facesContext);
                } else if (phase == PhaseId.PROCESS_VALIDATIONS) {
                    child.processValidators(facesContext);
                } else if (phase == PhaseId.UPDATE_MODEL_VALUES) {
                    child.processUpdates(facesContext);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        // clear rowIndex
        setRowIndex(-1);
    }

    /**
     * <p>Return true when we need to keep the child state to display error
     * messages.</p>
     *
     * @param facesContext
     * @return
     */
    private boolean keepSaved(FacesContext facesContext) {
        Iterator childIds = savedChildren.keySet().iterator();
        while (childIds.hasNext()) {
            String childId = (String) childIds.next();
            Iterator childMessages = facesContext.getMessages(childId);
            while (childMessages.hasNext()) {
                FacesMessage childMessage = (FacesMessage) childMessages.next();
                if (childMessage.getSeverity()
                        .compareTo(FacesMessage.SEVERITY_ERROR) >= 0) {
                    return (true);
                }
            }
        }
        // return true if this component is nested inside a UIData 
        return (isNestedWithinUIData());
    }

    private boolean isNestedWithinUIData() {
        UIComponent parent = this;
        while (null != (parent = parent.getParent())) {
            if (parent instanceof UIData) {
                return true;
            }
        }
        return (false);
    }


    protected void restoreChildrenState(FacesContext facesContext) {
        Iterator children = getChildren().iterator();
        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            restoreChildState(facesContext, child);
        }
    }


    protected void restoreChildState(FacesContext facesContext,
                                     UIComponent component) {
        String id = component.getId();
        if (!isValid(id)) {
            return;
        }
        component.setId(id);
        restoreChild(facesContext, component);
        Iterator children = component.getChildren().iterator();
        while (children.hasNext()) {
            restoreChildState(facesContext, (UIComponent) children.next());
        }
    }

    protected void saveChild(FacesContext facesContext, UIComponent component) {
        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(facesContext);
            ChildState state = (ChildState) savedChildren.get(clientId);
            if (state == null) {
                state = new ChildState();
                savedChildren.put(clientId, state);
            }
            state.setValue(input.getLocalValue());
            state.setValid(input.isValid());
            state.setSubmittedValue(input.getSubmittedValue());
            state.setLocalValueSet(input.isLocalValueSet());
        } else if (component instanceof HtmlForm) {
            String clientId = component.getClientId(facesContext);
            Boolean isThisFormSubmitted = (Boolean) savedChildren.get(clientId);
            if (isThisFormSubmitted == null) {
                isThisFormSubmitted =
                        new Boolean(((HtmlForm) component).isSubmitted());
                savedChildren.put(clientId, isThisFormSubmitted);
            }
        }
    }

    protected void restoreChild(FacesContext facesContext,
                                UIComponent component) {
        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(facesContext);
            ChildState state = (ChildState) savedChildren.get(clientId);
            if (state == null) {
                state = new ChildState();
            }
            input.setValue(state.getValue());
            input.setValid(state.isValid());
            input.setSubmittedValue(state.getSubmittedValue());
            input.setLocalValueSet(state.isLocalValueSet());
        } else if (component instanceof HtmlForm) {
            String clientId = component.getClientId(facesContext);
            Boolean isThisFormSubmitted = (Boolean) savedChildren.get(clientId);
            if (isThisFormSubmitted == null) {
                isThisFormSubmitted = Boolean.FALSE;
            }
            ((HtmlForm) component)
                    .setSubmitted(isThisFormSubmitted.booleanValue());
        }
    }

    protected void saveChildrenState(FacesContext facesContext) {
        Iterator children = getChildren().iterator();
        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            saveChildState(facesContext, child);
        }
    }

    protected void saveChildState(FacesContext facesContext,
                                  UIComponent component) {
        saveChild(facesContext, component);
        Iterator children = component.getChildren().iterator();
        while (children.hasNext()) {
            saveChildState(facesContext, (UIComponent) children.next());
        }
    }

    //  Event associated with the specific rowindex
    class RowEvent extends FacesEvent {
        private FacesEvent event = null;
        private int eventRowIndex = -1;

        public RowEvent(UIComponent component, FacesEvent event,
                        int eventRowIndex) {
            super(component);
            this.event = event;
            this.eventRowIndex = eventRowIndex;
        }

        public FacesEvent getFacesEvent() {
            return (this.event);
        }

        public boolean isAppropriateListener(FacesListener listener) {
            return false;
        }

        public void processListener(FacesListener listener) {
            throw new IllegalStateException();
        }

        public PhaseId getPhaseId() {
            return (this.event.getPhaseId());
        }

        public void setPhaseId(PhaseId phaseId) {
            this.event.setPhaseId(phaseId);
        }

        public void broadcast() {
            int oldRowIndex = getRowIndex();
            setRowIndex(eventRowIndex);
            event.getComponent().broadcast(event);
            setRowIndex(oldRowIndex);
        }
    }

    public Object saveState(FacesContext context) {
        Object values[] = new Object[4];
        values[0] = super.saveState(context);
        values[1] = new Integer(rowIndex);
        values[2] = savedChildren;
        values[3] = var;
        return (values);
    }


    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        rowIndex = ((Integer) values[1]).intValue();
        savedChildren = (Map) values[2];
        var = (String) values[3];
    }

    private boolean isValid(String id) {
        if (id == null) {
            return false;
        }
        if (!Character.isLetter(id.charAt(0)) && (id.charAt(0) != '_')) {
            return false;
        }
        return true;
    }

}

class ChildState implements Serializable {

    private Object submittedValue;
    private boolean valid = true;
    private Object value;
    private boolean localValueSet;

    Object getSubmittedValue() {
        return submittedValue;
    }

    void setSubmittedValue(Object submittedValue) {
        this.submittedValue = submittedValue;
    }

    boolean isValid() {
        return valid;
    }

    void setValid(boolean valid) {
        this.valid = valid;
    }

    Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    boolean isLocalValueSet() {
        return localValueSet;
    }

    public void setLocalValueSet(boolean localValueSet) {
        this.localValueSet = localValueSet;
    }
}


