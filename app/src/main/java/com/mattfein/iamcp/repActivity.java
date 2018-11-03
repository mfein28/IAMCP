package com.mattfein.iamcp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.JsonArray;
import com.mattfein.iamcp.adapters.RepAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class repActivity extends AppCompatActivity {
    String address;
    ArrayList<Representative> repList = new ArrayList<>();
    private LocationManager locationManager;
    private LocationListener locationListener;
    String fbUserEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep);
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser fbUser = mAuth.getCurrentUser();
        fbUserEmail = fbUser.getEmail();
        setUpAlertDialogue(fbUserEmail);
    }




    private void setUpAlertDialogue(String usersEmail) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(getApplicationContext());
        alert.setMessage("It will be used to find your representatives and will not be saved.");
        alert.setTitle("Enter an address:");
        final View alertLayout = getLayoutInflater().inflate(R.layout.alertdialog, null);
        alert.setView(alertLayout);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).setCountry("US")
                .build();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setFilter(typeFilter);
        AlertDialog alertDialog = alert.show();
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        // TODO: Get info about the selected place.
                        CharSequence charSequence = place.getAddress();
                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try
                        {
                            List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude,place.getLatLng().longitude, 1);
                            String address = addresses.get(0).getAddressLine(0);
                            String stringAddress = address;
                            stringAddress = stringAddress.replace(" ", "+");
                            stringAddress = stringAddress.replace(",", "");
                            Log.e("heres address", "Place: " + stringAddress);
                            getInfo(stringAddress, usersEmail);
                            alertDialog.dismiss();


                        } catch (IOException e)
                        {

                            e.printStackTrace();
                        }




                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.i("Error", "An error occurred: " + status);
                    }
                });






        Log.e("test", "test");

    }




    private void getInfo(String address, String userEmail) {
        String url = "https://www.googleapis.com/civicinfo/v2/representatives?address=" + address + "&includeOffices=true&levels=country&roles=legislatorUpperBody&roles=legislatorLowerBody&key=AIzaSyAmmevt501yRRzhSDBk3m8PY1TuQkVLrNg";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
        ArrayList<String> mNames = new ArrayList<>();
        ArrayList<String> mPhones = new ArrayList<>();
        ArrayList<String> mParties = new ArrayList<>();
        ArrayList<String> mPics = new ArrayList<>();
        ArrayList<String> mUrls = new ArrayList<>();
        ArrayList<Map<String, String>> mChannels = new ArrayList<>();
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null) {
                    String kind = jsonObject.optString("kind");
                    JSONObject normalizedInput = jsonObject.optJSONObject("normalizedInput");
                    JSONObject divisions = jsonObject.optJSONObject("divisions");
                    JSONArray offices = jsonObject.optJSONArray("offices");
                    try {
                        JSONArray officials = jsonObject.getJSONArray("officials");
                        for (int i = 0; i < officials.length(); i++) {
                            JSONObject object = officials.getJSONObject(i);
                            JSONArray channels = object.getJSONArray("channels");



                            Map<String, String> channelMap = new HashMap<String, String>();
                            for(int j = 0; j < channels.length(); j++){
                                JSONObject channelObj = channels.getJSONObject(j);
                                String type = channelObj.getString("type");
                                String id = channelObj.getString("id");
                                channelMap.put(type, id);
                                Log.e("CHaneel", channelMap.toString());

                            }
                            String repName = object.getString("name");
                            String repAddress = object.getString("address");
                            String repParty = object.getString("party");
                            String repPhones = object.getString("phones").replace("[","").replace("]","").replace("\"", "");
                            String repUrls = object.getString("urls").replace("[", "").replace("]", "").replace("\\", "");
                            String repPhotoUrl;
                            repPhotoUrl = object.optString("photoUrl");


                            Representative representative = new Representative(repName, repAddress, repParty, repPhones, repUrls, repPhotoUrl, channelMap);
                            Log.e("RepURL", representative.getUrls());
                            Log.e("Party", representative.getParty());
                            repList.add(representative);
                        }
                        String list = repList.toString();
                        int i = 0;
                        while(i < repList.size()){
                            mNames.add(repList.get(i).getName());
                            mPhones.add(repList.get(i).getPhones());
                            mParties.add(repList.get(i).getParty());
                            mPics.add(repList.get(i).getPhotourl());
                            mUrls.add(repList.get(i).getUrls());
                            mChannels.add(repList.get(i).getChannels());
                            i++;

                        }
                        RecyclerView recyclerView = findViewById(R.id.repRecycle);
                        Context context = recyclerView.getContext();
                        RepAdapter repAdapter = new RepAdapter(recyclerView, mUrls, mPics, mNames, mParties, mPhones, mChannels, context, userEmail);
                        recyclerView.setAdapter(repAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });



        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);

    }




}
