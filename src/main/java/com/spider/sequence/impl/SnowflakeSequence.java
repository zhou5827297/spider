package com.spider.sequence.impl;

import com.spider.sequence.Sequence;
import com.spider.util.SnowflakeIdWorker;
import com.spider.config.ServerConstant;

/**
 * SnowFlake生成器
 */
public class SnowflakeSequence implements Sequence {
    private static final Sequence SEQUENCE = new SnowflakeSequence();

    private SnowflakeIdWorker SNOWFLAKEIDWORKER = new SnowflakeIdWorker(ServerConstant.CLIENT_WORKERID, ServerConstant.LIENT_DATACENTERID);

    @Override
    public String getSequenceId() {
        return String.valueOf(SNOWFLAKEIDWORKER.nextId());
    }

    public static Sequence getSequence() {
        return SEQUENCE;
    }

}
