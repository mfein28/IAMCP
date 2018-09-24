package com.mattfein.iamcp;

import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class repActivity extends AppCompatActivity {
    String zipcode;
    ArrayList<Representative> repList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep);
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        setUpAlertDialogue();
    }

    private void setUpAlertDialogue() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(getApplicationContext());
        alert.setMessage("This will be use to find your representatives.");
        alert.setTitle("Enter you Zip Code");

        alert.setView(edittext);
        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                  zipcode = edittext.getText().toString();
                  getInfo(zipcode);

            }
        });

        alert.show();

        Log.e("test", "test");

    }

    private void getInfo(String zipcode) {


        String url = "https://whoismyrepresentative.com/getall_mems.php?zip=" + zipcode + "&output=json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Representative representative = new Representative(
                                object.getString("name"),
                                object.getString("party"),
                                object.getString("state"),
                                object.getString("district"),
                                object.getString("phone"),
                                object.getString("office"),
                                object.getString("link"));
                        repList.add(representative);
                    }
                    String list = repList.toString();
                    Log.e("replist", list);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }




}
