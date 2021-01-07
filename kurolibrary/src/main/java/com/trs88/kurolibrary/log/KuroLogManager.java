package com.trs88.kurolibrary.log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KuroLogManager {
    private KuroLogConfig config;
    private static KuroLogManager instance;
    private List<KuroLogPrinter> printers =new ArrayList<>();//保存打印器

    private KuroLogManager(KuroLogConfig config,KuroLogPrinter[] printers) {
        this.config = config;
        this.printers.addAll(Arrays.asList(printers));
    }

    public static KuroLogManager getInstance() {
        return instance;
    }

    public static void init(@NonNull KuroLogConfig config,KuroLogPrinter... printers){
        instance =new KuroLogManager(config,printers);
    }

    public KuroLogConfig getConfig(){
        return config;
    }

    public void addPrinter(KuroLogPrinter printer){
        printers.add(printer);
    }

    public void removePrinter(KuroLogPrinter printer){
        if (printers !=null){
            printers.remove(printer);
        }
    }

    public List<KuroLogPrinter> getPrinters(){
        return printers;
    }
}
