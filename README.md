
### Demonstrates the Slick "distinct" bug.

Issue:
[slick/slick#1689](https://github.com/slick/slick/issues/1689)

To run test:
```
sbt run
```

#### With Slick 3.1.1

```
...
background log: info: ####### Start of test #######
background log: info: User(John Doe,167,true,Meadows,Some(1))
background log: info: User(Fred Smith,189,false,Mendocino,Some(2))
background log: info: User(Big Guy,195,true,New York,Some(3))
background log: info: Distinct query:
background log: info: SQL to run: List(select "NAME", "HAS_DOG", "HEIGHT", "CITY", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID") Success. Number of users: 2
background log: info: SQL to run: List(select "HAS_DOG", "CITY", "HEIGHT", "NAME", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID") Success. Number of users: 2
background log: info: SQL to run: List(select "HEIGHT", "NAME", "HAS_DOG", "ID", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID") Success. Number of users: 2
background log: info: SQL to run: List(select "ID", "HAS_DOG", "CITY", "HEIGHT", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID")
background log: info:    !! FAILED --- Distinct query run failed with exception: Data conversion error converting "Mendocino" [22018-187], Cause: null
background log: info: SQL to run: List(select "HEIGHT", "HAS_DOG", "CITY", "ID", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID")
background log: info:    !! FAILED --- Distinct query run failed with exception: Data conversion error converting "Fred Smith" [22018-187], Cause: null
background log: info: SQL to run: List(select "NAME", "HEIGHT", "CITY", "ID", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID")
background log: info:    !! FAILED --- Distinct query run failed with exception: Data conversion error converting "Mendocino" [22018-187], Cause: For input string: "Mendocino"
background log: info: ####### End of test, closing db connection #######
...
```

So with 6 tries, the first 3 will successfully run but the other 3 will fail. Which is a consistent behaviour.  
And the generated SQL queries are a little bit strange.

#### With Slick 3.2.1
```
...
background log: info: ####### Start of test #######
background log: info: User(John Doe,167,true,Meadows,Some(1))
background log: info: User(Fred Smith,189,false,Mendocino,Some(2))
background log: info: User(Big Guy,195,true,New York,Some(3))
background log: info: Distinct query:
background log: info: SQL to run: List(select "NAME", "ID", "HAS_DOG", "CITY", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID") Success. Number of users: 2
background log: info: SQL to run: List(select "CITY", "ID", "HAS_DOG", "HEIGHT", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID")
background log: info:    !! FAILED --- Distinct query run failed with exception: Data conversion error converting "Mendocino" [22018-187], Cause: null
background log: info: SQL to run: List(select "HAS_DOG", "CITY", "NAME", "HEIGHT", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID")
background log: info:    !! FAILED --- Distinct query run failed with exception: Data conversion error converting "Mendocino" [22018-187], Cause: For input string: "Mendocino"
background log: info: SQL to run: List(select "HAS_DOG", "ID", "HEIGHT", "NAME", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID")
background log: info:    !! FAILED --- Distinct query run failed with exception: Data conversion error converting "Mendocino" [22018-187], Cause: null
background log: info: SQL to run: List(select "HAS_DOG", "CITY", "NAME", "ID", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID")
background log: info:    !! FAILED --- Distinct query run failed with exception: Data conversion error converting "Mendocino" [22018-187], Cause: For input string: "Mendocino"
background log: info: SQL to run: List(select "HEIGHT", "ID", "NAME", "HAS_DOG", min("ID") from "USERS" where "CITY" like 'M%' group by "NAME", "HEIGHT", "HAS_DOG", "CITY", "ID")
background log: info:    !! FAILED --- Distinct query run failed with exception: Data conversion error converting "Mendocino" [22018-187], Cause: null
background log: info: ####### End of test, closing db connection #######
...
```

It was a little bit different result and not so consistent but also fails sometimes.


##### Versions

Scala: 2.11.11  
Sbt: 0.13.16  
Slick: 3.2.1  
OS: Windows 10
