package com.federicoberon.alarme.ui.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.federicoberon.alarme.MainActivity;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.federicoberon.alarme.R;
import com.federicoberon.alarme.AlarMe;
import com.federicoberon.alarme.databinding.FragmentAboutBinding;

import javax.inject.Inject;

public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    @Inject
    AboutViewModel aboutViewModel;


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)requireActivity()).getBinding().appBarMain.appBar.setExpanded(false, false);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)((MainActivity)requireActivity()).getBinding().appBarMain.appBar.getLayoutParams();
        lp.height = 140;
        ((MainActivity) requireActivity()).getBinding().appBarMain.fab.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ((AlarMe) requireActivity().getApplicationContext())
                .appComponent.inject(this);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        requireContext().getTheme().applyStyle(R.style.AppThemeDark, true);
        loadAbout();

        return root;
    }

    private void loadAbout() {

        String url = "https://www.paypal.com/donate/?hosted_button_id=XAKHYHY3HUFQG";
        Intent donateIntent = new Intent(Intent.ACTION_VIEW);
        donateIntent.setData(Uri.parse(url));

        AboutBuilder builder = AboutBuilder.with(requireActivity())
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
                .setLinksAnimated(true)
                .setDividerDashGap(12)
                .setName("Federico Beron")
                .setSubTitle("Mobile Developer")
                .setBrief(R.string.brew_personal_desc)
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .setActionsColumnsCount(2)
                .addFeedbackAction("soporte.escuelapp@gmail.com")
                .addPrivacyPolicyAction(v -> {

                    Bundle args = new Bundle();
                    args.putLong(TermsDialogFragment.TYPE_OF_CONTENT, 1L);

                    Navigation.findNavController(
                            binding.getRoot()).navigate(R.id.action_nav_about_to_termsDialogFragment, args);
                })
                .addAction(R.mipmap.privacy, R.string.terms_of_services, v -> {
                    Bundle args = new Bundle();
                    args.putLong(TermsDialogFragment.TYPE_OF_CONTENT, 0L);

                    Navigation.findNavController(
                            binding.getRoot()).navigate(R.id.action_nav_about_to_termsDialogFragment, args);
                })
                .addDonateAction(donateIntent)
                .setWrapScrollView(true)
                .setShowAsCard(true);

        AboutView view = builder.build();
        binding.about.addView(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        requireContext().getTheme().applyStyle(R.style.SimpleRemindMe, true);

        binding = null;
    }
}