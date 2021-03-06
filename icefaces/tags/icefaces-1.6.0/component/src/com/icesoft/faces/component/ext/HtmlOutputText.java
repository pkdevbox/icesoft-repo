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

package com.icesoft.faces.component.ext;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.JavascriptContext;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;


/**
 * This is an extension of javax.faces.component.html.HtmlOutputText, which
 * provides some additional behavior to this component such as: <ul> <li>changes
 * the component's rendered state based on the authentication</li> <li>adds
 * effects to the component</li> <ul>
 */
public class HtmlOutputText extends javax.faces.component.html.HtmlOutputText {
    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.HtmlOutputText";
    public static final String RENDERER_TYPE = "com.icesoft.faces.Text";
    private static final boolean DEFAULT_VISIBLE = true;
    private String styleClass = null;
    private String renderedOnUserRole = null;
    private Effect effect;
    private Boolean visible = null;

    private CurrentStyle currentStyle;

    private Effect onclickeffect;
    private Effect ondblclickeffect;
    private Effect onmousedowneffect;
    private Effect onmouseupeffect;
    private Effect onmousemoveeffect;
    private Effect onmouseovereffect;
    private Effect onmouseouteffect;
    private Effect onchangeeffect;
    private Effect onkeypresseffect;
    private Effect onkeydowneffect;
    private Effect onkeyupeffect;

    public HtmlOutputText() {
        super();
        setRendererType(RENDERER_TYPE);
    }


    public void setValueBinding(String s, ValueBinding vb) {

        if (s != null && s.indexOf("effect") != -1) {

            // If this is an effect attribute make sure Ice Extras is included
            JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                         getFacesContext());
        }
        super.setValueBinding(s, vb);
    }

    /**
     * <p>Set the value of the <code>effect</code> property.</p>
     */
    public void setEffect(Effect effect) {
        this.effect = effect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>effect</code> property.</p>
     */
    public Effect getEffect() {
        if (effect != null) {
            return effect;
        }
        ValueBinding vb = getValueBinding("effect");
        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>visible</code> property.</p>
     */
    public void setVisible(boolean visible) {
        this.visible = Boolean.valueOf(visible);
    }

    /**
     * <p>Return the value of the <code>visible</code> property.</p>
     */
    public boolean getVisible() {
        if (visible != null) {
            return visible.booleanValue();
        }
        ValueBinding vb = getValueBinding("visible");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() : DEFAULT_VISIBLE;
    }

    /**
     * <p>Set the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * <p>Return the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                styleClass,
                CSS_DEFAULT.OUTPUT_TEXT_DEFAULT_STYLE_CLASS,
                "styleClass");    
    }

    /**
     * <p>Return the value of the <code>onclickeffect</code> property.</p>
     */
    public Effect getOnclickeffect() {
        if (onclickeffect != null) {
            return onclickeffect;
        }
        ValueBinding vb = getValueBinding("onclickeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onclickeffect</code> property.</p>
     */
    public void setOnclickeffect(Effect onclickeffect) {
        this.onclickeffect = onclickeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>ondblclickeffect</code> property.</p>
     */
    public Effect getOndblclickeffect() {
        if (ondblclickeffect != null) {
            return ondblclickeffect;
        }
        ValueBinding vb = getValueBinding("ondblclickeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>ondblclickeffect</code> property.</p>
     */
    public void setOndblclickeffect(Effect ondblclickeffect) {
        this.ondblclickeffect = ondblclickeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmousedowneffect</code> property.</p>
     */
    public Effect getOnmousedowneffect() {
        if (onmousedowneffect != null) {
            return onmousedowneffect;
        }
        ValueBinding vb = getValueBinding("onmousedowneffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmousedowneffect</code> property.</p>
     */
    public void setOnmousedowneffect(Effect onmousedowneffect) {
        this.onmousedowneffect = onmousedowneffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmouseupeffect</code> property.</p>
     */
    public Effect getOnmouseupeffect() {
        if (onmouseupeffect != null) {
            return onmouseupeffect;
        }
        ValueBinding vb = getValueBinding("onmouseupeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseupeffect</code> property.</p>
     */
    public void setOnmouseupeffect(Effect onmouseupeffect) {
        this.onmouseupeffect = onmouseupeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmousemoveeffect</code> property.</p>
     */
    public Effect getOnmousemoveeffect() {
        if (onmousemoveeffect != null) {
            return onmousemoveeffect;
        }
        ValueBinding vb = getValueBinding("onmousemoveeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmousemoveeffect</code> property.</p>
     */
    public void setOnmousemoveeffect(Effect onmousemoveeffect) {
        this.onmousemoveeffect = onmousemoveeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmouseovereffect</code> property.</p>
     */
    public Effect getOnmouseovereffect() {
        if (onmouseovereffect != null) {
            return onmouseovereffect;
        }
        ValueBinding vb = getValueBinding("onmouseovereffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseovereffect</code> property.</p>
     */
    public void setOnmouseovereffect(Effect onmouseovereffect) {
        this.onmouseovereffect = onmouseovereffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onmouseouteffect</code> property.</p>
     */
    public Effect getOnmouseouteffect() {
        if (onmouseouteffect != null) {
            return onmouseouteffect;
        }
        ValueBinding vb = getValueBinding("onmouseouteffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onmouseouteffect</code> property.</p>
     */
    public void setOnmouseouteffect(Effect onmouseouteffect) {
        this.onmouseouteffect = onmouseouteffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onchangeeffect</code> property.</p>
     */
    public Effect getOnchangeeffect() {
        if (onchangeeffect != null) {
            return onchangeeffect;
        }
        ValueBinding vb = getValueBinding("onchangeeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onchangeeffect</code> property.</p>
     */
    public void setOnchangeeffect(Effect onchangeeffect) {
        this.onchangeeffect = onchangeeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onkeypresseffect</code> property.</p>
     */
    public Effect getOnkeypresseffect() {
        if (onkeypresseffect != null) {
            return onkeypresseffect;
        }
        ValueBinding vb = getValueBinding("onkeypresseffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeypresseffect</code> property.</p>
     */
    public void setOnkeypresseffect(Effect onkeypresseffect) {
        this.onkeypresseffect = onkeypresseffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onkeydowneffect</code> property.</p>
     */
    public Effect getOnkeydowneffect() {
        if (onkeydowneffect != null) {
            return onkeydowneffect;
        }
        ValueBinding vb = getValueBinding("onkeydowneffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeydowneffect</code> property.</p>
     */
    public void setOnkeydowneffect(Effect onkeydowneffect) {
        this.onkeydowneffect = onkeydowneffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>onkeyupeffect</code> property.</p>
     */
    public Effect getOnkeyupeffect() {
        if (onkeyupeffect != null) {
            return onkeyupeffect;
        }
        ValueBinding vb = getValueBinding("onkeyupeffect");

        return vb != null ? (Effect) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>onkeyupeffect</code> property.</p>
     */
    public void setOnkeyupeffect(Effect onkeyupeffect) {
        this.onkeyupeffect = onkeyupeffect;
        JavascriptContext
                .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    /**
     * <p>Return the value of the <code>rendered</code> property.</p>
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    /**
     * <p>Return the value of the <code>currentStyle</code> property.</p>
     */
    public CurrentStyle getCurrentStyle() {
        return currentStyle;
    }

    /**
     * <p>Set the value of the <code>currentStyle</code> property.</p>
     */
    public void setCurrentStyle(CurrentStyle currentStyle) {
        this.currentStyle = currentStyle;
    }

    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[19];
        values[0] = super.saveState(context);
        values[1] = renderedOnUserRole;
        values[2] = styleClass;
        values[3] = effect;
        values[4] = onclickeffect;
        values[5] = ondblclickeffect;
        values[6] = onmousedowneffect;
        values[7] = onmouseupeffect;
        values[8] = onmousemoveeffect;
        values[9] = onmouseovereffect;
        values[10] = onmouseouteffect;
        values[11] = onchangeeffect;
        values[14] = onkeypresseffect;
        values[15] = onkeydowneffect;
        values[16] = onkeyupeffect;
        values[17] = currentStyle;
        values[18] = visible;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        renderedOnUserRole = (String) values[1];
        styleClass = (String) values[2];
        effect = (Effect) values[3];
        onclickeffect = (Effect) values[4];
        ondblclickeffect = (Effect) values[5];
        onmousedowneffect = (Effect) values[6];
        onmouseupeffect = (Effect) values[7];
        onmousemoveeffect = (Effect) values[8];
        onmouseovereffect = (Effect) values[9];
        onmouseouteffect = (Effect) values[10];
        onchangeeffect = (Effect) values[11];
        onkeypresseffect = (Effect) values[14];
        onkeydowneffect = (Effect) values[15];
        onkeyupeffect = (Effect) values[16];
        currentStyle = (CurrentStyle) values[17];
        visible = (Boolean) values[18];
    }
}
