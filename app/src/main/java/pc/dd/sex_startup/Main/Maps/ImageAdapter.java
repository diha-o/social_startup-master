package pc.dd.sex_startup.Main.Maps;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.maps.android.clustering.Cluster;
import com.squareup.picasso.Picasso;

import pc.dd.sex_startup.Main.Helpers.MyItemContainer;
import pc.dd.sex_startup.R;

/**
 * Created by UserData on 13.07.2016.
 */

public class ImageAdapter  extends BaseAdapter {
    private Context mContext;
    private Cluster<MyItemContainer> clusterm;
    private String[] images_url;
    private String[] userUid;
    private Boolean first_create = true;
    private ImageView imageView = null;
    private  GridView.LayoutParams params = null;
    public ImageAdapter(Context c,Cluster<MyItemContainer> clusterm) {
        mContext = c;
        this.clusterm = clusterm;
        this.images_url = get_images_url(clusterm);
        this.userUid = get_user_uid(clusterm);
    }

    public int getCount() {
        return images_url.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            int width = metrics.widthPixels / 2;
            int height = metrics.heightPixels / 4;
            imageView = new ImageView(mContext);

            params= new GridView.LayoutParams(width, height);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setContentDescription(userUid[position]);
        } else {
            imageView = (ImageView) convertView;
        }


        Picasso.with(mContext)
                .load(images_url[position])
                .placeholder(R.drawable.ic_crop_original_white_24dp_4x)
                .error(R.drawable.ic_errorl_white_24dp_4x)
                .into(imageView);

        return imageView;
    }
    private String[] get_images_url(Cluster<MyItemContainer> clus){
        String[] images = new String[clus.getSize()];
        int i = -1;
        for (MyItemContainer p: clus.getItems()){
            i+=1;
            try {
                images[i] = p.getUrl1();
                //  url = new URL(p.getUrl1());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return images;
    }
    private String[] get_user_uid(Cluster<MyItemContainer> clus){
        String[] uid = new String[clus.getSize()];
        int i = -1;
        for (MyItemContainer p: clus.getItems()){
            i+=1;
            try {
                uid[i] = p.getMyuid();
                //  url = new URL(p.getUrl1());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return uid;
    }

}

