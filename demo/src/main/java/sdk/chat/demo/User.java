package sdk.chat.demo;

import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser {

    public User(String id) {
        this.id = id;
    }

    String id;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return id;
    }

    @Override
    public String getAvatar() {
        return id;
    }
}
