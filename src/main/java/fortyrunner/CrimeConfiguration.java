package fortyrunner;

import org.apache.ignite.Ignite;
import org.apache.ignite.configuration.CacheConfiguration;

public class CrimeConfiguration {

  public static final String CRIMES = "crimes";

  public static final int START_SIZE = 1_300_000;

  public static final long LIMIT = 6_000_0000;

  public void init(Ignite ignite) {
    CacheConfiguration<String, Crime> config = new CacheConfiguration<>(CrimeConfiguration.CRIMES);
    config.setIndexedTypes(String.class, Crime.class);
    config.setStartSize(CrimeConfiguration.START_SIZE);
    config.setStoreKeepBinary(true);

    ignite.getOrCreateCache(config);
  }
}
