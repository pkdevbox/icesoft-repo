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

package org.icefaces.ace.component.notificationpanel;

import org.icefaces.ace.meta.annotation.ClientBehaviorHolder;
import org.icefaces.ace.meta.annotation.ClientEvent;
import org.icefaces.ace.api.IceClientBehaviorHolder;
import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.baseMeta.UIComponentBaseMeta;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@Component(
        tagName = "notificationPanel",
        componentClass = "org.icefaces.ace.component.notificationpanel.NotificationPanel",
        generatedClass = "org.icefaces.ace.component.notificationpanel.NotificationPanelBase",
        extendsClass = "javax.faces.component.UIComponentBase",
        rendererClass = "org.icefaces.ace.component.notificationpanel.NotificationPanelRenderer",
        componentFamily = "org.icefaces.ace.component.NotificationPanel",
        componentType = "org.icefaces.ace.component.NotificationPanel",
        rendererType = "org.icefaces.ace.component.NotificationPanelRenderer",
        tlddoc = "The Notification Panel is a component that displays a multipurpose fixed positioned panel for notification. Any group " +
                 "of JSF content can be placed inside notification panel. " +
                 "If position of panel is out of place, try putting it as last child of body." +
                 "<p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/NotificationPanel\">NotificationPanel Wiki Documentation</a>.")
@ResourceDependencies({
		@ResourceDependency(library="icefaces.ace", name="util/combined.css"),
        @ResourceDependency(library = "icefaces.ace", name = "util/ace-jquery.js"),
        @ResourceDependency(library = "icefaces.ace", name = "util/ace-components.js")
})
@ClientBehaviorHolder(events = {
	@ClientEvent(name="close", tlddoc="Fired when the panel is closed (default event).", defaultRender="@all", defaultExecute="@all"),
	@ClientEvent(name="display", tlddoc="Fired when the panel is displayed.", defaultRender="@all", defaultExecute="@all")
}, defaultEvent="close")
public class NotificationPanelMeta extends UIComponentBaseMeta {
    @Property(tlddoc = "Name of the client side widget.")
    private String widgetVar;

    @Property(tlddoc = "Style of the container element.")
    private String style;

    @Property(tlddoc = "StyleClass of the container element.")
    private String styleClass;

    @Property(defaultValue = "top", tlddoc = "Position of the bar, \"top\" or \"bottom\".")
    private String position;

    @Property(defaultValue = "fade", tlddoc = "Name of the effect, \"fade\", \"slide\" or \"none\".")
    private String effect;

    @Property(defaultValue = "normal", tlddoc = "Speed of the effect, \"slow\", \"normal\" or \"fast\".")
    private String effectSpeed;

    @Property(defaultValue = "false", tlddoc = "Set visibility from the server")
    private boolean visible;
}
