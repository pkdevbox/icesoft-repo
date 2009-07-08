//fckeditor needs Ice namespace
window.Ice = {};

function iceSubmitPartial(form, component, evt) {
    ice.submitEvent(evt, component, form || formOf(component));
    return false;
}

function iceSubmit(form, component, evt) {
    var code;
    if (evt.keyCode) code = evt.keyCode;
    else if (evt.which) code = evt.which;
    if (code > 3) {
        if (code != 13) {
            return false;
        }
    }
    ice.submitEvent(evt, component || form, form || formOf(component));
    return false;
}

function formOf(element) {
    var parent = element.parentNode;
    while (parent) {
        if (parent.tagName && parent.tagName.toLowerCase() == 'form') return parent;
        parent = parent.parentNode;
    }

    throw 'Cannot find enclosing form.';
}

function setFocus() {
}

window.onLoad = ice.onLoad;
window.onUnload = ice.onUnload;
var noop = function() {
};
window.logger = {debug: noop, info: noop, warn: noop, error: noop, child: function() {
    return window.logger
}};
