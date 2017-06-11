package pc.dd.sex_startup.Main.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import pc.dd.sex_startup.R;

/**
 * Created by UserData on 19.08.2016.
 */

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private hani.momanii.supernova_emoji_library.Helper.EmojiconTextView chatText;
    private ImageView imageView;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;
    private TextView nickname;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.fragment_left_sms, parent, false);
        }else{
            row = inflater.inflate(R.layout.fragment_right_sms, parent, false);
        }

        chatText = (hani.momanii.supernova_emoji_library.Helper.EmojiconTextView) row.findViewById(R.id.text_left_sms);
        chatText.setText(chatMessageObj.message);



        if(chatMessageObj.image_url !=null) {
            imageView = (ImageView) row.findViewById(R.id.image_left_sms);
            Picasso.with(context)
                    .load(chatMessageObj.image_url)
                    .resize(70,70)
                    .into(imageView);

            nickname = (TextView) row.findViewById(R.id.text_view_nickname);
            nickname.setText(chatMessageObj.nikname);
        } else {
        }

        return row;
    }
}