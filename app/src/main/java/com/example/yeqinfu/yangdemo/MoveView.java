package com.example.yeqinfu.yangdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeqinfu on 17-2-20.
 */
public class MoveView extends View {

    private int viewNumber = 10;//旋转view的数量
    private float radius = 400;//半径
    private float centerX;
    private float centerY;
    // 1.创建一个画笔
    private Paint mPaint = new Paint();
    // 1.创建一个画笔
    private Paint redPaint = new Paint();
    //粗画笔
    private Paint bigPaint=new Paint();

    private float circleRadius = 80;//子小圆的半径
    private float smallRadius = 20;//子小小圆的半径

    private List<Point> list;//子小圆的点集合
    private Point controllerPoint;//控制点相对于子圆的坐标
    private int focusIndex=-1;//焦点所在位置
    private int dis=10;//控制点和小球的表面距离
    private double radiusOffset=0;//焦点位置角偏移


    public void resetView(){
        viewNumber=10;
        radius=400;
        circleRadius=40;
        smallRadius=20;
        isTouchSmallCicle=false;
        isInMove=false;
        focusIndex=-1;
        int offset=(int)(-circleRadius-smallRadius-dis);
        controllerPoint=new Point(offset,offset);//控制点初始化相对位置
        invalidate();
    }



    // 2.初始化画笔
    private void initPaint() {
        mPaint.setColor(Color.BLACK);       //设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
        mPaint.setStrokeWidth(10f);         //设置画笔宽度为10px
        redPaint.setColor(Color.RED);       //设置画笔颜色
        redPaint.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
        redPaint.setStrokeWidth(10f);         //设置画笔宽度为10px
        bigPaint.setColor(Color.BLACK);       //设置画笔颜色
        bigPaint.setStyle(Paint.Style.STROKE);  //设置画笔模式为填充
        bigPaint.setStrokeWidth(10f);         //设置画笔宽度为10px
    }


    public MoveView(Context context) {
        super(context);
    }

    public MoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        centerX = Utils.getScreenWidth(getContext()) / 2;
        centerY = Utils.getScreenHeight(getContext()) / 2;
        list = new ArrayList<>();
        int offset=(int)(-circleRadius-smallRadius-dis);
        controllerPoint=new Point(offset,offset);//控制点初始化相对位置

    }

    public MoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public int getViewNumber() {
        return viewNumber;
    }

    public void setViewNumber(int viewNumber) {


        if (viewNumber>0){
            this.viewNumber = viewNumber;
            invalidate();
        }

    }


    @Override
    protected void onDraw(Canvas canvas) {
        list.clear();
        Point tempPoint = null;
        for (int i = 0; i < viewNumber; i++) {
            Point point = getPointByIndex(i);
            list.add(point);
            Path p=new Path();
            p.moveTo(point.x,point.y);
            p.lineTo(point.x+circleRadius,point.y);
            p.lineTo(point.x+circleRadius,point.y+circleRadius);
            p.lineTo(point.x,point.y+circleRadius);
            p.close();
            canvas.drawPath(p,mPaint);
          //  canvas.drawCircle(point.x, point.y, circleRadius, mPaint);
            if (focusIndex==i){//当前子view被选中
                tempPoint=point;
            }
        }
      /*  if (focusIndex!=-1){
            canvas.save();
            canvas.restore();
            canvas.translate(tempPoint.x, tempPoint.y);
            canvas.translate(controllerPoint.x, controllerPoint.y);
            canvas.drawCircle(0, 0, smallRadius, redPaint);
            Path p=new Path();
            int offset=(int)(smallRadius+circleRadius+dis);
            p.lineTo(2*offset,0);
            p.lineTo(2*offset,2*offset);
            p.lineTo(0,2*offset);
            p.close();
            canvas.drawPath(p,bigPaint);
            canvas.save();

        }*/


    }


    /**
     * 根据下标指定一个点位置
     *
     * @param index
     * @return
     */
    private Point getPointByIndex(int index) {
        Point p = new Point();
        double d = 1.00 * index / viewNumber * 2 * Math.PI-radiusOffset;
        double x = centerX - Math.cos(d) * radius;
        double y = centerY - Math.sin(d) * radius;
        p.set((int) x, (int) y);
        return p;
    }

    boolean isInMove = false;//是否点击子view
    boolean isTouchSmallCicle = false;//是否点击小小圆
    int touchSmallCicleIndex=0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {//点击判断
            isInMove = isTouchChildView(event.getX(), event.getY());
            if (isInMove){
                invalidate();
            }
            isTouchSmallCicle = isTouchSmallChildView(event.getX(), event.getY());


        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isInMove) {
                changeRadiusCircle(event.getX(),event.getY());

                invalidate();
            }
            if (isTouchSmallCicle&&focusIndex!=-1){
                Point p=list.get(focusIndex);
                if (p.x>0&&p.y>0){//一象限list
                    controllerPoint.set((int) (event.getX() - p.x), (int) (event.getY() - p.y));
                }else if (p.x<0&&p.y>0){//二象限
                    controllerPoint.set((int)(p.x-event.getX()),(int)(event.getY()-p.y));
                }else if (p.x<0&&p.y<0){//三象限
                    controllerPoint.set((int)(p.x-event.getX()),(int)(-event.getY()+p.y));
                }else{
                    controllerPoint.set((int)(event.getX()-p.x),(int)(-event.getY()+p.y));
                }



                invalidate();
            }
        }


        return true;
    }

    private void changeRadiusCircle(float x,float y) {
        double xx = (x - centerX) * (x - centerX);
        double yy = (y - centerY) * (y - centerY);
        radius = (float) Math.sqrt(xx + yy);
        if (x-centerX>0&&y-centerY>0){//一象限
            radiusOffset= (Math.PI- Math.atan(yy/xx));
        }else if (x-centerX<0&&y-centerY>0){
            radiusOffset= (Math.atan(yy/xx));
        }else if (x-centerX<0&&y-centerY<0){
            radiusOffset= (2*Math.PI- Math.atan(yy/xx));
        }else {
            radiusOffset= (Math.PI+Math.atan(yy/xx));
        }
    }

    /**
     * 判断触点是否在某个小圆
     *
     * @return
     */
    private boolean isTouchChildView(float x, float y) {
        for (int i = 0; i < list.size(); i++) {
            Point p = list.get(i);
            float dis = (float) Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
            if (dis < circleRadius+this.dis+smallRadius) {
                focusIndex=i;
                return true;
            }
        }

        return false;
    }

    /**
     * 判断触点是否在某个小小圆
     *
     * @return
     */
    private boolean isTouchSmallChildView(float x, float y) {
        if (focusIndex!=-1){
           float xx=controllerPoint.x+list.get(focusIndex).x;
            float yy=controllerPoint.y+list.get(focusIndex).y;

            float xxx=(xx-x)*(xx-x);
            float yyy=(yy-y)*(yy-y);
            double offset=Math.sqrt(xxx+yyy);

            if (offset<smallRadius+dis){
                return true;
            }
        }
        return false;
    }





}
