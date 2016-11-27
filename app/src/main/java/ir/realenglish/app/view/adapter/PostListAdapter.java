package ir.realenglish.app.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.koushikdutta.ion.Ion;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.model.Favorite;
import ir.realenglish.app.model.Post;
import ir.realenglish.app.model.Report;
import ir.realenglish.app.model.Score;
import ir.realenglish.app.utils.Utils;
import ir.realenglish.app.view.activity.ActivityPost;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;


public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.ViewHolder> {

    private Context context;
    private List<Post> list;
    private List<Score> scoreList;
    private List<Favorite> favoriteList;
    private int lastPosition = -1, searchIndex, indexFavorite;
    private Animation animation, fadeInAnim;
    private Post item;
    private Tag tag;


    ColorGenerator generator = ColorGenerator.MATERIAL;
    TextDrawable.IBuilder builder = TextDrawable.builder()
            .beginConfig()
            .width(100)
            .height(100)
            .endConfig()
            .rect();

    public PostListAdapter(Context context, List<Post> list, List<Score> scores, List<Favorite> favorites) {
        this.context = context;
        this.list = list;
        this.favoriteList = favorites;
        this.scoreList = scores;
        animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);

        fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_post, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        item = list.get(position);
        viewHolder.txtTitle.setText(item.title);
        viewHolder.txtDesc.setText(item.description);
        viewHolder.txtUsername.setText(item.user.name);
        viewHolder.txtDate.setText(getUserTimeStamp(item.timestamp));
        viewHolder.txtScore.setText("" + item.score);
        if (isFavorite(item.remoteId) != -1) {
            viewHolder.imgFav.setImageResource(R.mipmap.ic_heart);
        } else {
            viewHolder.imgFav.setImageResource(R.mipmap.ic_heart_empty);
        }

        searchIndex = searchInScoreList(item.remoteId);
        if (searchIndex != -1) {
            viewHolder.imgLike.setImageResource(R.drawable.ic_thumb_up);
            viewHolder.imgLike.setColorFilter(Color.argb(255, 0, 156, 38), PorterDuff.Mode.SRC_ATOP);
        }

        viewHolder.tagView.removeAllTags();
        for (int i = 0; i < item.tags.size(); i++) {
            tag = new Tag(item.tags.get(i).name);
            tag.tagTextColor = Color.parseColor("#FFFFFF");
            tag.layoutColor = Color.parseColor("#EA3C88");
            tag.layoutColorPress = Color.parseColor("#D3065F");
            viewHolder.tagView.addTag(tag);
        }

        int color = generator.getColor(item.user.name);
        if (item.user.hasThumbnail()) {
            Ion.with(viewHolder.circleThumb).placeholder(builder.build(item.user.getTextDrawable(), color)).fadeIn(false).animateIn(R.anim.fade_in).load(EndPoints.IMAGE_URL + "/user/"
                    + item.user.thumbnail);
        } else {
            viewHolder.circleThumb.setImageDrawable(builder.build(item.user.getTextDrawable(), color));
        }

        setAnimation(viewHolder.cardView, position);
    }

    private int searchInScoreList(int id) {
        for (Score score : scoreList) {
            if (score.scoreableId == id) {
                return scoreList.indexOf(score);
            }
        }
        return -1;
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private String getUserTimeStamp(String timestamp) {
        try {
            return Utils.getPrettyDate(timestamp, true);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.clearAnimation();
    }

    private int isFavorite(int remoteId) {
        for (Favorite favorite : favoriteList) {
            if (favorite.favoriteableId == remoteId) {
                return favoriteList.indexOf(favorite);
            }
        }
        return -1;
    }

    private void showPopupMenu(final View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenu().add("Hi");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        popup.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.cardView) CardView cardView;
        @Bind(R.id.txtTitle) TextView txtTitle;
        @Bind(R.id.txtDesc) TextView txtDesc;
        @Bind(R.id.circleThumb) ImageView circleThumb;
        @Bind(R.id.imgLike) ImageView imgLike;
        @Bind(R.id.imgFav) ImageView imgFav;
        @Bind(R.id.tagView) TagView tagView;
        @Bind(R.id.txtDate) TextView txtDate;
        @Bind(R.id.txtScore) TextView txtScore;
        @Bind(R.id.txtUsername) TextView txtUsername;
        @Bind(R.id.btnReport) Button btnReport;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            btnReport.setOnClickListener(this);
            imgLike.setOnClickListener(this);
            imgFav.setOnClickListener(this);
            btnReport.setOnClickListener(this);
            cardView.setOnClickListener(this);


        }

        public void clearAnimation() {
            cardView.clearAnimation();
        }

        @Override
        public void onClick(View v) {
            int postion = getAdapterPosition();
            Post thisPost = list.get(postion);
            switch (v.getId()) {
                case R.id.btnReport:
                    Report.showDialog(context, btnReport, "post", thisPost.remoteId);
                    break;
                case R.id.imgLike:
                    imgLike.setScaleY(0.5f);
                    imgLike.setScaleX(0.5f);
                    imgLike.animate().scaleX(1).scaleY(1).setInterpolator(new LinearInterpolator());
                    searchIndex = searchInScoreList(list.get(postion).remoteId);
                    if (searchIndex != -1) {
                        // cancel like
                        imgLike.clearColorFilter();
                        txtScore.setText(String.valueOf((Integer.parseInt(txtScore.getText().toString()) - 5)));
                        Score.changeLike(context, imgLike, "post", thisPost.remoteId, false);
                        scoreList.remove(searchIndex);
                    } else {
                        // like
                        imgLike.setColorFilter(Color.argb(255, 0, 156, 38), PorterDuff.Mode.SRC_ATOP);
                        txtScore.setText(String.valueOf((Integer.parseInt(txtScore.getText().toString()) + 5)));
                        Score score = Score.changeLike(context, imgLike, "post", thisPost.remoteId, true);
                        scoreList.add(score);
                    }
                    break;
                case R.id.imgFav:
                    imgFav.setScaleX(0.5f);
                    imgFav.setScaleY(.05f);
                    indexFavorite = isFavorite(thisPost.remoteId);
                    if (indexFavorite != -1) {
                        imgFav.setImageResource(R.mipmap.ic_heart_empty);
                        imgFav.animate().scaleY(1).scaleX(1);
                        favoriteList.get(indexFavorite).delete();
                        favoriteList.remove(indexFavorite);
                        Favorite.changeFavoriteList(context, imgFav, "post", thisPost.remoteId, false);
                    } else {
                        imgFav.setImageResource(R.mipmap.ic_heart);
                        imgFav.animate().scaleY(1).scaleX(1).setInterpolator(new OvershootInterpolator());
                        Favorite favorite = Favorite.changeFavoriteList(context, imgFav, "post", thisPost.remoteId, true);
                        favoriteList.add(favorite);
                    }
                    break;
                case R.id.cardView:
                    Intent intent = new Intent(context, ActivityPost.class);
                    intent.putExtra("idiom", thisPost);
                    context.startActivity(intent);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}