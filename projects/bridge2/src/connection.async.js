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

var send = operator();
var onSend = operator();
var onReceive = operator();
var onServerError = operator();
var whenDown = operator();
var whenTrouble = operator();
var shutdown = operator();

[ Ice.Community.Connection = new Object, Ice.Connection, Ice.Reliability.Heartbeat ].as(function(This, Connection, Heartbeat) {
    This.AsyncConnection = function(logger, sessionID, viewID, configuration, commandDispatcher) {
        var logger = logger.child('async-connection');
        var sendChannel = Client(true);
        var receiveChannel = Client(true);
        var defaultQuery = Query();
        addNameValue(defaultQuery, 'ice.view', viewID);
        addNameValue(defaultQuery, 'ice.session', sessionID);
        var onSendListeners = [];
        var onReceiveFromSendListeners = [];
        var onReceiveListeners = [];
        var onServerErrorListeners = [];
        var connectionDownListeners = [];
        var connectionTroubleListeners = [];

        var listener = object(function(method) {
            method(close, noop);
            method(abort, noop);
        });
        var listening = object(function(method) {
            method(remove, noop);
        });
        var timeoutBomb = { cancel: Function.NOOP };
        var heartbeat = { stop: Function.NOOP };

        var pingURI = configuration.context.current + 'block/ping';
        var getURI = configuration.context.current + 'block/receive-updates';
        var sendURI = configuration.context.current + 'block/send-receive-updates';
        var receiveURI = configuration.context.async + 'block/receive-updated-views';

        //clear connectionDownListeners to avoid bogus connection lost messages
        onBeforeUnload(window, function() {
            connectionDownListeners = [];
        });

        var timeout = configuration.timeout ? configuration.timeout : 60000;

        var serverErrorCallback = onServerErrorListeners.broadcaster();
        var receiveCallback = function(response) {
            try {
                onReceiveListeners.broadcast(response);
            } catch (e) {
                logger.error('receive broadcast failed', e);
            }
        };
        var sendXWindowCookie = noop;
        var receiveXWindowCookie = function (response) {
            var xWindowCookie = getHeader(response, "X-Set-Window-Cookie");
            if (xWindowCookie) {
                sendXWindowCookie = function(request) {
                    setHeader(request, "X-Window-Cookie", xWindowCookie);
                };
            }
        };

        //read/create cookie that contains the updated views
        var updatedViews = lookupCookie('updates', function() {
            return Cookie('updates', '');
        });

        //register command that handles the updated-views message
        register(commandDispatcher, 'updated-views', function(message) {
            logger.info("Views update: " + value(updatedViews));
            var views = split(value(updatedViews), ' ');
            var text = message.firstChild;
            if (text && !blank(text.data)) {
                update(updatedViews, join(asSet(concatenate(views, split(text.data, ' '))), ' '));
            } else {
                logger.warn("No updated views were returned.");
            }
        });


        //remove the blocking connection marker so that everytime a new
        //bridge instance is created the blocking connection will
        //be re-established
        //this strategy is mainly employed to fix the window.onunload issue
        //in Opera -- see http://jira.icefaces.org/browse/ICE-1872
        try {
            listening = lookupCookie('bconn');
            remove(listening);
        } catch (e) {
            //do nothing
        }

        //build up retry actions
        var timedRetryAbort = function (retryAction, abortAction, timeouts) {
            var index = 0;
            var errorCallbacks = timeouts.inject([abortAction], function(callbacks, interval) {
                callbacks.unshift(retryAction.delayFor(interval));
                return callbacks;
            });
            return function() {
                if (index < errorCallbacks.length) {
                    errorCallbacks[index].apply(this, arguments);
                    index++;
                }
            };
        };

        var connect = function() {
            logger.debug("closing previous connection...");
            close(listener);
            logger.debug("connect...");
            listener = postAsynchronously(receiveChannel, receiveURI, function(q) {
                each(window.sessions, curry(addNameValue, q, 'ice.session'));
            }, function(request) {
                FormPost(request);
                //sendXWindowCookie(request);
            }, $witch(function (condition) {
                condition(OK, function(response) {
                    if (notEmpty(contentAsText(response))) {
                        receiveCallback(response);
                    }
                    //receiveXWindowCookie(response);
                    if (getHeader(response, 'X-Connection') != 'close') {
                        connect();
                    }
                });
                condition(ServerInternalError, retryOnServerError);
            }));
        };

        //build callbacks only after this.connection function was defined
        var retryOnServerError = timedRetryAbort(connect, serverErrorCallback, configuration.serverErrorRetryTimeouts || [1000, 2000, 4000]);

        //avoid error messages for 'pong' messages that arrive after blocking connection is closed
        register(commandDispatcher, 'pong', Function.NOOP);
        //heartbeat setup
        var heartbeatInterval = configuration.heartbeat.interval ? configuration.heartbeat.interval : 50000;
        var heartbeatTimeout = configuration.heartbeat.timeout ? configuration.heartbeat.timeout : 30000;
        var heartbeatRetries = configuration.heartbeat.retries ? configuration.heartbeat.retries : 3;
        var initializeConnection = function() {
            //stop the previous heartbeat instance
            heartbeat.stop();
            heartbeat = new Heartbeat(heartbeatInterval, heartbeatTimeout, logger);
            heartbeat.onPing(function(ping) {
                //re-register a pong command on every ping
                register(commandDispatcher, 'pong', function() {
                    ping.pong();
                });
                postAsynchronously(sendChannel, pingURI, function(q) {
                    addQuery(q, defaultQuery);
                }, FormPost, noop);
            });

            heartbeat.onLostPongs(connectionDownListeners.broadcaster(), heartbeatRetries);
            heartbeat.onLostPongs(connectionTroubleListeners.broadcaster());
            heartbeat.onLostPongs(function() {
                logger.debug('retry to connect...');
                connect();
            });

            heartbeat.start();
            connect();
        };

        //monitor if the blocking connection needs to be started
        var pollingPeriod = 1000;
        var fullViewID = sessionID + ':' + viewID;
        var leaseCookie = lookupCookie('ice.lease', function() {
            return Cookie('ice.lease', asString((new Date).getTime()));
        });
        var connectionCookie = listening = lookupCookie('bconn', function() {
            return Cookie('bconn', '-');
        });
        function updateLease() {
            update(leaseCookie, (new Date).getTime() + pollingPeriod * 2);
        }
        function isLeaseExpired() {
            return asNumber(value(leaseCookie)) < (new Date).getTime();
        }
        function shouldEstablishBlockingConnection() {
            return !existsCookie('bconn') || !startsWith(lookupCookieValue('bconn'), sessionID);
        }
        function offerCandidature() {
            update(connectionCookie, fullViewID);
        }
        function isWinningCandidate() {
            return startsWith(value(connectionCookie), fullViewID);
        }
        function markAsOwned() {
            update(connectionCookie, fullViewID + ':acquired');
        }
        function hasOwner() {
            return endsWith(value(connectionCookie), ':acquired');
        }
        var blockingConnectionMonitor = function() {
            if (shouldEstablishBlockingConnection()) {
                offerCandidature();
                logger.info('blocking connection not initialized...candidate for its creation');
            } else {
                if (isWinningCandidate()) {
                    if (!hasOwner()) {
                        markAsOwned();
                        //start blocking connection since no other view has started it
                        initializeConnection();
                    }
                    updateLease();
                }
                if (hasOwner() && isLeaseExpired()) {
                    offerCandidature();
                    logger.info('blocking connection lease expired...candidate for its creation');
                }
            }
        }.repeatExecutionEvery(pollingPeriod);

        var pickUpdates = function() {
            postAsynchronously(sendChannel, getURI, function(q) {
                addQuery(q, defaultQuery);
            }, FormPost, $witch(function(condition) {
                condition(OK, function(response) {
                    if (notEmpty(contentAsText(response))) receiveCallback(response);
                });
            }));
        };

        //pick any updates that might be generated in between bridge re-initialization
        //todo: replace heuristic with more exact solution
        pickUpdates.delayExecutionFor(pollingPeriod);

        //monitor & pick updates for this view
        var updatesMonitor = function() {
            try {
                var views = split(value(updatedViews), ' ');
                if (contains(views, fullViewID)) {
                    pickUpdates();
                    update(updatedViews, join(complement(views, [ fullViewID ]), ' '));
                }
            } catch (e) {
                logger.warn('failed to listen for updates', e);
            }
        }.repeatExecutionEvery(300);

        logger.info('asynchronous mode');

        return object(function(method) {
            method(send, function(self, query) {
                timeoutBomb.cancel();
                timeoutBomb = connectionDownListeners.broadcaster().delayExecutionFor(timeout);
                broadcast(onSendListeners);
                logger.debug('send > ' + sendURI);
                postAsynchronously(sendChannel, sendURI, function(q) {
                    addQuery(q, query);
                    addQuery(q, defaultQuery);
                    addNameValue(q, 'ice.focus', window.currentFocus);
                    logger.debug(asURIEncodedString(q));
                }, FormPost, $witch(function(condition) {
                    condition(OK, function(response, request) {
                        timeoutBomb.cancel();
                        receiveCallback(response);
                        broadcast(onReceiveFromSendListeners, arguments);
                    });

                    condition(ServerInternalError, serverErrorCallback);
                }));
            });

            method(onSend, function(self, sendCallback, receiveCallback) {
                onSendListeners.push(sendCallback);
                if (receiveCallback) onReceiveFromSendListeners.push(receiveCallback);
            });

            method(onReceive, function(self, callback) {
                onReceiveListeners.push(callback);
            });

            method(onReceive, function(self, callback) {
                onReceiveListeners.push(callback);
            });

            method(onServerError, function(self, callback) {
                onServerErrorListeners.push(callback);
            });

            method(whenDown, function(self, callback) {
                connectionDownListeners.push(callback);
            });

            method(whenTrouble, function(self, callback) {
                connectionTroubleListeners.push(callback);
            });

            method(shutdown, function(self) {
                try {
                    //shutdown once
                    method(shutdown, noop);
                    //avoid sending XMLHTTP requests that might create new sessions on the server
                    method(send, noop);
                    connect = noop;
                    heartbeat.stop();
                } catch (e) {
                    //ignore, we really need to shutdown
                } finally {
                    each([onSendListeners, onReceiveListeners, connectionDownListeners, onServerErrorListeners, onReceiveFromSendListeners], empty);
                    abort(listener);
                    [ updatesMonitor, blockingConnectionMonitor ].eachWithGuard(function(monitor) {
                        monitor.cancel();
                    });
                    remove(listening);
                }
            });
        });
    };
});

