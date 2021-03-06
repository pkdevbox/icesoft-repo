/*
 * Copyright 2004-2012 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.icefaces.samples.showcase.example.compat.paginator;

import java.io.Serializable;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

@ComponentExample(
        parent = PaginatorBean.BEAN_NAME,
        title = "example.compat.paginator.vertical.title",
        description = "example.compat.paginator.vertical.description",
        example = "/resources/examples/compat/paginator/paginatorVertical.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="paginatorVertical.xhtml",
                    resource = "/resources/examples/compat/"+
                               "paginator/paginatorVertical.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="PaginatorVertical.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/"+
                               "showcase/example/compat/paginator/PaginatorVertical.java")
        }
)
@ManagedBean(name= PaginatorVertical.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PaginatorVertical extends ComponentExampleImpl<PaginatorVertical> implements Serializable {
	
	public static final String BEAN_NAME = "paginatorVertical";
	
	public PaginatorVertical() {
		super(PaginatorVertical.class);
	}
}
