package pro.quizer.quizerexit.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.halilibo.bettervideoplayer.BetterVideoPlayer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.ElementSubtype;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;

public class InfoFragment extends AbstractContentElementFragment {

    public static final String BUNDLE_CURRENT_QUESTION = "BUNDLE_CURRENT_QUESTION";
    public static final String BUNDLE_CALLBACK = "BUNDLE_CALLBACK";
    public static final String BUNDLE_MAP = "BUNDLE_MAP";

    private ElementModel mCurrentElement;
    private OptionsModel mAttributes;
    private HashMap<Integer, ElementModel> mMap;
    private NavigationCallback mCallback;
    private boolean mIsButtonVisible;

    public static Fragment newInstance(final boolean isButtonVisible, @NonNull final ElementModel pElement, final NavigationCallback pCallback, final HashMap<Integer, ElementModel> pMap) {
        final Fragment fragment = new InfoFragment();

        final Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_IS_BUTTON_VISIBLE, isButtonVisible);
        bundle.putSerializable(BUNDLE_CURRENT_QUESTION, pElement);
        bundle.putSerializable(BUNDLE_CALLBACK, pCallback);
        bundle.putSerializable(BUNDLE_MAP, pMap);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle bundle = getArguments();

        if (bundle != null) {
            mCurrentElement = (ElementModel) bundle.getSerializable(BUNDLE_CURRENT_QUESTION);
            mCallback = (NavigationCallback) bundle.getSerializable(BUNDLE_CALLBACK);
            mMap = (HashMap<Integer, ElementModel>) bundle.getSerializable(BUNDLE_MAP);
            mAttributes = mCurrentElement.getOptions();
            mIsButtonVisible = bundle.getBoolean(BUNDLE_IS_BUTTON_VISIBLE);

            initView(view);
            handleButtonsVisibility();
        } else {
            showToast(getString(R.string.NOTIFICATION_INTERNAL_APP_ERROR) + "1002");
        }
    }

    @Override
    protected boolean isFromDialog() {
        return false;
    }

    @Override
    protected boolean isButtonVisible() {
        return mIsButtonVisible;
    }

    private final Runnable mBackRunnable = new Runnable() {
        @Override
        public void run() {
            mCallback.onBack();
        }
    };

    private final Runnable mExitRunnable = new Runnable() {
        @Override
        public void run() {
            mCallback.onExit();
        }
    };

    private final Runnable mForwardRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mCallback.onForward(mCurrentElement.getOptions().getJump(), getForwardButton());
            } catch (final Exception pE) {
                showToast(pE.getMessage());
            }
        }
    };

    @Override
    protected Runnable getForwardRunnable() {
        return mForwardRunnable;
    }

    @Override
    protected Runnable getBackRunnable() {
        return mBackRunnable;
    }

    @Override
    protected Runnable getExitRunnable() {
        return mExitRunnable;
    }

    @Override
    protected OptionsModel getOptions() {
        return mAttributes;
    }

    @Override
    protected HashMap<Integer, ElementModel> getMap() {
        return mMap;
    }

    @Override
    protected ElementModel getElementModel() {
        return mCurrentElement;
    }

    private void initView(final View pView) {
        TextView mText;
        ImageView mImageView;
        BetterVideoPlayer mVideoPlayer;
        BetterVideoPlayer mAudioPlayer;

        mText = pView.findViewById(R.id.info_text);
        mImageView = pView.findViewById(R.id.info_image);
        mVideoPlayer = pView.findViewById(R.id.info_video_player);
        mAudioPlayer = pView.findViewById(R.id.info_audio_player);

        final OptionsModel options = mCurrentElement.getOptions();
        final String data = options.getData();

        switch (mCurrentElement.getSubtype()) {
            case ElementSubtype.HTML:
                UiUtils.setTextOrHide(mText, data);

                break;
//            case ElementSubtype.AUDIO:
//                final String fileAudioPath = getFilePath(data);
//
//                if (StringUtils.isEmpty(fileAudioPath)) {
//                    return;
//                }
//
//                mAudioPlayer.setVisibility(View.VISIBLE);
//                mAudioPlayer.setSource(Uri.fromFile(new File(fileAudioPath)));
//                mAudioPlayer.enableSwipeGestures(((BaseActivity) mBaseActivity).getWindow());
//
//                break;
//            case ElementSubtype.VIDEO:
//                final String fileVideoPath = getFilePath(data);
//
//                if (StringUtils.isEmpty(fileVideoPath)) {
//                    return;
//                }
//
//                mVideoPlayer.setVisibility(View.VISIBLE);
//                mVideoPlayer.setSource(Uri.fromFile(new File(fileVideoPath)));
//
//                mVideoPlayer.setHideControlsOnPlay(true);
//
//                mVideoPlayer.enableSwipeGestures(((BaseActivity) mBaseActivity).getWindow());
//
//                break;
//            case ElementSubtype.IMAGE:
//                final String filePhotooPath = getFilePath(data);
//
//                if (StringUtils.isEmpty(filePhotooPath)) {
//                    return;
//                }
//
//                mImageView.setVisibility(View.VISIBLE);
//
//                Picasso.with(mBaseActivity)
//                        .load(new File(filePhotooPath))
//                        .into(mImageView);
//
//
//                break;
            default:

                break;
        }
    }
//        RecyclerView mRecyclerView = pView.findViewById(R.id.info_recycler_view);
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mRecyclerView.setHasFixedSize(true);
//        ContentElementsAdapter mAdapter = new ContentElementsAdapter((BaseActivity) getContext(), mCurrentElement.getElements());
//        mRecyclerView.setAdapter(mAdapter);
//    }
}