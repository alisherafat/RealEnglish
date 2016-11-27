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
import ir.realenglish.app.model.Test;

public class FragmentNewTest extends Fragment {
    public static final int NEW = 1;
    public static final int UPDATE = 2;
    @Bind(R.id.edtTitle) EditText edttitle;
    @Bind(R.id.edtOption1) EditText edtOption1;
    @Bind(R.id.edtOption2) EditText edtOption2;
    @Bind(R.id.edtOption3) EditText edtOption3;
    @Bind(R.id.edtOption4) EditText edtOption4;
    @Bind(R.id.edtAnswer) EditText edtAnswer;
    private Test test;
    private int type;
    private OnSubmitNewTest listener;

    public FragmentNewTest() {
        // Required empty public constructor
    }

    public static FragmentNewTest newInstance(int type, Test test) {
        FragmentNewTest fragment = new FragmentNewTest();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putSerializable("test", test);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitNewTest) {
            listener = (OnSubmitNewTest) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSubmitNewTest");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type", 0);
            switch (type) {
                case NEW:
                    test = new Test();
                    break;
                case UPDATE:
                    test = (Test) getArguments().getSerializable("test");
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_test, container, false);
        ButterKnife.bind(this, view);
        if (type == UPDATE) {
            handleEditMode();
        }
        return view;
    }

    private void handleEditMode() {
        edttitle.setText(test.title);
        edtOption1.setText(test.options[0]);
        edtOption2.setText(test.options[1]);
        edtOption3.setText(test.options[2]);
        edtOption4.setText(test.options[3]);
        edtAnswer.setText(String.valueOf(test.answer));
    }

    @OnClick(R.id.btnSubmit)
    void onClick() {
        test.title = edttitle.getText().toString().trim();
        test.options = new String[4];
        test.options[0] = edtOption1.getText().toString().trim();
        test.options[1] = edtOption2.getText().toString().trim();
        test.options[2] = edtOption3.getText().toString().trim();
        test.options[3] = edtOption4.getText().toString().trim();
        test.answer = Integer.parseInt(edtAnswer.getText().toString().trim());
        listener.onSubmitNewTest(test);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnSubmitNewTest {
        void onSubmitNewTest(Test test);
    }

}
