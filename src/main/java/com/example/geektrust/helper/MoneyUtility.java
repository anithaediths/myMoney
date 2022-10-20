package com.example.geektrust.helper;

import com.example.geektrust.model.TransactionContext;

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

    public static void printRebalance(TransactionContext transactionContext) {
        Map<Integer, List<Double>> portfolio = transactionContext.getPortfolio();
        double[] portfolioPercent = transactionContext.getPortfolioPercent();
        List<Double> updatedInvestment = transactionContext.getUpdatedInvestment();
        int count = transactionContext.getCount();

        double totalAmount;
        List<Double> currentPortfolio;

        StringBuilder stringBuilder = new StringBuilder();

        currentPortfolio = portfolio.get(count - 1);

        totalAmount = currentPortfolio.get(currentPortfolio.size() - Constants.ONE);

        for (double portfolioPct : portfolioPercent) {
            updatedInvestment.add(portfolioPct * totalAmount);
            Double totalPortfolioAssetAmount = portfolioPct * totalAmount;

            stringBuilder.append((Math.round(Math.floor(totalPortfolioAssetAmount))));
            stringBuilder.append(Constants.SPACE);
        }

        updatedInvestment.add(totalAmount);
        portfolio.put(count - Constants.ONE, updatedInvestment);

        transactionContext.setPortfolio(portfolio);
        transactionContext.setUpdatedInvestment(updatedInvestment);

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
