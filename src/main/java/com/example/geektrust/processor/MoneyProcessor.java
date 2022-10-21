package com.example.geektrust.processor;

import com.example.geektrust.helper.Constants;
import com.example.geektrust.helper.MoneyUtility;
import com.example.geektrust.model.Months;
import com.example.geektrust.model.TransactionContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoneyProcessor implements IMoneyProcessor {
    private static final String REGEX_PORTFOLIO_PERCENTAGE = "^-?\\d+\\.?\\d+";
    private static final int SIX = 6;
    private static final int TWELVE = 12;
    private static final String CANNOTREBALANCE = "CANNOT_REBALANCE";

    @Override
    public void allocateMoney(TransactionContext transactionContext,
                              String[] instructions) {
        List<Double> investment = transactionContext.getInvestment();
        int count = transactionContext.getCount();
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();

        Arrays.stream(instructions).skip(Constants.ONE).forEach(instruction -> investment.add(Double.parseDouble(instruction)));

        double totalAllocatedAmount = investment.stream().mapToDouble(Double::doubleValue).sum();
        investment.add(totalAllocatedAmount);

        portfolio.put(count, investment);
        transactionContext.setPortfolio(portfolio);
        double[] portfolioPercent = MoneyUtility.calculatePortfolioPercent(transactionContext, investment, totalAllocatedAmount);
        transactionContext.setPortfolioPercent(portfolioPercent);

        count++;
        transactionContext.setCount(count);
    }

    @Override
    public void processSIP(TransactionContext transactionContext, String[] instructions) {
        List<Double> sip = transactionContext.getSip();
        Arrays.stream(instructions).skip(Constants.ONE).forEach(instruction -> sip.add(Double.parseDouble(instruction)));
        transactionContext.setSip(sip);
    }

    @Override
    public void changeGains(TransactionContext transactionContext, String[] instructions) {
        processGains(transactionContext, instructions);

        String currentMonth = instructions[instructions.length - 1];
        if (currentMonth.equals(Months.JUNE.name()) || currentMonth.equals(Months.DECEMBER.name())) {
            MoneyUtility.rebalance(transactionContext);
        }
    }

    private void processGains(TransactionContext transactionContext, String[] instructions) {
        Pattern pattern = Pattern.compile(REGEX_PORTFOLIO_PERCENTAGE);
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        List<Double> sip = transactionContext.getSip();
        int count = transactionContext.getCount();

        List<Double> portfolioValues = portfolio.get(count - Constants.ONE);
        List<Double> investments = new LinkedList<>();

        double total = Constants.ZERO;

        for (int i = Constants.ONE; i < instructions.length - Constants.ONE; i++) {
            Matcher matcher = pattern.matcher(instructions[i]);
            if (matcher.find()) {
                double portfolioIncreasePercentage = Double.parseDouble(matcher.group());
                double recentPortfolioAssetAmount = portfolioValues.get(i - Constants.ONE);
                double updatedPortfolioSIPAssetAmountFlr = MoneyUtility.floorAndRound(MoneyUtility.getUpdatedPortfolioSIPAssetAmountFlr(portfolioIncreasePercentage, recentPortfolioAssetAmount));;

                if (count - Constants.ONE > Constants.ZERO) {
                    double recentPortfolioSIPAssetAmount = recentPortfolioAssetAmount + sip.get(i - Constants.ONE);
                    updatedPortfolioSIPAssetAmountFlr = MoneyUtility.floorAndRound(MoneyUtility.getUpdatedPortfolioSIPAssetAmountFlr(portfolioIncreasePercentage, recentPortfolioSIPAssetAmount));
                }
                investments.add(updatedPortfolioSIPAssetAmountFlr);
                total += MoneyUtility.floorAndRound(updatedPortfolioSIPAssetAmountFlr);
            }
        }
        investments.add(total);
        portfolio.put(count, investments);
        transactionContext.setPortfolio(portfolio);

        count++;
        transactionContext.setCount(count);
    }

    @Override
    public void printBalance(TransactionContext transactionContext, int index) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        StringBuilder sb = new StringBuilder();

        List<Double> portfolioForPrint = portfolio.get(index+1);
        MoneyUtility.printPortfolio(sb, portfolioForPrint);
    }

    @Override
    public void rebalance(TransactionContext transactionContext) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        int size = portfolio.size() - Constants.ONE;
        if (size < SIX) {
            System.out.println(CANNOTREBALANCE);
        } else  {
            StringBuilder stringBuilder = new StringBuilder();
            List<Double> rebalancedPortfolio;
            if (size < TWELVE) {
                rebalancedPortfolio = portfolio.get(SIX);
            } else {
                rebalancedPortfolio = portfolio.get(TWELVE);
            }

            MoneyUtility.printPortfolio(stringBuilder, rebalancedPortfolio);
        }
    }

}
