package com.icesoft.faces.component.inputrichtext;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.w3c.dom.Element;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicInputRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;

public class InputRichTextRenderer extends DomBasicInputRenderer {

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        String clientId = uiComponent.getClientId(facesContext);
        InputRichText inputRichText = (InputRichText) uiComponent;
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        if (!domContext.isInitialized()) {
            Element root = domContext.createRootElement(HTML.DIV_ELEM);
            root.setAttribute(HTML.ID_ATTR, clientId + "container");
            Element div = domContext.createElement(HTML.DIV_ELEM);
            root.setAttribute(HTML.CLASS_ATTR, inputRichText.getStyleClass());
            if (inputRichText.getStyle() != null) {
                root.setAttribute(HTML.STYLE_ATTR, inputRichText.getStyle());
            }
            root.appendChild(div);
            div.setAttribute(HTML.ID_ATTR, clientId + "editor");
            StringBuffer call = new StringBuffer();

            call.append("Ice.FCKeditor.register ('" + clientId + "', new Ice.FCKeditor('" + clientId + "', '" + inputRichText.getLanguage() 
            		+ "', '" + inputRichText.getFor() + "', '" + inputRichText.getBaseURI().getPath() + "','" + inputRichText.getWidth() + 
            		"', '" + inputRichText.getHeight() + "', '"+ inputRichText.getToolbar()+"', '"+ inputRichText.getCustomConfigPath() + 
            		"', '"+ inputRichText.getSkin() +"'));");
            String value = "";
            if (inputRichText.getValue() != null) {
                value =  inputRichText.getValue().toString();
            }
            addHiddenField (domContext, root,clientId + "valueHolder", 
                                    value);
            addHiddenField (domContext, root,clientId + "Disabled",
                                    String.valueOf(inputRichText.isDisabled()));            
            Element script = domContext.createElement(HTML.SCRIPT_ELEM);
            script.setAttribute(HTML.TYPE_ATTR, "text/javascript");
            script.appendChild(domContext.createTextNode(call.toString()));
            root.appendChild(script);
            if (inputRichText.isSaveOnSubmit()) {
                Element saveOnSubmit = domContext.createElement(HTML.INPUT_ELEM);
                saveOnSubmit.setAttribute(HTML.ID_ATTR, clientId + "saveOnSubmit");
                saveOnSubmit.setAttribute(HTML.TYPE_ATTR, "hidden");                
                root.appendChild(saveOnSubmit);
            }
            
            div.setAttribute(HTML.ONMOUSEOUT_ATTR, "Ice.FCKeditorUtility.updateFields('" + clientId + "');");
            div.setAttribute(HTML.ONMOUSEOVER_ATTR, "Ice.FCKeditorUtility.activeEditor ='" + clientId + "';");
            JavascriptContext.addJavascriptCall(facesContext, "Ice.FCKeditorUtility.updateValue ('" + clientId + "');");            
            domContext.stepOver();
        }
    }

    private void addHiddenField (DOMContext domContext, Element root, 
                                            String fieldName, String value) {
            Element hiddenFld = (Element) domContext.createElement(HTML.INPUT_ELEM);
            hiddenFld.setAttribute(HTML.TYPE_ATTR, "hidden");
            hiddenFld.setAttribute(HTML.ID_ATTR, fieldName);
            hiddenFld.setAttribute(HTML.NAME_ATTR,fieldName);
            if (value != null) {
                hiddenFld.setAttribute(HTML.VALUE_ATTR, value);
            }
            root.appendChild(hiddenFld);
    }

}
