package me.oogh.similar.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.oogh.similar.R;
import me.oogh.similar.data.entry.Murmur;
import me.oogh.similar.utils.DateUtils;

/**
 * Created by oogh on 18-3-20.
 */

public class MurmurDailyListViewAdapter extends RecyclerView.Adapter<MurmurDailyListViewAdapter.ViewHolder> {
    private List<Murmur> mDataSet;

    private SparseBooleanArray mSelectedItems;
    private Context mContext;

    public MurmurDailyListViewAdapter(Context context, List<Murmur> dataSet) {
        mContext = context;
        mDataSet = dataSet;
        mSelectedItems = new SparseBooleanArray();
    }

    public SparseBooleanArray getSelectedItems() {
        return mSelectedItems;
    }


    /**
     * 更新数据
     *
     * @param dataSet
     */
    public void updateDataSet(List<Murmur> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    private static final String TAG = MurmurDailyListViewAdapter.class.getSimpleName();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.murmur_daily_recycle_item, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Murmur murmur = mDataSet.get(position);
        holder.date.setText(DateUtils.format(murmur.getDate()));
        holder.content.setText(murmur.getContent());
        Drawable ripple = mContext.getResources().getDrawable(R.drawable.ic_ripple_recycle_item, null);
        Drawable color = mContext.getResources().getDrawable(R.drawable.ic_color_recycle_item, null);
        Drawable drawable = mSelectedItems.get(position) ? color : ripple;
        holder.itemView.setBackground(drawable);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * 切换 itemView的选中状态
     *
     * @param position
     */
    public void toggleSelectStatus(int position) {
        Log.i(TAG, "\n\ntoggleSelectStatus:" + position + " 选中？ " + mSelectedItems.get(position));
        if (mSelectedItems.get(position)) {
            mSelectedItems.delete(position);
        } else {
            mSelectedItems.put(position, true);
        }
        Log.i(TAG, "toggleSelectStatus:" + position + " 选中？ " + mSelectedItems.get(position));
        notifyDataSetChanged();
    }

    /**
     * 获取被选中Item的数量
     *
     * @return
     */
    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    /**
     * 清空存放被选中项的缓存集合
     */
    public void clearSelectedItems() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_murmur_daily_date)
        TextView date;
        @BindView(R.id.tv_murmur_daily_content)
        TextView content;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
