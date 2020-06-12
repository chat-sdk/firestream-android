package sdk.chat.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import firestream.chat.message.Sendable;

public class MessageMemoryStore {

    public static final MessageMemoryStore instance = new MessageMemoryStore();
    Map<String, List<Sendable>> messages = new HashMap<>();

    public void addMessage(String userId, Sendable sendable) {
        List<Sendable> list = messages.get(userId);
        if (list == null) {
            list = new ArrayList<>();
            messages.put(userId, list);
        }
        for (Sendable s: list) {
            if (s.getId().equals(sendable.getId())) {
                return;
            }
        }
        list.add(sendable);
    }

    public List<Sendable> sendablesForUser(String userId) {
        List<Sendable> sendables = messages.get(userId);
        if (sendables == null) {
            return new ArrayList<>();
        }
        return messages.get(userId);
    }

}
