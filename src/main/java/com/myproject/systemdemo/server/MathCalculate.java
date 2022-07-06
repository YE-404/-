package com.myproject.systemdemo.server;

import com.myproject.systemdemo.domain.ClockInfo;
import com.myproject.systemdemo.domain.result.PointInfo;
import com.myproject.systemdemo.domain.result.RectMes;

import static java.lang.Math.atan2;

public class MathCalculate {
      public static ClockInfo calculate(PointInfo min, PointInfo middle, PointInfo max, RectMes rectMes){
          PointInfo circlePoint = calculateCircle(min,middle,max);
          double minAngle = GetPointHAngle(min,circlePoint);
          double maxAngle = GetPointHAngle(max,circlePoint);
          ClockInfo clockInfo = judgeAngle(minAngle, maxAngle);
          System.out.println(rectMes);
          double x = (double) (circlePoint.getX() - rectMes.getX()) / rectMes.getW();
          double y = (double) (circlePoint.getY() - rectMes.getY()) / rectMes.getH();
          System.out.println("x = " + x);
          System.out.println("y = " + y);
          clockInfo.setMaxValue(max.getValue());
          clockInfo.setMinValue(min.getValue());
          clockInfo.setCircleX(x);
          clockInfo.setCircleY(y);
          return clockInfo;
    }
     public static PointInfo calculateCircle(PointInfo min, PointInfo middle,PointInfo max){
         float yDelta_a = middle.getY() - min.getY();
         float xDelta_a = middle.getX() - min.getX();
         float yDelta_b = max.getY() - middle.getY();
         float xDelta_b = max.getX() - middle.getX();
         PointInfo circlePoint = new PointInfo();

         float aSlope = yDelta_a/xDelta_a;
         float bSlope = yDelta_b/xDelta_b;
         int x = (int) ((aSlope*bSlope*(min.getY() - max.getY()) + bSlope*(min.getX() + middle.getX())
                          - aSlope*(middle.getX()+max.getX()) )/(2* (bSlope-aSlope) ));
         int y = (int) (-1*(x - (min.getX()+middle.getX())/2)/aSlope +  (min.getY()+middle.getY())/2);
         circlePoint.setX(x);
         circlePoint.setY(y);
         return circlePoint;

//         PointInfo circlePoint = new PointInfo();
//         int dx1 = min.getX(), dx2 = middle.getX(), dx3 = max.getX();
//         int dy1 = min.getY(), dy2 = middle.getY(), dy3 = max.getY();
//         int midx1 = (dx1 + dx2) / 2;
//         int midy1 = (dy1 + dy2) / 2;
//         int midx2 = (dx2 + dx3) / 2;
//         int midy2 = (dy2 + dy3) / 2;
//         int h1 = - (dx2 - dx1) / (dy2 - dy1);
//         int h2 = - (dx3 - dx2) / (dy3 - dy2);
//         int x = (midy2 - midy1 + midx1 * h1 - midx2 * h2) / (h1 - h2);
//         int y = h1 * (x - midx1) + midy1;
//         circlePoint.setX(x);
//         circlePoint.setY(y);
//         return circlePoint;
     }

    public static PointInfo fourPointCalculateCircle(PointInfo min, PointInfo middleS, PointInfo middleL, PointInfo max){
        PointInfo circlePoint = new PointInfo();
        int dx1 = min.getX(), dx2 = middleS.getX(), dx3 = middleL.getX(), dx4 = max.getX();
        int dy1 = min.getY(), dy2 = middleS.getY(), dy3 = middleL.getY(), dy4 = max.getY();
        int midx1 = (dx1 + dx3) / 2;
        int midy1 = (dy1 + dy3) / 2;
        int midx2 = (dx2 + dx4) / 2;
        int midy2 = (dy2 + dy4) / 2;
        int h1 = - (dx3 - dx1) / (dy3 - dy1);
        int h2 = - (dx4 - dx2) / (dy4 - dy2);
        int x = (midy2 - midy1 + midx1 * h1 - midx2 * h2) / (h1 - h2);
        int y = h1 * (x - midx1) + midy1;
        circlePoint.setX(x);
        circlePoint.setY(y);
        return circlePoint;
    }

     public static ClockInfo judgeAngle(double minAngle, double maxAngle){
         double angleRange;
         if(minAngle < maxAngle){
             maxAngle = maxAngle - 360;
         }
         angleRange = minAngle - maxAngle;
         return new ClockInfo(minAngle,maxAngle,angleRange);
     }


     public static double GetPointHAngle(PointInfo p, PointInfo ori)//根据坐标x、y值计算其方位角
     {
         double hAngle = 0;
         double dy = p.getY() - ori.getY();
         double dx = p.getX() - ori.getX();
         if (dx==0 && dy>0)
         {
             hAngle = 270;
         }
         else if(dx==0 && dy<0)
         {
             hAngle = 90;
         }
         else if(dy==0 && dx>0)
         {
             hAngle = 0;
         }
         else if(dy==0 && dx<0)
         {
             hAngle = 180;
         }
         else if(dx>0 && dy>0)//第四象限
         {
             System.out.println("第四象限"+p.getValue());
             hAngle = 360 - atan2(dy,dx)*180/Math.PI;
         }
         else if(dx>0 && dy<0)//第一象限
         {
             System.out.println("第一象限"+p.getValue());
             hAngle = - atan2(dy,dx)*180/Math.PI;
         }
         else if(dx<0 && dy<0)//第二象限
         {
             System.out.println("第二象限"+p.getValue());
             hAngle = - atan2(dy,dx)*180/Math.PI;
         }
         else if(dx<0 && dy>0)//第三象限
         {
             System.out.println("第三象限"+p.getValue());
             hAngle = 360 - atan2(dy,dx)*180/Math.PI;
         }
         return hAngle;
     }


 }
