package net.megavex.scoreboardlibrary.internal.nms.v1_18_R2.sidebar;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.internal.nms.base.util.UnsafeUtilities;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R2.NMSImpl;
import net.megavex.scoreboardlibrary.internal.nms.v1_18_R2.util.NativeAdventureUtil;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Sidebar implementation for PaperMC, using its native Adventure support to make performance better
 */
public class PaperSidebarNMSImpl extends AbstractSidebarImpl {

  private ClientboundSetObjectivePacket createPacket;
  private ClientboundSetObjectivePacket updatePacket;

  public PaperSidebarNMSImpl(NMSImpl impl, Sidebar sidebar) {
    super(impl, sidebar);
  }

  private void initialisePackets() {
    if (createPacket == null || updatePacket == null) {
      synchronized (this) {
        if (createPacket == null || updatePacket == null) {
          createPacket = objectivePacketConstructor.invoke();
          updatePacket = objectivePacketConstructor.invoke();
          createObjectivePacket(createPacket, 0);
          createObjectivePacket(updatePacket, 2);
          updateTitle(sidebar.title());
        }
      }
    }
  }

  private void updateDisplayName(ClientboundSetObjectivePacket packet, net.minecraft.network.chat.Component displayName) {
    UnsafeUtilities.setField(objectiveDisplayNameField, packet, displayName);
  }

  @Override
  public void updateTitle(Component displayName) {
    if (createPacket != null && updatePacket != null) {
      net.minecraft.network.chat.Component vanilla = NativeAdventureUtil.fromAdventureComponent(displayName);
      updateDisplayName(createPacket, vanilla);
      updateDisplayName(updatePacket, vanilla);
    }
  }

  @Override
  protected void sendObjectivePacket(Collection<Player> players, boolean create) {
    initialisePackets();
    impl.sendPacket(players, create ? createPacket : updatePacket);
  }
}