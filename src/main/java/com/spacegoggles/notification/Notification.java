package com.spacegoggles.notification;

import javaslang.*;
import javaslang.collection.HashMap;

import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.control.Option;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Based on NotificationCenter on iOS but built to be better, stronger, and faster(well maybe).
 * Created by Joshua Hendricks.
 */
public class Notification
{
    private Map<String, List<Function>> topicFunctionCallbacks = HashMap.empty();
    private Map<String, List<Function0>> topicSupplierCallbacks = HashMap.empty();
    private Map<String, List<Consumer>> topicConsumerCallbacks = HashMap.empty();

    /**
     * Observe for topic and contribute to rolling value
     * @param topic
     * @param callback
     */
    public void addObserver(String topic, Function callback){
        List<Function> callbacks = topicFunctionCallbacks.get(topic).getOrElse(List.empty());
        List<Function> addedCallbacks = callbacks.push(callback);
        topicFunctionCallbacks = topicFunctionCallbacks.put(topic, addedCallbacks);
    }

    /**
     * Observe for topic and only return value
     * @param topic
     * @param callback
     */
    public void addObserver(String topic, Function0 callback){
        List<Function0> callbacks = topicSupplierCallbacks.get(topic).getOrElse(List.empty());
        List<Function0> supplierCallbacks = callbacks.push(callback);
        topicSupplierCallbacks = topicSupplierCallbacks.put(topic, supplierCallbacks);
    }

    /**
     * Observe for a topic and only do something
     * @param topic
     * @param callback
     */
    public void addObserver(String topic, Consumer callback){
        List<Consumer> callbacks = topicConsumerCallbacks.get(topic).getOrElse(List.empty());
        List<Consumer> addedCallbacks = callbacks.push(callback);
        topicConsumerCallbacks = topicConsumerCallbacks.put(topic, addedCallbacks);
    }

    /**
     * Apply all callbacks to value.  This will allow callback to contribute to value.
     * Returns ending value after contributions.
     * @param topic
     * @param object
     * @return new object after contributions
     */
    public Object apply(String topic, Object object) {
        Option<List<Function>> callbacks = topicFunctionCallbacks.get(topic);
        Object resultObject = callbacks
                .map(l -> l
                            .foldRight(object, (a, b) -> a.apply(b)))
                .getOrElse(object);
        return resultObject;
    }

    /**
     * Gather information from topic.
     * @param topic
     * @return
     */
    public List<Object> get(String topic) {
        Option<List<Function0>> callbacks = topicSupplierCallbacks.get(topic);
        List<Object> resultObjects = callbacks.map(l -> l.map(c -> c.get())).getOrElse(List.empty());
        return resultObjects;
    }

    /**
     * Send to topic some data and forget
     * @param topic
     * @param object
     */
    public void post(String topic, Object object) {
        Option<List<Consumer>> callbacks = topicConsumerCallbacks.get(topic);
        callbacks.forEach(l -> l.forEach(c -> c.accept(object)));
    }
}
