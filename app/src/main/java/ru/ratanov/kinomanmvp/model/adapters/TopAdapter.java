package ru.ratanov.kinomanmvp.model.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.ratanov.kinomanmvp.R;
import ru.ratanov.kinomanmvp.model.content.TopItem;
import ru.ratanov.kinomanmvp.model.parsers.FilmParser;
import ru.ratanov.kinomanmvp.ui.activity.detail.DetailActivity;

/**
 * Created by ACER on 27.11.2016.
 */

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.TopViewHolder> {

    private Context mContext;
    private List<TopItem> mItems;

    public TopAdapter(Context context, List<TopItem> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public TopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_tile_item, parent, false);
        return new TopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TopViewHolder holder, int position) {
        TopItem resultsItem = mItems.get(position);
        holder.bindItem(resultsItem);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class TopViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        TopViewHolder(View view) {
            super(view);
            mImageView = (ImageView) itemView.findViewById(R.id.tile_picture);
        }

        void bindItem(final TopItem resultsItem) {
            Picasso.with(mContext)
                    .load(resultsItem.getPictureUrl())
                    .into(mImageView);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FilmParser filmParser = new FilmParser();
                    if (filmParser.isFilmBlocked(resultsItem.getLink())) {
                        Snackbar.make(view, R.string.blocked, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Intent intent = DetailActivity.newIntent(mContext, resultsItem.getLink());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
