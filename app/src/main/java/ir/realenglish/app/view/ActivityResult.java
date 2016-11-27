package ir.realenglish.app.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;

import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import ir.realenglish.app.R;
import ir.realenglish.app.custom.RecordFragment;
import ir.realenglish.app.model.Lesson;
import ir.realenglish.app.model.Test;
import ir.realenglish.app.model.Word;
import ir.realenglish.app.view.fragment.FragmentAddText;
import ir.realenglish.app.view.fragment.FragmentNewFile;
import ir.realenglish.app.view.fragment.FragmentNewTest;
import ir.realenglish.app.view.fragment.FragmentNewWord;

public class ActivityResult extends BaseActivity implements RecordFragment.OnRecordFinishListener,
        FragmentAddText.OnSubmitTextListener, FragmentNewWord.OnSubmitNewWord,FragmentNewFile.OnSubmitNewFile
        , FragmentNewTest.OnSubmitNewTest {
    public static final String TYPE = "action";
    public static final String DEFAULT_TEXT = "default_text";
    public static final String WORD_TO_UPDATE = "word_to_update";
    public static final String TEST_TO_UPDATE = "test_to_update";
    public static final String FILE_TO_UPDATE = "FILE_TO_UPDATE";
    public static final int RECORD_VOICE = 1;
    public static final int GET_TEXT = 2;
    public static final int NEW_WORD = 3;
    public static final int NEW_TEST = 4;
    public static final int UPDATE_WORD = 5;
    public static final int UPDATE_TEST = 6;
    public static final int NEW_FILE = 7;
    public static final int UPDATE_FILE = 8;

    @Bind(R.id.fab) FloatingActionButton floatingActionButton;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        setToolBar();
        context = this;
        floatingActionButton.hide();
        switch (getIntent().getExtras().getInt(TYPE)) {
            case RECORD_VOICE:
                setTitle("Record");
                Fragment recordFragment = new RecordFragment();
                startFragment(recordFragment);
                break;
            case GET_TEXT: {
                setTitle("Text");
                Fragment fragment = FragmentAddText.newInstance(getIntent().getStringExtra(DEFAULT_TEXT));
                startFragment(fragment);
                break;
            }
            case NEW_WORD: {
                setTitle("New Word");
                Fragment fragment = FragmentNewWord.newInstance(FragmentNewWord.NEW, null);
                startFragment(fragment);
                break;
            }
            case NEW_TEST: {
                getSupportActionBar().setTitle("New Test");
                Fragment fragment = FragmentNewTest.newInstance(FragmentNewTest.NEW, null);
                startFragment(fragment);
                break;
            }
            case UPDATE_WORD: {
                getSupportActionBar().setTitle("Update Word");
                Fragment fragment = FragmentNewWord.newInstance(FragmentNewWord.UPDATE, (Word) getIntent().getSerializableExtra(WORD_TO_UPDATE));
                startFragment(fragment);
                break;
            }
            case UPDATE_TEST: {
                getSupportActionBar().setTitle("Update Test");
                Fragment fragment = FragmentNewTest.newInstance(FragmentNewTest.UPDATE, (Test) getIntent().getSerializableExtra(TEST_TO_UPDATE));
                startFragment(fragment);
                break;
            }
            case NEW_FILE:{
                getSupportActionBar().setTitle("New File");
                Fragment fragment = FragmentNewFile.newInstance(FragmentNewFile.NEW, null);
                startFragment(fragment);
                break;
            }
            case UPDATE_FILE: {
                getSupportActionBar().setTitle("Update File");
                Fragment fragment = FragmentNewFile.newInstance(FragmentNewFile.UPDATE, (Lesson.File) getIntent().getSerializableExtra(FILE_TO_UPDATE));
                startFragment(fragment);
                break;
            }

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    public FloatingActionButton getFAB() {
        return floatingActionButton;
    }

    @Override
    public void onRecordFinish(String path) {
        Intent intent = new Intent();
        intent.putExtra("key", path);
        setResult(RESULT_OK, intent);
        finish();
        slideToLeftTransition();
    }

    @Override
    public void onTextSubmit(String text) {
        Intent intent = new Intent();
        intent.putExtra("key", text);
        setResult(RESULT_OK, intent);
        finish();
        slideToLeftTransition();
    }

    @Override
    public void onSubmitNewWord(Word word) {
        Intent intent = new Intent();
        intent.putExtra("word", word);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSubmitNewTest(Test test) {
        Intent intent = new Intent();
        intent.putExtra("test", test);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSubmitNewFile(Lesson.File file) {
        Intent intent = new Intent();
        intent.putExtra("file", file);
        setResult(RESULT_OK, intent);
        finish();
    }
}
