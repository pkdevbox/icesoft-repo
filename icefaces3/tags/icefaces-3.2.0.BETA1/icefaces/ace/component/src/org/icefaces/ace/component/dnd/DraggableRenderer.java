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
package org.icefaces.ace.component.dnd;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

//import org.icefaces.ace.component.dashboard.Dashboard;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.render.MandatoryResourceComponent;

import org.icefaces.ace.util.JSONBuilder;

@MandatoryResourceComponent(tagName="draggable", value="org.icefaces.ace.component.dnd.Draggable")
public class DraggableRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        Draggable draggable = (Draggable) component;
        String clientId = draggable.getClientId(facesContext);
        String target = findTarget(facesContext, draggable);
//        String dashboard = draggable.getDashboard();

        writer.startElement("span", draggable);
		writer.writeAttribute("id", clientId, null);
		writer.startElement("script", draggable);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("ice.ace.jq(function() {");
        writer.write(this.resolveWidgetVar(draggable) + " = new ");
		JSONBuilder jb = JSONBuilder.create();
		jb.beginFunction("ice.ace.Draggable");
			jb.item(clientId);
			jb.beginMap();
			jb.entry("target", target);System.out.println("* " + draggable.getCursor());
			jb.entryNonNullValue("cursor", draggable.getCursor());
        //Configuration
        if (draggable.isDisabled())
            jb.entry("disabled", true);
        if (draggable.getAxis() != null)
            jb.entry("axis", draggable.getAxis());
        if (draggable.getContainment() != null)
            jb.entry("containment", draggable.getContainment());
        if (draggable.getHelper() != null)
            jb.entry("helper", draggable.getHelper());
        if (draggable.isRevert())
            jb.entry("revert", "invalid");
        if (draggable.getZindex() != -1)
            jb.entry("zIndex", draggable.getZindex());
        if (draggable.getHandle() != null)
            jb.entry("handle", draggable.getHandle());
        if (draggable.getOpacity() != 1.0)
            jb.entry("opacity", draggable.getOpacity());
        if (draggable.getStack() != null)
            jb.entry("stack", draggable.getStack());
        if (draggable.getGrid() != null)
            jb.entry("grid", "[" + draggable.getGrid() + "]", true);
        if (draggable.getScope() != null)
            jb.entry("scope", draggable.getScope());
        if (draggable.isSnap()) {
            jb.entry("snap", true);
            jb.entry("snapTolerance", draggable.getSnapTolerance());
            if (draggable.getSnapMode() != null)
                jb.entry("snapMode", draggable.getSnapMode());
        }
        //Dashboard support
/*
        if (dashboard != null) {
            Dashboard db = (Dashboard) draggable.findComponent(dashboard);
            if (db == null) throw new FacesException("Cannot find dashboard \"" + dashboard + "\" in view");
            writer.write(",connectToSortable:'" + ComponentUtils.escapeJQueryId(db.getClientId(facesContext)) + " .ui-dashboard-column'");
        }
*/
        jb.endMap().endFunction();
		writer.write(jb.toString());

        writer.write("});");
        writer.endElement("script");
		writer.endElement("span");
    }

    protected String findTarget(FacesContext facesContext, Draggable draggable) {
        String _for;
        UIComponent component;

        if ((_for = draggable.getFor()) != null)
            if ((component = draggable.findComponent(_for)) == null)
                throw new FacesException("Cannot find component \"" + _for + "\" in view.");
            else return component.getClientId(facesContext);
        else  return draggable.getParent().getClientId(facesContext);
    }
}
