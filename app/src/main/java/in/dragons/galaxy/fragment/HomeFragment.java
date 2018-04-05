package in.dragons.galaxy.fragment;

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

public class HomeFragment extends UtilFragment {

    private RelativeLayout open_community;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            if ((ViewGroup) view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }
        view = inflater.inflate(R.layout.fragment_home, container, false);
        /*open_community = view.findViewById(R.id.open_community);
        open_community.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://t.me/GalaxyOfficial");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });*/
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
            ((TextView) view.findViewById(R.id.account_name))
                    .setText(PreferenceFragment.getString(getActivity(), "GOOGLE_NAME"));

            Picasso.with(getActivity())
                    .load(PreferenceFragment.getString(getActivity(), "GOOGLE_URL"))
                    .placeholder(R.drawable.ic_user_placeholder)
                    .transform(new CircleTransform())
                    .into(((ImageView) view.findViewById(R.id.account_avatar)));
        } else {
            ((TextView) view.findViewById(R.id.account_name)).setText(R.string.acc_dummy_name);
            ((ImageView) view.findViewById(R.id.account_avatar))
                    .setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_dummy_avatar));
        }
    }

    protected void resetUser() {
        ((TextView) view.findViewById(R.id.account_name)).setText(R.string.header_usr_noEmail);
        ((ImageView) view.findViewById(R.id.account_avatar))
                .setImageDrawable(getResources()
                        .getDrawable(R.drawable.ic_user_placeholder));
    }
}
