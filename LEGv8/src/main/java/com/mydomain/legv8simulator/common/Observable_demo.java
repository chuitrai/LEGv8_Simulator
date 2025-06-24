package main.java.com.mydomain.legv8simulator.common;

import java.util.ArrayList;
import java.util.List;

public class Observable_demo {
    private final List<Observer_demo> observers = new ArrayList<>();

    public void addObserver(Observer_demo observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    protected void notifyObservers(Object arg) {
        for (Observer_demo observer : new ArrayList<>(observers)) {
            observer.update(arg);
        }
    }
}