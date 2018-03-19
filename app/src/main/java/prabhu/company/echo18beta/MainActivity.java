package prabhu.company.echo18beta;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class MainActivity extends AppCompatActivity {

    private SpaceNavigationView spaceNavigationView;
    private android.support.v4.app.FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initialiseViews(savedInstanceState);
    }

    private void initialiseViews(Bundle savedInstanceState) {
        spaceNavigationView = findViewById(R.id.mainSpaceNavigationView);
        fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new HomeFragment()).commit();

        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Tower", R.drawable.ic_network_cell_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("Speed", R.drawable.ic_network_check_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("Coverage", R.drawable.ic_swap_vert_black_24dp));
        spaceNavigationView.addSpaceItem(new SpaceItem("Analytics", R.drawable.ic_trending_up_black_24dp));

        spaceNavigationView.setCentreButtonIcon(R.drawable.ic_home_black_24dp);
        spaceNavigationView.setCentreButtonColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));
        spaceNavigationView.setCentreButtonRippleColor(R.color.colorAccent);
        spaceNavigationView.setActiveCentreButtonBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        spaceNavigationView.setActiveCentreButtonIconColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        spaceNavigationView.setInActiveCentreButtonIconColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new HomeFragment()).commit();
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Fragment fragment;
                switch (itemIndex) {
                    case 0:
                        fragment = new TowerFragment();
                        break;
                    case 1:
                        fragment = new SpeedFragment();
                        break;
                    case 2:
                        fragment = new CoverageFragment();
                        break;
                    default:
                        fragment = new AnalyticsFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, fragment).commit();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {

            }
        });

    }
}
