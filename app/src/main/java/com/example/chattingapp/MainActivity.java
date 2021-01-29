package com.example.chattingapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    EditText messageTextField;
    Button sendMessage, loginButton;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    LoginUser user = new LoginUser("", false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating objects
        listView = findViewById(R.id.listview);
        messageTextField = findViewById(R.id.editTextTextMultiLine);
        sendMessage = findViewById(R.id.button);
        loginButton = findViewById(R.id.login_btn);

        //set-up click listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(messageTextField.getText().toString())) {
                    messageTextField.setError("Please Enter UserName!");
                } else{
                    user.isUserLogin = true;
                    user.userName = messageTextField.getText().toString();

                    listView.setVisibility(View.VISIBLE);
                    sendMessage.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.INVISIBLE);
                    messageTextField.setText("");
                    messageTextField.setHint("Type your Message");
                }
            }
        });


       sendMessage.setOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

            //Refreshing screen every 1 second = 1000ms
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                GetMessagesFromDb(); }
        };
        timer.schedule(task, 0,1000);

        //hide list view and send button
        listView.setVisibility(View.INVISIBLE);
        sendMessage.setVisibility(View.INVISIBLE);

    }
    public void sendMessage() {
        String messageField = messageTextField.getText().toString();

        if (TextUtils.isEmpty(messageTextField.getText().toString())) {
            messageTextField.setError("Please Enter Message!");
        } else {

            Date date = new Date();
            Message newMessage = new Message(messageField,String.valueOf(date.getTime()), user.userName);
            db.collection("Messages")
                    .add(newMessage)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                            messageTextField.setText(""); // removes text from textField
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error adding document", e);
                        }
                    });
        }
    }
    public void GetMessagesFromDb(){

        ArrayList<Message> messagesFromDb = new ArrayList<>();


        db.collection("Messages")
                .whereGreaterThan("created",user.userLoginTime)
                .orderBy("created")
                 .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                messagesFromDb.add(document.toObject(Message.class) );
                                Log.d("--------------Messages",document.toObject(Message.class).message);
                            }
                          showMessages(messagesFromDb);

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void showMessages(ArrayList<Message> msgfromdb)
    {


        MessageAdapter messageAdapter = new MessageAdapter(msgfromdb,this);
        listView.setAdapter(messageAdapter);

    }
}