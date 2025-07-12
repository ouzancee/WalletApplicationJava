package com.example.walletapplication.presentation.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResourceId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        observeViewModel();
    }

    protected abstract int getLayoutResourceId();

    protected abstract void initializeViews(View view);

    protected abstract void observeViewModel();

    protected void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            Log.d(TAG, message);
        }
    }

    protected void showLongToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    protected void showError(String error) {
        if (error != null && !error.isEmpty()) {
            showErrorSnackbar(error);
            Log.e(TAG, error);
        }
    }

    protected void showErrorSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
            Log.e(TAG, message  );
        }
    }

    protected void showErrorSnackbar(String message, String actionText, View.OnClickListener action) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_LONG)
                    .setAction(actionText, action)
                    .show();
        }
    }

    protected void showSuccessSnackbar(String message) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
        }
    }

    protected void showRetrySnackbar(String message, Runnable retryAction) {
        if (getView() != null) {
            Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Tekrar Dene", v -> retryAction.run())
                    .show();
        }
    }

    protected void setTitle(String title) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            if (((BaseActivity) getActivity()).getSupportActionBar() != null) {
                ((BaseActivity) getActivity()).getSupportActionBar().setTitle(title);
            }
        }
    }
} 