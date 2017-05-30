package com.vasyaevstropov.runmanager;

import android.content.Intent;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vasyaevstropov.runmanager.Models.ChatMessage;

import java.text.SimpleDateFormat;

public class ChatActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    private EditText edit;
    private RelativeLayout activity_chat;
    private ListView listOfMessage;
    private  Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initToolBar();


        activity_chat = (RelativeLayout) findViewById(R.id.chatActivity);
        edit = (EditText) findViewById(R.id.editTextMessage);
        listOfMessage = (ListView) findViewById(R.id.listViewChat);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(edit.getText().toString(),
                            FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                    edit.setText("");

                Snackbar.make(activity_chat, "Введите сообщение", Snackbar.LENGTH_SHORT).show();

            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            Snackbar.make(activity_chat, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT).show();

        }
        displayChatMessage();

    }

    private void displayChatMessage() {


        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.chat_card, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                TextView messageText = (TextView) v.findViewById(R.id.tvMessage);
                TextView messageUser = (TextView) v.findViewById(R.id.tvLogin);
                TextView messageTime = (TextView) v.findViewById(R.id.tvTime);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getUserName());

                messageTime.setText(new SimpleDateFormat("HH:mm").format(model.getMessageTime())); //"yyyy-MM-dd HH:mm:ss"
            }
        };
        listOfMessage.setAdapter(adapter);
        listOfMessage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getBaseContext(),position+"", Toast.LENGTH_SHORT).show();
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference();
                db_node.removeValue();

                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(activity_chat, "Succesfully signed in. Welcome!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            } else {
                Snackbar.make(activity_chat, "We couldn't sign you in. Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_chat, "Yout signed out.", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });

        }
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

}
