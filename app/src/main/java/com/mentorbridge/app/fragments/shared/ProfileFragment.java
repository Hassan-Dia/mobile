package com.mentorbridge.app.fragments.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mentorbridge.app.R;
import com.mentorbridge.app.activities.MainActivity;
import com.mentorbridge.app.utils.SessionManager;

public class ProfileFragment extends Fragment {

    private TextView txtName, txtEmail, txtRole;
    private Button btnLogout;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());

        initViews(view);
        displayProfile();

        return view;
    }

    private void initViews(View view) {
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtRole = view.findViewById(R.id.txtRole);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showLogoutOption();
            }
        });
    }

    private void displayProfile() {
        txtName.setText(sessionManager.getFullName());
        txtEmail.setText(sessionManager.getEmail());
        
        String role = sessionManager.getRole();
        String displayRole = role.substring(0, 1).toUpperCase() + role.substring(1);
        txtRole.setText(displayRole);
    }
}
