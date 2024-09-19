import duke.*;
import org.apache.commons.csv.*;

import java.io.File;

public class WhichCountriesExport {

    public static void listExporters(CSVParser parser, String exportOfInterest) {
        for (CSVRecord record : parser) {
            String exports = record.get("Exports");
            if (exports.contains(exportOfInterest)) {
                System.out.println(record.get("Country"));
            }
        }
    }

    public void whoExportsCoffee() {
        FileResource fr = new FileResource();
        CSVParser csv = fr.getCSVParser();
        listExporters(csv, "coffee");
    }

    public static String countryInfo(CSVParser parser, String country) {
        StringBuilder sb = new StringBuilder();
        for (CSVRecord record : parser) {
            final var countryName = record.get("Country");
            final var exports = record.get("Exports");
            final var value = record.get("Value (dollars)");
            if (countryName.equals(country)) {
                sb.append(countryName + ": ");
                sb.append(exports + ": ");
                sb.append(value);
                return sb.toString();
            }
        }

        return "NOT FOUND";
    }

    public static void listExportersTwoProducts(CSVParser parser, String exportItem_1, String exportItem_2) {
        for (CSVRecord record : parser) {
            String exports = record.get("Exports");
            if (exports.contains(exportItem_1) && exports.contains(exportItem_2)) {
                System.out.println(record.get("Country"));
            }
        }
    }

    public static int numberOfExporters(CSVParser parser, String exportItem) {
        int count = 0;
        for (CSVRecord record : parser) {
            String exports = record.get("Exports");
            if (exports.contains(exportItem)) {
               count++;
            }
        }
        return count;
    }

    public static void bigExporters(CSVParser parser, String amount$) {
        int referenceStringLength = amount$.length();
        for (CSVRecord record : parser) {
            String exportsValue = record.get("Value (dollars)");
            if (exportsValue.length() > referenceStringLength) {
                System.out.println(record.get("Country") + " " + exportsValue);
            }
        }
    }

    public static void main(String[] args) {
        FileResource fr = new FileResource(new File("src\\main\\resources\\exports\\exportdata.csv"));
        CSVParser csv = fr.getCSVParser();

//        listExportersTwoProducts(csv, "gold", "diamonds");
//        listExporters(csv, "gold");
//        System.out.println(numberOfExporters(csv, "gold"));
//        System.out.println(countryInfo(csv, "Nauru"));
        bigExporters(csv, "$999,999,999,999");
    }
}
