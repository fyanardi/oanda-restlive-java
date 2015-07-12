package com.yanardi.oanda.sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yanardi.oanda.data.Account;
import com.yanardi.oanda.data.AccountInfo;
import com.yanardi.oanda.rest.AccountType;
import com.yanardi.oanda.rest.OANDARestAPI;

/**
 * Print aggregation of all accounts, grouped by accounts currency 
 *
 * @author Fredy Yanardi
 *
 */
public class PrintAccountAggregation {

    static class AccountSummary {
        double balance;
        double unrealizedPl;
    }

    public static void main(String[] args) {
        // Replace the access token with your access token
        String accessToken = "";
        AccountType accountType = AccountType.FXTRADE_PRACTICE;
        OANDARestAPI oandaRestAPI = new OANDARestAPI(accessToken, accountType);

        printAccountAggregation(oandaRestAPI);
    }

    private static void printAccountAggregation(OANDARestAPI oandaRestAPI) {
        List<Account> accounts = null;
        Map<String, AccountSummary> accountSummaries = new HashMap<String, AccountSummary>();
        try {
            accounts = oandaRestAPI.getAccounts();
            for (Account account : accounts) {
                AccountInfo accountInfo = oandaRestAPI.getAccountInfo(account.getAccountId());
                String currency = accountInfo.getAccountCurrency();
                double balance = accountInfo.getBalance();
                double unrealizedPl = accountInfo.getUnrealizedPl();
                if (!accountSummaries.containsKey(currency)) {
                    accountSummaries.put(currency, new AccountSummary());
                }
                accountSummaries.get(currency).balance += accountInfo.getBalance();
                accountSummaries.get(currency).unrealizedPl += accountInfo.getUnrealizedPl();

                System.out.println("[" + accountInfo.getAccountCurrency() + "] " + accountInfo.getAccountName() +
                        ": balance: " + balance + ", unrealized P/L: " + unrealizedPl);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("=====================================================");
        for (Map.Entry<String, AccountSummary> entry : accountSummaries.entrySet()) {
            System.out.println("Currency: " + entry.getKey());
            System.out.println("\tTotal Balance: " + entry.getValue().balance);
            System.out.println("\tTotal Unrealized P/L: " + entry.getValue().unrealizedPl);
        }
    }

}
