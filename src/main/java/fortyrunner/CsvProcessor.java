package fortyrunner;

import org.apache.camel.*;

import java.util.*;


public class CsvProcessor implements Processor {

  @Override
  public void process(final Exchange exchange) throws Exception {
    Message message = exchange.getIn();

    List<HouseInfo> body = (List<HouseInfo>) message.getBody();

    OptionalDouble average = body.stream().mapToDouble(HouseInfo::getPrice).average();

    System.out.println(String.format("The CSV file contains %d lines, and the average price is %.2f", body.size(), average.getAsDouble()));

    // Save the average on the message header.. avoids creation of pojo to hold a tuple
    message.setHeader("average-price", average);

  }
}
