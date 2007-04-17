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

package com.icesoft.icefaces.samples.showcase.components.textEntry;

import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;

/**
 * <p>The TextFieldsBean class is the backing bean for the Text Entry
 * demonstration. It is used to store the values of the input fields.</p>
 */
@Name("textFields")
public class TextFieldsBean {
    /**
     * The different kinds of text input fields.
     */
    private String name;
    private String password;
    private String comments;

    /**
     * Gets the name property.
     *
     * @return value of name property
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name property
     *
     * @param newValue new value of the name property
     */
    public void setName(String newValue) {
        if (dontSet()) return;

        name = newValue;
    }

    /**
     * Gets the password property.
     *
     * @return value of the password property
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password property.
     *
     * @param newValue new value of the password property
     */
    public void setPassword(String newValue) {
        if (dontSet()) return;

        password = newValue;
    }

    /**
     * Gets the comments property.
     *
     * @return value of the comments property
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments property.
     *
     * @param newValue new value of the comments property
     */
    public void setComments(String newValue) {
        if (dontSet()) return;

        comments = newValue;
    }

    private boolean dontSet() {
        return FacesContext.getCurrentInstance().getExternalContext()
                .getRequestMap().get("RESET") != null;
    }

    /**
     * Restore the demo to its original state.
     *
     * @return "Done" for navigation rules
     */
    public String reset() {

        FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
                .put("RESET", Boolean.TRUE);//partial submit sends too much.
        name = "";
        password = "";
        comments = "";

        return "Done";
    }
}