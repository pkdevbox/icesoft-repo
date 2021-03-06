/*
 * Copyright 2004-2012 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.icemobile.jsp.tags;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.Writer;

/**
 *
 */
public class CommandButtonTag extends SimpleTagSupport {

    // Default button types.
    public static final String BUTTON_TYPE_DEFAULT = "default";
    public static final String BUTTON_TYPE_IMPORTANT = "important";
    public static final String BUTTON_TYPE_ATTENTION = "attention";
    public static final String BUTTON_TYPE_BACK = "back";

    // button styles.
    public static final String BASE_STYLE_CLASS = "mobi-button";
    public static final String DISABLED_STYLE_CLASS = " mobi-button-dis";
    public static final String DEFAULT_STYLE_CLASS = " mobi-button-default";
    public static final String IMPORTANT_STYLE_CLASS = " mobi-button-important";
    public static final String BACK_STYLE_CLASS = " mobi-button-back";
    public static final String ATTENTION_STYLE_CLASS = " mobi-button-attention";
    public static final String SELECTED_STYLE_CLASS = " mobi-button-selected";

    private String id;
    private String name;
    private String style;
    private String styleClass;
    private String buttonType;

    private boolean disabled;
    private String value;
    private String type;
    private boolean selected;

    public void doTag() throws IOException {


        PageContext pageContext = (PageContext) getJspContext();
        Writer out = pageContext.getOut();
        ServletRequest sr = pageContext.getRequest();

        out.write("<input ");

        if (id != null && !"".equals(id)) {
            out.write(" id='" + getId() + "'");
        }

        if (name != null && !"".equals(name)) {
            out.write(" name='" + getName() + "'");
        }

        StringBuilder builder = new StringBuilder(BASE_STYLE_CLASS);
        if (BUTTON_TYPE_DEFAULT.equals(buttonType)) {
            builder.append(DEFAULT_STYLE_CLASS);
        } else if (BUTTON_TYPE_BACK.equals(buttonType)) {
            builder.append(BACK_STYLE_CLASS);
        } else if (BUTTON_TYPE_ATTENTION.equals(buttonType)) {
            builder.append(ATTENTION_STYLE_CLASS);
        } else if (BUTTON_TYPE_IMPORTANT.equals(buttonType)) {
            builder.append(IMPORTANT_STYLE_CLASS);
        }

        // apply selected state if any
        if (isSelected()) {
            builder.append(SELECTED_STYLE_CLASS);
        }
        // apply disabled style state if specified.
        if (isDisabled()) {
            builder.append(DISABLED_STYLE_CLASS);
        }
        // append any user specific style attributes
        if (styleClass != null) {
            builder.append(" ").append(styleClass);
        }
        out.write(" class='" + builder.toString() + "'");

        // should be auto base though
        if (null != style) {
            out.write(" style='" + style + "'");
        }
        // button type for styling purposes
        if (type == null) {
            out.write(" type='submit'");
        } else {
            out.write(" type='" + type + "'");
        }

        if (isDisabled()) {
            out.write(" disabled=\"true\"");
        }

        if (null != value) {
            out.write(" value='" + value + "'");
        }
        out.write("/>");

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
