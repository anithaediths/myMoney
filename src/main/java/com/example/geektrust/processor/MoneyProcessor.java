package com.example.geektrust.processor;

import com.example.geektrust.model.TransactionContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyProcessor implements IMoneyProcessor {
    @Override
    public void allocateMoney(TransactionContext transactionContext,
                              String[] instructions) {
        double totalAllocatedAmount = 0;
        double allocatedAmount = 0;

        List<Double> investment = transactionContext.getInvestment();
        int count = transactionContext.getCount();
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();

        for (int i = 1; i < instructions.length; i++) {
            allocatedAmount = Double.parseDouble(instructions[i]);
            totalAllocatedAmount += allocatedAmount;
            investment.add(allocatedAmount);
        }
        investment.add(totalAllocatedAmount);

        portfolio.put(count, investment);
        transactionContext.setPortfolio(portfolio);
        calculatePercent(transactionContext, investment, totalAllocatedAmount);

        count++;
        transactionContext.setCount(count);
    }

    public void calculatePercent(TransactionContext transactionContext, List<Double> investment, double total) {
        double[] portfolioPercent = transactionContext.getPortfolioPercent();
        for (int i = 0; i < investment.size() - 1; i++) {
            portfolioPercent[i] = investment.get(i) / total;
        }
        transactionContext.setPortfolioPercent(portfolioPercent);
    }

/*
    public void changeGains(TransactionContext transactionContext, String[] instructions) {
        Pattern p = Pattern.compile("^-?\\d+\\.?\\d+");
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();

        List<Double> sip = transactionContext.getSip();
        int count = transactionContext.getCount();

        int previousPortfolioIndex = count - 1;
        List<Double> recentPortfolio = portfolio.get(previousPortfolioIndex);
        List<Double> updatedInvestment = transactionContext.getUpdatedInvestment();
        double totalInvestmentAmount = 0;

        for (int i = 1; i < instructions.length - 1; i++) {
            Matcher m = p.matcher(instructions[i]);
            if (m.find()) {
                double value = Double.parseDouble(m.group());
                double recentPortfolioAssetAmount = recentPortfolio.get(i - 1);

                if (previousPortfolioIndex > 0) {
                    double recentPortfolioSIPAssetAmount = recentPortfolioAssetAmount + sip.get(i - 1);
                    double recentPortfolioSIPAssetTotalAmountInPct = recentPortfolioSIPAssetAmount * value;
                    double recentPortfolioSIPAssetTotalAmount = recentPortfolioSIPAssetTotalAmountInPct / 100;
                    double updatedPortfolioSIPAssetAmount = recentPortfolioSIPAssetTotalAmount + recentPortfolioSIPAssetAmount;
                    updatedInvestment.add(updatedPortfolioSIPAssetAmount);
                    totalInvestmentAmount += updatedPortfolioSIPAssetAmount;
                } else {
                    double recentPortfolioSIPAssetTotalAmountInPct = recentPortfolioAssetAmount * value;
                    double recentPortfolioSIPAssetTotalAmount = recentPortfolioSIPAssetTotalAmountInPct / 100;
                    double updatedPortfolioSIPAssetAmount = recentPortfolioSIPAssetTotalAmount + recentPortfolioAssetAmount;
                    updatedInvestment.add(updatedPortfolioSIPAssetAmount);
                    totalInvestmentAmount += updatedPortfolioSIPAssetAmount;
                }
            }
        }
        updatedInvestment.add(totalInvestmentAmount);
        portfolio.put(count, updatedInvestment);
        transactionContext.setUpdatedInvestment(updatedInvestment);
        transactionContext.setPortfolio(portfolio);
        count++;
        transactionContext.setCount(count);

    }
*/


    public void changeGains(TransactionContext transactionContext, String[] instructions) {
        Pattern p = Pattern.compile("^-?\\d+\\.?\\d+");
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        List<Double> sip = transactionContext.getSip();
        int count = transactionContext.getCount();

        List<Double> listValues = portfolio.get(count - 1);

        List<Double> updatedInvestment = new LinkedList<>();

        double total = 0;

        for (int i = 1; i < instructions.length - 1; i++) {
            Matcher m = p.matcher(instructions[i]);
            if (m.find()) {
                double value = Double.parseDouble(m.group());
                double recentPortfolioAssetAmount = listValues.get(i - 1);

                if (count - 1 > 0) {
                    double recentPortfolioSIPAssetAmount = recentPortfolioAssetAmount + sip.get(i - 1);
                    double recentPortfolioSIPAssetTotalAmountInPct = recentPortfolioSIPAssetAmount * value;
                    double recentPortfolioSIPAssetTotalAmount = recentPortfolioSIPAssetTotalAmountInPct / 100;
                    double updatedPortfolioSIPAssetAmount = recentPortfolioSIPAssetTotalAmount + recentPortfolioSIPAssetAmount;
                    updatedInvestment.add(updatedPortfolioSIPAssetAmount);
                    total += updatedPortfolioSIPAssetAmount;
                } else {
                    double recentPortfolioSIPAssetTotalAmountInPct = recentPortfolioAssetAmount * value;
                    double recentPortfolioSIPAssetTotalAmount = recentPortfolioSIPAssetTotalAmountInPct / 100;
                    double updatedPortfolioSIPAssetAmount = recentPortfolioSIPAssetTotalAmount + recentPortfolioAssetAmount;
                    updatedInvestment.add(updatedPortfolioSIPAssetAmount);
                    total += updatedPortfolioSIPAssetAmount;
                }
            }
        }
        updatedInvestment.add(total);
        portfolio.put(count, updatedInvestment);
        transactionContext.setUpdatedInvestment(updatedInvestment);
        transactionContext.setPortfolio(portfolio);
        count++;
        transactionContext.setCount(count);


        //   return count;
    }

    public String printBalance(TransactionContext transactionContext, int index) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        List<Double> monthlyValues = portfolio.get(index + 1);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < monthlyValues.size() - 1; i++) {
            sb.append(monthlyValues.get(i).shortValue());
            sb.append(" ");
        }
        System.out.println(sb);
       // logger.info(sb.toString());
        return sb.toString();
    }


    public void rebalance(TransactionContext transactionContext) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        int size = portfolio.size() - 1;
        if (size % 6 == 0) {
            printRebalance(transactionContext);
        } else {
            System.out.println("CANNOT_REBALANCE");
        }
    }


    private void printRebalance(TransactionContext transactionContext) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        double[] portfolioPercent = transactionContext.getPortfolioPercent();
        List<Double> updatedInvestment = transactionContext.getUpdatedInvestment();
        int count = transactionContext.getCount();

        double totalAmount;
        List<Double> currentPortfolio;
        Double totalPortfolioAssetAmount;

        StringBuilder sb = new StringBuilder();

        currentPortfolio = portfolio.get(count - 1);

        totalAmount = currentPortfolio.get(currentPortfolio.size() - 1);

        for (double portfolioPct : portfolioPercent) {
            updatedInvestment.add(portfolioPct * totalAmount);
            totalPortfolioAssetAmount = portfolioPct * totalAmount;
            sb.append(totalPortfolioAssetAmount.shortValue());
            sb.append(" ");
        }

        updatedInvestment.add(totalAmount);
        portfolio.put(count - 1, updatedInvestment);

        transactionContext.setPortfolio(portfolio);
        transactionContext.setUpdatedInvestment(updatedInvestment);

        System.out.println(sb);
    }

    public void processSIP(TransactionContext transactionContext, String[] instructions) {
        List<Double> sip = transactionContext.getSip();
        for (int i = 1; i < instructions.length; i++) {
            sip.add(Double.parseDouble(instructions[i]));
        }
        transactionContext.setSip(sip);
    }

}
