package ir.realenglish.app.view.adapter;

/**
 * Created by ALI on 8/25/2016.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.model.Word;
import ir.realenglish.app.presenter.UserService;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    private List<Word> items;
    private Context context;
    private OnEditWordClickListener listener;
    private boolean mustShowEditOption;

    public WordAdapter(Context context, List<Word> items) {
        this.context = context;
        this.items = items;
        mustShowEditOption = UserService.grantSuper();
    }

    public void setOnEditWordClickListener(OnEditWordClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_word, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item = items.get(position);
        holder.txtTitle.setText(holder.item.title);
        holder.txtType.setText(holder.item.type);
        holder.txtBody.setText(holder.item.body);

        if (holder.item.hasLink()) {
            holder.btnLink.setVisibility(View.VISIBLE);
            holder.btnLink.setText(holder.item.link);
        }

        if (mustShowEditOption) {
            holder.imgMenu.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private Word item;
        @Bind(R.id.txtTitle) TextView txtTitle;
        @Bind(R.id.txtBody) TextView txtBody;
        @Bind(R.id.txtType) TextView txtType;
        @Bind(R.id.btnLink) Button btnLink;
@Bind(R.id.imgMenu)
        ImageView imgMenu;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = view;
        }

        @OnClick({R.id.btnLink, R.id.imgMenu})
        void onClick(View view) {
            final Word word = items.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.imgMenu: {
                    final PopupMenu popup = new PopupMenu(view.getContext(), view);
                    popup.inflate(R.menu.menu_idiom_item_text);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.item_edit) {
                                if (listener != null) {
                                    listener.onEditClicked(getAdapterPosition());
                                }
                            } else {
                                deleteItemText(getAdapterPosition());
                            }
                            return true;
                        }
                    });
                    popup.show();
                    break;
                }
            }
        }
    }

    private void deleteItemText(final int position) {
        new MaterialDialog.Builder(context).positiveText("yes").negativeText("cancel").title("Delete")
                .content("Are you sure you want to delete this item?").iconRes(R.mipmap.ic_action_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(
                            @NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        items.remove(position);
                        notifyItemRemoved(position);
                    }
                }).build().show();
    }

    public interface OnEditWordClickListener {
        void onEditClicked(int position);
    }


}
