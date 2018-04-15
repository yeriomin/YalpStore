package in.dragons.galaxy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.dragons.galaxy.CircleTransform;
import in.dragons.galaxy.R;
import in.dragons.galaxy.activities.AccountsActivity;
import in.dragons.galaxy.view.AdaptiveToolbar;

public class HomeFragment extends UtilFragment {

    private View view;
    AdaptiveToolbar adtb;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            if ((ViewGroup) view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }
        view = inflater.inflate(R.layout.fragment_home, container, false);
        /*RelativeLayout open_community = view.findViewById(R.id.open_community);
        open_community.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://t.me/GalaxyOfficial");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });*/
        adtb = view.findViewById(R.id.adtb);
        adtb.getAvatar_icon().setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AccountsActivity.class);
            intent.putExtra("account_profile_animate", true);
            startActivity(intent);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn())
            setUser();
        else {
            resetUser();
            LoginFirst();
        }
    }

    protected void setUser() {
        if (isGoogle()) {
            Picasso.with(getActivity())
                    .load(PreferenceFragment.getString(getActivity(), "GOOGLE_URL"))
                    .placeholder(R.drawable.ic_user_placeholder)
                    .transform(new CircleTransform())
                    .into(adtb.getAvatar_icon());
        } else {
            (adtb.getAvatar_icon()).setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_dummy_avatar));
        }
    }
    protected void resetUser() {
        (adtb.getAvatar_icon()).setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_user_placeholder));
    }
}
