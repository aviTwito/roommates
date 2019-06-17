package atoa.roomates.activities;
/**
 * Copyright 2016 Avi twito,Or Am-Amshalem
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.IOException;

import atoa.roomates.R;
import atoa.roomates.adapters.PagerAdapter;
import atoa.roomates.models.RoomateModel;
import atoa.roomates.support.AppStartScreen;
import atoa.roomates.support.GenericMethods;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * the app main screen. consists two tabs: one tab is the apartment notes and the second tab is the apartment status
 * it also holds a navigation menu where the user can navigate to the other screen of the app
 */
public class HomeActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {


    //DATA
    SharedPreferences mPreferences;
    RoomateModel mRoomateModel;


    //UI
    //tool bar and navigation drawer elements
    Toolbar mToolbar;
    DrawerLayout mDrawer;
    private NavigationView mNvDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    TextView mToolBarTitle, mFullName;
    Typeface mFont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //changes the tool bar title font
        mToolBarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/noot-aj.ttf");
        mToolBarTitle.setTypeface(mFont);

        //Initializing the tablayout
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //customizing the tabs in the tab layout
        final TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(R.string.messages);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_insert_comment_white_24dp, 0, 0);
        mTabLayout.getTabAt(1).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(R.string.bill_state);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_insert_chart_white_24dp, 0, 0);
        mTabLayout.getTabAt(0).setCustomView(tabTwo);

        //Initializing viewPager
        mViewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());

        //Adding adapter to pager
        mViewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        mTabLayout.setOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));


        mPreferences = getSharedPreferences("appData", 0);


        mRoomateModel = GenericMethods.getInstance(this).getUserData();

        mDrawer = (DrawerLayout) findViewById(R.id.main_layout);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(mDrawerToggle);

        //responsible for navigation icon on the toolbat
        mDrawerToggle = setupDrawerToggle();


        //inizialzie nevigation drawer
        mNvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(mNvDrawer);

        //creates an instance of the side menu header in order to change the image to the current user image
        View headerLayout = mNvDrawer.inflateHeaderView(R.layout.nav_header);
        TextView contectUs = (TextView) mNvDrawer.findViewById(R.id.contact_us);
        final TextView questionsAndAnswers = (TextView) mNvDrawer.findViewById(R.id.question_and_answers);
        contectUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUs();
            }
        });
        questionsAndAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionsAndAnswers();
            }
        });

        // creates an image view instance of the header image view
        final CircleImageView headerImage = (CircleImageView) headerLayout.findViewById(R.id.profilePicture);
        mFullName = (TextView) headerLayout.findViewById(R.id.tvFullName);
        String temp = mRoomateModel.getName() + " " + mRoomateModel.getLastName();
        mFullName.setText(temp);

        //gets the encoded string from the app data which represent the user photo.
        String photoPath = mPreferences.getString(RegistraionActivity.USER_PROFILE_PICTURE, "");

        Bitmap bitmap;
        try {
            bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
            ExifInterface exif = new ExifInterface(photoPath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap bmRotated = GenericMethods.getInstance(this).rotateBitmap(bitmap, orientation);
            headerImage.setImageBitmap(bmRotated);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void questionsAndAnswers() {
        Intent intent = new Intent(this, QuestionAndAnswersActivity.class);
        startActivity(intent);
    }

    private void contactUs() {
        AlertDialog.Builder contactUsDialog = new AlertDialog.Builder(this);
        contactUsDialog.setTitle(getString(R.string.contact_us));
        contactUsDialog.setMessage(getString(R.string.contact) + "\n" + getString(R.string.mail));
        contactUsDialog.setCancelable(true);
        contactUsDialog.show();
    }


    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    /**
     * handle side menu items click
     *
     * @param menuItem the selected option on the navigation drawer
     */
    public void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_shopScreen:
                startShopcartScreen();
                break;
            case R.id.nav_billScreen:
                startBillScreen();
                break;
            case R.id.nav_roommatesScreen:
                srartRoommatesScreen();

            default:

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.blank_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                mPreferences = getSharedPreferences("appData", 0);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean(LoginActivity.USER_IS_LOGGED_IN, false);
                editor.apply();
                Intent intent = new Intent(this, AppStartScreen.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * creates and start shopCart Activity
     */
    public void startShopcartScreen() {
        Intent intent = new Intent(this, ShopCartActivity.class);
        this.startActivity(intent);
    }

    /**
     * creates and start Bill Activity
     */
    public void startBillScreen() {
        Intent intent = new Intent(this, BillsActivity.class);
        this.startActivity(intent);
    }

    /**
     * creates and start Roommates screen Activity
     */
    private void srartRoommatesScreen() {
        Intent intent = new Intent(this, RoomatesActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private Boolean exit = false;

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.press_to_exit,
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }


}
