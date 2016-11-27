package ir.realenglish.app.view.adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemDrawableTypes;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemResults;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.model.PushNotification;

/**
 * Created by ALI on 8/19/2016.
 */
public class PushNotificationAdapter extends RecyclerView.Adapter<PushNotificationAdapter.ViewHolder>
        implements SwipeableItemAdapter<PushNotificationAdapter.ViewHolder> {
    private Context context;
    private List<PushNotification> items;
    private Snackbar snackbar;

    interface Swipeable extends SwipeableItemConstants {
    }


    public PushNotificationAdapter(Context context, List<PushNotification> items) {
        this.context = context;
        this.items = items;
        setHasStableIds(true); // this is required for swiping feature.
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item = items.get(position);
        holder.txtTitle.setText(holder.item.title);
        holder.txtBody.setText(holder.item.body);
        holder.txtTimestamp.setText(holder.item.getNiceTimestamp());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public long getItemId(int position) {
        return items.get(position).remoteID;
    }

    @Override
    public SwipeResultAction onSwipeItem(ViewHolder holder, int position,
                                         @SwipeableItemResults int result) {
        if (result == Swipeable.RESULT_CANCELED) {
            return new SwipeResultActionDefault();
        } else {
            return new MySwipeResultActionRemoveItem(this, holder, position);
        }
    }

    @Override
    public int onGetSwipeReactionType(ViewHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_RIGHT;
    }

    @Override
    public void onSetSwipeBackground(ViewHolder holder, int position,
                                     @SwipeableItemDrawableTypes int type) {
    }

    static class MySwipeResultActionRemoveItem extends SwipeResultActionRemoveItem {
        private PushNotificationAdapter adapter;
        private int position;
        private ViewHolder holder;

        public MySwipeResultActionRemoveItem(PushNotificationAdapter adapter, ViewHolder holder, int position) {
            this.adapter = adapter;
            this.holder = holder;
            this.position = position;
        }

        @Override
        protected void onPerformAction() {
            adapter.snackbar = Snackbar.make(holder.view, "Notification Deleted. ", Snackbar.LENGTH_SHORT);
            adapter.snackbar.show();

            PushNotification.deleteByRemoteId(adapter.items.get(position).remoteID);
            adapter.items.remove(position);
            adapter.notifyItemRemoved(position);

        }
    }


    public class ViewHolder extends AbstractSwipeableItemViewHolder {
        public final View view;
        public PushNotification item;
        @Bind(R.id.txtTitle) TextView txtTitle;
        @Bind(R.id.txtTimestamp) TextView txtTimestamp;
        @Bind(R.id.txtBody) TextView txtBody;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.get(getAdapterPosition()).handleClick(context);
                }
            });
        }

        @Override
        public View getSwipeableContainerView() {
            return view;
        }

        @OnClick({R.id.txtTimestamp})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.txtTimestamp:
                    break;
            }
        }
    }
}
