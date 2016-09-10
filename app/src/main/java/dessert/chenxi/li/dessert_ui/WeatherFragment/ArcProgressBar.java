package dessert.chenxi.li.dessert_ui.WeatherFragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import dessert.chenxi.li.dessert_ui.R;

/**
 * Created by 李天烨 on 2016/8/16.
 */
public class ArcProgressBar extends View {
    private int circleRectWidth;
    //圆弧边框宽度
    private float arcStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            30, getContext().getResources().getDisplayMetrics());
    //图标名称字符大小
    private float chartNameTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            30, getContext().getResources().getDisplayMetrics());
    //圆形中心当前进度数字字符大小
    private float unitTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            40, getContext().getResources().getDisplayMetrics());
    //底层圆弧画笔
    private Paint backArcPaint;
    //前层圆弧画笔
    private Paint fontArcPaint;
    //绘制图标名称的画笔
    private Paint chartNamePaint;
    //绘制数字的画笔
    private Paint currentProgressNumberPaint;
    //圆弧半径
    private int circleRadius;
    //中心点X轴坐标
    private int centerX;
    //中心店Y轴坐标
    private int centerY;
    //半径占控件宽度的比例
    private final float RADIUS_RATIO = 0.3f;
    //圆弧开始绘制的角度
    private final int START_ANGLE = 135;
    //底层圆弧扫过的角度
    private final int INNER_CIRCLE_SWEEP_ANGLE = 270;
    //底层圆弧的颜色
    private final int INNER_CIRCLE_BORDER_COLOR = Color.parseColor("#aaf0f1f2");
    //上层圆弧的颜色
    private final int FONT_CIRCLE_BORDER_COLOR = Color.parseColor("#eef0f1f2");
    private final int BG_COLOR = Color.parseColor("#00bfff");
    //默认图标名称
    private String chartName = "无标题";
    //默认当前进度
    private float currentProgress = 0;
    //当前进度单位
    private String progressUnitString = "℃";
    //进度单位所占的宽度
    private float unitTextWidth;
    //底部文案的y轴坐标
    private float yPosBottomAlign;
    //最大进度數
    private float maxProgress = 50;
    private RectF rectF;

    public ArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar);
        float hours = typedArray.getFloat(R.styleable.ArcProgressBar_current_progress, 0);
        String chartTitle = typedArray.getString(R.styleable.ArcProgressBar_chart_title);
        float maxProgress = typedArray.getFloat(R.styleable.ArcProgressBar_max_progress, 0);
        String progressUnit = typedArray.getString(R.styleable.ArcProgressBar_progress_unit);

        if (hours > 0) {
            this.currentProgress = hours;
        }
        if (maxProgress > 0) {
            this.maxProgress = maxProgress;
        }

        if (!TextUtils.isEmpty(chartTitle)) {
            this.chartName = chartTitle;
        }
        if (!TextUtils.isEmpty(progressUnit)) {
            this.progressUnitString = progressUnit;
        }
        typedArray.recycle();

        init();
    }

    private void init() {
        backArcPaint = new Paint();
        backArcPaint.setAntiAlias(true);
        backArcPaint.setColor(INNER_CIRCLE_BORDER_COLOR);
        backArcPaint.setStrokeWidth(arcStrokeWidth);
        backArcPaint.setStyle(Paint.Style.STROKE);
        backArcPaint.setStrokeCap(Paint.Cap.ROUND);

        fontArcPaint = new Paint();
        fontArcPaint.setAntiAlias(true);
        fontArcPaint.setColor(FONT_CIRCLE_BORDER_COLOR);
        fontArcPaint.setStrokeWidth(arcStrokeWidth);
        fontArcPaint.setStyle(Paint.Style.STROKE);
        fontArcPaint.setStrokeCap(Paint.Cap.ROUND);

        chartNamePaint = new Paint();
        chartNamePaint.setStyle(Paint.Style.FILL);
        chartNamePaint.setAntiAlias(true);
        chartNamePaint.setTextSize(chartNameTextSize);
        chartNamePaint.setColor(FONT_CIRCLE_BORDER_COLOR);

        unitTextWidth = chartNamePaint.measureText(progressUnitString);

        currentProgressNumberPaint = new Paint();
        currentProgressNumberPaint.setStyle(Paint.Style.FILL);
        currentProgressNumberPaint.setAntiAlias(true);
        currentProgressNumberPaint.setTextSize(unitTextSize);
        currentProgressNumberPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.AT_MOST) {
            float defaultSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    200, getContext().getResources().getDisplayMetrics());
            widthSpecSize = (int) defaultSize;
            heightSpecSize = (int) defaultSize;
        }
        setMeasuredDimension(Math.min(widthSpecSize, heightSpecSize), Math.min(widthSpecSize, heightSpecSize));

        circleRectWidth = widthSpecSize;
        circleRadius = (int) (circleRectWidth * RADIUS_RATIO);
        centerX = circleRectWidth / 2;
        centerY = circleRectWidth / 2;
        float rad = (float) (45 * Math.PI / 180);
        yPosBottomAlign = (float) (circleRadius * Math.sin(rad) + centerY);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawChart(canvas, currentProgress);
    }

    private void drawChart(Canvas canvas, float loopIndex) {
        canvas.drawColor(BG_COLOR);
        //1.绘制背景圆弧
        if (rectF == null) {
            rectF = new RectF(centerX - circleRadius,//left
                    centerY - circleRadius,//top
                    centerX + circleRadius,//right
                    centerY + circleRadius);//bottom
        }

        canvas.drawArc(rectF, START_ANGLE, INNER_CIRCLE_SWEEP_ANGLE, false, backArcPaint);

        //2.绘制进度圆弧
        if (maxProgress > 0) {
            canvas.drawArc(rectF, START_ANGLE, loopIndex / maxProgress * 270, false, fontArcPaint);
        }

        //3.绘制底部文案
        float chartNameWidth = chartNamePaint.measureText(chartName);
        Paint.FontMetrics fontMetrics = chartNamePaint.getFontMetrics();
        float chartNameHeight = fontMetrics.descent - fontMetrics.ascent;
        canvas.drawText(chartName, centerX - chartNameWidth / 2, (float) (yPosBottomAlign + chartNameHeight * 1.5),
                        chartNamePaint);

        //4.绘制中间的当前进度
        float hourNumberWidth = currentProgressNumberPaint.measureText(String.valueOf(loopIndex));
        float hourNumberHeight = currentProgressNumberPaint.getFontMetrics().bottom -
                                 currentProgressNumberPaint.getFontMetrics().top;
        //4.1绘制当前进度数字
        canvas.drawText(String.valueOf(loopIndex), centerX - hourNumberWidth / 2, centerY + chartNameHeight / 4,
                        currentProgressNumberPaint);
        //4.1绘制进度单位
        canvas.drawText(progressUnitString, centerX - unitTextWidth / 2,
                        centerY + chartNameHeight / 4 + hourNumberHeight / 2, chartNamePaint);
    }

    /**
     * 设置当前进度
     *
     * @param hour
     */
    public ArcProgressBar setCurrentProgress(float hour) {
        if (hour < 0) {
            currentProgress = 0f;
        } else if (hour > maxProgress) {
            currentProgress = maxProgress;
        } else {
            currentProgress = hour;
        }
        return this;
    }

    /**
     * 设置图标名称(底部)
     *
     * @param chartName
     */
    public ArcProgressBar setProgressUnit(String chartName) {
        if (TextUtils.isEmpty(chartName))
            return this;

        this.chartName = chartName;
        return this;
    }

    /**
     * 设置最大进度
     */
    public ArcProgressBar setMaxProgress(float maxHour) {
        if (maxHour <= 0) {
            return this;
        } else if (maxHour < currentProgress) {
            this.maxProgress = currentProgress;
        } else {
            this.maxProgress = maxHour;
        }
        return this;
    }

    /**
     * 刷新界面
     * PS:参数设置完成后，务必调用此方法刷新页面
     */
    public void refresh() {
        invalidate();
    }
}
