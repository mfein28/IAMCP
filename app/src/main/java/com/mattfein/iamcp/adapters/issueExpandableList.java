package com.mattfein.iamcp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
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

import java.util.HashMap;
import java.util.List;

public class issueExpandableList extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;
    private String userEmail;
    private static final String POLICYCOUNT = "isPolicyRead";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public issueExpandableList(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap, String userEmail) {
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
            image.setImageResource(R.drawable.whitespace);
            listHeader.setTextColor(context.getResources().getColor(R.color.white));
            card.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
        }
        if(groupPosition == 1){
            image.setImageResource(R.drawable.csed);
            listHeader.setTextColor(context.getResources().getColor(R.color.white));
            card.setCardBackgroundColor(context.getResources().getColor(R.color.colorLight));
        }
        if(groupPosition == 2){
            image.setImageResource(R.drawable.ipgraphic);
            card.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            listHeader.setTextColor(context.getResources().getColor(R.color.white));
        }
        if(groupPosition == 3){
            image.setImageResource(R.drawable.dataprivacy);
            card.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
            listHeader.setTextColor(context.getResources().getColor(R.color.white));
        }

        listHeader.setText(headerTitle);
        return convertView;
    }

    public void iteratePolicy(){
        final DocumentReference query = db.collection("Task").document(userEmail);
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Long currentStatus = (Long) documentSnapshot.get(POLICYCOUNT);
                currentStatus = currentStatus + 1;
                query.update(POLICYCOUNT, currentStatus);


            }
        });

    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        CardView card;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.whitespaceslay, null);
            TextView content = (TextView) convertView.findViewById(R.id.content);
            TextView title = (TextView) convertView.findViewById(R.id.issueTitle);
            TextView content2 = (TextView) convertView.findViewById(R.id.content2);
            ImageView contentImage = (ImageView) convertView.findViewById(R.id.issueImage);
            card = (CardView) convertView.findViewById(R.id.moreinforCard);


        }
        TextView content = (TextView) convertView.findViewById(R.id.content);
        TextView title = (TextView) convertView.findViewById(R.id.issueTitle);
        TextView content2 = (TextView) convertView.findViewById(R.id.content2);
        ImageView contentImage = (ImageView) convertView.findViewById(R.id.issueImage);
        card = (CardView) convertView.findViewById(R.id.moreinforCard);
        if(groupPosition == 0){
            iteratePolicy();
           content.setText(context.getResources().getString(R.string.tv_whitespace));
           title.setText(context.getResources().getString(R.string.white_title));
           content2.setText(context.getResources().getString(R.string.tv_whitespacetwo));
           card.setCardBackgroundColor(context.getResources().getColor(R.color.gray));
           contentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.whitespacegraphic));
        }
        if(groupPosition == 1){
            iteratePolicy();
            content.setText(context.getResources().getString(R.string.cs_ed));
            title.setText(context.getResources().getString(R.string.cs_title));
            card.setCardBackgroundColor(context.getResources().getColor(R.color.colorLight));
            content2.setText(" ");
            contentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.csedimage));


        }
        if(groupPosition == 2){
            iteratePolicy();
            content.setText(context.getResources().getString(R.string.intellectualprop));
            title.setText(context.getResources().getString(R.string.iptitle));
            content2.setText(" ");
            card.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            contentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.intellectualprop));


        }
        if(groupPosition == 3){
            iteratePolicy();
            content.setText(context.getResources().getString(R.string.dataPrivacy1));
            title.setText(context.getResources().getString(R.string.dataprivacytitle));
            content2.setText(context.getResources().getString(R.string.microsoftwarrantcase));
            contentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.cloudact));
            card.setCardBackgroundColor(context.getResources().getColor(R.color.gray));

        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
