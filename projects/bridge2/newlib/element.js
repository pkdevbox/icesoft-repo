onLoad(window, function() {
    var container = document.body.appendChild(document.createElement('div'));
    container.style.visibility = 'hidden';
    container.style.display = 'none';

    window.HTMLParser = function(parserCallback) {
        parserCallback(function(html) {
            container.innerHTML = html;
            return container.childNodes;
        });
        container.innerHTML = '';
    };
});

var id = operator();
var tag = operator();
var parents = operator();
var enclosingForm = operator();
var enclosingBridge = operator();

//DOM patching methods
var updateElement = operator();
var removeElement = operator();

var EventListenerNames = [
    'onkeydown', 'onkeypress', 'onkeyup', 'onhelp', 'onclick', 'ondblclick',
    'onmousedown', 'onmousemove', 'onmouseout', 'onmouseover', 'onmouseup',
    'onblur', 'onfocus', 'onchange', 'onreset', 'onsubmit'
];

function disconnectEventListeners(element) {
    each(EventListenerNames, function(listenerName) {
        try {
            element[listenerName] = null;
        } catch (e) {
            //ignore
        }
    });
}

var eachAttribute = operator();
var content = operator();
var asHTML = operator();

function Update(element) {
    var tag = element.getAttribute('tag');

    function appendStartTag(self, html) {
        append(html, '<');
        append(html, tag);
        eachAttribute(self, function(name, value) {
            append(html, ' ');
            append(html, name);
            append(html, '="');
            append(html, value);
            append(html, '"');
        });
        append(html, '>');
    }

    function appendEndTag(self, html) {
        append(html, '</');
        append(html, tag);
        append(html, '>');
    }

    return object(function(method) {
        method(eachAttribute, function(self, iterator) {
            each(element.getElementsByTagName('*'), function(e) {
                iterator(e.getAttribute('name'), attribute.firstChild ? attribute.firstChild.data : '');
            });
        });

        method(content, function(self) {
            var contentElement = element.getElementsByTagName('content')[0];
            return contentElement.firstChild ?
                   contentElement.firstChild.data.replace(/<\!\#cdata\#/g, '<![CDATA[').replace(/\#\#>/g, ']]>') : '';
        });

        method(asHTML, function(self) {
            var html = [];
            appendStartTag(self, html);
            append(html, content(self));
            appendEndTag(self, html);
            return join(html, '');
        });

        method(asString, function(self) {
            var html = [];
            appendStartTag(self, html);
            append(html, "...");
            appendEndTag(self, html);
            return join(html, '');
        });
    });
}

var focus = operator();
var canSubmitForm = operator();

function Element(element) {
    return object(function(method) {
        method(id, function(self) {
            return element.id;
        });

        method(tag, function(self) {
            return toLowerCase(element.tagName);
        });

        method(parents, function(self) {
            return Stream(function(cellConstructor) {
                function parentStream(e) {
                    if (e == null || e == document) return null;
                    return function() {
                        return cellConstructor($element(e), parentStream(e.parentNode));
                    };
                }

                return parentStream(element.parentNode);
            });
        });

        method(enclosingForm, function(self) {
            return detect(parents(self), function(e) {
                return tag(e) == 'form';
            }, function() {
                throw 'cannot find enclosing form';
            });
        });

        method(enclosingBridge, function(self) {
            return detect(parents(self), function(e) {
                return property(e, 'bridge') != null;
            }, function() {
                throw 'cannot find enclosing bridge';
            });
        });

        method(updateElement, function(self, update) {
            HTMLParser(function(parse) {
                var newElement = parse(asHTML(update))[0];
                each(element.getElementsByTagName('*'), disconnectEventListeners);
                element.parentNode.replaceChild(newElement, element);
                element = newElement;
            });
        });

        method(submit, function(self) {
            var query = Query();
            serializeOn(self, query);
            send(connection(enclosingBridge(self)), query);
        });

        method(serializeOn, noop);

        method(canSubmitForm, none);

        method(asString, function(self) {
            return asString(element);
        });
    });
}

function InputElement(element) {
    return objectWithAncestors(function(method) {
        method(enclosingForm, function(self) {
            var f = element.form;
            if (f) {
                return $element(f);
            } else {
                throw 'cannot find enclosing form';
            }
        });

        method(focus, function(self) {
            var onFocusListener = element.onfocus;
            element.onfocus = noop;
            element.focus();
            element.onfocus = onFocusListener;
        });

        method(canSubmitForm, function(self) {
            var type = toLowerCase(element.type);
            return type == 'submit' || type == 'image' || type == 'button';
        });

        method(serializeOn, function(self, query) {
            switch (toLowerCase(element.type)) {
                case 'image':
                case 'textarea':
                case 'submit':
                case 'hidden':
                case 'password':
                case 'text': addNameValue(query, element.name, element.value); break;
                case 'checkbox':
                case 'radio': if (element.checked) addNameValue(query, element.name, element.value || 'on'); break;
            }
        });
    }, Element(element));
}

function SelectElement(element) {
    return objectWithAncestors(function(method) {
        method(canSubmitForm, none);

        method(serializeOn, function(self, query) {
            each(select(element.options, function(option) {
                return option.selected;
            }), function(selectedOption) {
                var value = selectedOption.value || (selectedOption.value == '' ? '' : selectedOption.text);
                addNameValue(query, element.name, value);
            });
        });
    }, InputElement(element));
}

function ButtonElement(element) {
    return objectWithAncestors(function(method) {
        method(canSubmitForm, function(self) {
            return toLowerCase(element.type) == 'submit';
        });

        method(serializeOn, function(self, query) {
            addNameValue(query, element.name, element.value);
        });
    }, InputElement(element));
}

function AnchorElement(element) {
    return objectWithAncestors(function(method) {
        method(canSubmitForm, any);

        method(serializeOn, function(self, query) {
            var name = element.name;
            if (name) addNameValue(query, name, name);
        });
    }, InputElement(element));
}

var detectDefaultSubmit = operator();
var captureAndRedirectSubmit = operator();
var submit = operator();

function FormElement(element) {
    return objectWithAncestors(function(method) {
        method(enclosingForm, function(self) {
            throw 'forms cannot be nested';
        });

        //todo: this method should go away since it encourages special treatment for elements with ID ending in ':default' 
        method(detectDefaultSubmit, function(self) {
            var defaultID = this.element.id + ':default';
            return $element(detect(element.elements, function(e) {
                e.id = defaultID;
            }, function() {
                throw 'cannot find default submit';
            }));
        });

        method(captureAndRedirectSubmit, function(self) {
            //captures normal form submit events and sends them through a XMLHttpRequest
            var previousOnSubmit = element.onsubmit;
            element.onsubmit = function(event) {
                if (previousOnSubmit) previousOnSubmit();
                submit(self);
            };

            element.submit = function() {
                element.onsubmit = noop;
                submit(self);
            };
        });

        method(submit, function(self) {
            var query = Query();
            serializeOn(self, query);
            send(connection(enclosingBridge(self)), query);
        });

        method(updateElement, function(self, update) {
            each(element.getElementsByTagName('*'), disconnectEventListeners);
            disconnectEventListeners(element);
            element.innerHTML = content(update);
            each(['acceptcharset', 'action', 'enctype', 'method', 'name', 'target'], function(name) {
                element.removeAttribute(name);
            });
            eachAttribute(update, function(name, value) {
                try {
                    element.setAttribute(name, value);
                } catch (e) {
                    error(logger, 'failed to set attribute ' + name + ':' + value, e);
                }
            });
        });
    }, Element(element));
}

function ScriptElement(element) {
    return objectWithAncestors(function(method) {
        method(updateElement, function(self, update) {
            //if script element is updated its code will be evaluate in IE (thus evaluating it twice)
            //evaluate code in the 'window' context
            var scriptCode = content(update);
            if (scriptCode != '' && scriptCode != ';') {
                var evalFunc = function() {
                    eval(scriptCode);
                };
                evalFunc.apply(window);
            }
        });
    }, Element(element));
}

function TitleElement(element) {
    return objectWithAncestors(function(method) {
        method(updateElement, function(self, update) {
            element.ownerDocument.title = content(update);
        });
    }, Element(element));
}

function TableCellElement(element) {
    return objectWithAncestors(function(method) {
        method(updateElement, function(self, update) {
            HTMLParser(function(parse) {
                var newElement = parse('<TABLE>' + asHTML(update) + '</TABLE>')[0];
                each(element.getElementsByTagName('*'), disconnectEventListeners);
                while ((null != newElement) && (element.id != newElement.id)) {
                    newElement = newElement.firstChild;
                }
                element.parentNode.replaceChild(newElement, element);
                element = newElement;
            });
        });
    }, Element(element));
}

function BodyElement(element) {
    return objectWithAncestors(function(method) {
        method(updateElement, function(self, update) {
            each(element.getElementsByTagName('*'), disconnectEventListeners);
            //strip <noscript> tag to fix Safari bug
            // #3131 If this is a response from an error code, there may not be a <noscript> tag.
            var html = content(update);
            var start = new RegExp('\<noscript\>', 'g').exec(html);
            if (start == null) {
                element.innerHTML = html;
            } else {
                var end = new RegExp('\<\/noscript\>', 'g').exec(html);
                element.innerHTML = substring(html, 0, start.index) + substring(html, end.index + 11, html.length);
            }
        });
    }, Element(element));
}

function IFrameElement(element) {
    return objectWithAncestors(function(method) {
        method(updateElement, function(self, update) {
            HTMLParser(function(parse) {
                var newElement = parse(asHTML(update))[0];

                each(['title', 'lang', 'dir', 'class', 'style', 'align', 'frameborder',
                    'width', 'height', 'hspace', 'ismap', 'longdesc', 'marginwidth',
                    'marginheight', 'name', 'scrolling'], function(attributeName) {
                    var value = newElement.getAttribute(attributeName);
                    if (value == null) {
                        element.removeAttribute(attributeName);
                    } else {
                        element.setAttribute(attributeName, value);
                    }
                });

                //special case for the 'src' attribute (Safari bug?)
                var oldLocation = element.contentWindow.location.href;
                var newLocation = newElement.contentWindow.location.href;
                if (oldLocation != newLocation) {
                    element.contentWindow.location = newLocation;
                }

                //overwrite listeners and bind them to the existing element
                each(EventListenerNames, function(name) {
                    element[name] = newElement[name] ? newElement[name] : null;
                    newElement[name] = null;
                });
            });
        });
    }, Element(element));
}

function $element(e) {
    //no polymophism here...'switch' is the way then.
    switch (toLowerCase(e.tagName)) {
        case 'textarea':
        case 'input': return InputElement(e);
        case 'th':
        case 'td':
        case 'tr': return TableCellElement(e);
        case 'button': return ButtonElement(e);
        case 'select': return SelectElement(e);
        case 'form': return FormElement(e);
        case 'body': return BodyElement(e);
        case 'script': return ScriptElement(e);
        case 'title': return TitleElement(e);
        case 'a': return AnchorElement(e);
        case 'iframe': return IFrameElement(e);
        default : return Element(e);
    }
}

function $elementWithID(id) {
    return $element(document.getElementById(id));
}