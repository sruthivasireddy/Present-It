package com.usergo.presentit.helpers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usergo.presentit.R;

/**
 * Created by sruth on 4/17/2016.
 */
public class CoursesFragment extends Fragment {

    public static Fragment newInstance(Context context) {
        CoursesFragment f = new CoursesFragment();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.courses_fragment, null);
        return root;
    }
}
