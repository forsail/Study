package cn.steve.share.sdk.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.steve.bottomsheet.GridSpacingItemDecoration;
import cn.steve.share.sdk.ShareAction;
import cn.steve.study.R;

/**
 * Created by yantinggeng on 2016/4/11.
 */
public class BottomSheetShareFragment extends AppCompatDialogFragment {

    public static final String SHAREITEMS = "SHAREITEMS";
    private ArrayList<ShareItem> shareItems;
    private ShareGridAdapter adapter;

    public BottomSheetShareFragment() {
    }

    public BottomSheetShareFragment(ArrayList<ShareItem> shareItems) {
        super();
        this.shareItems = shareItems;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.adapter = new ShareGridAdapter(this.shareItems);

        View view = inflater.inflate(R.layout.fragment_bottom_sheet_share, container, false);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.bottomSheetShareRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int spacing = getResources().getDimensionPixelSize(R.dimen.space_14);
        int spanCount = 4;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setListener(new ShareGridAdapter.OnItemCLickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                ShareItem shareItem = shareItems.get(itemPosition);
                ShareAction action = shareItem.getAction();
                if (action == null) {
                    return;
                }
                action.share(shareItem.getShareData(), shareItem.getShareCallBack());
                BottomSheetShareFragment.this.dismiss();
            }
        });

        view.findViewById(R.id.shareButtonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetShareDialog bottomSheetShareDialog = new BottomSheetShareDialog(getActivity(), getTheme());
        bottomSheetShareDialog.setDefaultState(BottomSheetBehavior.STATE_EXPANDED);
        return bottomSheetShareDialog;
    }
}
