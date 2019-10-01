package pro.quizer.quizer3.view.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import pro.quizer.quizer3.MainActivity;
import pro.quizer.quizer3.view.Anim;

import pro.quizer.quizer3.R;

public class NotificationFragment extends ScreenFragment {

    private RelativeLayout cont;
    private TextView text;
    private ImageView closeRing;
    private ImageView closeCross;
    private boolean positive = true;
    private boolean isClosed = true;

    public NotificationFragment() {
        super(R.layout.notification);
    }

    @Override
    protected void onReady() {

        cont = (RelativeLayout) findViewById(R.id.cont);
        text = (TextView) findViewById(R.id.tv_notification);
        closeRing = (ImageView) findViewById(R.id.img_close_ring);
        closeCross = (ImageView) findViewById(R.id.img_close_crest);

        text.setSelected(true);

        cont.setOnClickListener(v -> textPress());
        closeRing.setOnClickListener(v -> closePress());
    }

    public void setData(String txtNotification, String action, String eventID) {

        if (action.equals("success")) this.positive = true;
        if (action.equals("incorrect")) this.positive = false;
        if (action.equals("account_activated")) {
            MainFragment.wasNotify = MainFragment.NotifyType.Activated;
            this.positive = true;
        }
        isClosed = false;

        Thread thread = new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> {
                            text.setText(txtNotification);

                            if (positive) {
                                Log.d(MainActivity.TAG, "Notification positive: " + txtNotification);
                                cont.setVisibility(View.VISIBLE);
                                cont.startAnimation(Anim.getAnimation(getContext(), R.anim.show_notification));
                                cont.setBackgroundResource(R.drawable.notification_green);
                                closeRing.setBackgroundResource(R.drawable.circle_yellow);
                                closeCross.setBackgroundResource(R.drawable.ico_x);
                            } else {
                                Log.d(MainActivity.TAG, "Notification negative: " + txtNotification);
                                cont.setVisibility(View.VISIBLE);
                                cont.startAnimation(Anim.getAnimation(getContext(), R.anim.show_notification));
                                cont.setBackgroundResource(R.drawable.notification_orange);
                                closeRing.setBackgroundResource(R.drawable.circle_green);
                                closeCross.setBackgroundResource(R.drawable.ico_close_white);
                            }

                            text.setSelected(true);

                            Slider slider = new Slider();
                            slider.execute();
                        });

                }
            }
        };
        thread.start();

    }

    public void hide() {
        cont.setVisibility(View.GONE);
        isClosed = true;
    }

    class Slider extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!isClosed) {
                cont.startAnimation(Anim.getAnimation(getContext(), R.anim.hide_notification));
                cont.setVisibility(View.GONE);
                isClosed = true;
                //TODO Something
            }
        }
    }

    private void textPress() {

        isClosed = true;

        Thread thread = new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    getActivity().runOnUiThread(() -> {
                        hide();
                        //TODO Something
                    });
                }
            }
        };
        thread.start();
    }

    private void closePress() {
        if (!isClosed) {
            cont.startAnimation(Anim.getAnimation(getContext(), R.anim.hide_notification));
            cont.setVisibility(View.GONE);
            isClosed = true;
            //TODO Something
        }
    }

    private void restartActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}
