package com.usergo.presentit;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.appspot.yet_another_test_1261.presentIt.model.Person;
import com.usergo.presentit.helpers.AccountUtils;
import com.usergo.presentit.helpers.PresentItException;
import com.usergo.presentit.helpers.PresentItUtils;

import java.io.IOException;

/**
 * Created by sruth on 4/17/2016.
 */
public class ProfileLoader extends AsyncTaskLoader<Person>{

    private static final String TAG = "ProfileLoader";
    private Exception pException;
    public ProfileLoader(Context context) {
        super(context);
    }

    @Override
    public Person loadInBackground() {
        try {
            Person person = PresentItUtils.getPersonProfile();
            return person;
        } catch (IOException e) {
            Log.e(TAG, "Failed to get Profile", e);
            pException = e;
        }  catch (PresentItException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        cancelLoad();
        if (pException != null) {
            AccountUtils.displayNetworkErrorMessage(getContext());
        }
    }

    public Exception getException() {
        return pException;
    }
}
