/*
 * 
 * AbsoluteLayout allow placement of components in absolute positions.
 * A flow layout arranges components in relative alignment.
 */
package com.icesoft.faces.component.panellayout;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

public class PanelLayoutRenderer extends Renderer {

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement("div", uiComponent);
        
        if(uiComponent instanceof PanelLayout){
            PanelLayout panelLayout = (PanelLayout)uiComponent;
            String style = panelLayout.getStyle();
            
            StringBuffer prefixStyle = new StringBuffer(" ");
            if(panelLayout.getLayout().equals(PanelLayout.FLOWLAYOUT)){
                prefixStyle.append("position:relative;");
            }
            
            String modifiedStyle = null;
            if(style != null && style.length() > 0){
                modifiedStyle = prefixStyle.toString()+style;
            }else{
                modifiedStyle = prefixStyle.toString();
            }
            
            writer.writeAttribute("style", modifiedStyle, "style");
            String clientId = uiComponent.getClientId(facesContext);
            writer.writeAttribute("id", clientId, "id");
            
            String styleClass = panelLayout.getStyleClass();
            if(styleClass != null && styleClass.length() > 0){
                writer.writeAttribute("class", styleClass, "styleClass");
            }
        }                    
    }

    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {

        if (context == null || component == null) {
            throw new NullPointerException();
        }
        
        Iterator kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            kid.encodeBegin(context);
            if (kid.getRendersChildren()) {
                kid.encodeChildren(context);
            }
            kid.encodeEnd(context);
        }
    }

    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement("div");
    }

    public boolean getRendersChildren() {
        return true;
    }
}
