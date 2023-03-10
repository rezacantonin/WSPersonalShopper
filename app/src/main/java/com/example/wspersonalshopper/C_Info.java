package com.example.wspersonalshopper;


public class C_Info
{
    public boolean Nasel;
    public C_Item item;
    public String ErrorMsg;
    public boolean ConnectErr;

    public C_Info()
    {
        Nasel=false;
        item=new C_Item();
        ErrorMsg="";
        ConnectErr=false;
    }

}
