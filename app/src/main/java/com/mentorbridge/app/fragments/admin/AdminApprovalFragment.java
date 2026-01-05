package com.mentorbridge.app.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mentorbridge.app.R;
import com.mentorbridge.app.utils.SessionManager;

public class AdminApprovalFragment extends Fragment {

    private TextView txtWelcome;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_approval, container, false);

        sessionManager = new SessionManager(requireContext());

        txtWelcome = view.findViewById(R.id.txtWelcome);
        txtWelcome.setText("Pending Mentor Approvals");

        return view;
    }
}
