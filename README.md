# Firestream Beta
> A lightweight Firebase messaging library from Chat SDK

## Features

1. 1-to-1 Messaging
2. Group chat, roles, moderation
3. Android, iOS, Web and Node.js
2. Fully customisable messages
3. Typing Indicator
4. Delivery receipts
5. User blocking
6. Presence
7. Message history (optional)
7. **Firestore** or **Realtime** database

- [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)
- Commercial use allowed
- Support via Github bug tracker

FireStream supports both **Firestore** and the **Realtime** database. You pay the hosting cost directly to Firebase and have sole access to your data. 

There are many chat systems available, here are the benefits of FireStream:

1. Open Souce - you can review, fork, optimize and audit the code
2. Transparent - all the "server" code is included in the client
3. Full Control - you control the source, you have sole access to the data
4. Powered by Firebase - for a managed solution, it's the best

#### Firebase benefits:

1. Low cost, excellent performance and reliability 
2. Reasonable data usage policy
3. Google isn't going out of business any time soon
4. You have full control over YOUR users' data

## Community

+ **Discord:** If you need support, join our [Server](https://discord.gg/abT5BM4)
+ **Support the project:** [Patreon](https://www.patreon.com/chatsdk) or [Github Sponsors](https://github.com/sponsors/chat-sdk) 🙏 and get access to premium modules
+ **Upvote:** our advert on [StackOverflow](https://meta.stackoverflow.com/questions/394409/open-source-advertising-1h-2020/396154#396154)
+ **Contribute by writing code:** Email the [Contributing
Document](https://github.com/chat-sdk/chat-sdk-ios/blob/master/CONTRIBUTING.md) to [**team@sdk.chat**](mailto:team@sdk.chat)
+ **Give us a star** on Github ⭐
+ **Upvoting us:** [Product Hunt](https://www.producthunt.com/posts/chat-sdk)
+ **Tweet:** about your Chat SDK project using [@chat_sdk](https://mobile.twitter.com/chat_sdk) 

You can also help us by:

+ Providing feedback and feature requests
+ Reporting bugs
+ Fixing bugs
+ Writing documentation

Email us at: [team@sdk.chat](mailto:team@sdk.chat)

We also offer development services we are a team of full stack developers who are Firebase experts.
For more information check out our [consulting site](https://chat-sdk.github.io/hire-us/). 

  
## Setup

### Dependencies

Add the following to your project level `build.gradle` file:

```
allprojects {
    repositories {
        ...
        maven { url "http://dl.bintray.com/chat-sdk/chat-sdk-android" }
    }
}
```

Add the following to your app-level `build.gradle` file:

Enable Java 8:

```
android {
    ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

[ ![Version](https://api.bintray.com/packages/chat-sdk/chat-sdk-android/AudioMessagePro/images/download.svg) ](https://bintray.com/chat-sdk/chat-sdk-android/AudioMessagePro/_latestVersion)

```
dependencies {
    ...
    implementation "sdk.chat:firestream:[version]"

    // And

    implementation "sdk.chat:firestream-realtime:[version]"
    
    // Or
    
    implementation "sdk.chat:firestream-firestore:[version]"
}
```

### Firebase

You will need to add Firebase to your project. You can follow the guide [here](https://firebase.google.com/docs/android/setup).

## Initialise Firestream

If you want to use Firebase Firestore:

```
Fire.stream().initialize(this, new FirestoreService());
```

If you want to use Firebase Realtime:

```
Fire.stream().initialize(this, new RealtimeService());
```

The library will automatically integrate with Firebase's login / logout cycle. 

#### Which is better Firestore or Realtime?
>Firestore and Firebase each has it's own set of strenghts and weaknesses. But here is a short summary.
>
>Firestore is better for:
>
>- Apps with more than 10 million monthly users
>- Apps where realtime latency isn't critical
>- Apps were a lot of users send few messages
>
>Firebase is better for:
>
>- Apps where realtime latency is critical
>- Apps where a few users send a lot of messages

## Hello world!

#### Send a message

```
Fire.stream().sendMessageWithText("userId", "Hello World!").subscribe();
```

#### Receive a message

```
Disposable d = Fire.stream().getSendableEvents().getMessages().pastAndNewEvents().subscribe(messageEvent -> {
    if (messageEvent.isAdded()) {
        // Message received
        String text = messageEvent.get().toTextMessage().getText();
        Logger.debug(text);
    }
    if (messageEvent.isRemoved()) {
        // Message removed
    }
});
```

Breaking it down, the base unit that can be sent is a `Sendable` there are a number of classes that extend from this class: `Message`, `Presence`, `Invitation` etc...
So in the above example, we get message sendables and we request past and future events. We then check the event type, convert the message into a text message and print the result. 

>Note:
>We use the RxAndroid library. This means that any asynchronous tasks are activated lazily, you need to call `subscribe` for the action to be executed.

## Chat Room

#### Make a new Chat Room

```
// Create a new chat room
Fire.stream().createChat("name", "url", new User("1"), new User("2")).subscribe(chat -> {

    // Send a message
    chat.sendMessageWithText("Hello World!");

    // Add a user
    chat.addUser(true, new User("3")).subscribe();

    // Make a user an admin
    chat.setRole(new User("2"), RoleType.admin()).subscribe();
    
    // etc...
});
```

#### Listen for Chat Rooms

```
// Listen for new chat rooms we have been added to
Fire.stream().getChatEvents().pastAndNewEvents().subscribe(chatEvent -> {
    if (chatEvent.isAdded()) {
        IChat chat = chatEvent.get();

        // Get a message listener
        chat.manage(chat.getSendableEvents().getMessages().pastAndNewEvents().subscribe(messageEvent -> {
            String text = messageEvent.get().toTextMessage().getText();
            Logger.debug(text);
        }));
    }
});
```

## Disposing of Disposables

Whenever you call subscribe, you get a disposable. This is used to remove the observer. Managing these disposables can be tricky because you want to keep the references around until the time when you want to dispose of them. 

FireStream has two helpful facilities to handles this:

1. Let the framework manage them for you:

```
Fire.stream().manage(disposable);
``` 

```
chat.manage(disposable);
```

If you ask Firestream to manage a disposable, it will store the reference and then call dispose when the chat disconnects. This usually happens when the user logs out. The same goes for a group chat, if the user leaves the chat, the client will disconnect and the disposables will be disposed. 

2. Manage them yourself:

```
DisposableMap dm = new DisposableMap();

// Add
dm.add(disposable);

// Or add with a key
dm.put(chat.id, disposable);

// Then call to dispose of the default list
dm.dispose();

// Dispose of disposables associated with this key
dm.dispose(key);

// Dispose of all disposables
dm.disposeAll();
```

## Performance and Hosting

FireStream can run on either Firestore or the Realtime Database. 

### Firestore 

*Performance and scalability:*

1. 1 million concurent users ~ 50 million MAU
2. Messages arrive in near realtime

For more details [Firestore usage and limits](https://firebase.google.com/docs/firestore/quotas).

*Hosting Cost:*

1. Free
  1. Up to 50 million monthly users
  2. 600k messages 	
2. $25 per month
  1. Up to 50 million monthly users
  2. 3 million messages 
3. Pay as you go
  1. Up to 50 million monthly users
  2. $1 per 400k messages 

For more details [Firestore pricing](https://firebase.google.com/pricing).

### Realtime database 

*Performance and scalability:*

1. 200k concurrent users ~ 10 million monthly users
2. Messages arrive in realtime

For more details [Realtime database limits](https://firebase.google.com/docs/database/usage/limits).

*Hosting Cost:*

1. Free
  1. Up to 5k monthly users
  2. 50 million messages
2. $25 per month
  1. Up to 10 million monthly users
  2. 100 million messages
3. Pay as you go
  1. Up to 10 million monthly users
  2. $1 per 5 million messages 

For more details [Firebase Pricing Page](https://firebase.google.com/pricing).











