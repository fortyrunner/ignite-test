package fortyrunner;


import org.apache.camel.builder.*;
import org.apache.camel.model.dataformat.*;
import org.apache.ignite.*;

/**
 * Setup routes to
 * <ol>
 * <li>watch for files in src/data</li>
 * <li>process CSV and XML on separate SEDA routes</li>
 * <li>Recombine output of CSV and XML parsing onto a new queue</li>
 * <li>Implement (non-transactional) persistence by multi-casting the output to (dummy) end points </li>
 * </ol>
 */
public class DataFlow extends RouteBuilder {

  private final Ignite ignite;

  public DataFlow(final Ignite ignite) {
    this.ignite = ignite;
  }

  /**
   * Let's configure the Camel routing rules using Java code...
   */
  public void configure() {


    CsvDataFormat csv = new CsvDataFormat();
    csv.setSkipHeaderRecord(true);

    // here is a sample which processes the input files
    // (leaving them in place - see the 'noop' flag)
    from("file:src/data?noop=true").id("A. File Loader and Router.").
      log("Loaded ${file:name}").
      choice().
      when(header("CamelFileName").
        endsWith("csv")).to("seda:csv").
      end();

    // Separate queue to process CSV file (Note Different thread)
    // bindy is an addin that processes any type of formatted file
    // CSV is very easy

    from("seda:csv").id("B. CSV Parser").log("Parsing CSV").
      unmarshal().
      bindy(BindyType.Csv, HouseInfo.class).
      process(new CsvProcessor()).
      to("seda:persist");


    // Finally, move the processed files onto a queue that handles
    // persistence
    // TODO - Add routes for database

    from("seda:persist").id("D. Replicator").
      log("Replicate to Database and cache").
      multicast().
      to("seda:ignite");


    from("seda:ignite").process(new IgniteProcessor(this.ignite)).to("seda:check-cache");

    from("seda:check-cache").process(new IgniteReader(this.ignite)).end();


  }
}
