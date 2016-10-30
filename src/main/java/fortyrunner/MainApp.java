package fortyrunner;

import org.apache.camel.component.metrics.routepolicy.*;
import org.apache.camel.main.*;
import org.apache.ignite.*;
import org.apache.ignite.cache.*;
import org.apache.ignite.configuration.*;


public class MainApp {

  static final String PRICES = "prices";

  public static void main(String... args) throws Exception {

    Ignite ignite = Ignition.start();
    CacheConfiguration<String, HouseInfo> config = new CacheConfiguration<>(PRICES);
    config.setCacheMode(CacheMode.PARTITIONED);
    config.setIndexedTypes(String.class, HouseInfo.class);
    ignite.createCache(config);

    Main main = new Main();
    main.addRouteBuilder(new DataFlow(ignite));
    main.getOrCreateCamelContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());
    main.run();


  }

}

