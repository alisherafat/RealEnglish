package ir.realenglish.app.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.realenglish.app.R;

public class FragmentAddText extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    @Bind(R.id.edtBody) EditText edtBody;
    @Bind(R.id.imgSubmit) ImageView imgSubmit;
    private String dafaultText;

    private OnSubmitTextListener mListener;

    public FragmentAddText() {
        // Required empty public constructor
    }

    public static FragmentAddText newInstance(String param1) {
        FragmentAddText fragment = new FragmentAddText();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dafaultText = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_text, container, false);
        ButterKnife.bind(this, view);
        edtBody.setText(dafaultText);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitTextListener) {
            mListener = (OnSubmitTextListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.edtBody, R.id.imgSubmit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edtBody:
                break;
            case R.id.imgSubmit:
                String body = edtBody.getText().toString().trim();
                if (body.length() < 1) return;
                if (mListener != null) {
                    mListener.onTextSubmit(body);
                }
                break;
        }
    }

    public interface OnSubmitTextListener {
        void onTextSubmit(String text);
    }
}
