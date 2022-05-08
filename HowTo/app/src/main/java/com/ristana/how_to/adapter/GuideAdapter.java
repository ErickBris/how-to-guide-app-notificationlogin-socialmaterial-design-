package com.ristana.how_to.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ristana.how_to.R;
import com.ristana.how_to.entity.Guide;
import com.ristana.how_to.entity.GuideORM;
import com.ristana.how_to.ui.activity.GuideActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hsn on 01/04/2017.
 */

public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.GuideHolder>{
    private  List<Guide> guideList;
    private  Context context;
    private Boolean favorites = false;
    public GuideAdapter(List<Guide> guideList, Context context) {
        this.guideList = guideList;
        this.context = context;
    }
    public GuideAdapter(List<Guide> guideList, Context context,Boolean favorites) {
        this.guideList = guideList;
        this.context = context;
        this.favorites=favorites;
    }
    @Override
    public GuideHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, null, false);
        viewHolder.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new GuideHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(final GuideHolder holder,final int position) {
        holder.text_view_time_article_item.setText(guideList.get(position).getCreated());
        holder.text_view_content.setText(guideList.get(position).getTitle());
        Picasso.with(context).load(guideList.get(position).getImage()).placeholder(R.drawable.image_item).into(holder.image_view_article_item);
        if (guideList.get(position).getCategory() == null) {
            holder.text_view_categoryarticle_item.setText(context.getResources().getString(R.string.global));
        }else {
            holder.text_view_categoryarticle_item.setText(guideList.get(position).getCategory());
        }
        holder.card_view_guide_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(context, GuideActivity.class);

                intent.putExtra("id_guide",guideList.get(position).getId()+"");
                intent.putExtra("image_guide",guideList.get(position).getImage()+"");
                intent.putExtra("time_guide",guideList.get(position).getCreated()+"");
                intent.putExtra("title_guide",guideList.get(position).getTitle()+"");

                holder.card_view_guide_item.getContext().startActivity(intent);
            }
        });
        List<GuideORM>  guideORMLists = GuideORM.find(GuideORM.class, "num = ? ", guideList.get(position).getId()+"");
        if (guideORMLists.size() == 0) {
            holder.image_view_save_article_item.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.image_view_save_article_item.setImageResource(R.drawable.ic_favorite_check);
        }
        holder.image_view_save_article_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<GuideORM> guideORMList = GuideORM.find(GuideORM.class, "num = ? ", guideList.get(position).getId()+"");
                if (guideORMList.size() == 0) {
                    GuideORM guideORM= new GuideORM(guideList.get(position));
                    guideORM.save();
                    holder.image_view_save_article_item.setImageResource(R.drawable.ic_favorite_check);
                }else{
                    GuideORM book = GuideORM.findById(GuideORM.class,guideORMList.get(0).getId());
                    if (book!=null){
                        book.delete();
                        holder.image_view_save_article_item.setImageResource(R.drawable.ic_favorite);
                    }
                    if (favorites==true){
                       guideList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return guideList.size();
    }

    public static class GuideHolder extends RecyclerView.ViewHolder {
        private ImageView image_view_article_item;
        private TextView text_view_categoryarticle_item;
        private TextView text_view_time_article_item;
        private TextView text_view_content;
        private ImageView image_view_save_article_item;
        private CardView card_view_guide_item;
        public GuideHolder(View itemView) {
            super(itemView);
            this.image_view_article_item=(ImageView) itemView.findViewById(R.id.image_view_article_item);
            this.text_view_categoryarticle_item=(TextView) itemView.findViewById(R.id.text_view_categoryarticle_item);
            this.text_view_time_article_item=(TextView) itemView.findViewById(R.id.text_view_time_article_item);
            this.text_view_content=(TextView) itemView.findViewById(R.id.text_view_content);
            this.image_view_save_article_item=(ImageView) itemView.findViewById(R.id.image_view_save_article_item);
            this.card_view_guide_item=(CardView) itemView.findViewById(R.id.card_view_guide_item);
        }
    }
}
