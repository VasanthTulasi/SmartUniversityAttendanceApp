package com.smart.student.attendance.vasanth.smartattendanceforstudent;

public class CardClass {
    private int indexNumber;
    private String nameOfPerson;
    private String rollNumberOfPerson; //Added this line

    public CardClass(int indexNumber, String nameOfPerson,String rollNumberOfPerson) {
        this.indexNumber = indexNumber;
        this.nameOfPerson = nameOfPerson;
        this.rollNumberOfPerson  = rollNumberOfPerson;
    }

    public CardClass(int indexNumber, String nameOfPerson) {
        this.indexNumber = indexNumber;
        this.nameOfPerson = nameOfPerson;
    }

    //Added this method
    public String getRollNumberOfPerson() {
        return rollNumberOfPerson;
    }

    //Added this method
    public void setRollNumberOfPerson(String rollNumberOfPerson) {
        this.rollNumberOfPerson = rollNumberOfPerson;
    }

    public int getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public String getNameOfPerson() {
        return nameOfPerson;
    }

    public void setNameOfPerson(String nameOfPerson) {
        this.nameOfPerson = nameOfPerson;
    }
}
