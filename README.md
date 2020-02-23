# XmlGrinder



## Overview

#### Serializing a simple object:

```java
@Xml
public class Single {

    @Attribute
    private int id;

    @XmlElement
    private String name;

    @XmlElement
    private int age;

    public Single(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    //getters and setters here(optional)
}
```
To serialize an instance of the above object a XmlGrinder is required.

```java
    XmlGrinder grinder=new XmlGrinder();
     
    Single single=new Single(2,"Single",20);
    grinder.write(single, "E:\\single.xml");
    
```
And output:
    
   
```xml
    <Single id="2">
      <name>Single</name>
      <age>20</age>
    </Single>
 ```
 
 #### Deserializing a simple object:
 
 Deserialization is really easy, the read method is used, which produces an instance of the annotated object. 
 
 ```java
 
   XmlGrinder grinder = new XmlGrinder();
        
   Single single=grinder.read(Single.class, "E:\\single.xml");
 ```
 
 #### Serializing nested class and list
 
 To serialize more complex object the empty constructor always is required.
 
 ```java
 @Xml
public class Nested {
    
    @Attribute
    private String name;
    
    @XmlElement
    private String address;
    
    @XmlElement
    private String email;
    
    //Empty constructor is neccesary
    public Nested() {
    }

    public Nested(String name, String address, String email) {
        this.name = name;
        this.address = address;
        this.email = email;
    }
    
    //getters and setters here(optional)
}

@Xml
public class NestedWithList {
    
    @Attribute
    private int index;
    
    @XmlElement
    private String name;
    
    @XmlElement
    private Nested nested;
    
    @XmlList
    private List<Single> singles;

    //Empty constructor is necessary
    public NestedWithList() {
    }

    public NestedWithList(int index, String name, Nested nested, List<Single> singles) {
        this.index = index;
        this.name = name;
        this.nested = nested;
        this.singles = singles;
    }

    //getters and setters here(optional)
    
}
```
And the output:

```xml
<NestedWithList index="345">
    <name>nestedWithList</name>
    <nested name="Harley">
        <address>Gotham</address>
        <email>nested@withlist.com</email>
    </nested>
    <singles>
        <Single id="1">
            <name>Joker</name>
            <age>34</age>
        </Single>
        <Single id="2">
            <name>Batman</name>
            <age>66</age>
        </Single>
    </singles>
</NestedWithList>

```

#### Deserializing a nested object and/or list:

Deserialization of complex object is similar as above.

```java
   XmlGrinder grinder = new XmlGrinder();

   NestedWithList nestedWithList=grinder.read(NestedWithList.class, "E:\\nest.xml");
```
#### Limitations

It does not support namespace or any other type of collection. More efficient version is possible, some algorithm will be better
and it would be worth to write custom parser/and writer.

#### Note
This is example project of Liberator 0.2
