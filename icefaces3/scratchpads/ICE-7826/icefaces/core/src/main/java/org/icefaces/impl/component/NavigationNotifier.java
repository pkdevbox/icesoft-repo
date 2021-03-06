package org.icefaces.impl.component;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.Map;

@ResourceDependencies({
        @ResourceDependency(name = "navigation-notifier/blank.html"),
        @ResourceDependency(name = "navigation-notifier/json2007.js"),
        @ResourceDependency(name = "navigation-notifier/rsh.js")
})
public class NavigationNotifier extends UICommand {
    public NavigationNotifier() {
    }

    public String getFamily() {
        return getClass().getName();
    }

    public void decode(FacesContext context) {
        Map requestParameterMap = context.getExternalContext().getRequestParameterMap();
        String source = String.valueOf(requestParameterMap.get("javax.faces.source"));
        String clientId = getClientId();
        if (clientId.equals(source)) {
            queueEvent(new ActionEvent(this));
        }
    }

    public void encodeBegin(FacesContext context) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = getClientId();
        writer.startElement("span", this);
        writer.writeAttribute("id", id + "_notifier", null);

        writer.startElement("input", this);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("value", "", null);
        writer.endElement("input");

        writer.startElement("form", this);
        writer.writeAttribute("id", "rshStorageForm", null);
        writer.writeAttribute("style", "left:-1000px;top:-1000px;width:1px;height:1px;border:0;position:absolute;", null);
        writer.startElement("textarea", this);
        writer.writeAttribute("id", "rshStorageField", null);
        writer.writeAttribute("style", "left:-1000px;top:-1000px;width:1px;height:1px;border:0;position:absolute;", null);
        writer.endElement("textarea");
        writer.endElement("form");

        writer.startElement("script", this);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("window.dhtmlHistory.create();\n");
        writer.write("var init = function(newLocation, historyData) {\n");
        writer.write("dhtmlHistory.initialize();\n");
        writer.write("dhtmlHistory.addListener(function(newLocation, historyData) {\n");
        writer.write("ice.se(null, '" + id + "');\n");
        writer.write("});\n");
        writer.write("};\n");
        writer.write("if (window.addEventListener) { window.addEventListener('load', init, false) } else { window.attachEvent('onload', init); }");
        writer.endElement("script");

        writer.endElement("span");
    }
}
