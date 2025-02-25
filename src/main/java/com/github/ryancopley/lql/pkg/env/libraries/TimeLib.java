// --------- FILE START: "TimeLib.java" (converted from pkg/env/libraries/time.go) ----------
package com.github.ryancopley.lql.pkg.env.libraries;

import com.github.ryancopley.lql.pkg.env.ILibrary;
import com.github.ryancopley.lql.pkg.errors.Errors;
import com.github.ryancopley.lql.pkg.param.Param;
import com.github.ryancopley.lql.pkg.types.Types;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TimeLib implements ILibrary {

    // TimeValue represents a time with epochMillis and zone.
    public static class TimeValue {
        private long epochMillis;
        private String zone;

        public TimeValue(long epochMillis, String zone) {
            this.epochMillis = epochMillis;
            this.zone = zone;
        }

        public long getEpochMillis() {
            return epochMillis;
        }

        public String getZone() {
            return zone;
        }
    }

    public TimeLib() {
    }

    private TimeValue newTimeValue(ZonedDateTime t) {
        return new TimeValue(t.toInstant().toEpochMilli(), t.getZone().toString());
    }

    @Override
    public Object call(String functionName, List<Param> args, int line, int col, int parenLine, int parenCol) throws Exception {
        switch (functionName) {
            case "now": {
                if (!args.isEmpty()) {
                    throw Errors.newParameterError("time.now() takes no arguments", line, col);
                }
                ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
                return newTimeValue(now);
            }
            case "parse": {
                if (args.size() < 2) {
                    throw Errors.newParameterError("time.parse requires at least 2 arguments", parenLine, parenCol);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object inputObj = arg0.getValue();
                Object formatObj = arg1.getValue();
                if (!(inputObj instanceof String)) {
                    throw Errors.newTypeError("time.parse: first argument must be a string", arg0.getLine(), arg0.getColumn());
                }
                if (!(formatObj instanceof String)) {
                    throw Errors.newTypeError("time.parse: second argument must be a string", arg1.getLine(), arg1.getColumn());
                }
                String inputStr = ((String) inputObj).trim();
                String format = (String) formatObj;
                ZonedDateTime tTime;
                try {
                    switch (format) {
                        case "iso8601":
                            tTime = ZonedDateTime.parse(inputStr, DateTimeFormatter.ISO_DATE_TIME);
                            break;
                        case "dateOnly":
                            LocalDate ld = LocalDate.parse(inputStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            tTime = ld.atStartOfDay(ZoneOffset.UTC);
                            break;
                        case "epochMillis":
                            long ms = Long.parseLong(inputStr);
                            tTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneOffset.UTC);
                            return new TimeValue(ms, "UTC");
                        case "rfc2822":
                            DateTimeFormatter rfc2822 = DateTimeFormatter.RFC_1123_DATE_TIME;
                            tTime = ZonedDateTime.parse(inputStr, rfc2822);
                            break;
                        case "custom":
                            if (args.size() != 3) {
                                throw Errors.newParameterError("time.parse with 'custom' requires a formatDetails argument", line, col);
                            }
                            Param arg2 = args.get(2);
                            Object formatDetailsObj = arg2.getValue();
                            if (!(formatDetailsObj instanceof String)) {
                                throw Errors.newTypeError("time.parse: formatDetails must be a string", arg2.getLine(), arg2.getColumn());
                            }
                            String formatDetails = (String) formatDetailsObj;
                            DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern(formatDetails).withZone(ZoneOffset.UTC);
                            tTime = ZonedDateTime.parse(inputStr, customFormatter);
                            break;
                        default:
                            throw Errors.newTypeError("time.parse: unknown format", arg1.getLine(), arg1.getColumn());
                    }
                } catch (Exception e) {
                    throw Errors.newTypeError("time.parse error: " + e.getMessage(), arg0.getLine(), arg0.getColumn());
                }
                return newTimeValue(tTime.withZoneSameInstant(ZoneOffset.UTC));
            }
            case "add": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("time.add requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.add: first argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                Param arg1 = args.get(1);
                Long dur = Types.toInt(arg1.getValue());
                if (dur == null) {
                    throw Errors.newTypeError("time.add: second argument must be numeric", arg1.getLine(), arg1.getColumn());
                }
                long newMillis = tv.getEpochMillis() + dur;
                return new TimeValue(newMillis, tv.getZone());
            }
            case "subtract": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("time.subtract requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.subtract: first argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                Param arg1 = args.get(1);
                Long dur = Types.toInt(arg1.getValue());
                if (dur == null) {
                    throw Errors.newTypeError("time.subtract: second argument must be numeric", arg1.getLine(), arg1.getColumn());
                }
                long newMillis = tv.getEpochMillis() - dur;
                return new TimeValue(newMillis, tv.getZone());
            }
            case "diff": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("time.diff requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object tv1Obj = arg0.getValue();
                Object tv2Obj = arg1.getValue();
                if (!(tv1Obj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.diff: first argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                if (!(tv2Obj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.diff: second argument must be Time", arg1.getLine(), arg1.getColumn());
                }
                TimeValue tv1 = (TimeValue) tv1Obj;
                TimeValue tv2 = (TimeValue) tv2Obj;
                return tv1.getEpochMillis() - tv2.getEpochMillis();
            }
            case "isBefore": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("time.isBefore requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object tv1Obj = arg0.getValue();
                Object tv2Obj = arg1.getValue();
                if (!(tv1Obj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.isBefore: first argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                if (!(tv2Obj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.isBefore: second argument must be Time", arg1.getLine(), arg1.getColumn());
                }
                TimeValue tv1 = (TimeValue) tv1Obj;
                TimeValue tv2 = (TimeValue) tv2Obj;
                return tv1.getEpochMillis() < tv2.getEpochMillis();
            }
            case "isAfter": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("time.isAfter requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object tv1Obj = arg0.getValue();
                Object tv2Obj = arg1.getValue();
                if (!(tv1Obj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.isAfter: first argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                if (!(tv2Obj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.isAfter: second argument must be Time", arg1.getLine(), arg1.getColumn());
                }
                TimeValue tv1 = (TimeValue) tv1Obj;
                TimeValue tv2 = (TimeValue) tv2Obj;
                return tv1.getEpochMillis() > tv2.getEpochMillis();
            }
            case "isEqual": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("time.isEqual requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object tv1Obj = arg0.getValue();
                Object tv2Obj = arg1.getValue();
                if (!(tv1Obj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.isEqual: first argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                if (!(tv2Obj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.isEqual: second argument must be Time", arg1.getLine(), arg1.getColumn());
                }
                TimeValue tv1 = (TimeValue) tv1Obj;
                TimeValue tv2 = (TimeValue) tv2Obj;
                return tv1.getEpochMillis() == tv2.getEpochMillis();
            }
            case "toEpochMillis": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("time.toEpochMillis requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.toEpochMillis: argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                return tv.getEpochMillis();
            }
            case "format": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("time.format requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Param arg1 = args.get(1);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.format: first argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                Object formatObj = arg1.getValue();
                if (!(formatObj instanceof String)) {
                    throw Errors.newTypeError("time.format: second argument must be string", arg1.getLine(), arg1.getColumn());
                }
                String formatStr = ((String) formatObj).trim();
                if (formatStr.isEmpty()) {
                    formatStr = DateTimeFormatter.ISO_DATE_TIME.toString();
                }
                ZoneId zone;
                try {
                    zone = ZoneId.of(tv.getZone());
                } catch (Exception e) {
                    zone = ZoneOffset.UTC;
                }
                Instant instant = Instant.ofEpochMilli(tv.getEpochMillis());
                ZonedDateTime tTime = ZonedDateTime.ofInstant(instant, zone);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatStr).withZone(zone);
                return tTime.format(formatter);
            }
            case "getYear": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("time.getYear requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.getYear: argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                ZoneId zone;
                try {
                    zone = ZoneId.of(tv.getZone());
                } catch (Exception e) {
                    zone = ZoneOffset.UTC;
                }
                ZonedDateTime tTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(tv.getEpochMillis()), zone);
                return (long) tTime.getYear();
            }
            case "getMonth": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("time.getMonth requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.getMonth: argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                ZoneId zone;
                try {
                    zone = ZoneId.of(tv.getZone());
                } catch (Exception e) {
                    zone = ZoneOffset.UTC;
                }
                ZonedDateTime tTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(tv.getEpochMillis()), zone);
                return (long) tTime.getMonthValue();
            }
            case "getDay": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("time.getDay requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.getDay: argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                ZoneId zone;
                try {
                    zone = ZoneId.of(tv.getZone());
                } catch (Exception e) {
                    zone = ZoneOffset.UTC;
                }
                ZonedDateTime tTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(tv.getEpochMillis()), zone);
                return (long) tTime.getDayOfMonth();
            }
            case "startOfDay": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("time.startOfDay requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.startOfDay: argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                ZoneId zone;
                try {
                    zone = ZoneId.of(tv.getZone());
                } catch (Exception e) {
                    zone = ZoneOffset.UTC;
                }
                ZonedDateTime tTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(tv.getEpochMillis()), zone);
                ZonedDateTime start = tTime.toLocalDate().atStartOfDay(zone);
                return newTimeValue(start);
            }
            case "endOfDay": {
                if (args.size() != 1) {
                    throw Errors.newParameterError("time.endOfDay requires 1 argument", line, col);
                }
                Param arg0 = args.get(0);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.endOfDay: argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                ZoneId zone;
                try {
                    zone = ZoneId.of(tv.getZone());
                } catch (Exception e) {
                    zone = ZoneOffset.UTC;
                }
                ZonedDateTime tTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(tv.getEpochMillis()), zone);
                ZonedDateTime end = tTime.toLocalDate().atTime(23, 59, 59, 999_000_000).atZone(tTime.getZone());
                return newTimeValue(end);
            }
            case "withZone": {
                if (args.size() != 2) {
                    throw Errors.newParameterError("time.withZone requires 2 arguments", line, col);
                }
                Param arg0 = args.get(0);
                Object tvObj = arg0.getValue();
                if (!(tvObj instanceof TimeValue)) {
                    throw Errors.newTypeError("time.withZone: first argument must be Time", arg0.getLine(), arg0.getColumn());
                }
                TimeValue tv = (TimeValue) tvObj;
                Param arg1 = args.get(1);
                Object zoneObj = arg1.getValue();
                if (!(zoneObj instanceof String)) {
                    throw Errors.newTypeError("time.withZone: second argument must be string", arg1.getLine(), arg1.getColumn());
                }
                String zoneName = (String) zoneObj;
                ZoneId zone;
                try {
                    zone = ZoneId.of(zoneName);
                } catch (Exception e) {
                    throw Errors.newTypeError("time.withZone: invalid zone name", arg1.getLine(), arg1.getColumn());
                }
                return new TimeValue(tv.getEpochMillis(), zone.toString());
            }
            default:
                throw Errors.newFunctionCallError("unknown time function '" + functionName + "'", 0, 0);
        }
    }
}
// --------- FILE END: "TimeLib.java" ----------
