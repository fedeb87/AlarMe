package com.federicoberon.alarme.ui.about;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.federicoberon.alarme.R;

public class TermsDialogFragment extends DialogFragment {

    private TextView mTextViewTitleTerms;
    private TextView mTextViewBodyTerms;

    public static final String TYPE_OF_CONTENT = "Type of content";

    public TermsDialogFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_terms, container);

        int typeOfContent = getArguments().getInt("typeOfContent");

        initViews(mView);
        configRootView();
        populateUI(typeOfContent);
        return mView;
    }

    private void populateUI(int typeOfContent) {
        if (typeOfContent==0) {
            mTextViewTitleTerms.setText(requireActivity().getResources().getString(
                    R.string.terms_of_services));
            mTextViewBodyTerms.setText(requireActivity().getResources().getString(
                    R.string.terms_of_services_body));
        }else {
            mTextViewTitleTerms.setText(requireActivity().getResources().getString(
                    R.string.privacy_policy));
            mTextViewBodyTerms.setText(requireActivity().getResources().getString(
                    R.string.privacy_policy_body));
        }
    }

    private void initViews(View view) {
        mTextViewTitleTerms = view.findViewById(R.id.textViewTitleTerms);
        mTextViewBodyTerms = view.findViewById(R.id.textViewBodyTerms);
    }

    public static TermsDialogFragment newInstance(int typeOfContent) {

        TermsDialogFragment termsDialogFragment = new TermsDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("typeOfContent", typeOfContent);
        termsDialogFragment.setArguments(args);

        return termsDialogFragment;
    }

    public void configRootView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int displayHeight = displayMetrics.heightPixels;
        //getDialog().setContentView(R.layout.fragment_filter);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.95f);
        int dialogWindowHeight = (int) (displayHeight * 0.70f);
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;
        getDialog().getWindow().setAttributes(layoutParams);
    }
}
