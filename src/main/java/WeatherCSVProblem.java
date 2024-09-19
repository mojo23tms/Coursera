import duke.DirectoryResource;
import duke.FileResource;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;

public class WeatherCSVProblem {

    public static void main(String[] args) {
        testHottestInDay("2015-01-01");
        testHottestInManyDays("2014");
        testColdestInDay("2014-05-01");
        testColdestInManyDays("2013");
        testFileWithColdestTemperature();
        testLowestHumidityInFile();
        testLowestHumidityMultipleFiles();
        testAvgTempInFile();
        testAvgTempWithHumid();
    }

    public static CSVRecord hottestHourInFile(CSVParser parser) {
        CSVRecord largestSoFar = null;

        for (CSVRecord record : parser) {
            CSVRecord current = record;
            largestSoFar = getLargestOfTwo(largestSoFar, current, "TemperatureF");
        }

        return largestSoFar;
    }

    public static CSVRecord coldestHourInFile(CSVParser parser) {
        CSVRecord smallestSoFar = null;

        for (CSVRecord record : parser) {
            CSVRecord current = record;
            if (current.get("TemperatureF").contains("-9999")) {
                continue;
            }
            smallestSoFar = getSmallestOfTwo(smallestSoFar, current, "TemperatureF");
        }

        return smallestSoFar;
    }

    public static CSVRecord hottestInManyDays(String yearDirectory) {
        File dr = new File("src\\main\\resources\\nc_weather\\" + yearDirectory);
        CSVRecord largestSoFar = null;
        for (File file : dr.listFiles()) {
            FileResource fr = new FileResource(file);
            CSVRecord current = hottestHourInFile(fr.getCSVParser());

            largestSoFar = getLargestOfTwo(largestSoFar, current, "TemperatureF");
        }

        return largestSoFar;
    }

    public static CSVRecord coldestInManyDays(String yearDirectory) {
        File dr = new File("src\\main\\resources\\nc_weather\\" + yearDirectory);
        CSVRecord lowestSoFar = null;
        for (File file : dr.listFiles()) {
            FileResource fr = new FileResource(file);
            CSVRecord current = coldestHourInFile(fr.getCSVParser());
            lowestSoFar = getSmallestOfTwo(lowestSoFar, current, "TemperatureF");
        }

        return lowestSoFar;
    }

    public static CSVRecord coldestInManyDays() {
        Iterable<File> dr = new DirectoryResource().selectedFiles();
        CSVRecord lowestSoFar = null;
        for (File file : dr) {
            FileResource fr = new FileResource(file);
            CSVRecord current = coldestHourInFile(fr.getCSVParser());

            lowestSoFar = getSmallestOfTwo(lowestSoFar, current, "TemperatureF");
        }

        return lowestSoFar;
    }

    public static String fileWithColdestTemperature() {
        DirectoryResource dr = new DirectoryResource();
        File lowestTempFile = null;
        CSVParser parser = null;
        for (File file : dr.selectedFiles()) {

            if (lowestTempFile == null) {
                lowestTempFile = file;
            } else {
                parser = new FileResource(file).getCSVParser();
                double currTemp = Double.parseDouble(coldestHourInFile(parser).get("TemperatureF"));
                parser = new FileResource(lowestTempFile).getCSVParser();
                double lowestTemp = Double.parseDouble(coldestHourInFile(parser).get("TemperatureF"));

                if (lowestTemp > currTemp) {
                    lowestTempFile = file;
                    parser = new FileResource(lowestTempFile).getCSVParser();
                }
            }
        }

        String fileName = lowestTempFile.getName();
        System.out.println("Coldest day was in file " + fileName);
        System.out.println("Coldest temperature on that day was " + coldestHourInFile(parser).get("TemperatureF"));
        System.out.println("All the Temperatures on the coldest day were: ");
        parser = new FileResource(lowestTempFile).getCSVParser();
        for (CSVRecord record : parser) {
            System.out.println(record.get("DateUTC") + ": " + record.get("TemperatureF"));
        }
        return fileName;
    }

    public static CSVRecord lowestHumidityInFile(CSVParser parser) {
        CSVRecord lowestHumidity = null;

        for (CSVRecord record : parser) {
            String currentHumidity = record.get("Humidity");
            if (currentHumidity.equals("N/A")) {
                continue;
            }
            lowestHumidity = getSmallestOfTwo(lowestHumidity, record, "Humidity");

        }

        return lowestHumidity;
    }

    public static CSVRecord lowestHumidityInManyFiles() {
        DirectoryResource dr = new DirectoryResource();
        CSVRecord lowestHumidRecord = null;
        CSVParser parser;
        for (File file : dr.selectedFiles()) {
            parser = new FileResource(file).getCSVParser();
            if (lowestHumidRecord == null) {
                lowestHumidRecord = lowestHumidityInFile(parser);
            } else {
                double currHumid = Double.parseDouble(lowestHumidityInFile(parser).get("Humidity"));
                double lowestHumid = Double.parseDouble(lowestHumidRecord.get("Humidity"));

                if (lowestHumid > currHumid) {
                    parser = new FileResource(file).getCSVParser();
                    lowestHumidRecord = lowestHumidityInFile(parser);
                }
            }
        }
        return lowestHumidRecord;
    }

    public static double averageTemperatureInFile(CSVParser parser) {
        return averageTemperatureWithHighHumidity(parser, 0);
    }

    public static double averageTemperatureWithHighHumidity(CSVParser parser, int value) {
        int count = 0;
        double sum = 0;

        for (CSVRecord record : parser) {
            double humid = Double.parseDouble(record.get("Humidity"));
            if (humid >= value) {
                sum += Double.parseDouble(record.get("TemperatureF"));
                count++;
            }
        }

        return sum / count;
    }

    public static CSVRecord getLargestOfTwo(CSVRecord largestSoFar, CSVRecord current, String parameterName) {
        if (largestSoFar == null) {
            largestSoFar = current;
        } else {
            double currTemp = Double.parseDouble(current.get(parameterName));
            double largestTemp = Double.parseDouble(largestSoFar.get(parameterName));

            if (currTemp > largestTemp) {
                largestSoFar = current;
            }
        }

        return largestSoFar;
    }

    public static CSVRecord getSmallestOfTwo(CSVRecord smallestSoFar, CSVRecord current, String parameterName) {
        if (smallestSoFar == null) {
            smallestSoFar = current;
        } else {
            double currTemp = Double.parseDouble(current.get(parameterName));
            double lowestTemp = Double.parseDouble(smallestSoFar.get(parameterName));

            if (currTemp < lowestTemp) {
                smallestSoFar = current;
            }
        }

        return smallestSoFar;
    }


    /**
     * @param date in format YYYY-MM-DD ("2015-12-31")
     */
    public static void testHottestInDay(String date) {
        FileResource fr = new FileResource(new File("src\\main\\resources\\nc_weather\\" + date.substring(0, 4) + "\\weather-" + date + ".csv"));
        CSVRecord largest = hottestHourInFile(fr.getCSVParser());
        System.out.println("Biggest temperature was " + largest.get("TemperatureF") + " at " + largest.get("TimeEST"));
    }

    public static void testColdestInDay(String date) {
        FileResource fr = new FileResource(new File("src\\main\\resources\\nc_weather\\" + date.substring(0, 4) + "\\weather-" + date + ".csv"));
        CSVRecord lowest = coldestHourInFile(fr.getCSVParser());
        System.out.println("Lowest temperature was " + lowest.get("TemperatureF") + " at " + lowest.get(0));
    }

    public static void testHottestInManyDays(String yearDirectory) {
        CSVRecord largest = hottestInManyDays(yearDirectory);
        System.out.println("Biggest temperature was " + largest.get("TemperatureF") + " at " + largest.get(0) + " " + largest.get("DateUTC"));
    }

    public static void testColdestInManyDays(String yearDirectory) {
        CSVRecord coldest = coldestInManyDays(yearDirectory);
        System.out.println("Lowest temperature was " + coldest.get("TemperatureF") + " at " + coldest.get(0) + " " + coldest.get("DateUTC"));
    }

    public static void testFileWithColdestTemperature() {
        String fileName = fileWithColdestTemperature();
    }

    public static void testLowestHumidityInFile() {
        FileResource fr = new FileResource();
        CSVParser parser = fr.getCSVParser();
        CSVRecord csv = lowestHumidityInFile(parser);
        System.out.println("Lowest humidity was " + csv.get("Humidity") + " at " + csv.get("DateUTC"));
    }

    public static void testLowestHumidityMultipleFiles() {
        CSVRecord csv = lowestHumidityInManyFiles();
        System.out.println("Lowest humidity was " + csv.get("Humidity") + " at " + csv.get("DateUTC"));
    }

    public static void testAvgTempInFile() {
        FileResource fr = new FileResource();
        double avgTemp = averageTemperatureInFile(fr.getCSVParser());
        System.out.println("Average temperature in file is " + avgTemp);
    }

    public static void testAvgTempWithHumid() {
        FileResource fr = new FileResource();
        double avgTemp = averageTemperatureWithHighHumidity(fr.getCSVParser(), 80);
        if (!(avgTemp > 0)) {
            System.out.println("No temperatures with that humidity");
        } else {
            System.out.println("Average temperature with high Humidity in file is " + avgTemp);
        }
    }


}
