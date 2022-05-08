package com.ristana.how_to.ui.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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

public class PageFragment extends Fragment {
    public  String id_category;
    private RecyclerView recycle_view_page_fragment;
    private View view;
    private Boolean loaded=false;
    private SwipeRefreshLayout swipe_refreshl_page_fragment;
    private     int     pastVisiblesItems,
            visibleItemCount,
            totalItemCount;
    private boolean loading = true;
    private LinearLayoutManager linearLayoutManager;
    private List<Guide> guideList=new ArrayList<>();
    private GuideAdapter guideAdapter;
    private Integer next_article_id=-1;
    private CardView card_refreshl_page_fragment_next;
    private RelativeLayout relative_layout_page_f;
    private ImageView imageView_empty;

    public PageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.id_category = this.getArguments().getString("id_category");
        view= inflater.inflate(R.layout.fragment_page, container, false);
        initView();
        initAction();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if (!loaded){
                getArticle();
            }
        }
        else{

        }
    }
    public void initAction(){
        this.swipe_refreshl_page_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getArticle();
            }
        });
    }
    public void initView(){
        this.relative_layout_page_f=(RelativeLayout) this.view.findViewById(R.id.relative_layout_page_f);
        this.imageView_empty=(ImageView)  this.view.findViewById(R.id.imageView_empty);
        this.card_refreshl_page_fragment_next=(CardView) this.view.findViewById(R.id.card_refreshl_page_fragment_next);
        this.recycle_view_page_fragment=(RecyclerView) this.view.findViewById(R.id.recycle_view_page_fragment);
        this.swipe_refreshl_page_fragment=(SwipeRefreshLayout) this.view.findViewById(R.id.swipe_refreshl_page_fragment);
        this.recycle_view_page_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
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
                            getNextArticles();
                        }
                    }
                }
            }
        });
        guideAdapter=new GuideAdapter(guideList,getActivity().getApplicationContext());
        recycle_view_page_fragment.setHasFixedSize(true);
        recycle_view_page_fragment.setAdapter(guideAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_view_page_fragment.setLayoutManager(linearLayoutManager);
    }
    private void getArticle() {
        this.swipe_refreshl_page_fragment.setRefreshing(true);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Guide>> call = service.guidesByCategory(id_category);
        call.enqueue(new Callback<List<Guide>>() {
            @Override
            public void onResponse(Call<List<Guide>> call, Response<List<Guide>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()!=0){
                        imageView_empty.setVisibility(View.GONE);
                        recycle_view_page_fragment.setVisibility(View.VISIBLE);
                        guideList.clear();
                        for (int i=0;i<response.body().size();i++){
                            guideList.add(response.body().get(i));
                        }
                        guideAdapter.notifyDataSetChanged();
                        guideAdapter.notifyDataSetChanged();
                        if (response.body().size()!=0){
                            next_article_id=response.body().get(guideList.size()-1).getId();
                            loading = true;
                        }
                        loaded=true;
                    }else {
                        imageView_empty.setVisibility(View.VISIBLE);
                        recycle_view_page_fragment.setVisibility(View.GONE);
                    }
                }else {
                    Snackbar snackbar = Snackbar
                            .make(relative_layout_page_f, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getArticle();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();

                }

                swipe_refreshl_page_fragment.setRefreshing(false);

            }
            @Override
            public void onFailure(Call<List<Guide>> call, Throwable t) {
                swipe_refreshl_page_fragment.setRefreshing(false);

                Snackbar snackbar = Snackbar
                        .make(relative_layout_page_f, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getArticle();
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
    private void getNextArticles() {
        this.card_refreshl_page_fragment_next.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Guide>> call = service.guidesByCategoryNext(this.id_category,this.next_article_id);
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
                            .make(relative_layout_page_f, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getNextArticles();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }

                card_refreshl_page_fragment_next.setVisibility(View.GONE);

            }
            @Override
            public void onFailure(Call<List<Guide>> call, Throwable t) {
                card_refreshl_page_fragment_next.setVisibility(View.GONE);
                Snackbar snackbar = Snackbar
                        .make(relative_layout_page_f, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getNextArticles();
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
