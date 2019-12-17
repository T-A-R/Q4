package pro.quizer.quizer3.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("unused")
public class ImageUtils {
    public static final int MAX_IMAGE_WIDTH = 900;

    public static Bitmap getCropedBitmap(Bitmap input, int width, int height) {
        if (input.getWidth() > MAX_IMAGE_WIDTH) {
            input = ImageUtils.resize(input, MAX_IMAGE_WIDTH);
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        float inputAspect = (float) input.getWidth() / (float) input.getHeight();
        float outputAspect = (float) width / (float) height;
        int srcWidth;
        int srcHeight;
        if (inputAspect < outputAspect) { // по ширине
            srcWidth = input.getWidth();
            srcHeight = (int) (input.getWidth() / outputAspect);
        } else { // по высоте
            srcWidth = (int) (input.getHeight() * outputAspect);
            srcHeight = input.getHeight();
        }
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        Rect dst = new Rect(0, 0, width, height);
        int x = (input.getWidth() - srcWidth) / 2;
        int y = (input.getHeight() - srcHeight) / 2;
        Rect src = new Rect(x, y, x + srcWidth, y + srcHeight);
        canvas.drawBitmap(input, src, dst, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static int getAverageColor(Bitmap bitmap) {
        long redBucket = 0;
        long greenBucket = 0;
        long blueBucket = 0;
        long pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                int c = bitmap.getPixel(x, y);
                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
            }
            if (Thread.interrupted())
                return 0;
        }
        return Color.rgb((int) (redBucket / pixelCount), (int) (greenBucket / pixelCount), (int) (blueBucket / pixelCount));
    }

    public static void getAverageColorAsync(final Bitmap bitmap, final AverageColorListener listener) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                return getAverageColor(bitmap);
            }

            @Override
            protected void onPostExecute(Integer color) {
                super.onPostExecute(color);
                listener.onColor(color);
            }
        }.execute();
    }

    public interface AverageColorListener {
        void onColor(int color);
    }

    static public Bitmap getGradient(int width, int height, int color) {
        Bitmap gradient = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Shader shader = new LinearGradient(0, -height / 2, 0, height * 0.9f, Color.TRANSPARENT, color, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setShader(shader);
        Canvas canvas = new Canvas(gradient);
        canvas.drawRect(0, 0, width, height, paint);
        return gradient;
    }

    static public Bitmap getGradiented(Bitmap input, int color) {
        if (input.getWidth() > MAX_IMAGE_WIDTH) {
            input = ImageUtils.resize(input, MAX_IMAGE_WIDTH);
        }
        int width = input.getWidth();
        int height = input.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Rect rect = new Rect(0, 0, width, height);
        canvas.drawBitmap(input, rect, rect, null);
        canvas.drawBitmap(getGradient(width, height, color), rect, rect, null);
        return output;
    }

    static public Bitmap getColorCovered(Bitmap input, int color) {
        int width = input.getWidth();
        int height = input.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Rect rect = new Rect(0, 0, width, height);
        canvas.drawBitmap(input, rect, rect, null);
        canvas.drawColor(color);
        return output;
    }

    static public Bitmap getEmpty() {
        return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
    }

    static public Bitmap getBitmap(Context content, int src) {
        System.gc();
        return BitmapFactory.decodeResource(content.getResources(), src);
    }

    static public AsyncTask<Void, Void, Bitmap> getBitmap(String url, BitmapListener listener) {
        AsyncTask<Void, Void, Bitmap> task = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                System.gc();
//                try {
//                    Bitmap bitmap = Picasso.get().load(url).get();
////                    bitmap = ServerAPI.cropBmpToServer(bitmap);
//                    return bitmap;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return null;
//                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (!isCancelled())
                    listener.onBitmap(bitmap);
            }
        };
        task.execute();
        return task;
    }

    static public Thread getBitmapThread(String url, BitmapListener listener) {
        Thread thread = new Thread(() -> {
            System.gc();
            Bitmap bitmap;
            URL urlObj;
            try {
                urlObj = new URL(url);
            } catch (MalformedURLException e) {
                Log.d("IRON", "ImageUtils.getBitmapThread() MalformedURLException " + e);
                if (!Thread.interrupted())
                    listener.onBitmap(null);
                return;
            }
            InputStream in;
            try {
                in = urlObj.openStream();
            } catch (IOException e) {
                Log.d("IRON", "ImageUtils.getBitmapThread() IOException" + e + " " + e.getMessage());
                if (!Thread.interrupted())
                    listener.onBitmap(null);
                return;
            }
            bitmap = BitmapFactory.decodeStream(in);
            System.gc();
            if (!Thread.interrupted()) {
//                bitmap = ServerAPI.cropBmpToServer(bitmap);
                System.gc();
                listener.onBitmap(bitmap);
            }
        });
        thread.start();
        return thread;
    }

    public interface BitmapListener {
        void onBitmap(Bitmap bitmap);
    }

    public static Bitmap getScreenshot(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    public static Bitmap getLightweightBitmap(String picturePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);
        if (bitmap.getWidth() > 1000) {
            bitmap = ImageUtils.resize(bitmap, 1000);
        }
        return bitmap;
    }

    private static Bitmap resize(Bitmap bitmap, int width) {
        float originalWidth = bitmap.getWidth();
        float originalHeight = bitmap.getHeight();

        if (originalWidth < width) return bitmap;

        float scale = ((float) width) / originalWidth;

        Bitmap resizedBitmap = Bitmap.createBitmap((int) (originalWidth * scale), (int) (originalHeight * scale), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(resizedBitmap);

        Matrix transformation = new Matrix();
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(bitmap, transformation, paint);

        return resizedBitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
