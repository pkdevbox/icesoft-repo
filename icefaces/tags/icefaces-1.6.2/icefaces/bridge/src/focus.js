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

//todo: implement focus management!
var currentFocus;
Ice.Focus = new Object();
Ice.Focus.userInterupt = false;

Ice.Focus.userInterupt = function (e){
    window.logger.debug('Interup pressed');
    if(Ice.Focus.userInterupt == false){
        window.logger.debug('User action. Set focus will be ignored.');
        Ice.Focus.userInterupt = true;
    }

}

Ice.Focus.setFocus = function(id){
    // This delay is required for focusing newly rendered components on IE 
    // ICE-1247
    window.setTimeout("Ice.Focus.setFocusNow('" + id + "');",100);  
};

Ice.Focus.setFocusNow = function(id){
    if((Ice.Focus.userInterupt==false) && (id != '') && (id != 'undefined')){
        try{
            id.asExtendedElement().focus();
            setFocus(id);
            var ele = document.getElementById(id);
            if(ele){
                ele.focus();
            }else{
                window.logger.debug('Focus Failed. No element for id [' + id + "]");                
            }
            window.logger.debug('Focus Set on [' + id + "]");
        }catch(e){
            window.logger.error('Failed to set focus on [' + id +']',e);               
        }
    }else{
        window.logger.debug('Focus interupted. Not Set on [' + id + ']');
    }
};


document.onKeyDown = function(listener){
    var previousListener = document.onkeydown;
    document.onkeydown = previousListener!=null ? function(e){
        listener(Ice.EventModel.Event.adaptToKeyEvent(e));
        previousListener(e);
    } : function(e){
        listener(Ice.EventModel.Event.adaptToKeyEvent(e));
    };
};

document.onMouseDown = function(listener){
    var previousListener = document.onmousedown;
    document.onmousedown = previousListener!=null ? function(e){
        listener(e);
        previousListener(e);
    } : function(e){
        listener(e);
    };
};

document.onKeyDown(Ice.Focus.userInterupt);
document.onMouseDown(Ice.Focus.userInterupt);

function setFocus(id) {
    currentFocus = id;
}

window.onScroll(function() {
    currentFocus = null;
    window.focus();
});

