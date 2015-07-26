package com.example.manojg.tesarrayadapter;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;
import java.util.Collection;


public class adapterActivity extends BaseAdapter{

    Context mContext;
    ArrayList<Beacon> actualBeacons =new ArrayList<Beacon>();
    LayoutInflater inflator;
    public adapterActivity(Context context) {
        super();
        mContext=context;
        inflator=LayoutInflater.from(this.mContext);

        this.actualBeacons = new ArrayList<Beacon>();
    }

    @Override
    public int getCount() {
        return actualBeacons.size();
    }

    @Override
    public Beacon getItem(int position) {
        return actualBeacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void replaceWith(Collection<Beacon> newBeacons) {
        this.actualBeacons.clear();
        this.actualBeacons.addAll(newBeacons);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BeaconViewHolder beaconHolder;
        if(convertView ==null){
            convertView=inflator.inflate(R.layout.activity_adapter,null);
            beaconHolder=new BeaconViewHolder();
            convertView.setTag(beaconHolder);
        }
        else
        {
            beaconHolder=(BeaconViewHolder)convertView.getTag();

        }
        beaconHolder.nid=detail(convertView, R.id.nid, actualBeacons.get(position).getMacAddress());
        beaconHolder.uuid=detail(convertView, R.id.uuid, actualBeacons.get(position).getProximityUUID());
        beaconHolder.major=detail(convertView, R.id.major, String.format("%d", actualBeacons.get(position).getMajor()));
        beaconHolder.minor=detail(convertView, R.id.minor, String.format("%d",actualBeacons.get(position).getMinor()));
        beaconHolder.prox=detail(convertView, R.id.proximity, String.format("%d",actualBeacons.get(position).getRssi()));
        beaconHolder.BeaconIcon=detail(convertView, R.id.beaconImage);
        return convertView;
    }
    private  TextView detail(View v, int resid, String text)
    {
        TextView tv=(TextView)v.findViewById(resid);
        tv.setText(text);
        return tv;
    }
    private  ImageView detail(View v, int resid)
    {
        ImageView tv=(ImageView)v.findViewById(resid);
        return tv;
    }

    private static class BeaconViewHolder{
        TextView nid, uuid, major, minor, prox;
        ImageView BeaconIcon;
    }

}
