package fortyrunner;

import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.CacheConfiguration;

public class CrimeConfiguration {

  public static final String CRIMES = "crime";

  public static final long LIMIT = 6_000_000;

  public void init(Ignite ignite) {
    CacheConfiguration<String, Crime> config = new CacheConfiguration<>(CrimeConfiguration.CRIMES);
    config.setIndexedTypes(String.class, Crime.class);
    config.setStoreKeepBinary(true);

    ignite.getOrCreateCache(config);
  }
}
