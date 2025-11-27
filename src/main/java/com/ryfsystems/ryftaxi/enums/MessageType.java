package com.ryfsystems.ryftaxi.enums;

public enum MessageType {
    CHAT, 
    JOIN, 
    LEAVE, 
    TYPING,
    STOP_TYPING,

    SERVICE_REQUEST,
    SERVICE_ACCEPTED,
    SERVICE_REJECTED,
    SERVICE_CANCELLED,
    SERVICE_COMPLETED,
    DRIVER_LOCATION,
    SERVICE_STATUS_UPDATE
}
