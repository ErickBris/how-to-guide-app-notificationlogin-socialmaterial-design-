package com.ristana.how_to.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.ActionItemBadgeAdder;
import com.ristana.how_to.R;
import com.ristana.how_to.adapter.CommentAdapter;
import com.ristana.how_to.adapter.StepAdapter;
import com.ristana.how_to.api.apiClient;
import com.ristana.how_to.api.apiRest;
import com.ristana.how_to.entity.ApiResponse;
import com.ristana.how_to.entity.Comment;
import com.ristana.how_to.entity.Guide;
import com.ristana.how_to.entity.GuideORM;
import com.ristana.how_to.entity.Step;
import com.ristana.how_to.manager.PrefManager;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class GuideActivity extends AppCompatActivity {
    private String id_guide;
    private String image_guide;
    private String time_guide;
    private String title_guide;
    private ImageView image_view_guide;
    private TextView text_view_title_guide;
    private WebView text_view_content_guide;
    private TextView text_view_time_guide;
    private RelativeLayout relativeLayout;
    private ProgressBar guide_progress;
    private RecyclerView recyclerView_steps;
    private List<Step> stepList=new ArrayList<>();
    private StepAdapter stepAdapter;
    private LinearLayoutManager linearLayoutManager;
    private String content_guide;
    private Guide guide=null;
    private Menu mOptionsMenu;
    private CardView card_view_guide_comments;
    private ImageView imageView_empty_comment;
    private RecyclerView recycle_view_comment;
    private ImageButton image_button_comment_add;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentList= new ArrayList<>();
    private LinearLayoutManager linearLayoutManagerSteps;
    private EditText edit_text_comment_add;
    private ProgressBar progress_bar_comment_add;
   // private InterstitialAd mInterstitialAd;
   // private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        Intent intent = getIntent();
        id_guide= intent.getStringExtra("id_guide");
        image_guide = intent.getStringExtra("image_guide");
        time_guide= intent.getStringExtra("time_guide");
        title_guide= intent.getStringExtra("title_guide");
        initView();
        initAction();


    }

    private void setGuide() {
        Picasso.with(getApplicationContext()).load(image_guide).placeholder(R.drawable.image_item).into(this.image_view_guide);
        text_view_time_guide.setText(time_guide);
        text_view_title_guide.setText(title_guide);
        getSupportActionBar().setTitle(title_guide);

        getGuide();
    }

    public void initView(){
        getSupportActionBar().setElevation(5);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.image_view_guide=(ImageView) findViewById(R.id.image_view_guide);
        this.text_view_title_guide=(TextView) findViewById(R.id.text_view_title_guide);
        this.text_view_content_guide=(WebView) findViewById(R.id.text_view_content_guide);
        this.text_view_time_guide=(TextView) findViewById(R.id.text_view_time_guide);
        this.relativeLayout=(RelativeLayout) findViewById(R.id.relativeLayout);
        this.guide_progress=(ProgressBar) findViewById(R.id.guide_progress);
        this.recyclerView_steps=(RecyclerView) findViewById(R.id.recyclerView_steps);
        this.linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.linearLayoutManagerSteps= new LinearLayoutManager(getApplicationContext());
        this.card_view_guide_comments=(CardView) findViewById(R.id.card_view_guide_comments);
        this.edit_text_comment_add=(EditText) findViewById(R.id.edit_text_comment_add);
        this.progress_bar_comment_add=(ProgressBar) findViewById(R.id.progress_bar_comment_add);

        Animation c= AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.initial);
        c.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                card_view_guide_comments.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        card_view_guide_comments.startAnimation(c);



        this.imageView_empty_comment=(ImageView) findViewById(R.id.imageView_empty_comment);
        this.recycle_view_comment=(RecyclerView) findViewById(R.id.recycle_view_comment);
        this.image_button_comment_add=(ImageButton) findViewById(R.id.image_button_comment_add);
        this.image_button_comment_add.setEnabled(false);

        this.linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        this.linearLayoutManagerSteps.setOrientation(LinearLayoutManager.VERTICAL);


        this.commentAdapter = new CommentAdapter(commentList, getApplication());
        this.recycle_view_comment.setHasFixedSize(true);
        this.recycle_view_comment.setAdapter(commentAdapter);
        this.recycle_view_comment.setLayoutManager(linearLayoutManager);

        this.stepAdapter =new StepAdapter(stepList,getApplicationContext());
        this.recyclerView_steps.setHasFixedSize(true);
        this.recyclerView_steps.setAdapter(stepAdapter);
        this.recyclerView_steps.setLayoutManager(linearLayoutManagerSteps);
    }

    public void initAction()
    {
        this.image_button_comment_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addComment();
            }
        });
        this.edit_text_comment_add.addTextChangedListener(new GuideActivity.GuideTextWatcher(this.edit_text_comment_add));
    }
    public void getGuide()
    {
        guide_progress.setVisibility(View.VISIBLE);
        text_view_content_guide.setVisibility(View.GONE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Guide> call = service.getGuide(id_guide);
        call.enqueue(new Callback<Guide>() {
            @Override
            public void onResponse(Call<Guide> call, Response<Guide> response) {
                if(response.isSuccessful()){
                    guide=response.body();
                    ActionItemBadge.update(GuideActivity.this, mOptionsMenu.findItem(R.id.action_comment),getResources().getDrawable( R.drawable.ic_comment), ActionItemBadge.BadgeStyles.DARK_GREY, response.body().getComments().size());
                    text_view_content_guide.loadData(response.body().getContent(), "text/html; charset=utf-8", "utf-8");
                    content_guide=response.body().getContent();
                    guide_progress.setVisibility(View.GONE);
                    text_view_content_guide.setVisibility(View.VISIBLE);
                    stepList.clear();
                    for (int i=0;i<response.body().getSteps().size();i++)
                        stepList.add(response.body().getSteps().get(i));
                    stepAdapter.notifyDataSetChanged();

                    if (response.body().getComments().size()!=0){
                        for (int i=0;i<response.body().getComments().size();i++)
                            commentList.add(response.body().getComments().get(i));

                        commentAdapter.notifyDataSetChanged();
                        imageView_empty_comment.setVisibility(View.GONE);
                        recycle_view_comment.setVisibility(View.VISIBLE);
                    }else{
                        imageView_empty_comment.setVisibility(View.VISIBLE);
                        recycle_view_comment.setVisibility(View.GONE);
                    }


                    recyclerView_steps.setNestedScrollingEnabled(false);
                }else {
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getGuide();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                    guide_progress.setVisibility(View.GONE);
                    text_view_content_guide.setVisibility(View.GONE);
                }

            }
            @Override
            public void onFailure(Call<Guide> call, Throwable t) {
                Snackbar snackbar = Snackbar
                        .make(relativeLayout, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getGuide();
                            }
                        });
                snackbar.setActionTextColor(Color.RED);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
                guide_progress.setVisibility(View.GONE);
                text_view_content_guide.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.mOptionsMenu=menu;
        getMenuInflater().inflate(R.menu.menu_guide, menu);
        List<GuideORM> guideORMList = GuideORM.find(GuideORM.class, "num = ? ", id_guide);
        if (guideORMList.size() == 0) {
            menu.getItem(1).setIcon(R.drawable.ic_favorite_border);
        } else {
            menu.getItem(1).setIcon(R.drawable.ic_favorite_black);
        }
        setGuide();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (card_view_guide_comments.getVisibility() == View.VISIBLE) {
                    Animation c = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.slid_down);
                    c.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            card_view_guide_comments.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    card_view_guide_comments.startAnimation(c);
                    return true;
                }else{
                    NavUtils.navigateUpFromSameTask(this);
                }
                break;
            case R.id.action_save:
                if (guide!=null){
                    List<GuideORM> guideORMList = GuideORM.find(GuideORM.class, "num = ? ", guide.getId()+"");
                    if (guideORMList.size() == 0) {
                        GuideORM guideORM= new GuideORM(guide);
                        guideORM.save();
                        item.setIcon(R.drawable.ic_favorite_black);
                    }else{
                        GuideORM book = GuideORM.findById(GuideORM.class,guideORMList.get(0).getId());
                        if (book!=null){
                            book.delete();
                            item.setIcon(R.drawable.ic_favorite_border);
                        }
                    }
                }
                break;
            case R.id.action_share:
                if (guide!=null) {
                    String shareBody = title_guide + " \n\n " + android.text.Html.fromHtml(content_guide).toString().replace("img{max-width:100% !important}", "") + " \n\n I Would like to share this with you.To Read More You Can Download " + getString(R.string.app_name) + "  Application  from   https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
                }
                break;
            case R.id.action_comment:
                    if (card_view_guide_comments.getVisibility() == View.VISIBLE)
                    {
                       Animation c= AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slid_down);
                        c.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                card_view_guide_comments.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        card_view_guide_comments.startAnimation(c);


                    }else{
                        Animation c= AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slid_up);
                        c.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                card_view_guide_comments.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        card_view_guide_comments.startAnimation(c);

                    }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        if (card_view_guide_comments.getVisibility() == View.VISIBLE) {
            Animation c = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slid_down);
            c.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    card_view_guide_comments.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            card_view_guide_comments.startAnimation(c);
            return;
        }
        super.onBackPressed();
        return;
    }
    public void addComment(){

        PrefManager prf= new PrefManager(getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")){
            progress_bar_comment_add.setVisibility(View.VISIBLE);
            image_button_comment_add.setVisibility(View.GONE);
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.addComment(prf.getString("ID_USER").toString(),id_guide,edit_text_comment_add.getText().toString());
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    if (response.isSuccessful()){
                        if (response.body().getCode()==200){
                            recycle_view_comment.setVisibility(View.VISIBLE);
                            imageView_empty_comment.setVisibility(View.GONE);
                            Toast.makeText(GuideActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            edit_text_comment_add.setText("");
                            String id="";
                            String content="";
                            String author="";
                            String image="";

                            for (int i=0;i<response.body().getValues().size();i++){
                                if (response.body().getValues().get(i).getName().equals("id")){
                                    id=response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("content")){
                                    content=response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("author")){
                                    author=response.body().getValues().get(i).getValue();
                                }
                                if (response.body().getValues().get(i).getName().equals("image")){
                                    image=response.body().getValues().get(i).getValue();
                                }
                            }
                            Comment comment= new Comment();
                            comment.setId(Integer.parseInt(id));
                            comment.setAuthor(author);
                            comment.setContent(content);
                            comment.setImage(image);
                            comment.setEnabled(true);
                            comment.setCreated(getResources().getString(R.string.now_time));
                            commentList.add(comment);
                            commentAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Snackbar snackbar = Snackbar
                                .make(relativeLayout , getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                                .setAction( getResources().getString(R.string.retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        addComment();
                                    }
                                });
                        snackbar.setActionTextColor(Color.RED);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    }
                    recycle_view_comment.scrollToPosition(recycle_view_comment.getAdapter().getItemCount()-1);
                    commentAdapter.notifyDataSetChanged();
                    progress_bar_comment_add.setVisibility(View.GONE);
                    image_button_comment_add.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout , getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction( getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addComment();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();

                    progress_bar_comment_add.setVisibility(View.VISIBLE);
                    image_button_comment_add.setVisibility(View.GONE);
                }
            });
        }else{
            Intent intent = new Intent(GuideActivity.this,LoginActivity.class);
            startActivity(intent);
        }

    }
    private class GuideTextWatcher implements TextWatcher {
        private View view;
        private GuideTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edit_text_comment_add:
                    ValidateComment();
                    break;
            }
        }
    }

    private boolean ValidateComment() {
        String email = edit_text_comment_add.getText().toString().trim();
        if (email.isEmpty()) {
            image_button_comment_add.setEnabled(false);
            return false;
        }else{
            image_button_comment_add.setEnabled(true);
        }
        return true;
    }


}
