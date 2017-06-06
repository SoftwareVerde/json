package com.softwareverde.log;

public interface LogCallback {
    LogCallback SysErrLogger = new LogCallback() {
        @Override
        public void onWarning(final Exception exception) {
            exception.printStackTrace();
        }
    };

    void onWarning(Exception exception);
}
