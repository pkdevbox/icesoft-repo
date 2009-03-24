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

package com.icesoft.faces.component.inputfile;

import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.ResourceRegistry;
import com.icesoft.faces.context.StringResource;
import com.icesoft.faces.context.Resource.Options;
import com.icesoft.faces.utils.MessageUtils;
import org.apache.commons.fileupload.FileUploadBase;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class InputFileRenderer extends Renderer {

    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        String id = component.getClientId(context);
        InputFile c = (InputFile) component;
        BridgeFacesContext facesContext = (BridgeFacesContext) context;
        ResponseWriter writer = context.getResponseWriter();
        StringWriter iframeContentWriter = new StringWriter();
        c.renderIFrame(iframeContentWriter, facesContext);
        String iframeContent = iframeContentWriter.toString();        
        String frameName = id + ":uploadFrame";
        String pseudoURL = ((ResourceRegistry) context).registerResource(new StringResource(iframeContent) {
            public void withOptions(Options options) throws IOException {
                super.withOptions(options);
                options.setMimeType("text/html");
            }
        }).toString();
        
        writer.startElement("iframe", c);
        writer.writeAttribute("src", pseudoURL, null);
        writer.writeAttribute("id", frameName, null);
        writer.writeAttribute("name", frameName, null);
        writer.writeAttribute("class", c.getStyleClass(), null);
        writer.writeAttribute("style", c.getStyle(), null);
        writer.writeAttribute("width", c.getWidth() + "px", null);
        writer.writeAttribute("height", c.getHeight() + "px", null);
        writer.writeAttribute("title", "Input File Frame", null);
        writer.writeAttribute("frameborder", "0", null);
        writer.writeAttribute("marginwidth", "0", null);
        writer.writeAttribute("marginheight", "0", null);
        writer.writeAttribute("scrolling", "no", null);
        // Need to set allowtransparency="true" on the IFRAME element tag so that,
        // along with InputFile.renderIFrame()'s <body style="background-color: transparent;">, the IFRAME
        // will be transparent, allowing background colors to show through.
        writer.writeAttribute("allowtransparency", "true", null);
        writer.endElement("iframe");

        String submitOnUpload = c.getValidatedSubmitOnUpload();
        if (!submitOnUpload.equals(InputFile.SUBMIT_NONE)) {
            boolean preUpload =
                submitOnUpload.equals(InputFile.SUBMIT_PRE_UPLOAD) ||
                submitOnUpload.equals(InputFile.SUBMIT_PRE_POST_UPLOAD);
            boolean postUpload =
                submitOnUpload.equals(InputFile.SUBMIT_POST_UPLOAD) ||
                submitOnUpload.equals(InputFile.SUBMIT_PRE_POST_UPLOAD);
            
            writer.startElement("script", c);
            writer.writeAttribute("type", "text/javascript", null);
            writer.writeAttribute("id", id, null);
            writer.writeText(
                "var register = function() {" +
                        "var frame = document.getElementById('" + frameName + "').contentWindow;" +
                        "var submit = function() { " +
                            "if(arguments.length == 1 && arguments[0] == 1) { " +
                                ( postUpload
                                  ? ("Ice.InputFileIdPostUpload = '" + id + "'; Ice.InputFileIdPreUpload = null;")
                                  : "return;" ) +
                            " } " +
                            "else { " +
                                ( preUpload
                                  ? ("Ice.InputFileIdPreUpload = '" + id + "'; Ice.InputFileIdPostUpload = null;")
                                  : "return;" ) +
                            " } try { '" + id + "'.asExtendedElement().form().submit(); } catch (e) { logger.warn('Form not available', e); } finally { Ice.InputFileIdPreUpload = null; Ice.InputFileIdPostUpload = null; } };" +
                        //trigger form submit when the upload starts
                        "frame.document.getElementsByTagName('form')[0].onsubmit = submit;" +
                        //trigger form submit when the upload ends and re-register handlers
                        "var uploadEnd = function() { submit(1); setTimeout(register, 200); };" +
                        "if (frame.attachEvent) { frame.attachEvent('onunload', uploadEnd); } else { frame.onunload = uploadEnd; } };" +
                        //register the callback after a delay because IE6 or IE7 won't make the iframe available fast enough
                        //Increased the delay for Firefox 3.0.7
                        "if (navigator.userAgent.indexOf('Firefox/3.0.7')> 0) setTimeout(register, 400); else setTimeout(register, 0);", null);
            writer.endElement("script");
        }

        Throwable uploadException = c.getUploadException();
        if (uploadException != null) {
            try {
                throw uploadException;
            } catch (FileUploadBase.FileSizeLimitExceededException e) {
                context.addMessage(c.getClientId(context), MessageUtils.getMessage(context, InputFile.SIZE_LIMIT_EXCEEDED_MESSAGE_ID));
            } catch (FileUploadBase.UnknownSizeException e) {
                context.addMessage(c.getClientId(context), MessageUtils.getMessage(context, InputFile.UNKNOWN_SIZE_MESSAGE_ID));
            } catch (FileUploadBase.InvalidContentTypeException e) {
                String fileName = c.getFileInfo().getFileName();
                if (fileName == null) {
                    File file = c.getFile();
                    if (file != null)
                        fileName = file.getName();
                }
                if (fileName == null)
                    fileName = "";
                context.addMessage(c.getClientId(context), MessageUtils.getMessage(context, InputFile.INVALID_FILE_MESSAGE_ID, new Object[]{fileName}));
            } catch (Throwable t) {
                //ignore
            }
        }
    }
    
    public void decode(FacesContext facesContext, UIComponent component) {
        super.decode(facesContext, component);
        
        InputFile inputFile = (InputFile) component;
        inputFile.setPreUpload(false);
        inputFile.setPostUpload(false);
        
        Map parameter = facesContext.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(facesContext);
        String preUpload = (String) parameter.get("ice.inputFile.preUpload");
        String postUpload = (String) parameter.get("ice.inputFile.postUpload");
        if (preUpload != null && preUpload.length() > 0) {
            if (preUpload.equals(clientId)) {
                inputFile.setPreUpload(true);
                inputFile.queueEvent( new InputFileProgressEvent(inputFile) );
            }
        }
        if (postUpload != null && postUpload.length() > 0) {
            if (postUpload.equals(clientId)) {
                inputFile.setPostUpload(true);
                inputFile.queueEvent( new InputFileProgressEvent(inputFile) );
            }
        }
    }
}