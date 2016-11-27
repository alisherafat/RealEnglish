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
import ir.realenglish.app.model.Lesson;

public class FragmentNewFile extends Fragment {
    public static final int NEW = 1;
    public static final int UPDATE = 2;
    @Bind(R.id.edtName) EditText edtName;
    @Bind(R.id.edtPath) EditText edtPath;
    private Lesson.File file;
    private int type;
    private OnSubmitNewFile listener;

    public FragmentNewFile() {
        // Required empty public constructor
    }

    public static FragmentNewFile newInstance(int type, Lesson.File file) {
        FragmentNewFile fragment = new FragmentNewFile();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putSerializable("file", file);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitNewFile) {
            listener = (OnSubmitNewFile) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSubmitNewFile");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type", 0);
            switch (type) {
                case NEW:
                    file = new Lesson.File();
                    break;
                case UPDATE:
                    file = (Lesson.File) getArguments().getSerializable("file");
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_file, container, false);
        ButterKnife.bind(this, view);
        if (type == UPDATE) {
            handleEditMode();
        }
        return view;
    }

    private void handleEditMode() {
        edtName.setText(file.name);
        edtPath.setText(file.path);
    }

    @OnClick(R.id.btnSubmit)
    void onClick() {
        file.name = edtName.getText().toString().trim();
        file.path = edtPath.getText().toString().trim();
        listener.onSubmitNewFile(file);
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

    public interface OnSubmitNewFile {
        void onSubmitNewFile(Lesson.File file);
    }

}
