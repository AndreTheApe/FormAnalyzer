package andretheape.csci210;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;
public class FormAnalyzer 
{
     final byte BLACK = 0;
     final byte GREY =  2;
     final byte WHITE = 4;
     float whiteRate = 0.9f;
     float blackRate = 0.95f;
     int boxHeight = 20;
     int minHeight = 16;
     File f;
     String dir;
     BufferedImage image=null;
     int W, H;
     byte [][] b;
     public  FormAnalyzer(String dir, String old) 
    {
        try
        {
             f = new  File(dir, old);
             this.dir = dir;
             image = ImageIO.read(f);
             W = (int)image.getWidth();
             H = (int)image.getHeight();
             b = new byte[W][]; 
            
             int red, green, blue;
             int newPixel;
             int threshold =210;
          
            for(int i=0; i< W; i++) 
            {
                b[i] = new byte[H];
                for(int j=0; j< H; j++)
                {
                  red = new Color(image.getRGB(i, j)).getRed();
                  green = new Color(image.getRGB(i, j)).getGreen();
                  blue = new Color(image.getRGB(i, j)).getBlue();
                  int alpha = new Color(image.getRGB(i, j)).getAlpha();
                  newPixel = (int)Math.round(red*.4  + blue*.4  + green*.2);
                  if (newPixel>120 ) b[i][j] = WHITE;
                  else b[i][j] = 0;
                }
            }
        }
        catch (Exception e)
       {
             
       }
      }
      public void bw(String temp)  
      {
         try{
            int bl = (new Color(0,0,0)).getRGB();
            int wt = (new Color(255,255,255)).getRGB();
            int gr = (new Color(125,125,125)).getRGB();
            BufferedImage  bimg = new BufferedImage(W, H, BufferedImage.TYPE_BYTE_GRAY); 
            byte bb[][] = new byte[2][];
            for(int j=0; j < H; j++)
            {
                bb[0] = new byte[H]; 
                bb[1] = new byte[H];
            }
            for(int i=0; i< W; i++) 
            {
                for(int j=0; j < H; j++)
                {
                    if (b[i][j]==0
                     || i>0 && j>0 && i < W-1 && j < H-1 && b[i-1][j-1] +  b[i-1][j] + b[i-1][j+1] + b[i][j-1] + b[i][j] + b[i][j+1] +b[i+1][j-1] + b[i+1][j] + b[i+1][j+1] <= (WHITE*7)
                     || i>0 && j>0 && i < W-1   && b[i-1][j-1] +  b[i-1][j] +   b[i][j-1] + b[i][j] + b[i+1][j-1] + b[i+1][j]  <= (WHITE*4)
                     || i>0 && j>0 && j < H-1   && b[i-1][j-1] +  b[i-1][j] + b[i-1][j+1] + b[i][j-1] + b[i][j] + b[i][j+1]  <= WHITE*4
                     || i>0 && j>0 && b[i-1][j-1] +  b[i-1][j] +  b[i][j-1] + b[i][j] <= 2*WHITE
                     || j>0 && i < W-1 && j < H-1 &&  b[i][j-1] + b[i][j] + b[i][j+1] +b[i+1][j-1] + b[i+1][j] + b[i+1][j+1] <= (WHITE*4)
                     || i>0 && i < W-1 && j < H-1 &&   b[i-1][j] + b[i-1][j+1]   + b[i][j] + b[i][j+1]   + b[i+1][j] + b[i+1][j+1] <= (WHITE*4)
                     || i < W-1 && j < H-1 &&     b[i][j] + b[i][j+1]   + b[i+1][j] + b[i+1][j+1] <= 2*WHITE
                    )
                    {
                        bimg.setRGB(i, j, bl);
                        bb[i%2][j] = BLACK;
                    }
                    else
                    {
                        bimg.setRGB(i, j, wt);
                        bb[i%2][j] = WHITE;
                    } 
                }
                
                if (i > 0)
                {
                    for(int j=0; j < H; j++)
                    {
                        b[i-1][j] = bb[(i-1)%2][j]; 
                    }
                }
            }
            for(int j=0; j < H; j++)
            {
                 b[W-1][j] = bb[(W-1)%2][j]; 
            }
            
            
          File fi = new File(dir,temp);
          String target = temp.replaceFirst("^[^\\.]+.", "");
          ImageIO.write(bimg, target, fi);
       
       }
       catch (Exception e)
       {
            
       }
   }
    
   public  void  searchforrect(String temp) 
   {
       ArrayList<int []> rect = new ArrayList();
       int previousmini = 0,previousmaxi = 0,previousminj = 0,previousmaxj = 0;
       for (int j=0; j < H; j++)
       for (int i=0; i < W; i++)
       {
           byte cl = b[i][j];
           if (cl == BLACK || cl == WHITE)
           {
               byte ct =  (byte)(cl==BLACK?(BLACK+1):(WHITE-1));
               Queue<int []> s = new LinkedList();
               s.add(new int[]{i,j});
               b[i][j] = ct;
               int mini = W, minj= H, maxi = -1, maxj = -1, total=0;
               while (!s.isEmpty())
               {
                   int [] p = s.poll();
                   int[][] q = new int[][]{new int[]{p[0]-1,p[1]},new int[]{p[0]+1,p[1]},new int[]{p[0],p[1]-1},new int[]{p[0],p[1]+1}};
                   for (int k=0; k < 4; k++)
                   if (q[k][0] >=0 && q[k][0] < W && q[k][1]>=0 && q[k][1] < H && b[q[k][0]][q[k][1]] == cl)
                   {
                       s.add(q[k]);
                       b[q[k][0]][q[k][1]] = ct;
                   }

                   if (p[0] > maxi) maxi = p[0];
                   if (p[0] < mini) mini = p[0]; 
                   if (p[1] > maxj) maxj = p[1];
                   if (p[1] < minj) minj = p[1];
                   total++;
               }
               int width = maxi - mini + 1;
               int height = maxj - minj + 1;
               boolean isfield = false;
               if (cl == WHITE)
               {
                   if (total >= whiteRate*width*height &&
                          width >= 10 && height >= 10)
                   {
                       isfield = true;
                   }
               }
               else
               {
                   if (total >= blackRate * width * height
                       &&  width >= 10 && height <= 4) 
                   {    
                       int top = minj-1;
                       while (top >= 0)
                       {
                          int sum=0;
                          for (int j1=mini; j1 <= maxi; j1++)
                              if (b[j1][top] < GREY) 
                                  sum++;
                          if (sum > 0.02 * width)
                          {
                              top++;
                              break;
                          }
                          top--;
                       }
                       if (minj - top >= minHeight)
                       {
                           if (Math.abs(previousmini - mini) < 3 && Math.abs(previousmaxi - maxi) < 3 
                              && Math.abs(previousmaxj - top) <= 5)
                           {
                               for (int i1=mini+1; i1 <= maxi-1; i1++)
                               {
                                   b[i1][previousmaxj] = WHITE;
                               }
                               maxj = minj-1;
                               minj = previousminj; 
                           }
                           else
                           {   
                               maxj = minj-1;
                               minj = (top > maxj-boxHeight)? top:(maxj-boxHeight); 
                           }
                           previousmini = mini;
                           previousmaxi = maxi;
                           previousminj = minj;
                           previousmaxj = maxj;
                           isfield = true;
                       }
                   }
               }
               if (isfield)
               {
                   for (int i1=mini; i1 <= maxi; i1++)
                   {
                       b[i1][minj] = b[i1][maxj] = GREY;
                   }
                   for (int j1=minj+1; j1 <= maxj-1; j1++)
                   {
                       b[mini][j1] = b[maxi][j1] = GREY;
                   }
               }
           }
        }
       
        try
        {
            int red = (new Color(255, 0,0)).getRGB();
            
            BufferedImage  bimg = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB); 
            for(int i=0; i< W; i++) 
            {
                for(int j=0; j < H; j++)
                {
                    if (b[i][j] == GREY)
                       bimg.setRGB(i,j, red);
                    else
                       bimg.setRGB(i, j, image.getRGB(i, j));
                }
            }
            
          File fi = new File(dir,temp);
          String target = temp.replaceFirst("^[^\\.]+.", "");
          ImageIO.write(bimg, target, fi);
         }
         catch(Exception e){}
       
   }
   public static void main(String args[])
   {
       String dir = "C:\\Users\\pdamo\\Desktop";
       FormAnalyzer s = new FormAnalyzer(dir, "aa.jpg");
       s.bw("bb.jpg");
       s.searchforrect("cc.jpg");
   }
}