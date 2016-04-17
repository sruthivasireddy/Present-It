package com.usergo.presentit.helpers;

import android.content.Context;
import android.util.Log;

import com.appspot.yet_another_test_1261.presentIt.model.Person;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.usergo.presentit.Constants;

import java.io.IOException;

/**
 * Created by sruth on 4/16/2016.
 */
public class PresentItUtils {

    private final static String TAG = "PresentItUtils";

    private static com.appspot.yet_another_test_1261.presentIt.PresentIt pApiServiceHandler;

    public static void build(Context context, String email) {
        pApiServiceHandler = buildServiceHandler(context, email);
    }

    public static com.appspot.yet_another_test_1261.presentIt.PresentIt buildServiceHandler(
            Context context, String email) {
        Log.d(TAG,"Building service handler");
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                context, Constants.AUDIENCE);
        credential.setSelectedAccountName(email);

        com.appspot.yet_another_test_1261.presentIt.PresentIt.Builder builder
                = new com.appspot.yet_another_test_1261.presentIt.PresentIt.Builder(
                Constants.HTTP_TRANSPORT,
                Constants.JSON_FACTORY, credential);
        builder.setApplicationName("yet-another-test-1261");
        return builder.build();
    }

    public static Person getPersonProfile() throws PresentItException, IOException {
        if (null == pApiServiceHandler) {
            Log.e(TAG, "getPersonProfile(): no service handler was built");
            throw new PresentItException();
        }
        Log.d(TAG, pApiServiceHandler.getRootUrl());

        com.appspot.yet_another_test_1261.presentIt.PresentIt.GetPersonProfile getProfile =
                pApiServiceHandler.getPersonProfile();
        Person person = getProfile.execute();
        Log.d(TAG, "person profile name-"+person.getFirstName());
        return person;
    }
}
