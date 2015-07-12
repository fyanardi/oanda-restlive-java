package com.yanardi.oanda.data;

public class Account {
    private String accountId;
    private String accountName;
    private String accountCurrency;
    private String marginRate;

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

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public String getMarginRate() {
        return marginRate;
    }

    public void setMarginRate(String marginRate) {
        this.marginRate = marginRate;
    }
}
