package com.MyTransactions;

/**
 * Created by bennettzhou on 26/04/2017.
 */
public class Value
{
    private String amount;

    private String currency;

    public String getAmount ()
    {
        return amount;
    }

    public void setAmount (String amount)
    {
        this.amount = amount;
    }

    public String getCurrency ()
    {
        return currency;
    }

    public void setCurrency (String currency)
    {
        this.currency = currency;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [amount = "+amount+", currency = "+currency+"]";
    }
}
