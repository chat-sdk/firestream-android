package sdk.chat;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

import firestream.chat.firestore.FirestoreService;
import firestream.chat.namespace.Fire;
import firestream.chat.test.TestScript;
import firestream.chat.types.ContactType;

/**
 * Created by Ben Smiley on 6/8/2014.
 */
//public class MainApplication extends MultiDexApplication {
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TestScript ts = new TestScript(new FirestoreService(), this, "firestream");
        ts.onFinish = () -> {
            Fire.stream().block(TestScript.testUser1()).subscribe();
            Fire.stream().addContact(TestScript.testUser1(), ContactType.contact()).subscribe();
        };

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            // We are connecting for the first time
            if (firebaseAuth.getCurrentUser() == null) {
                firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {

                });
            }
        });

    }

}
