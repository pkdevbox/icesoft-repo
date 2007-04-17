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

package com.icesoft.icefaces.samples.showcase.components.fileUpload;

import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;

import javax.faces.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.util.EventObject;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.ScopeType;
import org.jboss.seam.InterceptionType;

import javax.ejb.Stateful;
import javax.ejb.Remove;



/**
 * <p>The InputFileBean class is the backing bean for the inputfile showcase
 * demonstration. It is used to store the state of the file uploading
 * operation.</p>
 *
 * @since 0.3.0
 */
@Stateful

/*
 InterceptionType.NEVER obviously has the side effect of preventing @Injection
 but it also prevents IllegalStateExceptions due to Seam not being initialized
 during requests to this bean from the current implementation of the FileUploadManager. As the
 FileUploadServlet gets integrated into the normal Servlet mechanism,
 this will go away. 
*/
@Intercept(InterceptionType.NEVER)
@Name("inputFile")
@Scope(ScopeType.SESSION)
public class InputFileBean  implements  InputFile {

    private int percent = -1;
    private File file = null;
    private PersistentFacesState state;

    private String fileName = "";
    private String contentType = "";

    private InnerProgressMonitor pmImpl;


    public InputFileBean() {
        pmImpl = new InnerProgressMonitor();
        state = PersistentFacesState.getInstance();
    }


    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public InnerProgressMonitor getProgressMonitor() {
        return pmImpl;
    }

    public InnerProgressMonitor getActionMonitor() {
        return pmImpl;
    } 


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {

        return fileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    @Remove
    @Destroy
    public void destroy() {
    }


    /**
     * Inner class to handle updates and action. Do this to keep the
     * render call from triggering concurrent access problems in the Bean. 
     */
    public class InnerProgressMonitor implements
                                      Serializable {



        public void progress(EventObject event) {

            com.icesoft.faces.component.inputfile.InputFile file =
                    (com.icesoft.faces.component.inputfile.InputFile) event.getSource();
            InputFileBean.this.setPercent( file.getFileInfo().getPercent());
            InputFileBean.this.setFile( file.getFile() );

            try {
                // execute the lifecycle to initialize Seam to prevent
                // IllegalStateExceptions, and render. 
                state.execute();
                state.render();

            } catch (RenderingException re ) {
                System.out.println("Rendering exception : " + re);
                re.printStackTrace();
            }
        }




        public String action(ActionEvent event) {

            com.icesoft.faces.component.inputfile.InputFile inputFile = (com.icesoft.faces.component.inputfile.InputFile) event.getSource();
            if (inputFile.getStatus() == com.icesoft.faces.component.inputfile.InputFile
                    .SAVED) {
                InputFileBean.this.setFileName(inputFile.getFileInfo().getFileName());
                InputFileBean.this.setContentType(inputFile.getFileInfo().getContentType());
                InputFileBean.this.setFile(inputFile.getFile());
            }

            if (inputFile.getStatus() == com.icesoft.faces.component.inputfile.InputFile
                    .INVALID) {
                inputFile.getFileInfo().getException().printStackTrace();
            }

            if (inputFile.getStatus() == com.icesoft.faces.component.inputfile.InputFile
                    .SIZE_LIMIT_EXCEEDED) {
                inputFile.getFileInfo().getException().printStackTrace();
            }

            if (inputFile.getStatus() == com.icesoft.faces.component.inputfile.InputFile
                    .UNKNOWN_SIZE) {
                inputFile.getFileInfo().getException().printStackTrace();
            }
            return null;
        }
    }

}
