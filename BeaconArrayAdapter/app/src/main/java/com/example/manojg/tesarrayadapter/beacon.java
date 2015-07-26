package com.example.manojg.tesarrayadapter;

import java.util.ArrayList;
import com.estimote.sdk.Beacon;

/**
 * Created by manojg on 6/22/2015.
 */
public class beacon {
    public String nid;
    public String uuid;
    public String major;
    public String minor;
    public String proximity;

public ArrayList<beacon> getBeacons(){
    beacon one=new beacon();
    one.nid="101";
    one.uuid="jksdfkds";
    one.major="7873";
    one.minor="10";
    one.proximity="200m";
    beacon two=new beacon();
    two.nid="101";
    two.uuid="jksdfkds";
    two.major="7873";
    two.minor="10";
    two.proximity="200m";
    beacon to=new beacon();
    to.nid="101";
    to.uuid="b9407f30-f5f8-466e-aff9-25556b57fe6d";
    to.major="307";
    to.minor="1";
    ArrayList<beacon> beacons=new ArrayList<beacon>();
    beacons.add(one);
    beacons.add(two);
    beacons.add(to);
    beacons.add(to);
    return beacons;
}
}
