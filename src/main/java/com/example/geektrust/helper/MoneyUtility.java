package com.example.geektrust.helper;

import com.example.geektrust.model.TransactionContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MoneyUtility {
    private MoneyUtility() {
    }

    private static final int HUNDRED = 100;

    public static double getUpdatedPortfolioSIPAssetAmountFlr(double portfolioIncreasePercentage, double recentPortfolioSIPAssetAmount) {
        double recentPortfolioSIPAssetTotalAmountInPct = recentPortfolioSIPAssetAmount * portfolioIncreasePercentage;
        double recentPortfolioSIPAssetTotalAmount = recentPortfolioSIPAssetTotalAmountInPct / HUNDRED;
        double updatedPortfolioSIPAssetAmount = recentPortfolioSIPAssetTotalAmount + recentPortfolioSIPAssetAmount;
        return Math.round(Math.floor(updatedPortfolioSIPAssetAmount));
    }

    public static void rebalance(TransactionContext transactionContext) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        double[] portfolioPercent = transactionContext.getPortfolioPercent();
        List<Double> updatedInvestment = new LinkedList<>();
        int count = transactionContext.getCount();

        double totalAmount;
        List<Double> currentPortfolio;
        currentPortfolio = portfolio.get(portfolio.size()-1);
        totalAmount = Math.round(Math.floor(currentPortfolio.get(currentPortfolio.size() - Constants.ONE)));

        for (double portfolioPct : portfolioPercent) {
            updatedInvestment.add(Math.floor(portfolioPct * totalAmount));
        }

        updatedInvestment.add(totalAmount);
        portfolio.put(count - Constants.ONE, updatedInvestment);
        transactionContext.setPortfolio(portfolio);
        transactionContext.setUpdatedInvestment(updatedInvestment);
      //  printRebalance(updatedInvestment);
    }

    public static void printRebalance(List<Double> updatedInvestment) {
        StringBuilder stringBuilder = new StringBuilder();
        updatedInvestment.remove(updatedInvestment.size()-1);
        updatedInvestment.forEach(ui -> {
            stringBuilder.append((Math.round(Math.floor(ui))));
            stringBuilder.append(Constants.SPACE);

        });


        System.out.println(stringBuilder);
    }

    public static double[] calculatePortfolioPercent(TransactionContext transactionContext, List<Double> investment, double total) {
        double[] portfolioPercent = transactionContext.getPortfolioPercent();
        for (int i = Constants.ZERO; i < investment.size() - Constants.ONE; i++) {
            portfolioPercent[i] = investment.get(i) / total;
        }
        return portfolioPercent;
    }
}
