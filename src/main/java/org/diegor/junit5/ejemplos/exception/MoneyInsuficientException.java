package org.diegor.junit5.ejemplos.exception;

public class MoneyInsuficientException extends RuntimeException{
    public MoneyInsuficientException (String message){
        super(message);
    }
}
