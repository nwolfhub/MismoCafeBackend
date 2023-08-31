package org.nwolfhub;

import org.nwolfhub.easycli.Defaults;
import org.nwolfhub.easycli.EasyCLI;
import org.nwolfhub.easycli.model.Level;

public class Main {
    public static EasyCLI cli;
    public static void main(String[] args) {
        cli = new EasyCLI();
        cli.addTemplate(Defaults.loggingTemplate);
        cli.setLevel(Level.Info);
    }
}