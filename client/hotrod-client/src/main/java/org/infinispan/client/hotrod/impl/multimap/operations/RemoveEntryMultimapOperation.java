package org.infinispan.client.hotrod.impl.multimap.operations;

import static org.infinispan.client.hotrod.impl.multimap.protocol.MultimapHotRodConstants.REMOVE_ENTRY_MULTIMAP_REQUEST;

import java.util.concurrent.atomic.AtomicInteger;

import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.impl.protocol.Codec;
import org.infinispan.client.hotrod.impl.protocol.HeaderParams;
import org.infinispan.client.hotrod.impl.protocol.HotRodConstants;
import org.infinispan.client.hotrod.impl.transport.netty.ChannelFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import net.jcip.annotations.Immutable;

/**
 * Implements "remove" for multimap as defined by  <a href="http://community.jboss.org/wiki/HotRodProtocol">Hot Rod
 * protocol specification</a>.
 *
 * @author Katia Aresti, karesti@redhat.com
 * @since 9.2
 */
@Immutable
public class RemoveEntryMultimapOperation extends AbstractMultimapKeyValueOperation<Boolean> {

   public RemoveEntryMultimapOperation(Codec codec, ChannelFactory channelFactory, Object key, byte[] keyBytes, byte[] cacheName, AtomicInteger topologyId, int flags, Configuration cfg, byte[] value) {
      super(codec, channelFactory, key, keyBytes, cacheName, topologyId, flags, cfg, value);
   }

   @Override
   protected void executeOperation(Channel channel) {
      HeaderParams header = headerParams(REMOVE_ENTRY_MULTIMAP_REQUEST);
      scheduleRead(channel, header);
      sendKeyValueOperation(channel, header);
   }

   @Override
   public Boolean decodePayload(ByteBuf buf, short status) {
      if (HotRodConstants.isNotExist(status)) {
         return Boolean.FALSE;
      }

      return buf.readByte() == 1 ? Boolean.TRUE : Boolean.FALSE;
   }
}
