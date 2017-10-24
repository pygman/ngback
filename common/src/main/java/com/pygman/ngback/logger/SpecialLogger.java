package com.pygman.ngback.logger;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * 特殊目录日志
 * 留着统一特殊处理
 * Created by pygman on 16-11-25.
 */
public abstract class SpecialLogger implements Logger {
    private Logger logger;

    protected abstract Logger getLogger();

    @Override
    public String getName() {
        return getLogger().getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return getLogger().isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        getLogger().trace(s);
    }

    @Override
    public void trace(String s, Object o) {
        getLogger().trace(s, o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        getLogger().trace(s, o, o1);
    }

    @Override
    public void trace(String s, Object... objects) {
        getLogger().trace(s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        getLogger().trace(s, throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return getLogger().isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String s) {
        getLogger().trace(marker, s);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        getLogger().trace(marker, s, o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        getLogger().trace(marker, s, o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        getLogger().trace(marker, s, objects);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        getLogger().trace(marker, s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        getLogger().debug(s);
    }

    @Override
    public void debug(String s, Object o) {
        getLogger().debug(s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        getLogger().debug(s, o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        getLogger().debug(s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        getLogger().debug(s, throwable);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return getLogger().isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String s) {
        getLogger().debug(marker, s);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        getLogger().debug(marker, s, o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        getLogger().debug(marker, s, o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        getLogger().debug(marker, s, objects);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        getLogger().debug(marker, s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    @Override
    public void info(String s) {
        getLogger().info(s);
    }

    @Override
    public void info(String s, Object o) {
        getLogger().info(s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        getLogger().info(s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        getLogger().info(s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        getLogger().info(s, throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return getLogger().isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String s) {
        getLogger().info(marker, s);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        getLogger().info(marker, s, o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        getLogger().info(marker, s, o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        getLogger().info(marker, s, objects);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        getLogger().info(marker, s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return getLogger().isWarnEnabled();
    }

    @Override
    public void warn(String s) {
        getLogger().warn(s);
    }

    @Override
    public void warn(String s, Object o) {
        getLogger().warn(s, o);
    }

    @Override
    public void warn(String s, Object... objects) {
        getLogger().warn(s, objects);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        getLogger().warn(s, o, o1);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        getLogger().warn(s, throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return getLogger().isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String s) {
        getLogger().warn(marker, s);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        getLogger().warn(marker, s, o);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        getLogger().warn(marker, s, o, o1);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        getLogger().warn(marker, s, objects);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        getLogger().warn(marker, s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return getLogger().isErrorEnabled();
    }

    @Override
    public void error(String s) {
        getLogger().error(s);
    }

    @Override
    public void error(String s, Object o) {
        getLogger().error(s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        getLogger().error(s, o, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        getLogger().error(s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        getLogger().error(s, throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return getLogger().isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String s) {
        getLogger().error(marker, s);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        getLogger().error(marker, s, o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        getLogger().error(marker, s, o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        getLogger().error(marker, s, objects);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        getLogger().error(marker, s, throwable);
    }

}
