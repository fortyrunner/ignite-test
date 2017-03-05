package fortyrunner;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.affinity.AffinityKey;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.util.List;

public class SimpleQuery {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleQuery.class);


  public static void main(final String... args) {


    IgniteConfiguration cfg = ClusterConfiguration.getIgniteConfiguration();
    Ignite ignite = Ignition.start(cfg);

    CrimeConfiguration crimes = new CrimeConfiguration();
    crimes.init(ignite);


    IgniteCache<AffinityKey<String>, Crime> crimeCache = ignite.getOrCreateCache(CrimeConfiguration.CRIMES);

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
