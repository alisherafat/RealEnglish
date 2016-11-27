package ir.realenglish.app.view;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.model.Post;
import ir.realenglish.app.model.Tag;
import ir.realenglish.app.network.MyNetworkService;
import ir.realenglish.app.utils.Utils;
import ir.realenglish.app.utils.VerticalSpaceItemDecoration;
import ir.realenglish.app.view.activity.ContainerActivity;
import ir.realenglish.app.view.adapter.PostItemAdapter;
import me.kaede.tagview.OnTagDeleteListener;
import me.kaede.tagview.TagView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddPost extends Fragment {

    private final int REQUEST_TEXT = 4654;
    private final int RECORD_AUDIO_REQUEST = 1235;
    private final int PICK_IMAGE_REQUEST = 1023;
    private final int PICK_AUDIO_REQUEST = 1368;
    @Bind(R.id.edtTitle) EditText edtTitle;
    @Bind(R.id.tilTitle) TextInputLayout tilTitle;
    @Bind(R.id.edtDescription) EditText edtDescription;
    @Bind(R.id.tilDescription) TextInputLayout tilDescription;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.fabAddAudio) FloatingActionButton fabAddAudio;
    @Bind(R.id.fabRecordVoice) FloatingActionButton fabRecordVoice;
    @Bind(R.id.fabAddImage) FloatingActionButton fabAddImage;
    @Bind(R.id.fabAddText) FloatingActionButton fabAddText;
    private AutoCompleteTextView autoCompleteView;
    private TagView tagView;
    private ProgressBar progressBar;
    private List<Post.Item> itemList;// = new ArrayList<>();
    private PostItemAdapter adapter;
    private Post post;
    private List<Tag> selectedTags = new ArrayList<>();
    private List<Tag> serverTags = new ArrayList<>();
    private boolean isEditMode = false;
    private ContainerActivity parent;
    private int currentEditedPosition;

    public FragmentAddPost() {
        // Required empty public constructor
    }

    public static FragmentAddPost newInstance(Post post, boolean editMode) {
        FragmentAddPost fragment = new FragmentAddPost();
        Bundle args = new Bundle();
        args.putSerializable("post", post);
        args.putBoolean("editMode", editMode);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_idiom_add, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(15));
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parent = (ContainerActivity) getActivity();
        handleFragmentMode();
        adapter = new PostItemAdapter(getContext(), post, true);

        RecyclerViewDragDropManager dragDropManager = new RecyclerViewDragDropManager();

        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setInitiateOnLongPress(true);

        recyclerView.setAdapter(dragDropManager.createWrappedAdapter(adapter));
        dragDropManager.attachRecyclerView(recyclerView);

        adapter.setTextItemEditListener(new PostItemAdapter.OnTextItemEditClick() {
            @Override
            public void onClick(int position) {
                currentEditedPosition = position;
                Intent intent = new Intent(getContext(), ActivityResult.class);
                intent.putExtra(ActivityResult.TYPE, ActivityResult.GET_TEXT);
                intent.putExtra(ActivityResult.DEFAULT_TEXT, itemList.get(position).body);
                startActivityForResult(intent,PostItemAdapter.REQUEST_TEXT_CODE);
            }
        });
    }

    private void handleFragmentMode() {
        if (isEditMode) {
            selectedTags = post.tags;
            itemList = post.getItems();
            edtTitle.setText(post.title);
            edtDescription.setText(post.description);
        } else {
            // new Post
            itemList = new ArrayList<>();
            post.setItems(itemList);
        }
    }

    private void openTagPanel() {
        MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                .title("Set tag for your post")
                .customView(R.layout.dialog_set_tag, true)
                .positiveText(getString(android.R.string.ok)).build();
        autoCompleteView = (AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.autoCompleteView);
        tagView = (TagView) dialog.getCustomView().findViewById(R.id.tagView);
        progressBar = (ProgressBar) dialog.getCustomView().findViewById(R.id.progressBar);
        if (serverTags.size() < 1) {
            Ion.with(getContext()).load("GET", EndPoints.TAG_LIST).asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            progressBar.setVisibility(View.VISIBLE);
                            if (result != null) {
                                progressBar.setVisibility(View.GONE);
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<Tag>>() {
                                }.getType();
                                serverTags.addAll(gson.<Collection<? extends Tag>>fromJson(result, listType));
                                List<String> tagNames = new ArrayList<>();
                                for (Tag tag : serverTags) {
                                    tagNames.add(tag.name);
                                }
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        getContext(), android.R.layout.simple_dropdown_item_1line,
                                        tagNames);
                                autoCompleteView.setAdapter(arrayAdapter);
                            }
                        }
                    });
        } else {
            List<String> tagNames = new ArrayList<>();
            for (Tag tag : serverTags) {
                tagNames.add(tag.name);
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getContext(), android.R.layout.simple_dropdown_item_1line,
                    tagNames);
            autoCompleteView.setAdapter(arrayAdapter);
        }
        for (Tag item : selectedTags) {
            me.kaede.tagview.Tag tag = new me.kaede.tagview.Tag(item.name);
            tag.isDeletable = true;
            tagView.addTag(tag);
        }
        autoCompleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteView.showDropDown();
            }
        });
        autoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tag selectedTag = new Tag();
                selectedTag.name = (String) parent.getItemAtPosition(position);
                selectedTag = serverTags.get(serverTags.indexOf(selectedTag));
                if (!selectedTags.contains(selectedTag)) {
                    selectedTags.add(selectedTag);
                    me.kaede.tagview.Tag tag = new me.kaede.tagview.Tag(selectedTag.name);
                    tag.isDeletable = true;
                    tagView.addTag(tag);
                }
                autoCompleteView.setText("");
            }
        });
        tagView.setOnTagDeleteListener(new OnTagDeleteListener() {
            @Override
            public void onTagDeleted(int position, me.kaede.tagview.Tag tag) {
                Tag deletedTag = new Tag();
                deletedTag.name = tag.text;
                selectedTags.remove(deletedTag);
            }
        });
        dialog.show();
    }

    private void sendIdiom() {
        post.tags = selectedTags;
        post.title = edtTitle.getText().toString().trim();
        post.description = edtDescription.getText().toString().trim();
        post.setItems(itemList);

        if (!isValidSending()) return;

        int sort = 0;
        for (Post.Item item : itemList) {
            item.sort = ++sort;
        }
        Intent intent = new Intent(getContext(), MyNetworkService.class);
        intent.putExtra(MyNetworkService.ACTION, MyNetworkService.SEND_POST);
        intent.putExtra("post", post);
        intent.putExtra("editMode", isEditMode);
        getContext().startService(intent);
    }



    @OnClick(R.id.fabAddText)
    public void btnTextClick() {

        Intent intent = new Intent(getContext(), ActivityResult.class);
        intent.putExtra(ActivityResult.TYPE, ActivityResult.GET_TEXT);
        startActivityForResult(intent, REQUEST_TEXT);
        ((ContainerActivity) getActivity()).slideToRightTransition();
        /*
        new MaterialDialog.Builder(getContext()).title("Add text").positiveText("Add").negativeText("cancel")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .input("type...", null, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        if (input == null || input.toString().trim().isEmpty()) {
                            Utils.message("you passed an empty text!");
                            return;
                        }
                        Post.Item item = new Post.Item();
                        item.type = 1;
                        item.body = input.toString();
                        addItem(item);
                    }
                }).build().show();
                */
    }

    @OnClick({R.id.fabTag})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabTag:{
                openTagPanel();
                break;
            }
        }
    }
    @OnClick(R.id.fabAddImage)
    public void btnImageClick() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @OnClick(R.id.fabAddAudio)
    public void btnAudioClick() {
        Intent audioIntent = new Intent();
        audioIntent.setType("audio/*");
        audioIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(audioIntent, PICK_AUDIO_REQUEST);
    }

    @OnClick(R.id.fabRecordVoice)
    public void btnRecordClick() {
        Intent intent = new Intent(getContext(), ActivityResult.class);
        intent.putExtra(ActivityResult.TYPE, ActivityResult.RECORD_VOICE);
        startActivityForResult(intent, RECORD_AUDIO_REQUEST);
        ((ContainerActivity) getActivity()).slideToRightTransition();
    }

    private void addItem(Post.Item item) {
        itemList.add(item);
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1) {
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != getActivity().RESULT_OK || data == null) {
            //
            return;
        }
        if (requestCode == PICK_IMAGE_REQUEST) {
            try {
                String path = Utils.getRealPathFromURI(getContext(), data.getData());
                Post.Item item = new Post.Item();
                item.type = 2;
                item.localName = Utils.generateRandomString(6);
                item.localPath = path;
                addItem(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_AUDIO_REQUEST) {
            try {
                Uri filePath = data.getData();
                Post.Item item = new Post.Item();
                item.type = 3;
                item.localName = Utils.generateRandomString(6);
                item.localPath = Utils.getRealPathFromURI(getContext(), filePath);
                addItem(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == RECORD_AUDIO_REQUEST) {
            Post.Item item = new Post.Item();
            item.type = 3;
            item.localName = Utils.generateRandomString(6);
            item.localPath = data.getStringExtra("key");
            if (item.localPath == null) {
                Utils.toast("something went wrong while recording!");
                return;
            }
            addItem(item);
        }

        if (requestCode == REQUEST_TEXT) {
            String body = data.getStringExtra("key");
            Post.Item item = new Post.Item();
            item.type = 1;
            item.body = body;
            addItem(item);
        }

        if (requestCode == PostItemAdapter.REQUEST_TEXT_CODE) {
            String body = data.getStringExtra("key");
            if (body.length() > 5) {
                itemList.get(currentEditedPosition).body = body;
                adapter.notifyItemChanged(currentEditedPosition);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isEditMode = getArguments().getBoolean("editMode");
            if (isEditMode) {
                post = (Post) getArguments().getSerializable("post");

            } else {
                post = new Post();
            }
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_add_post, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                sendIdiom();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private boolean isValidSending() {
        if (post.title.length() < 3) {
            tilTitle.setError("Enter a title for your idiom");
            return false;
        }
        tilTitle.setError(null);
        if (post.getItems().size() < 1) {
            Utils.toast("Add one item at least");
            return false;
        }
        if (post.tags.size() < 1) {
            openTagPanel();
            return false;
        }
        return true;
    }
}
