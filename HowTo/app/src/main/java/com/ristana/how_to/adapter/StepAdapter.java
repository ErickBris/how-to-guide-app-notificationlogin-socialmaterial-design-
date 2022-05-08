package com.ristana.how_to.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ristana.how_to.R;
import com.ristana.how_to.entity.Guide;
import com.ristana.how_to.entity.Step;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hsn on 04/04/2017.
 */

public class StepAdapter   extends RecyclerView.Adapter<StepAdapter.StepHolder>{
    private List<Step> stepList;
    private Context context;
    public StepAdapter(List<Step> stepList, Context context) {
        this.stepList = stepList;
        this.context = context;
    }

    @Override
    public StepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_item, null, false);
        viewHolder.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new StepAdapter.StepHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(StepHolder holder, int position) {
        holder.text_view_pos_step_item.setText(stepList.get(position).getPosition().toString());
        holder.text_view_title_step_item.setText(stepList.get(position).getTitle().toString());
        if (stepList.get(position).getImage()==null){
            holder.image_view_img_step_item.setVisibility(View.GONE);
        }else{
            Picasso.with(context).load(stepList.get(position).getImage()).placeholder(R.drawable.image_item).into(holder.image_view_img_step_item);

        }
        holder.web_view_content_step_item.loadData(stepList.get(position).getContent(), "text/html; charset=utf-8", "utf-8");

    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }
    public static class StepHolder extends RecyclerView.ViewHolder {
        private  TextView text_view_pos_step_item;
        private  TextView text_view_title_step_item;
        private  ImageView image_view_img_step_item;
        private LinearLayout linearLayout_step_item;
        private WebView web_view_content_step_item;
        public StepHolder(View itemView) {
            super(itemView);
            this.linearLayout_step_item=(LinearLayout) itemView.findViewById(R.id.linearLayout_step_item);
            this.text_view_pos_step_item=(TextView) itemView.findViewById(R.id.text_view_pos_step_item);
            this.text_view_title_step_item=(TextView) itemView.findViewById(R.id.text_view_title_step_item);
            this.image_view_img_step_item=(ImageView) itemView.findViewById(R.id.image_view_img_step_item);
            this.web_view_content_step_item=(WebView) itemView.findViewById(R.id.web_view_content_step_item);
        }
    }
}
