package com.example.geektrust.processor;

import com.example.geektrust.model.TransactionContext;

public interface IMoneyProcessor {

    void allocateMoney(TransactionContext transactionContext,
                      String[] instructions);
    void processSIP(TransactionContext transactionContext, String[] instructions);

    void changeGains(TransactionContext transactionContext, String[] instructions);

    String printBalance(TransactionContext transactionContext, int index);

    void rebalance(TransactionContext transactionContext);

}
