package com.sematext.spm.client.functions;

import com.sematext.spm.client.observation.CalculationFunction;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class TrimTimeUnit implements CalculationFunction {
    private static final Map<String, TimeUnit> TIME_UNITS = new LinkedHashMap<String, TimeUnit>();

    static {

        List<StringToTimeUnit> stringToTimeUnits = new ArrayList<StringToTimeUnit>();

        stringToTimeUnits.add(new StringToTimeUnit("nseconds", TimeUnit.NANOSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("nsecond", TimeUnit.NANOSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("nsecs", TimeUnit.NANOSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("nsec", TimeUnit.NANOSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("ns", TimeUnit.NANOSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("useconds", TimeUnit.MICROSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("usecond", TimeUnit.MICROSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("usecs", TimeUnit.MICROSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("usec", TimeUnit.MICROSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("us", TimeUnit.MICROSECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("mseconds", TimeUnit.MILLISECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("msecond", TimeUnit.MILLISECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("msecs", TimeUnit.MILLISECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("msec", TimeUnit.MILLISECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("ms", TimeUnit.MILLISECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("seconds", TimeUnit.SECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("second", TimeUnit.SECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("secs", TimeUnit.SECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("sec", TimeUnit.SECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("s", TimeUnit.SECONDS));
        stringToTimeUnits.add(new StringToTimeUnit("minutes", TimeUnit.MINUTES));
        stringToTimeUnits.add(new StringToTimeUnit("minute", TimeUnit.MINUTES));
        stringToTimeUnits.add(new StringToTimeUnit("mins", TimeUnit.MINUTES));
        stringToTimeUnits.add(new StringToTimeUnit("min", TimeUnit.MINUTES));
        stringToTimeUnits.add(new StringToTimeUnit("m", TimeUnit.MINUTES));
        stringToTimeUnits.add(new StringToTimeUnit("hours", TimeUnit.HOURS));
        stringToTimeUnits.add(new StringToTimeUnit("hour", TimeUnit.HOURS));
        stringToTimeUnits.add(new StringToTimeUnit("hrs", TimeUnit.HOURS));
        stringToTimeUnits.add(new StringToTimeUnit("hr", TimeUnit.HOURS));
        stringToTimeUnits.add(new StringToTimeUnit("h", TimeUnit.HOURS));

        // Sort the time units by decreasing order of length
        Collections.sort(stringToTimeUnits, new LengthComparator());

        for (StringToTimeUnit stringToTimeUnit : stringToTimeUnits) {
            TIME_UNITS.put(stringToTimeUnit.unit, stringToTimeUnit.timeUnit);
        }
    }

    @Override
    public Object calculateAttribute(Map<String, Object> metrics, Object... params) {
        if (params != null && params.length == 2) {
            String metricName = params[0].toString();
            String value = (String) metrics.get(metricName);
            TimeUnit unitToConvert = TIME_UNITS.get(params[1].toString());
            if (unitToConvert == null) {
                throw new IllegalArgumentException(String.format("Cannot find units in value %s for %s", value, metricName));
            }
            return extractValue(value, metricName, unitToConvert);
        } else {
            throw new IllegalArgumentException("Missing metric name and unit to convert in params");
        }
    }

    private Object extractValue(String value, String metricName, TimeUnit toUnit) {
        for (Map.Entry<String, TimeUnit> fromUnit : TIME_UNITS.entrySet()) {
            if (value.endsWith(fromUnit.getKey())) {
                String trimmedValue = value.substring(0, value.indexOf(fromUnit.getKey())).trim();
                return convert(trimmedValue, toUnit, fromUnit.getValue());
            }
        }
        throw new IllegalArgumentException(String.format("Cannot find units in value %s for %s", value, metricName));
    }

    protected abstract Number convert(String trimmedValue, TimeUnit toUnit, TimeUnit fromUnit);

    @Override
    public String calculateTag(Map<String, String> objectNameTags, Object... params) {
        throw new UnsupportedOperationException("Can't be used in tag context");
    }

    private static final class LengthComparator implements Comparator<StringToTimeUnit> {

        @Override
        public int compare(StringToTimeUnit o1, StringToTimeUnit o2) {
            return o2.unit.length() - o1.unit.length();
        }
    }

    private static final class StringToTimeUnit {
        private String unit;
        private TimeUnit timeUnit;

        StringToTimeUnit(String unit, TimeUnit timeUnit) {
            this.unit = unit;
            this.timeUnit = timeUnit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StringToTimeUnit that = (StringToTimeUnit) o;

            return unit.equals(that.unit);
        }

        @Override
        public int hashCode() {
            return unit.hashCode();
        }
    }
}
