package dev.keva.dbutil;

import java.io.File;
import java.util.concurrent.Callable;

import static dev.keva.dbutil.ConvertType.KDB;
import static dev.keva.dbutil.ConvertType.RDB;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

public class ConvertCommand implements Callable<Integer> {

    @Option(names = {"-t", "--type"}, required = true, defaultValue = "KDB",
        description = "Type to convert from, valid values: ${COMPLETION-CANDIDATES}, default: ${DEFAULT-VALUE}")
    ConvertType fromType;

    @Parameters(index = "0")
    File source;
    @Parameters(index = "1")
    File dest;

    @Override
    public Integer call() {
        try {
            if (fromType == KDB) {
                Converter.fromKdbToRdb(source, dest);
            } else if (fromType == RDB) {
                Converter.fromRdbToKdb(source, dest);
            }
            return 0;
        } catch (Exception e) {
            return 69;
        }
    }

}
