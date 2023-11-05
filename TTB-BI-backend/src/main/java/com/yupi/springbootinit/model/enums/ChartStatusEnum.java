package com.yupi.springbootinit.model.enums;


public enum ChartStatusEnum {

    STATUS_WAIT("wait"),
    STATUS_SUCCEED("succeed"),
    STATUS_RUNNING("running"),
    STATUS_FAILED("failed");


    public String getMessage() {
        return message;
    }

    /**
     * 信息
     */
    private final String message;

    ChartStatusEnum(String message) {
        this.message = message;
    }
}
