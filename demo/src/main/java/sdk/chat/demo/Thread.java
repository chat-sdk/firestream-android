package sdk.chat.demo;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Arrays;
import java.util.List;

import firestream.chat.message.Sendable;
import firestream.chat.namespace.Fire;

public class Thread implements IDialog<Message> {

    String id;
    Message lastMessage;

    public Thread(String id) {
        this.id = id;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return id;
    }

    @Override
    public String getDialogName() {
        return id;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return Arrays.asList(new User(id));
    }

    @Override
    public Message getLastMessage() {
        List<Sendable> sendables = MessageMemoryStore.instance.sendablesForUser(id);
        if (sendables != null && sendables.size() > 0) {
            sendables.get(sendables.size() - 1);
        }
        return null;
    }

    @Override
    public void setLastMessage(Message message) {
        lastMessage = message;
    }

    @Override
    public int getUnreadCount() {
        return 0;
    }
}
