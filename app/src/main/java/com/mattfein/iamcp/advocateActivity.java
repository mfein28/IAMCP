package com.mattfein.iamcp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mattfein.iamcp.adapters.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class advocateActivity extends AppCompatActivity {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    public List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    public static Boolean allRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advocate);
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        View currentView = this.findViewById(android.R.id.content).getRootView();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fbUser = mAuth.getCurrentUser();

        final String fbUserEmail = fbUser.getEmail();

        listView = (ExpandableListView) currentView.findViewById(R.id.advocateList);
        initAdapter();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash, fbUserEmail);
        listView.setAdapter(listAdapter);
    }



    private void initAdapter(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("Calling your Representative");
        listDataHeader.add("Emailing your Representative");
        listDataHeader.add("Writing an Op-Ed");
        listDataHeader.add("Scheduling a meeting.");

        List<String> call = new ArrayList<>();
        call.add("test");

        List<String> email = new ArrayList<>();
        email.add("Example Text");

        List<String> opEd = new ArrayList<>();
        opEd.add("Example Text");

        List<String> meeting = new ArrayList<>();
        meeting.add("Example Text");

        listHash.put(listDataHeader.get(0), call);
        listHash.put(listDataHeader.get(1), email);
        listHash.put(listDataHeader.get(2), opEd);
        listHash.put(listDataHeader.get(3), meeting);

    }
}
