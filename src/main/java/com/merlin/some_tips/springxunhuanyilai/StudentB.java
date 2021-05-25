package com.merlin.some_tips.springxunhuanyilai;

public class StudentB {

    private StudentC studentC ;

    public void setStudentC(StudentC studentC) {
        this.studentC = studentC;
    }

    public StudentB() {
    }

    public StudentB(StudentC studentC) {
        this.studentC = studentC;
    }
}