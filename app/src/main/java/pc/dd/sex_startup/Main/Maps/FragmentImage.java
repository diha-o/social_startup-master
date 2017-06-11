package pc.dd.sex_startup.Main.Maps;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;

import java.lang.reflect.Field;

import pc.dd.sex_startup.Main.Helpers.MyItemContainer;
import pc.dd.sex_startup.Main.Helpers.UserData;
import pc.dd.sex_startup.R;

/**
 * Created by UserData on 10.07.2016.
 */

public class FragmentImage extends Fragment {
    private final Cluster<MyItemContainer> clusterm;
    private DatabaseReference root;
    private FirebaseDatabase database;
    public FirebaseAuth auth;

    public FragmentImage(Cluster<MyItemContainer> clusterm) {
        this.clusterm = clusterm;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootV = inflater.inflate(R.layout.fragment_image,container,false);
        GridView gridview = (GridView) rootV.findViewById(R.id.fragment_imageviewG);
        gridview.setNumColumns(2);
        gridview.setColumnWidth(GridView.AUTO_FIT);
        gridview.setVerticalSpacing(0);
        gridview.setHorizontalSpacing(0);
        gridview.setAdapter(new ImageAdapter(rootV.getContext(),clusterm));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                auth = FirebaseAuth.getInstance();
                                                 FirebaseUser user = auth.getCurrentUser();
                                                if (user != null) {
                                                    database = FirebaseDatabase.getInstance();
                                                    root = database.getReference(view.getContentDescription().toString());
                                                    //createChat(view.getContentDescription().toString(),null);
                                                    getInfoAboutUser(database.getReference(user.getUid()), view.getContentDescription().toString());
                                                }

                                            }
        });
        return rootV;
    }

    private void createChat(String userUid, String img_profile, String nick) {
        Intent intent = new Intent(getActivity().getApplicationContext(),pc.dd.sex_startup.Main.Chat.mainChat.class);
        intent.putExtra("second_uid",userUid);
        intent.putExtra("user_name",nick);
        intent.putExtra("img_prof",img_profile);
        intent.putExtra("root",root.toString());
        intent.putExtra("from_chatRooms","1");
        startActivity(intent);

    }

    private void getInfoAboutUser(DatabaseReference myRef, final String userUid) {
        final UserData[] user = new UserData[1];
        final Integer[] temp = {1};
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    if(temp[0] == 1){
                    user[0] = messageSnapshot.child("UserData data: ").getValue(UserData.class);
                    String url = (String) messageSnapshot.child("url").getValue();
                    String nickname = (String) messageSnapshot.child("nickname").getValue();
                    createChat(userUid,url,nickname);
                        temp[0] = 2;
                    break;
                    } else {};
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
