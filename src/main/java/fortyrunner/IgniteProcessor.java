package fortyrunner;

import org.apache.camel.*;
import org.apache.ignite.*;

import java.util.*;


/**
 * Using the list of HouseInfo objects - put them in an ignite cache. The key is a simple string that is a concat of the
 * name+date+price. This is unique across the set
 */
public class IgniteProcessor implements Processor {

  private final Ignite ignite;

  public IgniteProcessor(final Ignite ignite) {
    this.ignite = ignite;
  }

  @Override
  public void process(final Exchange exchange) throws Exception {

    IgniteCache<String, HouseInfo> cache = ignite.getOrCreateCache(MainApp.PRICES);

    List<HouseInfo> list = (List<HouseInfo>) exchange.getIn().getBody();

    long start = System.currentTimeMillis();
    for (HouseInfo houseInfo : list) {
      cache.put(houseInfo.getKey(), houseInfo);
    }

    long took = System.currentTimeMillis() - start;
    System.out.printf("\nIt took %d ms to write %d entries into the cache \n", took, list.size());

  }

}
