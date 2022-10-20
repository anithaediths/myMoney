package com.example.geektrust.helper;

import com.example.geektrust.helper.MoneyUtility;
import com.example.geektrust.model.TransactionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

class TestMoneyUtility {
    TransactionContext transactionContext = new TransactionContext();

    @BeforeEach
    void setup() {
        transactionContext.setInvestment(new LinkedList<>());
        transactionContext.setUpdatedInvestment(new LinkedList<>());
        transactionContext.setSip(new LinkedList<>());
        LinkedHashMap<Integer, List<Double>> portfolio = new LinkedHashMap<>();
        portfolio.put(0, Arrays.asList(6000.0, 3000.0, 1000.0));
        portfolio.put(1, Arrays.asList(6000.0, 3000.0, 1000.0));
        transactionContext.setPortfolio(portfolio);
        transactionContext.setPortfolioPercent(new double[3]);
        transactionContext.setCount(2);
    }

    @Test
    void testGetUpdatedPortfolioSIPAssetAmountFlr() {
        double updatedPortfolioSIPAssetAmountFlr = MoneyUtility.getUpdatedPortfolioSIPAssetAmountFlr(4.00, 2000);
        Assertions.assertEquals(2080.0,updatedPortfolioSIPAssetAmountFlr  );
    }

    @Test
    void testPrintRebalance() {
        Assertions.assertDoesNotThrow(() ->MoneyUtility.printRebalance(transactionContext));
    }

    @Test
    void testCalculatePercent() {
        List<Double> investments = Arrays.asList(10.5, 20.2);
        double[] portfolioPercent = MoneyUtility.calculatePortfolioPercent(transactionContext, investments, 1000);
        Assertions.assertEquals(3,portfolioPercent.length  );
        Assertions.assertEquals(0.0105,portfolioPercent[0]  );
    }
}
