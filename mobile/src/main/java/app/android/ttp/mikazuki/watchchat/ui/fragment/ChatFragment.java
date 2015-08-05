package app.android.ttp.mikazuki.watchchat.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.android.ttp.mikazuki.watchchat.R;
import app.android.ttp.mikazuki.watchchat.data.preference.repository.PreferenceSettingRepository;
import app.android.ttp.mikazuki.watchchat.domain.entity.Message;
import app.android.ttp.mikazuki.watchchat.domain.repository.SettingRepository;
import app.android.ttp.mikazuki.watchchat.ui.adapter.ChatListAdapter;
import app.android.ttp.mikazuki.watchchat.util.Constants;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.input)
    EditText mInput;
    @Bind(R.id.chat_list)
    ListView mChatList;

    ChatListAdapter mListAdapter;
    List<Message> mMessages;
    private SettingRepository mSettingRepository;
    private BroadcastReceiver mMessageUpdateBroadcastReceiver;
    private BroadcastReceiver mMessagesFetchBroadcastReceiver;

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);

        mTitle.setText(mSettingRepository.getOpponentName() + "とのチャット");
        mMessages = new ArrayList<Message>();
        mListAdapter = new ChatListAdapter(getActivity().getApplicationContext(), 0, mMessages);
        mChatList.setAdapter(mListAdapter);

        mListener.onFetchMessages();

        return view;
    }

    @OnClick(R.id.send)
    public void onSendButtonPressed() {
        if (mListener != null) {
            String input = mInput.getText().toString();
            mListener.onSendMessage(input);
            updateMessage(input, true, new Date());
            // キーボードを閉じる
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            mInput.setText("");
        }
    }

    private String now() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
        mSettingRepository = new PreferenceSettingRepository(getActivity().getApplicationContext());
        mMessageUpdateBroadcastReceiver = new MessageUpdateBroadcastReceiver();
        mMessagesFetchBroadcastReceiver = new MessagesFetchBroadcastReceiver();
        LocalBroadcastManager manager =
                LocalBroadcastManager
                        .getInstance(getActivity());
        manager.registerReceiver(mMessageUpdateBroadcastReceiver, new IntentFilter(Constants.FRAGMENT_MESSAGE_UPDATED));
        manager.registerReceiver(mMessagesFetchBroadcastReceiver, new IntentFilter(Constants.FRAGMENT_MESSAGES_FETCHED));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageUpdateBroadcastReceiver);
    }

    class MessageUpdateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMessage(intent.getStringExtra(Constants.UPDATED_MESSAGE), false, new Date());
        }
    }

    class MessagesFetchBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message[] messages = (Message[])intent.getSerializableExtra(Constants.FETCHED_MESSAGES);
            for (Message message: messages) {
                updateMessage(message.getContent(), message.getSenderId() == mSettingRepository.getUserId(), message.getCreatedAt());
            }
        }
    }

    private void updateMessage(String message, boolean isMine, Date createdAt) {
        if (isMine) {
            mMessages.add(new Message(message, mSettingRepository.getUserId(), mSettingRepository.getOpponentId(), createdAt));
        } else {
            mMessages.add(new Message(message, mSettingRepository.getOpponentId(), mSettingRepository.getUserId(), createdAt));
        }
        mListAdapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        public void onFetchMessages();

        public void onSendMessage(String message);
    }

}
