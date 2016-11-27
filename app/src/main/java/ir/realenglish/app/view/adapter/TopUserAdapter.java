package ir.realenglish.app.view.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import ir.realenglish.app.model.User;


public class TopUserAdapter extends RecyclerView.Adapter<TopUserAdapter.ViewHolder> {

    private List<User> userList;
    private Context context;
    private int lastPosition = -1;
    Animation animation;
    ColorGenerator generator = ColorGenerator.MATERIAL;
    TextDrawable.IBuilder builder = TextDrawable.builder()
            .rect();

    public TopUserAdapter(List<User> topUserList, Context context) {
        userList = topUserList;
        this.context = context;
        animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_top_user_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        User user = userList.get(position);
        viewHolder.txtUsername.setText(user.name);
        viewHolder.txtScore.setText(String.valueOf("+" + user.score));
        viewHolder.txtRegDate.setText(user.getPrettyTimeStamp());
        int color = generator.getColor(user.name);
        if (user.hasThumbnail()) {
            Ion.with(viewHolder.imgUser).placeholder(builder.build(user.getTextDrawable(), color)).fadeIn(false).animateIn(R.anim.fade_in)
                    .load(EndPoints.IMAGE_URL + "/user/" + user.thumbnail);
        } else {
            viewHolder.imgUser.setImageDrawable(builder.build(user.getTextDrawable(), color));
        }
        setAnimation(viewHolder.cardView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.clearAnimation();
    }

    private void showPopupMenu(final View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        final MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup_chat, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        popup.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cardView) CardView cardView;
        @Bind(R.id.txtUsername) TextView txtUsername;
        @Bind(R.id.txtScore) TextView txtScore;
        @Bind(R.id.txtRegDate) TextView txtRegDate;
        @Bind(R.id.imgUser) ImageView imgUser;
        //  @Bind(R.id.imageButton) ImageButton imageButton;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            //imageButton.setOnClickListener(this);
        }

        public void clearAnimation() {
            cardView.clearAnimation();
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}