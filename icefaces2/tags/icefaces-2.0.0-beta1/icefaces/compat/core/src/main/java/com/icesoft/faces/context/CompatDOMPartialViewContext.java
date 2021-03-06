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

package com.icesoft.faces.context;

import com.icesoft.faces.context.effects.JavascriptContext;
import org.icefaces.context.DOMPartialViewContext;
import org.icefaces.context.DOMResponseWriter;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.PartialViewContext;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class CompatDOMPartialViewContext extends DOMPartialViewContext {

    public CompatDOMPartialViewContext(PartialViewContext partialViewContext, FacesContext facesContext) {
        super(partialViewContext, facesContext);
    }

    protected Writer getResponseOutputWriter() throws IOException {
        return new WriteViewStateMarkup(super.getResponseOutputWriter());
    }

    protected void renderExtensions() {
        String focusId = facesContext.getExternalContext().getRequestParameterMap().get("ice.focus");
        if (focusId != null && !focusId.equals("null") && !focusId.equals("undefined")) {
            JavascriptContext.focus(facesContext, focusId);
        }

        String javascriptCalls = JavascriptContext.getJavascriptCalls(facesContext);
        if (javascriptCalls != null && javascriptCalls.trim().length() > 0) {
            try {
                PartialResponseWriter partialWriter = getPartialResponseWriter();
                partialWriter.startEval();
                partialWriter.write(javascriptCalls);
                partialWriter.endEval();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class WriteViewStateMarkup extends FilterWriter {
        protected WriteViewStateMarkup(Writer out) {
            super(out);
        }

        public void write(String str) throws IOException {
            if (DOMResponseWriter.STATE_FIELD_MARKER.equals(str)) {
                out.write("<input id=\"javax.faces.ViewState\" type=\"hidden\" autocomplete=\"off\" value=\"");
                out.write(facesContext.getApplication().getStateManager().getViewState(facesContext));
                out.write("\" name=\"javax.faces.ViewState\"/>");
            } else {
                out.write(str);
            }
        }
    }
}
