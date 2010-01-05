package org.icepush.samples.icechat.gwt.server.service;

import org.icepush.samples.icechat.gwt.client.chat.ChatRoomMessage;
import org.icepush.samples.icechat.gwt.client.service.ChatService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.icepush.samples.icechat.gwt.push.adapter.GWTPushRequestContextAdaptor;
import org.icepush.PushContext;


import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.icepush.samples.icechat.gwt.client.chat.ChatHandleBuilder;
import org.icepush.samples.icechat.gwt.client.chat.ChatMessageBuilder;
import org.icepush.samples.icechat.gwt.client.chat.ChatRoomHandle;

public class ChatServiceImpl extends RemoteServiceServlet implements ChatService {

    private List<String> chatRooms = new ArrayList<String>();
    private HashMap<String, List<String>> participants = new HashMap<String, List<String>>();

    private HashMap<String, List<ChatRoomMessage>> chatMessages  = new HashMap<String, List<ChatRoomMessage>>();

    public void createChatRoom(String name) {
        try {
            this.chatRooms.add(name);
            GWTPushRequestContextAdaptor pushAdaptor = GWTPushRequestContextAdaptor.getInstance(this.getServletContext(), getThreadLocalRequest(), getThreadLocalResponse());
            PushContext pushContext = pushAdaptor.getPushContext();

            pushContext.push("chatRoomIndex");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ChatRoomHandle> getChatRooms() {
        List<ChatRoomHandle> result = new ArrayList<ChatRoomHandle>(this.chatRooms.size());

        for (String chatName : this.chatRooms) {
            ChatHandleBuilder builder = new ChatHandleBuilder();
            ChatRoomHandle handle = builder.createHandle(chatName);
            result.add(handle);
        }
        return result;
    }

    public void joinChatRoom(ChatRoomHandle handle,String username) {
        List<String> participantList = this.participants.get(handle.getName());
        if(participantList == null){
            participantList = new ArrayList<String>();
            this.participants.put(handle.getName(), participantList);
        }

        if(participantList.contains(username)){
            return; //already joined.
        }

        participantList.add(username);

    }

    public List<String> getParticipants(ChatRoomHandle handle) {
        return this.participants.get(handle.getName());
    }

    public void sendMessage(String message, String username, ChatRoomHandle handle) {
        List<ChatRoomMessage> messages = this.chatMessages.get(handle.getName());
        if(messages == null){
            messages = new ArrayList<ChatRoomMessage>();
            this.chatMessages.put(handle.getName(), messages);
        }

        ChatMessageBuilder builder = new ChatMessageBuilder();
        messages.add(builder.createChatMessage(message, username));
    }

    public ChatRoomHandle getMessages(ChatRoomHandle handle) {

        List<ChatRoomMessage> result = new ArrayList<ChatRoomMessage>();
        if(this.chatMessages.get(handle.getName()) == null){
            return handle; //return the unchanged handle.
        }

        for(int i = handle.getMessageIndex(); i < this.chatMessages.get(handle.getName()).size(); i++){
            result.add(this.chatMessages.get(handle.getName()).get(i));
        }

        ChatHandleBuilder builder = new ChatHandleBuilder();
        builder.addMessages(handle, result);
        return handle;
    }
}
