package com.mattfein.iamcp;

        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageInfo;
        import android.content.pm.PackageManager;
        import android.content.pm.Signature;
        import android.os.Build;
        import android.preference.PreferenceManager;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.util.Base64;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.Window;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.FirebaseException;
        import com.google.firebase.FirebaseTooManyRequestsException;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.PhoneAuthCredential;

        import com.google.firebase.firestore.FirebaseFirestore;
        import com.linkedin.platform.APIHelper;
        import com.linkedin.platform.LISessionManager;
        import com.linkedin.platform.errors.LIApiError;
        import com.linkedin.platform.errors.LIAuthError;
        import com.linkedin.platform.listeners.ApiListener;
        import com.linkedin.platform.listeners.ApiResponse;
        import com.linkedin.platform.listeners.AuthListener;
        import com.linkedin.platform.utils.Scope;

        import org.json.JSONException;
        import org.json.JSONObject;
        import org.w3c.dom.Text;

        import java.security.MessageDigest;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Map;
        import java.util.Set;
        import java.util.concurrent.TimeUnit;

        import static android.app.PendingIntent.getActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private TextView statusTextView;
    private TextView detailTextView;
    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private Button registerButton;
    private Button finalRegister;
    private EditText companyNameEditText;

    private static final String ISSUE_AREA = "issueArea";
    private static final String ACTIVITY_TYPE = "activityType";
    private static final String DESCRIPTION_ARRAY = "taskDescription";

    private static final String REPCOUNT = "isRepRead";
    private static final String POLICYCOUNT = "isPolicyRead";
    private static final String ADVOCACYCOUNT = "isAdvocacyRead";


    private static final String Name = "Name";
    private static final String ORGANIZATION_NAME = "BusinessName";
    private static final String USER_EMAIL = "advocateUser";
    private static final String LIProfilePic = "linkedinPro";
    private static final String TIMESTAMP = "TimeStamps";

    public FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        setUpLogin();


    }


    public void setUpLogin() {
        statusTextView = (TextView) findViewById(R.id.statusText);
        detailTextView = (TextView) findViewById(R.id.detailText);
        loginButton = (Button) findViewById(R.id.loginbutton);
        registerButton = (Button) findViewById(R.id.registerbutton);
        finalRegister = (Button) findViewById(R.id.SubmitRegister);
        companyNameEditText = (EditText) findViewById(R.id.CompanyName);
        emailField = (EditText) findViewById(R.id.enteremail);
        passwordField = (EditText) findViewById(R.id.enterpassword);


        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        finalRegister.setOnClickListener(this);


    }


    private void signIn(final String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent startApp = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(startApp);
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void register(final String email, String password, final String companyName) {
        Log.d(TAG, "signIn:" + email);
        final String finalemail = email.toLowerCase();
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "RegisterwithEmail:success");
                            setupUserData(email, companyName);
                        }

                        else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


    private void createAccount(final String email, final String password, final String companyName) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            register(email, password, companyName);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }



        return valid;
    }

    //List of permissions granted on LinkedIn console
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }


    @Override
    protected void onActivityResult(int requestCose, int resultCode, Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCose, resultCode, data);
    }

    private void setupUserData(final String email, final String companyName) {
        Log.e("It reached personal", "yay");
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener(){

                    @Override
                    public void onAuthSuccess() {
                        final String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";
                        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                        apiHelper.getRequest(getApplicationContext(), url, new ApiListener() {
                            @Override
                            public void onApiSuccess(ApiResponse apiResponse) {
                                try {
                                    JSONObject jsonObject = apiResponse.getResponseDataAsJson();
                                    String firstName = jsonObject.getString("firstName");
                                    String lastName = jsonObject.getString("lastName");
                                    String pictureUrl = jsonObject.getString("pictureUrl");
                                    String emailAddress = email;

                                    ArrayList<String> DescriptionArray = new ArrayList<>();
                                    ArrayList<String> ActivityArray = new ArrayList<>();
                                    ArrayList<String> IssueArray = new ArrayList<>();
                                    ArrayList<String> DatesArray = new ArrayList<>();

                                     Map<String, Object> userDetails = new HashMap<>();
                                    userDetails.put(Name, firstName + " " + lastName);
                                    userDetails.put(REPCOUNT, 0);
                                    userDetails.put(POLICYCOUNT, 0);
                                    userDetails.put(ADVOCACYCOUNT, 0);
                                    userDetails.put(USER_EMAIL, emailAddress);
                                    userDetails.put(LIProfilePic, pictureUrl);
                                    userDetails.put(DESCRIPTION_ARRAY,DescriptionArray);
                                    userDetails.put(ACTIVITY_TYPE, ActivityArray);
                                    userDetails.put(ISSUE_AREA, IssueArray);
                                    userDetails.put(ORGANIZATION_NAME, companyName);
                                    userDetails.put(TIMESTAMP, DatesArray);

                                    db.collection("Task").document(email).set(userDetails);

                                    Intent startApp = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(startApp);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }



                            @Override
                            public void onApiError(LIApiError LIApiError) {

                            }
                        });
                    }
            @Override
            public void onAuthError(LIAuthError error) {

            }
        }, true);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.registerbutton) {
            loginButton.setVisibility(View.GONE);
            registerButton.setVisibility(View.GONE);
            finalRegister.setVisibility(View.VISIBLE);
            companyNameEditText.setVisibility(View.VISIBLE);


        }
        if (i == R.id.SubmitRegister) {
            createAccount(emailField.getText().toString().toLowerCase(), passwordField.getText().toString(), companyNameEditText.getText().toString());

        }
        if (i == R.id.loginbutton) {
            signIn(emailField.getText().toString().toLowerCase(), passwordField.getText().toString());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        //Checks if user already signedin
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent startApp = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(startApp);
        }
        // moreLogin(currentUser);
    }
}

