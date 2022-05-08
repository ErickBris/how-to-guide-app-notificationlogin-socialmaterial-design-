package com.ristana.how_to.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ristana.how_to.R;
import com.ristana.how_to.entity.Comment;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsn on 06/04/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private List<Comment> commentList= new ArrayList<>();
    private Context context;
    public CommentAdapter(List<Comment> commentList, Context context){
        this.context=context;
        this.commentList=commentList;
    }
    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, null, false);
        viewHolder.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new CommentAdapter.CommentHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        holder.text_view_time_item_comment.setText(commentList.get(position).getCreated());
        holder.text_view_name_item_comment.setText(commentList.get(position).getAuthor());
        Picasso.with(context).load(commentList.get(position).getImage()).placeholder(R.drawable.image_item).into(holder.image_view_comment_iten);
        if (!commentList.get(position).getEnabled()){
            holder.text_view_content_item_comment.setText(context.getResources().getString(R.string.comment_hidden));
            holder.text_view_content_item_comment.setTextColor(context.getResources().getColor(R.color.graycolor));
        }else{
            holder.text_view_content_item_comment.setText(commentList.get(position).getContent());
        }
    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_name_item_comment;
        private final TextView text_view_time_item_comment;
        private final TextView text_view_content_item_comment;
        private final ImageView image_view_comment_iten;
        public CommentHolder(View itemView) {
            super(itemView);
            this.image_view_comment_iten=(ImageView) itemView.findViewById(R.id.image_view_comment_iten);
            this.text_view_name_item_comment=(TextView) itemView.findViewById(R.id.text_view_name_item_comment);
            this.text_view_time_item_comment=(TextView) itemView.findViewById(R.id.text_view_time_item_comment);
            this.text_view_content_item_comment=(TextView) itemView.findViewById(R.id.text_view_content_item_comment);
        }
    }
}
