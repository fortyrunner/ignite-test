package fortyrunner;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.main.Main;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Arrays;


/**
 * Make sure that you setup IGNITE_HOME etc before starting this test
 * <p>
 * Using the command line
 * <p>
 * ignite.sh /Users/john/ignite/examples/config/example-cache.xml
 * <p>
 * Start a number of nodes - 4 is a good number for 6mi records.
 * <p>
 * You will need to adjust the default heap size of the nodes - change this in ignite.sh 2G is a good size.
 */
public class MainApp {

  static final String CRIMES = "crimes";

  private static final int START_SIZE = 1_300_000;

  private static final long LIMIT = 6_000_0000;

  public static void main(String... args) throws Exception {


    IgniteConfiguration cfg = getIgniteConfiguration();
    Ignite ignite = Ignition.start(cfg);

    CacheConfiguration<String, Crime> config = new CacheConfiguration<>(CRIMES);
    config.setIndexedTypes(String.class, Crime.class);
    config.setStartSize(START_SIZE);
    config.setStoreKeepBinary(true);

    ignite.getOrCreateCache(config);

    Main main = new Main();
    main.addRouteBuilder(new DataFlow(ignite, LIMIT));
    main.getOrCreateCamelContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());
    main.run();


  }

  private static IgniteConfiguration getIgniteConfiguration() {

    TcpDiscoverySpi spi = new TcpDiscoverySpi();

    // create a new instance of tcp discovery multicast ip finder
    TcpDiscoveryMulticastIpFinder tcMp = new TcpDiscoveryMulticastIpFinder();
    tcMp.setAddresses(Arrays.asList("localhost")); // change your IP address here

    // set the multi cast ip finder for spi
    spi.setIpFinder(tcMp);


    // Pure client mode - this client will not act as a node

    IgniteConfiguration cfg = new IgniteConfiguration();
    cfg.setClientMode(true);

    // set the discovery spi to ignite configuration
    cfg.setDiscoverySpi(spi);

    return cfg;
  }

}

