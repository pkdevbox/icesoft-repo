/*
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License
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
 * 2004-2011 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 */

package org.icefaces.samples.showcase.example.ace.file;

import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;
import org.icefaces.samples.showcase.util.FacesUtils;

import org.icefaces.ace.component.fileentry.*;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;

@ComponentExample(
        title = "example.ace.fileentry.title",
        description = "example.ace.fileentry.description",
        example = "/resources/examples/ace/fileentry/fileentry.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="FileEntry.xhtml",
                    resource = "/resources/examples/ace/"+
                               "fileentry/fileentry.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="FileEntryBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/"+
                               "showcase/example/ace/file/FileEntryBean.java")
        }
)
@Menu(
	title = "menu.ace.fileentry.subMenu.title",
	menuLinks = {
                    @MenuLink(title = "menu.ace.fileentry.subMenu.main", isDefault = true, exampleBeanName = FileEntryBean.BEAN_NAME), 
                    @MenuLink(title = "menu.ace.fileentry.subMenu.listener", exampleBeanName = FileEntryListenerBean.BEAN_NAME),     
                    @MenuLink(title = "menu.ace.fileentry.subMenu.validation",exampleBeanName = FileEntryValidationOptionsBean.BEAN_NAME)
})
@ManagedBean(name= FileEntryBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class FileEntryBean extends ComponentExampleImpl<FileEntryBean> implements Serializable {

    public static final String BEAN_NAME = "fileEntry";
    private LinkedHashMap <String, String> fileData;
    

    public FileEntryBean()  
    {
        super(FileEntryBean.class);
    }                                       

    public void sampleListener(FileEntryEvent e) 
    {
        FileEntry fe = (FileEntry)e.getComponent();
        FileEntryResults results = fe.getResults();
        File parent = null;
        StringBuilder m = null;
        
    //get data About File
    
        for (FileEntryResults.FileInfo i : results.getFiles()) 
        {
            parent = i.getFile().getParentFile();
            m = new StringBuilder(512);

            m.append("File Name: ");
            m.append(i.getFileName());
            FacesUtils.addInfoMessage(fe.getClientId(), m.toString());
            m = new StringBuilder(512);
            m.append("File Size: ");
            m.append(i.getSize());
            m.append(" bytes");
            FacesUtils.addInfoMessage(fe.getClientId(), m.toString());
        }                                                          

        if (parent != null) 
        {
            m = new StringBuilder(128);
            long dirSize = 0;
            int fileCount = 0;
            for (File file : parent.listFiles()) 
            {
                fileCount++;
                dirSize += file.length();
            }
            m.append("Total Files In Upload Directory: ");
            m.append(fileCount);
            FacesUtils.addInfoMessage(fe.getClientId(), m.toString());
            m = new StringBuilder(128);
            m.append("Total Size of Files In Directory: ");
            m.append(dirSize);
            m.append(" bytes");
            FacesUtils.addInfoMessage(fe.getClientId(), m.toString());
        }
    }
}
