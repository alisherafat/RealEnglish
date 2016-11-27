package ir.realenglish.app.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.koushikdutta.ion.Ion;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.model.Comment;
import ir.realenglish.app.model.Report;
import ir.realenglish.app.model.Score;
import ir.realenglish.app.utils.Utils;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context context;
    private List<Comment> comments;
    private Comment comment;
    private int selfId, searchIndex;
    private List<Score> scoreList;

    ColorGenerator generator = ColorGenerator.MATERIAL;
    TextDrawable.IBuilder builder = TextDrawable.builder()
            .beginConfig()
            .width(100)
            .height(100)
            .endConfig()
            .rect();


    public CommentAdapter(Context context, List<Comment> comments, List<Score> scores, int selfId) {
        this.context = context;
        this.comments = comments;
        this.scoreList = scores;
        this.selfId = selfId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        comment = comments.get(position);
        viewHolder.txtBody.setText(comment.body);
        viewHolder.txtUsername.setText(comment.user.name);
        viewHolder.txtTimestamp.setText(comment.getPrettyTimeStamp());
        viewHolder.txtScore.setText(String.valueOf(comment.score));
        if (isSelf(comment)) {
            viewHolder.imgMenu.setVisibility(View.VISIBLE);
        }

        searchIndex = searchInScoreList(comment.remoteId);
        if (searchIndex != -1) {
            viewHolder.imgLike.setColorFilter(Color.argb(255, 0, 156, 38), PorterDuff.Mode.SRC_ATOP);
        }

        int color = generator.getColor(comment.user.name);
        if (comment.user.hasThumbnail()) {
            Ion.with(viewHolder.imgThumbnail).placeholder(builder.build(comment.user.getTextDrawable(), color))
                    .fadeIn(false).animateIn(R.anim.fade_in).load(EndPoints.IMAGE_URL + "/user/" + comment.user.thumbnail);
        } else {
            viewHolder.imgThumbnail.setImageDrawable(builder.build(comment.user.getTextDrawable(), color));
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.clearAnimation();
    }

    private int searchInScoreList(int id) {
        for (Score score : scoreList) {
            if (score.scoreableId == id) {
                return scoreList.indexOf(score);
            }
        }
        return -1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.imgThumbnail) ImageView imgThumbnail;
        @Bind(R.id.txtUsername) TextView txtUsername;
        @Bind(R.id.txtBody) TextView txtBody;
        @Bind(R.id.btnReport) Button btnReport;
        @Bind(R.id.txtTimestamp) TextView txtTimestamp;
        @Bind(R.id.imgMenu) ImageView imgMenu;
        @Bind(R.id.txtScore) TextView txtScore;
        @Bind(R.id.imgLike) ImageView imgLike;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            btnReport.setOnClickListener(this);
            imgMenu.setOnClickListener(this);
            imgLike.setOnClickListener(this);
        }

        public void clearAnimation() {
            //    cardView.clearAnimation();
        }

        @Override
        public void onClick(View v) {
            int postion = getAdapterPosition();
            Comment thisComment = comments.get(postion);
            switch (v.getId()) {
                case R.id.imgMenu:
                    showItemMenu(v, postion);
                    break;
                case R.id.btnReport:
                    Report.showDialog(context, btnReport, "comment", thisComment.remoteId);
                    break;
                case R.id.imgLike:
                    imgLike.setScaleY(0.5f);
                    imgLike.setScaleX(0.5f);
                    imgLike.animate().scaleX(1).scaleY(1).setInterpolator(new LinearInterpolator());
                    searchIndex = searchInScoreList(comments.get(postion).remoteId);
                    if (searchIndex != -1) {
                        // cancel like
                        imgLike.clearColorFilter();
                        txtScore.setText(String.valueOf((Integer.parseInt(txtScore.getText().toString()) - 5)));
                        Score.changeLike(context, imgLike, "comment", thisComment.remoteId, false);
                        scoreList.remove(searchIndex);
                    } else {
                        // like
                        imgLike.setColorFilter(Color.argb(255, 0, 156, 38), PorterDuff.Mode.SRC_ATOP);
                        txtScore.setText(String.valueOf((Integer.parseInt(txtScore.getText().toString()) + 5)));
                        Score score = Score.changeLike(context, imgLike, "comment", thisComment.remoteId, true);
                        scoreList.add(score);
                    }
                    break;
            }

        }
    }

    private void showItemMenu(final View view, final int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.menu_comment);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.item_edit) {
                    showInputDialog(position);
                } else {
                    deleteItemText(position);
                }
                return true;
            }
        });
        popup.show();
    }

    public void showInputDialog(final int position) {
        final Comment comment = comments.get(position);
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .positiveText("submit")
                .inputRange(5, 255)
                .input("type...", comment.body, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input.toString().trim().isEmpty()) {
                            Utils.toast("oh, it's empty!");
                            return;
                        }
                        Comment.update(context, comment.remoteId, input.toString());
                        comment.body = input.toString();
                        notifyItemChanged(position);
                    }
                }).build();

        dialog.show();
    }

    private void deleteItemText(final int position) {
        new MaterialDialog.Builder(context)
                .positiveText("yes")
                .negativeText("cancel")
                .title("Delete")
                .content("Are you sure you want to delete this comment?")
                .icon(new IconicsDrawable(context).icon(GoogleMaterial.Icon.gmd_delete).color(Color.RED).sizeDp(24))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(
                            @NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Comment.remove(context, comments.get(position).remoteId);
                        comments.remove(position);
                        notifyItemRemoved(position);
                    }
                }).build().show();
    }

    private boolean isSelf(Comment comment) {
        if (comment.user.id == selfId) {
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

}