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
 *
 */

package org.icemobile.jsp.tags;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

public class InputTextTag extends SimpleTagSupport {

    public static final String INPUT_TEXT_CLASS = "mobi-input-text";
    private static Logger LOG = Logger.getLogger(InputTextTag.class.getName());

    private String id;
    private String name;
    private String style;
    private String styleClass;
    private String type;
    private String value;

    // textarea only
    private int cols;
    private int rows;

    // passthrough attributes for html5
    private String autoCorrect;
    private String placeholder;

    public void doTag() throws IOException {

        PageContext pageContext = (PageContext) getJspContext();
        Writer out = pageContext.getOut();

        String element = "input";
        String type = getType();
        boolean isTextArea = false;
        if (type != null && type.equalsIgnoreCase("textarea")) {
            element = "textarea";
            isTextArea = true;
        }
        out.write("<" + element);
        if (id != null && !"".equals(id)) {
            out.write(" id=\"" + getId() + "\"");
        }
        if (name != null && !"".equals(name)) {
            out.write(" name=\"" + getName() + "\"");
        } else {
            LOG.warning("InputText tag (id: " + id + ") has no name for submission");
        }

        StringBuilder baseClass = new StringBuilder(INPUT_TEXT_CLASS);
        String styleClass = getStyleClass();
        if (styleClass != null && !"".equals(styleClass)) {
            baseClass.append(" ").append(styleClass);
        }
        // apply pass through attributes.
        out.write(" class=\"" + baseClass.toString() + "\"");
        out.write(" style=\"" + style + "\"");
        out.write(" autocorrect=\"" + autoCorrect + "\"");
        out.write(" placeholder=\"" + placeholder + "\"");
        // apply textarea passthough attributes.
        if (rows > 0) {
            out.write(" rows=\"" + this.type + "\"");
        }
        if (cols > 0) {
            out.write(" cols=\"" + this.type + "\"");
        }
        String value = getValue();
        if (!isTextArea) {
            out.write(" type=\"" + this.type + "\"");
            if (value != null) {
                out.write(" value=\"" + value + "\"");
            }
            out.write("/>");
        } else {
            out.write(">");
            if (value != null) {
                out.write(value);
            }
            out.write("</textarea>");
        }
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAutoCorrect() {
        return autoCorrect;
    }

    public void setAutoCorrect(String autoCorrect) {
        this.autoCorrect = autoCorrect;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}