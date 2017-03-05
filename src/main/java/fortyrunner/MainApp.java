package fortyrunner;

import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.main.Main;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;


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

  public static void main(String... args) throws Exception {

    IgniteConfiguration cfg = ClusterConfiguration.getIgniteConfiguration();
    Ignite ignite = Ignition.start(cfg);

    CrimeConfiguration crimes = new CrimeConfiguration();
    crimes.init(ignite);


    Main main = new Main();
    main.addRouteBuilder(new DataFlow(ignite, CrimeConfiguration.LIMIT));
    main.getOrCreateCamelContext().addRoutePolicyFactory(new MetricsRoutePolicyFactory());
    main.run();


  }


}

