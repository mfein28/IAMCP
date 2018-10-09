package com.mattfein.iamcp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mattfein.iamcp.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;


public class ExpandableListAdapter extends BaseExpandableListAdapter implements Serializable{
    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;
    private String userEmail;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private static final String ADVOCACYCOUNT = "isAdvocacyRead";

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap, String userEmail) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.userEmail = userEmail;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);

        }
        TextView listHeader = (TextView) convertView.findViewById(R.id.listheader);
        ImageView image = (ImageView) convertView.findViewById(R.id.AdvocateImage);
        CardView card = (CardView) convertView.findViewById(R.id.listcard);
        if(groupPosition == 0){
            image.setImageResource(R.drawable.phone);
            listHeader.setTextColor(context.getResources().getColor(R.color.white));
            card.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
        }
        if(groupPosition == 1){
            image.setImageResource(R.drawable.email);
            listHeader.setTextColor(context.getResources().getColor(R.color.white));
            card.setCardBackgroundColor(context.getResources().getColor(R.color.colorLight));
        }
        if(groupPosition == 2){
            image.setImageResource(R.drawable.write);
            card.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            listHeader.setTextColor(context.getResources().getColor(R.color.white));
        }
        if(groupPosition == 3){
            image.setImageResource(R.drawable.date);
            card.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
            listHeader.setTextColor(context.getResources().getColor(R.color.white));
        }

        listHeader.setText(headerTitle);
        return convertView;
    }

    public void iterateAdovocate(){
        final DocumentReference query = db.collection("Task").document(userEmail);
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Long currentStatus = (Long) documentSnapshot.get(ADVOCACYCOUNT);
                currentStatus = currentStatus + 1;
                query.update(ADVOCACYCOUNT, currentStatus);


            }
        });

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandlistitem, null);
        }
        TextView txtChild = (TextView) convertView.findViewById(R.id.ExpandListItem);
        if(groupPosition == 0){
            txtChild.setText(Html.fromHtml((convertView.getResources().getString(R.string.telephoning_gov))));
            iterateAdovocate();
        }
        if(groupPosition == 1){
            txtChild.setText(Html.fromHtml((convertView.getResources().getString(R.string.emailing_gov))));
            iterateAdovocate();
        }
        if(groupPosition == 2){
            txtChild.setText(Html.fromHtml((convertView.getResources().getString(R.string.writing_oped))));
            iterateAdovocate();
        }
        if(groupPosition == 3){

            txtChild.setText(Html.fromHtml((convertView.getResources().getString(R.string.schedule_meet))));
            iterateAdovocate();
        }


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
