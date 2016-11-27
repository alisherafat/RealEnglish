package ir.realenglish.app.view.adapter;

/**
 * Created by ALI on 8/29/2016.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.model.Test;
import ir.realenglish.app.presenter.UserService;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    private List<Test> items;
    private Context context;
    private OnEditTestClickListener listener;
    private boolean mustShowEditOption;

    public TestAdapter(Context context, List<Test> items) {
        this.context = context;
        this.items = items;
        mustShowEditOption = UserService.grantSuper();
    }

    public void setOnEditTestClickListener(OnEditTestClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_test, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item = items.get(position);
        holder.txtTitle.setText(holder.item.title);

        holder.option1.setText(holder.item.options[0]);
        holder.option2.setText(holder.item.options[1]);
        holder.option3.setText(holder.item.options[2]);
        holder.option4.setText(holder.item.options[3]);

        if (holder.item.userAnswer == -1) {
            holder.radioGroup.clearCheck();
        } else {
            ((RadioButton) holder.radioGroup.getChildAt((holder.item.userAnswer - 1))).setChecked(true);
        }

        if (holder.item.mustShowResult) {
            holder.lytWrong.setVisibility(View.GONE);
            holder.lytCorrect.setVisibility(View.VISIBLE);
            holder.txtCorrect.setText(String.valueOf(holder.item.answer));
            if (holder.item.userAnswer != holder.item.answer && holder.item.userAnswer != -1) {
                holder.lytWrong.setVisibility(View.VISIBLE);
                holder.txtWrong.setText(String.valueOf(holder.item.userAnswer));
            }
            /*
            if (holder.item.userAnswer == -1) {
                  ((RadioButton) holder.radioGroup.getChildAt((holder.item.answer - 1))).setChecked(true);
            }
            */
        }

        if (mustShowEditOption) {
            holder.imgMenu.setVisibility(View.VISIBLE);
        }

    }

    public void showResult() {
        for (Test test : items) {
            test.mustShowResult = true;
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Test item;
        @Bind(R.id.txtTitle) TextView txtTitle;
        @Bind(R.id.option1) RadioButton option1;
        @Bind(R.id.option2) RadioButton option2;
        @Bind(R.id.option3) RadioButton option3;
        @Bind(R.id.option4) RadioButton option4;
        @Bind(R.id.radioGroup) RadioGroup radioGroup;
        @Bind(R.id.lytCorrect) LinearLayout lytCorrect;
        @Bind(R.id.lytWrong) LinearLayout lytWrong;
        @Bind(R.id.txtCorrect) TextView txtCorrect;
        @Bind(R.id.txtWrong) TextView txtWrong;
        @Bind(R.id.imgMenu) ImageView imgMenu;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
/*
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Test item = items.get(getAdapterPosition());
                    Utils.log("called: "  + item.title);
                    switch (checkedId) {
                        case R.id.option1:
                            item.userAnswer = 1;
                            break;
                        case R.id.option2:
                            item.userAnswer = 2;
                            break;
                        case R.id.option3:
                            item.userAnswer = 3;
                            break;
                        case R.id.option4:
                            item.userAnswer = 4;
                            break;
                    }
                }
            });
            */
            option1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        item.userAnswer = 1;
                    }
                }
            });

            option2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        item.userAnswer = 2;
                    }
                }
            });

            option3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        item.userAnswer = 3;
                    }
                }
            });

            option4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        item.userAnswer = 4;
                    }
                }
            });
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
                                    listener.onEditTestClicked(getAdapterPosition());
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

    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface OnEditTestClickListener {
        void onEditTestClicked(int position);
    }
}