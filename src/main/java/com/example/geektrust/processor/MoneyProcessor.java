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
    private static final int DBL_ZERO = 0;
    private static final int SIX = 6;
    private static final int TWELVE = 12;
    private static final String CANNOTREBALANCE = "CANNOT_REBALANCE";

    @Override
    public void allocateMoney(TransactionContext transactionContext,
                              String[] instructions) {
        List<Double> investment = transactionContext.getInvestment();
        int count = transactionContext.getCount();
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();

        Arrays.stream(instructions).skip(Constants.ONE).forEach(instruction -> {
            investment.add(Double.parseDouble(instruction));
        });

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
        Arrays.stream(instructions).skip(Constants.ONE).forEach(instruction -> {
            sip.add(Double.parseDouble(instruction));
        });
        transactionContext.setSip(sip);
    }

    @Override
    public void changeGains(TransactionContext transactionContext, String[] instructions) {
        Pattern pattern = Pattern.compile(REGEX_PORTFOLIO_PERCENTAGE);
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        List<Double> sip = transactionContext.getSip();
        int count = transactionContext.getCount();

        List<Double> portfolioValues = portfolio.get(count - Constants.ONE);
        List<Double> updatedInvestment = new LinkedList<>();

        double total = DBL_ZERO;
        String currentMonth = instructions[instructions.length - 1];

        for (int i = Constants.ONE; i < instructions.length - Constants.ONE; i++) {
            Matcher matcher = pattern.matcher(instructions[i]);
            if (matcher.find()) {
                double portfolioIncreasePercentage = Double.parseDouble(matcher.group());
                double recentPortfolioAssetAmount = portfolioValues.get(i - Constants.ONE);
                double updatedPortfolioSIPAssetAmountFlr;

                if (count - Constants.ONE > Constants.ZERO) {
                    double recentPortfolioSIPAssetAmount = recentPortfolioAssetAmount + sip.get(i - Constants.ONE);
                    updatedPortfolioSIPAssetAmountFlr = Math.round(Math.floor(MoneyUtility.getUpdatedPortfolioSIPAssetAmountFlr(portfolioIncreasePercentage, recentPortfolioSIPAssetAmount)));
                } else {
                    updatedPortfolioSIPAssetAmountFlr = Math.round(Math.floor(MoneyUtility.getUpdatedPortfolioSIPAssetAmountFlr(portfolioIncreasePercentage, recentPortfolioAssetAmount)));
                }
                updatedInvestment.add(updatedPortfolioSIPAssetAmountFlr);
                total += Math.round(Math.floor(updatedPortfolioSIPAssetAmountFlr));
            }
        }
        updatedInvestment.add(total);
        transactionContext.setUpdatedInvestment(updatedInvestment);
        portfolio.put(count, transactionContext.getUpdatedInvestment());
        transactionContext.setPortfolio(portfolio);

        count++;
        transactionContext.setCount(count);

        if (currentMonth.equals(Months.JUNE.name()) || currentMonth.equals(Months.DECEMBER.name())) {
            MoneyUtility.rebalance(transactionContext);
        }

    }

    @Override
    public void printBalance(TransactionContext transactionContext, int index) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        List<Double> monthlyValues = portfolio.get(index + Constants.ONE);
        StringBuilder sb = new StringBuilder();
       // System.out.println(portfolio);

        List<Double> portfolioForPrint = portfolio.get(index+1);
      //  System.out.println("portfolioForPrint "+ portfolioForPrint);
        for (int i = 0; i < portfolioForPrint.size() - 1; i++) {
            sb.append(Math.round(Math.floor(portfolioForPrint.get(i))));
            sb.append(Constants.SPACE);
        }
       /* for (int i = Constants.ZERO; i < monthlyValues.size() - Constants.ONE; i++) {
            sb.append(monthlyValues.get(i).shortValue());
            sb.append(Constants.SPACE);
        }*/
        System.out.println(sb);
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

            for (int i = 0; i < rebalancedPortfolio.size() - 1; i++) {
                stringBuilder.append(Math.round(Math.floor(rebalancedPortfolio.get(i))));
                stringBuilder.append(Constants.SPACE);
            }
            System.out.println(stringBuilder);
        }
    }

}
