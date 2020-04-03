[![Build Status](https://travis-ci.com/Ruslan08/trigger-collections.svg?branch=master)](https://travis-ci.com/Ruslan08/trigger-collections)
[![codecov](https://codecov.io/gh/Ruslan08/trigger-collections/branch/master/graph/badge.svg)](https://codecov.io/gh/Ruslan08/trigger-collections)

Wrapper for some of standard Java collections with ability to add triggers that fire when the collection is modified. Each wrapper is called as `Trigger` + Java collection name. For example: `TriggerCollection`, `TriggerList`. Each trigger has 3 types of callbacks `before`, `after` and `allow`, and can be fired for all standard collection methods. Like `before` + standard method name. For now the following operations are supported:
1. `beforeAdd/afterAdd/allowAdd`
2. `beforeAddAll/afterAddAll/allowAddAll`
3. `beforeRemove/afterRemove/allowRemove`
4. `beforeRemoveAll/afterRemoveAll/allowRemoveAll`

5. `beforeGet/afterGet`

## How to use
1. Build project with `mvn clean package`
2. Add jar file to your project

## Keep track of collection modifying

```java
List<Integer> sourceList = new ArrayList<>();

List<Integer> list = TriggerList.from(sourceList)
        .beforeAdd(valueToAdd -> someAction(valueToAdd))
        .afterRemove((valueToAdd, res) -> res ? doSmth() : doSmthElse())
        .build();

list.add(1);    // someAction(1) is called
list.add(5);    // someAction(5) is called
list.remove(3); // doSmthElse() is called
```

## Prevent some of operation

```java
List<Integer> sourceList = new ArrayList<>();
Predicate<Integer> predicate = integer -> integer < 5;

List<Integer> list = TriggerList.from(sourceList)
        .allowAdd(predicate)    // allow adding integers less than 5 only
        .build();

list.add(4);
list.add(6);

list.size(); // 1

```
