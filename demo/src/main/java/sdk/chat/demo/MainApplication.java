package sdk.chat.demo;

import android.app.Application;

import org.pmw.tinylog.Logger;

import java.io.Console;

import firestream.chat.FirestreamConfig;
import firestream.chat.chat.Chat;
import firestream.chat.chat.User;
import firestream.chat.firestore.FirestoreService;
import firestream.chat.interfaces.IChat;
import firestream.chat.message.Message;
import firestream.chat.namespace.Fire;
import firestream.chat.realtime.RealtimeService;
import firestream.chat.types.RoleType;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import sdk.guru.common.DisposableMap;
import sdk.guru.common.Event;

/**
 * Created by Ben Smiley on 6/8/2014.
 */
//public class MainApplication extends MultiDexApplication {
public class MainApplication extends Application {

    protected DisposableMap dm = new DisposableMap();

    @Override
    public void onCreate() {
        super.onCreate();

        Fire.stream().initialize(this, new FirestreamConfig(this).setRoot("firestream").setSandbox("demo"), new RealtimeService());

//        Fire.stream().sendMessageWithText("userId", "Hello World!");
//
//        Disposable d = Fire.stream().getSendableEvents().getMessages().pastAndNewEvents().subscribe(messageEvent -> {
//            if (messageEvent.isAdded()) {
//                // Message received
//                String text = messageEvent.get().toTextMessage().getText();
//                Logger.debug(text);
//            }
//            if (messageEvent.isRemoved()) {
//                // Message removed
//            }
//        });
//
//        // If you want to stop listening
//        d.dispose();
//
//        // Get the current user's id
//        Fire.stream().currentUserId();
//
//        // Create a new chat room
//        dm.add(Fire.stream().createChat("name", "url", new User("1"), new User("2")).subscribe(chat -> {
//
//            // Send a message
//            chat.sendMessageWithText("Hello World!");
//
//            // Add a user
//            chat.addUser(true, new User("3")).subscribe();
//
//            // Make a user an admin
//            chat.setRole(new User("2"), RoleType.admin()).subscribe();
//
//        }));
//
//        // Listen for new chat rooms we have been added to
//        dm.add(Fire.stream().getChatEvents().pastAndNewEvents().subscribe(chatEvent -> {
//            if (chatEvent.isAdded()) {
//                IChat chat = chatEvent.get();
//
//                // Get a message listener
//                chat.manage(chat.getSendableEvents().getMessages().pastAndNewEvents().subscribe(messageEvent -> {
//                    String text = messageEvent.get().toTextMessage().getText();
//                    Logger.debug(text);
//                }));
//            }
//        }));

    }

}
