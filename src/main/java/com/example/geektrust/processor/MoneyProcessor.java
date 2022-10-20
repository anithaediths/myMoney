package com.example.geektrust.processor;

import com.example.geektrust.model.TransactionContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyProcessor implements IMoneyProcessor {
    private static final String REGEX_PORTFOLIO_PERCENTAGE = "^-?\\d+\\.?\\d+";
    private static final int ZERO = 0;
    private static final int DBL_ZERO = 0;
    private static final int HUNDRED = 100;
    private static final int SIX = 6;

    private static final int ONE = 1;
    private static final String SPACE = " ";
    private static final String CANNOTREBALANCE = "CANNOT_REBALANCE";


    @Override
    public void allocateMoney(TransactionContext transactionContext,
                              String[] instructions) {
        double totalAllocatedAmount = DBL_ZERO;
        double allocatedAmount;

        List<Double> investment = transactionContext.getInvestment();
        int count = transactionContext.getCount();
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();

        for (int i = ONE; i < instructions.length; i++) {
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


    @Override
    public void processSIP(TransactionContext transactionContext, String[] instructions) {
        List<Double> sip = transactionContext.getSip();
        for (int i = ONE; i < instructions.length; i++) {
            sip.add(Double.parseDouble(instructions[i]));
        }
        transactionContext.setSip(sip);
    }

    private void calculatePercent(TransactionContext transactionContext, List<Double> investment, double total) {
        double[] portfolioPercent = transactionContext.getPortfolioPercent();
        for (int i = ZERO; i < investment.size() - ONE; i++) {
            portfolioPercent[i] = investment.get(i) / total;
        }
        transactionContext.setPortfolioPercent(portfolioPercent);
    }

    @Override
    public void changeGains(TransactionContext transactionContext, String[] instructions) {
        Pattern pattern = Pattern.compile(REGEX_PORTFOLIO_PERCENTAGE);
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        List<Double> sip = transactionContext.getSip();
        int count = transactionContext.getCount();

        List<Double> listValues = portfolio.get(count - ONE);
        List<Double> updatedInvestment = new LinkedList<>();

        double total = DBL_ZERO;

        for (int i = ONE; i < instructions.length - ONE; i++) {
            Matcher m = pattern.matcher(instructions[i]);
            if (m.find()) {
                double portfolioIncreasePercentage = Double.parseDouble(m.group());
                double recentPortfolioAssetAmount = listValues.get(i - ONE);
                double updatedPortfolioSIPAssetAmountFlr;

                if (count - ONE > ZERO) {
                    double recentPortfolioSIPAssetAmount = recentPortfolioAssetAmount + sip.get(i - ONE);
                    updatedPortfolioSIPAssetAmountFlr = getUpdatedPortfolioSIPAssetAmountFlr(portfolioIncreasePercentage, recentPortfolioSIPAssetAmount);
                } else {
                    updatedPortfolioSIPAssetAmountFlr = getUpdatedPortfolioSIPAssetAmountFlr(portfolioIncreasePercentage, recentPortfolioAssetAmount);
                }
                updatedInvestment.add(updatedPortfolioSIPAssetAmountFlr);
                total += updatedPortfolioSIPAssetAmountFlr;
            }
        }
        updatedInvestment.add(total);
        portfolio.put(count, updatedInvestment);
        transactionContext.setUpdatedInvestment(updatedInvestment);
        transactionContext.setPortfolio(portfolio);
        count++;
        transactionContext.setCount(count);
    }

    private double getUpdatedPortfolioSIPAssetAmountFlr(double portfolioIncreasePercentage, double recentPortfolioSIPAssetAmount) {
        double recentPortfolioSIPAssetTotalAmountInPct = recentPortfolioSIPAssetAmount * portfolioIncreasePercentage;
        double recentPortfolioSIPAssetTotalAmount = recentPortfolioSIPAssetTotalAmountInPct / HUNDRED;
        double updatedPortfolioSIPAssetAmount = recentPortfolioSIPAssetTotalAmount + recentPortfolioSIPAssetAmount;
        return Math.round(Math.floor(updatedPortfolioSIPAssetAmount));
    }

    public String printBalance(TransactionContext transactionContext, int index) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        List<Double> monthlyValues = portfolio.get(index + ONE);
        StringBuilder sb = new StringBuilder();

        for (int i = ZERO; i < monthlyValues.size() - ONE; i++) {
            sb.append(monthlyValues.get(i).shortValue());
            sb.append(" ");
        }
        System.out.println(sb);
        return sb.toString();
    }


    public void rebalance(TransactionContext transactionContext) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        int size = portfolio.size() - ONE;
        if (size % SIX == ZERO) {
            printRebalance(transactionContext);
        } else {
            System.out.println(CANNOTREBALANCE);
        }
    }


    private void printRebalance(TransactionContext transactionContext) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        double[] portfolioPercent = transactionContext.getPortfolioPercent();
        List<Double> updatedInvestment = transactionContext.getUpdatedInvestment();
        int count = transactionContext.getCount();

        double totalAmount;
        List<Double> currentPortfolio;

        StringBuilder stringBuilder = new StringBuilder();

        currentPortfolio = portfolio.get(count - 1);

        totalAmount = currentPortfolio.get(currentPortfolio.size() - ONE);

        for (double portfolioPct : portfolioPercent) {
            updatedInvestment.add(portfolioPct * totalAmount);
            Double totalPortfolioAssetAmount = portfolioPct * totalAmount;

            stringBuilder.append((Math.round(Math.floor(totalPortfolioAssetAmount))));
            stringBuilder.append(SPACE);
        }

        updatedInvestment.add(totalAmount);
        portfolio.put(count - ONE, updatedInvestment);

        transactionContext.setPortfolio(portfolio);
        transactionContext.setUpdatedInvestment(updatedInvestment);

        System.out.println(stringBuilder);
    }


}
