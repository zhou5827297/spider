package com.zhoukai.status;

/**
 * 任务推送状态码值
 * 1：待执行，2：执行中，3：执行成功，4：执行失败
 */
public enum TaskPushStatusEnum {
    WAIT(1),
    DEALING(2),
    SUCCESS(3),
    FAIL(4);
    private int status;

    TaskPushStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
