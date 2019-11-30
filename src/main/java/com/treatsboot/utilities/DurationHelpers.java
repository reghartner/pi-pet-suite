package com.treatsboot.utilities;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DurationHelpers
{
    public static String getPrettyDuration(long ms)
    {
        Duration duration = new Duration(ms);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
            .appendMinutes()
            .appendSuffix("m")
            .appendSeconds()
            .appendSuffix("s")
            .toFormatter();
        return formatter.print(duration.toPeriod());
    }
}
