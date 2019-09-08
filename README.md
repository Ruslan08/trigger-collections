[![Build Status](https://travis-ci.com/Ruslan08/trigger-collections.svg?branch=master)](https://travis-ci.com/Ruslan08/trigger-collections)
[![codecov](https://codecov.io/gh/Ruslan08/trigger-collections/branch/master/graph/badge.svg)](https://codecov.io/gh/Ruslan08/trigger-collections)

Wrapper for some of standard Java collections with ability to add triggers that fire when the collection is modified. Each wrapper called as `Trigger` + Java collection name. For example: `TriggerCollection`, `TriggerList`. Each trigger has 2 types of callbacks `before` and `after`, which will be fire before or after target operation correspondingly. For now the following operations are supported:
1. `beforeAdd/afterAdd`
2. `beforeAddAll/afterAddAll`
3. `beforeRemove/afterRemove`
4. `beforeRemoveAll/afterRemoveAll`

## How to use
1. Build project with `mvn clean package`
2. Add jar file to your project

## Logging
The most commonly use case is logging:
```java
List<Integer> sourceList = new ArrayList<>();

List<Integer> list = TriggerList.from(sourceList)
        .afterRemove((i, b) -> System.out.println(i + (b ? " removed" : " is not removed")))
        .build();

list.add(1);
list.add(5);
list.remove(3);
```
> 1 is adding</br>
> 5 is adding</br>
> 3 is not removed

...
