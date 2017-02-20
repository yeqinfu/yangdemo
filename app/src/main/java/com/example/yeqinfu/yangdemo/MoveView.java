package com.example.yeqinfu.yangdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
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

    private float circleRadius = 40;//子小圆的半径
    private float smallRadius = 20;//子小小圆的半径

    private List<Point> list;//子小圆的点集合
    private List<Point> dragList;//用于拖动的小小圆集合


    public void resetView(){
        viewNumber=10;
        radius=400;
        circleRadius=40;
        smallRadius=20;
        dragList.clear();
        for (int i=0;i<list.size();i++){
            dragList.add(i,getSmallPoint(list.get(i)));
        }
        isTouchSmallCicle=false;
        isInMove=false;
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
        bigPaint.setStyle(Paint.Style.FILL_AND_STROKE);  //设置画笔模式为填充
        bigPaint.setStrokeWidth(30f);         //设置画笔宽度为10px
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
        dragList = new ArrayList<>();


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
        if (isTouchSmallCicle){
            list.clear();
            for (int i = 0; i < viewNumber; i++) {
                Point point = getPointByIndex(i);
                list.add(point);
                canvas.drawCircle(point.x, point.y, circleRadius, mPaint);
                //小小圆
                Point s=dragList.get(i);
                canvas.drawCircle(s.x, s.y, smallRadius, redPaint);
            }
        }else{
            list.clear();
            dragList.clear();
            for (int i = 0; i < viewNumber; i++) {
                Point point = getPointByIndex(i);
                list.add(point);
                canvas.drawCircle(point.x, point.y, circleRadius, mPaint);
                //小小圆
                Point s = getSmallPoint(point);
                dragList.add(s);
                canvas.drawCircle(s.x, s.y, smallRadius, redPaint);
            }
        }

    }


    /**
     * 根据下标指定一个点位置
     *
     * @param index
     * @return
     */
    private Point getPointByIndex(int index) {
        Point p = new Point();
        double d = 1.00 * index / viewNumber * 2 * Math.PI;
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
            isTouchSmallCicle = isTouchSmallChildView(event.getX(), event.getY());

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isInMove) {
                double xx = (event.getX() - centerX) * (event.getX() - centerX);
                double yy = (event.getY() - centerY) * (event.getY() - centerY);
                radius = (float) Math.sqrt(xx + yy);
                invalidate();
            }
            if (isTouchSmallCicle){
                dragList.get(touchSmallCicleIndex).set((int)event.getX(),(int)event.getY());
                double xx = (event.getX() - list.get(touchSmallCicleIndex).x) * (event.getX() - list.get(touchSmallCicleIndex).x);
                double yy = (event.getY() - list.get(touchSmallCicleIndex).y) * (event.getY() - list.get(touchSmallCicleIndex).y);
                circleRadius = (float) Math.sqrt(xx + yy)-smallRadius-5;
                for (int i=0;i<list.size();i++){
                    if (i!=touchSmallCicleIndex){
                        Point p=getSmallPoint(list.get(i));
                        dragList.set(i,p);
                    }
                }
                invalidate();
            }
        }


        return true;
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
            if (dis < circleRadius) {
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
        for (int i = 0; i < dragList.size(); i++) {
            Point p = dragList.get(i);
            float dis = (float) Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
            if (dis < smallRadius) {
                touchSmallCicleIndex=i;
                return true;
            }
        }

        return false;
    }


    private Point getSmallPoint(Point s) {
        Point p = new Point();
        p.set((int) (s.x - circleRadius - smallRadius - 5), (int) (s.y - circleRadius - smallRadius - 5));
        return p;
    }


}
