package org.diegor.junit5.ejemplos.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Banco {
    private String name;
    private List<Count> countList;

    public Banco(){
        countList = new ArrayList<>();
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public List<Count> getCountList(){
        return countList;
    }

    public void setCountList(List<Count> countList){
        this.countList = countList;
    }
    public void addCount(Count count){
        countList.add(count);
        count.setBank(this);
    }

    public void transfer(Count originCount, Count recipientCount, BigDecimal mount){
        originCount.debit(mount);
        recipientCount.credit(mount);
    }



}
