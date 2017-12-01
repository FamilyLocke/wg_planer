package de.ameyering.wgplaner.wgplaner.section.setup;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.home.HomeActivity;
import de.ameyering.wgplaner.wgplaner.section.setup.fragment.DescriptionFragment;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;

public class SetUpActivity extends AppCompatActivity {
    private static final String DESCRIPTION_FRAGMENT_TAG = "DescriptionFragment";
    private static final String PATH_PATTERN = "^(http|https)://api.wgplaner.ameyering.de/groups/join/[A-Z0-9]{12}";

    private Toolbar toolbar;
    private boolean toastWasShown = false;

    private DescriptionFragment descriptionFragment = new DescriptionFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        toolbar = findViewById(R.id.set_up_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        toolbar.setNavigationContentDescription(getString(R.string.nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popBackStack();
            }
        });


        getSupportFragmentManager().addOnBackStackChangedListener(new
        FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    getSupportActionBar().hide();

                } else {
                    getSupportActionBar().show();
                }
            }
        });

        if (savedInstanceState != null) {
            descriptionFragment = (DescriptionFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, DESCRIPTION_FRAGMENT_TAG);

        } else {
            if(getIntent().getData() != null){
                Uri data = getIntent().getData();
                Pattern pattern = Pattern.compile(PATH_PATTERN);
                Matcher matcher = pattern.matcher(data.toString());

                if(matcher.matches()){
                    if(!DataProvider.getInstance().joinCurrentGroup(data.getLastPathSegment(), this)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SetUpActivity.this, getString(R.string.server_connection_failed),
                                    Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Intent intent = new Intent(this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            loadDescriptionFragment();
            toolbar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, DESCRIPTION_FRAGMENT_TAG, descriptionFragment);
    }

    private void loadDescriptionFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right,
            R.anim.anim_fragment_exit_to_left, R.anim.anim_fragment_enter_from_left,
            R.anim.anim_fragment_exit_to_right);
        transaction.add(R.id.container_set_up, descriptionFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        popBackStack();
    }

    private void popBackStack() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if (!toastWasShown) {
                Toast toast = Toast.makeText(this, getString(R.string.back_press_leave_notification),
                        Toast.LENGTH_LONG);
                toast.show();
                toastWasShown = true;

            } else {
                toastWasShown = false;
                finish();
            }

        } else {
            getSupportFragmentManager().popBackStack();
            toastWasShown = false;
        }
    }
}
