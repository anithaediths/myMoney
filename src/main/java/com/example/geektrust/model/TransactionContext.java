package com.example.geektrust.model;

import java.util.List;
import java.util.Map;

//@Getter
//@Setter
public class TransactionContext {

    private double[] portfolioPercent;
    private Map<Integer, List<Double>> portfolio;
    private List<Double> sip;
    private List<Double> updatedInvestment;
    private List<Double> investment;
    private int count;
    public List<Double> getSip() {
        return sip;
    }

    public double[] getPortfolioPercent() {
        return portfolioPercent;
    }

    public void setPortfolioPercent(double[] portfolioPercent) {
        this.portfolioPercent = portfolioPercent;
    }

    public Map<Integer, List<Double>> getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Map<Integer, List<Double>> portfolio) {
        this.portfolio = portfolio;
    }


    public void setSip(List<Double> sip) {
        this.sip = sip;
    }

    public List<Double> getUpdatedInvestment() {
        return updatedInvestment;
    }

    public void setUpdatedInvestment(List<Double> updatedInvestment) {
        this.updatedInvestment = updatedInvestment;
    }

    public List<Double> getInvestment() {
        return investment;
    }

    public void setInvestment(List<Double> investment) {
        this.investment = investment;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
