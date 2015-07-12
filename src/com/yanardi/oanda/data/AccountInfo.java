package com.yanardi.oanda.data;

public class AccountInfo {
    private String accountId;
    private String accountName;
    private double balance;
    private double unrealizedPl;
    private double realizedPl;
    private double marginUsed;
    private double marginAvail;
    private int openTrades;
    private int openOrders;
    private double marginRate;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getUnrealizedPl() {
        return unrealizedPl;
    }

    public void setUnrealizedPl(double unrealizedPl) {
        this.unrealizedPl = unrealizedPl;
    }

    public double getRealizedPl() {
        return realizedPl;
    }

    public void setRealizedPl(double realizedPl) {
        this.realizedPl = realizedPl;
    }

    public double getMarginUsed() {
        return marginUsed;
    }

    public void setMarginUsed(double marginUsed) {
        this.marginUsed = marginUsed;
    }

    public double getMarginAvail() {
        return marginAvail;
    }

    public void setMarginAvail(double marginAvail) {
        this.marginAvail = marginAvail;
    }

    public int getOpenTrades() {
        return openTrades;
    }

    public void setOpenTrades(int openTrades) {
        this.openTrades = openTrades;
    }

    public int getOpenOrders() {
        return openOrders;
    }

    public void setOpenOrders(int openOrders) {
        this.openOrders = openOrders;
    }

    public double getMarginRate() {
        return marginRate;
    }

    public void setMarginRate(double marginRate) {
        this.marginRate = marginRate;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }
    private String accountCurrency;
}
