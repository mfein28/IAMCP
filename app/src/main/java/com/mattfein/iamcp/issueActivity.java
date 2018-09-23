package com.mattfein.iamcp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;

import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class issueActivity extends AppCompatActivity {

    private ExpandableListView listView;
    private issueExpandableList listAdapter;
    public List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

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
        listAdapter = new issueExpandableList(this, listDataHeader, listHash, fbUserEmail);
        listView.setAdapter(listAdapter);
    }

    private void initAdapter(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        listDataHeader.add("TV White Spaces Broadband");
        listDataHeader.add("Computer Science Education");
        listDataHeader.add("Intellectual Property");
        listDataHeader.add("Data Privacy");

        List<String> whitespace = new ArrayList<>();
        whitespace.add("test");

        List<String> csed = new ArrayList<>();
        csed.add("Example Text");

        List<String> ip = new ArrayList<>();
        ip.add("Example Text");

        List<String> datapriv = new ArrayList<>();
        datapriv.add("Example Text");

        listHash.put(listDataHeader.get(0), whitespace);
        listHash.put(listDataHeader.get(1), csed);
        listHash.put(listDataHeader.get(2), ip);
        listHash.put(listDataHeader.get(3), datapriv);

    }
}
