package fortyrunner;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.OptionalDouble;


public class CsvProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CsvProcessor.class);

  @Override
  public void process(final Exchange exchange) throws Exception {
    Message message = exchange.getIn();

    List<HouseInfo> body = (List<HouseInfo>) message.getBody();

    OptionalDouble average = body.stream().mapToDouble(HouseInfo::getPrice).average();

    LOGGER.info("The CSV file contains {} lines, and the average price is {}", body.size(), average.getAsDouble());

    // Save the average on the message header.. avoids creation of pojo to hold a tuple
    message.setHeader("average-price", average);

  }
}
