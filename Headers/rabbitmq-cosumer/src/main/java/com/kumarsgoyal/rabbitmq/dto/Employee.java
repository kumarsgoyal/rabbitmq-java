package com.kumarsgoyal.rabbitmq.dto;

public class Employee {

    private String name;

    private String empId;

    private String priority;

    public Employee() {
    }

    public Employee(String name, String empId, String priority) {
        this.name = name;
        this.empId = empId;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
