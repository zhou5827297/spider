package com.zhoukai.status;

/**
 * 流程状态码值
 * 1：待执行，2：执行中，3：执行成功，4：执行失败，5：执行超时
 */
public enum ProcessStatusEnum {
    WAIT(1),
    DEALING(2),
    SUCCESS(3),
    FAIL(4),
    TIMEOUT(5);
    private int status;

    ProcessStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
