package com.merlin.some_tips.springxunhuanyilai;

public class StudentC {

    private StudentA studentA ;

    public void setStudentA(StudentA studentA) {
        this.studentA = studentA;
    }

    public StudentC() {
    }

    public StudentC(StudentA studentA) {
        this.studentA = studentA;
    }
}