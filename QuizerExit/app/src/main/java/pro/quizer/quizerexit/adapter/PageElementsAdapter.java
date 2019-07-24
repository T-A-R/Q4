package pro.quizer.quizerexit.adapter;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import pro.quizer.quizerexit.IAdapter;
import pro.quizer.quizerexit.NavigationCallback;
import pro.quizer.quizerexit.R;
import pro.quizer.quizerexit.activity.BaseActivity;
import pro.quizer.quizerexit.database.model.UserModelR;
import pro.quizer.quizerexit.fragment.ElementFragment;
import pro.quizer.quizerexit.model.config.ElementModel;
import pro.quizer.quizerexit.model.database.UserModel;
import pro.quizer.quizerexit.utils.ViewIdGenerator;

public class PageElementsAdapter extends RecyclerView.Adapter<PageElementsAdapter.PageElementViewHolder> implements IAdapter {

    private final BaseActivity mBaseActivity;
    private final List<ElementModel> mContents;
    private NavigationCallback mCallback;
    private String mToken;
    private String mLoginAdmin;
    private int mUserId;
    private String mUserLogin;
    private boolean mIsPhotoQuestionnaire;
    private int mProjectId;
//    private UserModel mUser;
    private UserModelR mUser;
    private HashMap<Integer, ElementModel> mMap;
    private FragmentManager fragmentManager;
    private boolean mIsButtonVisible;
    private ElementModel mParentElement;

    public PageElementsAdapter(
            final ElementModel parentElement,
            final boolean isButtonVisible,
            final BaseActivity pContext,
            final List<ElementModel> pContents,
            final NavigationCallback pCallback,
            final String pToken,
            final String pLoginAdmin,
            final int pUserId,
            final String pUserLogin,
            final boolean pIsPhotoQuestionnaire,
            final int pProjectId,
//            final UserModel pUser,
            final UserModelR pUser,
            final HashMap<Integer, ElementModel> pMap,
            final FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.mBaseActivity = pContext;
        this.mContents = pContents;
        mParentElement = parentElement;
        mIsButtonVisible = isButtonVisible;
        mCallback = pCallback;
        mToken = pToken;
        mLoginAdmin = pLoginAdmin;
        mUserId = pUserId;
        mUserLogin = pUserLogin;
        mIsPhotoQuestionnaire = pIsPhotoQuestionnaire;
        mProjectId = pProjectId;
        mUser = pUser;
        mMap = pMap;
    }

    @Override
    public void onViewDetachedFromWindow(final PageElementViewHolder holder) {
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
    public PageElementViewHolder onCreateViewHolder(@NonNull final ViewGroup pViewGroup, final int pPosition) {
        final View itemView = LayoutInflater.from(mBaseActivity).inflate(R.layout.adapter_page_element, pViewGroup, false);
        return new PageElementViewHolder(itemView, mBaseActivity, fragmentManager);
    }

    @Override
    public void onBindViewHolder(@NonNull final PageElementViewHolder pAnswerListViewHolder, final int pPosition) {
        pAnswerListViewHolder.onBind(mContents.get(pPosition), pPosition);
    }

    @Override
    public int processNext() throws Exception {
        final int jump = mParentElement.getOptions().getJump();

        for (final ElementModel elementModel : mParentElement.getElements()) {
            if (!elementModel.isAnyChecked()) {
                throw new Exception(mBaseActivity.getString(R.string.NOTIFICATION_SELECT_ANY_ITEM));
            }
        }

        return jump;
    }


    class PageElementViewHolder extends AbstractViewHolder {

        public final int frameId;
        public FragmentManager mFragmentManager;

        PageElementViewHolder(final View itemView, final BaseActivity pBaseActivity, final FragmentManager fragmentManager) {
            super(itemView, pBaseActivity);
            frameId = ViewIdGenerator.generateViewId();
            itemView.findViewById(R.id.page_content).setId(frameId);

            mFragmentManager = fragmentManager;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBind(final ElementModel pContent, final int pPosition) {
            mFragmentManager.beginTransaction()
                    .replace(frameId,
                            ElementFragment.newInstance(
                                    false,
                                    false,
                                    frameId,
                                    pContent,
                                    mCallback,
                                    mToken,
                                    mLoginAdmin,
                                    mUserId,
                                    mUserLogin,
                                    mIsPhotoQuestionnaire,
                                    mProjectId,
                                    mUser,
                                    mMap))
                    .commit();
        }
    }
}