package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.View;
import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.SessionExpired;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.MimeTypeMatcher;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.ServerProxy;
import com.icesoft.faces.webapp.http.common.standard.OKResponse;
import com.icesoft.faces.webapp.http.common.standard.PathDispatcherServer;
import com.icesoft.faces.webapp.http.common.standard.ResponseHandlerServer;
import com.icesoft.faces.webapp.http.core.DisposeBeans;
import com.icesoft.faces.webapp.http.core.DisposeViews;
import com.icesoft.faces.webapp.http.core.MultiViewServer;
import com.icesoft.faces.webapp.http.core.PageTest;
import com.icesoft.faces.webapp.http.core.PushServerDetector;
import com.icesoft.faces.webapp.http.core.ReceivePing;
import com.icesoft.faces.webapp.http.core.ReceiveSendUpdates;
import com.icesoft.faces.webapp.http.core.RequestVerifier;
import com.icesoft.faces.webapp.http.core.ResourceDispatcher;
import com.icesoft.faces.webapp.http.core.SendUpdates;
import com.icesoft.faces.webapp.http.core.SingleViewServer;
import com.icesoft.faces.webapp.http.core.UploadServer;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.net.messaging.MessageHandler;
import com.icesoft.net.messaging.MessageServiceClient;
import com.icesoft.util.IdGenerator;
import com.icesoft.util.MonitorRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class MainSessionBoundServlet extends PathDispatcher implements PageTest {
    private static final Log Log = LogFactory.getLog(MainSessionBoundServlet.class);
    private static final String ResourcePrefix = "/block/resource/";
    private static final String ResourceRegex = ".*" + ResourcePrefix.replaceAll("\\/", "\\/") + ".*";
    private static final SessionExpired SessionExpired = new SessionExpired();
    private static final Server OKServer = new ResponseHandlerServer(OKResponse.Handler);
    private static final Runnable NOOP = new Runnable() {
        public void run() {
        }
    };
    private final Runnable drainUpdatedViews = new Runnable() {
        public void run() {
            allUpdatedViews.removeAll(synchronouslyUpdatedViews);
            if (!allUpdatedViews.isEmpty()) {
                Log.warn(allUpdatedViews + " views have accumulated updates");
            }
            allUpdatedViews.clear();
        }
    };
    private final Map views = Collections.synchronizedMap(new HashMap());
    private final ViewQueue allUpdatedViews = new ViewQueue();
    private final Collection synchronouslyUpdatedViews = new HashSet();
    private final String sessionID;
    private boolean pageLoaded = false;
    private Runnable shutdown;

    public MainSessionBoundServlet(final HttpSession session, final SessionDispatcher.Monitor sessionMonitor, IdGenerator idGenerator, MimeTypeMatcher mimeTypeMatcher, MonitorRunner monitorRunner, Configuration configuration, final MessageServiceClient messageService, final String blockingRequestHandlerContext, final Authorization authorization) {
        this.sessionID = restoreOrCreateSessionID(session, idGenerator);
        ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);
        final ResourceDispatcher resourceDispatcher = new ResourceDispatcher(ResourcePrefix, mimeTypeMatcher, sessionMonitor, configuration);
        final Server viewServlet;
        final Server disposeViews;
        final MessageHandler handler;
        if (configuration.getAttributeAsBoolean("concurrentDOMViews", false)) {
            viewServlet = new MultiViewServer(session, sessionID, sessionMonitor, views, allUpdatedViews, configuration, resourceDispatcher, blockingRequestHandlerContext, authorization);
            if (messageService == null) {
                disposeViews = new DisposeViews(sessionID, views);
                handler = null;
            } else {
                disposeViews = OKServer;
                handler = new DisposeViewsHandler();
                handler.setCallback(new DisposeViewsHandler.Callback() {
                    public void disposeView(final String sessionIdentifier, final String viewIdentifier) {
                        if (sessionID.equals(sessionIdentifier)) {
                            View view = (View) views.remove(viewIdentifier);
                            if (view != null) {
                                view.dispose();
                            }
                        }
                    }
                });
                messageService.addMessageHandler(handler, MessageServiceClient.PUSH_TOPIC_NAME);
            }
        } else {
            viewServlet = new SingleViewServer(session, sessionID, sessionMonitor, views, allUpdatedViews, configuration, resourceDispatcher, blockingRequestHandlerContext, authorization);
            disposeViews = OKServer;
            handler = null;
        }

        final Server sendUpdatedViews;
        final Server sendUpdates;
        final Server receivePing;
        if (configuration.getAttributeAsBoolean("synchronousUpdate", false)) {
            //drain the updated views queue if in 'synchronous mode'
            allUpdatedViews.onPut(drainUpdatedViews);
            sendUpdatedViews = OKServer;
            sendUpdates = OKServer;
            receivePing = OKServer;
        } else {
            //setup blocking connection server
            sendUpdatedViews = new RequestVerifier(sessionID, new PushServerDetector(session, sessionID, synchronouslyUpdatedViews, allUpdatedViews, monitorRunner, configuration, messageService, this));
            sendUpdates = new RequestVerifier(sessionID, new SendUpdates(configuration, views, this));
            receivePing = new RequestVerifier(sessionID, new ReceivePing(views, this));
        }

        Server upload = new UploadServer(views, configuration);
        Server receiveSendUpdates = new RequestVerifier(sessionID, new ReceiveSendUpdates(views, synchronouslyUpdatedViews, sessionMonitor, this));

        dispatchOn(".*block\\/receive\\-updated\\-views$", new EnvironmentAdaptingServlet(sendUpdatedViews, configuration, session.getServletContext()));
        PathDispatcherServer dispatcherServer = new PathDispatcherServer();
        dispatcherServer.dispatchOn(".*block\\/send\\-receive\\-updates$", receiveSendUpdates);
        dispatcherServer.dispatchOn(".*block\\/receive\\-updates$", sendUpdates);
        dispatcherServer.dispatchOn(".*block\\/ping$", receivePing);
        dispatcherServer.dispatchOn(".*block\\/dispose\\-views$", disposeViews);
        dispatcherServer.dispatchOn(ResourceRegex, resourceDispatcher);
        dispatcherServer.dispatchOn(".*uploadHtml", upload);
        dispatcherServer.dispatchOn(".*", new ServerProxy(viewServlet) {
            public void service(Request request) throws Exception {
                pageLoaded = true;
                super.service(request);
            }
        });
        dispatchOn(".*", new BasicAdaptingServlet(dispatcherServer));
        shutdown = new Runnable() {
            public void run() {
                //avoid running shutdown more than once
                shutdown = NOOP;
                //dispose session scoped beans
                DisposeBeans.in(session);
                //send 'session-expired' to all views
                Iterator i = views.values().iterator();
                while (i.hasNext()) {
                    CommandQueue commandQueue = (CommandQueue) i.next();
                    commandQueue.put(SessionExpired);
                }
                ContextEventRepeater.iceFacesIdDisposed(session, sessionID);
                //shutdown all contained servers
                MainSessionBoundServlet.super.shutdown();
                //dispose all views
                Iterator viewIterator = views.values().iterator();
                while (viewIterator.hasNext()) {
                    View view = (View) viewIterator.next();
                    view.dispose();
                }

                if (handler != null) {
                    //todo: introduce NOOP handler
                    messageService.removeMessageHandler(handler, MessageServiceClient.PUSH_TOPIC_NAME);
                }
            }
        };
    }

    private static String restoreOrCreateSessionID(HttpSession session, IdGenerator idGenerator) {
        String key = MainSessionBoundServlet.class.getName();
        Object o = session.getAttribute(key);
        if (o == null) {
            String id = idGenerator.newIdentifier();
            session.setAttribute(key, id);
            return id;
        } else {
            return (String) o;
        }
    }

    public void shutdown() {
        shutdown.run();
    }

    public boolean isLoaded() {
        return pageLoaded;
    }

    public Map getViews() {
        return views;
    }

    //Exposing queues for Tomcat 6 Ajax Push
    public ViewQueue getAllUpdatedViews() {
        return allUpdatedViews;
    }

    public Collection getSynchronouslyUpdatedViews() {
        return synchronouslyUpdatedViews;
    }

    public String getSessionID() {
        return sessionID;
    }
}
