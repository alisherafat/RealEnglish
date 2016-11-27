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
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.model.Lesson;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private List<Lesson.File> items;
    private Context context;
    private OnEditFileClickListener listener;

    public FileAdapter(Context context, List<Lesson.File> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnEditFileClickListener(OnEditFileClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_file, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item = items.get(position);
        holder.txtName.setText(holder.item.name);
        holder.txtPath.setText(holder.item.path);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Lesson.File item;
        @Bind(R.id.txtName) TextView txtName;
        @Bind(R.id.txtPath) TextView txtPath;
        @Bind(R.id.imgMenu) ImageView imgMenu;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.imgMenu})
        void onClick(View view) {
            switch (view.getId()) {
                case R.id.imgMenu: {
                    final PopupMenu popup = new PopupMenu(view.getContext(), view);
                    popup.inflate(R.menu.menu_idiom_item_text);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.item_edit) {
                                if (listener != null) {
                                    listener.onEditFileClicked(getAdapterPosition());
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

    public interface OnEditFileClickListener {
        void onEditFileClicked(int position);
    }


}
