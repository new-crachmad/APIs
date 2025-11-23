package com.eventmaster.repository;


import com.eventmaster.model.*;
import com.eventmaster.util.Ids;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class InMemoryStore {
public final Map<Long, Event> events = new ConcurrentHashMap<>();
public final Map<Long, User> users = new ConcurrentHashMap<>();
public final Map<Long, Category> categories = new ConcurrentHashMap<>();
public final Map<Long, Registration> registrations = new ConcurrentHashMap<>();


public final Ids eventIds = new Ids();
public final Ids userIds = new Ids();
public final Ids categoryIds = new Ids();
public final Ids registrationIds = new Ids();
}