package pc.dd.sex_startup.Main.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.maps.android.clustering.Cluster;
import com.squareup.picasso.Picasso;

import pc.dd.sex_startup.Main.Helpers.MyItemContainer;
import pc.dd.sex_startup.R;

/**
 * Created by UserData on 15.08.2016.
 */

public class chatListViewAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private Cluster<MyItemContainer> clusterm;
    private String[] images_url;
    private String[] nickname;
    private String[] msg;

    public chatListViewAdapter(Context context, int resource, String[] objects, String[] msg_array,String[] nick) {
        super(context, resource, objects);
        this.images_url = objects;
        this.mContext = context;
        this.nickname = nick;
        this.msg= msg_array;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.fragment_list_view_adapter, null);
        }

        //Integer objects = getItem(position);

        if (images_url != null) {
            ImageView tt1 = (ImageView) v.findViewById(R.id._fragmentImage);
            TextView tt2 = (TextView) v.findViewById(R.id._fragmentNickanme);
            hani.momanii.supernova_emoji_library.Helper.EmojiconTextView tt3 = (hani.momanii.supernova_emoji_library.Helper.EmojiconTextView) v.findViewById(R.id._fragmentDescription);

            if (tt1 != null) {
               // tt1.setImageResource(objects);
                tt2.setText(nickname[position]);
                tt3.setText(msg[position]);
                Picasso.with(mContext)
                        .load(images_url[position])
                        .centerCrop()
                        .resize(90,90)
                        .placeholder(R.drawable.ic_crop_original_white_24dp_2x)
                        .error(R.drawable.ic_errorl_white_24dp_2x)
                        .into(tt1);

            }

        }

        return v;
    }

}
