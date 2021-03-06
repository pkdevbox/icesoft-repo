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

package com.icesoft.faces.el;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;

public class PartialSubmitValueBinding extends ValueBinding {
    private ValueBinding wrapped;
    private UIInput input;
    private String clientId;

    public PartialSubmitValueBinding(ValueBinding wrapped, UIInput input, String clientId) {
        this.wrapped = wrapped;
        this.input = input;
        this.clientId = clientId;
    }

    public Object getValue(FacesContext facesContext)
            throws EvaluationException, PropertyNotFoundException {
        //PartialSubmit dictates that we obey "required" only for
        //the single component causing the PartialSubmit. Components
        //used in an iterative fashion have different client IDs for
        //the same UIComponent instance.
        if (clientId.equals(input.getClientId(facesContext))) {
            return wrapped.getValue(facesContext);
        }
        return Boolean.FALSE;
    }

    public void setValue(FacesContext facesContext, Object value)
            throws EvaluationException,
            PropertyNotFoundException {
        wrapped.setValue(facesContext, value);
    }

    public boolean isReadOnly(FacesContext facesContext)
            throws EvaluationException, PropertyNotFoundException {
        return wrapped.isReadOnly(facesContext);
    }

    public Class getType(FacesContext facesContext)
            throws EvaluationException, PropertyNotFoundException {
        return wrapped.getType(facesContext);
    }

    public String getExpressionString() {
        return wrapped.getExpressionString();
    }

}

