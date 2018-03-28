package me.oogh.similar.murmur;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.oogh.similar.R;
import me.oogh.similar.adapter.MurmurRecycleAdapter;
import me.oogh.similar.common.ActionModeCallback;
import me.oogh.similar.common.Actionable;
import me.oogh.similar.common.OnItemClickListener;
import me.oogh.similar.data.entry.Event;
import me.oogh.similar.data.entry.Murmur;

/**
 * @author oogh <oogh216@163.com>
 * @date 2018-03-04
 * @description
 */

public class TabFutureFragment extends Fragment implements Actionable {
    @BindView(R.id.rv_murmur_future_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.srl_murmur_future_fragment)
    SwipeRefreshLayout mSwipeRefreshView;
    @BindView(R.id.ll_murmur_future_empty)
    LinearLayout mEmptyView;
    private Unbinder unbinder;

    private MurmurRecycleAdapter mAdapter;
    private List<Murmur> mDataSet;

    private ActionMode mActionMode;

    public TabFutureFragment() {

    }

    public static TabFutureFragment newInstance() {
        TabFutureFragment fragment = new TabFutureFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new MurmurRecycleAdapter(getContext(), mDataSet = new ArrayList<>());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.murmur_future_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener(getContext(), mRecyclerView, new OnItemClickListener.Handler() {
            @Override
            public void handleClick(View view, int position) {
                if (mActionMode != null) {
                    handleItemTouched(view, position);
                } else {
                    startActivity(new Intent(getContext(), EditMurmurActivity.class));
                    EventBus.getDefault().postSticky(new Event.MurmurEvent(
                            mDataSet.get(position),
                            Event.Tag.MURMUR_PARAM));
                }
            }

            @Override
            public void handleLongPress(View view, int position) {
                handleItemTouched(view, position);
            }
        }));

    }

    /**
     * 处理Item的触摸
     *
     * @param view
     * @param position
     */
    private void handleItemTouched(View view, int position) {
        mAdapter.toggleSelectStatus(position);
        boolean hasItemSelected = mAdapter.getSelectedItemCount() > 0;
        if (hasItemSelected && mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity())
                    .startSupportActionMode(new ActionModeCallback(this, tag -> {
                        switch (tag) {
                            case DELETE:
                                removeItems();
                                break;
                        }
                    }));
        } else if (!hasItemSelected && mActionMode != null) {
            mActionMode.finish();
        }

        if (mActionMode != null) {
            mActionMode.setTitle(mAdapter.getSelectedItemCount() + " 项被选中");
        }

    }

    /**
     * 删除选中的Items
     */
    private void removeItems() {
        SparseBooleanArray selectedItems = mAdapter.getSelectedItems();
        int count = selectedItems.size();
        for (int i = (count - 1); i >= 0; i--) {
            if (selectedItems.valueAt(i)) {
                Murmur murmur = mDataSet.get(selectedItems.keyAt(i));
                EventBus.getDefault().post(new Event.MurmurEvent(murmur, Event.Tag.MURMUR_REMOVE));
            }
        }
        Toast.makeText(getContext(), count + " 条数据已被删除", Toast.LENGTH_SHORT).show();
        mActionMode.finish();
    }

    /**
     * 接收MurmurList
     *
     * @param event
     */
    @Subscribe(sticky = true)
    public void onReceiveMurmurList(Event.MurmurListEvent event) {
        if (event.tag == Event.Tag.MURMUR_SHOW_LIST) {
            mSwipeRefreshView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            List<Murmur> murmurs = new ArrayList<>();
            for (Murmur murmur : event.murmurs) {
                if ("future".equals(murmur.getTag())) {
                    murmurs.add(murmur);
                }
            }

            if (murmurs.isEmpty()) {
                showEmpty();
            } else {
                mDataSet = murmurs;
                mAdapter.updateDataSet(mDataSet);
            }
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    private void showEmpty() {
        mEmptyView.setVisibility(View.VISIBLE);
        mSwipeRefreshView.setVisibility(View.GONE);
    }

//    /**
//     * 显示空白
//     *
//     * @param event
//     */
//    @Subscribe(sticky = true)
//    public void onShowEmpty(Event.MurmurListEvent event) {
//        if (event.tag == Event.Tag.MURMUR_SHOW_EMPTY) {
//            mEmptyView.setVisibility(View.VISIBLE);
//            mSwipeRefreshView.setVisibility(View.GONE);
//            EventBus.getDefault().removeStickyEvent(event);
//        }
//    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void destroyActionMode() {
        mActionMode = mActionMode == null ? mActionMode : null;
        mAdapter.clearSelectedItems();
    }
}
