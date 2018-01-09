package com.yeejay.yplay.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public final class LogUtils {
    private static Logger mLogger;

    private  LogUtils(){}

    public static Logger getInstance() {
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }

        return mLogger;
    }

    /**
     * Is the logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for the DEBUG level,
     *         false otherwise.
     */
     public static boolean isDebugEnabled(){
         if (mLogger == null) {
             mLogger = LoggerFactory.getLogger("LOG_WRITE");
         }
        return mLogger.isDebugEnabled();
    }

    /**
     * Log a message at the DEBUG level.
     *
     * @param msg the message string to be logged
     */
    public static void debug(String msg){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(msg);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and argument.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level. </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public static void debug(String format, Object arg){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(format, arg);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level. </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public static void debug(String format, Object arg1, Object arg2){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(format, arg1, arg2);
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the DEBUG level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for DEBUG. The variants taking
     * {@link #debug(String, Object) one} and {@link #debug(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public static void debug(String format, Object... arguments){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(format, arguments);
    }

    /**
     * Log an exception (throwable) at the DEBUG level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public static void debug(String msg, Throwable t){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(msg, t);
    }

    /**
     * Similar to {@link #isDebugEnabled()} method except that the
     * marker data is also taken into account.
     *
     * @param marker The marker data to take into consideration
     * @return True if this Logger is enabled for the DEBUG level,
     *         false otherwise.
     */
    public static boolean isDebugEnabled(Marker marker){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        return mLogger.isDebugEnabled(marker);
    }

    /**
     * Log a message with the specific Marker at the DEBUG level.
     *
     * @param marker the marker data specific to this log statement
     * @param msg    the message string to be logged
     */
    public static void debug(Marker marker, String msg){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(marker, msg);
    }

    /**
     * This method is similar to {@link #debug(String, Object)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    public static void debug(Marker marker, String format, Object arg){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(marker, format, arg);
    }

    /**
     * This method is similar to {@link #debug(String, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public static void debug(Marker marker, String format, Object arg1, Object arg2){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(marker, format, arg1, arg2);
    }

    /**
     * This method is similar to {@link #debug(String, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker    the marker data specific to this log statement
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public static void debug(Marker marker, String format, Object... arguments){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(marker, format, arguments);
    }

    /**
     * This method is similar to {@link #debug(String, Throwable)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    public static void debug(Marker marker, String msg, Throwable t){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.debug(marker, msg, t);
    }

    /**
     * Is the logger instance enabled for the ERROR level?
     *
     * @return True if this Logger is enabled for the ERROR level,
     *         false otherwise.
     */
    public static boolean isErrorEnabled(){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        return mLogger.isErrorEnabled();
    }

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    public static void error(String msg){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(msg);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and argument.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level. </p>
     *
     * @param format the format string
     * @param arg    the argument
     */
    public static void error(String format, Object arg){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(format, arg);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level. </p>
     *
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public static void error(String format, Object arg1, Object arg2){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(format, arg1, arg2);
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     * <p/>
     * <p>This form avoids superfluous string concatenation when the logger
     * is disabled for the ERROR level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for ERROR. The variants taking
     * {@link #error(String, Object) one} and {@link #error(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.</p>
     *
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public static void error(String format, Object... arguments){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(format, arguments);
    }

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    public static void error(String msg, Throwable t){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(msg, t);
    }

    /**
     * Similar to {@link #isErrorEnabled()} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker The marker data to take into consideration
     * @return True if this Logger is enabled for the ERROR level,
     *         false otherwise.
     */
    public static boolean isErrorEnabled(Marker marker){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        return mLogger.isErrorEnabled(marker);
    }

    /**
     * Log a message with the specific Marker at the ERROR level.
     *
     * @param marker The marker specific to this log statement
     * @param msg    the message string to be logged
     */
    public static void error(Marker marker, String msg){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(marker, msg);
    }

    /**
     * This method is similar to {@link #error(String, Object)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg    the argument
     */
    public static void error(Marker marker, String format, Object arg){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(marker, format, arg);
    }

    /**
     * This method is similar to {@link #error(String, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1   the first argument
     * @param arg2   the second argument
     */
    public static void error(Marker marker, String format, Object arg1, Object arg2){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(marker, format, arg1, arg2);
    }

    /**
     * This method is similar to {@link #error(String, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker    the marker data specific to this log statement
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    public static void error(Marker marker, String format, Object... arguments){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(marker, format, arguments);
    }

    /**
     * This method is similar to {@link #error(String, Throwable)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param msg    the message accompanying the exception
     * @param t      the exception (throwable) to log
     */
    public static void error(Marker marker, String msg, Throwable t){
        if (mLogger == null) {
            mLogger = LoggerFactory.getLogger("LOG_WRITE");
        }
        mLogger.error(marker, msg, t);
    }
}