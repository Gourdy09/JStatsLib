package com.statistics.utils;

import java.awt.Color;

public enum Themes{

   // Pre-defined Themes
   /**
    * <summary>
    * A list of themes to use when rendering the plots
    * Theme = [BoxColor, MedianColor, OutlierColor, LineColor, TextColor, BackgroundColor]
    * </summary>
    * */ 
   
   LSD(new Color[]{
       new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256)),
       new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256)),
       new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256)),
       new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256)),
       new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256)),
       new Color((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256))
   }),
        
   LIGHT(new Color[]{
         new Color(87, 115, 163),
         new Color(211, 112, 46),
         new Color(211, 46, 46),
         new Color(76, 76, 76),
         new Color(76, 76, 76),
         new Color(234, 234, 242)
   }),
   
   DARK(new Color[]{
        new Color(0, 240, 245),
        new Color(255, 170, 31),
        new Color(163, 73, 164),
        new Color(0, 162, 232),
        new Color(255, 255, 255),
        new Color(0, 0, 0)
   }),

    GRAYSCALE(new Color[]{
        new Color(200, 200, 200),
        new Color(40, 40, 40),
        new Color(75, 75, 75),
        new Color(200, 0, 0),
        new Color(0, 0, 0),
        new Color(250, 250, 250)
    }),

   CAMO(new Color[]{
        new Color(47, 77, 37),
        new Color(214, 158, 28),
        new Color(139, 148, 126),
        new Color(30, 82, 31),
        new Color(3, 28, 4),
        new Color(186, 173, 143)
   }),

   RED(new Color[]{
        new Color(255, 0, 0),
        new Color(156, 64, 54),
        new Color(239, 88, 86),
        new Color(130, 82, 31),
        new Color(255, 255, 255),
        new Color(129, 36, 29)
   }),

   BLACKGREEN(new Color[]{
        new Color(24, 212, 21),
        new Color(156, 64, 54),
        new Color(239, 88, 86),
        new Color(130, 82, 31),
        new Color(255, 255, 255),
        new Color(0, 0, 0)
   }),
   
   REDWHITE(new Color[]{ 
        new Color(255, 0, 0),
        new Color(255, 0, 0),
        new Color(255, 0, 0),
        new Color(255, 0, 0), 
        new Color(255, 0, 0),
        new Color(255, 255, 255)
   }),

   REDGREENBLUE(new Color[]{ 
        new Color(255, 0, 0),
        new Color(0, 255, 0),
        new Color(0, 0, 255),
        new Color(255, 0, 0), 
        new Color(0, 255, 0),
        new Color(0, 0, 0)
   }),
   
   NAVY(new Color[]{ 
        new Color(60, 84, 163),
        new Color(23, 30, 54),
        new Color(76, 88, 133),
        new Color(32, 75, 232),
        new Color(5, 21, 33),
        new Color(129, 160, 184)
   }),
   
   CHRISTMAS(new Color[]{ 
        new Color(255, 255, 255),
        new Color(35, 161, 16),
        new Color(23, 122, 45),
        new Color(255, 255, 255),
        new Color(35, 161, 16),
        new Color(255, 0, 0)
   }),

   HALLOWEEN(new Color[]{ 
        new Color(240, 99, 5),
        new Color(255, 255, 255),
        new Color(240, 99, 5),
        new Color(255, 255, 255),
        new Color(240, 99, 5),
        new Color(0, 0, 0)
   }),

   PITCHBLACK(new Color[]{ 
        new Color(0, 0, 0),
        new Color(0, 0, 0),
        new Color(0, 0, 0),
        new Color(0, 0, 0),
        new Color(0, 0, 0),
        new Color(0, 0, 0)
   }),

   OCEANBLUES(new Color[]{ 
        new Color(78, 184, 237),
        new Color(15, 95, 135),
        new Color(86, 139, 204),
        new Color(51, 86, 130),
        new Color(9, 85, 181),
        new Color(255, 255, 255)
   }),

   YELLOWORANGE(new Color[]{ 
        new Color(189, 75, 0),
        new Color(214, 206, 60),
        new Color(255, 191, 112),
        new Color(161, 134, 0),
        new Color(179, 79, 52),
        new Color(255, 255, 255)
   }),
   
   PURPLE(new Color[]{ 
        new Color(87, 1, 78),
        new Color(235, 0, 210),
        new Color(92, 6, 70),
        new Color(194, 0, 100),
        new Color(64, 17, 41),
        new Color(201, 153, 196)
    }),  

    PUKE(new Color[]{ 
        new Color(64, 43, 8),
        new Color(10, 110, 53),
        new Color(255, 169, 31),
        new Color(199, 187, 169),
        new Color(31, 5, 26),
        new Color(144, 189, 109)
    }),

    VERYBRIGHT(new Color[]{ 
        new Color(131, 217, 222),
        new Color(255, 0, 0),
        new Color(255, 0, 195),
        new Color(255, 247, 0),
        new Color(158, 89, 255),
        new Color(0, 0, 0)
    }),

    FLASHBANG(new Color[]{ 
        new Color(255, 255, 255),
        new Color(255, 255, 255),
        new Color(255, 255, 255),
        new Color(255, 255, 255),
        new Color(255, 255, 255),
        new Color(255, 255, 255)
    }),

    AMERICA(new Color[]{ 
        new Color(0, 33, 71),
        new Color(179, 25, 66),
        new Color(255, 255, 255),
        new Color(0, 33, 71),
        new Color(179, 25, 66),
        new Color(0, 0, 0)
    });
   
   public final Color[] theme;
   
   Themes(Color[] t){
      this.theme = t;
   }
}