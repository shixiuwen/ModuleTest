package shixia.moduletest.module_calendar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ShiXiuwen on 2017/3/2.
 * <p>
 * Description:自定义日历控件
 * <p>
 * 对外提供的方法：
 * <p>
 * <p>
 * 1.setOnCalendarMonthChangeListener()    翻页结束之后回调，返回当前页面年月信息，eg : 2017-3
 * 2.setOnSomedayClickListener()           有具体日期被点击的时候回调，返回点击的具体日期，eg : 2017-3-9
 * 3.nextMonth()                           往下移动一个月
 * 4.preMonth()                            往上移动一个月
 * 5.showSpecialDates()                    设置有特殊数据的日期，在这些日期下面加上小红点
 * 6.setDetailDateViewVisible()            设置顶部具体日期显示是否可见，默认可见，如设置为不可见，请在控件初始化后调用该方法，其他地方调用不生效
 * 7.setWeekViewVisible()                  设置顶部星期信息是否可见，默认可见，如设置为不可见，请在控件初始化后调用该方法，其他地方调用不生效
 * 8.initCalendarDate()                    设置（初始化）日期显示的年月（默认为当前年月），比如可指定日历显示日期为 2019-8-8
 */

public class CalendarView extends LinearLayout {

    private Context mContext;
    private float oldX = 0;
    //用于计算本次滑动距离，判断向前翻页或者向后翻页
    private float moveDistance = 0;

    //默认开始的时候移动到中间，偏移控件宽度
    private int currentScrollXBeforeAction;

    private CalendarView parentView = this;

    private String todayDate;   //今天日期  eg：2017-03-08

    private int prePageYear;
    private int prePageMonth;        //初始化为[1-12]

    private int currentPageYear;
    private int currentPageMonth;   //初始化为[1-12]

    private int nextPageYear;
    private int nextPageMonth;       //初始化为[1-12]

    private int date = 1;           //每个月第一天

    private List<Integer> dateArray = new ArrayList<>();

    //手指开始按下的时间，用于计算滑动速度
    private long actionDownTime = 0;

    private int measureWidth;   //日历宽度
    private int measureHeight;  //日历高度
    //被选中的日期
    private GrandSonDayCalendarView selectedDateView;
    //当天日期
    private GrandSonDayCalendarView todayView;
    //被选中的日期时间
    private String strSelectedDate = "";

    //默认为该控件上手指按下就是点击事件，如果滑动置为false，表示为日历滑动事件，不响应点击
    private boolean isClickEvent = true;

    /*因为提供的方法中包含可设置日历头部是否可见，而我们的日历当不指定高度的时候，高度为宽度的816/1080,即
    8行*102，每一行高度为102，当有头部（如星期）设置为不可见时，高度不再是8*102,8这个值应该相对减少*/
    private int lineCount = 8;
    private boolean isWeekViewVisible = true;       //星期是否可见
    private boolean isDetailDateViewVisible = true; //顶部具体时间view是否可见

    private ScrollView scrollConflictView;  //有滑动冲突的View

    public CalendarView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        setClickable(true);
        setOrientation(HORIZONTAL);

        //计算当前月份，上一个月和下一个月
        initCurrentMonth();

        post(new Runnable() {
            @Override
            public void run() {
                Log.e("amos", "post" + getWidth());

                //上个月份，显示在上一页
                SonCalendar preSonCalendar = new SonCalendar(mContext, attrs);
                LayoutParams lp01 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp01.height = measureHeight;
                lp01.width = measureWidth;
                addView(preSonCalendar, lp01);
                preSonCalendar.refreshDate(prePageYear, prePageMonth, 2);

                //本月份，显示在当前页
                SonCalendar currSonCalendar = new SonCalendar(mContext, attrs);
                LayoutParams lp02 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp02.height = measureHeight;
                lp02.width = measureWidth;
                addView(currSonCalendar, lp02);
                currSonCalendar.refreshDate(currentPageYear, currentPageMonth, 2);

                //下个月份，显示在下一页
                SonCalendar nextSonCalendar = new SonCalendar(mContext, attrs);
                LayoutParams lp03 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp03.height = measureHeight;
                lp03.width = measureWidth;
                addView(nextSonCalendar, lp03);
                nextSonCalendar.refreshDate(nextPageYear, nextPageMonth, 2);

                //滚动到中间一屏，显示本月月份，使其可以前后滑动到上一个月份和下一个月份
                scrollTo(measureWidth, 0);
            }
        });
    }

    private void initCurrentMonth() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);

        currentPageYear = year;
        currentPageMonth = month + 1;
        todayDate = year + "-" + String.format(Locale.CHINA, "%02d", (month + 1)) + "-" + date;

        if (month == 0) {
            prePageMonth = 12;
            prePageYear = year - 1;
        } else {
            prePageYear = year;
            prePageMonth = month;
        }

        if (month == 11) {
            nextPageYear = year + 1;
            nextPageMonth = 1;
        } else {
            nextPageYear = year;
            nextPageMonth = month + 2;
        }
        Log.e("refreshDate", "currentPageYear:" + currentPageYear + "　" + "currentPageMonth" + currentPageMonth
                + " " + "prePageYear:" + prePageYear + " " + "prePageMonth:" + prePageMonth + " " +
                "nextPageYear:" + nextPageYear + " " + "nextPageMonth:" + nextPageMonth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                actionDownTime = System.currentTimeMillis();
                oldX = motionEvent.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(motionEvent);
                break;
            case MotionEvent.ACTION_UP:
                actionUp();
                break;
        }
        return super.onTouchEvent(motionEvent);
    }


    /**
     * 日历的一页，包含顶部显示，星期，当月日期展示（六行日的显示）
     */
    private class SonCalendar extends LinearLayout {

        private String sonCalendarDate; //当前页表示的 年份-月份
        private int dateCount = 0;  //要刷新的月份的天数

        public SonCalendar(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setOrientation(VERTICAL);

            Log.e("amos", "SonCalendar" + " " + CalendarView.this.getWidth());
            //添加顶部日期显示
            if (isDetailDateViewVisible) {
                SonTitleCalendarView sonTitleCalendarView = new SonTitleCalendarView(context, attrs);
                LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.width = measureWidth;
                layoutParams.height = measureHeight / lineCount;
//            sonTitleCalendarView.setBackgroundColor(Color.argb(255, 123, 123, 123));
                addView(sonTitleCalendarView, layoutParams);
            }

            //添加星期信息
            if (isWeekViewVisible) {
                SonWeekCalendarView sonWeekCalendarView = new SonWeekCalendarView(context, attrs);
                LayoutParams weekLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                weekLayoutParams.width = measureWidth;
                weekLayoutParams.height = measureHeight / lineCount;
                addView(sonWeekCalendarView, weekLayoutParams);
            }

            //添加日期信息
            for (int i = 0; i < 6; i++) {
                SonLineDayCalendarView sonLineDayCalendarView = new SonLineDayCalendarView(context, attrs);
                LayoutParams lineDayLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lineDayLayoutParams.width = measureWidth;
                lineDayLayoutParams.height = measureHeight / lineCount;
                addView(sonLineDayCalendarView, lineDayLayoutParams);
            }
        }

        /**
         * 刷新当前日期显示
         *
         * @param currentPageYear  当前页面显示年份
         * @param currentPageMonth 当前界面显示月份
         * @param type             0为重置上一页，1为重置下一页，2根据传入的年月刷新界面
         */
        public void refreshDate(int currentPageYear, int currentPageMonth, int type) {
            if (type == 0) {
                if (currentPageMonth == 1) {
                    prePageYear = currentPageYear - 1;
                    prePageMonth = 12;
                } else {
                    prePageYear = currentPageYear;
                    prePageMonth = currentPageMonth - 1;
                }
            } else if (type == 1) {
                if (currentPageMonth == 12) {
                    nextPageYear = currentPageYear + 1;
                    nextPageMonth = 1;
                } else {
                    nextPageYear = currentPageYear;
                    nextPageMonth = currentPageMonth + 1;
                }
            }

            String dateStr = "";

            if (type == 0) {  //重置上一页
                String strPrePageMonth = String.format(Locale.CHINA, "%02d", prePageMonth);  //拼接月份信息，如2月显示为02月
                sonCalendarDate = prePageYear + "-" + strPrePageMonth;
                dateStr = prePageYear + "-" + strPrePageMonth + "-" + "01";
            } else if (type == 1) {  //重置下一页
                String strNextPageMonth = String.format(Locale.CHINA, "%02d", nextPageMonth);  //拼接月份信息，如2月显示为02月
                sonCalendarDate = nextPageYear + "-" + strNextPageMonth;
                dateStr = nextPageYear + "-" + strNextPageMonth + "-" + "01";
            } else if (type == 2) {
                String strCurrentPageMonth = String.format(Locale.CHINA, "%02d", currentPageMonth);
                sonCalendarDate = currentPageYear + "-" + strCurrentPageMonth;
                dateStr = currentPageYear + "-" + strCurrentPageMonth + "-" + "01";
            }

            if (isDetailDateViewVisible) {
                ((SonTitleCalendarView) getChildAt(0)).setDate(sonCalendarDate);
            }

            //设置要刷新的页面Calendar
            Calendar calendar = Calendar.getInstance();
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            try {
                Date date = format.parse(dateStr);
                calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            calendar.roll(Calendar.DATE, -1);    //日期回滚到最后一天，获取当天日期即为当月天数
            dateCount = calendar.get(Calendar.DATE);

            for (int i = lineCount - 6; i < lineCount; i++) {
                if (i == lineCount - 6) {
                    ((SonLineDayCalendarView) this.getChildAt(i)).refreshDate(true, dayOfWeek, dateCount);
                } else {
                    ((SonLineDayCalendarView) this.getChildAt(i)).refreshDate(false, 7 * (i - 2) + (7 - dayOfWeek), dateCount);
                }
            }
        }

        private String getSonCalendarDate() {
            return sonCalendarDate;
        }
    }

    /**
     * 日历顶部的日期显示，两个按钮，点击左滑或者右滑
     */
    private class SonTitleCalendarView extends RelativeLayout {

        private TextView textDay;

        public SonTitleCalendarView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            textDay = new TextView(context);
            LayoutParams leftBtnLayoutParam = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50.0F / 800 * measureWidth);
            leftBtnLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT);
            addView(textDay, leftBtnLayoutParam);
        }

        /**
         * 设置头部日期显示
         *
         * @param sonCalendarDate 头部日期显示
         */
        public void setDate(String sonCalendarDate) {
            textDay.setText(sonCalendarDate);
        }
    }

    /**
     * 日历顶部的星期
     */
    private class SonWeekCalendarView extends LinearLayout {

        public SonWeekCalendarView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setOrientation(HORIZONTAL);
            String week = "一";
            for (int i = 0; i < 7; i++) {
                TextView textView = new TextView(context);
                LayoutParams weekLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                weekLayoutParams.weight = 1;
                switch (i) {
                    case 0:
                        week = "日";
                        break;
                    case 1:
                        week = "一";
                        break;
                    case 2:
                        week = "二";
                        break;
                    case 3:
                        week = "三";
                        break;
                    case 4:
                        week = "四";
                        break;
                    case 5:
                        week = "五";
                        break;
                    case 6:
                        week = "六";
                        break;
                }
                textView.setText(week);
                textView.setTextColor(Color.argb(255, 51, 51, 51));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 44.0F / 1080 * measureWidth);
                textView.setGravity(Gravity.CENTER);
                addView(textView, weekLayoutParams);
            }
        }
    }

    /**
     * 日历内对应的日的一行，最多六行
     */
    private class SonLineDayCalendarView extends LinearLayout {

        public SonLineDayCalendarView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setOrientation(HORIZONTAL);
            for (int j = 0; j < 7; j++) {
                GrandSonDayCalendarView grandSonDayCalendarView = new GrandSonDayCalendarView(context, attrs);
                LayoutParams weekLayoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                weekLayoutParams.weight = 1;
                addView(grandSonDayCalendarView, weekLayoutParams);
            }
        }

        /**
         * 刷新每一行的日期显示
         *
         * @param isFirstLine 是否是第一行，第一行可能有几个数不绘制
         * @param i           改行起始数字
         */
        public void refreshDate(boolean isFirstLine, int i, int dateCount) {
            if (isFirstLine) {
                date = 1;   //每月第一行重置date==1；
                for (int k = 0; k < 7; k++) {
                    if (k >= i - 1) {
                        ((GrandSonDayCalendarView) this.getChildAt(k)).refreshDate(date++);
                    } else {
                        ((GrandSonDayCalendarView) this.getChildAt(k)).refreshDate(-1);
                    }
                }
            } else {
                for (int k = 0; k < 7; k++) {
                    if (date <= dateCount) {
                        ((GrandSonDayCalendarView) this.getChildAt(k)).refreshDate(date++);
                    } else {
                        ((GrandSonDayCalendarView) this.getChildAt(k)).refreshDate(-1);
                    }
                }
            }
        }
    }

    /**
     * 日历中对应的每一天，如果想定义日期的样式，如在底部加一个点或者在外部加一个圈，请自定义该部分
     */
    private class GrandSonDayCalendarView extends View {
        private String textContent = "-1";
        private Paint clickPaint;
        private Paint todayPaint;
        private Paint dotPaint;
        private TextPaint textPaint;

        private static final int STATUS_INIT = 0;           //未点击任何日期时的初始化状态，此时当天日期显示为蓝色
        private static final int STATUS_SELECTED = 1;       //有日期被选中状态
        private static final int STATUS_UNSELECTED = 2;     //日期取消选中状态
        private int selectStatus = STATUS_INIT;             //该日期是否被选中，1.该日期为当天时选中；2.该日期被点击后选中

        public GrandSonDayCalendarView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);

            setClickable(true);
            textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            textPaint.setTextSize(36.0F / 800 * measureWidth);
            textPaint.setColor(Color.argb(255, 153, 153, 153));

            clickPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            clickPaint.setColor(Color.argb(255, 11, 180, 251));

            todayPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            todayPaint.setColor(Color.argb(255, 127, 233, 227));

            dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            dotPaint.setColor(Color.argb(255, 255, 0, 0));
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    actionDownTime = System.currentTimeMillis();
                    oldX = event.getRawX();
                    isClickEvent = true;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //如果有move事件，说明当前为滑动日历事件，不响应具体日期点击事件,响应滑动事件
                    actionMove(event);
                    break;
                case MotionEvent.ACTION_UP:
                    if (isClickEvent && !textContent.equals("-1") && !textContent.equals(" ")) { //手指抬起的时候判断有没有滑动事件拦截，如果没有，响应点击事件
                        // TODO: 2017/3/3  以下为点击的时候的具体逻辑
                        strSelectedDate = currentPageYear + "-" + currentPageMonth;
                        if (selectedDateView != null) {
                            selectedDateView.setSelectStatus(STATUS_UNSELECTED);
                        }
                        selectedDateView = this;    //重置当前选中日期
                        this.setSelectStatus(STATUS_SELECTED);
                        if (todayView != null && selectedDateView != todayView) {
                            todayView.setSelectStatus(STATUS_UNSELECTED);
                        }
                        if (todayView != null) {
                            todayView.invalidate();
                        }
                        if (somedayClickListener != null) {
                            somedayClickListener.onSomedayClick(currentPageYear + "-" + currentPageMonth + "-" + textContent);
                        }
//                        Toast.makeText(mContext, "date:" + currentPageYear + "-" + currentPageMonth + "-" + textContent, Toast.LENGTH_SHORT).show();
                    } else { //处理移动事件
                        //手指抬起的时候判断向前翻页、向后翻页、停留在本页
                        actionUp();
                    }
                    break;
            }
            return super.dispatchTouchEvent(event);
        }

        public void setSelectStatus(int selectStatus) {
            this.selectStatus = selectStatus;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int measureWidth = getMeasuredWidth();
            int measureHeight = getMeasuredHeight();

            //今天
            String grandSonCalendarDate = ((SonCalendar) getParent().getParent()).getSonCalendarDate() + "-" + textContent;
            if (grandSonCalendarDate.equals(todayDate)) {
                todayView = this;   //初始化todayView;
                if (selectStatus == STATUS_SELECTED || selectStatus == STATUS_INIT) {    //蓝色
                    todayPaint.setColor(Color.argb(255, 11, 180, 251));
                } else {    //将外框置灰
                    todayPaint.setColor(Color.argb(255, 218, 218, 218));
                }
                canvas.drawCircle(measureWidth / 2, measureHeight / 2, 34.0F / 1080 * measureWidth * 7, todayPaint);
            }

            //点击后被选中，外部画圈，字体变为白色，否则字体还原为灰色
            if (selectStatus == STATUS_SELECTED && strSelectedDate.equals(currentPageYear + "-" + currentPageMonth)) {
                canvas.drawCircle(measureWidth / 2, measureHeight / 2, 34.0F / 1080 * measureWidth * 7, clickPaint);
            }

            float textWidth = textPaint.measureText(textContent);
            Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
            int baseline = (measureHeight - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            if (!textContent.equals("-1")) {    //有日期显示的
                if ((grandSonCalendarDate.equals(todayDate) && (selectStatus == STATUS_INIT || selectStatus == STATUS_SELECTED)) ||   //对未点击之前，当天日期显示为白色字体的特殊处理
                        selectStatus == STATUS_SELECTED && strSelectedDate.equals(currentPageYear + "-" + currentPageMonth)) {
                    textPaint.setColor(Color.argb(255, 255, 255, 255));
                } else {    //未被选中的字体或者当天日期字体之外的字设置为灰色
                    textPaint.setColor(Color.argb(255, 153, 153, 153));
                }
                canvas.drawText(textContent, measureWidth / 2 - textWidth / 2, baseline, textPaint);
                if (dateArray.contains(Integer.parseInt(textContent))) {
                    canvas.drawCircle(measureWidth / 2, measureHeight - measureHeight / 12, measureHeight / 16, dotPaint);
                }
            } else {                            //无日期显示的
                canvas.drawText(" ", measureWidth / 2 - textWidth / 2, baseline, textPaint);
            }
        }

        public void refreshDate(int i) {
            textContent = i + "";
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureWidth = getSizeByCalculate(widthMeasureSpec, true);
        measureHeight = getSizeByCalculate(heightMeasureSpec, false);
        if (measureHeight == 0) {
            measureHeight = (int) (102.0F * lineCount / 1080 * measureWidth);
        }
        currentScrollXBeforeAction = measureWidth;
        setMeasuredDimension(measureWidth, measureHeight);

    }

    private int getSizeByCalculate(int mLong, boolean isWidth) {
        int result;  //日历的默认大小为800px
        int size = MeasureSpec.getSize(mLong);
        int mode = MeasureSpec.getMode(mLong);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else { //用于wrap_content不考虑un_exactly情况
            if (isWidth) {
                result = getScreenWidth();  //默认宽度为屏幕宽度
            } else { //如果为长度且未精确指定，设置为宽度的800/1080
                result = 0;
            }
        }
        return result;
    }

    /**
     * 获取屏幕宽度，用于设置默认的日历大小
     *
     * @return screen width
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void scrollAnimation(View view, String s, int from, final int to) {
        ObjectAnimator valueAnimator = ObjectAnimator.ofInt(view, s, from, to);
        valueAnimator.setDuration(200);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int currentValue = (int) valueAnimator.getAnimatedValue();
                parentView.scrollTo(currentValue, 0);
                if (currentValue == to) { //动画执行结束
                    int newScrollX = parentView.getScrollX();
                    int oldScrollX = currentScrollXBeforeAction;
                    if (newScrollX > oldScrollX) {  //说明滑动到了下一页
                        //执行动画结束之后，如果滑动到了下一页，将第一页移除，放到最后一页，目的是让当前所在页面放在最中间
                        View removeView = parentView.getChildAt(0); //取出要移除的第一页
                        parentView.removeView(removeView);  //移除取出的第一页
                        parentView.addView(removeView); //将取出的第一页放到最后一页
                        parentView.setScrollX(measureWidth);    //移除第一页后，界面会重置到scrollX = 0,执行该操作让界面回到偏移控件宽度
                        //当前页变为上一页，下一页变为当前页，第一页重置为下一页
                        prePageYear = currentPageYear;
                        prePageMonth = currentPageMonth;
                        currentPageYear = nextPageYear;
                        currentPageMonth = nextPageMonth;
                        ((SonCalendar) removeView).refreshDate(currentPageYear, currentPageMonth, 1);
                        // TODO: 2017/3/6 动画执行结束，进行当月网络信息请求
                        if (calendarMonthChangeListener != null) {  //回调方法
                            calendarMonthChangeListener.onCalendarMonthChange(currentPageYear + "-" + currentPageMonth);
                        }
                    } else if (newScrollX < oldScrollX) { //说明滑动到了上一页
                        //执行动画结束之后，如果滑动到了上一页，将最后一页移除，添加到第一页隐藏，目的是让当前所在页面放在最中间
                        View removeView = parentView.getChildAt(2);
                        parentView.removeView(removeView);
                        parentView.addView(removeView, 0);
                        parentView.setScrollX(measureWidth);
                        //当前页变为下一页，上一页变为当前页，最后一页变为上一页
                        nextPageYear = currentPageYear;
                        nextPageMonth = currentPageMonth;
                        currentPageYear = prePageYear;
                        currentPageMonth = prePageMonth;
                        ((SonCalendar) removeView).refreshDate(currentPageYear, currentPageMonth, 0);
                        // TODO: 2017/3/6 动画执行结束，进行当月网络信息请求
                        if (calendarMonthChangeListener != null) {  //回调方法
                            calendarMonthChangeListener.onCalendarMonthChange(currentPageYear + "-" + currentPageMonth);
                        }
                    }
                    currentScrollXBeforeAction = parentView.getScrollX();   //重置下一次动作之前的滑动角度

                    //在请求的数据中，有一部分有点的数组，表示该月当前天数数组中有账单
                    // TODO: 2017/3/9  模拟网络请求之后调用 showSpecialDates() 方法显示特殊日期,请删除
                    List<Integer> array = new ArrayList<>();
                    array.add(9);
                    array.add(10);
                    array.add(24);
                    array.add(19);
                    parentView.showSpecialDates(array);
                }
            }
        });
    }

    /**
     * 处理手指滑动事件
     *
     * @param motionEvent 手指滑动事件
     */
    private void actionMove(MotionEvent motionEvent) {
        //设置外部可滚动的父布局不拦截滑动事件
        if (scrollConflictView != null) {
            scrollConflictView.requestDisallowInterceptTouchEvent(true);
        }
        float newX = motionEvent.getRawX();
        float move = oldX - newX;
        this.scrollBy((int) move, 0);
        oldX = motionEvent.getRawX();
        moveDistance += move;
        if (Math.abs(moveDistance) > 10) {
            isClickEvent = false;
        }
    }

    /**
     * 处理手指抬起事件
     */
    private void actionUp() {
        long actionUpTime = System.currentTimeMillis();
        double moveSpeed = Math.abs(moveDistance * 1000 / (actionUpTime - actionDownTime));
        Log.e("moveSpeed", moveSpeed + "");
        //手指抬起的时候判断向前翻页、向后翻页、停留在本页
        if (moveDistance > measureWidth / 2 || (moveSpeed > 1000 && moveDistance > 0)) {   //向下一页滑动
            scrollAnimation(parentView, "amos", parentView.getScrollX(), currentScrollXBeforeAction + measureWidth);
        } else if (moveDistance < -measureWidth / 2 || (moveSpeed > 1000 && moveDistance < 0)) { //向上一页滑动
            scrollAnimation(parentView, "amos", parentView.getScrollX(), currentScrollXBeforeAction - measureWidth);
        } else if ((moveDistance >= 0 && moveDistance <= measureWidth / 2) || (moveDistance < 0 && moveDistance > -measureWidth / 2)) {  //停留在本页
            scrollAnimation(parentView, "amos", parentView.getScrollX(), currentScrollXBeforeAction);
        }
        moveDistance = 0;
    }

    /**
     * -------------------------------- 翻页切换月份的回调方法 --------------------------------
     *
     * @param calendarMonthChangeListener 回调监听
     */
    public void setOnCalendarMonthChangeListener(OnCalendarMonthChangeListener calendarMonthChangeListener) {
        this.calendarMonthChangeListener = calendarMonthChangeListener;
    }

    private OnCalendarMonthChangeListener calendarMonthChangeListener;

    interface OnCalendarMonthChangeListener {
        /**
         * @param currentPageDate 当前所在年月 eg:2017-03
         */
        void onCalendarMonthChange(String currentPageDate);
    }

    /**
     * -------------------------------- 具体某一天被点击的回调方法 --------------------------------
     *
     * @param somedayClickListener 具体某一天点击监听
     */
    public void setOnSomedayClickListener(OnSomedayClickListener somedayClickListener) {
        this.somedayClickListener = somedayClickListener;
    }

    private OnSomedayClickListener somedayClickListener;

    interface OnSomedayClickListener {
        /**
         * @param date 点击的具体日期 eg：2017-03-09
         */
        void onSomedayClick(String date);
    }

    /**
     * 显示下一个月，向下一个月翻页
     */
    public void nextMonth() {
        scrollAnimation(parentView, "amos", parentView.getScrollX(), currentScrollXBeforeAction + measureWidth);
    }

    /**
     * 显示上一个月，向上一个月翻页
     */
    public void preMonth() {
        scrollAnimation(parentView, "amos", parentView.getScrollX(), currentScrollXBeforeAction - measureWidth);
    }

    /**
     * 在有数据的日期之下显示小红点，通常在翻页结束之后调用
     *
     * @param dateArray 包含要显示小红点的日期的数组
     */
    public void showSpecialDates(List<Integer> dateArray) {
        this.dateArray = dateArray;
        String currentMonth = String.format(Locale.CHINA, "%02d", currentPageMonth);
        for (int i = 0; i < 3; i++) {
            SonCalendar sonCalendar = (SonCalendar) parentView.getChildAt(i);
            Log.e("showBillDates", currentPageYear + "-" + currentMonth + "-" + "01" + " " + "sonCalendarDate:" + sonCalendar.getSonCalendarDate());
            if (sonCalendar.getSonCalendarDate().equals(currentPageYear + "-" + currentMonth)) {
                //刷新选中的月份圆点
                sonCalendar.refreshDate(currentPageYear, currentPageMonth, 2);
            }
        }
    }

    /**
     * 动态设置日历头部具体日期是否可见，提供了翻页时的回调，可以不滑动
     */
    public void setDetailDateViewVisible(boolean visible) {
        if (!visible) {
            lineCount--;
            isDetailDateViewVisible = false;
        }
    }

    /**
     * 动态设置日历头部星期是否可见，因为根据具体需求，星期为不变内容是可以不滑动的
     *
     * @param visible 是否可见
     */
    public void setWeekViewVisible(boolean visible) {
        if (!visible) {
            lineCount--;
            isWeekViewVisible = false;
        }
    }

    /**
     * 设置日历初始化时间，日历显示的时候，默认显示当前页面为设置的年月
     *
     * @param year  默认显示的年份
     * @param month 默认显示的月份
     */
    public void initCalendarDate(final int year, final int month) {
        parentView.post(new Runnable() {
            @Override
            public void run() {
                currentPageYear = year;
                currentPageMonth = month;
                if (currentPageMonth == 1) {
                    prePageYear = currentPageYear - 1;
                    prePageMonth = 12;
                } else {
                    prePageYear = currentPageYear;
                    prePageMonth = currentPageMonth - 1;
                }
                if (currentPageMonth == 12) {
                    nextPageYear = currentPageYear + 1;
                    nextPageMonth = 1;
                } else {
                    nextPageYear = currentPageYear;
                    nextPageMonth = currentPageMonth + 1;
                }
                ((SonCalendar) parentView.getChildAt(0)).refreshDate(prePageYear, prePageMonth, 2);
                ((SonCalendar) parentView.getChildAt(1)).refreshDate(currentPageYear, currentPageMonth, 2);
                ((SonCalendar) parentView.getChildAt(2)).refreshDate(nextPageYear, nextPageMonth, 2);
            }
        });
    }

    public void setScrollConflictView(ScrollView view) {
        this.scrollConflictView = view;
    }
}
