package org.infinispan.cdi.test;

import org.infinispan.cdi.Remote;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Arquillian deployment utility class.
 *
 * @author Kevin Pollet <kevin.pollet@serli.com> (C) 2011 SERLI
 */
public final class Deployments {
   /**
    * The base deployment web archive. The CDI extension is packaged as an individual jar.
    */
   public static WebArchive baseDeployment() {
      return ShrinkWrap.create(WebArchive.class, "test.war")
            .addAsWebInfResource(Deployments.class.getResource("/META-INF/beans.xml"), "beans.xml")
            .addAsLibrary(
                  ShrinkWrap.create(JavaArchive.class, "infinispan-cdi-remote.jar")
                        .addPackage(Remote.class.getPackage())
                        .addAsManifestResource(Remote.class.getResource("/META-INF/beans.xml"), "beans.xml")
                        .addAsManifestResource(Remote.class.getResource("/META-INF/services/javax.enterprise.inject.spi.Extension"), "services/javax.enterprise.inject.spi.Extension")
            );
   }
}
