package com.OCBCObjects.DepositAccounts;

import java.util.ArrayList;

/**
 * Created by bennettzhou on 05/04/2017.
 */
public class SubCategoryList
{
    private ArrayList<Product> product;

    private String description;

    private String subCategoryName;

    public ArrayList<Product> getProduct() {
        return product;
    }

    public void setProduct(ArrayList<Product> product) {
        this.product = product;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getSubCategoryName ()
    {
        return subCategoryName;
    }

    public void setSubCategoryName (String subCategoryName)
    {
        this.subCategoryName = subCategoryName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [product = "+product+", description = "+description+", subCategoryName = "+subCategoryName+"]";
    }
}
