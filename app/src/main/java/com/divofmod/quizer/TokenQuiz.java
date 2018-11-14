package com.divofmod.quizer;

import android.util.Log;

import java.util.Random;

public abstract class TokenQuiz {
   public static String TokenQuiz(String  id)
   {
       Random r = new Random();
       Long i = System.currentTimeMillis() * Integer.parseInt(id) + r.nextInt(500);
       String token = String.valueOf(i);
       Log.i("Token", "TokenQuiz: " + token);
       return token;
   }
}
