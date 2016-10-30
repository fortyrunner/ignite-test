package fortyrunner;

import org.apache.camel.*;
import org.apache.ignite.*;
import org.apache.ignite.cache.affinity.*;
import org.apache.ignite.cache.query.*;

import javax.cache.*;
import java.util.*;

/**
 * Run a few SQL queries against the ignite cache
 */
public class IgniteReader implements org.apache.camel.Processor {

  private final Ignite ignite;

  public IgniteReader(final Ignite ignite) {
    this.ignite = ignite;
  }

  @Override
  public void process(final Exchange exchange) throws Exception {
    IgniteCache<AffinityKey<String>, HouseInfo> cache = ignite.getOrCreateCache(MainApp.PRICES);

    System.out.println("\n\n\n");

    String sql = "price > ?";

    long starts = System.currentTimeMillis();
    SqlQuery<AffinityKey<String>, HouseInfo> query = new SqlQuery<AffinityKey<String>, HouseInfo>(HouseInfo.class, sql).setArgs(100000);
    List<Cache.Entry<AffinityKey<String>, HouseInfo>> all = cache.query(query).getAll();

    long took = System.currentTimeMillis() - starts;
    System.out.printf("\nIt took %d ms to filter %d entries from the cache \n", took, all.size());

    System.out.println("There are " + all.size() + " houses that cost more than 100,000\n");
    for (Cache.Entry<AffinityKey<String>, HouseInfo> entry : all) {
      HouseInfo houseInfo = entry.getValue();
      System.out.println(houseInfo.toCSV());
    }

    System.out.println("\n\n\n");

    query = new SqlQuery<AffinityKey<String>, HouseInfo>(HouseInfo.class, sql).setArgs(50000);
    all = cache.query(query).getAll();

    System.out.println("There are " + all.size() + " houses that cost more than 50,000");


  }

}
