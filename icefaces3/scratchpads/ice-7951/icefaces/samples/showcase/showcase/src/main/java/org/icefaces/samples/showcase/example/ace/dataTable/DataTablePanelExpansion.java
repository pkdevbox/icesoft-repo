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

package org.icefaces.samples.showcase.example.ace.dataTable;

import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;
import java.util.ArrayList;
import java.util.List;
import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;
import org.icefaces.samples.showcase.example.compat.dataTable.Car;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ComponentExample(
        parent = DataTableBean.BEAN_NAME,
        title = "example.ace.dataTable.panelexpansion.title",
        description = "example.ace.dataTable.panelexpansion.description",
        example = "/resources/examples/ace/dataTable/dataTablePanelExpansion.xhtml"
)
@ExampleResources(
        resources ={
                // xhtml
                @ExampleResource(type = ResourceType.xhtml,
                        title="dataTablePanelExpansion.xhtml",
                        resource = "/resources/examples/ace/dataTable/dataTablePanelExpansion.xhtml"),
                // Java Source
                @ExampleResource(type = ResourceType.java,
                        title="DataTablePanelExpansion.java",
                        resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                                "/example/ace/dataTable/DataTablePanelExpansion.java")
        }
)
@ManagedBean(name= DataTablePanelExpansion.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DataTablePanelExpansion extends ComponentExampleImpl<DataTablePanelExpansion> implements Serializable {
    public static final String BEAN_NAME = "dataTablePanelExpansion";
    private List<Car> carsData;
    /////////////---- CONSTRUCTOR BEGIN
    public DataTablePanelExpansion() {
        super(DataTablePanelExpansion.class);
        carsData = new ArrayList<Car>(DataTableData.getDefaultData());
    }
    /////////////---- GETTERS & SETTERS BEGIN
    public List<Car> getCarsData() { return carsData; }
    public void setCarsData(List<Car> carsData) { this.carsData = carsData; }
}
