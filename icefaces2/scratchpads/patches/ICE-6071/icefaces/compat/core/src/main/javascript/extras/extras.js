
/*
 * Version: MPL 1.1
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
 * 2004-2010 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 */

Ice.DnD = Class.create();
var IceLoaded = false;
Ice.DnD.logger = Class.create();
Ice.DnD.logger = {
    debug:function() {
    },
    warn:function() {
    },
    error:function() {
    }
};

Ice.DnD = {
    log: function(s) {
        Ice.DnD.logger.debug(s);
    },
    DRAG_START:1,
    DRAG_CANCEL:2,
    DROPPED:3,
    HOVER_START:4,
    HOVER_END:5,

    init:function() {
        Ice.DnD.logger = window.logger.child('dragDrop');
        Ice.StateMon.logger = window.logger.child('stateMon');
        Ice.StateMon.destroyAll();
        IceLoaded = true;
    },

    elementReplaced:function(ele) {
        var currentEle = $(ele.id);
        return currentEle != null && currentEle != ele;
    },

    check:function () {
        Ice.StateMon.checkAll();
    },

    alreadyDrag:function(ele) {
        ele = $(ele)
        var found = false;
        $A(Draggables.drags).each(function(drag) {
            if (drag.element && drag.element.id == ele.id) {
                found = true;
            }
        });
        return found;
    },

    sortableDraggable:function(ele) {
        ele = $(ele)
        var found = false;

        $A(Draggables.drags).each(function(drag) {
            if (drag.element && drag.element.id == ele.id) {
                if (drag.options.sort) {
                    found = true;
                }
            }
        });
        return found;
    },

    alreadyDrop:function(ele) {
        ele = $(ele)
        var found = false;
        $(Droppables.drops).each(function(drop) {
            if (drop && drop.element.id == ele.id) {

                found = true;
            }
        });
        return found;
    },

    alreadySort:function(ele) {
        var opts = Sortable.options(ele);
        if (opts)return true;
        return false;
    }
};

Ice.PanelCollapsible = {
    fire:function(eleId, hdrEleId, styleCls, usrdfndCls) {
        var ele = document.getElementById(eleId);
        var hdrEle = document.getElementById(hdrEleId);
        try {
            if (Element.visible(ele)) {
                hdrEle.className = Ice.PanelCollapsible.getStyleClass(styleCls, usrdfndCls, 'ColpsdHdr');
                Ice.PanelCollapsible.collapse(eleId);
            } else {
                hdrEle.className = Ice.PanelCollapsible.getStyleClass(styleCls, usrdfndCls, 'Hdr');
                Ice.PanelCollapsible.expand(eleId);
            }
        } catch(eee) {

            console.log("Error in panel collapsible [" + eee + "]");
        }

    },

    expand:function(eleId) {
        var ele = document.getElementById(eleId);
        if (!Element.visible(ele)) {
            Effect.SlideDown(eleId, {uploadCSS:true,submit:true,duration:0.5});
        }
    },

    collapse:function(eleId) {
        var ele = document.getElementById(eleId);
        if (Element.visible(ele)) {
            Effect.SlideUp(eleId, {uploadCSS:true,submit:true,duration:0.5});
        }
    },

    getStyleClass:function(styleCls, usrdfndCls, suffix) {
        var activeClass = styleCls + suffix;
        if (usrdfndCls != null) {
            activeClass += ' ' + usrdfndCls + suffix;
        }
        return activeClass;
    }
}

Ice.tableRowClicked = function(event, useEvent, rowid, formId, hdnFld, toggleClassNames) {
    var ctrlKyFld = document.getElementsByName(hdnFld + 'ctrKy');
    var sftKyFld = document.getElementsByName(hdnFld + 'sftKy');
    if (!event)
        var event = window.event;

    if (ctrlKyFld.length > 0) {
        ctrlKyFld = ctrlKyFld[0];
    } else {
        ctrlKyFld = null;
    }

    if (sftKyFld.length > 0) {
        sftKyFld = sftKyFld[0];
    } else {
        sftKyFld = null;
    }
    if (ctrlKyFld && event) {
        ctrlKyFld.value = event.ctrlKey || event.metaKey;
    }
    if (sftKyFld && event) {
        sftKyFld.value = event.shiftKey;
    }
    var targ;

    if (event.target)
       targ = event.target;
    else if (event.srcElement)
       targ = event.srcElement;            
    try {
        if (useEvent) {
            // Some versions of Safari return the text node,
            //  while other browsers return the parent tag
            if (targ.nodeType == 3)
                targ = targ.parentNode;
            while (true) {
                var tname = targ.tagName.toLowerCase();
                if (tname == 'tr') {
                    break;
                }
                if (tname == 'input' ||
                    tname == 'select' ||
                    tname == 'option' ||
                    tname == 'a' ||
                    tname == 'textarea')
                {
                    return;
                }
                // Search up to see if we're deep within an anchor
                if (targ.parentNode)
                    targ = targ.parentNode;
                else {
                    break;
                }
            }
        }
        var evt = Event.extend(event);
        var row = evt.element();
        if (row.tagName.toLowerCase() != "tr") {
            row = evt.element().up("tr[onclick*='Ice.tableRowClicked']");
        }
        if (row) {
            // If preStyleOnSelection=false, then toggleClassNames=='', so we
            // should leave the row styling alone
            if (toggleClassNames) {
                row.className = toggleClassNames;
                row.onmouseover = Prototype.emptyFunction;
                row.onmouseout = Prototype.emptyFunction;
            }
        }
        var form = document.getElementById(formId);
        var fld = form[hdnFld];
        fld.value = rowid;
        fld.id = fld.name; //fix for ICE-5275
        var nothingEvent = new Object();
        iceSubmitPartial(form, fld, event);
        setFocus('');
        fld.id = ""; //preserve ICE-2874
        fld.value=""; //ICE-5492
    } catch(e) {
        console.log("Error in rowSelector[" + e + "]");
    }
}

Ice.clickEvents = {};

Ice.registerClick = function(elem, hdnClkRow, hdnClkCount, rowid, formId, delay, toggleOnClick, event, useEvent, hdnFld, toggleClassNames) {
    if (!Ice.clickEvents[elem.id]) {
        Ice.clickEvents[elem.id] = new Ice.clickEvent(elem, hdnClkRow, hdnClkCount, rowid, formId, delay, toggleOnClick, event, useEvent, hdnFld, toggleClassNames);
    }
}

Ice.registerDblClick = function(elem) {
    if (document.selection) document.selection.empty();
    if (Ice.clickEvents[elem.id]) {
        Ice.clickEvents[elem.id].submit(2);
    }
}

Ice.clickEvent = Class.create({
    initialize: function(elem, hdnClkRow, hdnClkCount, rowid, formId, delay, toggleOnClick, event, useEvent, hdnFld, toggleClassNames) {
        this.elem = elem;
        this.hdnClkRow = hdnClkRow;
        this.hdnClkCount = hdnClkCount;
        this.rowid = rowid;
        this.formId = formId;

        if (delay < 0) this.delay = 0;
        else if (delay > 1000) this.delay = 1000;
        else this.delay = delay;

        this.toggleOnClick = toggleOnClick;
        if (this.toggleOnClick) {
            this.event = Object.clone(event);
            this.useEvent = useEvent;
            this.hdnFld = hdnFld;
            this.toggleClassNames = toggleClassNames;
        }

        this.timer = setTimeout(this.submit.bind(this, 1), this.delay);
    },
    submit: function(numClicks) {
        clearTimeout(this.timer);
        Ice.clickEvents[this.elem.id] = null;
        var rowField = document.forms[this.formId][this.hdnClkRow];
        rowField.value = this.rowid;
        var countField = document.forms[this.formId][this.hdnClkCount];
        countField.value = numClicks;
        if (this.toggleOnClick) {
            Ice.tableRowClicked(this.event, this.useEvent, this.rowid, this.formId, this.hdnFld, this.toggleClassNames);
        } else {
            var nothingEvent = new Object();
            iceSubmitPartial(null, rowField, nothingEvent);
        }
    }
});

Ice.preventTextSelection = function(event) {
    if (Ice.isEventSourceInputElement(event)) {
        return true;
    } else {
        Ice.disableTxtSelection(document.body);
        return false;
    }
}

Ice.disableTxtSelection = function (element) {
    element.onselectstart = function () {
        return false;
    }
    element.onmousedown = function (evt) {
        return false;
    };
}

Ice.enableTxtSelection = function (element) {
    element.onselectstart = function () {
        return true;
    }
    element.onmousedown = function (evt) {
        return true;
    };
}

Ice.txtAreaMaxLen = function(field, maxlength) {
    if (maxlength >= 0 && field.value.length > maxlength) {
        field.value = field.value.substring(0, maxlength);
    }
}

Ice.isEventSourceInputElement = function(event) {
    var evt = event || window.event;
    var evt = Event.extend(evt);
    var elem = evt.element();
    var tag = elem.tagName.toLowerCase();
    if (tag == 'input' || tag == 'select' || tag == 'option' || tag == 'a' || tag == 'textarea') {
        return true;
    } else {
        return false;
    }
}
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

Ice.util = Class.create();
Ice.util = {
    findForm: function(a) {
        var n = a.nodeName.toLowerCase();
        if (n == 'form') return a;
        return this.findForm(a.parentNode);
    },
    //used by the selectInputDate component
    adjustMyPosition: function(element, containerElement) {
        var elementHeight = $(element).getHeight();
        var elementCumulativeTop = Element.cumulativeOffset($(element)).top;
        var documentX = document.viewport.getScrollOffsets().top + document.viewport.getHeight();
        var containerElementTop = Element.cumulativeOffset($(containerElement)).top ;
        var diff = elementCumulativeTop - containerElementTop ;
        var elementX = elementCumulativeTop + $(element).getHeight();
        var newElementX = elementHeight + diff;

        if (documentX < elementX &&
            newElementX < documentX &&
            elementHeight < containerElementTop) {
            $(element).parentNode.style.position = "absolute";
            $(element).parentNode.style.top = "-" + newElementX + "px";
        }
    },
    radioCheckboxEnter: function(form, component, evt) {
        if (evt.keyCode == Event.KEY_RETURN) {
            iceSubmit(form, component, evt);
        }
    }
};

var IE = (Try.these(
        function() {
            new ActiveXObject('Msxml2.XMLHTTP');
            return true;
        },

        function() {
            new ActiveXObject('Microsoft.XMLHTTP');
            return true;
        }

        ) || false);

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
 
//related class com.icesoft.faces.component.util.DelimitedProperties 
Ice.delimitedProperties = Class.create({
  initialize: function() {
    this.props = {};
  },

  set: function(key, value) {
    this.props[key] = value;
  },
  
  get:function(key) {
    this.props[key];
  },
  
  deleteAll: function() {
    for (p in this.props) {
       delete this.props[p];
    }
  },
  
  getPropsAsString:function() {
     var str = "";
     for (p in this.props) {
       str+= p + '!'+ this.props[p] +',';
     }
     return str;
  },
  
  getPropsAsObject:function() {
     return this.props;
  }
});
 

Ice.StateMon = Class.create();
Ice.StateMon = {
    logger:null,

    monitors:Array(),

    add:function(monitor) {
        this.logger.debug('Added monitor for [' + monitor.id + '] type [' + monitor.type + ']');
        this.monitors.push(monitor);
    },

    checkAll:function() {
        // Remove all elements no longer found, que found elements
        // that have new HTML elements for rebuilding
        var i = 0;
        var monitor = null;
        var size = this.monitors.length;
        for (i = 0; i < size; i++) {
            monitor = this.monitors[i];
            try {
                if (monitor.changeDetected()) {
                    this.logger.debug('Monitor [' + monitor.id + '] has been replaced');
                    monitor.rebuildMe = true;
                } else {
                    this.logger.debug('Monitor [' + monitor.id + '] has not been replaced');
                    monitor.rebuildMe = false;
                }
                if (!this.elementExists(monitor.id)) {
                    this.logger.debug('Monitor [' + monitor.id + '] no longer exists in dom');
                    monitor.destroyMe = true;
                }
            } catch(ee) {
                this.logger.error("Error checking monitor [" + monitor.id + "] Msg [" + ee + "]");
            }
        }
        this.destroy();
        // Rebuild monitors
        this.rebuild();
    },

    removeMonitors:function(monitor) {
        var nm = Array();
        var i = 0;
        for (i = 0; i < this.monitors.length; i++) {
            if (!this.monitors[i].removeMe)
                nm.push(this.monitors[i]);
        }
        this.monitors = nm;
    },

    destroy:function() {
        var i = 0;
        var monitor = null;
        // Destroy monitors that no longer have HTML elements
        var newMonitors = Array();
        for (i = 0; i < this.monitors.length; i++) {
            monitor = this.monitors[i];
            try {
                if (!monitor.destroyMe) {
                    newMonitors.push(monitor);
                } else {
                    try {
                        this.logger.debug("Destroyed monitor [" + monitor.id + "]");
                        monitor.destroy();
                    } catch(destroyException) {
                        this.logger.warn("Monitor [" + monitor.id + "] destroyed with exception [" + destroyException + "]");
                    }
                    monitor = null;
                }
            } catch(ee) {
                this.logger.error("Error destroying monitor [" + monitor.id + "] Msg [" + ee + "]");
            }
        }
        this.monitors = newMonitors;
    },

    destroyAll:function() {
        var i = 0;
        var monitor = null;
        // Destroy monitors that no longer have HTML elements
        var newMonitors = Array();
        for (i = 0; i < this.monitors.length; i++) {
            monitor = this.monitors[i];
            try {
                if (monitor != null) {
                    this.logger.debug("Destroyed monitor [" + monitor.id + "]");
                    monitor.destroy();
                }
            } catch(destroyException) {
                this.logger.warn("Monitor [" + monitor.id + "] destroyed with exception [" + destroyException + "]");
            }
            monitor = null;

        }
        this.monitors = Array();
    },

    rebuild:function() {
        var size = this.monitors.length;
        try {
            for (i = 0; i < size; i++) {
                monitor = this.monitors[i];
                if (monitor.rebuildMe) {
                    this.logger.debug('Rebuilding [' + monitor.id + ']');
                    try {
                        monitor.destroy();
                    } catch(monitorDestroyException) {
                        this.logger.warn('Could not destroy monitor before rebuilding ID[' + monitor.id + '] msg [' + monitorDestroyException + ']');
                    }
                    monitor.rebuild();
                    monitor.rebuildMe = false;
                    monitor.removeMe = true;
                } else {
                    this.logger.debug("Not rebuilding [" + monitor.id + "] type [" + monitor.type + "]");
                }
            }
            // This monitor is dead. A new one was created
            this.removeMonitors();
        } catch(ee) {
            this.logger.error("Error rebuilding monitors [" + ee + "]");
        }
    },

    elementExists:function(id) {
        var o = $(id);
        if (!o)return false;
        return true;
    },

    elementReplaced:function(ele) {
        if (ele && !ele.id) {
            // If element does not have an ID then it wont require initialization
            return false;
        }
        var currentEle = $(ele.id);
        if (!currentEle) {
            this.logger.debug('Element not found id[' + ele.id + '] element[' + ele + '] type [' + ele.nodeName + ']');
        }
        if (currentEle != null && currentEle != ele) {
            this.logger.debug("Element replaced");
            return true;
        }
    }
};

Ice.MonitorBase = Class.create();
Ice.MonitorBase.prototype = {
    object:null,
    id:null,
    htmlElement:null,
    rebuildMe:false,

    rebuild:function() {
    },

    createOptions:null,
    options:null,
    destroyMe:false,

    destroy:function() {
    },

    type:'Base',

    initialize:function() {
    },

    changeDetected:function() {
        return Ice.StateMon.elementReplaced(this.htmlElement);
    },

    removeMe:false
};

Ice.SortableMonitor = Class.create();
Ice.SortableMonitor.prototype = Object.extend(new Ice.MonitorBase(), {
    initialize:function(htmlElement, createOptions) {
        this.type = 'Sortable';
        this.object = null;
        this.id = htmlElement.id;
        this.htmlElement = htmlElement;
        this.createOptions = createOptions;
    },

    destroy:function() {
        Sortable.destroy(this.htmlElement);
    },

    rebuild:function() {
        Ice.StateMon.logger.debug('Rebuilding Sortable ID[' + this.id + '] Options[' + this.createOptions + ']');
        Sortable.create(this.id, this.createOptions);
    },

    changeDetected:function() {
        return true;
    }
});

Ice.DraggableMonitor = Class.create();
Ice.DraggableMonitor.prototype = Object.extend(new Ice.MonitorBase(), {
    initialize:function(htmlElement, createOptions) {
        this.type = 'Draggable';
        this.object = null;
        this.id = htmlElement.id;
        this.htmlElement = htmlElement;
        this.createOptions = createOptions;
    },

    destroy:function() {
        this.object.destroy();
        Ice.StateMon.logger.debug('Destroyed Draggable [' + this.id + ']');
        $A(Draggables.drags).each(function(drag) {
            Ice.StateMon.logger.debug('Draggable [' + drag.element.id + "] not destroyed");
        });
    },

    rebuild:function() {
        Ice.StateMon.logger.debug('Rebuilding Draggable ID[' + this.id + '] Options[' + this.createOptions + ']');
        var d = new Draggable(this.id, this.createOptions);
        Ice.StateMon.logger.debug('Rebuilding Draggable ID[' + this.id + '] Options[' + this.createOptions + '] Complete');
    }
});


Ice.DroppableMonitor = Class.create();
Ice.DroppableMonitor.prototype = Object.extend(new Ice.MonitorBase, {
    initialize:function(htmlElement, createOptions) {
        this.type = 'Droppable';
        this.object = null;
        this.id = htmlElement.id;
        this.htmlElement = htmlElement;
        this.createOptions = createOptions;
    },

    destroy:function() {
        Droppables.remove(this.htmlElement);
    },

    rebuild:function() {
        Ice.StateMon.logger.debug('Rebuilding Droppables ID[' + this.id + '] Options[' + this.createOptions + ']');
        Droppables.add(this.id, this.createOptions);
    }
});

Ice.AutocompleterMonitor = Class.create();
Ice.AutocompleterMonitor.prototype = Object.extend(new Ice.MonitorBase, {
    initialize:function(htmlElement, update, createOptions, rowClass, selectedRowClass) {
        this.type = 'Autocompleter';
        this.object = null;
        this.id = htmlElement.id;
        this.htmlElement = htmlElement;
        this.createOptions = createOptions;
        this.update = update;
        this.rowClass = rowClass;
        this.selectedRowClass = selectedRowClass;
    },

    destroy:function() {
        this.object.dispose();
    },

    rebuild:function() {
        Ice.StateMon.logger.debug('Rebuilding Autocompleter ID[' + this.id + '] Options[' + this.createOptions + ']');
        return new Ice.Autocompleter(this.id, this.update.id, this.createOptions, this.rowClass, this.selectedRowClass);
    }
});
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

Ice.DnD.StyleReader = Class.create();
Ice.DnD.StyleReader = {

    styles: 'position,top,left,display',

    buildStyle: function(ele) {
        //Ice.DnD.logger.debug("Building Style");
        var result = '';
        Ice.DnD.StyleReader.styles.split(',').each(
                function(style) {
                    result += style + ':' + Ice.DnD.StyleReader.getStyle(ele, style) + ';';
                });
        return result;
    },
    getStyle: function(x, styleProp) {
        if (x.currentStyle)
            var y = x.currentStyle[styleProp];
        else if (window.getComputedStyle)
            var y = document.defaultView.getComputedStyle(x, null).getPropertyValue(styleProp);
        else
            var y = x.style[styleProp];
        return y;
    },

    findCssField:function(ele, f) {
        if (!f)
            f = Ice.util.findForm(ele);
        var fe = f.getElementsByTagName('input');
        var cssUpdate = null;
        var i = 0;
        // We only want hidden fields.
        for (i = 0; i < fe.length; i++) {
            if (fe[i].type == 'hidden' && fe[i].name == 'icefacesCssUpdates') {
                cssUpdate = fe[i];
                break;
            }
        }
        return cssUpdate;
    },
    upload: function(ele, submit) {

        var cssUpdate = Ice.DnD.StyleReader.findCssField(ele);

        if (cssUpdate) {
            var val = cssUpdate.value;
            var css = Ice.DnD.StyleReader.buildStyle(ele);
            Ice.DnD.logger.debug("Update CSS ID[" + ele.id + "] CSS[" + css + "] form filed name = [" + cssUpdate.name + "]");
            cssUpdate.value = val + ele.id + '{' + css + '}';
            if (submit) {
                var form = Ice.util.findForm(ele);
                iceSubmitPartial(form, ele, null);
            }
        }
    }
}


Ice.modal = Class.create();
Ice.modal = {
    running:false,
    target:null,
    ids: [],
    tabindexValues: [],
    zIndexCount: 25000,
    start:function(target, iframeUrl,trigger, manualPosition) {
        var modal = document.getElementById(target);
        modal.style.visibility = 'hidden';
        modal.style.position = 'absolute';
        var iframe = document.getElementById('iceModalFrame' + target);
        if (!iframe) {
            iframe = document.createElement('iframe');
            iframe.title = 'Ice Modal Frame';
            iframe.frameborder = "0";
            iframe.id = 'iceModalFrame' + target;
            iframe.src = iframeUrl;
            iframe.style.zIndex = Ice.modal.zIndexCount;
            Ice.modal.zIndexCount += 3;
            iframe.style.opacity = 0.5;
            iframe.style.filter = 'alpha(opacity=50)';

            iframe.style.position = 'absolute';
            iframe.style.visibility = 'hidden';
            iframe.style.backgroundColor = 'black';
            iframe.style.borderWidth = "0";

            iframe.style.top = '0';
            iframe.style.left = '0';
            //trick to avoid bug in IE, see http://support.microsoft.com/kb/927917
            modal.parentNode.insertBefore(iframe, modal);
            var modalDiv = document.createElement('div');
            modalDiv.style.position = 'absolute';
            modalDiv.style.zIndex = parseInt(iframe.style.zIndex) + 1;
            modalDiv.style.backgroundColor = 'transparent';
            modal.parentNode.insertBefore(modalDiv, modal);            
            var resize = function() {
                //lookup element again because 'resize' closure is registered only once
                var frame = document.getElementById('iceModalFrame' + target);
                if (frame) {
                    var frameDisp = frame.style.display;
                    frame.style.display = "none";
                    var documentWidth = document.documentElement.scrollWidth;
                    var bodyWidth = document.body.scrollWidth;
                    var documentHeight = document.documentElement.scrollHeight;
                    var bodyHeight = document.body.scrollHeight;
                    var width = (bodyWidth > documentWidth ? bodyWidth : documentWidth) ;
                    var height = (bodyHeight > documentHeight ? bodyHeight : documentHeight);
                    var viewportHeight = document.viewport.getHeight();
                    if (height < viewportHeight) height = viewportHeight;
                    frame.style.width = width + 'px';
                    frame.style.height = height + 'px';
                    frame.style.visibility = 'visible';
                    var modalWidth = 100;
                    var modalHeight = 100;
                    try {
                        modalWidth = Element.getWidth(modal);
                        modalHeight = Element.getHeight(modal);
                    } catch (e) {
                    }
                    modalWidth = parseInt(modalWidth) / 2;
                    modalHeight = parseInt(modalHeight) / 2;
                    modal.style.top = (parseInt(height) / 2) - modalHeight + "px";
                    modal.style.left = (parseInt(width) / 2 ) - modalWidth + "px";
                    frame.style.display = frameDisp;
                }
            };
            resize();
            Event.observe(window, "resize", resize);
            Event.observe(window, "scroll", resize);
        }

        var modal = document.getElementById(target);

        modal.style.zIndex = parseInt(iframe.style.zIndex) + 2;
        Ice.modal.target = modal;
        Ice.modal.ids.push(target);
        if (!Ice.modal.running) {
            Ice.modal.disableTabindex();
        }
        Ice.modal.running = true;
        modal.style.visibility = 'visible';
        if (trigger) {
            Ice.modal.trigger = trigger;
            $(trigger).blur();
            setFocus('');
        }
    },
    stop:function(target) {
        if (Ice.modal.ids.last() == target) {
            var iframe = document.getElementById('iceModalFrame' + target);
            if (iframe) {
                iframe.parentNode.removeChild(iframe.nextSibling);            	
                iframe.parentNode.removeChild(iframe);
                logger.debug('removed modal iframe for : ' + target);
            }
            Ice.modal.ids.pop();
            Ice.modal.zIndexCount -= 3;            
            Ice.modal.running = false;
            if (Ice.modal.trigger) {
                Ice.Focus.setFocus(Ice.modal.trigger);
                Ice.modal.trigger = '';
            }
            Ice.modal.restoreTabindex();
        }
    },
    disableTabindex: function() {
        var focusables = {};
        focusables.a = document.getElementsByTagName('a');
        focusables.area = document.getElementsByTagName('area');
        focusables.button = document.getElementsByTagName('button');
        focusables.input = document.getElementsByTagName('input');
        focusables.object = document.getElementsByTagName('object');
        focusables.select = document.getElementsByTagName('select');
        focusables.textarea = document.getElementsByTagName('textarea');

        var tabindexValues = [];
        for (listName in focusables) {
            var list = focusables[listName]
            for (var j = 0; j < list.length; j++) {
                var ele = list[j];
                if (!Ice.modal.containedInId(ele,Ice.modal.ids.last())) {
                    var obj = {};
                    obj.element = ele;
                    obj.tabIndex = ele.tabIndex ? ele.tabIndex : '';
                    ele.tabIndex = '-1';
                    tabindexValues.push(obj);
                }
            }
        }
        Ice.modal.tabindexValues = tabindexValues;
    },
    restoreTabindex: function() {
        Ice.modal.tabindexValues.each(function(obj) {
            obj.element.tabIndex = obj.tabIndex;
        });
        Ice.modal.tabindexValues = [];
    },
    containedInId:function(node, id) {
        if (node.id == id) {
            return true;
        }
        var parent = node.parentNode;
        if (parent) {
            return Ice.modal.containedInId(parent, id);
        }
        return false;
    }
};

Ice.autoCentre = Class.create();
Ice.autoCentre = {
	    ids:[],
	    centerAll:function() {
	        Ice.autoCentre.ids.each(Ice.autoCentre.keepCentred);
	    },
	    keepCentred:function(id) {
        var scrollX = window.pageXOffset || document.body.scrollLeft || document.documentElement.scrollLeft;
        var scrollY = window.pageYOffset || document.body.scrollTop || document.documentElement.scrollTop;
        var div = document.getElementById(id);
        if (div) {
            var x = Math.round((Element.getWidth(document.body) - Element.getWidth(div)) / 2 + scrollX);
            if (x < 0) x = 0;
            var y = Math.round(((window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight) - Element.getHeight(div)) / 2 + scrollY);
            if (y < 0) y = 0;
            x = x + "px";
            y = y + "px";
            Element.setStyle(div, {position:'absolute'});
            Element.setStyle(div, {left: x});
            Element.setStyle(div, {top:y});
        }
    },
    start:function(target) {
        Ice.autoCentre.keepCentred(target);
        if (Ice.autoCentre.ids.size() == 0) {
            Event.observe(window, 'resize', Ice.autoCentre.centerAll);
            Event.observe(window, 'scroll', Ice.autoCentre.centerAll);
        }
        if (Ice.autoCentre.ids.indexOf(target) < 0) {
            Ice.autoCentre.ids.push(target);
        }
    },
    stop:function(target) {
        Ice.autoCentre.ids = Ice.autoCentre.ids.without(target);
        if (Ice.autoCentre.ids.size() == 0) {
            Event.stopObserving(window, 'resize', Ice.autoCentre.centerAll);
            Event.stopObserving(window, 'scroll', Ice.autoCentre.centerAll);
        }
    }
};

Ice.autoPosition = Class.create();
Ice.autoPosition = {
    id:null,
    xPos:null,
    yPos:null,
    keepPositioned:function() {
        var scrollX = window.pageXOffset || document.body.scrollLeft || document.documentElement.scrollLeft;
        var scrollY = window.pageYOffset || document.body.scrollTop || document.documentElement.scrollTop;
        var div = document.getElementById(Ice.autoPosition.id);
        if (div) {
            var x = Math.round(Ice.autoPosition.xPos + scrollX) + "px";
            var y = Math.round(Ice.autoPosition.yPos + scrollY) + "px";
            Element.setStyle(div, {position:'absolute'});
            Element.setStyle(div, {left: x});
            Element.setStyle(div, {top:y});
        }
    },
    start:function(target, x, y) {
        Ice.autoPosition.id = target;
        Ice.autoPosition.xPos = x;
        Ice.autoPosition.yPos = y;
        var s = document.getElementById(target).style;
        if (!Prototype.Browser.IE) s.visibility = 'hidden';
        Ice.autoPosition.keepPositioned();
        if (!Prototype.Browser.IE) s.visibility = 'visible';
        Event.observe(window, 'scroll', Ice.autoPosition.keepPositioned);
    },
    stop:function(target) {
        if (Ice.autoPosition.id == target) {
            Event.stopObserving(window, 'scroll', Ice.autoPosition.keepPositioned);
        }
    }
};

Ice.iFrameFix = Class.create();
Ice.iFrameFix = {
    start: function(elementId, url) {
        var index = navigator.userAgent.indexOf("MSIE");
        if (index == -1) return;

        var version = parseFloat(navigator.userAgent.substring(index + 5));
        if (version >= 7) return;

        var popupDiv = document.getElementById(elementId);
        if (!popupDiv) return;

        var popupIFrame = document.getElementById(elementId + ":iframe");
        if (!popupIFrame) {
            popupIFrame = document.createElement("iframe");
            //          popupIFrame.src = "javascript:void 0;";
            popupIFrame.src = url;
            popupIFrame.setAttribute("id", elementId + ":iframe")
            //          popupDiv.insertBefore(popupIFrame, popupDiv.firstChild);
            popupDiv.appendChild(popupIFrame);
        }
        popupIFrame.style.position = "absolute";
        popupIFrame.style.zIndex = -1;
        popupIFrame.style.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=0)";
        popupIFrame.style.left = "0px";
        popupIFrame.style.top = "0px";
        popupIFrame.style.width = popupDiv.offsetWidth + 'px';
        popupIFrame.style.height = popupDiv.offsetHeight + 'px';
    }
};

Ice.DnD.adjustPosition = function(id) {
    var element = $(id);
    var viewportDimensions = document.viewport.getDimensions();
    var elementDimensions = element.getDimensions();
    var viewportOffset = element.viewportOffset();
    var positionedOffset = element.positionedOffset();
    var widthDiff = viewportDimensions.width - viewportOffset.left - elementDimensions.width;
    var heightDiff = viewportDimensions.height - viewportOffset.top - elementDimensions.height;

    if (viewportOffset.left < 0) {
        element.style.left = positionedOffset.left - viewportOffset.left + "px";
    } else if (widthDiff < 0) {
        element.style.left = positionedOffset.left + widthDiff + "px";
    }
    if (viewportOffset.top < 0) {
        element.style.top = positionedOffset.top - viewportOffset.top + "px";
    } else if (heightDiff < 0) {
        element.style.top = positionedOffset.top + heightDiff + "px";
    }
}
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

Ice.Initializer = Class.create();
Ice.Initializer = {
    queuedCalls: new Array(),
    ranCalls: new Array(),
    loaded: false,
    addCall: function(id, call) {
        if (Ice.Initializer.loaded) {
            Ice.DnD.logger.debug("Call Ran");
            eval(call);
        } else {
            rapper = new Object();
            rapper.call = call;
            rapper.id = id;
            Ice.Initializer.queuedCalls[Ice.Initializer.queuedCalls.length] = rapper;
            Ice.DnD.logger.debug("Call Queued");
        }
    },

    runQueuedCalls: function() {
        Ice.Initializer.loaded = true;
        ar = Ice.Initializer.queuedCalls;
        var i = 0;
        for (i = 0; i < ar.length; i++) {
            eval(ar[i].call);
            Ice.Initializer.ranCalls[ar[i].id] = true;
        }
        Ice.DnD.logger.debug("Run Queued Calls");
    }
};
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


// Used to improve drag perofmance when many drop targets are present
var DropRegions = {
    init:false,
    SCALE:10,
    map:[],

    register: function(drop) {
        var element = drop.element;
        var topLeft = Position.cumulativeOffset(element);
        var bottomRight = [topLeft[0] + element.offsetWidth, topLeft[1] + element.offsetHeight];
        var tlX = Math.round(topLeft[0] / this.SCALE);
        var tlY = Math.round(topLeft[1] / this.SCALE);
        var brX = Math.round(bottomRight[0] / this.SCALE) + 1;
        var brY = Math.round(bottomRight[1] / this.SCALE) + 1;
        var x = 0;
        var y = 0;

        for (x = tlX; x < brX; x++) {
            for (y = tlY; y < brY; y++) {
                if (this.map[x] == null)
                    this.map[x] = [];
                if (this.map[x][y] == null)
                    this.map[x][y] = [];
                this.map[x][y].push(drop);
            }
        }
    },

    drops: function(point) {
        var x = Math.round(point[0] / DropRegions.SCALE);
        var y = Math.round(point[1] / DropRegions.SCALE);
        if (this.map[x] == null)
            return [];
        if (this.map[x][y] == null)
            return [];
        return this.map[x][y];
    }
}

Ice.DndEvent = Class.create();
Ice.DndEvent.lastEvent = null;
Ice.DndEvent.prototype = {
    drag:null,
    drop:null,
    eventType:null,
    dragFire:null,
    dropFire:null,
    submitInfo:new Ice.delimitedProperties(),
    initialize: function() {
    },

    submit: function() {
        var ele = this.drag.element;
        var iframe = document.getElementById('iceModalFrame' + ele.id);
        if (iframe) {
            ele.style.zIndex = parseInt(iframe.style.zIndex) + 2;
        }
        if (this.drag.options.sort == true)return;
        thisEv = ele.id + '-' + this.eventType;

        try {
            Ice.DndEvent.lastEvent = thisEv;
            ignoreDrag = this.ignoreEvent(this.drag.options.mask);
            if (ignoreDrag)Ice.DnD.logger.debug("Drag Type [" + this.eventType + "] Ignored. Mask [" + this.drag.options.mask + "]");
            ignoreDrop = true;

            if (this.drop)ignoreDrop = this.ignoreEvent(this.drop.mask);
            if (this.drop)Ice.DnD.logger.debug("Drop Mask [" + this.drop.mask + "] Ignored [" + ignoreDrop + "]");
            if (ignoreDrag && ignoreDrop)return;
            if (this.drop && ignoreDrop)Ice.DnD.logger.debug("Drop Type [" + this.eventType + "] Ignored. Mask [" + this.drop.mask + "]");

            var ignoreCss = false;
            if (this.drag.options.revert == true)ignoreCss = true;
            if (this.drag.options.dragGhost == true)ignoreCss = true;

            if (this.eventType == 4 || this.eventType == 5 || this.eventType == 1)ignoreCss = true; // Don't send style updates on hovering

            Ice.DnD.logger.debug("DnD Event [" + this.eventType + "] ignoreCss[" + ignoreCss + "] value [" + Ice.DnD.StyleReader.buildStyle(ele) + "]");
            if (!ignoreDrag) {
                Ice.DnD.logger.debug("Drag CSS");
                this.populateDrag(ele, ignoreCss);
                // Different browsers need different values (Safri will take second, IE will take first for example)
                if (this.drag.dragGhost == true)this.populateDrag(this.drag._original, ignoreCss);
            }
            if (!ignoreDrop) {
                Ice.DnD.logger.debug("Drop CSS");
                this.populateDrop(this.drop.element, ignoreCss);
            }
            //don't submit if the "clientOnly" attribute is true on the panelPopup
            var clientOnly = $(ele.id + "clientOnly");
            if ((!ignoreDrag || !ignoreDrop) && !clientOnly) {
                Ice.DnD.logger.debug("DnD Event [" + this.eventType + "] Sent");
                var form = Ice.util.findForm(ele);
                var formId = form.id;
                var nothingEvent = new Object();
                var cssUpdate = Ice.DnD.StyleReader.findCssField(ele, form);
                Ice.DnD.logger.debug("Submitting  drag form ID[" + form.id + "] CssUpdate [" + cssUpdate.value + "]!");
                this.serializeSubmitInfo(form);
                try {
                    iceSubmitPartial(form, ele, nothingEvent);
                } catch(formExcept) {
                    Ice.DnD.logger.error("error submitting dnd event", formExcept);

                }
                Ice.DnD.logger.debug("drag form ID[" + form.id + "] submitted");
                // Drop targets might be in a separate form. If this is the case then
                // submit both forms
                if (!ignoreDrop) {
                    form = Ice.util.findForm(this.drop.element);

                    if (form.id != formId) {
                        Ice.DnD.logger.debug("Diff [" + form.id + "]!=[" + formId + "] Submitting");
                        iceSubmitPartial(form, this.drop.element, nothingEvent);
                    }
                }
                this.resetSubmitInfo(form);
            }
        } catch(exc) {
            Ice.DnD.logger.error("Could not find form in drag drop", exc);
        }
        return;
    },

    populateDrag:function(ele, ignoreCss) {
       this.submitInfo.set(ele.id+'status', this.eventType);
       if (this.drop) {
          this.submitInfo.set(ele.id+'dropID', this.drop.element.id);         
       }
       if (!ignoreCss) Ice.DnD.StyleReader.upload(ele);
       return true;
    },

    populateDrop:function(ele, ignoreCss) { 
        this.submitInfo.set(ele.id+'status', this.eventType);
        this.submitInfo.set(ele.id+'dropID', this.drag.element.id);      
        if (!ignoreCss) Ice.DnD.StyleReader.upload(ele);
        return true;
    },

    ignoreEvent:function(mask) {
        if (!mask)return false;//No mask, no ignore
        var result = false;
        if (mask) {
            if (mask.indexOf(this.eventType) != -1) {
                result = true;
            }
        }
        return result;
    },
    
    serializeSubmitInfo: function (form) {
        var str = this.submitInfo.getPropsAsString();
        var hdn = this.getHiddenField(form); 
        if(hdn)        
           hdn.value=str.substring(0, str.length-1);        
    },
    
    resetSubmitInfo: function(form) {
        this.submitInfo.deleteAll();
        var hdn = this.getHiddenField(form);
        if(hdn)
            hdn.value="";
    },
    
    getHiddenField: function(form) {
        var hdnId = form.id + ":iceDND";
        var hdn = document.getElementsByName(hdnId)[0];
        if (hdn)
            return hdn;
        else
            Ice.DnD.logger.debug("Data field not found");
        return null;
    }
};

Ice.SortEvent = Class.create();
Ice.SortEvent.prototype = {
    start:function() {
        Ice.DnD.logger.debug('Starting Sort Event');
    },

    end:function() {
        Ice.DnD.logger.debug('Ending Sort Event');
    }
};
Draggable.prototype.dragGhost = false;
Draggable.prototype.ORIGINAL_initialize = Draggable.prototype.initialize;
Draggable.prototype.initialize = function(C) {
    var B = Ice.StateMon.monitors;
    for (i = 0; i < B.length; i++) {
        A = B[i];
        if (A.id == C && A.type == "Draggable") {
            Ice.DnD.logger.debug("Draggable [" + $(C).id + "] has already been created");
            return;
        }
    }
    this.element = $(C);
    var D = arguments[1];
    if (D.dragGhost == true) {
        this.dragGhost = true;
    }
    if (!D.starteffect) {
        D.starteffect = function() {
        };
    }
    if (!D.endeffect) {
        D.endeffect = function() {
        };
    }
    if (D.handle) {
        D.handle = $(D.handle);
        D.handle = $(D.handle.id);
    }
    this.ORIGINAL_initialize(this.element, D);
    if (!D.sort) {
        Ice.DnD.logger.debug("Draggable Created ID[" + this.element.id + "]");
        var A = new Ice.DraggableMonitor(this.element, D);
        A.object = this;
        Ice.StateMon.add(A);
    }
    Ice.DnD.logger.debug("Draggable [" + this.element.id + "] created");
};
Draggable.prototype.ORIGINAL_startDrag = Draggable.prototype.startDrag;
Draggable.prototype.startDrag = function(C) {
    this.dragging = true;
    if (this.dragGhost == true) {
        Ice.DnD.logger.debug("Init Drag Ghost ID[" + this.element.id + "]");
        Draggables.register(this);
        try {
            this._ghost = this.element.cloneNode(true);
            var B = Ice.util.findForm(this.element);
            B.appendChild(this._ghost);
            Position.absolutize(this._ghost);
            Element.makePositioned(this._ghost);
            this._original = this.element;
            Position.clone(this._original, this._ghost);
            var D = parseInt(this._original.style.zIndex);
            this._ghost.style.left = Event.pointerX(C) + "px";
            this._ghost.style.zIndex = ++D;
            this.element = this._ghost;
            this.eventResize = this.resize.bindAsEventListener(this);
            Event.observe(window, "resize", this.eventResize);
        } catch(A) {
            Ice.DnD.logger.error("Error init DragGhost  ID[" + this.element.id + "]", A);
        }
    }
    if (this.options.dragCursor) {
        this._cursor = this.element.cloneNode(true);
        document.body.appendChild(this._cursor);
        Position.absolutize(this._cursor);
        var D = 1 + this.element.style.zIndex;
        this._cursor.style.zIndex = D;
        Ice.DnD.logger.debug("clone created");
    }
    this.ORIGINAL_startDrag(C);
};
Draggable.prototype.ORIGINAL_draw = Draggable.prototype.draw;
Draggable.prototype.draw = function(A) {
    if (!this.options.dragCursor) {
        return this.ORIGINAL_draw(A);
    }
    var E = Position.cumulativeOffset(this.element);
    var D = this.currentDelta();
    E[0] -= D[0];
    E[1] -= D[1];
    var C = A;
    if (this.options.snap) {
        if (typeof this.options.snap == "function") {
            C = this.options.snap(C[0], C[1]);
        } else {
            if (this.options.snap instanceof Array) {
                C = C.map(function(F, G) {
                    return Math.round(F / this.options.snap[G]) * this.options.snap[G];
                }.bind(this));
            } else {
                C = C.map(function(F) {
                    return Math.round(F / this.options.snap) * this.options.snap;
                }.bind(this));
            }
        }
    }
    var B = this._cursor.style;
    if ((!this.options.constraint) || (this.options.constraint == "horizontal")) {
        B.left = C[0] + "px";
    }
    if ((!this.options.constraint) || (this.options.constraint == "vertical")) {
        B.top = C[1] + "px";
    }
    if (B.visibility == "hidden") {
        B.visibility = "";
    }
};
Draggable.prototype.resize = function(A) {
};
Draggable.removeMe = function(D) {
    $(D).undoPositioned();
    var C = Ice.StateMon.monitors;
    var F = Array();
    for (i = 0; i < C.length; i++) {
        monitor = C[i];
        try {
            if (monitor.id == D && monitor.type == "Draggable") {
                if (monitor.object.dragging) {
                    return;
                }
                try {
                    var E = $(D + "clientOnly");
                    if (!E) {
                        monitor.destroyMe = true;
                        monitor.destroy();
                    }
                } catch(B) {
                    logger.warn("Monitor [" + monitor.id + "] destroyed with exception [" + B + "]");
                }
            } else {
                F.push(monitor);
            }
        } catch(A) {
            logger.error("Error destroying monitor [" + monitor.id + "] Msg [" + A + "]");
        }
    }
    Ice.StateMon.monitors = F;
};
Draggable.prototype.ORIGINAL_updateDrag = Draggable.prototype.updateDrag;
Draggable.prototype.updateDrag = function(F, G) {
    Droppables.affectedDrop = null;
    this.ORIGINAL_updateDrag(F, G);
    ad = Droppables.affectedDrop;
    iceEv = new Ice.DndEvent();
    iceEv.drag = this;
    if (this.dragGhost == true) {
        var A = parseInt(this.element.offsetHeight);
        var C = parseInt(Element.getStyle(this.element, "top").split("px")[0]);
        if (Prototype.Browser.IE) {
            C = this.element.cumulativeOffset().top;
        }
        var B = Event.pointerY(F);
        var E = A + C;
        var D = (B > C && B < E);
        if (!D) {
            this.element.style.top = B + "px";
        }
    }
    if (this.hoveringDrop && !ad) {
        iceEv.eventType = Ice.DnD.HOVER_END;
    }
    if (ad && (!this.hoveringDrop || this.hoveringDrop.element.id != ad.element.id)) {
        iceEv.eventType = Ice.DnD.HOVER_START;
        iceEv.drop = ad;
    }
    this.hoveringDrop = (ad != null) ? ad : null;
    if (!iceEv.eventType) {
        iceEv.eventType = Ice.DnD.DRAG_START;
    }
    iceEv.submit();
};
Draggable.prototype.ORIGINAL_finishDrag = Draggable.prototype.finishDrag;
Draggable.prototype.finishDrag = function(D, E) {
    if (!this.options.sort) {
        this.dragging = false;
        if (E) {
            iceEv = new Ice.DndEvent();
            iceEv.drag = this;
            if (this.hoveringDrop) {
                iceEv.drop = this.hoveringDrop;
                iceEv.eventType = Ice.DnD.DROPPED;
            } else {
                iceEv.eventType = Ice.DnD.DRAG_CANCEL;
            }
            iceEv.submit();
            if (this.dragGhost == true) {
                this.element = this._original;
                Element.remove(this._ghost);
                this._ghost = null;
            }
            if (this.options.dragCursor) {
                Element.remove(this._cursor);
                this._cursor = null;
            }
            Draggable.removeMe(this.element.id);
        }
    }
    this.ORIGINAL_finishDrag(D, E);
    DropRegions.init = false;
    DropRegions.map = [];
    if (this.options.sort && E) {
        try {
            var C = Ice.util.findForm(this.element);
            var B = new Object();
            Ice.DnD.logger.debug("Submitting Sortable [" + this.element + "]");
            iceSubmit(C, this.element, B);
        } catch(A) {
            Ice.DnD.logger.error("error submiting sortable element[" + this.element + "] Err Msg[" + A + "]");
        }
    }
};
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
// Original license and copyright:
// Copyright (c) 2005 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
//           (c) 2005 Sammi Williams (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz)
// 
// See scriptaculous.js for full license.

Droppables.affectedDrop = null;

Droppables.ORIGINAL_show = Droppables.show;
Droppables.show = function(point, element) {
    if (!this.drops.length) return;

    if (this.last_active) this.deactivate(this.last_active);
    var dropsToScan = this.drops;
    if (DropRegions.init && this.drops.all(function(drop) {
        return !drop.scrollid;
    })) {
        dropsToScan = DropRegions.drops(point);
    }
    dropsToScan.each(function(drop) {
        if (Position.within(drop.element, point[0], point[1])) {
            if (Droppables.isAffected(point, element, drop)) {
                //Ice.DnD.logger.debug("Affected True OnHover" + drop.onHover + "]");
                Droppables.affectedDrop = drop;
                if (drop.onHover)
                    drop.onHover(element, drop.element, Position.overlap(drop.overlap, drop.element));
                if (drop.greedy) {
                    Droppables.activate(drop);
                }
            }
        }
        if (!DropRegions.init) {
            DropRegions.register(drop);
        }
    });
    DropRegions.init = true;
};

Droppables.ORIGINAL_isAffected = Droppables.isAffected;
Droppables.isAffected = function(point, element, drop) {
    var result = false;
    result = Droppables.ORIGINAL_isAffected(point, element, drop);
    if (result && drop.sort) {
        if (!Ice.DnD.sortableDraggable(element)) {
            result = false;
        }
    }
    return result;
};

Droppables.ORIGINAL_add = Droppables.add;
Droppables.add = function(ele, options) {
    var monitors = Ice.StateMon.monitors;
    for (i = 0; i < monitors.length; i++) {
        monitor = monitors[i];
        if (monitor.id == ele && monitor.type == 'Droppable') {
            if (ele['hasDroppable']) {
                return;
            } else {
                monitor.removeMe = true;
                Ice.StateMon.removeMonitors();
            }
        }
    }
    ele['hasDroppable'] = true;
    Droppables.ORIGINAL_add(ele, options);
    if (options && !options.sort) {
        var monitor = new Ice.DroppableMonitor($(ele), options);
        Ice.StateMon.add(monitor);
    }
}
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
// Original license and copyright:
// Copyright (c) 2005 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
//           (c) 2005 Sammi Williams (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz)
// 
// See scriptaculous.js for full license.


var SortableObserver = Class.create();
SortableObserver.prototype = {

    initialize: function(element, observer) {
        this.element = $(element);
        this.observer = observer;
        this.lastValue = Sortable.serialize(this.element);
    },

    onStart: function(name, drag) {
        this.lastValue = Sortable.serialize(this.element);
        var options = Sortable.options(this.element);
        options.lastDrag = drag;
        //alert("Name [" + name + "] drag [" + drag.element.id + "]");
        options.lastDrag = drag.element.id;
        //SortableObserver.count++;
    },

    onEnd: function() {
        Sortable.unmark();
        var newValue = Sortable.serialize(this.element);
        var options = Sortable.options(this.element);
        options.serializeValue = newValue;
        if (this.lastValue != newValue) {
            this.observer(this.element)
        }
    }
}

var Sortable = {
    sortables: {},
    sortableElements: Array(),
    kids:null,
    _findRootElement: function(element) {
        while (element.tagName != "BODY") {
            if (element.id && Sortable.sortables[element.id]) return element;
            element = element.parentNode;
        }
    },

    options: function(element) {
        element = Sortable._findRootElement($(element));
        if (!element) return;
        return Sortable.sortables[element.id];
    },

    destroy: function(element) {
        var s = Sortable.options(element);

        if (s) {
            Draggables.removeObserver(s.element);
            s.droppables.each(function(d) {
                Droppables.remove(d)
            });
            s.draggables.invoke('destroy');

            delete Sortable.sortables[s.element.id];
            var i = 0;
            var n = Array();
            for (i = 0; i < Sortable.sortableElements.length; i++) {
                if (Sortable.sortableElements[i] && Sortable.sortableElements[i].id != s.element.id) {
                    n.push(Sortable.sortableElements[i]);
                }
            }
            Sortable.sortableElements = n;
        }
    },

    create: function(element, o, override) {
        element = $(element);
        if (Ice.DnD.alreadySort(element)) {
            Ice.DnD.logger.debug('Sort ID [' + element.id + '] already created');
            return;
        }
        var monitor = new Ice.SortableMonitor(element, o);
        var options = Object.extend({
            element:     element,
            tag:         'li',       // assumes li children, override with tag: 'tagname'
            dropOnEmpty: false,

            overlap:     'vertical', // one of 'vertical', 'horizontal'
            constraint:  'vertical', // one of 'vertical', 'horizontal', false
            containment: element,    // also takes array of elements (or id's); or false
            handle:      false,      // or a CSS class
            only:        false,
            hoverclass:  null,
            ghosting:    false,
            format:      null,
            onChange:    Prototype.emptyFunction,
            onUpdate:    Prototype.emptyFunction
        }, arguments[1] || {});
        // clear any old sortable with same element
        this.destroy(element);
        // build options for the draggables
        var options_for_draggable = {
            revert:      true,
            ghosting:    options.ghosting,
            constraint:  options.constraint,
            handle:      options.handle,
            // Sort flag is used by Drag and Drop javascript to avoid Drag and Drop events from being sent
            sort:        true};
        if (options.starteffect)
            options_for_draggable.starteffect = options.starteffect;
        if (options.reverteffect)
            options_for_draggable.reverteffect = options.reverteffect;
        else
            if (options.ghosting) options_for_draggable.reverteffect = function(element) {
                element.style.top = 0;
                element.style.left = 0;
            };
        if (options.endeffect)
            options_for_draggable.endeffect = options.endeffect;
        if (options.zindex)
            options_for_draggable.zindex = options.zindex;
        // build options for the droppables
        var options_for_droppable = {
            overlap:     options.overlap,
            containment: options.containment,
            hoverclass:  options.hoverclass,
            onHover:     Sortable.onHover,
            greedy:      !options.dropOnEmpty,
            sort:        true
        }
        // fix for gecko engine
        Element.cleanWhitespace(element);
        options.draggables = [];
        options.droppables = [];

        // drop on empty handling
        if (options.dropOnEmpty) {
            Droppables.add(element,
            {
                containment: options.containment,
                onHover: Sortable.onEmptyHover, greedy: false, sort:true});
            options.droppables.push(element);
        }
        (options.elements || this.findElements(element, options) || []).each(function(e, i) {
            var handle = options.handles ? $(options.handles[i]) :
                         (options.handle ? $(e).select('.' + options.handle)[0] : e);
            options.draggables.push(
                    new Draggable(e, Object.extend(options_for_draggable, { handle: handle })));
            Droppables.add(e, options_for_droppable);
            if (options.tree) e.treeNode = element;
            options.droppables.push(e);
        });

        // keep reference
        this.sortables[element.id] = options;
        this.sortableElements.push(element);
        monitor.options = options;
        Ice.StateMon.add(monitor);
        // for onupdate
        var observer = new SortableObserver(element, options.onUpdate);
        Draggables.addObserver(observer);
    },

    //return all suitable-for-sortable elements in a guaranteed order
    findElements: function(element, options) {
        if (!element.hasChildNodes()) return null;
        var elements = [];
        $A(element.childNodes).each(function(e) {
            if (e.tagName && e.tagName.toUpperCase() == options.tag.toUpperCase() &&
                (!options.only || (Element.hasClassName(e, options.only))))
                elements.push(e);
        });
        return (elements.length > 0 ? elements.flatten() : null);
    },

    onHover: function(element, dropon, overlap) {
        if (overlap > 0.5) {
            Sortable.mark(dropon, 'before');
            if (dropon.previousSibling != element) {
                var oldParentNode = element.parentNode;
                element.style.visibility = "hidden";
                // fix gecko rendering
                dropon.parentNode.insertBefore(element, dropon);
                if (dropon.parentNode != oldParentNode)
                    Sortable.options(oldParentNode).onChange(element);
                Sortable.options(dropon.parentNode).onChange(element);
            }
        } else {
            Sortable.mark(dropon, 'after');
            var nextElement = dropon.nextSibling || null;
            if (nextElement != element) {
                var oldParentNode = element.parentNode;
                element.style.visibility = "hidden";
                // fix gecko rendering
                dropon.parentNode.insertBefore(element, nextElement);
                if (dropon.parentNode != oldParentNode)
                    Sortable.options(oldParentNode).onChange(element);
                Sortable.options(dropon.parentNode).onChange(element);
            }
        }
    },

    onEmptyHover: function(element, dropon) {
        if (element.parentNode != dropon) {
            var oldParentNode = element.parentNode;
            dropon.appendChild(element);
            Sortable.options(oldParentNode).onChange(element);
            Sortable.options(dropon).onChange(element);
        }
    },

    unmark: function() {
        if (Sortable._marker) Element.hide(Sortable._marker);
    },

    mark: function(dropon, position) {
        // mark on ghosting only
        var sortable = Sortable.options(dropon.parentNode);
        if (!sortable) return;
        if (sortable && !sortable.ghosting) return;

        if (!Sortable._marker) {
            Sortable._marker = $('dropmarker') || document.createElement('DIV');
            Element.hide(Sortable._marker);
            Element.addClassName(Sortable._marker, 'dropmarker');
            Sortable._marker.style.position = 'absolute';
            document.getElementsByTagName("body").item(0).appendChild(Sortable._marker);
        }
        var offsets = Position.cumulativeOffset(dropon);
        Sortable._marker.style.left = offsets[0] + 'px';
        Sortable._marker.style.top = offsets[1] + 'px';

        if (position == 'after')
            if (sortable.overlap == 'horizontal')
                Sortable._marker.style.left = (offsets[0] + dropon.clientWidth) + 'px';
            else
                Sortable._marker.style.top = (offsets[1] + dropon.clientHeight) + 'px';

        Element.show(Sortable._marker);
    },

    serialize : function(element) {
        element = $(element);
        var sortableOptions = Sortable.options(element);
        var options = Object.extend({
            tag:  sortableOptions.tag,
            only: sortableOptions.only,
            name: element.id,
            format: sortableOptions.format || /^[^_]*_(.*)$/
        }, arguments[1] || {});
        //alert("Last Drag [" + sortableOptions.lastDrag + "]");
        return "first;" + sortableOptions.lastDrag + ";changed;" + $(this.findElements(element, options) || []).map(function(item) {
            return item.id;
        }).join(";");
    }
}
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
// Original license and copyright:
// Copyright (c) 2005 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
//           (c) 2005 Ivan Krstic (http://blogs.law.harvard.edu/ivan)
//           (c) 2005 Jon Tirsen (http://www.tirsen.com)
// Contributors:
//  Richard Livsey
//  Rahul Bhargava
//  Rob Wills
// 
// See scriptaculous.js for full license.

var Autocompleter = {};

Autocompleter.Finder = {
    list:new Array(),
    add: function(ele, autocomplete) {
        this.list[ele.id] = autocomplete;
    },
    find: function(id) {
        return this.list[id];
    }
};

Autocompleter.Base = function() {
};

Autocompleter.Base.prototype = {
    baseInitialize: function(element, update, options, rowC, selectedRowC) {
        this.element = $(element);
        this.update = $(update);
        this.hasFocus = false;
        this.changed = false;
        this.active = false;
        this.index = -1;
        this.entryCount = 0;
        this.rowClass = rowC;
        this.selectedRowClass = selectedRowC;

        if (this.setOptions)
            this.setOptions(options);
        else
            this.options = options || {};

        this.options.paramName = this.options.paramName || this.element.name;
        this.options.tokens = this.options.tokens || [];
        this.options.frequency = this.options.frequency || 0.4;
        this.options.minChars = this.options.minChars || 1;
        this.options.onShow = this.options.onShow ||
                              function(element, update) {
                                try {
                                  if (update["style"] && (!update.style.position || update.style.position == 'absolute')) {
                                      update.style.position = 'absolute';
                                      Position.clone(element, update, {setHeight: false, offsetTop: element.offsetHeight});
                                      update.clonePosition(element.parentNode, {setTop:false, setWidth:false, setHeight:false,
                                          offsetLeft: element.offsetLeft - element.parentNode.offsetLeft});
                                  }
                                  Effect.Appear(update, {duration:0.15});
                                } catch(e){logger.info(e);}  
                              };
        this.options.onHide = this.options.onHide ||
                              function(element, update) {
                                  new Effect.Fade(update, {duration:0.15})
                              };

        if (typeof(this.options.tokens) == 'string')
            this.options.tokens = new Array(this.options.tokens);

        this.observer = null;
        this.element.setAttribute('autocomplete', 'off');
        Element.hide(this.update);
        Event.observe(this.element, "blur", this.onBlur.bindAsEventListener(this));
        Event.observe(this.element, "keypress", this.onKeyPress.bindAsEventListener(this));
        if (Prototype.Browser.IE || Prototype.Browser.WebKit)
            Event.observe(this.element, "keydown", this.onKeyDown.bindAsEventListener(this));
        // ICE-3830
        if (Prototype.Browser.IE || Prototype.Browser.WebKit)
            Event.observe(this.element, "paste", this.onPaste.bindAsEventListener(this));
    },

    show: function() {
      try {  
        if (Element.getStyle(this.update, 'display') == 'none')this.options.onShow(this.element, this.update);
        if (!this.iefix &&
            (navigator.appVersion.indexOf('MSIE') > 0) &&
            (navigator.userAgent.indexOf('Opera') < 0) &&
            (Element.getStyle(this.update, 'position') == 'absolute')) {
            new Insertion.After(this.update,
                    '<iframe id="' + this.update.id + '_iefix" title="IE6_Fix" ' +
                    'style="display:none;position:absolute;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);" ' +
                    'src="javascript:\'<html></html>\'" frameborder="0" scrolling="no"></iframe>');
            this.iefix = $(this.update.id + '_iefix');
        }
        if (this.iefix) setTimeout(this.fixIEOverlapping.bind(this), 50);
        this.element.focus();     
      } catch (e) {logger.info(e);}
    },

    fixIEOverlapping: function() {
      try {
        Position.clone(this.update, this.iefix);
        this.iefix.style.zIndex = 1;
        this.update.style.zIndex = 2;
        Element.show(this.iefix);
      } catch(e) {logger.info(e);}
    },

    hide: function() {
        this.stopIndicator();
        if (Element.getStyle(this.update, 'display') != 'none') this.options.onHide(this.element, this.update);
        if (this.iefix) Element.hide(this.iefix);
    },

    startIndicator: function() {
        if (this.options.indicator) Element.show(this.options.indicator);
    },

    stopIndicator: function() {
        if (this.options.indicator) Element.hide(this.options.indicator);
    },

    onKeyPress: function(event) {
        if (!this.active) {
            Ice.Autocompleter.logger.debug("Key press ignored. Not active.");
            switch (event.keyCode) {
                case Event.KEY_TAB:
                case Event.KEY_RETURN:
                    this.getUpdatedChoices(true, event, -1);
                    return;
                case Event.KEY_DOWN:
                    this.getUpdatedChoices(false, event, -1);
                    return;
            }
        }

        Ice.Autocompleter.logger.debug("Key Press");
        if (this.active) {
            switch (event.keyCode) {
                case Event.KEY_TAB:
                case Event.KEY_RETURN:
                //this.selectEntry();
                //Event.stop(event);

                    this.hidden = true; // Hack to fix before beta. Was popup up the list after a selection was made
                    var idx = this.selectEntry();
                    Ice.Autocompleter.logger.debug("Getting updated choices on enter");
                    this.getUpdatedChoices(true, event, idx);
                    this.hide();
                    Event.stop(event);
                    return;
                case Event.KEY_ESC:
                    this.hide();
                    this.active = false;
                    Event.stop(event);
                    return;
                case Event.KEY_LEFT:
                case Event.KEY_RIGHT:
                    return;
                case Event.KEY_UP:
                    //ICE-4549 (the KEY_UP and KEY_DOWN would be handled by the onkeydown event for IE and WebKit)
                    if (!(Prototype.Browser.IE || Prototype.Browser.WebKit)) {                      
                        this.markPrevious();
                        this.render();
                        //if(navigator.appVersion.indexOf('AppleWebKit')>0)
                        Event.stop(event);
                        return;
	                }
                case Event.KEY_DOWN:
                    //ICE-4549 
                    if (!(Prototype.Browser.IE || Prototype.Browser.WebKit)) {                 
                        this.markNext();
                        this.render();
                        //if(navigator.appVersion.indexOf('AppleWebKit')>0)
                        Event.stop(event);
                        return;
                    }

            }
        }
        else {
            if (event.keyCode == Event.KEY_TAB || event.keyCode == Event.KEY_RETURN) return;
        }

        this.changed = true;
        this.hasFocus = true;
        this.index = -1;
        //This is to avoid an element being select because the mouse just happens to be over the element when the list pops up
        this.skip_mouse_hover = true;
        if (this.active) this.render();
        if (this.observer) clearTimeout(this.observer);
        this.observer = setTimeout(this.onObserverEvent.bind(this), this.options.frequency * 1000);
    },

    onKeyDown: function(event) {
        if (!this.active) {
            switch (event.keyCode) {
                case Event.KEY_DOWN:
                    this.getUpdatedChoices(false, event, -1);
                    return;
                case Event.KEY_BACKSPACE:
                case Event.KEY_DELETE:
                    if (this.observer) clearTimeout(this.observer);
                    this.observer = setTimeout(this.onObserverEvent.bind(this), this.options.frequency * 1000);
                    return;
            }
        }
        else if (this.active) {
            switch (event.keyCode) {
                case Event.KEY_UP:
                    this.markPrevious();
                    this.render();
                    Event.stop(event);
                    return;
                case Event.KEY_DOWN:
                    this.markNext();
                    this.render();
                    Event.stop(event);
                    return;
                case Event.KEY_ESC:
                    if (Prototype.Browser.WebKit) {
                        this.hide();
                        this.active = false;
                        Event.stop(event);
                        return;
                    }
                case Event.KEY_BACKSPACE:
                case Event.KEY_DELETE:
                    if (this.observer) clearTimeout(this.observer);
                    this.observer = setTimeout(this.onObserverEvent.bind(this), this.options.frequency * 1000);
                    return;
            }
        }
    },

    activate: function() {
        this.changed = false;
        this.hasFocus = true;
    },

    onHover: function(event) {
        var element = Event.findElement(event, 'DIV');
        if (this.index != element.autocompleteIndex) {
            if (!this.skip_mouse_hover) this.index = element.autocompleteIndex;
            this.render();
        }
        Event.stop(event);
    },

    onMove: function(event) {
        if (this.skip_mouse_hover) {
            this.skip_mouse_hover = false;
            this.onHover(event);
        }
    },

    onClick: function(event) {
        this.hidden = true;
        // Hack to fix before beta. Was popup up the list after a selection was made
        var element = Event.findElement(event, 'DIV');
        this.index = element.autocompleteIndex;
        var idx = element.autocompleteIndex;
        this.selectEntry();
        this.getUpdatedChoices(true, event, idx);
        this.hide();
    },

    onBlur: function(event) {
        if (navigator.userAgent.indexOf("MSIE") >= 0) { // ICE-2225
            var strictMode = document.compatMode && document.compatMode == "CSS1Compat";
            var docBody = strictMode ? document.documentElement : document.body;
            // Right or bottom border, if any, will be treated as scrollbar.
            // No way to determine their width or scrollbar width accurately.
            if (event.clientX > docBody.clientLeft + docBody.clientWidth ||
                event.clientY > docBody.clientTop + docBody.clientHeight) {
                this.element.focus();
                return;
            }
        }
        // needed to make click events working
        setTimeout(this.hide.bind(this), 250);
        this.hasFocus = false;
        this.active = false;
    },

    // ICE-3830
    onPaste: function(event) {
        this.changed = true;
        this.hasFocus = true;
        this.index = -1;
        this.skip_mouse_hover = true;
        if (this.active) this.render();
        if (this.observer) clearTimeout(this.observer);
        this.observer = setTimeout(this.onObserverEvent.bind(this), this.options.frequency * 1000);
        return;
    },

    render: function() {
        if (this.entryCount > 0) {
            for (var i = 0; i < this.entryCount; i++)
                if (this.index == i) {
                    ar = this.rowClass.split(" ");
                    for (var ai = 0; ai < ar.length; ai++)
                        Element.removeClassName(this.getEntry(i), ar[ai]);
                    ar = this.selectedRowClass.split(" ");
                    for (var ai = 0; ai < ar.length; ai++)
                        Element.addClassName(this.getEntry(i), ar[ai]);
                }
                else {
                    ar = this.selectedRowClass.split(" ");
                    for (var ai = 0; ai < ar.length; ai++)
                        Element.removeClassName(this.getEntry(i), ar[ai]);
                    ar = this.rowClass.split(" ");
                    for (var ai = 0; ai < ar.length; ai++)
                        Element.addClassName(this.getEntry(i), ar[ai]);
                }
            if (this.hasFocus) {
                this.show();
                this.active = true;
            }
        } else {
            this.active = false;
            this.hide();
        }
    },

    markPrevious: function() {
        if (this.index > 0) this.index--
        else this.index = this.entryCount - 1;
    },

    markNext: function() {
        if (this.index == -1) {
            this.index++;
            return;
        }
        if (this.index < this.entryCount - 1) this.index++
        else this.index = 0;
    },

    getEntry: function(index) {
        try {
            return this.update.firstChild.childNodes[index];
        } catch(ee) {
            return null;
        }
    },

    getCurrentEntry: function() {
        return this.getEntry(this.index);
    },

    selectEntry: function() {
        var idx = -1;
        this.active = false;
        if (this.index >= 0) {
            idx = this.index;
            this.updateElement(this.getCurrentEntry());
            this.index = -1;
        }
        return idx;
    },

    updateElement: function(selectedElement) {
        if (this.options.updateElement) {
            this.options.updateElement(selectedElement);
            return;
        }
        var value = '';
        if (this.options.select) {
            var nodes = document.getElementsByClassName(this.options.select, selectedElement) || [];
            if (nodes.length > 0) value = Element.collectTextNodes(nodes[0], this.options.select);
        } else
            value = Element.collectTextNodesIgnoreClass(selectedElement, 'informal');

        var lastTokenPos = this.findLastToken();
        if (lastTokenPos != -1) {
            var newValue = this.element.value.substr(0, lastTokenPos + 1);
            var whitespace = this.element.value.substr(lastTokenPos + 1).match(/^\s+/);
            if (whitespace)
                newValue += whitespace[0];
            this.element.value = newValue + value;
        } else {
            this.element.value = value;
        }
        this.element.focus();

        if (this.options.afterUpdateElement)
            this.options.afterUpdateElement(this.element, selectedElement);
    },

    updateChoices: function(choices) {
        if (!this.changed && this.hasFocus) {
            this.update.innerHTML = choices;
            Element.cleanWhitespace(this.update);
            Element.cleanWhitespace(this.update.firstChild);

            if (this.update.firstChild && this.update.firstChild.childNodes) {
                this.entryCount =
                this.update.firstChild.childNodes.length;
                for (var i = 0; i < this.entryCount; i++) {
                    var entry = this.getEntry(i);
                    entry.autocompleteIndex = i;
                    this.addObservers(entry);
                }
            } else {
                this.entryCount = 0;
            }
            this.stopIndicator();
            this.index = -1;
            this.render();
        } else {
            Ice.Autocompleter.logger.debug("Not updating choices Not Changed[" + this.changed + "] hasFocus[" + this.hasFocus + "]");
        }
    },

    addObservers: function(element) {
        Event.observe(element, "mouseover", this.onHover.bindAsEventListener(this));
        Event.observe(element, "click", this.onClick.bindAsEventListener(this));
        Event.observe(element, "mousemove", this.onMove.bindAsEventListener(this));
    },

    dispose:function() {
        for (var i = 0; i < this.entryCount; i++) {
            var entry = this.getEntry(i);
            entry.autocompleteIndex = i;
            Event.stopObserving(entry, "mouseover", this.onHover);
            Event.stopObserving(entry, "click", this.onClick);
            Event.stopObserving(entry, "mousemove", this.onMove);
        }
        Event.stopObserving(this.element, "mouseover", this.onHover);
        Event.stopObserving(this.element, "click", this.onClick);
        Event.stopObserving(this.element, "mousemove", this.onMove);
        Event.stopObserving(this.element, "blur", this.onBlur);
        Event.stopObserving(this.element, "keypress", this.onKeyPress);
        if (Prototype.Browser.IE || Prototype.Browser.WebKit)
            Event.stopObserving(this.element, "keydown", this.onKeyDown);
        Autocompleter.Finder.list[this.element.id] = null;
        Ice.Autocompleter.logger.debug("Destroyed autocomplete [" + this.element.id + "]");
    },

    onObserverEvent: function() {
        this.changed = false;
        if (this.getToken().length >= this.options.minChars) {
            this.startIndicator();
            this.getUpdatedChoices(false, undefined, -1);
        } else {
            this.active = false;
            this.hide();
            this.getUpdatedChoices(false, undefined, -1);
        }
    },

    getToken: function() {
        var tokenPos = this.findLastToken();
        if (tokenPos != -1)
            var ret = this.element.value.substr(tokenPos + 1).replace(/^\s+/, '').replace(/\s+$/, '');
        else
            var ret = this.element.value;

        return /\n/.test(ret) ? '' : ret;
    },

    findLastToken: function() {
        var lastTokenPos = -1;

        for (var i = 0; i < this.options.tokens.length; i++) {
            var thisTokenPos = this.element.value.lastIndexOf(this.options.tokens[i]);
            if (thisTokenPos > lastTokenPos)
                lastTokenPos = thisTokenPos;
        }
        return lastTokenPos;
    }
}

Ajax.Autocompleter = Class.create();
Object.extend(Object.extend(Ajax.Autocompleter.prototype, Autocompleter.Base.prototype), {
    initialize: function(element, update, url, options) {
        this.baseInitialize(element, update, options);
        this.options.asynchronous = true;
        this.options.onComplete = this.onComplete.bind(this);
        this.options.defaultParams = this.options.parameters || null;
        this.url = url;
    },

    getUpdatedChoices: function() {
        entry = encodeURIComponent(this.options.paramName) + '=' +
                encodeURIComponent(this.getToken());

        this.options.parameters = this.options.callback ?
                                  this.options.callback(this.element, entry) : entry;

        if (this.options.defaultParams)
            this.options.parameters += '&' + this.options.defaultParams;

        new Ajax.Request(this.url, this.options);
    },

    onComplete: function(request) {
        this.updateChoices(request.responseText);
    }
});


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
// Original license and copyright:
// Copyright (c) 2005 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
//           (c) 2005 Ivan Krstic (http://blogs.law.harvard.edu/ivan)
//           (c) 2005 Jon Tirsen (http://www.tirsen.com)
// Contributors:
//  Richard Livsey
//  Rahul Bhargava
//  Rob Wills
//
// See scriptaculous.js for full license.

Ice.Autocompleter = Class.create();


Object.extend(Object.extend(Ice.Autocompleter.prototype, Autocompleter.Base.prototype), {
    initialize: function(id, updateId, options, rowClass, selectedRowClass) {
        Ice.Autocompleter.logger.debug("Building Ice Autocompleter ID [" + id + "]");
        var existing = Autocompleter.Finder.list[id];
        if (existing && !existing.monitor.changeDetected()) {
            return;
        }

        if (options)
            options.minChars = 0;
        else
            options = {minChars:0};
        var element = $(id);
        var ue = $(updateId);
        this.baseInitialize(element, ue, options, rowClass, selectedRowClass);

        this.options.onComplete = this.onComplete.bind(this);
        this.options.defaultParams = this.options.parameters || null;
        this.monitor = new Ice.AutocompleterMonitor(element, ue, options, rowClass, selectedRowClass);
        this.monitor.object = this;
        Ice.StateMon.add(this.monitor);
        Autocompleter.Finder.add(this.element, this);
        Ice.Autocompleter.logger.debug("Done building Ice Autocompleter");
        if (this.monitor.changeDetected()) {
            Ice.Autocompleter.logger.debug("Change has been detected");
        }
    },

    getUpdatedChoices: function(isEnterKey, event, idx) {
        if (!event) {
            event = new Object();
        }
        entry = encodeURIComponent(this.options.paramName) + '=' +
                encodeURIComponent(this.getToken());

        this.options.parameters = this.options.callback ?
                                  this.options.callback(this.element, entry) : entry;

        if (this.options.defaultParams)
            this.options.parameters += '&' + this.options.defaultParams;

        var form = Ice.util.findForm(this.element);
        if (idx > -1) {
            var indexName = this.element.id + "_idx";
            form[indexName].value = idx;
        }

        //     form.focus_hidden_field.value=this.element.id;
        if (isEnterKey) {
            Ice.Autocompleter.logger.debug("Sending submit");
            iceSubmit(form, this.element, event);
        }
        else {
            Ice.Autocompleter.logger.debug("Sending partial submit");
            iceSubmitPartial(form, this.element, event);
        }
        
           var indexName = this.element.id + "_idx";
           form[indexName].value = "";
    },

    onComplete: function(request) {
        this.updateChoices(request.responseText);
    },

    updateNOW: function(text) {


        if (this.hidden) {
            this.hidden = false;
            //Ice.Autocompleter.logger.debug("Not showing due to hide force");
            return;
        }
        this.hasFocus = true;
        Element.cleanWhitespace(this.update);
        this.updateChoices(text);
        this.show();
        this.render();
    }
});
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
// Original copyright and license
// Copyright (c) 2005 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
// Contributors:
//  Justin Palmer (http://encytemedia.com/)
//  Mark Pilgrim (http://diveintomark.org/)
//  Martin Bialasinki
// 
// See scriptaculous.js for full license.  

Effect.Highlight.prototype.ORIGINAL_setup = Effect.Highlight.prototype.setup;
Effect.Highlight.prototype.setup = function() {
    if (this.element.highlighting) {
        this.cancel();
        return;
    }
    this.ORIGINAL_setup();
    this.element.highlighting = true;
}

Effect.Highlight.prototype.ORIGINAL_finish = Effect.Highlight.prototype.finish;
Effect.Highlight.prototype.finish = function() {
    this.ORIGINAL_finish();
    this.element.highlighting = false;
}


Object.extend(Effect.DefaultOptions, {afterFinish:function(ele) {
    if (this.uploadCSS != null) {
        Ice.DnD.StyleReader.upload(ele.element, ele.options.submit);
    }
    if (ele.options.iceFinish)
        ele.options.iceFinish(ele);
}});


function blankEffect() {
}// Blank Effect, used as a place holder in local effects

Effect.Grow = function(element) {
    element = $(element);
    var options = Object.extend({
        direction: 'center',
        moveTransition: Effect.Transitions.sinoidal,
        scaleTransition: Effect.Transitions.sinoidal,
        opacityTransition: Effect.Transitions.full
    }, arguments[1] || {});
    var oldStyle = {
        top: element.style.top,
        left: element.style.left,
        height: element.style.height,
        width: element.style.width,
        opacity: element.getInlineOpacity() };

    var dims = element.getDimensions();
    var initialMoveX, initialMoveY;
    var moveX, moveY;

    switch (options.direction) {
        case 'top-left':
            initialMoveX = initialMoveY = moveX = moveY = 0;
            break;
        case 'top-right':
            initialMoveX = dims.width;
            initialMoveY = moveY = 0;
            moveX = -dims.width;
            break;
        case 'bottom-left':
            initialMoveX = moveX = 0;
            initialMoveY = dims.height;
            moveY = -dims.height;
            break;
        case 'bottom-right':
            initialMoveX = dims.width;
            initialMoveY = dims.height;
            moveX = -dims.width;
            moveY = -dims.height;
            break;
        case 'center':
            initialMoveX = dims.width / 2;
            initialMoveY = dims.height / 2;
            moveX = -dims.width / 2;
            moveY = -dims.height / 2;
            break;
    }

    return new Effect.Move(element, {
        x: initialMoveX,
        y: initialMoveY,
        duration: 0.01,
        beforeSetup: function(effect) {
            effect.element.hide().makeClipping().makePositioned();
        },
        afterFinishInternal: function(effect) {
            new Effect.Parallel(
                    [ new Effect.Opacity(effect.element, { sync: true, to: 1.0, from: 0.0, transition: options.opacityTransition }),
                        new Effect.Move(effect.element, { x: moveX, y: moveY, sync: false, transition: options.moveTransition }),
                        new Effect.Scale(effect.element, 100, {
                            scaleMode: { originalHeight: dims.height, originalWidth: dims.width },
                            sync: false, scaleFrom: window.opera ? 1 : 0, transition: options.scaleTransition, restoreAfterFinish: true})
                    ], Object.extend({
                beforeSetup: function(effect) {
                    effect.effects[0].element.setStyle({height: '10px'}, {width: '10px'}).show();
                },
                afterFinishInternal: function(effect) {
                    effect.effects[0].element.undoClipping().undoPositioned().setStyle(oldStyle);
                }
            }, options))
        }
    });
}
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

try {
    Ice.DnD.init();
    Ice.Autocompleter.logger = logger.child('autocomplete');
    Ice.StateMon.checkAll();
    Ice.StateMon.rebuild();
} catch(ee) {
    alert('Error in extras bootstrap [' + ee + ']');
}

window.onUnload(function() {
    try {
        Ice.StateMon.destroyAll();
        Autocompleter.Finder.list = new Array();
    } catch(ee) {
        Ice.DnD.logger.debug('Unload Error [' + ee + ']');
    }
});

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
var GMapRepository = new Array();

var GMapWrapper = Class.create();
GMapWrapper.prototype = {
    initialize: function(eleId, realGMap) {
        this.eleId = eleId;
        this.realGMap = realGMap;
        this.controls = new Object();
        this.overlays = new Object();
        this.geoMarker = new Object();
        this.geoMarkerAddress;
        this.geoMarkerSet = false;
    },

    getElementId: function() {
        return this.eleId;
    },

    getRealGMap: function() {
        return this.realGMap;
    },

    getControlsArray: function() {
        return this.controls;
    }
};

Ice.GoogleMap = Class.create();
Ice.GoogleMap = {
    getGeocoder: function(id) {
        var geocoder = GMapRepository[id + 'geo'];
        if (geocoder == null) {
            GMapRepository[id + 'geo'] = new GClientGeocoder();
            return GMapRepository[id + 'geo'];
        } else {
            return geocoder;
        }
    },

    getGDirection: function(id, text_div) {
        var gdirection = GMapRepository[id + 'dir'];
        if (gdirection == null) {
            var map = Ice.GoogleMap.getGMapWrapper(id).getRealGMap();
            var directionsPanel = document.getElementById(text_div);
            GMapRepository[id + 'dir'] = new GDirections(map, directionsPanel);
            return GMapRepository[id + 'dir'];
        } else {
            return gdirection;
        }
    },

    getGMapWrapper:function (id) {
        var gmapWrapper = GMapRepository[id];
        if (gmapWrapper) {
            var gmapComp = document.getElementById(id);
            //the googlemap view must be unrendered, however
            //javascript object still exist, so recreate the googlemap
            //with its old state.
            if (!gmapComp.hasChildNodes()) {
                gmapWrapper = Ice.GoogleMap.recreate(id, gmapWrapper);
            }
        } else {
            //googleMap not found create a fresh new googleMap object
            gmapWrapper = Ice.GoogleMap.create(id);
        }
        return gmapWrapper;
    },

    loadDirection:function(id, text_div, query) {
        var direction = GMapRepository[id + 'dir'];
        var map = Ice.GoogleMap.getGMapWrapper(id).getRealGMap();
        if (direction == null) {
            var directionsPanel = document.getElementById(text_div);
            direction = new GDirections(map, directionsPanel);
            GMapRepository[id + 'dir'] = direction;
        }
        direction.load(query);
    },

    addOverlay:function (ele, overlayId, ovrLay) {
        var gmapWrapper = Ice.GoogleMap.getGMapWrapper(ele);
        var overlay = gmapWrapper.overlays[overlayId];
        if (overlay == null) {
            overlay = eval(ovrLay);
            gmapWrapper.getRealGMap().addOverlay(overlay);
            gmapWrapper.overlays[overlayId] = overlay;
        }
    },

    removeOverlay:function(ele, overlayId) {
        var gmapWrapper = Ice.GoogleMap.getGMapWrapper(ele);
        var overlay = gmapWrapper.overlays[overlayId];
        if (overlay != null) {
            gmapWrapper.getRealGMap().removeOverlay(overlay);
        } else {
            //nothing found just return
            return;
        }
        var newOvrLyArray = new Object();
        for (overlayObj in gmapWrapper.overlays) {
            if (overlayId != overlayObj) {
                newOvrLyArray[overlayObj] = gmapWrapper.overlays[overlayObj];
            }
        }
        gmapWrapper.overlays = newOvrLyArray;
    },

    locateAddress: function(clientId, address) {
        var gLatLng = function(point) {
            if (!point) {
                alert(address + ' not found');
            } else {
                var gmapWrapper = Ice.GoogleMap.getGMapWrapper(clientId);
                if (gmapWrapper) {
                    gmapWrapper.getRealGMap().setCenter(point, 13);
                    var marker = new GMarker(point);
                    gmapWrapper.getRealGMap().addOverlay(marker);
                    marker.openInfoWindowHtml(address);
                    gmapWrapper.geoMarker = marker;
                    gmapWrapper.geoMarkerAddress = address;
                    Ice.GoogleMap.submitEvent(clientId, gmapWrapper.getRealGMap(), "geocoder");
                } else {
                    //FOR IS DEFINED BUT MAP IS NOT FOUND,
                    //LOGGING CAN BE DONE HERE
                }
            } //outer if
        }; //function ends here

        var geocoder = Ice.GoogleMap.getGeocoder(clientId);
        geocoder.getLatLng(address, gLatLng);
    },

    create:function (ele) {
        var gmapWrapper = new GMapWrapper(ele, new GMap2(document.getElementById(ele)));
        var hiddenField = document.getElementById(ele + 'hdn');
        var mapTypedRegistered = false;

        GEvent.addListener(gmapWrapper.getRealGMap(), "zoomend", function(oldLevel, newLevel) {
            if (oldLevel != null)
                Ice.GoogleMap.submitEvent(ele, gmapWrapper.getRealGMap(), "zoomend", newLevel);
        });

        GEvent.addListener(gmapWrapper.getRealGMap(), "dragend", function() {
            Ice.GoogleMap.submitEvent(ele, gmapWrapper.getRealGMap(), "dragend");
        });

        GEvent.addListener(gmapWrapper.getRealGMap(), "maptypechanged", function() {
            if (mapTypedRegistered) {
                var type = $(ele + 'type');
                type.value = gmapWrapper.getRealGMap().getCurrentMapType().getName();
                Ice.GoogleMap.submitEvent(ele, gmapWrapper.getRealGMap(), "maptypechanged");
            }
            mapTypedRegistered = true;
        });
        initializing = false;
        GMapRepository[ele] = gmapWrapper;
        return gmapWrapper;
    },

    submitEvent: function(ele, map, eventName, zoomLevel) {
        try {
            var center = map.getCenter();
            var lat = $(ele + 'lat');
            var lng = $(ele + 'lng');
            var event = $(ele + 'event');
            var zoom = $(ele + 'zoom');
            var type = $(ele + 'type');
            lat.value = center.lat();
            lng.value = center.lng();
            event.value = eventName;
            if (zoomLevel == null) {
                zoom.value = map.getZoom();
            } else {
                zoom.value = zoomLevel;
                if (zoom.value == map.getZoom()) {
                    return;
                }
            }
            var form = Ice.util.findForm(lat);
            var nothingEvent = new Object();
            iceSubmitPartial(form, lat, nothingEvent);
            //reset event value, so the decode method of gmap can
            //make deceison before decode
            event.value = "";
        } catch(e) {
        }
    },

    recreate:function(ele, gmapWrapper) {
        Ice.GoogleMap.remove(ele);
        var controls = gmapWrapper.controls;
        var geoMarker = gmapWrapper.geoMarker;
        var geoMarkerAddress = gmapWrapper.geoMarkerAddress;
        gmapWrapper = Ice.GoogleMap.create(ele);
        gmapWrapper.geoMarker = geoMarker;
        gmapWrapper.geoMarkerAddress = geoMarkerAddress;
        gmapWrapper.geoMarkerSet = 'true';
        var tempObject = new Object();
        for (control in controls) {
            if (tempObject[control] == null) {
                Ice.GoogleMap.removeControl(ele, control);
                Ice.GoogleMap.addControl(ele, control)
            }
        }
        return gmapWrapper;
    },

    addControl:function(ele, controlName) {
        var gmapWrapper = Ice.GoogleMap.getGMapWrapper(ele);
        var control = gmapWrapper.controls[controlName];
        if (control == null) {
            control = eval('new ' + controlName + '()');
            gmapWrapper.getRealGMap().addControl(control);
            gmapWrapper.controls[controlName] = control;
        }
    },

    removeControl:function(ele, controlName) {
        var gmapWrapper = Ice.GoogleMap.getGMapWrapper(ele);
        var control = gmapWrapper.controls[controlName];
        if (control != null) {
            gmapWrapper.getRealGMap().removeControl(control);
        }
        var newCtrlArray = new Object();
        for (control in gmapWrapper.controls) {
            if (controlName != control) {
                newCtrlArray[control] = gmapWrapper.controls[control];
            }
        }
        gmapWrapper.controls = newCtrlArray;
    },

    remove:function(ele) {
        var newRepository = new Array();
        for (map in GMapRepository) {
            if (map != ele) {
                newRepository[map] = GMapRepository[map];
            }
        }
        GMapRepository = newRepository;
    },

    setMapType:function(ele, type) {
        var gmapWrapper = Ice.GoogleMap.getGMapWrapper(ele);
        //if the chart is recreated, so add any geoCoderMarker that was exist before.
        if (gmapWrapper.geoMarkerSet
                && gmapWrapper.geoMarker != null
                && gmapWrapper.geoMarkerAddress != null
                )
        {
            gmapWrapper.getRealGMap().addOverlay(gmapWrapper.geoMarker);
            gmapWrapper.geoMarker.openInfoWindowHtml(gmapWrapper.geoMarkerAddress);
            gmapWrapper.geoMarkerSet = false;
        }
        if (gmapWrapper.getRealGMap().getCurrentMapType() != null) {
            //set the map type only when difference found
            if (gmapWrapper.getRealGMap().getCurrentMapType().getName() != type) {
                switch (type)
                        {
                    case "Satellite":
                        gmapWrapper.getRealGMap().setMapType(G_SATELLITE_MAP);
                        break
                    case "Hybrid":
                        gmapWrapper.getRealGMap().setMapType(G_HYBRID_MAP);
                        break
                    case "Map":
                        gmapWrapper.getRealGMap().setMapType(G_NORMAL_MAP);
                        break
                }//switch
            }//inner if
        }//outer if        
    }//setMapType    
}

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
var JSObjects = new Array();
Ice.Repository = Class.create();
Ice.Repository = {
    obj:null,

    getInstance:function(_id) {
        if (JSObjects[_id] == null) {
            JSObjects[_id] = eval(this.obj);
        }
        return JSObjects[_id];
    },

    register:function(_id, obj) {
        this.obj = obj;
        if (JSObjects[_id] == null) {
            JSObjects[_id] = this.obj;
        }
        return this.obj;
    },

    remove:function(_id) {
        var removeArray = new Array();
        for (key in JSObjects) {
            if (key == _id) {
                //	JSObjects["iceIndex"] = parseInt(JSObjects["iceIndex"]) - 1;
                continue;
            }
            removeArray[key] = JSObjects[key];
        }
        JSObjects = removeArray;
    },

    getAll:function() {
        var tempArray = new Array();
        var i = 0;
        for (key in JSObjects) {
            if (key.indexOf(':') > 0) {
                tempArray[i++] = JSObjects[key];
            }
        }
        return tempArray;
    }
}

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
var visibleTooltipList = new Array();

ToolTipPanelPopup = Class.create({
  initialize: function(srcComp, tooltipCompId, event, hideOn, delay, dynamic, formId, ctxValue, iFrameUrl, displayOn, moveWithMouse) {
    this.src = srcComp;
    this.delay = delay || 500;
    this.dynamic = (dynamic == "true");
    this.tooltipCompId = tooltipCompId;
    this.srcCompId = srcComp.id;
    this.hideOn = hideOn;
    this.x = Event.pointerX(event);
    this.y = Event.pointerY(event);
    this.formId = formId;
    this.ctxValue = ctxValue
    this.iFrameUrl = iFrameUrl;
    this.moveWithMouse = moveWithMouse;
    //cancel bubbling
    event.cancelBubble = true;
    //attach events

    if (this.hideOn == "mousedown") {
        this.hideEvent = this.hidePopupOnMouseClick.bindAsEventListener(this);
    } else if (this.hideOn == "mouseout") {
        this.hideEvent = this.hidePopupOnMouseOut.bindAsEventListener(this);
    } else {
        this.hideOn = "none";
    }

    this.eventMouseMove = this.updateCordinate.bindAsEventListener(this);
    this.clearTimerEvent = this.clearTimer.bindAsEventListener(this);
    Event.observe(document, "mouseout" , this.clearTimerEvent);
    Event.observe(document, this.hideOn , this.hideEvent);
    Event.observe(document, "mousemove", this.eventMouseMove);
      if (displayOn == "hover") {
          this.timer = setTimeout(this.showPopup.bind(this), parseInt(this.delay));
      } else {
          this.showPopup.bind(this)();
          Event.extend(event).stop();
      }
  },

  showPopup: function() {
    if (this.isTooltipVisible()) return;
    if (this.dynamic) {
         //its a dynamic tooltip, so remove all its childres
        var tooltip = this.getTooltip();
        if(tooltip) {
	        tooltip.style.visibility = "hidden";        
	        var table = tooltip.childNodes[0];
	        if (table) {
	            tooltip.removeChild(table);
	        }
        }
    //dynamic? set status=show, populatefields, and submit
      this.submit("show");
      if (this.hideOn == "none") {
        //reset the info
        this.populateFields(true);
      }
    } else {
        //static? just set the visibility= true 
       var tooltip = this.getTooltip();
        tooltip.style.visibility = "visible";
        tooltip.style.position = "absolute" ;
        tooltip.style.display = "";
        tooltip.style.top = this.y - tooltip.offsetHeight - 4 + "px";
        tooltip.style.left = this.x+4+"px";
        ToolTipPanelPopupUtil.adjustPosition(tooltip);
        Ice.iFrameFix.start(this.tooltipCompId, this.iFrameUrl);
    }
    this.addToVisibleList();    
  },

  hidePopupOnMouseOut: function(event) {
    if (!this.isTooltipVisible()) return;
    if (Position.within($(this.tooltipCompId), Event.pointerX(event), Event.pointerY(event))) return; //ICE-3521
    this.hidePopup(event);
    this.state = "hide";
    this.populateFields();
    if (this.hideOn == "mouseout") {
        this.removedFromVisibleList();
    }
    this.dispose(event);
  },

  hidePopupOnMouseClick: function(event) {
    if (!this.isTooltipVisible() || !Event.isLeftClick(event)) return;
    var eventSrc = Event.element(event);
    if(this.srcOrchildOfSrcElement(eventSrc)) {
        return;
    } else {
        this.hidePopup(event);
    }
    if (this.hideOn == "mousedown") {
        this.removedFromVisibleList();
    }
    this.dispose(event);
  },


 dispose: function(event) {
    Event.stopObserving(document, this.hideOn, this.hideEvent);
    Event.stopObserving(document, "mousemove", this.eventMouseMove);

   },

  hidePopup:function(event) {
    if(this.dynamic) {
    //dynamic? set status=hide, populatefiels and submit 
        this.submit("hide");
    } else {
        //static? set visibility = false;
        tooltip =  this.getTooltip();
        tooltip.style.visibility = "hidden";
        tooltip.style.display = "none";
    }
  },
  
  
  submit:function(state, event) {
      if (!event) event = new Object();
      this.state = state;
      this.populateFields();
      var element = $(this.srcCompId);
      try {
        var form = Ice.util.findForm(element);
        iceSubmitPartial(form,element,event);
      } catch (e) {logger.info("Form not found" + e);}
  },
  
  clearTimer:function() {
     //   $(action).innerHTML += "<br/> Clearing the event";
        Event.stopObserving(document, "mouseout", this.clearTimerEvent);
        clearTimeout(this.timer);

  },

  updateCordinate: function(event) {
    if (Event.element(event) != this.src && !event.element().descendantOf(this.src)) return;
    this.x = Event.pointerX(event);
    this.y = Event.pointerY(event);
      if (!this.isTooltipVisible() || !this.moveWithMouse) return;
      var tooltip = this.getTooltip();
      tooltip.style.top = this.y - tooltip.offsetHeight - 4 + "px";
      tooltip.style.left = this.x + 4 + "px";
      ToolTipPanelPopupUtil.adjustPosition(tooltip);
      Ice.iFrameFix.start(this.tooltipCompId, this.iFrameUrl);
  },

  srcOrchildOfSrcElement: function(ele) {
     var tooltip =  this.getTooltip();
     if (tooltip  == ele) return true;
     while (ele.parentNode) {
        ele = ele.parentNode;
        if (tooltip  == ele){
            return true;
        }
     }
  },

  getTooltip: function () {
      return $(this.tooltipCompId);
  },
  
  populateFields: function(reset) {
  // the following field should be rendered by the panelPoupRenderer if rendered as tooltip


	    var form = $(this.formId);
	    if (form == null) return;
	    var iceTooltipInfo = form.getElements().find( function(element) {
	        if (element.id == "iceTooltipInfo") return element;
	    });
	    if (!iceTooltipInfo) { 
		    iceTooltipInfo = document.createElement('input');
		    iceTooltipInfo.id="iceTooltipInfo";
		    iceTooltipInfo.name="iceTooltipInfo";            
		    iceTooltipInfo.type="hidden";
	        form.appendChild(iceTooltipInfo);
	    }  else {
	 
	    }
	    if (reset) {
	       iceTooltipInfo.value = "";
	    } else {
	       iceTooltipInfo.value = "tooltip_id=" + this.tooltipCompId + 
	                     "; tooltip_src_id="+ this.src.id+ 
	                     "; tooltip_state="+ this.state +
	                     "; tooltip_x="+ this.x +
	                     "; tooltip_y="+ this.y +
	                     "; cntxValue="+ this.ctxValue;
	    }
    },
    
    addToVisibleList: function() {
        if (!this.isTooltipVisible()) {
            this.removedFromVisibleList('all');
            visibleTooltipList[parseInt(visibleTooltipList.length)] = {tooltipId: this.tooltipCompId, srcCompId: this.srcCompId};
        } else {
        }
    },
    
    removedFromVisibleList: function(all) {
        if (this.isTooltipVisible() || all) {
	        var newList = new Array();
		    var index = -1;
		    for (i=0; i < visibleTooltipList.length; i++) {
		        if (visibleTooltipList[i].tooltipId != this.tooltipCompId) {
		            index = parseInt(index)+ 1;
		            newList[index] = visibleTooltipList[i];
		        }else {
		        }
		    }
		    visibleTooltipList = newList;
		} else {
		}
    },
    
    isTooltipVisible: function(onlyTooltip) {
        for (i=0; i < visibleTooltipList.length; i++) {
            if (onlyTooltip) {
                if (visibleTooltipList[i].tooltipId== this.tooltipCompId) {
                    return true;
                }             
            } else {
                if (visibleTooltipList[i].tooltipId== this.tooltipCompId && visibleTooltipList[i].srcCompId == this.srcCompId) {
                    return true;
                } 
            }
  
        }
        return false;
    }
});

ToolTipPanelPopupUtil = {
    removeFromVisibleList:function(comp_id) {
        var newList = new Array();
        var index = -1;
        for (i=0; i < visibleTooltipList.length; i++) {
            if (visibleTooltipList[i].tooltipId != comp_id) {
                index = parseInt(index)+ 1;
                newList[index] = visibleTooltipList[i];
            }else {
            }
        }
        visibleTooltipList = newList;
    },
    adjustPosition: function(id) {
        var element = $(id);
        var viewportDimensions = document.viewport.getDimensions();
        var viewportScrollOffsets = document.viewport.getScrollOffsets();
        var elementDimensions = element.getDimensions();
        var elementOffsets = element.cumulativeOffset();
        var positionedOffset = element.positionedOffset();

        var diff = 0;
        if (elementOffsets.left < viewportScrollOffsets.left) {
            diff = viewportScrollOffsets.left - elementOffsets.left;
        } else if (elementOffsets.left + elementDimensions.width > viewportScrollOffsets.left + viewportDimensions.width) {
            diff = (elementOffsets.left + elementDimensions.width) - (viewportScrollOffsets.left + viewportDimensions.width);
            diff = - Math.min(diff, (elementOffsets.left - viewportScrollOffsets.left));
        }
        element.style.left = positionedOffset.left + diff + "px";
        
        diff = 0;
        if (elementOffsets.top < viewportScrollOffsets.top) {
            diff = viewportScrollOffsets.top - elementOffsets.top;
        } else if (elementOffsets.top + elementDimensions.height > viewportScrollOffsets.top + viewportDimensions.height) {
            diff = (elementOffsets.top + elementDimensions.height) - (viewportScrollOffsets.top + viewportDimensions.height);
            diff = - Math.min(diff, (elementOffsets.top - viewportScrollOffsets.top));
        }
        element.style.top = positionedOffset.top + diff + "px";
    },
    showPopup: function(id) {
        var tooltip = $(id);
        tooltip.style.top = parseInt(tooltip.style.top) - tooltip.offsetHeight - 4 + "px";
        tooltip.style.left = parseInt(tooltip.style.left) + 4 + "px";
        ToolTipPanelPopupUtil.adjustPosition(id);
    }
};


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
Ice.Resizable = Class.create({
    initialize: function(event, horizontal) {

        //resize handler
        this.source = Event.element(event);
        this.horizontal = horizontal;

        //initial pointer location
        if (this.horizontal) {
            this.pointerLocation = parseInt(Event.pointerY(event));
        } else {
            this.pointerLocation = parseInt(Event.pointerX(event));
        }

        this.eventMouseMove = this.resize.bindAsEventListener(this);
        this.eventMouseUp = this.detachEvent.bindAsEventListener(this);
        Event.observe(document, "mousemove", this.eventMouseMove);
        Event.observe(document, "mouseup", this.eventMouseUp);
        this.origionalHeight = this.source.style.height;
        this.disableTextSelection();
        this.getGhost().style.position = "absolute";
        //    this.getGhost().style.backgroundColor = "green";
        //    this.getGhost().style.border= "1px dashed";

        this.deadPoint = 20;
    },

    print: function(msg) {
        logger.info(msg);
    },

    getPreviousElement: function() {
    },

    getContainerElement: function() {
    },

    getNextElement: function() {
    },

    getGhost:function() {
        return this.source;
    },

    finalize: function (event) {
        this.source.style.position = "";
        this.source.style.left = Event.pointerX(event) + "px";
        //   this.source.style.backgroundColor = "#EFEFEF";
        //   this.source.style.border = "none";
    },

    resize: function(event) {
        this.getGhost().style.visibility = "";
        if (this.deadEnd(event)) return;
        //   this.getGhost().style.backgroundColor = "green";
        if (this.horizontal) {
            this.getGhost().style.cursor = "n-resize";
            var top = Event.pointerY(event) - this.getGhost().getOffsetParent().cumulativeOffset().top;
            this.getGhost().style.top = top + "px";
        } else {
            this.getGhost().style.cursor = "e-resize";
            var left = Event.pointerX(event) - this.getGhost().getOffsetParent().cumulativeOffset().left;
            this.getGhost().style.left = left + "px";
        }
    },


    detachEvent: function(event) {
        //restore height
        this.source.style.height = this.origionalHeight;
        if (this.getDifference(event) > 0 && !this.deadEnd(event)) {
            this.adjustPosition(event);
        }

        Event.stopObserving(document, "mousemove", this.eventMouseMove);
        Event.stopObserving(document, "mouseup", this.eventMouseUp);
        this.enableTextSelection();
        this.finalize(event);
    },

    adjustPosition:function(event) {
        var leftElementWidth = Element.getWidth(this.getPreviousElement());
        var rightElementWidth = Element.getWidth(this.getNextElement());
        var tableWidth = Element.getWidth(this.getContainerElement());
        var diff = this.getDifference(event);

        if (this.resizeAction == "inc") {
            this.getPreviousElement().style.width = (leftElementWidth + diff) + "px";
            this.getNextElement().style.width = (rightElementWidth - diff) + "px"

            //    this.getContainerElement().style.width = tableWidth + diff + "px";;

            this.print("Diff " + diff);
            this.print("Td width " + leftElementWidth + this.getPreviousElement().id);
            this.print("Table width " + tableWidth);


        } else {
            this.getPreviousElement().style.width = (leftElementWidth - diff) + "px";
            this.getNextElement().style.width = (rightElementWidth + diff) + "px"

            //      this.getContainerElement().style.width = tableWidth - diff + "px";
        }
    },

    getDifference: function(event) {
        var x;
        if (this.horizontal) {
            x = parseInt(Event.pointerY(event));
        } else {
            x = parseInt(Event.pointerX(event));
        }
        if (this.pointerLocation > x) {
            this.resizeAction = "dec";
            return this.pointerLocation - x;
        } else {
            this.resizeAction = "inc";
            return x - this.pointerLocation;
        }
    },

    deadEnd: function(event) {
        var diff = this.getDifference(event);
        if (this.resizeAction == "dec") {
            var leftElementWidth;
            if (this.horizontal) {
                leftElementWidth = Element.getHeight(this.getPreviousElement());
            } else {
                leftElementWidth = Element.getWidth(this.getPreviousElement());
            }

            if ((leftElementWidth - diff) < this.deadPoint) {
                // this.getGhost().style.backgroundColor = "red";
                return true;
            }
        } else {
            var rightElementWidth;
            if (this.horizontal) {
                rightElementWidth = Element.getHeight(this.getNextElement());
            } else {
                rightElementWidth = Element.getWidth(this.getNextElement());
            }

            if ((rightElementWidth - diff) < this.deadPoint) {
                //    this.getGhost().style.backgroundColor = "red";
                return true;
            }
        }
        return false;
    },

    disableTextSelection:function() {
        this.getContainerElement().onselectstart = function () {
            return false;
        }
        this.source.style.unselectable = "on";
        this.source.style.MozUserSelect = "none";
        this.source.style.KhtmlUserSelect = "none";
    },

    enableTextSelection:function() {
        this.getContainerElement().onselectstart = function () {
            return true;
        }
        this.source.style.unselectable = "";
        this.source.style.MozUserSelect = "";
        this.source.style.KhtmlUserSelect = "";
    }
});

Ice.ResizableGrid = Class.create(Ice.Resizable, {
    initialize: function($super, event) {
        $super(event);
        logger.info(">>>>>>>>>>>>>>>>>>> ");
        this.cntHght = (Element.getHeight(this.getContainerElement())) + "px";
        this.source.style.height = this.cntHght;
        this.getGhost().style.left = Event.pointerX(event) + "px";
        this.source.style.backgroundColor = "#CCCCCC";
    }
});

Ice.ResizableGrid.addMethods({
    getDifference: function($super, event) {
        return $super(event);
    },

    getContainerElement: function() {
        return this.source.parentNode.parentNode.parentNode.parentNode;
    },

    getPreviousElement: function() {
        if (this.source.parentNode.previousSibling.tagName == "TH") {
            return this.source.parentNode.previousSibling.firstChild;
        } else {
            return this.source.parentNode.previousSibling.previousSibling.firstChild;
        }
    },

    getNextElement: function() {
        if (this.source.parentNode.nextSibling.tagName == "TH") {
            return this.source.parentNode.nextSibling.firstChild;
        } else {
            return this.source.parentNode.nextSibling.nextSibling.firstChild;
        }
    },

    resize: function($super, event) {
        this.source.style.height = this.cntHght;
        this.getGhost().style.height = this.cntHght;
        $super(event);
        this.source.style.height = this.cntHght;
        this.getGhost().style.height = this.cntHght;
    },

    finalize: function ($super, event) {
        $super(event);
        this.source.style.height = "1px";
        this.source.style.backgroundColor = "transparent";
        this.getGhost().style.height = "1px";
        var clientOnly = $(this.getContainerElement().id + "clientOnly");
        if (clientOnly) {
            clientOnly.value = this.getAllColumnsWidth();
            var form = Ice.util.findForm(clientOnly);
            iceSubmitPartial(form, clientOnly, event);
        }
    },

    getAllColumnsWidth:function() {
        var container = this.getContainerElement();
        var children = container.firstChild.firstChild.childNodes;
        var widths = "";
        for (i = 0; i < children.length; i++) {
        	if (children[i].className == 'iceDatTblResBor') {
        		continue;
        	}
            widths += Element.getStyle(children[i].firstChild, "width") + ",";
        }
        return widths;
    }


});

Ice.PanelDivider = Class.create(Ice.Resizable, {
    initialize: function($super, event, horizontal) {
        $super(event, horizontal);
        this.deadPoint = 20;
        if (this.horizontal) {
            var spliterHeight = Element.getHeight(this.source);
            var mouseTop = Event.pointerY(event);
            this.getGhost().style.top = (mouseTop - (spliterHeight )) + "px";
            this.getGhost().style.width = (Element.getWidth(this.getContainerElement())) + "px";
        } else {
            var spliterWidth = Element.getWidth(this.source);
            var borderLeft = parseInt(Element.getStyle(this.source, 'border-left-width'));
            var borderRight = parseInt(Element.getStyle(this.source, 'border-right-width'));
            if (borderLeft && borderLeft >= 1) {
                spliterWidth -= borderLeft;
            }
            if (borderRight && borderRight >= 1) {
                spliterWidth -= borderRight;
            }
            var mouseLeft = Event.pointerX(event);
            this.getGhost().style.left = (mouseLeft - (spliterWidth )) + "px";
            this.getGhost().style.width = spliterWidth + "px";
            this.getGhost().style.height = (Element.getHeight(this.getContainerElement())) + "px";
        }
    }
});

Ice.PanelDivider.addMethods({
    getDifference: function($super, event) {
        return $super(event);
    },

    getContainerElement: function() {
        return this.source.parentNode.parentNode;
    },


    getPreviousElement: function() {
        if (this.source.previousSibling.tagName == "DIV") {
            return this.source.previousSibling;
        } else {
            return this.source.previousSibling.previousSibling;
        }
    },

    getNextElement: function() {
        if (this.source.nextSibling.tagName == "DIV") {
            return this.source.nextSibling;
        } else {
            return this.source.nextSibling.nextSibling;
        }
    },

    getGhost: function() {
        if (!this.ghost) {
            this.ghost = this.source.cloneNode(true);
            this.ghost.id = this.source.id + ":ghost";
            this.ghost.onmousedown = null;
            this.source.parentNode.appendChild(this.ghost);
            this.ghost.style.width = Element.getWidth(this.source) + "px";
            this.getGhost().style.visibility = "hidden";
        }
        this.ghost.setStyle({width:this.source.getStyle("width")});
        return this.ghost;
    },

    finalize: function (event) {
        Element.remove(this.ghost);
    },

    adjustPosition:function(event) {
        logger.info("<<<<<<<<<<<<<<<<<<<<< ADJUST POSTITITITITITI >>>>>>>>>>>>>>>>");
        var savedVisibility = this.getNextElement().style.visibility;
        this.getNextElement().style.visibility = "hidden";
        if (this.horizontal) {
            var leftElementHeight = (Element.getHeight(this.getPreviousElement()));
            var rightElementHeight = (Element.getHeight(this.getNextElement()));

            var tableHeight = Element.getHeight(this.getContainerElement());
            var totalHeight = (parseInt(leftElementHeight) + parseInt(rightElementHeight));
            var diff = this.getDifference(event);
            var inPercent;
            if (this.resizeAction == "inc") {
                inPercent = (leftElementHeight + diff) / tableHeight;
                topInPercent = Math.round(inPercent * 100);
                bottomInPercent = 99 - topInPercent;
                this.getPreviousElement().style.height = (topInPercent) + "%";
                //            this.getNextElement().style.height = bottomInPercent + "%"

            } else {
                inPercent = (leftElementHeight - diff) / tableHeight;
                topInPercent = Math.round(inPercent * 100);
                bottomInPercent = 99 - topInPercent;
                this.getPreviousElement().style.height = (topInPercent) + "%";
                //            this.getNextElement().style.height = bottomInPercent + "%"

            }
        } else {
            var leftElementWidth = (Element.getWidth(this.getPreviousElement()));
            var rightElementWidth = (Element.getWidth(this.getNextElement()));
            var splitterWidth = (Element.getWidth(this.source));
            var tableWidth = Element.getWidth(this.getContainerElement());
            var totalWidth = (parseInt(leftElementWidth) + parseInt(rightElementWidth));
            var diff = this.getDifference(event);
            if (this.resizeAction == "inc") {
                inPercent = (leftElementWidth + diff) / tableWidth;
                leftInPercent = Math.round(inPercent * 100);
                rightInPercent = 100 - leftInPercent;
                this.getPreviousElement().style.width = leftInPercent + "%";
                //            this.getNextElement().style.width = rightInPercent + "%"


            } else {
                inPercent = (leftElementWidth - diff) / tableWidth;
                leftInPercent = Math.round(inPercent * 100);
                rightInPercent = 100 - leftInPercent;
                this.getPreviousElement().style.width = leftInPercent + "%";
                //            this.getNextElement().style.width = rightInPercent + "%"

            }
        }
        Ice.PanelDivider.adjustSecondPaneSize(this.source, this.horizontal);
        this.getNextElement().style.visibility = savedVisibility;
        inPercent = inPercent + 0.01;
        this.submitInfo(event, inPercent);
    },

    submitInfo:function(event, inPercent) {
        var form = Ice.util.findForm(this.source);
        var clientId = this.getContainerElement().id;
        var firstPaneStyleElement = $(clientId + "FirstPane");
        var secondPaneStyleElement = $(clientId + "SecondPane");
        var inPercentElement = $(clientId + "InPercent");
        firstPaneStyleElement.value = this.getPreviousElement().style.cssText;
        secondPaneStyleElement.value = this.getNextElement().style.cssText;
        inPercentElement.value = Math.round(inPercent * 100);
        iceSubmitPartial(form, this.source, event);
    }

});

Ice.PanelDivider.adjustSecondPaneSize = function(divider, isHorizontal) {
    divider = $(divider);
    //    var container = $(Ice.PanelDivider.prototype.getContainerElement.call({source:divider})); // <ice:panelDivider>
    var container = $(divider.parentNode); // dimensions could be different from <ice:panelDivider>
    var firstPane = $(Ice.PanelDivider.prototype.getPreviousElement.call({source:divider}));
    var secondPane = $(Ice.PanelDivider.prototype.getNextElement.call({source:divider}));
    // Assuming no padding in container, no margin in divider and panes, and no padding or border in 2nd pane.
    // No way to determine their pixel values. Also, there may be margin collapsing, and
    // (offsetWidth - clientWidth) may include the scrollbar width, not just the border width.
    if (isHorizontal) {
        secondPane.style.height = container.clientHeight - firstPane.offsetHeight - divider.offsetHeight + "px";
    } else {
        // Firefox often wraps right pane around even though it should fit exactly, therefore subtract 1 more pixel.
        secondPane.style.width = container.clientWidth - firstPane.offsetWidth - divider.offsetWidth - 1 + "px";
    }
}

Ice.PanelDivider.dividerHash = $H();

Ice.PanelDivider.onWindowResize = function() {
    Ice.PanelDivider.dividerHash.each(function(pair) {
        if (!$(pair.key)) {
            Ice.PanelDivider.dividerHash.unset(pair.key);
            return;
        }
        Ice.PanelDivider.adjustSecondPaneSize(pair.key, pair.value);
    });
}

Ice.PanelDivider.onLoad = function(divider, isHorizontal) {
    Event.stopObserving(window, "resize", Ice.PanelDivider.onWindowResize); // Will register multiple times if don't do this?
    Ice.PanelDivider.dividerHash.set(divider, isHorizontal); // Will replace existing, if any.
    Event.observe(window, "resize", Ice.PanelDivider.onWindowResize);
    Ice.PanelDivider.adjustSecondPaneSize(divider, isHorizontal);
    Ice.PanelDivider.adjustPercentBasedHeight(divider, isHorizontal);
}

ResizableUtil = {
    adjustHeight:function(src) {
        var height = Element.getHeight(src);
        var paddingTop = parseInt(Element.getStyle(src, 'padding-top'));
        var paddingBottom = parseInt(Element.getStyle(src, 'padding-top'));
        if (paddingTop && paddingTop > 1) {
            height -= paddingTop;
        }
        if (paddingBottom && paddingBottom > 1) {
            height -= paddingBottom;
        }
        src.firstChild.style.height = (height - 1) + 'px';
    }
}

//this function added to fix ICE-4044 (Issue when setting panelDivider to a non-fixed height )
Ice.PanelDivider.adjustPercentBasedHeight = function(divider, isHorizontal) {
    if (isHorizontal)return;
    var rootElementId = divider.replace("Divider", "");
    var rootElement = $(rootElementId);

    var rootHeight = Element.getStyle(rootElement, 'height');
    var percentBasedHeight = null;
    if (rootHeight && rootHeight.indexOf("%") > 0) {
        percentBasedHeight = rootHeight.split("%")[0];
    }
    if (percentBasedHeight) {
        parentHeight = Ice.PanelDivider.getParentHeight(rootElement);
        newVal = Math.round(parentHeight * (percentBasedHeight / 100));
        rootElement.style.height = newVal + "px";
        $(divider).style.height = newVal + "px";
    }
}

//this function recusivly check the height of the parent element, until one found
//if none found and body has reached, then return the height of the viewport
Ice.PanelDivider.getParentHeight = function(element) {
    //if ture means that height is not assigned to any parent, so now get the
    //height of the viewPort
    if (element.tagName == 'BODY') {
        var viewPortHeight = document.viewport.getHeight();
        //for opera get the window.innerHeight
        if (Prototype.Browser.WebKit && typeof window.innerHeight != 'undefined') {
            viewPortHeight = window.innerHeight;
        }       //sub 4 to avoid scrollbar
        return (viewPortHeight - 4);
    }
    var sHeight = Element.getStyle(element, 'height');
    if (sHeight.indexOf("%") > 0) {
        return Ice.PanelDivider.getParentHeight(element.parentNode);
    } else {
        sHeight = Element.getHeight(element);
        //if no height defined on the element, it returns 2 without any unit
        //so get the height of its parent
        if (sHeight == "2") {

            return Ice.PanelDivider.getParentHeight(element.parentNode);
        }
    }
    return sHeight;
}
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

Ice.KeyNavigator = Class.create({
    initialize: function(componentId) {
        this.component = $(componentId);
        this.component.onkeydown = this.keydown.bindAsEventListener(this);
    },

    keydown: function(event) {
        this.srcElement = Event.element(event);
        switch(event.keyCode) {
    
            case Event.KEY_RETURN:
                this.showMenu(event);
                break;
	    
            case Event.KEY_UP:
                this.goNorth(event);
                Event.stop(event);            
                break;

            case Event.KEY_DOWN:
                this.goSouth(event);
                Event.stop(event);            
                break;

            case Event.KEY_LEFT:
                this.goWest(event);
                Event.stop(event);            
                break;

            case Event.KEY_RIGHT:
                this.goEast(event);
                Event.stop(event);
                break;
         }
    },

    goNorth: function(event) {},

    goSouth: function(event) {},

    goWest: function(event) {},

    goEast: function(event) {}

});

	Ice.MenuBarKeyNavigator = Class.create(Ice.KeyNavigator, {
	  initialize: function($super, componentId, displayOnClick) {
	    $super(componentId);
	    this.displayOnClick = displayOnClick;
	    this.component.onclick = this.hideAll.bindAsEventListener(this);
	    document.onclick = this.hideAllDocument.bindAsEventListener(this);    
	  
	    if (Element.hasClassName(this.component, 'iceMnuBarVrt')) {
	        this.vertical = true;
	    } else {
	        this.vertical = false;
	    }
	    this.clicked = true;
	    this.configureRootItems();
	  }
	});

	Ice.MenuBarKeyNavigator.addMethods({
	  goEast: function(event) {
	    this.applyFocus('e');
	  },
	  
	  goWest: function(event) { 
	    this.applyFocus('w');
	  },

	  goSouth: function(event) {
	    this.applyFocus('s');
	  },

	  goNorth: function(event) { 
	    this.applyFocus('n');  
	  },

	  focusMenuItem: function(iclass, next, direct) {
	      var ci = this.srcElement.up(iclass);
	      if (ci) {
	          if(direct =='e') {
	             var sm = $(ci.id+'_sub');
	             this.focusAnchor(sm);
	             return;
	          }
	          
	          if(direct =='w') {
	             var owner = $(ci.id.substring(0, ci.id.length-6));
	             if (owner) {
	                this.focusAnchor(owner);
	             } else {
	                this.focusAnchor($(ci.id.substring(0, ci.id.lastIndexOf(':'))));
	             }
	             return;
	          }          
	                                   
	          var ni = null;
	          if(next) {
	             ni = ci.next(iclass);
	          } else {
	             ni = ci.previous(iclass);  
	             if (!ni && direct == 'n') {
	                this.focusAnchor($(ci.id.substring(0, ci.id.lastIndexOf(':'))));
	                return;
	             }        
	          }
	          this.focusAnchor(ni);
	      }
	  },
	  
	  focusSubMenuItem: function(item) {
	      if (item) {
	         var sm = $(item.id+'_sub');
	         this.focusAnchor(sm);
	      }
	  },
	  
	  focusAnchor: function(item) {
	      if (item) {
	          try {
	              var anch = item.down('a');
	              anch.focus();
	          } catch(e){}
	      }
	  },
	  
    applyFocus: function(direct) {
	      var p = this.srcElement.parentNode;
	      var pm = Element.hasClassName(p, this.getPopupMenuClass());      
	      var mb = Element.hasClassName(p, this.getMenuBarItemClass());
	      var mi = Element.hasClassName(p, this.getMenuItemClass());

	        if(mb){
	            switch(direct) {
	                case 's':
	                if (this.vertical) 
	                    this.focusMenuItem('.'+this.getMenuBarItemClass(), true);
	                else
	                    this.focusSubMenuItem(p);
	                break;
	                
	                case 'e':
	                if (this.vertical)
	                    this.focusSubMenuItem(p);
	                else
	                    this.focusMenuItem('.'+this.getMenuBarItemClass(), true);                    
	                break;
	                
	                case 'w':
	                    this.focusMenuItem('.'+this.getMenuBarItemClass());
	                break;
	                                    
	                case 'n':
	                    if (this.vertical)
	                        this.focusMenuItem('.'+this.getMenuBarItemClass());
	                break;
	            }
	            
	        }else if (mi) {
	            this.focusMenuItem('.'+this.getMenuItemClass(), direct == 's', direct);
	        }else if (pm) {
	            switch(direct) {
	                case 'n':
	                     this.focusMenuItem('.'+this.getPopupMenuClass());
	                break;
	                case 's':
	                     this.focusMenuItem('.'+this.getPopupMenuClass(), true);                
	                break;
	                case 'e':
	                      this.focusSubMenuItem(p);
	                break;                
	                
	             }                
	        }
    },
	  
	  
    getMenuBarItemClass: function(event) {
	    if (this.vertical) {
	        return "iceMnuBarVrtItem";
	    } else {
	        return "iceMnuBarItem";
	    }
	  },

	  getSubMenuClass: function(event) {
	    if (this.vertical) {
	        return "iceMnuBarVrtSubMenu";
	    } else {
	        return "iceMnuBarSubMenu";
	    }
	  },

	  getSubMenuIndClass: function(event) {
	    if (this.vertical) {
	        return "iceMnuBarVrtSubMenuInd";
	    } else {
	        return "iceMnuBarSubMenuInd";
	    }
	  },
	  
	  getRootClass: function() {
	    if (this.vertical) {
	        return "iceMnuBarVrt";
	    } else {
	        return "iceMnuBar";
	    }  
	  },
	  
	  getMenuItemClass: function() {
	     return "iceMnuItm";
	  },
	  
	  getPopupMenuClass: function() {
	     return "iceMnuPopVrtItem";
	  },
	    
	  hover: function(event, element, isMouseDown) {
	    if (!isMouseDown) {
	        if (Ice.Menu.currentHover && Ice.Menu.currentHover == element.id) {
	            //already hovered do nothing
	            return;
	        }
	    }
	    Ice.Menu.currentHover = element.id;
	    if (this.clicked) {
	       if (this.displayOnClick && Ice.Menu.lastClickedMenu != this.component.id) {
	          this.clicked = false;
	          return;
	       }    
	    
	        var submenu = $(element.id + '_sub');
	        Ice.Menu.hideOrphanedMenusNotRelatedTo(element);
	        if (this.vertical) {
	            Ice.Menu.show(this.component,submenu,element);
	        } else {
	            Ice.Menu.show(element,submenu,null);
	        }
	    }
	  },
	  
	  mousedown: function(event, element) {
	    Ice.Menu.lastClickedMenu = this.component.id;
	    if (this.clicked) {
	        this.clicked = false;
	    } else {
	        this.clicked = true;    
	        this.hover(event, element, true);
	    }
	  },
	  
	  focus: function(event, element) {
	    this.hover(event, element);
	  },
	  
	  configureRootItems: function () {
	    var rootLevelItems = this.component.childNodes;
	    for(i=0; i < rootLevelItems.length; i++) {
	        var element = rootLevelItems[i];
		    if (element.tagName == "DIV") {
		        if (Element.hasClassName(element, this.getMenuBarItemClass())) {
		            element.onmouseover = this.hover.bindAsEventListener(this, element);
			        //add focus support 
			        var anch = element.firstChild;
			        if (anch.tagName == "A") {
			            anch.onfocus = this.focus.bindAsEventListener(this, element);
			        }
			        if (this.displayOnClick) { 
			            element.onmousedown = this.mousedown.bindAsEventListener(this, element);
			            this.clicked = false;            
			        }
		        }
		    }
	    }
	  },
	  
	  hideAll:function(event) {
	      element = Event.element(event); 
	      var baritem = element.up('.'+ this.getMenuBarItemClass());
	      var elt = event.element();
	      if (elt && elt.match("a[onclick]")) {
	          elt = elt.down();
	      }
	      if (elt) {
	          elt = elt.up(".iceMnuItm a[onclick^='return false']");
	      }
	      if (!(baritem && this.clicked) && !elt) {
	        Ice.Menu.lastClickedMenue = null;
	        Ice.Menu.hideAll();
	        if (this.displayOnClick) {       
	            this.clicked = false;
	        }         
	      }
	      event.stopPropagation();
    },
	   
    hideAllDocument:function(event) {
        Ice.Menu.lastClickedMenu = "document";
        if (this.displayOnClick) {       
            this.clicked = false;
        } 
        Ice.Menu.hideAll();
    },
	      
    showMenu:function(event) {
        element = Event.element(event);    
        var baritem = element.up('.'+ this.getMenuBarItemClass());
        if (baritem && this.displayOnClick) {
            this.mousedown(event, baritem);
       }
    }

});

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

Ice.dataTable = {};

Ice.dataTable.DataTable = Class.create({
    initialize: function(id) {
        this.id = id;
        this.resizeObserver = this.resize.bindAsEventListener(this);
    },

    resize: function() {
        var table = $(this.id);
        if (!table) return;
        var scrollTable = table.select("div.iceDatTblScrlSpr")[0];
        //no scrollabletable
        if (!scrollTable) return;

        var spacer = scrollTable.select("table > thead > tr > th:last-child > div")[0];
        var body = table.select("div.iceDatTblScrlSpr + div")[0];
        //nobody
        if (!body) return;

        var borderLeftWidth = body.getStyle("borderLeftWidth");
        var borderRightWidth = body.getStyle("borderRightWidth");
        if (Prototype.Browser.IE && body.scrollHeight > body.clientHeight) {
            body.style.overflowX = "hidden";
            body.style.overflowY = "scroll";
        }
        var width = body.getWidth();
        var scrollWidth = width - body.clientWidth;

        //no scroller
        if (scrollWidth == 0) return;

        body.setStyle({borderLeftWidth:0, borderRightWidth:0});
        var innerTable = body.select("table")[0];
        var headerTable = scrollTable.select("table")[0];

        if (spacer)
            spacer.setStyle({width:scrollWidth + "px"});

        //fixing IE6 bug, table width should be decreased by scrollWidth    
        var innerTable = body.select("table")[0];
        if (innerTable) {
            var innerTableWidth = innerTable.getWidth();
            if (Prototype.Browser.IE) {
                //            innerTable.setStyle({width:body.clientWidth  + "px"});
            }
        }

        body.setStyle({borderLeftWidth:borderLeftWidth, borderRightWidth:borderRightWidth});

    }
});

Ice.dataTable.DataTable.hash = $H();

Ice.dataTable.onLoad = function(id) {
    var table = Ice.dataTable.DataTable.hash.get(id);
    if (table) {
        Event.stopObserving(window, "load", table.resizeObserver);
        Event.stopObserving(window, "resize", table.resizeObserver);
    }
    table = new Ice.dataTable.DataTable(id);
    table.resize();
    Event.observe(window, "load", table.resizeObserver);
    Event.observe(window, "resize", table.resizeObserver);
    Ice.dataTable.DataTable.hash.set(id, table);
};
Ice.PanelConfirmation = Class.create({
    initialize: function(trigger, e, confirmationPanelId, autoCentre, draggable, displayAtMouse, iframeUrl, handler) {
        this.srcComp = trigger;
        this.event = e;
        this.panel = $(confirmationPanelId);
        this.url = iframeUrl;
        this.srcHandler = handler;

        this.isAutoCentre = autoCentre;
        this.isDraggable = draggable;
        this.isAtMouse = displayAtMouse;

        Ice.PanelConfirmation.current = this;
        this.showPanel();
    },
    showPanel: function() {
        Ice.modal.start(this.panel.id, this.url);
        Ice.iFrameFix.start(this.panel.id, this.url);
        this.panel.style.display = '';
        this.handleDraggableObject();
        Ice.autoPosition.stop(this.panel.id);
        if (this.isAtMouse) {
            this.panel.style.left = parseInt(Event.pointerX(this.event)) + "px";
            this.panel.style.top = parseInt(Event.pointerY(this.event)) + "px";
        } else {
            Ice.autoCentre.start(this.panel.id);
        }
        if (!this.isAutoCentre) {
            Ice.autoCentre.stop(this.panel.id);
        }
        this.setDefaultFocus();
    },
    accept: function() {
        this.close();
        setFocus(this.srcComp.id);
        this.srcHandler.call(this.srcComp, this.event);
    },
    cancel: function() {
        this.close();
    },
    close: function() {
        Ice.PanelConfirmation.current = null;
        this.panel.style.visibility = 'hidden';
        this.panel.style.display = 'none';
        Ice.modal.stop(this.panel.id);
        Ice.autoCentre.stop(this.panel.id);
        Draggable.removeMe(this.panel.id);
        Ice.Focus.setFocus(this.srcComp.id);
    },
    handleDraggableObject: function() {
        if (this.isDraggable) {
            Ice.DnD.adjustPosition(this.panel.id);
            new Draggable(this.panel.id, {
                handle:this.panel.id + '-handle',
                dragGhost:false,
                dragCursor:false,
                ghosting:false,
                revert:false,
                mask:'1,2,3,4,5'
            });
        }
    },
    setDefaultFocus: function() {
        var cancel = $(this.panel.id + '-cancel');
        if (cancel) {
            cancel.focus();
        } else {
            $(this.panel.id + '-accept').focus();
        }
    }
});

Ice.PanelConfirmation.current = null;

Ice.Calendar = {};
Ice.Calendar.listeners = {};

Ice.Calendar.addCloseListener = function(calendar, form, commandLink, hiddenField) {
    if (Ice.Calendar.listeners[calendar]) {
        return;
    } else {
        Ice.Calendar.listeners[calendar] = new Ice.Calendar.CloseListener(calendar, form, commandLink, hiddenField);
    }
};

Ice.Calendar.CloseListener = Class.create({
    initialize: function(calendar, form, commandLink, hiddenField) {
        this.calendarId = calendar;
        this.formId = form;
        this.commandLinkId = commandLink;
        this.hiddenFieldId = hiddenField;

        this.popupId = this.calendarId + '_ct';
        this.buttonId = this.calendarId + '_cb'

        this.handler = this.closePopupOnClickOutside.bindAsEventListener(this);
        Event.observe(document, 'click', this.handler);
    },
    closePopupOnClickOutside: function(event) {
        if (this.getPopup()) {
            if (this.isInPopup(event.element())) {
                return;
            }
            if (this.isWithin(this.getPopup(), event)) {
                return;
            }
            if (event.element() == this.getButton()) {
                this.dispose();
                return;
            }

            var id = event.element().id;
            if (id) setFocus(id);
            else setFocus('');

            this.submit(event);
            this.dispose();
        }
    },
    isInPopup: function(element) {
        if (element.id == this.popupId) return true;
        if (element == undefined || element == document) return false;
        return this.isInPopup(element.parentNode);
    },
    isWithin: function(element, event) {
        return Position.within(element, Event.pointerX(event), Event.pointerY(event));
    },
    dispose: function() {
        Ice.Calendar.listeners[this.calendarId] = null;
        Event.stopObserving(document, 'click', this.handler);
    },
    submit: function(event) {
        document.forms[this.formId][this.commandLinkId].value = this.getButton().id;
        document.forms[this.formId][this.hiddenFieldId].value = 'toggle';
        iceSubmitPartial(document.forms[this.formId], this.getButton(), event);
    },
    getPopup: function() {
        return $(this.popupId);
    },
    getButton: function() {
        return $(this.buttonId);
    }
});

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

Ice.Menu = Class.create();
Ice.Menu = {
    menuContext:null,
    currentMenu:null,
    openMenus:new Array(0),
    printOpenMenus:function() {
        var openMenuString = '';
        for (var i = 0; i < Ice.Menu.openMenus.length; i++) {
            openMenuString = openMenuString + Ice.Menu.openMenus[i].id + ' , ';
        }
        return openMenuString;
    },
    printHoverMenuAndOpenMenus: function(hoverMenu) {
        alert('hoverMenu=[' + hoverMenu.id + ']\n'
                + 'openMenus=[' + Ice.Menu.printOpenMenus() + ']');
    },
    printArray: function(arrayToPrint) {
        var buffer = '';
        for (var i = 0; i < arrayToPrint.length; i++) {
            buffer = buffer + arrayToPrint[i] + ', ';
        }
        return buffer;
    },
    printArrayOfIds: function(arrayToPrint) {
        var buffer = '';
        for (var i = 0; i < arrayToPrint.length; i++) {
            buffer = buffer + arrayToPrint[i].id + ', ';
        }
        return buffer;
    },
    hideAll: function() {
        for (var i = 0; i < Ice.Menu.openMenus.length; i++) {
            if (Ice.Menu.openMenus[i].iframe) Ice.Menu.openMenus[i].iframe.hide(); // ICE-2066, ICE-2912
            Ice.Menu.openMenus[i].style.display = 'none';
        }
        Ice.Menu.openMenus = new Array();
        Ice.Menu.currentMenu = null;
        Ice.Menu.menuContext = null;
    },
    getPosition: function(element, positionProperty) {
        var position = 0;
        while (element != null) {
            position += element["offset" + positionProperty];
            element = element.offsetParent;
        }
        return position;
    },
    show: function(supermenu, submenu, submenuDiv) {
        supermenu = $(supermenu);
        submenu = $(submenu);
        submenuDiv = $(submenuDiv);
        if (submenu) {
            var menu = $(submenu);
            //menu is already visible, don't do anything
            if (menu && menu.style.display == '') return;
            Ice.Menu.showMenuWithId(submenu);
            if (submenuDiv) {
                // ICE-3196, ICE-3620
                if (supermenu.viewportOffset().left + supermenu.offsetWidth + submenu.offsetWidth < document.viewport.getWidth()) {
                    submenu.clonePosition(supermenu, {setTop:false, setWidth:false, setHeight:false, offsetLeft:supermenu.offsetWidth});
                } else {
                    submenu.clonePosition(supermenu, {setTop:false, setWidth:false, setHeight:false, offsetLeft:- submenu.offsetWidth});
                }
                if (submenuDiv.viewportOffset().top + submenu.offsetHeight < document.viewport.getHeight()) {
                    submenu.clonePosition(submenuDiv, {setLeft:false, setWidth:false, setHeight:false});
                } else {
                    submenu.clonePosition(submenuDiv, {setLeft:false, setWidth:false, setHeight:false,
                        offsetTop:- submenu.offsetHeight + submenuDiv.offsetHeight});
                }
            } else {
                // ICE-3196, ICE-3620
                if (supermenu.viewportOffset().left + submenu.offsetWidth < document.viewport.getWidth()) {
                    submenu.clonePosition(supermenu, {setTop:false, setWidth:false, setHeight:false});
                } else {
                    submenu.clonePosition(supermenu, {setTop:false, setWidth:false, setHeight:false,
                        offsetLeft:document.viewport.getWidth() - supermenu.viewportOffset().left - submenu.offsetWidth});
                }
                if (supermenu.viewportOffset().top + supermenu.offsetHeight + submenu.offsetHeight < document.viewport.getHeight()) {
                    submenu.clonePosition(supermenu, {setLeft:false, setWidth:false, setHeight:false, offsetTop:supermenu.offsetHeight});
                } else {
                    submenu.clonePosition(supermenu, {setLeft:false, setWidth:false, setHeight:false, offsetTop:- submenu.offsetHeight});
                }
            }
            if (submenu.viewportOffset().top < 0) { // ICE-3658
                submenu.clonePosition(submenu, {setLeft:false, setWidth:false, setHeight:false, offsetTop:- submenu.viewportOffset().top});
            }
            Ice.Menu.showIframe(submenu); // ICE-2066, ICE-2912
        }
        Ice.Menu.currentMenu = submenu;
    },
    showPopup: function(showX, showY, submenu) {
        Ice.Menu.hideAll();
        submenu = $(submenu);
        if (submenu) {
            Ice.Menu.showMenuWithId(submenu);
            var styleLeft = showX + "px";
            submenu.style.left = styleLeft;

            var styleTop = showY + "px";
            submenu.style.top = styleTop;
            Ice.Menu.showIframe(submenu); // ICE-2066, ICE-2912
        }
        Ice.Menu.currentMenu = submenu;
    },
    showIframe: function(menuDiv) { // ICE-2066, ICE-2912
        if (!Prototype.Browser.IE) return;
        if (parseFloat(navigator.userAgent.substring(navigator.userAgent.indexOf("MSIE") + 5)) >= 7) return;
        var iframe = menuDiv.iframe;
        if (!iframe) {
            menuDiv.iframe = iframe = new Element("iframe", {src: 'javascript:\'<html></html>\';', frameborder: "0", scrolling: "no"});
            iframe.setStyle({position: "absolute", opacity: 0}).hide();
            menuDiv.insert({before: iframe});
        }
        iframe.clonePosition(menuDiv).show();
    },
    contextMenuPopup: function(event, popupMenu, targComp) {
        var dynamic = $(popupMenu + "_dynamic");
        if (!event) {
            event = window.event;
        }
        if (event) {
            event.returnValue = false;
            event.cancelBubble = true;

            if (event.stopPropagation) {
                event.stopPropagation();
            }

            var posx = 0; // Mouse position relative to
            var posy = 0; //  the document
            if (event.pageX || event.pageY) {
                posx = event.pageX;
                posy = event.pageY;
            }
            else if (event.clientX || event.clientY) {
                posx = event.clientX + document.body.scrollLeft
                        + document.documentElement.scrollLeft;
                posy = event.clientY + document.body.scrollTop
                        + document.documentElement.scrollTop;
            }
            if (dynamic) {
                dynamic.value = posx + ", " + posy + ", " + popupMenu + ", " + targComp;
                try {
                    var form = Ice.util.findForm(dynamic);
                    iceSubmitPartial(form, dynamic, event);
                } catch (e) {
                    logger.info("Form not found" + e);
                }
                return;
            }

            Ice.Menu.showIt(posx, posy, popupMenu, targComp);
        }
    },
    showIt: function(posx, posy, popupMenu, targComp) {
        Ice.Menu.showPopup(posx, posy, popupMenu.strip());
        Event.observe(document, "click", Ice.Menu.hidePopupMenu);
        Ice.Menu.setMenuContext(targComp.strip());
    },

    setMenuContext: function(mnuCtx) {
        if (Ice.Menu.menuContext == null) {
            Ice.Menu.menuContext = mnuCtx;
        }
    },
    hideOrphanedMenusNotRelatedTo: function(hoverMenu) {
        // form an array of allowable names
        var relatedMenus = new Array();
        var idSegments = hoverMenu.id.split(':');
        var nextRelatedMenu = '';
        for (var i = 0; i < idSegments.length; i++) {
            nextRelatedMenu = nextRelatedMenu + idSegments[i];
            var concatArray = new Array(nextRelatedMenu + '_sub');
            relatedMenus = relatedMenus.concat(concatArray);
            nextRelatedMenu = nextRelatedMenu + ':';
        }
        // iterate over open menus and set display='none' for any menu
        // that is not in the array of menus related to the current menu
        var arrayLength = Ice.Menu.openMenus.length;
        var menusToHide = new Array();
        for (var j = 0; j < arrayLength; j ++) {
            var nextOpenMenu = $(Ice.Menu.openMenus[j]);
            var found = 'false';
            for (var k = 0; k < relatedMenus.length; k++) {
                if (nextOpenMenu.id == relatedMenus[k]) {
                    found = 'true';
                }
            }
            if (found != 'true') {
                menusToHide[menusToHide.length] = nextOpenMenu.id;
                if (nextOpenMenu == Ice.Menu.currentMenu) {
                    Ice.Menu.currentMenu = null;
                }
            }
        }
        // iterate over the menus to hide
        Ice.Menu.hideMenusWithIdsInArray(menusToHide);
    },
    hideSubmenu: function(hoverMenu) {
        var cur = Ice.Menu.currentMenu;
        var hoverParentId = hoverMenu.id.substring(0, hoverMenu.id.lastIndexOf(':'));
        var curParentId = cur.id.substring(0, cur.id.lastIndexOf(':'));
        if (hoverParentId == curParentId) {
            Ice.Menu.hideMenuWithId(Ice.Menu.currentMenu);
        }
    },
    hideMenusWithIdsInArray: function(menuIdArray) {
        if (menuIdArray) {
            for (var i = 0; i < menuIdArray.length; i ++) {
                Ice.Menu.hideMenuWithId(menuIdArray[i]);
            }
        }
    },
    hideMenuWithId: function(menu) {
        menu = $(menu);
        if (menu) {
            if (menu.iframe) menu.iframe.hide(); // ICE-2066, ICE-2912
            menu.style.display = 'none';
            Ice.Menu.removeFromOpenMenus(menu);
        }
        return;
    },
    removeFromOpenMenus: function(menu) {
        var tempArray = new Array();
        for (var i = 0; i < Ice.Menu.openMenus.length; i ++) {
            if (Ice.Menu.openMenus[i].id != menu.id) {
                tempArray = tempArray.concat(new Array(Ice.Menu.openMenus[i]));
            }
        }
        Ice.Menu.openMenus = tempArray;
    },
    showMenuWithId: function(menu) {
        if (menu) {
            menu = $(menu);
            menu.style.display = '';
            Ice.Menu.addToOpenMenus(menu);
        }
    },
    addToOpenMenus: function(menu) {
        if (menu) {
            menu = $(menu);
            var found = 'false';
            for (var i = 0; i < Ice.Menu.openMenus.length; i ++) {
                if (Ice.Menu.openMenus[i].id == menu.id) {
                    found = 'true';
                    break;
                }
            }
            if (found != 'true') {
                var openMenu = new Array(menu);
                Ice.Menu.openMenus = Ice.Menu.openMenus.concat(openMenu);
            }

        }
    },
    appendHoverClasses: function(menuItem) {
        var styleClasses = menuItem.className.replace(/^\s+|\s+$/g, "").split(/\s+/);
        if (styleClasses[0] == "") return;

        for (var i = 0; i < styleClasses.length; i++) {
            if (styleClasses[i] == "portlet-menu-item-selected") {
                menuItem.className += " portlet-menu-item-hover-selected";
            } else {
                menuItem.className += " " + styleClasses[i] + "-hover";
            }
        }
    },
    removeHoverClasses: function(menuItem) {
        var n = menuItem.className.replace(/^\s+|\s+$/g, "").split(/\s+/).length / 2;
        var regExp = new RegExp("( portlet-menu-item-hover-selected| \\S+-hover){" + n + "}$");
        menuItem.className = menuItem.className.replace(regExp, "");
    },
    hidePopupMenu:function() {
        Ice.Menu.hideAll();
        Event.stopObserving(document, "click", Ice.Menu.hidePopupMenu);
    },
    hideOnMouseOut: function(rootID, event) {
        if (!event) {
            event = window.event;
        }
        if (event) {
            var element;
            element = event.relatedTarget;
            if (!element) element = event.toElement;
            if (element) {
                if (!Ice.Menu.isInMenu(element, rootID)) {
                    Ice.Menu.hideAll();
                }
            }
        }
    },
    isInMenu: function(element, rootID) {
        if (element.id == rootID) return true;
        if (element == undefined || element == document) return false;
        return Ice.Menu.isInMenu(element.parentNode, rootID);
    },

    cancelEvent:function(event) {
        if (window.event) {
            event.cancelBubble = true;
        } else if (event.stopPropagation) {
            event.stopPropagation();
        }
        return false;
    }
}

ice.onAfterUpdate(function() {
    if (Ice.StateMon) {
        Ice.StateMon.checkAll();
        Ice.StateMon.rebuild();
    }
});

Ice.treeNavigator = Class.create();
Ice.treeNavigator = {
  
      handleFocus: function (event, root, deep) {
           var type = event.type;
           if(type == 'click') {
              Ice.treeNavigator.reset();
              return;
           }

           var ele = Event.element(event);
           var kc= event.keyCode;
           var imgSrc = null;
           if (ele && ele.firstChild.getAttribute) {
              imgSrc = ele.firstChild.getAttribute('src');
           }
          if (!imgSrc) return;
          switch (kc) {
	          case 37: //left
	          //root node
	          if (imgSrc.indexOf('top_close_no_siblings') > 0 ||
	          imgSrc.indexOf('middle_close') > 0 ||
	          imgSrc.indexOf('bottom_close') > 0 ) {
	            logger.info('LEFT_KEY: top_close_no_siblings FOUND, root Node opend close it and reinitialize index'); 
	            ele.onclick();
	            Ice.treeNavigator.reset();
                return false;
	          }    
	    
	          break;
	
	          case 39: //right
		          if (imgSrc.indexOf('top_open_no_siblings') > 0 ||
		          imgSrc.indexOf('middle_open') > 0 ||
		          imgSrc.indexOf('bottom_open') > 0 ) {
		                ele.onclick();
		                Ice.treeNavigator.reset();  
		                return false;             
		          }        
	                
	          break;
	
	          case 38: //up
	              if (!Ice.treeNavigator.anchors) {
		             Ice.treeNavigator.updateAnchor(root, ele, deep);
		          }
		
		          if (imgSrc) {
		             if(imgSrc.indexOf('top_close_no_siblings') > 0) {
			            Ice.treeNavigator.index = 1;
			            Ice.treeNavigator.anchors[Ice.treeNavigator.index].focus();
		             }
		            Ice.treeNavigator.focusPrevious();
		          }  else {
		            Ice.treeNavigator.focusPrevious();
		          }
	          return false;              
	          case 40: //down
	              logger.info ('down'); 
	              if (!Ice.treeNavigator.anchors) {
	                Ice.treeNavigator.updateAnchor(root, ele, deep);
	              }
	              Ice.treeNavigator.focusNext();
	          return false;
          }//switch ends    
      }, //func ends
  
          index:0,
          
          anchors:null,
          
          reset: function() {
             Ice.treeNavigator.index = 0;
             Ice.treeNavigator.anchors = null;     
      },
      
      focusNext: function(deep) {  
         if (Ice.treeNavigator.index <(Ice.treeNavigator.anchors.length-1)){
             Ice.treeNavigator.index = Ice.treeNavigator.index + 1;
         }
         Ice.treeNavigator.anchors[Ice.treeNavigator.index].focus();
      },
      
      focusPrevious : function(deep) {
         if (Ice.treeNavigator.index>0) {
             Ice.treeNavigator.index = Ice.treeNavigator.index - 1;
         }
         Ice.treeNavigator.anchors[Ice.treeNavigator.index].focus();      
      },
      
      updateAnchor: function(root, ele, deep) {
        var anchors = [];
            if(deep) { 
                anchors = root.parentNode.getElementsByTagName('a');
                for (i=0; anchors.length > i; i++) {
                    if (ele == anchors[i]) {
                        Ice.treeNavigator.index = i;   
                    }
                }//for                  
            } else {   
	            _anchors = root.parentNode.getElementsByTagName('a');
	            j = 0;
	            for (i=0; _anchors.length > i; i++) {
	               if (_anchors[i].firstChild.src && _anchors[i].firstChild.src.indexOf('tree_nav') > 0) {
	                    if (ele == _anchors[i]) {
	                       Ice.treeNavigator.index = j;     
	                    }
	                    anchors[j++] = _anchors[i];
	               }
	            }//for    
            }
            Ice.treeNavigator.anchors = anchors;
         }//updateAnchor
  }// func ends
  
  Ice.tabNavigator = function(event) {
       var ele = Event.element(event);
       var kc= event.keyCode;
       switch (kc) {
          case 37:
          case 38:
             var preCell = ele.up('.icePnlTb').previousSibling;
             if (preCell) {
                 var lnk = preCell.down('.icePnlTbLblLnk');
                 if(lnk) {
                    lnk.focus();
                 }
             }
             if (ele.up('.icePnlTb')) {
                 if (event.preventDefault){
                     event.preventDefault();
                 } else if (event.returnValue){
                     event.returnValue = false;
                 }
             }           
          break; 
          case 39:
          case 40:
             var nextCell = ele.up('.icePnlTb').nextSibling;
             if (nextCell && Element.hasClassName(nextCell, 'icePnlTb')) {
                 var lnk = nextCell.down('.icePnlTbLblLnk');
                 if(lnk) {
                    lnk.focus();
                 }
             }
             if (ele.up('.icePnlTb')) {
                 if (event.preventDefault){
                     event.preventDefault();
                 } else if (event.returnValue){
                     event.returnValue = false;
                 }
             }
          break;            
       
      }    
  }
  
Ice.pnlTabOnFocus = function(ele, facet, kbs) {
    setFocus(ele.id);
    if(kbs) { 
        Event.observe(ele, 'keydown', Ice.tabNavigator); 
    }   
    if (!facet) return;
    Ice.simulateFocus(ele.parentNode, ele);
}

Ice.pnlTabOnBlur = function(ele, facet, kbs) {
    if(kbs) { 
        Event.stopObserving(ele, 'keydown', Ice.tabNavigator);
    }
    if (!facet)return;    
    setFocus('');
    Ice.simulateBlur(ele.parentNode, ele);
}

Ice.pnlClpFocus = function(anc) {
    var parent = anc.parentNode;
    Ice.simulateFocus(parent, anc);
    parent.style.padding='0px'; 
}

Ice.pnlClpBlur = function(anc) {
    var parent = anc.parentNode;
    Ice.simulateBlur(parent, anc);
    parent.style.padding='1px';   
}

Ice.simulateFocus = function(ele, anc) {
    if(!document.all) {
        anc.style.visibility='hidden';
    } 
    anc.style.borderStyle='none';
    anc.style.outlineStyle='none'; 
    anc.style.borderWidth='0px';
    anc.style.outlineWidth='0px'; 
    anc.style.margin='0px';  
    ele['_borderStyle'] = ele.style.borderStyle;     
    ele.style.borderStyle='dotted';
    ele['_borderWidth'] = ele.style.borderWidth;   
    ele.style.borderWidth='1px 1px 1px 1px';
    ele['_borderColor'] = ele.style.borderColor;   
    ele.style.borderColor = 'black';    
}

Ice.simulateBlur = function(ele, anc) {
    if(!document.all) {    
        anc.style.visibility='visible';
    } 
    ele.style.borderStyle = ele['_borderStyle'];
    ele.style.borderWidth = ele['_borderWidth'];  
    ele.style.borderColor = ele['_borderColor'];   
};

Ice.DataExporterOpenWindow = function(clientId, path, label, popupBlockerLbl) {
    var wdo = window.open(path);

    if (!wdo || typeof(wdo)== "undefined") {
        var ele = $(clientId+'container').firstChild;
        var lbl = popupBlockerLbl == "null"? label: popupBlockerLbl ;
        ele.onclick= function() {
           window.open(path);
        };
      
        if (ele.tagName == "INPUT") {
           ele.value=lbl;
        } else {
            if (ele.firstChild.tagName == "IMG") {
               ele.firstChild.title = lbl;
            } else {
               ele.innerHTML = lbl;
            }
        }
    }
    new Effect.Highlight(clientId+'container', { startcolor: '#fda505',endcolor: '#ffffff' });
}

