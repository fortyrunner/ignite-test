package fortyrunner;

import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CachePeekMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * The data for this is publically available via
 * <p>
 * curl https://data.cityofchicago.org/api/views/ijzp-q8t2/rows.csv?accessType=DOWNLOAD>crimes.csv
 * <p>
 * It is around 1.5GB in size
 */
public class CrimesProcessor implements org.apache.camel.Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CsvProcessor.class);

  private final Ignite ignite;

  public CrimesProcessor(Ignite ignite) {
    this.ignite = ignite;
  }


  @Override
  public void process(Exchange exchange) throws Exception {

    GenericFile body = (GenericFile) exchange.getIn().getBody();
    Path file = Paths.get(body.getAbsoluteFilePath());

    Optional<String> first = Files.lines(file).findFirst();

    LOGGER.info(first.get());

    long starts = System.currentTimeMillis();

    List<Crime> set = Files.lines(file)
      .map(s -> createCrime(s.split(",")))
      .limit(1_000_000)
      .collect(Collectors.toList());

    long took = System.currentTimeMillis() - starts;
    LOGGER.info("Parsed size=" + set.size() + " records in {} ms", took);

    createCache(set);

  }

  private void createCache(List<Crime> crimes) {
    IgniteCache<String, Crime> cache = ignite.getOrCreateCache(MainApp.CRIMES);

    LOGGER.info("Creating Ignite cache");
    long starts = System.currentTimeMillis();

    Map<String, Crime> map = new HashMap<>();
    crimes.forEach(e -> map.put(e.getId(), e));


    try (IgniteDataStreamer<String, Crime> stmr = ignite.dataStreamer(MainApp.CRIMES)) {
      stmr.perNodeBufferSize(2048); // default 1024

      crimes.forEach(e -> stmr.addData(e.getId(), e));

    }

    long took = System.currentTimeMillis() - starts;
    LOGGER.info("Added to Ignite, cache=" + cache.size(CachePeekMode.ALL) + ", took={} ms", took);
  }

  private Crime createCrime(String[] line) {
    Crime c = new Crime();
    c.setId(line[0]);
    c.setCaseNumber(line[1]);
    c.setDate(line[2]);
    c.setPrimaryType(line[5]);
    c.setDescription(line[6]);
    c.setDescriptionLocation(line[7]);
    c.setArrest(line[8]);
    return c;
  }
}
