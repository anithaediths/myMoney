package com.example.geektrust.processor;

import com.example.geektrust.helper.Constants;
import com.example.geektrust.helper.MoneyUtility;
import com.example.geektrust.model.TransactionContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyProcessor implements IMoneyProcessor {
    private static final String REGEX_PORTFOLIO_PERCENTAGE = "^-?\\d+\\.?\\d+";
    private static final int DBL_ZERO = 0;
    private static final int SIX = 6;
    private static final String CANNOTREBALANCE = "CANNOT_REBALANCE";

    @Override
    public void allocateMoney(TransactionContext transactionContext,
                              String[] instructions) {
        double totalAllocatedAmount = DBL_ZERO;
        double allocatedAmount;

        List<Double> investment = transactionContext.getInvestment();
        int count = transactionContext.getCount();
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();

        for (int i = Constants.ONE; i < instructions.length; i++) {
            allocatedAmount = Double.parseDouble(instructions[i]);
            totalAllocatedAmount += allocatedAmount;
            investment.add(allocatedAmount);
        }
        investment.add(totalAllocatedAmount);

        portfolio.put(count, investment);
        transactionContext.setPortfolio(portfolio);
        double[] portfolioPercent = MoneyUtility.calculatePercent(transactionContext, investment, totalAllocatedAmount);
        transactionContext.setPortfolioPercent(portfolioPercent);

        count++;
        transactionContext.setCount(count);
    }


    @Override
    public void processSIP(TransactionContext transactionContext, String[] instructions) {
        List<Double> sip = transactionContext.getSip();
        for (int i = Constants.ONE; i < instructions.length; i++) {
            sip.add(Double.parseDouble(instructions[i]));
        }
        transactionContext.setSip(sip);
    }


    @Override
    public void changeGains(TransactionContext transactionContext, String[] instructions) {
        Pattern pattern = Pattern.compile(REGEX_PORTFOLIO_PERCENTAGE);
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        List<Double> sip = transactionContext.getSip();
        int count = transactionContext.getCount();

        List<Double> listValues = portfolio.get(count - Constants.ONE);
        List<Double> updatedInvestment = new LinkedList<>();

        double total = DBL_ZERO;

        for (int i = Constants.ONE; i < instructions.length - Constants.ONE; i++) {
            Matcher m = pattern.matcher(instructions[i]);
            if (m.find()) {
                double portfolioIncreasePercentage = Double.parseDouble(m.group());
                double recentPortfolioAssetAmount = listValues.get(i - Constants.ONE);
                double updatedPortfolioSIPAssetAmountFlr;

                if (count - Constants.ONE > Constants.ZERO) {
                    double recentPortfolioSIPAssetAmount = recentPortfolioAssetAmount + sip.get(i - Constants.ONE);
                    updatedPortfolioSIPAssetAmountFlr = MoneyUtility.getUpdatedPortfolioSIPAssetAmountFlr(portfolioIncreasePercentage, recentPortfolioSIPAssetAmount);
                } else {
                    updatedPortfolioSIPAssetAmountFlr = MoneyUtility.getUpdatedPortfolioSIPAssetAmountFlr(portfolioIncreasePercentage, recentPortfolioAssetAmount);
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

    @Override
    public String printBalance(TransactionContext transactionContext, int index) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        List<Double> monthlyValues = portfolio.get(index + Constants.ONE);
        StringBuilder sb = new StringBuilder();

        for (int i = Constants.ZERO; i < monthlyValues.size() - Constants.ONE; i++) {
            sb.append(monthlyValues.get(i).shortValue());
            sb.append(" ");
        }
        System.out.println(sb);
        return sb.toString();
    }

    @Override
    public void rebalance(TransactionContext transactionContext) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        int size = portfolio.size() - Constants.ONE;
        if (size % SIX == Constants.ZERO) {
            MoneyUtility.printRebalance(transactionContext);
        } else {
            System.out.println(CANNOTREBALANCE);
        }
    }

}
