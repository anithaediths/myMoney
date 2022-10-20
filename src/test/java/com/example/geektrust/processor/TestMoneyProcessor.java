package com.example.geektrust.processor;

import com.example.geektrust.model.TransactionContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

class TestMoneyProcessor {

    TransactionContext transactionContext = new TransactionContext();
    MoneyProcessor moneyProcessor;

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
        moneyProcessor = new MoneyProcessor();
    }


    @Test
    void testAllocateMoney() {
        String instructions = "ALLOCATE 6000 3000 1000";

        moneyProcessor.allocateMoney(transactionContext, instructions.split(" "));
        Assertions.assertEquals(3,transactionContext.getCount()  );
        Assertions.assertEquals(0.3,transactionContext.getPortfolioPercent()[1]  );
        Assertions.assertEquals(3000.0,(double) transactionContext.getPortfolio().get(0).get(1)  );

    }

    @Test
    void testProcessSIP() {
        String instructions = "SIP 2000 1000 500";

        moneyProcessor.processSIP(transactionContext, instructions.split(" "));
        Assertions.assertEquals(2,transactionContext.getCount()  );
        Assertions.assertEquals(2000.0, (double) transactionContext.getSip().get(0)  );
        Assertions.assertEquals(1000.0, (double) transactionContext.getSip().get(1)  );


    }

    @Test
    void testChangeGains() {
        String instructions = "SIP 2000 1000 500";

        moneyProcessor.processSIP(transactionContext, instructions.split(" "));
        String instructionsChangeGains = "CHANGE 4.00% 10.00% 2.00% JANUARY";

        moneyProcessor.changeGains(transactionContext, instructionsChangeGains.split(" "));
        Assertions.assertEquals(3,transactionContext.getCount()  );
        Assertions.assertEquals(3000.0, (double) transactionContext.getPortfolio().get(0).get(1)  );
        Assertions.assertEquals(1000.0, (double) transactionContext.getPortfolio().get(1).get(2)  );

    }

    @Test
    void testPrintBalance() {
        moneyProcessor.printBalance(transactionContext, 0);
        Assertions.assertEquals(2,transactionContext.getCount()  );
    }

    @Test
    void testRebalance() {
        moneyProcessor.rebalance(transactionContext);
        Assertions.assertEquals(2,transactionContext.getCount()  );
    }
}
