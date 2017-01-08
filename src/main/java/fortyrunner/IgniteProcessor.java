package fortyrunner;

import org.apache.camel.*;
import org.apache.ignite.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Using the list of HouseInfo objects - put them in an ignite cache. The key is a simple string that is a concat of the
 * name+date+price. This is unique across the set
 */
public class IgniteProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(IgniteProcessor.class);

  private final Ignite ignite;

  public IgniteProcessor(final Ignite ignite) {
    this.ignite = ignite;
  }

  @Override
  public void process(final Exchange exchange) throws Exception {

    IgniteCache<String, HouseInfo> cache = ignite.getOrCreateCache(MainApp.PRICES);

    List<HouseInfo> list = (List<HouseInfo>) exchange.getIn().getBody();

    long start = System.currentTimeMillis();

    list.stream().forEach(h -> cache.put(h.getKey(), h));

    long took = System.currentTimeMillis() - start;
    LOGGER.info("\nIt took {} ms to write {} entries into the cache \n", took, list.size());

  }

}
