package sdk.chat.demo;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.Date;

import firestream.chat.message.Sendable;

public class Message implements IMessage {

    Sendable sendable;

    public Message(Sendable sendable) {
        this.sendable = sendable;
    }

    @Override
    public String getId() {
        return sendable.getId();
    }

    @Override
    public String getText() {
        return sendable.toTextMessage().getText();
    }

    @Override
    public IUser getUser() {
        return new User(sendable.getFrom());
    }

    @Override
    public Date getCreatedAt() {
        return sendable.getDate();
    }
}
