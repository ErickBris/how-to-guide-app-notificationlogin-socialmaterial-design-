package com.ristana.how_to.ui.activity;

import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ristana.how_to.R;
import com.ristana.how_to.adapter.GuideAdapter;
import com.ristana.how_to.entity.Guide;
import com.ristana.how_to.entity.GuideORM;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RelativeLayout activity_favorites;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recycle_view_home_favorite;
    private ImageView imageView_empty_favorite;
    private SwipeRefreshLayout swipe_refreshl_home_favorite;
    private List<Guide> guideList=new ArrayList<>();
    private GuideAdapter guideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        iniView();
        initAction();
        getGuide();

    }
    public void iniView(){
        getSupportActionBar().setElevation(5);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_saved));

        this.activity_favorites=(RelativeLayout) findViewById(R.id.activity_favorites);

        this.linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.recycle_view_home_favorite=(RecyclerView) findViewById(R.id.recycle_view_home_favorite);
        this.swipe_refreshl_home_favorite=(SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_home_favorite);
        this.imageView_empty_favorite=(ImageView) findViewById(R.id.imageView_empty_favorite);

        guideAdapter =new GuideAdapter(guideList,getApplicationContext(),true);
        recycle_view_home_favorite.setHasFixedSize(true);
        recycle_view_home_favorite.setAdapter(guideAdapter);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_view_home_favorite.setLayoutManager(linearLayoutManager);
    }

    public void initAction(){
        this.swipe_refreshl_home_favorite.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGuide();
            }
        });
    }

    private void getGuide() {

        swipe_refreshl_home_favorite.setRefreshing(true);
        List<GuideORM> guideORMList = GuideORM.listAll(GuideORM.class,"id DESC");
        guideList.clear();
        if (guideORMList.size()!=0){

            for (int i=0;i<guideORMList.size();i++){
                Guide a= new Guide();
                a.setORM(guideORMList.get(i));
                guideList.add(a);
            }
            guideAdapter.notifyDataSetChanged();
            imageView_empty_favorite.setVisibility(View.GONE);
            recycle_view_home_favorite.setVisibility(View.VISIBLE);
        }else{
            imageView_empty_favorite.setVisibility(View.VISIBLE);
            recycle_view_home_favorite.setVisibility(View.GONE);
        }

        swipe_refreshl_home_favorite.setRefreshing(false);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
