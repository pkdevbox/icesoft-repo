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

package com.icesoft.faces.component.ext;

import javax.faces.component.UIComponentBase;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;

public class HtmlCheckbox extends UIComponentBase {
    private String _for = null;
    private Integer _index = null;

    public HtmlCheckbox() {
        super();
        setRendererType(HtmlSelectManyCheckbox.RENDERER_TYPE);
    }

    public String getFamily() {
        return "com.icesoft.faces.HtmlCheckbox";
    }

    public void setFor(String forValue) {
        _for = forValue;
    }

    public String getFor() {
        if (_for != null) return _for;
        ValueBinding vb = getValueBinding("for");
        if (vb == null) return null;
        Object value = vb.getValue(getFacesContext());
        if (value == null) return null;
        return value.toString();
    }

    public void setIndex(int index) {
        _index = new Integer(index);
    }

    public int getIndex() {
        if (_index != null) return _index.intValue();
        ValueBinding vb = getValueBinding("index");
        Number v = vb != null ? (Number) vb.getValue(getFacesContext()) : null;
        return v != null ? v.intValue() : Integer.MIN_VALUE;
    }

    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
        values[1] = _for;
        values[2] = _index;
        return values;
    }

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _for = (String) values[1];
        _index = (Integer) values[2];
    }
}
