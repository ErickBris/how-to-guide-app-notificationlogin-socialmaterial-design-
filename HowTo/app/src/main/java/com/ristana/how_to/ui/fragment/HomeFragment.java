package com.ristana.how_to.ui.fragment;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ristana.how_to.R;
import com.ristana.how_to.adapter.GuideAdapter;
import com.ristana.how_to.entity.Guide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.ristana.how_to.api.apiClient;
import com.ristana.how_to.api.apiRest;

public class HomeFragment extends Fragment {
    private View view;
    private RecyclerView recycle_view_home_fragment;
    private SwipeRefreshLayout swipe_refreshl_home_fragment;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager linearLayoutManager;
    private Integer next_article_id=-1;
    private List<Guide> guideList=new ArrayList<>();
    private GuideAdapter guideAdapter;
    private CardView card_refreshl_home_fragment_next;
    private CardView card_view_weather;
    private boolean doubleBackToExitPressedOnce;
    private RelativeLayout relative_layout_home_f;
    private ImageView imageView_empty;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void initView(){
        this.relative_layout_home_f=(RelativeLayout) this.view.findViewById(R.id.relative_layout_home_f);
        this.imageView_empty=(ImageView)  this.view.findViewById(R.id.imageView_empty);

        this.linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        this.card_refreshl_home_fragment_next=(CardView) this.view.findViewById(R.id.card_refreshl_home_fragment_next);
        this.recycle_view_home_fragment=(RecyclerView) this.view.findViewById(R.id.recycle_view_home_fragment);
        this.swipe_refreshl_home_fragment=(SwipeRefreshLayout)  this.view.findViewById(R.id.swipe_refreshl_home_fragment);
        this.recycle_view_home_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount    = linearLayoutManager.getChildCount();
                    totalItemCount      = linearLayoutManager.getItemCount();
                    pastVisiblesItems   = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            getNextGuides();
                        }
                    }
                }else{

                }
            }
        });
        guideAdapter=new GuideAdapter(guideList,getActivity().getApplicationContext());
        recycle_view_home_fragment.setHasFixedSize(true);
        recycle_view_home_fragment.setAdapter(guideAdapter);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_view_home_fragment.setLayoutManager(linearLayoutManager);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        getGuides();
        initAction();
        return view;

    }
    public void initAction(){
        this.swipe_refreshl_home_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getGuides();
            }
        });
    }
    private void getGuides() {
        this.swipe_refreshl_home_fragment.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Guide>> call = service.guidesAll();
        call.enqueue(new Callback<List<Guide>>() {
            @Override
            public void onResponse(Call<List<Guide>> call, Response<List<Guide>> response) {
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        imageView_empty.setVisibility(View.GONE);
                        recycle_view_home_fragment.setVisibility(View.VISIBLE);
                        guideList.clear();
                        for (int i=0;i<response.body().size();i++){
                            guideList.add(response.body().get(i));
                        }
                        guideAdapter.notifyDataSetChanged();
                        if (response.body().size()!=0){
                            next_article_id=response.body().get(guideList.size()-1).getId();
                            loading = true;
                        }
                    }else {
                        imageView_empty.setVisibility(View.VISIBLE);
                        recycle_view_home_fragment.setVisibility(View.GONE);
                    }
                }else {
                    Snackbar snackbar = Snackbar
                            .make(relative_layout_home_f, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getGuides();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
                swipe_refreshl_home_fragment.setRefreshing(false);

            }
            @Override
            public void onFailure(Call<List<Guide>> call, Throwable t) {
                swipe_refreshl_home_fragment.setRefreshing(false);
                Snackbar snackbar = Snackbar
                        .make(relative_layout_home_f, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getGuides();
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
    private void getNextGuides() {
        this.card_refreshl_home_fragment_next.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Guide>> call = service.guidesNext(this.next_article_id);
        call.enqueue(new Callback<List<Guide>>() {
            @Override
            public void onResponse(Call<List<Guide>> call, Response<List<Guide>> response) {
                if(response.isSuccessful()){
                    for (int i=0;i<response.body().size();i++){
                        guideList.add(response.body().get(i));
                    }
                    guideAdapter.notifyDataSetChanged();
                    if (response.body().size()!=0){
                        next_article_id=guideList.get(guideList.size()-1).getId();
                        loading = true;
                    }
                }else{
                    Snackbar snackbar = Snackbar
                            .make(relative_layout_home_f, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getNextGuides();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }

                card_refreshl_home_fragment_next.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<List<Guide>> call, Throwable t) {

                card_refreshl_home_fragment_next.setVisibility(View.GONE);

                swipe_refreshl_home_fragment.setRefreshing(false);
                Snackbar snackbar = Snackbar
                        .make(relative_layout_home_f, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getNextGuides();
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
