package com.example.geektrust;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

class RoundingTest {

     @Test
     void testRounding() {
         NumberFormat nf = NumberFormat.getNumberInstance();
         nf.setMaximumFractionDigits(0);
         String rounded = nf.format(123.656);
         System.out.println(rounded);

         String rounded1 = String.format("%.0f", 123.656);
         System.out.println(rounded1);

         DecimalFormat df = new DecimalFormat("#,###");
         df.setRoundingMode(RoundingMode.HALF_UP);
         String rounded2 = df.format(123.656);
         System.out.println(rounded2);

         double largeDouble = 123.656;
         BigDecimal big = new BigDecimal(largeDouble);
         big = big.setScale(0, RoundingMode.HALF_UP);
         String rounded3 = big.toString();
         System.out.println(rounded3);

         String newValue = Double.toString(Math.floor(largeDouble));
         System.out.println(newValue);

     }
}
