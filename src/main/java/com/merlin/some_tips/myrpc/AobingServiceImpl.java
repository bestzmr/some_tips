package com.merlin.some_tips.myrpc;

public class AobingServiceImpl implements AobingService {
    @Override
    public String hello(String name) {
        return "Yo man Hello，I am" + name;
    }
}  