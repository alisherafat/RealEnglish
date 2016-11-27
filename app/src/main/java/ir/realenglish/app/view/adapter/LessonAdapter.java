package ir.realenglish.app.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.koushikdutta.ion.Ion;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.model.Favorite;
import ir.realenglish.app.model.Lesson;
import ir.realenglish.app.model.Score;
import ir.realenglish.app.view.ActivityLesson;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {

    private List<Lesson> items;
    private Context context;
    private List<Score> scores;
    private List<Favorite> favorites;
    private int lastPosition = -1, searchIndex, indexFavorite;
    private Animation animation;
    private Tag tag;


    ColorGenerator generator = ColorGenerator.MATERIAL;
    TextDrawable.IBuilder builder = TextDrawable.builder()
            .beginConfig()
            .width(100)
            .height(100)
            .endConfig()
            .rect();

    public LessonAdapter(Context context, List<Lesson> items, List<Score> scores, List<Favorite> favorites) {
        this.context = context;
        this.items = items;
        this.favorites = favorites;
        this.scores = scores;
        animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_lesson, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = items.get(position);
        holder.txtName.setText(holder.item.name);
        holder.txtNumber.setText(String.valueOf(holder.item.number));
        holder.txtScore.setText(String.valueOf(holder.item.score));
        holder.txtLevel.setText(holder.item.level);
        holder.txtDate.setText(holder.item.getTimestamp());

        searchIndex = searchInScoreList(holder.item.remoteId);
        if (searchIndex != -1) {
            holder.imgLike.setImageResource(R.drawable.ic_thumb_up);
            holder.imgLike.setColorFilter(Color.argb(255, 0, 156, 38), PorterDuff.Mode.SRC_ATOP);
        }

        holder.tagView.removeAllTags();
        for (int i = 0; i < holder.item.tags.size(); i++) {
            if (i > 4) break;
            tag = new Tag(holder.item.tags.get(i).name);
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#EA3C88");
            tag.layoutColorPress = Color.parseColor("#D3065F");
            tag.tagTextSize = 12f;
            holder.tagView.addTag(tag);
        }

        Ion.with(holder.imgThumbnail)
                .placeholder(builder.build(holder.item.getNamePlaceHolder(), generator.getColor(holder.item.name)))
                .fadeIn(false).animateIn(R.anim.fade_in).load(EndPoints.BASE_URL + "/file/lesson/lesson" + holder.item.remoteId + ".png");

        setAnimation(holder.view, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private int searchInScoreList(int id) {
        for (Score score : scores) {
            if (score.scoreableId == id) {
                return scores.indexOf(score);
            }
        }
        return -1;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.view.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private Lesson item;
        @Bind(R.id.imgThumbnail) ImageView imgThumbnail;
        @Bind(R.id.txtLevel) TextView txtLevel;
        @Bind(R.id.txtScore) TextView txtScore;
        @Bind(R.id.imgLike) ImageView imgLike;
        @Bind(R.id.txtName) TextView txtName;
        @Bind(R.id.tagView) TagView tagView;
        @Bind(R.id.txtDate) TextView txtDate;
        @Bind(R.id.txtNumber) TextView txtNumber;
        //   @Bind(R.id.imgFav) ImageView imgFav;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ActivityLesson.class);
                    intent.putExtra(ActivityLesson.LESSON, items.get(getAdapterPosition()));
                    context.startActivity(intent);

                }
            });

        }


        @OnClick(R.id.imgLike)
        void onClick() {
            Lesson item = items.get(getAdapterPosition());
            imgLike.setScaleY(0.5f);
            imgLike.setScaleX(0.5f);
            imgLike.animate().scaleX(1).scaleY(1).setInterpolator(new LinearInterpolator());
            searchIndex = searchInScoreList(item.remoteId);
            if (searchIndex != -1) {
                // cancel like
                imgLike.clearColorFilter();
                txtScore.setText(String.valueOf((Integer.parseInt(txtScore.getText().toString()) - 5)));
                Score.changeLike(context, imgLike, "lesson", item.remoteId, false);
                scores.remove(searchIndex);
            } else {
                // like
                imgLike.setColorFilter(Color.argb(255, 0, 156, 38), PorterDuff.Mode.SRC_ATOP);
                txtScore.setText(String.valueOf((Integer.parseInt(txtScore.getText().toString()) + 5)));
                Score score = Score.changeLike(context, imgLike, "lesson", item.remoteId, true);
                scores.add(score);
            }
        }
    }
}