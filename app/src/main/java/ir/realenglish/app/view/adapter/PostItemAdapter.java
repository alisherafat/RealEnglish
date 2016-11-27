package ir.realenglish.app.view.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ir.realenglish.app.R;
import ir.realenglish.app.app.Config;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.custom.AudioPlayer;
import ir.realenglish.app.model.Post;
import ir.realenglish.app.utils.Utils;


public class PostItemAdapter extends RecyclerView.Adapter<PostItemAdapter.ViewHolder> implements DraggableItemAdapter<PostItemAdapter.ViewHolder> {

    private Context context;
    private List<Post.Item> itemList;
    private int lastPosition = -1, searchIndex, postId;
    private Animation animation, fadeIn;
    private boolean isLocal;
    private final int TYPE_TEXT = 1, TYPE_IMAGE = 2, TYPE_AUDIO = 3;
    private MediaPlayer mediaPlayer;
    private List<String> readyFiles;
    private List<String> downloadinFiles = new ArrayList<>();

    private String path, currentPlayingName = "";
    private File filePath;
    private Post post;
    private int currentPlayingId = -2;
    private AudioPlayer currenAudioPlayer;
    public static final int REQUEST_TEXT_CODE = 1196;
    private OnTextItemEditClick listener;

    public PostItemAdapter(Context context, Post post, boolean isLocal) {
        this.context = context;
        this.post = post;
        itemList = post.getItems();
        this.isLocal = isLocal;
        readyFiles = post.getReadyFiles();
        if (post.remoteId > 0) {
            path = Config.DIR_POST + File.separator + post.remoteId;
            filePath = new File(path);
        }
        animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
        fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);

        mediaPlayer = new MediaPlayer();

        setHasStableIds(true);
    }

    public void setTextItemEditListener(OnTextItemEditClick listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_IMAGE:
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_post_item_image, parent, false), viewType);
            case TYPE_TEXT:
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_post_item_text, parent, false), viewType);
            case TYPE_AUDIO:
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_post_item_audio, parent, false), viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.item = itemList.get(position);
        switch (getItemViewType(position)) {
            case TYPE_IMAGE:
                handleImageItem(viewHolder, position);
                break;
            case TYPE_TEXT:
                handleTextItem(viewHolder, position);
                break;
            case TYPE_AUDIO:
                handleAudioItem(viewHolder, position);
                break;
        }
        setAnimation(viewHolder.cardView, position);
    }

    private void handleAudioItem(ViewHolder viewHolder, int position) {
        if (isLocal) {
            viewHolder.btnDelete.setVisibility(View.VISIBLE);
        }
        if (readyFiles.contains(viewHolder.item.body)) {
            viewHolder.audioPlayer.setStatus(AudioPlayer.READY);
        } else if (downloadinFiles.contains(viewHolder.item.body)) {
            viewHolder.audioPlayer.setStatus(AudioPlayer.DOWNLOADING);
        }
    }

    private void handleTextItem(ViewHolder viewHolder, int position) {
        if (isLocal) {
            viewHolder.imgMenu.setVisibility(View.VISIBLE);
        }
        viewHolder.txtItem.setText(viewHolder.item.body);
    }

    private void handleImageItem(final ViewHolder viewHolder, int position) {
        if (isLocal) {
            viewHolder.btnDelete.setVisibility(View.VISIBLE);
            if (viewHolder.item.localPath != null) {
                viewHolder.imgItem.setImageURI(Uri.fromFile((new File(itemList.get(position).localPath))));
                return;
            }
        }
        viewHolder.progressItem.setVisibility(View.VISIBLE);
        Ion.with(viewHolder.imgItem).load("GET", EndPoints.POST_FILE_DIRECTORY + "/"
                + viewHolder.item.body).setCallback(new FutureCallback<ImageView>() {
            @Override
            public void onCompleted(Exception e, ImageView result) {
                viewHolder.progressItem.setVisibility(View.GONE);
            }
        });

    }


    private void showItemMenu(final View view, final int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.menu_idiom_item_text);
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

    private void deleteItemText(final int position) {
        new MaterialDialog.Builder(context).positiveText("yes").negativeText("cancel").title("Delete")
                .content("Are you sure you want to delete this item?").iconRes(R.mipmap.ic_action_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(
                            @NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        itemList.remove(position);
                        notifyItemRemoved(position);
                    }
                }).build().show();
    }

    private void showInputDialog(final int position) {
        if (listener != null) {
            listener.onClick(position);
        }


        /*
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .positiveText("submit").inputRange(10, 400, Color.RED)
                .input("type...", itemList.get(position).body, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input == null || input.toString().trim().isEmpty()) {
                            Utils.message("you passed an empty text!");
                            return;
                        }
                        itemList.get(position).body = input.toString();
                        notifyItemChanged(position);
                    }
                }).build();

        dialog.show();
        */
    }

    public void releaseMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                // mediaPlayer.release();
                mediaPlayer.reset();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).hashCode();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Post.Item movedItem = itemList.remove(fromPosition);
        itemList.add(toPosition, movedItem);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
        return isLocal;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder holder, int position) {
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        holder.clearAnimation();
    }

    class ViewHolder extends AbstractDraggableItemViewHolder implements View.OnClickListener {
        private Post.Item item;
        private CardView cardView;
        private ImageView imgItem, imgMenu;
        private ProgressBar progressItem;
        private TextView txtItem;
        private AudioPlayer audioPlayer;
        private Button btnEdit, btnDelete;


        ViewHolder(View view, int viewType) {
            super(view);
            switch (viewType) {
                case 1:
                    txtItem = (TextView) view.findViewById(R.id.txtItem);
                    if (isLocal) {
                        imgMenu = (ImageView) view.findViewById(R.id.imgMenu);
                        imgMenu.setOnClickListener(this);
                    }
                    break;
                case 2:
                    imgItem = (ImageView) view.findViewById(R.id.imgItem);
                    progressItem = (ProgressBar) view.findViewById(R.id.prgItem);
                    if (isLocal) {
                        btnDelete = (Button) view.findViewById(R.id.btnDelete);
                        btnDelete.setOnClickListener(this);
                    }
                    break;
                case 3:
                    audioPlayer = (AudioPlayer) view.findViewById(R.id.audioStreamer);
                    audioPlayer.setOnTogglePlayClickListener(this);
                    if (isLocal) {
                        btnDelete = (Button) view.findViewById(R.id.btnDelete);
                        btnDelete.setOnClickListener(this);
                    }
                    break;
            }
            cardView = (CardView) view.findViewById(R.id.cardView);


        }

        public void clearAnimation() {
            cardView.clearAnimation();
        }

        @Override
        public void onClick(View v) {
            int postion = getAdapterPosition();
            final Post.Item item = itemList.get(postion);
            switch (v.getId()) {
                case R.id.btnDelete:
                    deleteItemText(postion);
                    break;
                case R.id.imgMenu:
                    showItemMenu(v, postion);
                    break;
                case R.id.myAudioPlayerFabTogglePlay: {
                    if (item.remoteId < 1) {
                        if (item.localName.equals(currentPlayingName)) {
                            audioPlayer.togglePlayPause();
                            return;
                        }
                        audioPlayer.setStatus(AudioPlayer.READY);
                        audioPlayer.attachMediaPlayer(mediaPlayer);
                        try {
                            if (currenAudioPlayer != null && mediaPlayer.isPlaying()) {
                                currenAudioPlayer.togglePlayPause();
                            }
                            audioPlayer.setLocalPath(item.localPath);
                            currentPlayingName = item.localName;
                            currenAudioPlayer = audioPlayer;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    if (item.remoteId == currentPlayingId) {
                        audioPlayer.togglePlayPause();
                        return;
                    }
                    if (readyFiles.contains(item.body)) {
                        audioPlayer.setStatus(AudioPlayer.READY);
                        audioPlayer.attachMediaPlayer(mediaPlayer);
                        try {
                            if (currenAudioPlayer != null && mediaPlayer.isPlaying()) {
                                currenAudioPlayer.togglePlayPause();
                            }
                            audioPlayer.setLocalPath(path + "/" + item.body);
                            currentPlayingId = item.remoteId;
                            currenAudioPlayer = audioPlayer;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (!downloadinFiles.contains(item.body)) {
                        filePath.mkdirs();
                        Utils.toast("Downloading");
                        downloadinFiles.add(item.body);
                        audioPlayer.setStatus(AudioPlayer.DOWNLOADING);
                        Ion.with(context).load(EndPoints.POST_FILE_DIRECTORY + "/" + item.body)
                                .write(new File(Config.DIR_POST + File.separator + post.remoteId + File.separator + item.body))
                                .setCallback(new FutureCallback<File>() {
                                    @Override
                                    public void onCompleted(Exception e, File result) {
                                        if (e != null) {
                                            e.printStackTrace();
                                            return;
                                        }
                                        downloadinFiles.remove(item.body);
                                        readyFiles.add(item.body);
                                        audioPlayer.setStatus(AudioPlayer.READY);
                                    }
                                });

                    }
                    break;
                }
            }
        }

    }

    public interface OnTextItemEditClick {
        void onClick(int position);
    }
}

