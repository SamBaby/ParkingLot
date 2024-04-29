package com.example.parking5.event;

/**
 * 
 * Interface to registry EventDelegate
 *
 * @param <T>
 */
public interface INotification<T extends INotificationEventArgs> {
    void perform(Object from, T args);
}