package sdk.chat.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import butterknife.BindView;
import butterknife.ButterKnife;
import firestream.chat.message.Body;
import firestream.chat.message.Sendable;
import firestream.chat.message.TextMessage;
import firestream.chat.namespace.Fire;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import sdk.guru.common.DisposableMap;
import sdk.guru.common.Event;
import sdk.guru.common.RX;

import static firestream.chat.message.TextMessage.TextKey;

public class ChatActivity extends Activity {

    @BindView(R.id.messagesList)
    MessagesList messagesList;
    @BindView(R.id.input)
    MessageInput input;

    MessagesListAdapter<Message> adapter;

    String otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        otherUserId = getIntent().getStringExtra("id");
        if (otherUserId == null || otherUserId.isEmpty()) {
            finish();
        }

        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        adapter = new MessagesListAdapter<>(Fire.stream().currentUserId(), new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                QRGEncoder qrgEncoder = new QRGEncoder(url, null, QRGContents.Type.TEXT, 200);
                imageView.setImageBitmap(qrgEncoder.getBitmap());
            }
        });
        messagesList.setAdapter(adapter);

        input.setInputListener(input -> {
            //validate and send message
            Fire.stream().sendMessageWithText(otherUserId, input.toString(), s -> {
                Sendable sendable = new Sendable(s, Fire.stream().currentUserId());
                TextMessage message = TextMessage.fromSendable(sendable);
                message.getBody().put(TextKey, input.toString());
                MessageMemoryStore.instance.addMessage(otherUserId, message);
                RX.main().scheduleDirect(this::reload);
            }).subscribe();
            return true;
        });

        Disposable d = Fire.stream().getSendableEvents().getMessages().observeOn(RX.main()).subscribe(messageEvent -> {
            if (messageEvent.isAdded()) {
                MessageMemoryStore.instance.addMessage(otherUserId, messageEvent.get());
            }
            reload();
        });

        reload();

    }

    public void reload() {
        adapter.clear();
        for (Sendable s: MessageMemoryStore.instance.sendablesForUser(otherUserId)) {
            adapter.addToStart(new Message(s), true);
        }
    }

}
