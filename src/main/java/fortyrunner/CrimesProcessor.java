package fortyrunner;

import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFile;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.cache.CachePeekMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(CrimesProcessor.class);

  private final Ignite ignite;

  private final long limit;

  public CrimesProcessor(final Ignite ignite, final long limit) {
    this.ignite = ignite;
    this.limit = limit;
  }


  @Override
  public void process(Exchange exchange) throws Exception {

    Path file = getPath(exchange);

    printHeader(file);

    long starts = System.currentTimeMillis();

    List<Crime> set = Files.lines(file)
      .map(s -> createCrime(s.split(",")))
      .limit(this.limit)
      .collect(Collectors.toList());

    long took = System.currentTimeMillis() - starts;
    LOGGER.info("Parsed size= {} records in {} ms", set.size(), took);

    createCache(set);

  }

  private void printHeader(Path file) throws IOException {
    Optional<String> headerLine = Files.lines(file).findFirst();

    LOGGER.info(headerLine.get());
  }

  private Path getPath(Exchange exchange) {
    GenericFile body = (GenericFile) exchange.getIn().getBody();
    return Paths.get(body.getAbsoluteFilePath());
  }

  private void createCache(List<Crime> crimes) {
    IgniteCache<String, Crime> cache = this.ignite.getOrCreateCache(CrimeConfiguration.CRIMES);

    LOGGER.info("Creating Ignite cache");
    long starts = System.currentTimeMillis();

    Map<String, Crime> map = new HashMap<>();
    crimes.forEach(e -> map.put(e.getId(), e));


    try (IgniteDataStreamer<String, Crime> streamer = this.ignite.dataStreamer(CrimeConfiguration.CRIMES)) {
      streamer.perNodeBufferSize(2048); // default 1024

      crimes.forEach(e -> streamer.addData(e.getId(), e));

    }

    long took = System.currentTimeMillis() - starts;
    LOGGER.info("Added to Ignite, cache={}, took={} ms", cache.size(CachePeekMode.ALL), took);
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
