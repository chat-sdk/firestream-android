package sdk.chat.demo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import butterknife.BindView;
import butterknife.ButterKnife;
import firestream.chat.chat.User;
import firestream.chat.firebase.service.Keys;
import firestream.chat.message.Message;
import firestream.chat.message.Sendable;
import firestream.chat.namespace.Fire;
import firestream.chat.types.ContactType;
import io.reactivex.functions.Consumer;
import sdk.guru.common.DisposableMap;
import sdk.guru.common.Event;
import sdk.guru.common.RX;

public class StartActivity extends Activity {

    DisposableMap dm = new DisposableMap();

    List<String> users = new ArrayList<>();

    private static final int REQUEST_CODE_QR_SCAN = 101;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.imageView)

    ImageView imageView;
    @BindView(R.id.scanButton)
    Button scanButton;
    @BindView(R.id.showButton)
    Button showButton;

    @BindView(R.id.textView)
    TextView textView;

    DialogsList dialogsList;
    protected DialogsListAdapter<Thread> dialogsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate();

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            // We are connecting for the first time
            if (firebaseAuth.getCurrentUser() == null) {
                // Login Anonymously
                firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    setId(Fire.stream().currentUserId());
                });
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                setId(Fire.stream().currentUserId());
            }
        });

        imageView.setVisibility(View.INVISIBLE);

        showButton.setOnClickListener(v -> {
            if (imageView.getVisibility() == View.INVISIBLE) {
                showQR();
                showButton.setText("Hide QR Code");
            } else {
                showButton.setText("Show QR Code");
                imageView.setVisibility(View.INVISIBLE);
            }
        });

        scanButton.setOnClickListener(v -> {

            try {
                try {
                    Dexter.withActivity(this)
                            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.VIBRATE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    if (report.areAllPermissionsGranted()) {
                                        Intent i = new Intent(StartActivity.this,QrCodeActivity.class);
                                        startActivityForResult( i,REQUEST_CODE_QR_SCAN);
                                    } else {
                                        Logger.debug("Dexter Error" + new Date().getTime());
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions1, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();
                } catch (Exception e) {
                    Logger.error(e);
                }

            } catch (Exception e) {
                Logger.error(e);
            }

        });

        dialogsList = findViewById(R.id.dialogsList);
        dialogsListAdapter = new DialogsListAdapter<>(R.layout.item_dialog, (imageView, url, payload) -> {
            try {
                QRGEncoder qrgEncoder = new QRGEncoder(url, null, QRGContents.Type.TEXT, 200);
                imageView.setImageBitmap(qrgEncoder.getBitmap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        dialogsListAdapter.setOnDialogViewClickListener((view, dialog) -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("id", dialog.getId());
            this.startActivity(intent, new Bundle());
        });

        dialogsList.setAdapter(dialogsListAdapter);

        // Firestream
        dm.add(Fire.stream().getContactEvents().observeOn(RX.main()).subscribe(userEvent -> {
            if(userEvent.isAdded()) {
                users.add(userEvent.get().getId());
            }
            if(userEvent.isRemoved()) {
                users.remove(userEvent.get().getId());
            }
            reloadList();
        }));

        dm.add(Fire.stream().getSendableEvents().getMessages().observeOn(RX.main()).subscribe(messageEvent -> {
            if (messageEvent.isAdded()) {
                if (!messageEvent.get().getId().equals(Fire.stream().currentUserId())) {
                    MessageMemoryStore.instance.addMessage(messageEvent.get().getFrom(), messageEvent.get());
                    addUser(messageEvent.get().getFrom());
                }
                reloadList();
            }
        }));

    }

    public void setId(String id) {
        textView.setText("Id: " + id);
    }

    public void showQR() {
        imageView.setVisibility(View.VISIBLE);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {
            QRGEncoder qrgEncoder = new QRGEncoder(uid, null, QRGContents.Type.TEXT, 200);
            imageView.setImageBitmap(qrgEncoder.getBitmap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if( result!=null)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(StartActivity.this).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if(requestCode == REQUEST_CODE_QR_SCAN)
        {
            if(data==null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            addUser(result);

        }
    }

    public void addUser(String uid) {
        Fire.stream().addContact(new User(uid), ContactType.contact()).subscribe();
    }

    public void reloadList() {
        dialogsListAdapter.clear();
        for (String id: users) {
            dialogsListAdapter.addItem(new Thread(id));
        }
        dialogsListAdapter.notifyDataSetChanged();
    }

}
