package com.mattfein.iamcp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static boolean isVisible;
    public static MainActivity mainActivity;
    private FirebaseMessaging fbm;
    private FirebaseAuth mAuth;

    Button signout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String twitter = "https://twitter.com/IAMCPorg?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor";
    private final String youtube = "https://www.youtube.com/user/IAMCPorg/videos";
    private final String facebook = "https://www.facebook.com/IAMCPInternational/";

    //NewsFeed
    private static final String ISSUE_AREA_News = "IssueAreas";
    private static final String Names_News = "Names";
    private static final String LI_URL_NEWS = "LIUrls";
    private static final String ActivityDescriptions_NEWS = "ActivityDescriptions";

    ConstraintLayout plusFab,  taskLay;
    RelativeLayout feedLayout;
    FloatingActionButton mainFab;
    SwipeRefreshLayout swipeLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isVisible = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = mAuth.getCurrentUser();
        setUpFAB();
        feedLayout = findViewById(R.id.feedLayout);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        showLinkDetails(fbUser, navigationView);
        setUpNewsFeed();
        swipeLayout = findViewById(R.id.swipeRefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setUpNewsFeed();
            }
        });



    }

    private void setUpNewsFeed() {
        DocumentReference query = db.collection("NewsFeed").document("NdhRlwTPvHUH8kerFyU5");
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> currentNameArray = new ArrayList<>();
                List<String> currentLIArray = new ArrayList<>();
                List<String> currentActivityArray = new ArrayList<>();
                List<String> currentIssueArray = new ArrayList<>();
                currentIssueArray = (List<String>) documentSnapshot.get(ISSUE_AREA_News);
                currentIssueArray = Lists.reverse(currentIssueArray);

                currentLIArray = (List<String>) documentSnapshot.get(LI_URL_NEWS);
                currentLIArray = Lists.reverse(currentLIArray);
                currentActivityArray = (List<String>) documentSnapshot.get(ActivityDescriptions_NEWS);
                currentActivityArray = Lists.reverse(currentActivityArray);
                currentNameArray = (List<String>) documentSnapshot.get(Names_News);
                currentNameArray = Lists.reverse(currentNameArray);
                initRecyclerView(currentNameArray, currentLIArray, currentActivityArray, currentIssueArray);
            }
        });

    }

    private void initRecyclerView(List<String> currentNameArray, List<String> currentLIArray, List<String> currentActivityArray, List<String> currentIssueArray){
        Log.d("Main Activity: ", "initRecyclerView");
        RecyclerView recyclerView = findViewById(R.id.newsfeedrecycle);

        NewsFeedAdapter adapter = new NewsFeedAdapter(this, currentNameArray, currentIssueArray, currentActivityArray, currentLIArray);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeLayout.setRefreshing(false);
    }

    //Sets up floating action button

    private void setUpFAB() {

        final Animation showButton = AnimationUtils.loadAnimation(MainActivity.this, R.anim.showbutton);
        final Animation hideButton = AnimationUtils.loadAnimation(MainActivity.this, R.anim.hidebutton);
        final Animation showLay = AnimationUtils.loadAnimation(MainActivity.this, R.anim.show_layout);
        final Animation hideLay = AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_layout);
        mainFab = (FloatingActionButton) findViewById(R.id.plusfabfab);
        taskLay = (ConstraintLayout) findViewById(R.id.taskLay);
        taskLay = (ConstraintLayout) findViewById(R.id.taskLay);
        mainFab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(taskLay.getVisibility() == View.VISIBLE && taskLay.getVisibility() == View.VISIBLE){
                    taskLay.setVisibility(View.GONE);
                    mainFab.startAnimation(showButton);
                    taskLay.startAnimation(hideLay);
                    mainFab.startAnimation(showButton);

                }else{
                    taskLay.setVisibility(View.VISIBLE);
                    taskLay.startAnimation(showLay);
                    mainFab.startAnimation(hideButton);
                }
            }
        });

        taskLay.setOnClickListener(new View.OnClickListener
                () {
            @Override
            public void onClick(View v) {
                taskLay.setVisibility(View.GONE);
                taskLay.startAnimation(hideLay);
                mainFab.startAnimation(showButton);
                Intent task = new Intent(getApplicationContext(), Task.class);
                startActivity(task);




            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "Logging user out", Toast.LENGTH_SHORT).show();
            Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
            mAuth.signOut();
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLinkDetails(FirebaseUser fbUser, final NavigationView navView) {
        String userEmail = fbUser.getEmail();

        final DocumentReference query = db.collection("Task").document(userEmail);
        query.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String name = documentSnapshot.getString("Name");
                    String liURL = documentSnapshot.getString("linkedinPro");
                    String compString = documentSnapshot.getString("BusinessName");
                    View hView = navView.getHeaderView(0);
                    ImageView proPic = (ImageView) hView.findViewById(R.id.linkedinphoto);
                    TextView textInfo = (TextView) hView.findViewById(R.id.username);
                    TextView companyName = (TextView) hView.findViewById(R.id.CompanyName);

                    textInfo.setText(name);
                    companyName.setText(compString);
                    Picasso.get().load(liURL).into(proPic);

                }

            }
        });

    }





    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profile) {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            mainFab.setVisibility(View.GONE);
            taskLay.setVisibility(View.GONE);
            feedLayout.setVisibility(View.GONE);
            taskLay.clearAnimation();
            taskLay.setClickable(false);
            transaction.add(R.id.frame, new Profile()).commit();

        } else if (id == R.id.prepare) {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            mainFab.setVisibility(View.GONE);
            taskLay.setVisibility(View.GONE);
            feedLayout.setVisibility(View.GONE);
            taskLay.clearAnimation();
            taskLay.setClickable(false);
            transaction.add(R.id.frame, new AdvocateDashboard()).commit();


        } else if (id == R.id.events) {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            mainFab.setVisibility(View.GONE);
            feedLayout.setVisibility(View.GONE);
            taskLay.setVisibility(View.GONE);
            taskLay.clearAnimation();
            taskLay.setClickable(false);
            transaction.add(R.id.frame, new Events()).commit();





        } else if (id == R.id.facebook) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(facebook));
        } else if (id == R.id.twitter) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(twitter));
        }
        else if (id == R.id.youtube) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(youtube));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}