package com.ristana.how_to.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.ristana.how_to.R;
import com.ristana.how_to.entity.Category;
import com.ristana.how_to.manager.PrefManager;
import com.ristana.how_to.ui.fragment.HomeFragment;
import com.ristana.how_to.ui.fragment.PageFragment;
import com.ruslankishai.unmaterialtab.tabs.RoundTab;
import com.ruslankishai.unmaterialtab.tabs.RoundTabLayout;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.ristana.how_to.api.apiClient;
import com.ristana.how_to.api.apiRest;
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager main_view_pager;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private RoundTabLayout main_tab_layout;
    private FloatingSearchView main_floating_search_view;
    private DrawerLayout drawer_layout;
    private NavigationView main_navigation_view;
    private Button header_sign_in;
    private Button header_sign_up;
    private Button header_logout;
    private Button header_profile;
    private TextView header_text_view_name;
    private LinearLayout linearLayout_login;
    private LinearLayout linearLayout_user;
    private PrefManager prf;
    private ImageView header_image_profile;
    private TextView header_text_view_email;
    private CoordinatorLayout content_main;
    private ViewPagerAdapter adapter;
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prf= new PrefManager(getApplicationContext());
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        initView();
        initAction();
        setupViewPager();
        getCategoriesList();

    }
    public void initView(){
        setContentView(R.layout.activity_main);
        this.main_floating_search_view =    (FloatingSearchView) findViewById(R.id.main_floating_search_view);
        this.main_view_pager = (ViewPager) findViewById(R.id.main_view_pager);
        this.main_view_pager.setOffscreenPageLimit(99);
        this.main_tab_layout = (RoundTabLayout) findViewById(R.id.main_tab_layout);
        this.drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.main_floating_search_view.attachNavigationDrawerToMenuButton(drawer_layout);


        this.main_navigation_view      =    (NavigationView) findViewById(R.id.main_navigation_view);
        this.main_navigation_view.setNavigationItemSelectedListener(this);

        View headerview = main_navigation_view.getHeaderView(0);
        this.header_sign_in=(Button) headerview.findViewById(R.id.header_sign_in);
        this.header_sign_up=(Button) headerview.findViewById(R.id.header_sign_up);
        this.header_logout =(Button) headerview.findViewById(R.id.header_logout);
        this.header_profile=(Button) headerview.findViewById(R.id.header_profile);
        this.header_image_profile=(ImageView) headerview.findViewById(R.id.header_image_profile);
        this.header_text_view_name=(TextView) headerview.findViewById(R.id.header_text_view_name);
        this.linearLayout_login=(LinearLayout) headerview.findViewById(R.id.linearLayout_login);
        this.linearLayout_user=(LinearLayout) headerview.findViewById(R.id.linearLayout_user);
        this.header_text_view_email=(TextView) headerview.findViewById(R.id.header_text_view_email);
        this.content_main=(CoordinatorLayout) findViewById(R.id.CoordinatorLayout_main);

    }
    public void initAction(){
        this.header_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        this.header_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        this.header_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        this.header_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,AccountActivity.class);
                startActivity(intent);
            }
        });
        this.main_floating_search_view.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }
            @Override
            public void onSearchAction(String currentQuery) {
                Intent intent= new Intent(MainActivity.this,SearchActivity.class);
                intent.putExtra("query",currentQuery);
                startActivity(intent);
            }
        });
    }
    private void setupViewPager() {
        adapter.addFragment(new HomeFragment(),getResources().getString(R.string.all));
        main_view_pager.setAdapter(adapter);
        main_view_pager.setCurrentItem(0);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.home_action:
                main_view_pager.setCurrentItem(0);
                break;
            case R.id.setting_action:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
            case R.id.saved_action:
                startActivity(new Intent(MainActivity.this,FavoritesActivity.class));
                break;
            case R.id.policy_action:
                startActivity(new Intent(MainActivity.this,PolicyActivity.class));
                break;
            case R.id.contact_us_action:
                startActivity(new Intent(MainActivity.this,ContactActivity.class));
                break;
            case R.id.about_us_action:
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
                break;
            case R.id.share_action:
                String shareBody = getString(R.string.app_name)+" "+getString(R.string.url_app_google_play)+getApplication().getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);

        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public      void logout(){
        PrefManager prf= new PrefManager(getApplicationContext());
        prf.remove("ID_USER");
        prf.remove("SALT_USER");
        prf.remove("TOKEN_USER");
        prf.remove("NAME_USER");
        prf.remove("TYPE_USER");
        prf.remove("USERNAME_USER");
        prf.remove("URL_USER");
        prf.remove("LOGGED");
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            linearLayout_login.setVisibility(View.GONE);
            linearLayout_user.setVisibility(View.VISIBLE);
            header_text_view_name.setText(prf.getString("NAME_USER").toString());
        }else{
            header_image_profile.setImageDrawable(getResources().getDrawable(R.drawable.default_male));
            linearLayout_login.setVisibility(View.VISIBLE);
            linearLayout_user.setVisibility(View.GONE);
            header_text_view_name.setText("");
        }
        Toast.makeText(getApplicationContext(),getString(R.string.message_logout),Toast.LENGTH_LONG).show();
    }
    @Override
    public void onStart(){
        super.onStart();
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            linearLayout_login.setVisibility(View.GONE);
            linearLayout_user.setVisibility(View.VISIBLE);
            header_text_view_name.setText(prf.getString("NAME_USER").toString());
            if (prf.getString("TYPE_USER").toString().equals("email")){
                header_text_view_email.setText(prf.getString("USERNAME_USER").toString());

            }else{
                header_text_view_email.setText(prf.getString("TYPE_USER").toString().substring(0, 1).toUpperCase() + prf.getString("TYPE_USER").toString().substring(1) +" Account");
            }

            header_text_view_name.setText(prf.getString("NAME_USER").toString());

            new DownloadImageTask(header_image_profile)
                    .execute(prf.getString("URL_USER").toString());
        }else{
            linearLayout_login.setVisibility(View.VISIBLE);
            linearLayout_user.setVisibility(View.GONE);
            header_text_view_name.setText("");
        }
    }
    @Override
    public void onResume(){
        super.onResume();

        if (prf.getString("LOGGED").toString().equals("TRUE")){
            linearLayout_login.setVisibility(View.GONE);
            linearLayout_user.setVisibility(View.VISIBLE);
            header_text_view_name.setText(prf.getString("NAME_USER").toString());
            new DownloadImageTask(header_image_profile)
                    .execute(prf.getString("URL_USER").toString());
            if (prf.getString("TYPE_USER").toString().equals("email")){
                header_text_view_email.setText(prf.getString("USERNAME_USER").toString());

            }else{
                header_text_view_email.setText(prf.getString("TYPE_USER").toString().substring(0, 1).toUpperCase() + prf.getString("TYPE_USER").toString().substring(1) +" Account");
            }
        }else{
            linearLayout_login.setVisibility(View.VISIBLE);
            linearLayout_user.setVisibility(View.GONE);
            header_text_view_name.setText("");
        }

    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    public void getCategoriesList(){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Category>> call = service.categorriesList();
        call.enqueue(new Callback<List<Category>>() {

            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()){
                    for (int i=0;i<response.body().size();i++){
                            Bundle bundle = new Bundle();
                            String myMessage =response.body().get(i).getTitle();
                            bundle.putString("id_category", response.body().get(i).getId()+"" );

                            PageFragment pageFragment=  new PageFragment();

                            pageFragment.setArguments(bundle);

                            adapter.addFragment(pageFragment,response.body().get(i).getTitle());
                            adapter.notifyDataSetChanged();


                    }
                    main_tab_layout.setupWithViewPager(main_view_pager);
                    RoundTab tab = main_tab_layout.getTab(0);
                    Drawable icon = getResources().getDrawable(R.drawable.ic_public);
                    tab.setIcon(icon);
                    tab.setHasIcon(true);
                }else {
                    Snackbar snackbar = Snackbar
                            .make(content_main, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getCategoriesList();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Snackbar snackbar = Snackbar
                        .make(content_main, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getCategoriesList();
                            }
                        });
                snackbar.setActionTextColor(Color.RED);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        });
    }
}
