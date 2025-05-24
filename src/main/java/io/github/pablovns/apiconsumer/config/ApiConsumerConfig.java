package io.github.pablovns.apiconsumer.config;

public class ApiConsumerConfig {
    private int defaultTimeout = 30000;
    private boolean followRedirects = true;

    public int getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }
}