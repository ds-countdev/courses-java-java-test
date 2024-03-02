package org.diegor.junit5.ejemplos.models;

import lombok.Getter;
import lombok.Setter;
import org.diegor.junit5.ejemplos.exception.MoneyInsuficientException;

import java.math.BigDecimal;

@Getter
@Setter
public class Count {

    private String persona;
    private BigDecimal money;
    private Banco bank;

    public Count(String persona, BigDecimal money) {
        this.money = money;
        this.persona = persona;
    }

    public BigDecimal getMoney() {return money;}
    public void setMoney(BigDecimal money) {this.money = money;}
    public String getPersona() {return persona;}
    public void setPersona(String persona) {this.persona = persona;}

    public Banco getBank(){return bank;}
    public void setBank(Banco bank){this.bank = bank;}

    public void debit(BigDecimal quantity){
        BigDecimal newQuantity = this.money.subtract(quantity);
        if(newQuantity.compareTo(BigDecimal.ZERO) < 0 )
            throw new MoneyInsuficientException("Insufficient Money");
        this.money = newQuantity;


    }
    public void credit(BigDecimal quantity){
        this.money = this.money.add(quantity);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Count)){
            return false;
        }
        Count count = (Count) obj;

        if(this.persona == null || this.money == null){
            return false;
        }
        return this.persona.equals(count.getPersona()) && this.money.equals(count.getMoney());
    }
}
