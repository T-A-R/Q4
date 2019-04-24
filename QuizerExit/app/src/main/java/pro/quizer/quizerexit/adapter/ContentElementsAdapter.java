package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

    private final Context mContext;
    private final List<ElementModel> mContents;

    public ContentElementsAdapter(final Context pContext, final List<ElementModel> pContents) {
        this.mContext = pContext;
        this.mContents = pContents;
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
        final View itemView = LayoutInflater.from(pViewGroup.getContext()).inflate(R.layout.adapter_info_element, pViewGroup, false);
        return new InfoElementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InfoElementViewHolder pAnswerListViewHolder, final int pPosition) {
        pAnswerListViewHolder.onBind(mContents.get(pPosition), pPosition);
    }

    @Override
    public int processNext() throws Exception {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    class InfoElementViewHolder extends AbstractViewHolder {

        TextView mText;
        ImageView mImageView;
        BetterVideoPlayer mVideoPlayer;
        BetterVideoPlayer mAudioPlayer;

        InfoElementViewHolder(final View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.info_text);
            mImageView = itemView.findViewById(R.id.info_image);
            mVideoPlayer = itemView.findViewById(R.id.info_video_player);
            mAudioPlayer = itemView.findViewById(R.id.info_audio_player);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBind(final ElementModel pContent, final int pPosition) {
            final OptionsModel options = pContent.getOptions();
            final String data = pContent.getData();

            switch (pContent.getType()) {
                case ElementSubtype.TEXT:
                    UiUtils.setTextOrHide(mText, options.getText());

                    break;
                case ElementSubtype.AUDIO:
                    final String fileAudioPath = getFilePath(data);

                    if (StringUtils.isEmpty(fileAudioPath)) {
                        return;
                    }

                    mAudioPlayer.setVisibility(View.VISIBLE);
                    mAudioPlayer.setSource(Uri.fromFile(new File(fileAudioPath)));
                    mAudioPlayer.enableSwipeGestures(((BaseActivity) mContext).getWindow());

                    break;
                case ElementSubtype.VIDEO:
                    final String fileVideoPath = getFilePath(data);

                    if (StringUtils.isEmpty(fileVideoPath)) {
                        return;
                    }

                    mVideoPlayer.setVisibility(View.VISIBLE);
                    mVideoPlayer.setSource(Uri.fromFile(new File(fileVideoPath)));

                    mVideoPlayer.setHideControlsOnPlay(true);

                    mVideoPlayer.enableSwipeGestures(((BaseActivity) mContext).getWindow());

                    break;
                case ElementSubtype.IMAGE:
                    final String filePhotooPath = getFilePath(data);

                    if (StringUtils.isEmpty(filePhotooPath)) {
                        return;
                    }

                    mImageView.setVisibility(View.VISIBLE);

                    Picasso.with(mContext)
                            .load(new File(filePhotooPath))
                            .into(mImageView);


                    break;
                default:

                    break;
            }
        }
    }

    private String getFilePath(final String data) {
        final String path = FileUtils.getFilesStoragePath(mContext);
        final String url = data;

        if (StringUtils.isEmpty(url)) {
            return Constants.Strings.EMPTY;
        }

        final String fileName = url.substring(url.lastIndexOf(FileUtils.FOLDER_DIVIDER), url.length());

        return path + fileName;
    }

}