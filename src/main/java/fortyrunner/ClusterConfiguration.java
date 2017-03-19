package fortyrunner;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Arrays;

public final class ClusterConfiguration {

  public static final boolean CLIENT_MODE = true;

  private ClusterConfiguration() {

  }

  public static IgniteConfiguration getIgniteConfiguration() {

    TcpDiscoverySpi spi = new TcpDiscoverySpi();

    // create a new instance of tcp discovery multicast ip finder
    TcpDiscoveryMulticastIpFinder tcMp = new TcpDiscoveryMulticastIpFinder();
    tcMp.setAddresses(Arrays.asList("localhost")); // change your IP address here

    // set the multi cast ip finder for spi
    spi.setIpFinder(tcMp);

    // Pure client mode - this client will not act as a node

    IgniteConfiguration cfg = new IgniteConfiguration();
    cfg.setClientMode(CLIENT_MODE);

    // set the discovery spi to ignite configuration
    cfg.setDiscoverySpi(spi);

    cfg.setPeerClassLoadingEnabled(true);

    return cfg;
  }
}
