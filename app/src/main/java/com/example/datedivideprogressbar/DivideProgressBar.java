package com.example.datedivideprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 等分的日期展示进度条
 */

public class DivideProgressBar extends View {

    private static final String TEMP="yyyy-MM-dd";
    private int textSize= sp2px(12);
    private int lineHeight= dp2px(5);
    private int textToLineDistance =dp2px(15);
    private int lineColor=getResources().getColor(R.color.line_part);
    private int progressColor=getResources().getColor(R.color.red);
    private Drawable drawable_p=getResources().getDrawable(R.mipmap.seekbar_p);
    private Drawable drawable_n=getResources().getDrawable(R.mipmap.seekbar_n);
    private Drawable drawable;
    private int drawableWidth,progress,day;//progress是进度的最小刻度位置,day为当前日期和开始日期相差得天数
    private Paint textPaint,linePaint;
    private List<ProgressBean> list=new ArrayList<>();
    private int[] array=new int[0];//[0,1,5,6]天数
    private RectF lineRect = new RectF(0, 0, 0, 0);
    private RectF progressRect = new RectF(0, 0, 0, 0);
    private Rect textRect=new Rect();
    private Rect timeRect=new Rect();
    public static class ProgressBean{

        public ProgressBean(String describe, String time) {
            this.describe = describe;
            this.time = time;
        }

        public String describe;
        public String time;//格式 yyyy-MM-dd
    }


    public DivideProgressBar(Context context) {
        this(context,null);
    }

    public DivideProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DivideProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        linePaint=new Paint();

        textPaint=new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        drawableWidth=drawable_p.getIntrinsicWidth();

        int padding=dp2px(10);
        setPadding(padding,padding,padding,padding);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode!=MeasureSpec.EXACTLY){
            textPaint.getTextBounds(TEMP, 0, 1,textRect);
            heightSize=getPaddingTop()+(textRect.height()*2)+(textToLineDistance*2)+lineHeight+getPaddingBottom();
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制进度条背景
        lineRect.left=getPaddingLeft()+(drawableWidth/2);
        lineRect.top=getHeight()/2-(lineHeight/2);
        lineRect.right=getWidth()-getPaddingRight()-(drawableWidth/2);
        lineRect.bottom=getHeight()/2+(lineHeight/2);
        linePaint.setColor(lineColor);
        canvas.drawRect(lineRect,linePaint);

        if (list.size()<2) return;
        //绘制进度最小刻度
        progressRect.left=lineRect.left;
        progressRect.top=lineRect.top;
        progressRect.right=(int) (lineRect.width()/(list.size()-1)*progress+lineRect.left);
        progressRect.bottom=lineRect.bottom;
        //多出刻度的距离
        if (day>array[progress]&&progress<array.length-1)
            progressRect.right+=lineRect.width()/(list.size()-1)
                    /(array[progress+1]-array[progress])*(day-array[progress]);

        linePaint.setColor(progressColor);
        canvas.drawRect(progressRect,linePaint);

        for (int i=0;i<list.size();i++){
            //绘制圆圈
            //position等分的位置
            int position= (int) (lineRect.width()/(list.size()-1)*i+lineRect.left);

            if (i<=progress){
                drawable=drawable_p;
            }else {
                drawable=drawable_n;
            }
            drawable.setBounds(position-(drawableWidth/2),getHeight()/2-(drawableWidth/2),
                    position+(drawableWidth/2),getHeight()/2+(drawableWidth/2));
            drawable.draw(canvas);

            //测量文字和时间的大小
            textPaint.getTextBounds(list.get(i).describe, 0, list.get(i).describe.length(),textRect);
            if (list.get(i).time==null) return;
            textPaint.getTextBounds(list.get(i).time, 0, list.get(i).time.length(),timeRect);
            //绘制文字
            if (i==0){
                canvas.drawText(list.get(i).describe,getPaddingLeft()
                        ,getHeight()/2-(lineHeight/2)-textToLineDistance,textPaint);
                canvas.drawText(list.get(i).time,getPaddingLeft(),getHeight()-getPaddingBottom(),textPaint);
            }else if (i==list.size()-1){
                canvas.drawText(list.get(i).describe,getWidth()-getPaddingRight()-textRect.width()
                        ,getHeight()/2-(lineHeight/2)-textToLineDistance,textPaint);
                canvas.drawText(list.get(i).time,getWidth()-getPaddingRight()-timeRect.width()
                        ,getHeight()-getPaddingBottom(),textPaint);
            }else {
                canvas.drawText(list.get(i).describe,position-(textRect.width()/2)
                        ,getHeight()/2-(lineHeight/2)-textToLineDistance,textPaint);
                canvas.drawText(list.get(i).time,position-(timeRect.width()/2)
                        ,getHeight()-getPaddingBottom(),textPaint);
            }

        }

    }

    public void setList(List<ProgressBean> list) {
        this.list = list;
        array=new int[list.size()];
        array[0]=0;
        for (int i=1;i<list.size();i++){
            array[i]=array[i-1]+TimeIntervalUtils.getTimeIntervalArray(list.get(i).time
                    ,list.get((i-1)).time,TEMP)[2];
        }
        invalidate();
    }

    /**
     *  设置当前日期，格式 yyyy-MM-dd
     * @param date 空则默认为当前日期
     */
    public void setDate(String date) {
        if (TextUtils.isEmpty(date))
            date=TimeIntervalUtils.getCurrentDateString(TEMP);
        if (list!=null&&list.size()>0)
            day=TimeIntervalUtils.getTimeIntervalArray(date,list.get(0).time,TEMP)[2];
        for (int i=0;i<array.length;i++){
            if (day>=array[i]){
                progress=i;
            }
        }
        invalidate();
    }

    public int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
