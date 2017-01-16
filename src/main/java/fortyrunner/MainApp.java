package fortyrunner;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.main.Main;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;


public class MainApp {

  static final String PRICES = "prices";

  static final String CRIMES = "crimes";

  public static void main(String... args) throws Exception {


    Ignite ignite = Ignition.start();
    CacheConfiguration<String, HouseInfo> config = new CacheConfiguration<>(PRICES);
    config.setCacheMode(CacheMode.PARTITIONED);
    config.setIndexedTypes(String.class, HouseInfo.class);
    ignite.createCache(config);

    config = new CacheConfiguration<>(CRIMES);
    config.setCacheMode(CacheMode.PARTITIONED);
    config.setIndexedTypes(String.class, Crime.class);
    config.setStartSize(6_300_000);
    config.setStoreKeepBinary(true);

    ignite.createCache(config);

    Main main = new Main();
    main.addRouteBuilder(new DataFlow(ignite));
    main.getOrCreateCamelContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());
    main.run();


  }

}

