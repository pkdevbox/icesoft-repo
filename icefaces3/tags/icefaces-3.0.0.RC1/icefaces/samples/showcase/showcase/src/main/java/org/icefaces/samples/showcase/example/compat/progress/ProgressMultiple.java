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

package org.icefaces.samples.showcase.example.compat.progress;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.context.FacesContext;

import org.icefaces.application.PushRenderer;
import org.icefaces.application.PortableRenderer;

import org.icefaces.samples.showcase.util.FacesUtils;
import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.Menu;
import org.icefaces.samples.showcase.metadata.annotation.MenuLink;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

@ComponentExample(
        parent = ProgressBean.BEAN_NAME,
        title = "example.compat.progress.multiple.title",
        description = "example.compat.progress.multiple.description",
        example = "/resources/examples/compat/progress/progressMultiple.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="progressMultiple.xhtml",
                    resource = "/resources/examples/compat/"+
                               "progress/progressMultiple.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="ProgressMultiple.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/"+
                               "showcase/example/compat/progress/ProgressMultiple.java")
        }
)
@ManagedBean(name= ProgressMultiple.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ProgressMultiple extends ComponentExampleImpl<ProgressMultiple> implements Serializable {
	
	public static final String BEAN_NAME = "progressMultiple";
	
	public ProgressMultiple() {
		super(ProgressMultiple.class);
	}
	
	public void startTask(ActionEvent event) {
	    LongTaskManager threadBean =
	        (LongTaskManager)FacesUtils.getManagedBean(LongTaskManager.BEAN_NAME);
	    
	    threadBean.startMultiThread(1, 20, 600);
	}
}
