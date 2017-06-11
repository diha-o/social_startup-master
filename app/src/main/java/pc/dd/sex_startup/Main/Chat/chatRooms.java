package pc.dd.sex_startup.Main.Chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.wangyuwei.loadingview.LoadingView;
import pc.dd.sex_startup.Main.Helpers.UserData;
import pc.dd.sex_startup.Main.Helpers.StatusBarFragment;
import pc.dd.sex_startup.R;

/**
 * Created by UserData on 14.08.2016.
 */

public class chatRooms extends Activity {

    private DatabaseReference root;
    private FirebaseDatabase database;
    public FirebaseAuth auth;
    private ChatInfo ChatInfo = new ChatInfo();
    private String user_name = null;
    private String img_prof = null;
    private String msg = null;
    private String uid = null;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;

    private ArrayList<String> temp_image_profile = new ArrayList<>();
    private ArrayList<String> temp_msg_array = new ArrayList<>();
    private ArrayList<String> temp_nick = new ArrayList<>();
    private ArrayList<String> temp_parent_uid = new ArrayList<>();

    private ArrayList<String> image_profile = new ArrayList<>();
    private ArrayList<String> msg_array = new ArrayList<>();
    private ArrayList<String> nick = new ArrayList<>();
    private ArrayList<String> parent_uid = new ArrayList<>();

    private  String[] image_string;
    private  String[] nick_string;
    private  String[] msg_string;
    private  String[] p_uid;
    private chatListViewAdapter adapter;
    private FirebaseUser user;

    private ArrayList<String> all_uid = new ArrayList<>();
    private ArrayList<String> success_uid = new ArrayList<>();
    private UserData us ;
    private  int end_of_itteration;
    private LoadingView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms);
        //progress bar
        progressBar = (LoadingView) findViewById(R.id.loading_view);
       // progressBar.setIndeterminateDrawable(nougatBoot);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.start();
        //setstatus bar
        StatusBarFragment status_bar = new StatusBarFragment();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.statbar_container_chatRooms,status_bar)
                .commit();
        Bundle args = new Bundle();
        args.putInt("visibility", View.GONE);
        args.putInt("icon",2);
        status_bar.setArguments(args);

        listView = (ListView) findViewById(R.id.listView);
        //arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_rooms);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            us=new UserData();
            database = FirebaseDatabase.getInstance();
                    get_all_user(database.getReference());
                    fast_get_innfouser(database.getReference(user.getUid()));
        }




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                start_new_act(us, success_uid.get(i));

            }
        });
    }



    private void  start_new_act(UserData me, String uid){
        Intent intent = new Intent(getApplicationContext(),pc.dd.sex_startup.Main.Chat.mainChat.class);
        intent.putExtra("second_uid",uid);
        intent.putExtra("user_name",me.getNickname());//fixxnyt
        intent.putExtra("img_prof",me.getUrl());// need fix
        intent.putExtra("root",database.getReference(uid).toString());
        intent.putExtra("from_chatRooms","0");

        startActivity(intent);
        this.overridePendingTransition(R.animator.animation_act_left,R.animator.animation_act_right);

        this.finish();
    }
    private void get_all_user(DatabaseReference reference) {
        all_uid = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //progress show
                progressBar.setVisibility(View.VISIBLE);
                end_of_itteration = 0;
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    all_uid.add((String) messageSnapshot.getKey());
                    end_of_itteration+=1;
                }

                root = database.getReference(user.getUid()).child("chat");

                for (int i = 0;i<all_uid.size();i++) {
                    get_adapter_v2(root, all_uid.get(i));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }

    private int start_itteration = 0;
    public void get_adapter_v2(DatabaseReference reference , final String second_child){

        reference.child(second_child).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                start_itteration +=1;
                if (dataSnapshot.hasChildren()) {

                    success_uid.add(dataSnapshot.getKey()); //second user uid for chat room

                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {

                        msg = (String) messageSnapshot.child("msg")
                                .getValue(); //need only last

                        user_name = (String) messageSnapshot.child("name")
                                .getValue(); //second user name


                        img_prof = (String) messageSnapshot.child("img_prof")
                                    .getValue(); //second user img

                        uid = (String) messageSnapshot.child("this_user_uid")
                                    .getValue(); //second user uid, like not me


                        if ((!user_name.equals(us.nickname))&&(!temp_nick.contains(user_name))) {
                            nick.add(user_name);
                            parent_uid.add(uid);
                            image_profile.add(img_prof);
                            temp_nick.add(user_name);
                        }else
                        {
//
                        }

                    }

                    msg_array.add(msg);
                }
                if(start_itteration == end_of_itteration){
                    create_adapter();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
private void create_adapter(){
    //creating adapter
    //main data to create adapter
    msg_string = new String[msg_array.size()];
    msg_string = msg_array.toArray(msg_string);

    nick_string = new String[nick.size()];
    nick_string = nick.toArray(nick_string);

    image_string = new String[image_profile.size()];
    image_string = image_profile.toArray(image_string);

    p_uid = new String[parent_uid.size()];
    p_uid = parent_uid.toArray(p_uid);


    adapter = new chatListViewAdapter(chatRooms.this, android.R.layout.simple_list_item_1, image_string, msg_string, nick_string); //создали класс адаптера и закинули туда все что нам нужно
    listView.setAdapter(adapter);
    progressBar.setVisibility(View.GONE);
}

    private void fast_get_innfouser(DatabaseReference myRef) {

        final Integer[] temp = {1};
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        if (temp[0] == 1) {

                            //  us[0] = messageSnapshot.child("UserData data: ").getValue(UserData.class);
                            String url = (String) messageSnapshot.child("url").getValue();
                            String nickname = (String) messageSnapshot.child("nickname").getValue();
                            us.setNickname(nickname);
                            us.setUrl(url);

                            temp[0] = 2;
                            continue;
                        } else {
                        }
                        ;

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    class ChatInfo {

        public String img_prof;
        public String msg;
        public String name;

        private ChatInfo() {
        }

        public ChatInfo(String name, String msg,String img_prof) {
            this.img_prof = img_prof;
            this.msg = msg;
            this.name = name;
        }
        public String getImg_prof() {
            return img_prof;
        }

        public String getMsg() {
            return msg;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public void onBackPressed() {
//        Intent i = new Intent(chatRooms.this, pc.dd.sex_startup.LogIn.Maps.Map.class);
//        startActivity(i);
       this.finish();
    }

}

