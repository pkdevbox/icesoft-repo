/*
 * Copyright 2004-2013 ICEsoft Technologies Canada Corp.
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
package org.icefaces.mobi.component.menubutton;



import org.icefaces.ace.meta.annotation.*;
import org.icefaces.ace.meta.baseMeta.UICommandMeta;
import org.icefaces.ace.meta.annotation.ClientBehaviorHolder;
import org.icefaces.ace.meta.annotation.ClientEvent;
import org.icefaces.ace.api.IceClientBehaviorHolder;
import org.icefaces.ace.meta.baseMeta.UISeriesBaseMeta;
import org.icefaces.mobi.utils.TLDConstants;

import javax.el.MethodExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.model.SelectItem;
import javax.el.MethodExpression;
import java.util.List;

@ClientBehaviorHolder(events = {
	@ClientEvent(name="click", javadoc="Fired when a menubutton is clicked",
            tlddoc="Fired when commandButton is clicked", defaultRender="@all",
            defaultExecute="@all")}, defaultEvent="close")
@Component(
        tagName = "menuButton",
        componentClass = "org.icefaces.mobi.component.menubutton.MenuButton",
        rendererClass = "org.icefaces.mobi.component.menubutton.MenuButtonRenderer",
        generatedClass = "org.icefaces.mobi.component.menubutton.MenuButtonBase",
        extendsClass = "org.icefaces.impl.component.UISeriesBase",
        componentType = "org.icefaces.component.MenuButton",
        rendererType = "org.icefaces.component.MenuButtonRenderer",
        componentFamily = "org.icefaces.MenuButton",
        tlddoc = "This component renders a select menu button with a collection or list of menuButtonItems as children. " +
                "Upon selection of a menuButtonItem, an actionListener will be queued. Children may" +
                "only be icemobile component type of menuButtonItem, and a collection may be specified by the " +
                "value attribute with the var attribute as per UISeries implementation.  Otherwise, several " +
                "menuButtonItem children can be designated as children of this component."
)

@ResourceDependencies({
        @ResourceDependency(library = "org.icefaces.component.util", name = "component.js")
})

public class MenuButtonMeta extends UISeriesBaseMeta{

    @Property(tlddoc = TLDConstants.STYLE)
    private String style;

    @Property(tlddoc = TLDConstants.STYLECLASS)
    private String styleClass;

    @Property(defaultValue = "false",
            tlddoc = TLDConstants.DISABLED)
    private boolean disabled;

    @Property(defaultValue="Menu", tlddoc="The label on the menu button.")
    private String buttonLabel;

    @Property(defaultValue="Select", tlddoc="The label for the first item in list which cannot be selected " +
            "but helpful to users to understand how to use the component, ie: that they must select an option in the list.")
    private String selectTitle;

    @Property(defaultValue = "false", tlddoc = TLDConstants.IMMEDIATE_INPUT)
    private boolean immediate;
    /** other possible attributes include vertical, scroll increment, circular, numbershown, currentIndex */
}
