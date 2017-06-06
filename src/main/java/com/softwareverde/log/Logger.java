package com.softwareverde.log;

import java.util.ArrayList;
import java.util.List;

public class Logger {
    private final List<LogCallback> _logCallbacks = new ArrayList<LogCallback>();

    protected void _emitWarning(final Exception exception, final List<LogCallback> logCallbacks) {
        synchronized (logCallbacks) { // Assumed to be a static final reference given...
            for (final LogCallback errorLogger : logCallbacks) {
                errorLogger.onWarning(exception);
            }
        }
    }

    public void addLogCallback(final LogCallback logCallback) {
        if (logCallback == null) { return; }
        synchronized (_logCallbacks) {
            _logCallbacks.add(logCallback);
        }
    }

    public void removeLogCallback(final LogCallback errorLogger) {
        synchronized (_logCallbacks) {
            _logCallbacks.remove(errorLogger);
        }
    }

    public void emitWarning(final Exception exception) {
        synchronized (_logCallbacks) {
            for (final LogCallback errorLogger : _logCallbacks) {
                errorLogger.onWarning(exception);
            }
        }
    }
}
