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
    public String LokacePozn;
    public int SarzeId;
    public String Sarze;
    public double Stav;
    public String StavStr;
    public int ZboziId;
    // doklady -
    public double Zvazeno; // u balicku je zde posledni zapsane mnozstvi, nebo nactene mnozstvi pri nacteni
    public int PolozkaId;
    public boolean Skryt;
    public boolean Smaz;
    //
    public int LokaceId;
    public String LokaceKod;
    //
    public boolean Vyprodej;
    public boolean ZakazObjednani;
    public boolean Naktivni;
    public int DodavatelId;
    public double NcPosledni;
    public int TemaId;
    public int SkupinaId;
    //
    public String HlavniDodavatel;
    public double PrumProdej;
    public double MinOdber;
    //
    public double MnozVaha;

    public boolean ShowEditMnoz;

    public C_Item() {
        Clear();
    }

    public C_Item(int id, String ean, String kod, String nazev, int velikostId, String velikostNazev, int barvaId, String barvaNazev,
                  int delkaId, String delkaNazev, int rozmerId, String rozmerNazev, double cena, Double mnozstvi, int lokaceId, String lokaceKod, int zboziId,
                  double prumProdej, double minOdber) {
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
        LokacePozn = "";
        SarzeId = 0;
        Sarze = "";
        Stav = 0;
        StavStr = "";
        ZboziId = zboziId;
        Zvazeno = 0;
        PolozkaId = 0;
        Skryt = false;
        Smaz = false;
        LokaceId = lokaceId;
        LokaceKod = lokaceKod;
        Vyprodej = false;
        ZakazObjednani = false;
        Naktivni = false;
        DodavatelId = 0;
        NcPosledni = 0;
        TemaId=0;
        SkupinaId=0;
        HlavniDodavatel = "";
        PrumProdej = prumProdej;
        MinOdber = minOdber;
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
        LokacePozn = item.LokacePozn;
        SarzeId = item.SarzeId;
        Sarze = item.Sarze;
        Stav = item.Stav;
        StavStr = item.StavStr;
        ZboziId = item.ZboziId;
        Zvazeno = item.Zvazeno;
        PolozkaId = item.PolozkaId;
        Skryt = item.Skryt;
        Smaz = item.Smaz;
        LokaceId = item.LokaceId;
        LokaceKod = item.LokaceKod;
        Vyprodej = item.Vyprodej;
        ZakazObjednani = item.ZakazObjednani;
        Naktivni = item.Naktivni;
        DodavatelId = item.DodavatelId;
        NcPosledni = item.NcPosledni;
        TemaId = item.TemaId;
        SkupinaId = item.SkupinaId;
        HlavniDodavatel = item.HlavniDodavatel;
        PrumProdej = item.PrumProdej;
        MinOdber = item.MinOdber;
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
        LokacePozn = "";
        SarzeId = 0;
        Sarze = "";
        Stav = 0;
        StavStr = "";
        ZboziId = 0;
        Zvazeno = 0;
        PolozkaId = 0;
        Skryt = false;
        Smaz = false;
        LokaceId = 0;
        LokaceKod = "";
        Vyprodej = false;
        ZakazObjednani = false;
        Naktivni = false;
        DodavatelId = 0;
        NcPosledni = 0;
        TemaId = 0;
        SkupinaId = 0;
        HlavniDodavatel = "";
        PrumProdej = 0;
        MinOdber = 0;
        MnozVaha=0;
        ShowEditMnoz=false;
    }

}

