package com.example.geektrust;

import com.example.geektrust.command.Command;
import com.example.geektrust.model.TransactionContext;
import com.example.geektrust.processor.IMoneyProcessor;
import com.example.geektrust.processor.MoneyProcessor;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class CommandProcessor {
    TransactionContext transactionContext;
    IMoneyProcessor iMoneyProcessor;
    public CommandProcessor() {
         transactionContext = new TransactionContext();
         iMoneyProcessor = new MoneyProcessor();
    }


    void readAndProcessCommand(List<String> months, List<String> lines) {
        transactionContext.setInvestment(new LinkedList<>());
        transactionContext.setUpdatedInvestment(new LinkedList<>());
        transactionContext.setSip(new LinkedList<>());
        transactionContext.setPortfolio(new LinkedHashMap<>());
        transactionContext.setPortfolioPercent(new double[3]);

        for (String line : lines) {
            String[] instructions = line.trim().split(" ");

            Command command = Command.valueOf(instructions[0]);

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
                    iMoneyProcessor.printBalance(transactionContext, months.indexOf(instructions[1]));
                    break;
                case REBALANCE:
                    iMoneyProcessor.rebalance(transactionContext);
                    break;

            }
        }
    }




}
