package com.eventmaster.util;


import java.util.concurrent.atomic.AtomicLong;


public class Ids {
private final AtomicLong seq = new AtomicLong(1);
public long next() { return seq.getAndIncrement(); }
}