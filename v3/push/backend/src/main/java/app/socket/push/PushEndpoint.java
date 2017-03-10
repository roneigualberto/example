/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.socket.push;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import java.util.stream.Collectors;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;

/**
 *
 * @author gladson
 */
@ServerEndpoint(value = "/push/{channel}", encoders = PushMessageEncoder.class, decoders = PushMessageDecoder.class)
public class PushEndpoint {

    private static final ConcurrentLinkedQueue<Session> peers = new ConcurrentLinkedQueue<>();

    private static final Logger logger = getLogger(PushEndpoint.class.getName());

    @OnOpen
    public void open(final Session session, EndpointConfig c, @PathParam("channel") String channel) {
        peers.add(session);
        session.getUserProperties().put("channel", channel);
    }

    @OnMessage
    public void onMessage(final Session session, final PushMessage message, @PathParam("channel") String channel) {

        if (message.getEvent().equalsIgnoreCase("login")) {
            if (message.getData() != null && !message.getData().isEmpty()) {
                session.getUserProperties().put("user", message.getData());
                logger.log(Level.FINE, "Logged - ", message.getData());
            }
        }

        if (message.getEvent().equalsIgnoreCase("count")) {
            PushMessage mm = new PushMessage("count", count());
            sendTo(new Gson().toJson(mm), channel);
            logger.log(Level.FINE, "count - ", message.getData());
        }

        if (message.getEvent().equalsIgnoreCase("channel")) {
            PushMessage mm = new PushMessage("channel", count(channel));
            sendTo(new Gson().toJson(mm), channel);
            logger.log(Level.FINE, "count - ", message.getData());
        }

        if (message.getEvent().equalsIgnoreCase("group")) {
            if (message.getData() != null && !message.getData().isEmpty()) {
                session.getUserProperties().put("group", message.getData());
                logger.log(Level.FINE, "In group - ", message.getData());
            }
        }

        if (message.getEvent().equalsIgnoreCase("identity")) {
            if (message.getData() != null && !message.getData().isEmpty()) {
                session.getUserProperties().put("identity", message.getData());
                logger.log(Level.FINE, "identity - ", message.getData());
            }
        }

        if (message.getEvent().equalsIgnoreCase("logout")) {
            session.getUserProperties().remove("user");
            logger.log(Level.FINE, "Log off - ", message.getData());
        }

    }

    @OnError
    public void onError(final Session session, Throwable t) {
        peers.remove(session);
        logger.log(Level.SEVERE, "onError failed - Session: " + session.getId(), t);
    }

    @OnClose
    public void closedConnection(final Session session, @PathParam("channel") String channel) {
        peers.remove(session);
    }

    public String count() {
        return "" + peers.size();
    }

    public String count(String term) {
        return "" + peers.parallelStream().filter(s -> s.getUserProperties().containsValue(term)).count();
    }

    public List<String> listUsers(String recipient) {
        List<String> list = new ArrayList<>();
        peers.parallelStream().filter(s -> s.getUserProperties().containsValue(recipient)).forEach((s) -> {
            if (s.isOpen()) {
                if (s.getUserProperties().get("user") != null) {
                    list.add((String) s.getUserProperties().get("user"));
                }
            } else {
                peers.remove(s);
            }
        });
        return list;
    }

//    public Map<String, String> listChannels() {
//        return peers.parallelStream().filter(s -> s.getUserProperties().containsKey("channel"))
//	        .collect(Collectors.toMap(s -> s.getUserProperties().getKey(), p -> s.getUserProperties().getValue()));
//    }
    
    public void sendTo(final String texto, final String recipient) {
        peers.parallelStream().filter(s -> s.getUserProperties().containsValue(recipient)).forEach((s) -> {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(texto);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "sendToUser failed " + s.getId() + " " + recipient + " " + texto, e);
                }
            } else {
                peers.remove(s);
            }
        });
    }

    public void sendToSession(final String texto, final String sessionID) {
        peers.parallelStream().filter(s -> s.getId().equalsIgnoreCase(sessionID)).forEach((s) -> {
            if (s.isOpen()) {
                    try {
                        s.getBasicRemote().sendText(texto);
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "sendToSessions failed " + s.getId() + " " + sessionID + " " + texto, e);
                    }
            } else {
                peers.remove(s);
            }
        });
    }

    public void sendToSessions(final String texto) {
        peers.parallelStream().forEach((s) -> {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(texto);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "sendToSessions failed " + s.getId() + " " + texto, e);
                }
            } else {
                peers.remove(s);
            }
        });
    }

}
