package com.MyAccounts;

/**
 * Created by bennettzhou on 20/04/2017.
 */
public class _links
{
    private Detail detail;

    private Self self;

    public Detail getDetail ()
    {
        return detail;
    }

    public void setDetail (Detail detail)
    {
        this.detail = detail;
    }

    public Self getSelf ()
    {
        return self;
    }

    public void setSelf (Self self)
    {
        this.self = self;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [detail = "+detail+", self = "+self+"]";
    }
}
