package com.example.chattingapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ListView listView, listView2;
    EditText messageTextField;
    Button sendMessage, loginButton, number_Of_Users;
    boolean button_pressed = false;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    String currentChatRoom = "0";
    Spinner drop_down_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating objects
        number_Of_Users = findViewById(R.id.number_Of_Users);
        listView = findViewById(R.id.listview);
        listView2 = findViewById(R.id.listview2);
        messageTextField = findViewById(R.id.editTextTextMultiLine);
        sendMessage = findViewById(R.id.button);
        loginButton = findViewById(R.id.login_btn);
        drop_down_menu = findViewById(R.id.dropdown_menu);

        //dropdown menu array adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drop_down_menu.setAdapter(adapter);
        drop_down_menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentChatRoom = String.valueOf(position);
                GetMessagesFromDb();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set-up click listeners
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(messageTextField.getText().toString())) {
                    messageTextField.setError("Please Enter UserName!");
                } else{
                    checkIfUserExist();
                }
            }
        });
        number_Of_Users.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                if (button_pressed == false){
                    getActiveUsersDetails();
                    button_pressed = true;
                    return;
                } else {
                    listView2.setVisibility(View.INVISIBLE);
                    button_pressed = false;
                    return;
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
                GetMessagesFromDb();
            }
        };
        timer.schedule(task, 0,1000);

        //hide list view and send button
        drop_down_menu.setVisibility(View.INVISIBLE);
        listView2.setVisibility(View.INVISIBLE);
        number_Of_Users.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);
        sendMessage.setVisibility(View.INVISIBLE);

    }
    public void sendMessage() {
        String messageField = messageTextField.getText().toString();

        if (TextUtils.isEmpty(messageTextField.getText().toString())) {
            messageTextField.setError("Please Enter Message!");
        } else {

            Date date = new Date();
            Message newMessage = new Message(messageField,String.valueOf(date.getTime())
                    , LoginUser.loginUser.userName, currentChatRoom);

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
    public void sendActiveUserDetails() {
            Date date = new Date();
            ActiveUser activeUser = new ActiveUser(LoginUser.loginUser.userName, String.valueOf(date.getTime()));
            db.collection("Users")
                    .add(activeUser)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            LoginUser.loginUser.userId = documentReference.getId();

                            Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                            messageTextField.setText(""); // removes text from textField
                        }
                    }); }
    public void newUserJoined(){
        Date date = new Date();
        String newUserJoined = "~" + LoginUser.loginUser.userName + " Has Joined The Chat ~";
        Message newMessage = new Message(newUserJoined,String.valueOf(date.getTime()),
                LoginUser.loginUser.userName, currentChatRoom);

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
    public void GetMessagesFromDb(){
        ArrayList<Message> messagesFromDb = new ArrayList<>();
        db.collection("Messages")
               // .whereGreaterThan("created",user.userLoginTime)
                .whereGreaterThan("created",LoginUser.loginUser.userLoginTime)
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
    public void getActiveUsersDetails(){
        ArrayList<ActiveUser> activeUsersFromdb = new ArrayList<>();
        db.collection("Users")
                .orderBy("created")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                activeUsersFromdb.add(document.toObject(ActiveUser.class) );
                                Log.d("--------------Users",document.toObject(ActiveUser.class).user);
                            }
                            showUserNames(activeUsersFromdb);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void showUserNames(ArrayList<ActiveUser> activeusersFromdb){
        UserAdapter userAdapter = new UserAdapter(activeusersFromdb, this);
        listView2.setVisibility(View.VISIBLE);
        listView2.setAdapter(userAdapter);
    }
    private void showMessages(ArrayList<Message> msgfromdb)
    {
        ArrayList<Message> filteredMessages = new ArrayList<>();

        for (Message message: msgfromdb) {
            if (message.chatRoom.equals(currentChatRoom)){
                filteredMessages.add(message);
            }
        }
        MessageAdapter messageAdapter = new MessageAdapter(filteredMessages,this);
        listView.setAdapter(messageAdapter);
    }
    public void deleteUser() {
        String activeUserId = LoginUser.loginUser.userId;
        db.collection("Users").document(activeUserId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deleteUser();
    }
    public void checkIfUserExist(){
        ArrayList<ActiveUser> activeUsersFromdb = new ArrayList<>();

        db.collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                activeUsersFromdb.add(document.toObject(ActiveUser.class) );
                                Log.d("--------------Users",document.toObject(ActiveUser.class).user);
                            }
                            compareNames(activeUsersFromdb);
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void compareNames(ArrayList<ActiveUser> activeUsersFromdb){
        String currentUserName = messageTextField.getText().toString();
        boolean userExist = false;

        for (ActiveUser activeUser: activeUsersFromdb){
            if (currentUserName.equals(activeUser.user)){
                userExist = true;
            }
        }
        if (userExist == false){
            LoginUser.loginUser.isUserLogin = true;
            LoginUser.loginUser.userName = messageTextField.getText().toString();

            drop_down_menu.setVisibility(View.VISIBLE);
            listView2.setVisibility(View.INVISIBLE);
            number_Of_Users.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            sendMessage.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            messageTextField.setText("");
            messageTextField.setHint("Type your Message");
            newUserJoined();
            sendActiveUserDetails();
        } else {
            messageTextField.setError("This name is already in use, please enter different name");
        }
    }

}