package ir.realenglish.app.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.app.EndPoints;
import ir.realenglish.app.model.Lesson;
import ir.realenglish.app.model.Tag;
import ir.realenglish.app.model.Test;
import ir.realenglish.app.model.Word;
import ir.realenglish.app.presenter.UserService;
import ir.realenglish.app.utils.DialogHelper;
import ir.realenglish.app.utils.Utils;
import ir.realenglish.app.view.ActivityResult;
import ir.realenglish.app.view.BaseActivity;
import ir.realenglish.app.view.adapter.FileAdapter;
import ir.realenglish.app.view.adapter.TestAdapter;
import ir.realenglish.app.view.adapter.WordAdapter;
import me.kaede.tagview.OnTagDeleteListener;
import me.kaede.tagview.TagView;


public class ActivityAddLesson extends BaseActivity implements WordAdapter.OnEditWordClickListener,
        TestAdapter.OnEditTestClickListener, FileAdapter.OnEditFileClickListener {
    public static final String TYPE = "type_mode";
    public static final String ID = "lesson_id_for_editmode";
    public static final int NEW = 1;
    public static final int EDIT = 2;

    @Bind(R.id.edtName) EditText edtName;
    @Bind(R.id.edtLevel) EditText edtLevel;
    @Bind(R.id.edtTranscript) EditText edtTranscript;
    @Bind(R.id.edtAuthor) EditText edtAuthor;
    @Bind(R.id.edtNumber) EditText edtNumber;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.recyclerViewTest) RecyclerView recyclerViewTest;
    @Bind(R.id.recyclerViewFiles) RecyclerView recyclerViewFiles;
    private Context context;
    private WordAdapter adapter;
    private TestAdapter testAdapter;
    private FileAdapter fileAdapter;
    private List<Word> words = new ArrayList<>();
    private List<Test> tests = new ArrayList<>();
    private Lesson.Data data = new Lesson.Data();
    private List<Lesson.File> files = new ArrayList<>();
    private List<Tag> selectedTags = new ArrayList<>();
    private List<Tag> serverTags = new ArrayList<>();
    private AutoCompleteTextView autoCompleteView;
    private TagView tagView;
    private ProgressBar progressBar;
    private final int REQUEST_NEW_WORD = 100;
    private final int REQUEST_NEW_TEST = 101;
    private final int REQUEST_UPDATE_WORD = 102;
    private final int REQUEST_UPDATE_TEST = 103;
    private final int REQUEST_NEW_FILE = 104;
    private final int REQUEST_EDIT_FILE = 105;
    private Lesson lesson = new Lesson();
    private int activityMode, remoteId, currentEditedWordPosition, currentEditedTestPosition, currentEditedFilePosition;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);
        ButterKnife.bind(this);
        context = this;
        setToolBar();


        adapter = new WordAdapter(this, words);
        adapter.setOnEditWordClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        testAdapter = new TestAdapter(this, tests);
        testAdapter.setOnEditTestClickListener(this);

        recyclerViewTest.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTest.setAdapter(testAdapter);

        fileAdapter = new FileAdapter(this, files);
        fileAdapter.setOnEditFileClickListener(this);

        recyclerViewFiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFiles.setAdapter(fileAdapter);

        activityMode = getIntent().getIntExtra(TYPE, 0);
        remoteId = getIntent().getIntExtra(ID, 0);
        if (activityMode == EDIT) {
            handleEditMode();
        }
    }

    private void handleEditMode() {
        final MaterialDialog dialog = DialogHelper.showIndeterminateProgressDialog(this, "Getting Lesson", false);
        dialog.show();
        String url = EndPoints.LESSON_EDIT_GET.replace("_R_", String.valueOf(remoteId)) + UserService.getApiToken();
        Ion.with(this).load("GET", url)
                .as(new TypeToken<Lesson>() {
                })
                .setCallback(new FutureCallback<Lesson>() {
                    @Override
                    public void onCompleted(Exception e, Lesson result) {
                        try {
                            dialog.dismiss();
                            lesson = result;
                            words.clear();
                            words.addAll(lesson.words);

                            tests.clear();
                            tests.addAll(lesson.tests);

                            files.clear();
                            files.addAll(lesson.data.files);

                            adapter.notifyDataSetChanged();
                            testAdapter.notifyDataSetChanged();
                            fileAdapter.notifyDataSetChanged();

                            selectedTags = lesson.tags;
                            edtName.setText(lesson.name);
                            edtTranscript.setText(lesson.transcript);
                            edtLevel.setText(lesson.level);
                            edtNumber.setText(String.valueOf(lesson.number));

                            edtAuthor.setText(lesson.data.author);

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });


    }

    private void sendLesson() {
        lesson.name = edtName.getText().toString().trim();
        lesson.level = edtLevel.getText().toString().trim();
        lesson.transcript = edtTranscript.getText().toString().trim();
        lesson.number = Integer.parseInt(edtNumber.getText().toString().trim());
        data.author = edtAuthor.getText().toString().trim();
        data.files = files;
        lesson.words = words;
        lesson.tests = tests;
        lesson.tags = selectedTags;
        lesson.data = data;


        if (!isValidSending()) return;

        final MaterialDialog dialog = DialogHelper.showIndeterminateProgressDialog(this, "Sending Lesson", false);
        dialog.show();

        Utils.log(lesson.toJson());
        String url = activityMode == EDIT
                ? EndPoints.LESSON_EDIT_PATCH.replace("_R_", String.valueOf(remoteId))
                : EndPoints.LESSONS_BASE;

        String method = activityMode == EDIT ? "PATCH" : "POST";
        Ion.with(this).load(method, url)
                .setBodyParameter("lesson", lesson.toJson())
                .setBodyParameter("api_token", UserService.getApiToken())
                .asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                try {
                    dialog.dismiss();
                    Utils.toast(result);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }


    private boolean isValidSending() {
        if (lesson.name.length() < 3) {
            edtName.setError("Enter a title for your idiom");
            return false;
        }
        if (lesson.level.length() < 1) {
            edtLevel.setError("");
            return false;
        }
        if (lesson.transcript.length() < 5) {
            edtTranscript.setError("");
            return false;
        }
        if (lesson.words.size() == 0) {
            Utils.toast("words is empty");
            return false;
        }

        if (tests.size() == 0) {
            Utils.toast("tests are empty");
            return false;
        }

        if (lesson.tags.size() < 1) {
            openTagPanel();
            return false;
        }
        return true;
    }

    @OnClick({R.id.fabAddWord, R.id.fabTag, R.id.fabAddTest, R.id.fabAddFile})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAddWord: {
                Intent intent = new Intent(context, ActivityResult.class);
                intent.putExtra(ActivityResult.TYPE, ActivityResult.NEW_WORD);
                startActivityForResult(intent, REQUEST_NEW_WORD);
                break;
            }
            case R.id.fabTag: {
                openTagPanel();
                break;
            }
            case R.id.fabAddTest: {
                Intent intent = new Intent(context, ActivityResult.class);
                intent.putExtra(ActivityResult.TYPE, ActivityResult.NEW_TEST);
                startActivityForResult(intent, REQUEST_NEW_TEST);
                break;
            }
            case R.id.fabAddFile: {
                Intent intent = new Intent(context, ActivityResult.class);
                intent.putExtra(ActivityResult.TYPE, ActivityResult.NEW_FILE);
                startActivityForResult(intent, REQUEST_NEW_FILE);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_NEW_WORD) {
            Word word = (Word) data.getSerializableExtra("word");
            words.add(word);
            adapter.notifyItemInserted(words.size());
            return;
        }
        if (requestCode == REQUEST_NEW_TEST) {
            Test test = (Test) data.getSerializableExtra("test");
            tests.add(test);
            testAdapter.notifyItemInserted(tests.size());
            return;
        }
        if (requestCode == REQUEST_UPDATE_WORD) {
            Word word = (Word) data.getSerializableExtra("word");
            words.set(currentEditedWordPosition, word);
            adapter.notifyItemChanged(currentEditedWordPosition);
            return;
        }

        if (requestCode == REQUEST_UPDATE_TEST) {
            Test test = (Test) data.getSerializableExtra("test");
            tests.set(currentEditedTestPosition, test);
            testAdapter.notifyItemChanged(currentEditedTestPosition);
            return;
        }

        if (requestCode == REQUEST_NEW_FILE) {
            Lesson.File file = (Lesson.File) data.getSerializableExtra("file");
            files.add(file);
            fileAdapter.notifyItemInserted(files.size());
            return;
        }
        if (requestCode == REQUEST_EDIT_FILE) {
            Lesson.File file = (Lesson.File) data.getSerializableExtra("file");
            files.set(currentEditedFilePosition, file);
            fileAdapter.notifyItemChanged(currentEditedFilePosition);
            return;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_lesson, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                sendLesson();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEditClicked(int position) {
        currentEditedWordPosition = position;
        Intent intent = new Intent(context, ActivityResult.class);
        intent.putExtra(ActivityResult.TYPE, ActivityResult.UPDATE_WORD);
        intent.putExtra(ActivityResult.WORD_TO_UPDATE, words.get(position));
        startActivityForResult(intent, REQUEST_UPDATE_WORD);
    }

    @Override
    public void onEditTestClicked(int position) {
        currentEditedTestPosition = position;
        Intent intent = new Intent(context, ActivityResult.class);
        intent.putExtra(ActivityResult.TYPE, ActivityResult.UPDATE_TEST);
        intent.putExtra(ActivityResult.TEST_TO_UPDATE, tests.get(position));
        startActivityForResult(intent, REQUEST_UPDATE_TEST);
    }

    @Override
    public void onEditFileClicked(int position) {
        currentEditedFilePosition = position;
        Intent intent = new Intent(context, ActivityResult.class);
        intent.putExtra(ActivityResult.TYPE, ActivityResult.UPDATE_FILE);
        intent.putExtra(ActivityResult.FILE_TO_UPDATE, files.get(position));
        startActivityForResult(intent, REQUEST_EDIT_FILE);
    }

    private void openTagPanel() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Set tag for your post")
                .customView(R.layout.dialog_set_tag, true)
                .positiveText(getString(android.R.string.ok)).build();
        autoCompleteView = (AutoCompleteTextView) dialog.getCustomView().findViewById(R.id.autoCompleteView);
        tagView = (TagView) dialog.getCustomView().findViewById(R.id.tagView);
        progressBar = (ProgressBar) dialog.getCustomView().findViewById(R.id.progressBar);
        if (serverTags.size() < 1) {
            Ion.with(this).load("GET", EndPoints.TAG_LIST).asJsonArray()
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
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                        context, android.R.layout.simple_dropdown_item_1line,
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    context, android.R.layout.simple_dropdown_item_1line,
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

}
