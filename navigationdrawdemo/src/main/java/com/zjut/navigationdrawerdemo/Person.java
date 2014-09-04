package com.zjut.navigationdrawerdemo;

/**
 * Created by Lake on 14-9-4.
 */
public class Person {
    private int id;
    private String name;
    private String gender;
    private int age;
    private String phone;
    private String email;
    private Boolean available;

    public Person(){}
    public Person(int id, String name, int age)
    {
        this.id=id;
        this.name=name;
        this.age=age;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
