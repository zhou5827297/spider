package com.spider.sequence.impl;

import com.spider.sequence.Sequence;

/**
 * 自定义流水号生成
 */
public class MySequence implements Sequence {

    @Override
    public String getSequenceId() {
        return "0";
    }


}
