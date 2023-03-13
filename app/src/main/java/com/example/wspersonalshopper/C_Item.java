package com.example.wspersonalshopper;

import java.util.ArrayList;

public class C_Item {
    public int Id;
    public String Ean;
    public String Kod;
    public String Nazev;
    public int VelikostId;
    public String VelikostNazev;
    public int BarvaId;
    public String BarvaNazev;
    public int DelkaId;
    public String DelkaNazev;
    public int RozmerId;
    public String RozmerNazev;
    public double Cena;
    public double Mnozstvi;
    public double Stav;
    public int ZboziId;
    public double MnozVaha;

    public boolean ShowEditMnoz;

    public C_Item() {
        Clear();
    }

    public C_Item(int id, String ean, String kod, String nazev, int velikostId, String velikostNazev, int barvaId, String barvaNazev,
                  int delkaId, String delkaNazev, int rozmerId, String rozmerNazev, double cena, Double mnozstvi, int zboziId)
    {
        Id = id;
        Ean = ean;
        Kod = kod;
        Nazev = nazev;
        VelikostId = velikostId;
        VelikostNazev = velikostNazev;
        BarvaId = barvaId;
        BarvaNazev = barvaNazev;
        DelkaId = delkaId;
        DelkaNazev = delkaNazev;
        RozmerId = rozmerId;
        RozmerNazev = rozmerNazev;
        Cena = cena;
        Mnozstvi = mnozstvi;
        Stav = 0;
        ZboziId = zboziId;
        MnozVaha=0;
        ShowEditMnoz=false;
    }

    public C_Item(final C_Item item) {
        this.CopyFrom(item);
    }

    public void CopyFrom(C_Item item) {
        Id = item.Id;
        Ean = item.Ean;
        Kod = item.Kod;
        Nazev = item.Nazev;
        VelikostId = item.VelikostId;
        VelikostNazev = item.VelikostNazev;
        BarvaId = item.BarvaId;
        BarvaNazev = item.BarvaNazev;
        DelkaId = item.DelkaId;
        DelkaNazev = item.DelkaNazev;
        RozmerId = item.RozmerId;
        RozmerNazev = item.RozmerNazev;
        Cena = item.Cena;
        Mnozstvi = item.Mnozstvi;
        Stav = item.Stav;
        ZboziId = item.ZboziId;
        MnozVaha = item.MnozVaha;
        ShowEditMnoz = item.ShowEditMnoz;
    }

    public void Clear() {
        Id = 0;
        Ean = "";
        Kod = "";
        Nazev = "";
        VelikostId = 0;
        VelikostNazev = "";
        BarvaId = 0;
        BarvaNazev = "";
        DelkaId = 0;
        DelkaNazev = "";
        RozmerId = 0;
        RozmerNazev = "";
        Cena = 0;
        Mnozstvi = 0.0;
        Stav = 0;
        ZboziId = 0;
        MnozVaha=0;
        ShowEditMnoz=false;
    }

}

