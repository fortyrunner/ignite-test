package fortyrunner;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.SqlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.util.List;

public class CrimesQuery implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CrimesQuery.class);

  private final Ignite ignite;

  public CrimesQuery(Ignite ignite) {
    this.ignite = ignite;
  }

  @Override
  public void process(Exchange exchange) throws Exception {

    IgniteCache<AffinityKey<String>, Crime> crimeCache = ignite.getOrCreateCache(MainApp.CRIMES);

    String sql = "primaryType = ?";

    long starts = System.currentTimeMillis();

    String crimeType = "ASSAULT";
    SqlQuery<AffinityKey<String>, Crime> query = new SqlQuery<AffinityKey<String>, Crime>(Crime.class, sql).setArgs(crimeType);
    List<Cache.Entry<AffinityKey<String>, Crime>> all = crimeCache.query(query).getAll();

    long took = System.currentTimeMillis() - starts;
    LOGGER.info("It took {} ms to filter {} entries from the cache", took, all.size());

    LOGGER.info("There are {} {} crimes", all.size(), crimeType);


  }


}
