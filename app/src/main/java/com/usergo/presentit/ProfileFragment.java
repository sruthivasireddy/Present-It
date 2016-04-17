package com.usergo.presentit;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.appspot.yet_another_test_1261.presentIt.model.Person;
import com.usergo.presentit.helpers.AccountUtils;
import com.usergo.presentit.helpers.PresentItException;
import com.usergo.presentit.helpers.PresentItUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by sruth on 4/16/2016.
 */
public class ProfileFragment extends Fragment {

//    public Person mPersonProfile;
//    public static ProfileFragment newInstance() {
//        ProfileFragment f = new ProfileFragment();
//        Bundle b = new Bundle();
//        f.setArguments(b);
//        return f;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.profile_fragment, container, false);
//        new ProfileLoadAsyncTask().execute();
//
//        if(mPersonProfile!= null && view != null)
//        {
//            TextView txtUserName = (TextView) container.findViewById(R.id.user_display_name);
//            txtUserName.setText(mPersonProfile.getFirstName() + " " + mPersonProfile.getLastName());
//            TextView txtEmailAddress = (TextView) container.findViewById(R.id.user_email);
//            txtEmailAddress.setText(mPersonProfile.getEmail());
//            TextView txtUfid = (TextView) container.findViewById(R.id.user_ufid);
//            txtUfid.setText(mPersonProfile.getUfid());
//        }
//        return view;
//    }
//
//    @Override
//    public Loader<Person> onCreateLoader(int id, Bundle args) {
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Person> loader, Person data) {
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Person> loader) {
//
//    }
//
//    class ProfileLoadAsyncTask extends AsyncTask<Void, Void, Void> {
//        private Exception mException;
//        ProgressDialog dialog;
//
//        public ProfileLoadAsyncTask() {
//            mPersonProfile = null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            dialog = ProgressDialog.show(getActivity(), "", "Loading....");
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//                try {
//                    if (AccountUtils.getEmailAccount(getActivity())!=null) {
//                        Person profile = PresentItUtils.getPersonProfile();
//                    } else {
//                       return null;
//                    }
//                } catch (IOException e) {
//                    mException = e;
//                } catch (PresentItException e) {
//                    e.printStackTrace();
//                }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            dialog.dismiss();
//        }
//    }


    public Person mPersonProfile;

    public static Fragment newInstance(Context context) {
        ProfileFragment f = new ProfileFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.profile_fragment, null);

        new ProfileLoadAsyncTask().execute();
        if(mPersonProfile != null) {
            Log.d("success", "entered not null and filling properly");
            TextView txtUserName = (TextView) root.findViewById(R.id.user_display_name);
            txtUserName.setText(mPersonProfile.getFirstName() + " " + mPersonProfile.getLastName());
            TextView txtEmailAddress = (TextView) root.findViewById(R.id.user_email);
            txtEmailAddress.setText(mPersonProfile.getEmail());
            TextView txtUfid = (TextView) root.findViewById(R.id.user_ufid);
            txtUfid.setText(mPersonProfile.getUfid());
            return root;
        }
        else
        {
            Log.d("fail", "entered null and filling default");
            TextView txtUserName = (TextView) root.findViewById(R.id.user_display_name);
            txtUserName.setText("Sru");
            TextView txtEmailAddress = (TextView) root.findViewById(R.id.user_email);
            txtEmailAddress.setText("abc@example.com");
            TextView txtUfid = (TextView) root.findViewById(R.id.user_ufid);
            txtUfid.setText("1213434");
            return root;
        }
    }

    class ProfileLoadAsyncTask extends AsyncTask<Void, Void, Void> {
        private Exception mException;
        ProgressDialog dialog;

        public ProfileLoadAsyncTask() {
            mPersonProfile = null;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(), "", "Loading....");
        }

        @Override
        protected Void doInBackground(Void... params) {
                try {
                    if (AccountUtils.getEmailAccount(getActivity())!=null) {
                        Log.d("email" , AccountUtils.getEmailAccount(getActivity()));
                        PresentItUtils.build(getActivity(), AccountUtils.getEmailAccount(getActivity()));
                        Person profile = PresentItUtils.getPersonProfile();
                    } else {
                       return null;
                    }
                } catch (IOException e) {
                    mException = e;
                } catch (PresentItException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
        }
    }
}
