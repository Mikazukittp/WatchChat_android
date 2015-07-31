package app.android.ttp.mikazuki.watchchat.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import app.android.ttp.mikazuki.watchchat.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @Bind(R.id.name)
    EditText mName;

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.regist)
    public void onRegisterButtonPressed() {
        String inputName = mName.getText().toString();
        if (inputName != null && !"".equals(inputName)){
            if (mListener != null) {
                mListener.onRegister(inputName);
            }
        }else{

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onRegister(String name);
    }

}
