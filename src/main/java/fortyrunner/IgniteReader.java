package fortyrunner;

import org.apache.camel.Exchange;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.util.List;

/**
 * Run a few SQL queries against the ignite cache
 */
public class IgniteReader implements org.apache.camel.Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(IgniteReader.class);

  private final Ignite ignite;

  public IgniteReader(final Ignite ignite) {
    this.ignite = ignite;
  }

  @Override
  public void process(final Exchange exchange) throws Exception {
    IgniteCache<AffinityKey<String>, HouseInfo> cache = ignite.getOrCreateCache(MainApp.PRICES);

    String sql = "price > ?";

    long starts = System.currentTimeMillis();
    SqlQuery<AffinityKey<String>, HouseInfo> query = new SqlQuery<AffinityKey<String>, HouseInfo>(HouseInfo.class, sql).setArgs(100000);
    List<Cache.Entry<AffinityKey<String>, HouseInfo>> all = cache.query(query).getAll();

    long took = System.currentTimeMillis() - starts;
    LOGGER.info("It took {} ms to filter {} entries from the cache", took, all.size());

    LOGGER.info("There are {} houses that cost more than 100,000", all.size());

    all.stream().forEach(h -> LOGGER.debug(h.getValue().toCSV()));

    query = new SqlQuery<AffinityKey<String>, HouseInfo>(HouseInfo.class, sql).setArgs(50000);
    all = cache.query(query).getAll();


    LOGGER.info("There are {} houses that cost more than 50,000", all.size());


  }


}
