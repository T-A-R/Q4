package com.example.quizer3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import android.graphics.drawable.VectorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
//import androidx.test.espresso.matcher.BoundedMatcher;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.hamcrest.TypeSafeMatcher;

public class CustomMatchers {
//
//    public static Matcher<View> withBackground(final int resourceId) {
//        return new TypeSafeMatcher<View>() {
//
//            @Override
//            public boolean matchesSafely(View view) {
//                return sameBitmap(view.getContext(), view.getBackground(), resourceId);
//            }
//
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("has background resource " + resourceId);
//            }
//        };
//    }
//
//    public static Matcher<View> withCompoundDrawable(final int resourceId) {
//        return new BoundedMatcher<View, TextView>(TextView.class) {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("has compound drawable resource " + resourceId);
//            }
//
//            @Override
//            public boolean matchesSafely(TextView textView) {
//                for (Drawable drawable : textView.getCompoundDrawables()) {
//                    if (sameBitmap(textView.getContext(), drawable, resourceId)) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//        };
//    }
//
//    public static Matcher<View> withImageDrawable(final int resourceId) {
//        return new BoundedMatcher<View, ImageView>(ImageView.class) {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("has image drawable resource " + resourceId);
//            }
//
//            @Override
//            public boolean matchesSafely(ImageView imageView) {
//                return sameBitmap(imageView.getContext(), imageView.getDrawable(), resourceId);
//            }
//        };
//    }
//
//    public static Matcher<View> withImageVectorDrawable(final int resourceId) {
//        return new BoundedMatcher<View, ImageView>(ImageView.class) {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("has image drawable resource " + resourceId);
//            }
//
//            @Override
//            public boolean matchesSafely(ImageView imageView) {
//                return sameVectorBitmap(imageView.getContext(), imageView.getDrawable(), resourceId);
//            }
//        };
//    }
//
//    private static boolean sameBitmap(Context context, Drawable drawable, int resourceId) {
//        Drawable otherDrawable = context.getResources().getDrawable(resourceId);
//        if (drawable == null || otherDrawable == null) {
//            return false;
//        }
//        if (drawable instanceof StateListDrawable && otherDrawable instanceof StateListDrawable) {
//            drawable = drawable.getCurrent();
//            otherDrawable = otherDrawable.getCurrent();
//        }
//        if (drawable instanceof BitmapDrawable) {
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//            Bitmap otherBitmap = ((BitmapDrawable) otherDrawable).getBitmap();
//            return bitmap.sameAs(otherBitmap);
//        }
//        return false;
//    }
//
//    private static boolean sameVectorBitmap(Context context, Drawable drawable, int resourceId) {
//        if(drawable instanceof VectorDrawableCompat) {
//            VectorDrawableCompat otherDrawable = VectorDrawableCompat.create(context.getResources(), resourceId, null);
//            if (otherDrawable == null) return false;
//
//            Bitmap bitmap = vectorDrawableCompatToBitmap((VectorDrawableCompat) drawable);
//            Bitmap otherBitmap = vectorDrawableCompatToBitmap(otherDrawable);
//
//            return bitmap.sameAs(otherBitmap);
//        } else if(drawable instanceof VectorDrawable) {
//            Drawable otherDrawable = AppCompatResources.getDrawable(context, resourceId);
//            if (otherDrawable == null) return false;
//
//            Bitmap bitmap = vectorDrawableToBitmap((VectorDrawable) drawable);
//            Bitmap otherBitmap = vectorDrawableToBitmap((VectorDrawable) otherDrawable);
//
//            return bitmap.sameAs(otherBitmap);
//        } else return false;
//    }
//
//
//    private static Bitmap vectorDrawableCompatToBitmap(VectorDrawableCompat vectorDrawable) {
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        vectorDrawable.draw(canvas);
//        return bitmap;
//    }
//
//    private static Bitmap vectorDrawableToBitmap(VectorDrawable vectorDrawable) {
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        vectorDrawable.draw(canvas);
//        return bitmap;
//    }
//
//    public static Matcher<View> atPosition(final int position, final Matcher<View> itemMatcher) {
////        checkNotNull(itemMatcher);
//        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("has item at position " + position + ": ");
//                itemMatcher.describeTo(description);
//            }
//
//            @Override
//            protected boolean matchesSafely(final RecyclerView view) {
//                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
//                if (viewHolder == null) {
//                    // has no item on such position
//                    return false;
//                }
//                return itemMatcher.matches(viewHolder.itemView);
//            }
//        };
//    }
//

}
