package com.dolzhenkoms.starter;

import java.util.Objects;
import java.util.UUID;

public class CreatePersonDTO {
  private int id;
  private String name;

  public CreatePersonDTO() {}

  public CreatePersonDTO(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public static CreatePersonDTO of(String name, int id) {
    return new CreatePersonDTO(name, id);
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CreatePersonDTO that = (CreatePersonDTO) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "CreatePersonDTO{" +
      "name='" + name + '\'' +
      '}';
  }
}
