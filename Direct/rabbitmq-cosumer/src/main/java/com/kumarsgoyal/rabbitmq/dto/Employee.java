package com.kumarsgoyal.rabbitmq.dto;

public class Employee {

    private String name;

    private String empId;

    public Employee() {
    }

    public Employee(String name, String empId) {
        this.name = name;
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmpId(String s) {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                       "name='" + name + '\'' +
                       ", empId='" + empId + '\'' +
                       '}';
    }
}
