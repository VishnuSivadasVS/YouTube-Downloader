package com.codeseasy.youtubedownloader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    String youTubeURL = null;
    String WritePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String ReadPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private SharedPref sharedpref;


    SignInButton signInButton;
    Button signOutButton;
    FirebaseAuth mAuth;
    private int RC_SIGN_IN = 2;
    GoogleSignInClient mGoogleSignInClient;
    ProgressBar progressBar;
    LinearLayout linearLayoutupgradebox, linearLayoutsignoutbox;
    TextView textViewusername;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        sharedpref = new SharedPref(this);
        if(sharedpref.loadNightModeState()==true) {
            setTheme(R.style.darktheme);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, UpdateService.class);
        startService(intent);

        textViewusername = findViewById(R.id.username);
        linearLayoutupgradebox =findViewById(R.id.upgradebox);
        linearLayoutsignoutbox = findViewById(R.id.signoutbox);
        signInButton = findViewById(R.id.sign_in_button);
        signOutButton = findViewById(R.id.sign_out_button);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    linearLayoutupgradebox.setVisibility(View.GONE);
                    linearLayoutsignoutbox.setVisibility(View.VISIBLE);
                    updateUI(mAuth.getCurrentUser());
                }
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                linearLayoutsignoutbox.setVisibility(View.GONE);
                linearLayoutupgradebox.setVisibility(View.VISIBLE);
            }
        });


        if (ActivityCompat.checkSelfPermission(this, WritePermission) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, ReadPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WritePermission, ReadPermission}, 1);
        }
        editText = findViewById(R.id.youtubevideourlenter);

    }

    public void openFolder()
    {
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));

    }

    public void YouTubeVideoDownloadF(int iTag){

        if (ActivityCompat.checkSelfPermission(this, WritePermission) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, ReadPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WritePermission, ReadPermission}, 1);
        } else {
                YTDownload(iTag);
        }
    }

    public void YTDownload(final int itag) {
        String VideoURLDownload = youTubeURL;
        @SuppressLint("StaticFieldLeak") YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, final String videoTitle, SparseArray<YtFile> ytFiles) {
                if ((ytFiles != null)) {
                    String downloadURL = ytFiles.get(itag).getUrl();
                    Log.e("Download URL: ", downloadURL);

                    if (downloadURL != null) {
                        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
                        request.setTitle(videoTitle);
                        request.setDestinationInExternalPublicDir("/Download/YouTube-Downloader/", videoTitle + ".mp4");
                        if (downloadManager != null) {
                            Toast.makeText(getApplicationContext(),"Downloading...",Toast.LENGTH_SHORT).show();
                            downloadManager.enqueue(request);
                        }
                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            public void onReceive(Context ctxt, Intent intent) {
                                Toast.makeText(getApplicationContext(),"Download Completed",Toast.LENGTH_SHORT).show();

                                Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/Download/YouTube-Downloader/");
                                Intent intentop = new Intent(Intent.ACTION_VIEW);
                                intentop.setDataAndType(selectedUri, "resource/folder");

                                if (intentop.resolveActivityInfo(getPackageManager(), 0) != null)
                                {
                                    startActivity(intentop);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Saved on: Download/YouTube-Downloader",Toast.LENGTH_LONG).show();
                                    restartApp();
                                }
                                unregisterReceiver(this);
                                finish();
                            }
                        };
                        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                    }
                } else Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        };
        youTubeUriExtractor.execute(VideoURLDownload);
    }

    public void viewdownloadsbtn(View view) {
        openFolder();
    }

    public void ytvdownload(View view) {
        youTubeURL = editText.getText().toString();
        if (youTubeURL.contains("http"))
        YouTubeVideoDownloadF(18);
        else Toast.makeText(this,"Enter URL First",Toast.LENGTH_LONG).show();
    }

    public void ytvdownloadhd(View view) {
        youTubeURL = editText.getText().toString();
        if(mAuth.getCurrentUser() != null) {
            if (youTubeURL.contains("http"))
                YouTubeVideoDownloadF(22);
            else Toast.makeText(this, "Enter URL First", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Upgrade To Pro", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.day:
            sharedpref.setNightModeState(false);
            restartApp();
            return(true);
        case R.id.night:
            sharedpref.setNightModeState(true);
            restartApp();
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }

    public void restartApp() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
                else
                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                Log.w("Main: ", "Google sign in failed", e);
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Main", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            linearLayoutupgradebox.setVisibility(View.GONE);
                            Log.d("Main: ", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w("Main: ", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "You are not Authenticated", Toast.LENGTH_LONG).show();
                            updateUI(null);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    @SuppressLint("SetTextI18n")
    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            textViewusername.setText("Logged in as: "+personName);
            linearLayoutsignoutbox.setVisibility(View.VISIBLE);
        }
    }
}
