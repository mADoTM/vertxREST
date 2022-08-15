package com.dolzhenkoms.starter;

import java.util.UUID;

public class Person {

  private int id;
  private String name;

  public static Person of(String name, int id) {
    Person person = new Person();

    person.setName(name);
    person.setId(id);

    return person;
  }

  public Person() { }

  public Person(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Person{" +
      "id=" + id +
      ", name='" + name + '\'' +
      '}';
  }
}
