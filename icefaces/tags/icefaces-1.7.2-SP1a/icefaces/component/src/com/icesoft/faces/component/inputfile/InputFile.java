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

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.FileUploadComponent;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.component.style.OutputStyle;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.util.CoreUtils;
import com.icesoft.faces.utils.MessageUtils;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.FacesEvent;
import javax.faces.event.AbortProcessingException;
import javax.faces.FacesException;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * InputFile is a JSF component class representing an ICEfaces inputFile.
 */
public class InputFile extends UICommand implements Serializable, FileUploadComponent {
    private static final Log log = LogFactory.getLog(InputFile.class);

    public static final int DEFAULT = 0;
    public static final int UPLOADING = 1;
    public static final int SAVED = 2;
    public static final int INVALID = 3;
    public static final int SIZE_LIMIT_EXCEEDED = 4;
    public static final int UNKNOWN_SIZE = 5;
    public static final int INVALID_NAME_PATTERN = 6;

    public static final String INVALID_FILE_MESSAGE_ID = "com.icesoft.faces.component.inputfile.INVALID_FILE";
    public static final String INVALID_NAME_PATTERN_MESSAGE_ID = "com.icesoft.faces.component.inputfile.INVALID_NAME_PATTERN";
    public static final String SIZE_LIMIT_EXCEEDED_MESSAGE_ID = "com.icesoft.faces.component.inputfile.SIZE_LIMIT_EXCEEDED";
    public static final String UNKNOWN_SIZE_MESSAGE_ID = "com.icesoft.faces.component.inputfile.UNKNOWN_SIZE";

    public static final String FILE_UPLOAD_PREFIX = "fileUpload";
    
    public static final String SUBMIT_NONE = "none";
    public static final String SUBMIT_PRE_UPLOAD = "preUpload";
    public static final String SUBMIT_POST_UPLOAD = "postUpload";
    public static final String SUBMIT_PRE_POST_UPLOAD = "preAndPostUpload";
    
    private Boolean disabled;
    private String style;
    private String styleClass;
    private String label;
    private String enabledOnUserRole;
    private String renderedOnUserRole;
    private String title;
    private int height = 30;
    private int width = 500;
    private int inputTextSize = 35;
    private String inputTextClass;
    private String fileNamePattern;
    private boolean uniqueFolder = true;
    private boolean uniqueFolderSet = false;
    private String uploadDirectory;
    private Boolean uploadDirectoryAbsolute;
    private Throwable uploadException;
    private int status = DEFAULT;
    private FileInfo fileInfo = new FileInfo();
    private int progress = 0;
    private File file;
    private long sizeMax;
    private MethodBinding progressListener;
    private Boolean progressRender;
    private String submitOnUpload;

    /**
     * <p>Return the value of the <code>COMPONENT_TYPE</code> of this
     * component.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.File";
    }

    /**
     * <p>Return the value of the <code>RENDERER_TYPE</code> of this
     * component.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.Upload";
    }

    /**
     * <p>Return the value of the <code>COMPONENT_FAMILY</code> of this
     * component.</p>
     */
    public String getFamily() {
        return "com.icesoft.faces.File";
    }

    public void upload(
            FileItemStream stream,
            String uploadDirectory,
            boolean uploadDirectoryAbsolute,
            long maxSize,
            ServletContext servletContext,
            String sessionId,
            PersistentFacesState state)
            throws IOException {
        reset();        
        this.uploadException = null;
        this.status = UPLOADING;
        this.sizeMax = maxSize;
        FacesContext context = FacesContext.getCurrentInstance();
        // InputFile uploadDirectory attribute takes precedence,
        //  but if it's not given, then default to the
        //  com.icesoft.faces.uploadDirectory context-param
        String folder = getUploadDirectory();
        if (folder == null) {
            folder = uploadDirectory;
        }
        // InputFile uploadDirectoryAbsolute attribute takes precedence,
        //  but if it's not given, then default to the
        //  com.icesoft.faces.uploadDirectoryAbsolute context-param
        Boolean folderAbs = getUploadDirectoryAbsolute();
        if (folderAbs == null) {
            folderAbs = uploadDirectoryAbsolute ? Boolean.TRUE : Boolean.FALSE;
        }
        if (!folderAbs.booleanValue()) {
            folder = servletContext.getRealPath(folder);
        }
        if (isUniqueFolder()) {
            String FILE_SEPARATOR = System.getProperty("file.separator");
            folder = folder + FILE_SEPARATOR + sessionId;
        }

        String namePattern = getFileNamePattern().trim();
        String fileName = stream.getName();
        // If server is Unix and file name has full Windows path info. (JIRA ICE-1868)
        if (File.separatorChar == '/' && fileName.matches("^[a-zA-Z]:\\\\.*?|^\\\\\\\\.*?")) {
            // Strip the path info. Keep the base file name
            fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        }
        try {
            if (fileName != null && fileName.length() > 0) {
                // IE gives us the whole path on the client, but we just
                //  want the client end file name, not the path
                File tempFileName = new File(fileName);
                fileName = tempFileName.getName();
            } else {
                throw new FileUploadBase.FileUploadIOException(
                        new FileUploadBase.InvalidContentTypeException());
            }

            fileInfo.setFileName(fileName);
            fileInfo.setContentType(stream.getContentType());
            if (fileName != null && fileName.trim().matches(namePattern)) {
                File folderFile = new File(folder);
                if (!folderFile.exists())
                    folderFile.mkdirs();
                file = new File(folder, fileName);
////System.out.println("upload(-)  fileName: " + fileName);
                OutputStream output = new FileOutputStream(file);
                Streams.copy(stream.openStream(), output, true);
                long fileLength = file.length();
                if (fileLength == 0) {
////System.out.println("upload(-)  fileLength is zero");
                    setProgressSafely(state, 0);
                    file.delete();
                    throw new FileUploadBase.FileUploadIOException(
                            new FileUploadBase.InvalidContentTypeException());
                }
                status = SAVED;
                fileInfo.setPhysicalPath(file.getAbsolutePath());
                fileInfo.setSize( fileLength );
////System.out.println("upload(-)  SAVED");
                notifyDoneSafely(state, true);
////System.out.println("upload(-)  ActionListeners");
            } else {
                fileInfo.reset();
                file = null;
                status = INVALID_NAME_PATTERN;
                context.addMessage(getClientId(context), MessageUtils.getMessage(context, INVALID_NAME_PATTERN_MESSAGE_ID, new Object[]{fileName, namePattern}));
                notifyDoneSafely(state, false);
            }
        } catch (FileUploadBase.FileUploadIOException uploadException) {
            this.uploadException = uploadException.getCause();
            try {
                throw this.uploadException;
            } catch (FileUploadBase.FileSizeLimitExceededException e) {
                status = SIZE_LIMIT_EXCEEDED;
            } catch (FileUploadBase.UnknownSizeException e) {
                status = UNKNOWN_SIZE;
            } catch (FileUploadBase.InvalidContentTypeException e) {
////System.out.println("upload(-)  INVALID :: InvalidContentType: " + e.getMessage());
                status = INVALID;
            } catch (Throwable t) {
////System.out.println("upload(-)  INVALID :: Throwable: " + t.getMessage());
////t.printStackTrace();
                status = INVALID;
            }
            fileInfo.setException(uploadException);
            if (file != null)
                file.delete();
            notifyDoneSafely(state, false);
            throw uploadException;
        }
        catch (IOException e) { // Eg: If creating the saved file fails
            this.uploadException = e;
////System.out.println("upload(-)  INVALID :: IOException: " + e.getMessage());
////e.printStackTrace();
            status = INVALID;
            fileInfo.setException(e);
            if (file != null)
                file.delete();
            notifyDoneSafely(state, false);
            throw e;
        }
        
////System.out.println("upload(-)  Method bottom");
        renderIfAppropriate(state, true);
    }
    
    protected void notifyDoneSafely(PersistentFacesState state, boolean updateFile) {
        state.acquireUploadLifecycleLock();
        try {
            state.setAllCurrentInstances();
            if (updateFile) {
                updateFileValueBinding();
            }
            notifyDone();
        }
        finally {
            state.releaseUploadLifecycleLock();
        }
    }
    
    protected void notifyDone() {
        ActionEvent event = new ActionEvent(this);

        //this is true for JSF 1.1 only
        MethodBinding actionListener = getActionListener();
        if (actionListener != null) {
            actionListener.invoke(
                    FacesContext.getCurrentInstance(),
                    new Object[]{event});
        }

        //this is true for JSF 1.2 only
        ActionListener[] actionListeners = getActionListeners();
        for (int i = 0; i < actionListeners.length; i++) {
            actionListeners[i].processAction(event);
        }
        MethodBinding action = getAction();
        if (action != null) {
            action.invoke(FacesContext.getCurrentInstance(), null);
        }
    }
    
    /**
     * @see javax.faces.component.UIComponentBase#broadcast(javax.faces.event.FacesEvent)
     */
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        super.broadcast(event);

        if (event instanceof InputFileProgressEvent) {
            // Later, InputFile.upload(-) does a reset(), but if we're getting
            //  a pre-upload progress event, we want things reset() here too
            if(fileInfo.isPreUpload()) {
                reset();
                fileInfo.setPreUpload(true);
            }
            
            if(progressListener != null) {
                progressListener.invoke(
                    FacesContext.getCurrentInstance(),
                    new Object[] {event});
            }
            
            setPreUpload(false);
            setPostUpload(false);
        }
    }
    
    public void renderIFrame(Writer writer, BridgeFacesContext context) throws IOException {
        writer.write("<html style=\"overflow:hidden;\">");
        ArrayList outputStyleComponents = findOutputStyleComponents(context.getViewRoot());
        if (outputStyleComponents != null) {
            writer.write("<head>");
            for (int i = 0; i < outputStyleComponents.size(); i++) {
                OutputStyle outputStyle = (OutputStyle) outputStyleComponents.get(i);
                String href = outputStyle.getHref();
                if ((href != null) && (href.length() > 0)) {
                    href = CoreUtils.resolveResourceURL(context, href);
                    writer.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + href + "\">");
                }
            }
            writer.write("</head>");
        }
        String srv = getUploadServletPath(context);
        writer.write("<body style=\"background-color:transparent; overflow:hidden\"><form method=\"post\" action=\"" + srv + "\" enctype=\"multipart/form-data\" id=\"fileUploadForm\">");
        writer.write("<input type=\"hidden\" name=\"ice.component\" value=\"");
        writer.write(this.getClientId(context));
        writer.write("\"/>");
        writer.write("<input type=\"hidden\" name=\"ice.view\"");
        writer.write(" value=\"" + context.getViewNumber() + "\"/>");
        writer.write("<input type=\"file\" name=\"upload\"");
        writer.write(" size=\"" + getInputTextSize() + "\"");
        String inputTextClass = getInputTextClass();
        if (inputTextClass != null)
            writer.write(" class=\"" + inputTextClass + "\"");
        String title = getTitle();
        if (title != null) writer.write(" title=\"" + title + "\"");
        writer.write("/>");
        writer.write("<input type=\"submit\" value=\"" + getLabel() + "\"");
        String buttonClass = getButtonClass();
        if (buttonClass != null) writer.write(" class=\"" + buttonClass + "\"");
        if (isDisabled()) writer.write(" disabled=\"disabled\"");
        writer.write("/>");
        writer.write("</form>");
        writer.write("</body></html>");
    }

    private String getUploadServletPath(BridgeFacesContext context) {
        String requestContextPath = null;
        if (context != null) {
            ExternalContext externalContext = context.getExternalContext();
            if (externalContext != null) {
                requestContextPath = externalContext.getRequestContextPath();
            }
        }
        if (requestContextPath == null || requestContextPath.length() == 0)
            return "./uploadHtml";
        else
            return requestContextPath + "/uploadHtml";
    }

    public Throwable getUploadException() {
        return uploadException;
    }

    /**
     * <p/>
     * Set the value of the <code>label</code> property. </p>
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * <p/>
     * Set the value of the <code>uniqueFolder</code> property. </p>
     */
    public void setUniqueFolder(boolean uniqueFolder) {
        if (uniqueFolder != this.uniqueFolder) {
            this.uniqueFolder = uniqueFolder;
        }
        this.uniqueFolderSet = true;
    }

    /**
     * <p/>
     * Return the value of the <code>uniqueFolder</code> property. </p>
     */
    public boolean isUniqueFolder() {
        if (this.uniqueFolderSet) {
            return (this.uniqueFolder);
        }
        ValueBinding vb = getValueBinding("uniqueFolder");
        if (vb != null) {
            return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
        }
        return true;
    }

    /**
     * <p/>
     * Return the value of the <code>label</code> property. </p>
     */
    public String getLabel() {
        if (label != null) {
            return label;
        }
        ValueBinding vb = getValueBinding("label");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "Upload";
    }

    /**
     * <p>Return the value of the <code>disabled</code> property.</p>
     */
    public boolean isDisabled() {
        if (!Util.isEnabledOnUserRole(this)) {
            return true;
        } else {
            if (disabled != null) {
                return disabled.booleanValue();
            }
            ValueBinding vb = getValueBinding("disabled");
            Boolean boolVal = vb != null ?
                    (Boolean) vb.getValue(getFacesContext()) : null;
            return boolVal != null ? boolVal.booleanValue() : false;

        }

    }

    /**
     * <p>Set the value of the <code>disabled</code> property.</p>
     */
    public void setDisabled(boolean disabled) {
        this.disabled = Boolean.valueOf(disabled);
    }

    /**
     * <p>Set the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    /**
     * <p>Return the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public String getEnabledOnUserRole() {
        if (enabledOnUserRole != null) {
            return enabledOnUserRole;
        }
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }


    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public String getUploadDirectory() {
        if (uploadDirectory != null) {
            return uploadDirectory;
        }
        ValueBinding vb = getValueBinding("uploadDirectory");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    public void setUploadDirectoryAbsolute(Boolean uploadDirectoryAbsolute) {
        this.uploadDirectoryAbsolute = uploadDirectoryAbsolute;
    }

    public Boolean getUploadDirectoryAbsolute() {
        if (uploadDirectoryAbsolute != null) {
            return uploadDirectoryAbsolute;
        }
        ValueBinding vb = getValueBinding("uploadDirectoryAbsolute");
        return vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
    }
    
    public void setProgressRender(Boolean progressRender) {
        this.progressRender = progressRender;
    }
    
    public Boolean getProgressRender() {
        if (progressRender != null) {
            return progressRender;
        }
        ValueBinding vb = getValueBinding("progressRender");
        return vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
    }
    
    public boolean renderOnProgress() {
        Boolean progRend = getProgressRender();
        return (progRend != null) ? progRend.booleanValue() : false;
    }
    
    public void setSubmitOnUpload(String submitOnUpload) {
        this.submitOnUpload = submitOnUpload;
    }
    
    public String getSubmitOnUpload() {
        if (submitOnUpload != null) {
            return submitOnUpload;
        }
        ValueBinding vb = getValueBinding("submitOnUpload");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }
    
    public String getValidatedSubmitOnUpload() {
        String sol = getSubmitOnUpload();
        if (sol == null || sol.length() == 0) {
            sol = SUBMIT_NONE;
        }
        else {
            if (!sol.equals(SUBMIT_NONE) &&
                !sol.equals(SUBMIT_PRE_UPLOAD) &&
                !sol.equals(SUBMIT_POST_UPLOAD) &&
                !sol.equals(SUBMIT_PRE_POST_UPLOAD)) {
                throw new FacesException( new IllegalArgumentException(
                    "Invalid value for ice:inputFile's submitOnUpload property: " + sol) );
            }
        }
        return sol;
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
     * <p>Return the value of the <code>rendered</code> property.</p>
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * <p>Return the value of the <code>style</code> property.</p>
     */
    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
                "border-collapse:collapse; border-spacing:0px; padding:0px;";
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
                CSS_DEFAULT.ICE_FILE_UPLOAD_BASE_CLASS,
                "styleClass",
                isDisabled());
    }

    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[26];
        values[0] = super.saveState(context);
        values[1] = disabled;
        values[2] = style;
        values[3] = styleClass;
        values[4] = label;
        values[5] = enabledOnUserRole;
        values[6] = renderedOnUserRole;
        values[7] = title;
        values[8] = new Integer(height);
        values[9] = new Integer(width);
        values[10] = new Integer(inputTextSize);
        values[11] = inputTextClass;
        values[12] = fileNamePattern;
        values[13] = uniqueFolder ? Boolean.TRUE : Boolean.FALSE;
        values[14] = uniqueFolderSet ? Boolean.TRUE : Boolean.FALSE;
        values[15] = uploadDirectory;
        values[16] = uploadDirectoryAbsolute;
        values[17] = uploadException;
        values[18] = new Integer(status);
        values[19] = fileInfo;
        values[20] = new Integer(progress);
        values[21] = file;
        values[22] = new Long(sizeMax);
        values[23] = saveAttachedState(context, progressListener);
        values[24] = progressRender;
        values[25] = submitOnUpload;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        disabled = (Boolean) values[1];
        style = (String) values[2];
        styleClass = (String) values[3];
        label = (String) values[4];
        enabledOnUserRole = (String) values[5];
        renderedOnUserRole = (String) values[6];
        title = (String) values[7];
        height = ((Integer) values[8]).intValue();
        width = ((Integer) values[9]).intValue();
        inputTextSize = ((Integer) values[10]).intValue();
        inputTextClass = (String) values[11];
        fileNamePattern = (String) values[12];
        uniqueFolder = ((Boolean) values[13]).booleanValue();
        uniqueFolderSet = ((Boolean) values[14]).booleanValue();
        uploadDirectory = (String) values[15];
        uploadDirectoryAbsolute = (Boolean) values[16];
        uploadException = (Throwable) values[17];
        status = ((Integer) values[18]).intValue();
        fileInfo = (FileInfo) values[19];
        progress = ((Integer) values[20]).intValue();
        file = (File) values[21];
        sizeMax = ((Long) values[22]).longValue();
        progressListener = (MethodBinding) restoreAttachedState(context, values[23]);
        progressRender = (Boolean) values[24];
        submitOnUpload = (String) values[25];
    }

    /**
     * <p>Set the value of the <code>title</code> property.</p>
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Return the value of the <code>title</code> property.</p>
     */
    public String getTitle() {
        if (title != null) {
            return title;
        }
        ValueBinding vb = getValueBinding("title");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    // size
    private String size = null;

    /**
     * <p>Set the value of the <code>size</code> property.</p>
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * <p>Return the value of the <code>size</code> property.</p>
     */
    public String getSize() {
        if (size != null) {
            return size;
        }
        ValueBinding vb = getValueBinding("size");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }


    public int getHeight() {
        ValueBinding vb = getValueBinding("height");
        if (vb != null) {
            Integer value = (Integer) vb.getValue(getFacesContext());
            if (null == value) {
                return height;
            }
            return (value.intValue());
        } else {
            return (this.height);
        }
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        ValueBinding vb = getValueBinding("width");
        if (vb != null) {
            Integer value = (Integer) vb.getValue(getFacesContext());
            if (null == value) {
                return width;
            }
            return (value.intValue());
        } else {
            return (this.width);
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getInputTextSize() {
        ValueBinding vb = getValueBinding("inputTextSize");
        if (vb != null) {
            Integer value = (Integer) vb.getValue(getFacesContext());
            if (null == value) {
                return inputTextSize;
            }
            return (value.intValue());
        } else {
            return (this.inputTextSize);
        }
    }

    public void setInputTextSize(int inputTextSize) {
        this.inputTextSize = inputTextSize;
    }

    public String getFileNamePattern() {
        if (fileNamePattern != null) {
            return fileNamePattern;
        }
        ValueBinding vb = getValueBinding("fileNamePattern");
        return vb != null ? (String) vb.getValue(getFacesContext()) : ".+";
    }

    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }


    public void setInputTextClass(String inputTextClass) {
        this.inputTextClass = inputTextClass;
    }

    public String getInputTextClass() {
        return Util.getQualifiedStyleClass(this,
                inputTextClass,
                CSS_DEFAULT.ICE_FILE_UPLOAD_DEFAULT_INPUT_TEXT_CLASS,
                "inputTextClass",
                isDisabled());
    }

    private String buttonClass = null;

    public void setButtonClass(String buttonClass) {
        this.buttonClass = buttonClass;
    }

    public String getButtonClass() {
        return Util.getQualifiedStyleClass(this,
                buttonClass,
                CSS_DEFAULT.ICE_FILE_UPLOAD_DEFAULT_BUTTON_CLASS,
                "buttonClass",
                isDisabled());
    }

    boolean isRegister() {
        return false;
    }

    public void setRegister(FacesContext facesContext) {
        //do nothing
    }

    /**
     * <p/>
     * Return the value of the <code>fileInfo</code> property. </p>
     */
    public FileInfo getFileInfo() {
        if (fileInfo == null)
            return null;
        return (FileInfo) fileInfo.clone();
    }

    void setFileInfo(FileInfo fileInfo) {
        //do nothing
    }

    /**
     * <p/>
     * Return the value of the <code>file</code> property. </p>
     */
    public File getFile() {
        return file;
    }

    /**
     * <p/>
     * Set the value of the <code>file<code> property. </p>
     */
    public void setFile(File file) {
        //do nothing
    }

    /**
     * In the 1.5.3 codebase, there was a writeable ValueBinding named "file"
     * that would be updated when a new file was saved. This provides
     * backwards compatibility with that.
     */
    protected void updateFileValueBinding() {
        try {
            ValueBinding vb = getValueBinding("file");
            if (vb != null)
                vb.setValue(FacesContext.getCurrentInstance(), getFile());
        }
        catch (Exception e) {
            log.warn("The InputFile's file attribute has a ValueBinding, whose value could not be set", e);
        }
    }

    public int getStatus() {
        return status;
    }

    /**
     * <p>Return the value of the <code>fileName</code> property.</p>
     *
     * @deprecated use getFileInfo().getFileName() instead.
     */
    public String getFilename() {
        return fileInfo.getFileName();
    }

    /**
     * <p>Set the value of the <code>fileName</code> property.</p>
     *
     * @deprecated use getFileInfo().setFileName() instead.
     */
    public void setFilename(String filename) {
        fileInfo.setFileName(filename);
    }

    /**
     * <p>Return the value of the <code>size</code> property.</p>
     *
     * @deprecated use getFileInfo().getSize() instead.
     */
    public long getFilesize() {
        return fileInfo.getSize();
    }

    /**
     * <p>Set the value of the <code>size</code> property.</p>
     */
    public void setFilesize(long filesize) {
        fileInfo.setSize(filesize);
    }

    public long getSizeMax() {
        return sizeMax;
    }

    public MethodBinding getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(MethodBinding binding) {
        progressListener = binding;
    }

    public int getProgress() {
        return progress;
    }

    protected void setProgressSafely(PersistentFacesState state, int i) {
        state.acquireUploadLifecycleLock();
        try {
            state.setAllCurrentInstances();
            setProgress(i);
        }
        finally {
            state.releaseUploadLifecycleLock();
        }
    }
    
    public void setProgress(int i) {

        progress = i;
        fileInfo.setPercent(i);
        if (getProgressListener() != null)
            getProgressListener().invoke(FacesContext.getCurrentInstance(), new Object[]{new InputFileProgressEvent(this)});
    }

    /**
     * To be called by the UploadServer, allowing us to move the progressRender
     * functionality right into the InputFile component 
     * @param i The progress percentage
     */
    public void updateProgress(PersistentFacesState state, int i) {
////System.out.println("updateProgress()  progress: " + i);
        setProgressSafely(state, i);
        renderIfAppropriate(state, renderOnProgress());
    }
    
    private void renderIfAppropriate(PersistentFacesState state, boolean appropriate) {
////System.out.println("renderIfAppropriate()  appropriate: " + appropriate);
        if (!appropriate)
            return;
        
        // If we can do server push
        if (!state.isSynchronousMode()) {
            try{
////System.out.println("renderIfAppropriate()    Seam: " + SeamUtilities.isSeamEnvironment());
////System.out.println("renderIfAppropriate()    Spring: " + SeamUtilities.isSpringEnvironment());
                // Seam throws spurious exceptions with PFS.renderLater
                //  so we'll work-around that for now. Fix later.
                if (SeamUtilities.isSeamEnvironment() ||
                    SeamUtilities.isSpringEnvironment()) {
                    state.setupAndExecuteAndRender();
                    state.setAllCurrentInstances();
                }
                else {
                    state.renderLater();
                }
////System.out.println("renderIfAppropriate()    Rendered");
            }
            catch(Exception e) {
                log.warn("Problem rendering view during file upload", e);
            }
        }
    }
    
    void setPreUpload(boolean p) {
        fileInfo.setPreUpload(p);
    }
    
    void setPostUpload(boolean p) {
        fileInfo.setPostUpload(p);
    }

    public String getCssFile() {
        return null;
    }

    private String getDisabled() {
        return null;
    }

    private String getStyleClassString() {
        return null;
    }

    private String getStyleString() {
        return null;
    }

    private String getStyleInfo() {
        return null;
    }

    private String getInputTextClassString() {
        return null;
    }

    private String getButtonClassString() {
        return null;
    }

    private String getTitleAsString() {
        return null;
    }

    private static ArrayList findOutputStyleComponents(UIComponent parent) {
        ArrayList returnValue = null;
        Iterator children = parent.getChildren().iterator();
        UIComponent childComponent = null;
        while (children.hasNext()) {
            childComponent = (UIComponent) children.next();
            if (childComponent instanceof OutputStyle) {
                if (returnValue == null) {
                    returnValue = new ArrayList();
                }
                returnValue.add(childComponent);
            } else {
                ArrayList outputStyleComponents = findOutputStyleComponents(childComponent);
                if (outputStyleComponents != null) {
                    if (returnValue == null) {
                        returnValue = outputStyleComponents;
                    } else {
                        returnValue.addAll(outputStyleComponents);
                    }
                }
            }
        }
        return returnValue;
    }

    private String onfocus;

    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    public String getOnfocus() {
        if (onfocus != null) {
            return onfocus;
        }
        ValueBinding vb = getValueBinding("onfocus");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    private String onchange;

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    public String getOnchange() {
        if (onchange != null) {
            return onchange;
        }
        ValueBinding vb = getValueBinding("onchange");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    private String accept;

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAccept() {
        if (accept != null) {
            return accept;
        }
        ValueBinding vb = getValueBinding("accept");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    private String accesskey;

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getAccesskey() {
        if (accesskey != null) {
            return accesskey;
        }
        ValueBinding vb = getValueBinding("accesskey");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    private String onblur;

    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    public String getOnblur() {
        if (onblur != null) {
            return onblur;
        }
        ValueBinding vb = getValueBinding("onblur");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    private String tabindex;

    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    public String getTabindex() {
        if (tabindex != null) {
            return tabindex;
        }
        ValueBinding vb = getValueBinding("tabindex");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    public void reset() {
        uploadException = null;
        status = DEFAULT;
        fileInfo = new FileInfo();
        progress = 0;
        file = null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            String clientId = getClientId(facesContext);
            Iterator msgs = facesContext.getMessages(clientId);
            if (msgs != null) {
                while (msgs.hasNext()) {
                    FacesMessage msg = (FacesMessage) msgs.next();
                    msgs.remove();
                }
            }
        }
    }
    
    /*
    // Usefull for debugging
    public void dump() {
        System.out.println("InputFile  file: " + file);
        System.out.println("InputFile  progress: " + progress);
        System.out.println("InputFile  status: " + status);
        System.out.println("InputFile  uploadException: " + uploadException);
        System.out.println("InputFile  " + fileInfo);
    }
    */
}
