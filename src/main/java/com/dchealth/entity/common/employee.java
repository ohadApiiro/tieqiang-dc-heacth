package com.dchealth.entity.common;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.LocalTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat.Style;

public class Employee {

    private String name;

    /**
     * numeric fields using @NumberFormat annotation for formatting.
     */
    private double salary;
    @NumberFormat(style = Style.PERCENT)
    private double w4AdditionalWithdraw;

    /**
     * date and time fields using @DateTimeFormat annotation for formatting.
     */
    private Date birthDate;
    @DateTimeFormat(pattern = "w:yyyy")
    private Calendar hireDate;

    /**
     * initialization block to provide sample data for display
     */
    {
        this.name = "John Doe";
        this.salary = 30100.50;
        this.w4AdditionalWithdraw = 0.02;

        Calendar dob = Calendar.getInstance();
        dob.set(1964, Calendar.AUGUST, 30);
        this.birthDate = dob.getTime();
        this.hireDate = Calendar.getInstance();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getW4AdditionalWithdraw() {
        return w4AdditionalWithdraw;
    }

    public void setW4AdditionalWithdraw(double w4AdditionalWithdraw) {
        this.w4AdditionalWithdraw = w4AdditionalWithdraw;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Calendar getHireDate() {
        return hireDate;
    }

    public void setHireDate(Calendar hireDate) {
        this.hireDate = hireDate;
    }

}

//    The Employee class now has formatting annotations.