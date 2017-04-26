package com.spacegoggles.notification;

import javaslang.Function0;
import javaslang.Function1;
import javaslang.collection.List;
import org.junit.Test;

import java.util.ArrayList;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Created by Joshua Hendricks
 */
public class NotificationTest
{

    @Test
    public void testNotification() {

        /*
          Modify an object by topic.  Apply some stuff to a string by topic.
         */

        Function1<String, Object> toLowerCase = Function1.of((value) -> value.toLowerCase());
        Notification notification = new Notification();
        notification.addObserver("string", toLowerCase);
        String newString = (String)notification.apply("string", "HELLO");

        assertEquals("New String should be lower", "hello", newString);
        Function1<String, Object> removeFirst = Function1.of((value) -> value.substring(1, value.length()));
        notification.addObserver("string", removeFirst);
        newString = (String)notification.apply("string", "HELLO");
        assertEquals("Should be lower and h should be removed", "ello", newString);


        /*
          Get reports from topic
         */

        Function0<String> report1 = Function0.of(() -> "my report");
        notification.addObserver("report", report1);

        Function0<String> report2 = Function0.of(() -> "my report 2");
        notification.addObserver("report", report2);

        List<Object> reports = notification.get("report");
        assertEquals("2 reports returned", 2, reports.size());


        /*
         send and forget
         */
        java.util.List<String> mutableList = new ArrayList<>();
        Consumer<String> consumer1 = (object) -> mutableList.add(object);
        notification.addObserver("send", consumer1);
        Consumer<String> consumer2 = (object) -> mutableList.add(object);
        notification.addObserver("send", consumer2);

        notification.post("send", "hello");

        assertEquals("two in mutable list", 2, mutableList.size());
    }
}
