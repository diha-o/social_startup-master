package pc.dd.sex_startup.Main.Helpers;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItemContainer implements ClusterItem {
    private final LatLng mPosition;
    private String url1;
    private String myuid;

    public MyItemContainer(double lat, double lng, String url, String uid) {
        mPosition = new LatLng(lat, lng);
        url1 = url;
        myuid = uid;
    }



    @Override
    public LatLng getPosition() {
        return mPosition;
    }
    public String getUrl1(){
        return url1;
    }
    public String getMyuid() {
        return myuid;
    }
}
