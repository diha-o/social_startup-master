package pc.dd.sex_startup.Main.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import pc.dd.sex_startup.R;

/**
 * Created by UserData on 13.08.2016.
 */

public class mainChat extends FragmentActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private ImageView btn_send_msg;
private  hani.momanii.supernova_emoji_library.Helper.EmojiconEditText emojiconEditText;
    private TextView chat_conversation;

    private String user_name,uid_name;
    private DatabaseReference root ;
    private DatabaseReference second_root ;
    private String temp_key;
    private String img_User;
    private FrameLayout container;
    Boolean second_challenge = false;
    private  FirebaseUser this_user;
    private ListView listView;
    private ChatArrayAdapter chatArrayAdapter;
    private String first_child;
    private String second_child;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        //set status bar


        btn_send_msg = (ImageView) findViewById(R.id.btn_send);
        btn_send_msg.animate().rotationXBy(0.8f).setDuration(100).start();
        listView = (ListView) findViewById(R.id.chat_listView);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.fragment_left_sms);
        listView.setAdapter(chatArrayAdapter);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        emojiconEditText= (hani.momanii.supernova_emoji_library.Helper.EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        ImageView klef = (ImageView) findViewById(R.id.jjjjj);
        EmojIconActions emojIcon=new EmojIconActions(this,findViewById(R.id.chat_rootView),emojiconEditText,klef,"#FF1E0412","#eba5bd","#E6EBEF");
        emojIcon.ShowEmojIcon();


        user_name = getIntent().getExtras().get("user_name").toString();
        uid_name = getIntent().getExtras().get("second_uid").toString();
        img_User = getIntent().getExtras().get("img_prof").toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        this_user = auth.getCurrentUser();
        //String from_Rooms = getIntent().getExtras().get("from_chatRooms").toString();


            first_child = uid_name;
            second_child=this_user.getUid();

        try {
            String second_r = getIntent().getExtras().get("root").toString();
            second_root = FirebaseDatabase.getInstance().getReferenceFromUrl(second_r).child("chat").child(second_child);
            second_challenge = true;
        }catch (Exception e){e.printStackTrace();}


        root = FirebaseDatabase.getInstance().getReference().child(this_user.getUid()).child("chat").child(first_child);

        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                set_key_ref();

                this_user();
                if(second_challenge)
                second_user();
                emojiconEditText.setText("");
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void second_user() {

        DatabaseReference message_root = second_root.child(temp_key);
        Map<String,Object> map2 = new HashMap<String, Object>();
        map2.put("img_prof",img_User);
        map2.put("name",user_name);
        map2.put("msg",emojiconEditText.getText().toString());
        map2.put("this_user_uid",this_user.getUid());
        message_root.updateChildren(map2);
    }
    private void set_key_ref(){

        Map<String,Object> map = new HashMap<String, Object>();
        temp_key = root.push().getKey();
        root.updateChildren(map);
    }
    private void this_user() {

        DatabaseReference message_root = root.child(temp_key);
        Map<String,Object> map2 = new HashMap<String, Object>();
        map2.put("img_prof",img_User);
        map2.put("name",user_name);
        map2.put("msg",emojiconEditText.getText().toString());
        map2.put("this_user_uid",this_user.getUid());
        message_root.updateChildren(map2);
    }

    private String chat_msg,chat_user_name, chat_user_image,chat_user_uid;
    private Boolean left = true;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){

            chat_user_image = (String) ((DataSnapshot)i.next()).getValue();
            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_uid = (String) ((DataSnapshot)i.next()).getValue();
            if(chat_user_name.equals(user_name)){
                left = false;
                chatArrayAdapter.add(new ChatMessage(left, chat_msg,null,null));
            }else{
                left=true;
                chatArrayAdapter.add(new ChatMessage(left,  chat_msg, chat_user_image,chat_user_name));
            }

        }


    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {

    }

        @Override
  public void onBackPressed() {

            Intent i = new Intent(mainChat.this, pc.dd.sex_startup.Main.Chat.chatRooms.class);
            startActivity(i);

            this.overridePendingTransition(R.animator.animation_act_left_back,R.animator.animation_act_right_back);

            this.finish();
    }
}
