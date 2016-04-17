package com.usergo.presentit;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.yet_another_test_1261.presentIt.model.Person;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.usergo.presentit.helpers.AccountUtils;
import com.usergo.presentit.helpers.CoursesFragment;
import com.usergo.presentit.helpers.PresentItException;
import com.usergo.presentit.helpers.PresentItUtils;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private String pEmailAccount;
    private AuthorizationCheckTask pAuthTask;

    GoogleApiClient pGoogleApiClient;
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private ProfileFragment pProfileFragment;
    /**
     * Activity result indicating a return from the Google account selection intent.
     */
    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pEmailAccount = AccountUtils.getEmailAccount(this);
        Log.i("mainactivity", "getemailaccount-" + pEmailAccount);


        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.navList);
        if(mDrawerList==null)
            Log.d("ERROR", null);

        mActivityTitle = "Profile";
        // mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setTitle(mActivityTitle);

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        ProfileFragment fragment = new ProfileFragment();
//        Bundle args = new Bundle();
//        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.linearLayoutFragment, fragment)
//                .commit();
//
//        // Highlight the selected item, update the title, and close the drawer
//        mDrawerList.setItemChecked(0, true);
//        mActivityTitle = mAdapter.getItem(0);
//        mDrawerLayout.closeDrawer(mDrawerList);
//        LayoutInflater inflater = getLayoutInflater();
//        LinearLayout container = (LinearLayout) findViewById(R.id.linearLayoutFragment);
//        inflater.inflate(R.layout.activity_main, container);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main, ProfileFragment.newInstance(MainActivity.this));
        tx.commit();
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Options");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void addDrawerItems() {
        String[] menuArray = { "Profile", "Courses", "Classrooms" };
        mAdapter = new ArrayAdapter<>(this,R.layout.drawer_listview_item , menuArray);
        mDrawerList.setAdapter(mAdapter);

        /* TODO: why to add these 2 lines */
        mDrawerList.bringToFront();
        mDrawerLayout.requestLayout();

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private void signIn() {
        Log.i("mainactivity", "pemailaccount-" + null + "entered signIn()");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(pGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pAuthTask != null) {
            pAuthTask.cancel(true);
            pAuthTask = null;
        }
    }

    protected void onResume() {
        super.onResume();

        if (null != pEmailAccount) {
            performAuthCheck(pEmailAccount);
        } else {
            selectAccount();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_account:
                new AlertDialog.Builder(MainActivity.this).setTitle(null)
                        .setMessage(getString(R.string.clear_account_message))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                AccountUtils.saveEmailAccount(MainActivity.this, null);
                                pEmailAccount = null;
                                dialog.cancel();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();

                break;
            case R.id.action_reload:
                //mConferenceListFragment.reload();
                break;
        }
        // Activate the navigation drawer toggle
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /*
     * Selects an account for talking to Google Play services. If there is more than one account on
     * the device, it allows user to choose one.
     */
    private void selectAccount() {
        Account[] accounts = AccountUtils.getGoogleAccounts(this);
       // String emailAccount = AccountUtils.getEmailAccount(this);
        int numOfAccount = accounts.length;
        Log.i("selectAccount", "number of google accounts "+numOfAccount);
        switch (numOfAccount) {
            case 0:
                // No accounts registered, nothing to do.
                Toast.makeText(this, R.string.toast_no_google_accounts_registered,
                        Toast.LENGTH_LONG).show();
    Log.i("mainactivity", "pemailaccount-" + null + "entering signIn()");
                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                // Build a GoogleApiClient with access to the Google Sign-In API and the
                // options specified by gso.
                pGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                signIn();
                break;
            case 1:
                pEmailAccount = accounts[0].name;
                performAuthCheck(pEmailAccount);
                break;
            default:
                // More than one Google Account is present, a chooser is necessary.
                // Invoke an {@code Intent} to allow the user to select a Google account.
                Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                        new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                        getString(R.string.select_account_for_access), null, null, null);
                startActivityForResult(accountSelector, ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == RESULT_OK) {
            // This path indicates the account selection activity resulted in the user selecting a
            // Google account and clicking OK.
            pEmailAccount = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        }
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else {
            finish();
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("STATE", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Log.i("MainActivity", "Authenticated Username:" + acct.getDisplayName());
                AccountUtils.saveEmailAccount(MainActivity.this, acct.getEmail());
            }
        } else {
            Log.e("MainActivity", "Failed to authenticate user");
            Toast.makeText(this, "Failed to authenticate user",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Schedule the authorization check.
     */
    private void performAuthCheck(String email) {
        // Cancel previously running tasks.
        if (pAuthTask != null) {
            pAuthTask.cancel(true);
        }

        // Start task to check authorization.
        pAuthTask = new AuthorizationCheckTask();
        pAuthTask.execute(email);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        
    }

    /**
     * Verifies OAuth2 token access for the application and Google account combination with
     * the {@code AccountManager} and the Play Services installed application. If the appropriate
     * OAuth2 access hasn't been granted (to this application) then the task may fire an
     * {@code Intent} to request that the user approve such access. If the appropriate access does
     * exist then the button that will let the user proceed to the next activity is enabled.
     */
    private class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {

        private final static boolean SUCCESS = true;
        private final static boolean FAILURE = false;
        private Exception mException;

        @Override
        protected Boolean doInBackground(String... emailAccounts) {
            Log.i("MainActivity", "Background task started.");

            if (!AccountUtils.checkGooglePlayServicesAvailable(MainActivity.this)) {
                publishProgress(R.string.gms_not_available);
                return FAILURE;
            }

            String emailAccount = emailAccounts[0];
            Log.i("backgroundmain", emailAccount);
            // Ensure only one task is running at a time.
            pAuthTask = this;

            // Ensure an email was selected.
            if (TextUtils.isEmpty(emailAccount)) {
                publishProgress(R.string.toast_no_google_account_selected);
                return FAILURE;
            }

            pEmailAccount = emailAccount;
            AccountUtils.saveEmailAccount(MainActivity.this, emailAccount);

            return SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Integer... stringIds) {
            // Toast only the most recent.
            Integer stringId = stringIds[0];
            Toast.makeText(MainActivity.this, getString(stringId), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            pAuthTask = this;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Authorization check successful, get conferences.
               // AccountUtils.build(MainActivity.this, pEmailAccount);
                Log.i("MainActivity", "Email id - " + pEmailAccount);
               // PresentItUtils.build(MainActivity.this, pEmailAccount);
                //loadProfileDetails();
            } else {
                // Authorization check unsuccessful.
                pEmailAccount = null;
                if (mException != null) {
                    AccountUtils.displayNetworkErrorMessage(MainActivity.this);
                }
            }
            pAuthTask = null;
        }

//        private void loadProfileDetails() {
//            try {
//                Person user = PresentItUtils.getPersonProfile();
//
//                TextView txtUserName = (TextView) findViewById(R.id.user_display_name);
//                txtUserName.setText(user.getFirstName() + " " + user.getLastName());
//                TextView txtEmailAddress = (TextView) findViewById(R.id.user_email);
//                txtEmailAddress.setText(user.getEmail());
//                TextView txtUfid = (TextView) findViewById(R.id.user_ufid);
//                txtUfid.setText(user.getUfid());
//
//            } catch (PresentItException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        @Override
        protected void onCancelled() {
            pAuthTask = null;
        }
    }//

    private class DrawerItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
           // Toast.makeText(MainActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
            //mDrawerLayout.closeDrawer(mDrawerList);
//            LayoutInflater inflater = getLayoutInflater();
//            LinearLayout container = (LinearLayout) findViewById(R.id.linearLayoutFragment);
//            inflater.inflate(R.layout.activity_main, container);
            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            //Log.d("info", "enetred switch position" + position);
            switch(position) {
                case 0:
                    tx.replace(R.id.main, ProfileFragment.newInstance(MainActivity.this));
                break;
                case 1: {
                    tx.replace(R.id.main, CoursesFragment.newInstance(MainActivity.this));
                }
                break;
                default:
                    break;
            }
            tx.commit();
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
//        ProfileFragment fragment = new ProfileFragment();
//        Bundle args = new Bundle();
//        //args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.linearLayoutFragment, fragment)
//                        .commit();
//
//        // Highlight the selected item, update the title, and close the drawer
//        mDrawerList.setItemChecked(position, true);
//        mActivityTitle = mAdapter.getItem(position);
//        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
