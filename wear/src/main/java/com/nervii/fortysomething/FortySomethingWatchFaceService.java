package com.nervii.fortysomething;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.wearable.DataMap;

public class FortySomethingWatchFaceService extends WeatherWatchFaceService {
    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends WeatherWatchFaceEngine {
        protected float mDebugInfoYOffset;
        Paint mTextPaintHour, mTextPaintMinute, mTextPaintSecond,
                mTextPaintColon, mTextPaintDate, mTextPaintTemperature,
                mTextPaintTemperatureScale;
        float mXOffset;

        private Engine() {
            super("FortySomething");
            UPDATE_RATE_MS = 1000;
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            mBackgroundColor = mBackgroundDefaultColor = mResources.getColor(R.color.fortysomething_bg_color);
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(mBackgroundDefaultColor);
            Resources resources = FortySomethingWatchFaceService.this.getResources();

            Typeface timeFont = Typeface.createFromAsset(mAsserts, mResources.getString(R.string.fortysomething_time_font));
            Typeface dateFont = Typeface.createFromAsset(mAsserts, mResources.getString(R.string.fortysomething_date_font));
            Typeface tempFont = Typeface.createFromAsset(mAsserts, mResources.getString(R.string.fortysomething_temperature_font));

            mTextPaintHour = new Paint();
            mTextPaintHour = createTextPaint(resources.getColor(R.color.digital_text), timeFont);
            mTextPaintMinute = new Paint();
            mTextPaintMinute = createTextPaint(resources.getColor(R.color.digital_text_grey), timeFont);
            mTextPaintSecond = new Paint();
            mTextPaintSecond = createTextPaint(resources.getColor(R.color.digital_text_dark_grey), timeFont);
            mTextPaintColon = new Paint();
            mTextPaintColon = createTextPaint(resources.getColor(R.color.digital_text_grey), timeFont);
            mTextPaintDate = new Paint();
            mTextPaintDate = createTextPaint(resources.getColor(R.color.digital_text_grey), dateFont);
            mTextPaintTemperature = new Paint();
            mTextPaintTemperature = createTextPaint(resources.getColor(R.color.digital_text_grey), tempFont);
            mTextPaintTemperatureScale = new Paint();
            mTextPaintTemperatureScale = createTextPaint(resources.getColor(R.color.digital_text_grey), tempFont);

        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            Resources resources = FortySomethingWatchFaceService.this.getResources();
            boolean isRound = insets.isRound();
            mXOffset = resources.getDimension(isRound
                    ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            float textSize = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);
            float textSizeMedium = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round_medium : R.dimen.digital_text_size_medium);
            float textSizeSmall = resources.getDimension(isRound
                    ? R.dimen.digital_text_size_round_small : R.dimen.digital_text_size_small);

            mTextPaintHour.setTextSize(textSize);
            mTextPaintMinute.setTextSize(textSize);
            mTextPaintSecond.setTextSize(textSizeMedium);
            mTextPaintDate.setTextSize(textSizeSmall);
            mTextPaintColon.setTextSize(textSize);
            mTextPaintTemperature.setTextSize(textSizeMedium);
            mTextPaintTemperatureScale.setTextSize(textSizeSmall);

            mDebugInfoYOffset = 5 + mDebugInfoPaint.getTextSize() + (mDebugInfoPaint.descent() + mDebugInfoPaint.ascent()) / 2;
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            log("onAmbientModeChanged: " + inAmbientMode);

            if (mLowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                mTextPaintHour.setAntiAlias(!inAmbientMode);
                mTextPaintMinute.setAntiAlias(!inAmbientMode);
                mTextPaintSecond.setAntiAlias(!inAmbientMode);
            }

            if (inAmbientMode) {
                mBackgroundPaint.setColor(mBackgroundDefaultColor);
            } else {
                mBackgroundPaint.setColor(mBackgroundColor);
            }

            invalidate();

            // Whether the timer should be running depends on whether we're in ambient mode (as well
            // as whether we're visible), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        protected void fetchConfig(DataMap config) {
            super.fetchConfig(config);
            if (config.containsKey(Consts.KEY_CONFIG_THEME)) {
                mBackgroundColor = mResources.getColor(mResources.getIdentifier("fortysomething_theme_" + mTheme + "_bg", "color", Consts.PACKAGE_NAME));
                if (!isInAmbientMode()) {
                    mBackgroundPaint.setColor(mBackgroundColor);
                }
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            //log("Draw");
            mTime.setToNow();

            boolean hasPeekCard = getPeekCardPosition().top != 0;
            int width = bounds.width();
            int height = bounds.height();

            canvas.drawRect(0, 0, width, height, mBackgroundPaint);

            //For Test
//            hourString = "11";
//            minString = "34";
//            mTemperature = 50;
//            mWeatherCondition = "clear";
//            mWeatherInfoReceivedTime = System.currentTimeMillis();
//            mSunriseTime.set(mWeatherInfoReceivedTime-10000);
//            mSunsetTime.set(mWeatherInfoReceivedTime+10000);

            String hour = String.format("%02d", mTime.hour);
            String minute = String.format("%02d", mTime.minute);
            String second = String.format("%02d", mTime.second);
            String colon = ":";

            String day = ConverterUtil.getDayName(mTime.weekDay);
            String month = ConverterUtil.convertToMonth(mTime.month);
            String date = String.valueOf(mTime.monthDay);
            String dateString = day + ", " + month + " " + date;

            float hourWidth = mTextPaintHour.measureText(hour);
            float minuteWidth = mTextPaintHour.measureText(minute);
            float colonWidth = mTextPaintHour.measureText(colon);
            int center_horizontal = (canvas.getWidth() / 2);
            int xPos = Math.round((canvas.getWidth() - (hourWidth + colonWidth + minuteWidth)) / 2);
            Rect r = new Rect();
            mTextPaintHour.getTextBounds(hour, 0, hour.length(), r);
            int yPos = (int) ((canvas.getHeight() / 2) - ((mTextPaintHour.descent() + mTextPaintHour.ascent()) / 2));
            yPos -= ((r.height())) / 2;
            yPos += 20;
            mTextPaintHour.setTextAlign(Paint.Align.LEFT);
            mTextPaintMinute.setTextAlign(Paint.Align.LEFT);
            mTextPaintSecond.setTextAlign(Paint.Align.LEFT);
            mTextPaintDate.setTextAlign(Paint.Align.CENTER);

            mTextPaintTemperature.setTextAlign(Paint.Align.LEFT);
            mTextPaintTemperatureScale.setTextAlign(Paint.Align.LEFT);


            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            canvas.drawText(hour, xPos, yPos, mTextPaintHour);
            canvas.drawText(colon, xPos + hourWidth, yPos-10, mTextPaintColon);
            canvas.drawText(minute, xPos + hourWidth + colonWidth, yPos, mTextPaintMinute);
            canvas.drawText(dateString, center_horizontal, (canvas.getHeight() / 2) - 60, mTextPaintDate);

            long timeSpan = System.currentTimeMillis() - mWeatherInfoReceivedTime;
            if (!this.isInAmbientMode()) {
                canvas.drawText(second, xPos + hourWidth + colonWidth + minuteWidth, (canvas.getHeight() / 2), mTextPaintSecond);

                // temperature
                //mTemperature = 32;
                //mWeatherCondition = "Rainy";
                boolean showTemperature =(timeSpan <= WEATHER_INFO_TIME_OUT);
                if (mTemperature != Integer.MAX_VALUE && showTemperature) {
                    /*
                    if ((mWeatherCondition.equals("cloudy") || mWeatherCondition.equals("clear")) && (Time.compare(mTime, mSunriseTime) < 0 || Time.compare(mTime, mSunsetTime) > 0)) {
                        //cloudy and clear have night picture
                        stringBuilder.append("night");
                    }
                    if (this.isInAmbientMode()) {
                        stringBuilder.append("_gray");
                    }
                    */
                    if (!(isRound && hasPeekCard)) {
                        String temperatureString = mWeatherCondition + ", " + String.valueOf(mTemperature);
                        String temperatureScaleString = mTemperatureScale == ConverterUtil.FAHRENHEIT ? ConverterUtil.FAHRENHEIT_STRING : ConverterUtil.CELSIUS_STRING;

                        // Align temperature scale to top of temperature
                        Rect r2 = new Rect();
                        mTextPaintTemperature.getTextBounds(temperatureString, 0, temperatureString.length(), r2);
                        Rect r3 = new Rect();
                        mTextPaintTemperatureScale.getTextBounds(temperatureScaleString, 0, temperatureScaleString.length(), r3);
                        int yPosDelta = r2.height() - r3.height();

                        float tempWidth = mTextPaintTemperature.measureText(temperatureString);
                        float scaleWidth = mTextPaintTemperatureScale.measureText(temperatureScaleString);

                        int tempPos = Math.round((canvas.getWidth() - (tempWidth + scaleWidth)) / 2);
                        canvas.drawText(temperatureString, tempPos, yPos + 45, mTextPaintTemperature);
                        canvas.drawText(temperatureScaleString, tempPos + tempWidth, yPos + 45 - yPosDelta, mTextPaintTemperatureScale);
                    }
                }
            }
            /*
            if (BuildConfig.DEBUG) {
                String timeString;
                if (mWeatherInfoReceivedTime == 0) {
                    timeString = "No data received";
                } else if (timeSpan > DateUtils.HOUR_IN_MILLIS) {
                    timeString = "Get: " + String.valueOf(timeSpan / DateUtils.HOUR_IN_MILLIS) + " hours ago";
                } else if (timeSpan > DateUtils.MINUTE_IN_MILLIS) {
                    timeString = "Get: " + String.valueOf(timeSpan / DateUtils.MINUTE_IN_MILLIS) + " mins ago";
                } else {
                    timeString = "Get: " + String.valueOf(timeSpan / DateUtils.SECOND_IN_MILLIS) + " secs ago";
                }

                canvas.drawText(timeString, width - mDebugInfoPaint.measureText(timeString), mDebugInfoYOffset, mDebugInfoPaint);
            }
            */
        }
    }
}
