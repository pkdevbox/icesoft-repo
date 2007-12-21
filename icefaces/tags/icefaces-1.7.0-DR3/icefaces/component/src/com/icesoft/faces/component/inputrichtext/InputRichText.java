package com.icesoft.faces.component.inputrichtext;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.ByteArrayResource;
import com.icesoft.faces.context.JarResource;
import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.ResourceLinker;
import com.icesoft.faces.context.ResourceRegistry;

import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InputRichText extends UIInput {
    public static final String COMPONENET_TYPE = "com.icesoft.faces.InputRichText";
    public static final String DEFAULT_RENDERER_TYPE = "com.icesoft.faces.InputRichTextRenderer";
    private static final Resource ICE_FCK_EDITOR_JS = new JarResource("com/icesoft/faces/component/inputrichtext/fckeditor_ext.js");
    private static final Resource FCK_EDITOR_JS = new JarResource("com/icesoft/faces/component/inputrichtext/fckeditor.js");
    private static final ResourceLinker.Handler FCK_LINKED_BASE = new ResourceLinker.Handler() {
        public void linkWith(ResourceLinker linker) {
            try {
                InputStream in = this.getClass().getClassLoader().getResourceAsStream("com/icesoft/faces/component/inputrichtext/fckeditor.zip");
                ZipInputStream zip = new ZipInputStream(in);
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        String entryName = entry.getName();
                        Resource linkedResource = new ByteArrayResource(toByteArray(zip));
                        linker.registerRelativeResource(entryName, linkedResource);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };
    private String language;
    private String _for;
    private Boolean html;
    private String style;
    private String styleClass;
    private String width;
    private String height;
    private URI baseURI;

    public String getRendererType() {
        return DEFAULT_RENDERER_TYPE;
    }

    public String getComponentType() {
        return COMPONENET_TYPE;
    }

    public void decode(FacesContext facesContext) {
        Map map = facesContext.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(facesContext);
        if (map.containsKey(clientId)) {
            String newValue = map.get(clientId).toString().replace('\n', ' ');
            setSubmittedValue(newValue);
        }
        super.decode(facesContext);
    }

    public void encodeBegin(FacesContext context) throws IOException {
        if (baseURI == null) {
            ResourceRegistry registry =
                (ResourceRegistry) FacesContext.getCurrentInstance();
            if (registry != null) {
                baseURI = registry.loadJavascriptCode(FCK_EDITOR_JS, FCK_LINKED_BASE);
                registry.loadJavascriptCode(ICE_FCK_EDITOR_JS);
            } else {
                //LOG fckeditor's library has not loaded, component will not work as desired
            }
        }
        super.encodeBegin(context);
    }
    /**
     * <p>Set the value of the <code>language</code> property.</p>
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * <p>Return the value of the <code>language</code> property.</p>
     */
    public String getLanguage() {
        if (language != null) {
            return language;
        }
        ValueBinding vb = getValueBinding("language");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "en";
    }

    /**
     * <p>Set the value of the <code>for</code> property.</p>
     */
    public void setFor(String _for) {
        this._for = _for;
    }

    /**
     * <p>Return the value of the <code>language</code> property.</p>
     */
    public String getFor() {
        if (_for != null) {
            return _for;
        }
        ValueBinding vb = getValueBinding("for");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "";
    }

    boolean isToolbarOnly() {
        return false;
    }

    public URI getBaseURI() {
        return baseURI;
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
                CSS_DEFAULT.INPUT_RICH_TEXT,
                "styleClass");

    }

    /**
     * <p>Set the value of the <code>width</code> property.</p>
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * <p>Return the value of the <code>width</code> property.</p>
     */
    public String getWidth() {
        if (width != null) {
            return width;
        }
        ValueBinding vb = getValueBinding("width");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "100%";
    }

    /**
     * <p>Set the value of the <code>height</code> property.</p>
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * <p>Return the value of the <code>height</code> property.</p>
     */
    public String getHeight() {
        if (height != null) {
            return height;
        }
        ValueBinding vb = getValueBinding("height");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "200";
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int len = 0;
        while ((len = input.read(buf)) > -1) output.write(buf, 0, len);
        return output.toByteArray();
    }
}
