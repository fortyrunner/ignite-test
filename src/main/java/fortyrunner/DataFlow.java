package fortyrunner;


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.CsvDataFormat;
import org.apache.ignite.Ignite;

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

  private final long limit;

  public DataFlow(final Ignite ignite, final long limit) {
    this.ignite = ignite;
    this.limit = limit;
  }

  /**
   * Let's configure the Camel routing rules using Java code...
   */
  public void configure() {


    CsvDataFormat csv = new CsvDataFormat();
    csv.setSkipHeaderRecord(true);

    // here is a sample which processes the input files
    // (leaving them in place - see the 'noop' flag)
    from("file:src/data?noop=true").id("A. File Loader and Router.")
      .process(new CrimesProcessor(this.ignite, this.limit))
      .process(new CrimesQuery(this.ignite))
      .end();


  }
}
