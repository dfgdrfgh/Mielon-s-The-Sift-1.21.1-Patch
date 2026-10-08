package mielon.thesift.client;

import mielon.thesift.client.loading.SiftClientPortalTransition;
import mielon.thesift.network.RiftLoadingPayload;

public final class RiftClientInitializer {
   public static void initialize() {
      RiftLoadingPayload.clientHandler = SiftClientPortalTransition::handle;
   }
}
