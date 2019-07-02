package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.halilibo.bettervideoplayer.BetterVideoPlayer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import pro.quizer.quizerexit.Constants;
import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.model.ElementSubtype;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.config.OptionsModel;
import pro.quizer.quizerexit.utils.FileUtils;
import pro.quizer.quizerexit.utils.StringUtils;
import pro.quizer.quizerexit.utils.UiUtils;

public class ContentElementsAdapter extends RecyclerView.Adapter<ContentElementsAdapter.InfoElementViewHolder> implements IAdapter {

    private final BaseActivity mBaseActivity;
    private final List<ElementModel> mContents;
    private final ElementModel mParentElement;
    private final ElementModel mParentQuestion;

    public ContentElementsAdapter(final ElementModel pParentQuestion, final ElementModel pParentElement, final BaseActivity pContext, final List<ElementModel> pContents) {
        this.mBaseActivity = pContext;
        this.mContents = pContents;
        mParentElement = pParentElement;
        mParentQuestion = pParentQuestion;
    }

    @Override
    public void onViewDetachedFromWindow(final InfoElementViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mContents.size();
    }

    @NonNull
    @Override
    public InfoElementViewHolder onCreateViewHolder(@NonNull final ViewGroup pViewGroup, final int pPosition) {
        final View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_info_element, pViewGroup, false);
        return new InfoElementViewHolder(itemView, mBaseActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull final InfoElementViewHolder pAnswerListViewHolder, final int pPosition) {
        pAnswerListViewHolder.onBind(mContents.get(pPosition), pPosition);
    }

    @Override
    public int processNext() throws Exception {
        return 0;
    }

    class InfoElementViewHolder extends AbstractViewHolder {

        TextView mText;
        ImageView mImageView;
        ImageView mIconView;
        BetterVideoPlayer mVideoPlayer;
        BetterVideoPlayer mAudioPlayer;

        InfoElementViewHolder(final View itemView, final BaseActivity pBaseActivity) {
            super(itemView, pBaseActivity);
            mText = itemView.findViewById(R.id.info_text);
            mIconView = itemView.findViewById(R.id.info_icon);
            mImageView = itemView.findViewById(R.id.info_image);
            mVideoPlayer = itemView.findViewById(R.id.info_video_player);
            mAudioPlayer = itemView.findViewById(R.id.info_audio_player);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBind(final ElementModel pContent, final int pPosition) {
            final OptionsModel options = pContent.getOptions();
            final String data = pContent.getData();
            final String dataOn = pContent.getDataOn();
            final String dataOff = pContent.getDataOff();

            switch (pContent.getType()) {
                case ElementSubtype.HTML:
                    UiUtils.setTextOrHide(mText, options.getText());

                    break;
                case ElementSubtype.AUDIO:
                    final String fileAudioPath = getFilePath(data);

                    if (StringUtils.isEmpty(fileAudioPath)) {
                        return;
                    }

                    mAudioPlayer.setVisibility(View.VISIBLE);
                    mAudioPlayer.setSource(Uri.fromFile(new File(fileAudioPath)));
                    mAudioPlayer.enableSwipeGestures(((BaseActivity) mBaseActivity).getWindow());

                    break;
                case ElementSubtype.VIDEO:
                    final String fileVideoPath = getFilePath(data);

                    if (StringUtils.isEmpty(fileVideoPath)) {
                        return;
                    }

                    mVideoPlayer.setVisibility(View.VISIBLE);
                    mVideoPlayer.setSource(Uri.fromFile(new File(fileVideoPath)));

                    mVideoPlayer.setHideControlsOnPlay(true);

                    mVideoPlayer.enableSwipeGestures(((BaseActivity) mBaseActivity).getWindow());

                    break;
                case ElementSubtype.IMAGE:
                    final String filePhotooPath = getFilePath(data);

                    if (StringUtils.isEmpty(filePhotooPath)) {
                        return;
                    }

                    mImageView.setVisibility(View.VISIBLE);

                    Picasso.with(mBaseActivity)
                            .load(new File(filePhotooPath))
                            .into(mImageView);


                    break;
                case ElementSubtype.STATUS_IMAGE:
                    final String statusImage;
                    if (!mParentQuestion.isAnyChecked()) {
                        statusImage = getFilePath(data);
                    } else if (mParentElement.isFullySelected()) {
                        statusImage = getFilePath(dataOn);
                    } else {
                        statusImage = getFilePath(dataOff);
                    }

                    if (StringUtils.isEmpty(statusImage)) {
                        return;
                    }

                    mIconView.setVisibility(View.VISIBLE);

                    Picasso.with(mBaseActivity)
                            .load(new File(statusImage))
                            .into(mIconView);
                    break;
                default:

                    break;
            }
        }
    }

    private String getFilePath(final String data) {
        final String path = FileUtils.getFilesStoragePath(mBaseActivity);
        final String url = data;

        if (StringUtils.isEmpty(url)) {
            return Constants.Strings.EMPTY;
        }

        final String fileName = FileUtils.getFileName(url);

        return path + FileUtils.FOLDER_DIVIDER + fileName;
    }
}