package ir.realenglish.app.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;
import ir.realenglish.app.model.Word;

public class FragmentNewWord extends Fragment {
    public static final int NEW = 1;
    public static final int UPDATE = 2;
    @Bind(R.id.edtTitle) EditText edttitle;
    @Bind(R.id.edtType) EditText edtType;
    @Bind(R.id.edtBody) EditText edtBody;
    @Bind(R.id.edtLink) EditText edtLink;
    private Word word;
    private int type;
    private OnSubmitNewWord listener;

    public FragmentNewWord() {
        // Required empty public constructor
    }

    public static FragmentNewWord newInstance(int type, Word word) {
        FragmentNewWord fragment = new FragmentNewWord();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putSerializable("word", word);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitNewWord) {
            listener = (OnSubmitNewWord) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSubmitNewWord");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type", 0);
            switch (type) {
                case NEW:
                    word = new Word();
                    break;
                case UPDATE:
                    word = (Word) getArguments().getSerializable("word");
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_word, container, false);
        ButterKnife.bind(this, view);
        if (type == UPDATE) {
            handleEditMode();
        }
        return view;
    }

    private void handleEditMode() {
        edttitle.setText(word.title);
        edtType.setText(word.type);
        edtBody.setText(word.body);
        edtLink.setText(word.link);
    }

    @OnClick(R.id.btnSubmit)
    void onClick() {
        word.title = edttitle.getText().toString().trim();
        word.type = edtType.getText().toString().trim();
        word.body = edtBody.getText().toString().trim();
        word.link = edtLink.getText().toString().trim();
        listener.onSubmitNewWord(word);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnSubmitNewWord {
        void onSubmitNewWord(Word word);
    }

}
