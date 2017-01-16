package fortyrunner;


import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

/**
 * Annotated class that matches the CSV format of our House Prices file
 */
@CsvRecord(separator = ",", crlf = "UNIX", skipFirstLine = true)
public class HouseInfo implements Serializable {

  @DataField(pos = 1)
  private String date;

  @DataField(pos = 2)
  private String name;

  @DataField(name = "Average_Price_SA", pos = 3)
  @QuerySqlField(index = true)
  private double price;

  public double getPrice() {
    return this.price;
  }

  public String getName() {
    return name;
  }

  public String getDate() {
    return this.date;
  }

  @Override
  public String toString() {
    return "HouseInfo " +
      "name='" + this.name + '\'' +
      ", date=" + this.date +
      ", price=" + this.price +
      '}';
  }

  public String toCSV() {
    return this.name + "," + this.date + "," + this.price;
  }


  public String getKey() {
    return toCSV();
  }
}
