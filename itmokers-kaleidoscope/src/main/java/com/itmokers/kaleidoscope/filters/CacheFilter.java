// Copyright 2015 ios.appchina.com Inc. All Rights Reserved.

package com.itmokers.kaleidoscope.filters;


import lombok.Data;

/**
 * @author luofei@appchina.com (Your Name Here)
 */
public class CacheFilter {
    @Data
    public static class SizeCacheFilter extends CacheFilter {
        private int size;
    }

    @Data
    public static class StartSizeCacheFilter extends SizeCacheFilter {
        private int start;
    }

    @Data
    public static class ScoreStartSizeCacheFilter extends SizeCacheFilter {
        private Double maxScore;
        private Double minScore;
    }

    @Data
    public static class ChannelScoreStartSizeCacheFilter extends ScoreStartSizeCacheFilter {
        private String channel;
    }

}
