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

[ Ice.Community ].as(function(This) {

    This.Application = Object.subclass({
        initialize: function(configuration, container) {
            var sessionID = configuration.session;
            var viewID = configuration.view;
            var logger = window.logger.child(sessionID.substring(0, 4) + '#' + viewID);
            var statusManager = new Ice.Status.StatusManager(configuration, container);
            var scriptLoader = new Ice.Script.Loader(logger);
            var commandDispatcher = new Ice.Command.Dispatcher();
            var parameters = Ice.Parameter.Query.create(function(query) {
                query.add('ice.session', sessionID);
                query.add('ice.view', viewID);
            });
            this.connection = configuration.synchronous ?
                              new Ice.Connection.SyncConnection(logger, configuration.connection, parameters) :
                              new This.Connection.AsyncConnection(logger, sessionID, viewID, configuration.connection, parameters, commandDispatcher);

            commandDispatcher.register('noop', Function.NOOP);
            commandDispatcher.register('set-cookie', Ice.Command.SetCookie);
            commandDispatcher.register('parsererror', Ice.Command.ParsingError);
            commandDispatcher.register('redirect', function(element) {
                //replace ampersand entities incorrectly decoded by Safari 2.0.4
                var url = element.getAttribute("url").replace(/&#38;/g, "&");
                logger.info('Redirecting to ' + url);
                //avoid view disposal on navigation rules
                if (url.contains('rvn=')) {
                    this.connection.cancelDisposeViews();
                }
                window.location.href = url;
            }.bind(this));
            commandDispatcher.register('reload', function(element) {
                logger.info('Reloading');
                var url = window.location.href;
                this.connection.cancelDisposeViews();
                if (url.contains('rvn=')) {
                    window.location.reload();
                } else {
                    var view = element.getAttribute('view');
                    var queryPrefix = url.contains('?') ? '&' : '?';
                    window.location.href = url + queryPrefix + 'rvn=' + view;
                }
            }.bind(this));
            commandDispatcher.register('macro', function(message) {
                $enumerate(message.childNodes).each(function(subMessage) {
                    commandDispatcher.deserializeAndExecute(subMessage);
                });
            });
            commandDispatcher.register('updates', function(element) {
                $enumerate(element.getElementsByTagName('update')).each(function(updateElement) {
                    try {
                        var address = updateElement.getAttribute('address');
                        var update = new Ice.ElementModel.Update(updateElement);
                        address.asExtendedElement().updateDOM(update);
                        logger.debug('applied update : ' + update.asString());
                        scriptLoader.searchAndEvaluateScripts(address.asElement());
                        if (Ice.StateMon) {
                            Ice.StateMon.checkAll();
                            Ice.StateMon.rebuild();
                        }
                    } catch (e) {
                        logger.error('failed to insert element: ' + update.asString(), e);
                    }
                });
            });
            commandDispatcher.register('session-expired', function() {
                logger.warn('Session has expired');
                statusManager.sessionExpired.on();
                statusManager.sessionExpiredPopup.on(); // ICE-2621
                this.connection.cancelDisposeViews();
                this.dispose();
            }.bind(this));

            window.onBeforeUnload(function() {
                this.connection.sendDisposeViews();
                this.connection.shutdown();
            }.bind(this));

            window.onUnload(function() {
                this.dispose();
            }.bind(this));

            this.connection.onSend(function() {
                Ice.Focus.userInterupt = false;
            });

            this.connection.onReceive(function(request) {
                commandDispatcher.deserializeAndExecute(request.contentAsDOM().documentElement);
            });

            this.connection.onReceive(function() {
                window.documentSynchronizer.synchronize();
            });

            this.connection.onServerError(function (response) {
                logger.error('server side error');
                statusManager.serverError.on();
                statusManager.serverErrorPopup.on(); // ICE-2621
                this.connection.sendDisposeViews();
                this.dispose();
                //todo: refactor this in something more elegant
                var html = response.content();
                var start = new RegExp('\<body[^\<]*\>', 'g').exec(html);
                var end = new RegExp('\<\/body\>', 'g').exec(html);
                var body = html.substring(start.index, end.index + end[0].length)
                var content = body.substring(html.indexOf('>') + 1, html.lastIndexOf('<'));
                var tag = container.tagName;
                var c = $element(container);
                c.disconnectEventListeners();
                c.replaceHtml(['<', tag, '>', content, '</', tag, '>'].join(''));
                scriptLoader.searchAndEvaluateScripts(container);
            }.bind(this));

            this.connection.whenDown(function() {
                logger.warn('connection to server was lost');
                statusManager.connectionLost.on();
                statusManager.connectionLostPopup.on(); // ICE-2621
                this.dispose();
            }.bind(this));

            this.connection.whenTrouble(function() {
                logger.warn('connection in trouble');
                statusManager.connectionTrouble.on();
            });

            this.connection.onSend(function() {
                statusManager.busy.on();
            });

            this.connection.onReceive(function() {
                statusManager.busy.off();
            });

            logger.info('bridge loaded!');
        },

        dispose: function() {
            this.connection.shutdown();
            this.dispose = Function.NOOP;
        }
    });
});

window.logger = new Ice.Log.Logger([ 'window' ]);
window.console && window.console.firebug ? new Ice.Log.FirebugLogHandler(window.logger) : new Ice.Log.WindowLogHandler(window.logger, window);

window.onLoad(function() {
    window.documentSynchronizer = new Ice.Document.Synchronizer(window.logger);
});

window.onKeyPress(function(e) {
    if (e.isEscKey()) e.cancelDefaultAction();
});
