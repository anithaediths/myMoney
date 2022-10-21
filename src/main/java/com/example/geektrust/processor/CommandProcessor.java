package com.example.geektrust.processor;

import com.example.geektrust.command.Command;
import com.example.geektrust.helper.Constants;
import com.example.geektrust.model.TransactionContext;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class CommandProcessor {
    TransactionContext transactionContext;
    IMoneyProcessor iMoneyProcessor;
    private static final int THREE = 3;

    public CommandProcessor() {
         transactionContext = new TransactionContext();
         iMoneyProcessor = new MoneyProcessor();
    }

    public void readAndProcessCommand(List<String> months, List<String> lines) {
        initializeTransactionContext();

        for (String line : lines) {
            String[] instructions = line.trim().split(Constants.SPACE);

            Command command = Command.valueOf(instructions[Constants.ZERO]);

            switch (command) {
                case ALLOCATE:
                    iMoneyProcessor.allocateMoney(transactionContext, instructions);
                    break;
                case SIP:
                    iMoneyProcessor.processSIP(transactionContext, instructions);
                    break;
                case CHANGE:
                    iMoneyProcessor.changeGains(transactionContext, instructions);
                    break;
                case BALANCE:
                    iMoneyProcessor.printBalance(transactionContext, months.indexOf(instructions[Constants.ONE]));
                    break;
                case REBALANCE:
                    iMoneyProcessor.rebalance(transactionContext);
                    break;
            }
        }
    }

    private void initializeTransactionContext() {
        transactionContext.setInvestment(new LinkedList<>());
        transactionContext.setSip(new LinkedList<>());
        transactionContext.setPortfolio(new LinkedHashMap<>());
        transactionContext.setPortfolioPercent(new double[THREE]);
    }
}
