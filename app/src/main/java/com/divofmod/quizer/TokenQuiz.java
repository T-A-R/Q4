package com.divofmod.quizer;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public abstract class TokenQuiz {
   public static String TokenQuiz(String  id)
   {

      SimpleDateFormat patt =  new SimpleDateFormat("yyyyMMDD");
       patt.format(new Date());

       String d  = patt + rand();
       String token = d;

       return token;
   }

   private static String rand()
   {
       String data = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
       String s = "";
       Random r = new Random();
       for (int i = 0; i < 12; i++)
       {
          int rand =  r.nextInt(62);
           s = s + data.substring(rand,rand+1);
       }

       return s;
   }
}
