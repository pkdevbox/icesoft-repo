/*
 * Version: MPL 1.1
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
 * 2004-2010 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 */

package org.icefaces.application.showcase.view.bean.examples.component.menuBar;

import org.icefaces.application.showcase.util.FacesUtils;
import org.icefaces.application.showcase.view.bean.BaseBean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * <p>The MenuBarBean class determines which menu item fired the ActionEvent and
 * stores the modified id information in a String. MenuBarBean also controls the
 * orientation of the Menu Bar.</p>
 */
@ManagedBean(name = "menuBar")
@ViewScoped
public class MenuBarBean extends BaseBean {

    // records which menu item fired the event
    private String actionFired;

    // records the param value for the menu item which fired the event
    private String param;

    // orientation of the menubar ("Horizontal" or "Vertical")
    private String orientation = "Horizontal";

    /**
     * Get the param value for the menu item which fired the event.
     *
     * @return the param value
     */
    public String getParam() {
        return param;
    }

    /**
     * Set the param value.
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * Get the modified ID of the fired action.
     *
     * @return the modified ID
     */
    public String getActionFired() {
        return actionFired;
    }

    /**
     * Identify the ID of the element that fired the event and return it in a
     * form suitable for display.
     *
     * @param e the event that fired the listener
     */
    public void primaryListener(ActionEvent e) {

        actionFired = ((UIComponent) e.getSource())
                .getClientId(FacesContext.getCurrentInstance());
        param = FacesUtils.getRequestParameter("myParam");

        // highlight server side backing bean values. 
        valueChangeEffect.setFired(false);

    }

    /**
     * Get the orientation of the menu ("horizontal" or "vertical")
     *
     * @return the orientation of the menu
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Set the orientation of the menu ("horizontal" or "vertical").
     *
     * @param orientation the new orientation of the menu
     */
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }


}