package com.mentorbridge.app.fragments.mentor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mentorbridge.app.R;
import com.mentorbridge.app.utils.SessionManager;

public class AvailabilityFragment extends Fragment {

    private TextView txtWelcome;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_availability, container, false);

        sessionManager = new SessionManager(requireContext());

        txtWelcome = view.findViewById(R.id.txtWelcome);
        txtWelcome.setText("Manage your availability, " + sessionManager.getFullName());

        return view;
    }
}
