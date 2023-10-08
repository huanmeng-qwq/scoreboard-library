package net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.team;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.ImmutableTeamProperties;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PacketSender;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.PropertiesPacketType;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.ComponentProvider;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.PacketAccessors;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.modern.util.NativeAdventureUtil;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamConstants;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.team.TeamDisplayPacketAdapter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Locale;

public class PaperTeamsPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl {
  public PaperTeamsPacketAdapterImpl(@NotNull PacketSender<Packet<?>> sender, @NotNull ComponentProvider componentProvider, @NotNull String teamName) {
    super(sender, componentProvider, teamName);
  }

  @Override
  public @NotNull TeamDisplayPacketAdapter createTeamDisplayAdapter(@NotNull ImmutableTeamProperties<Component> properties) {
    return new TeamDisplayPacketAdapterImpl(properties);
  }

  private class TeamDisplayPacketAdapterImpl extends AbstractTeamsPacketAdapterImpl.TeamDisplayPacketAdapterImpl {
    private final ClientboundSetPlayerTeamPacket.Parameters parameters = parametersConstructor.invoke();
    private final ClientboundSetPlayerTeamPacket createPacket = createTeamsPacket(TeamConstants.MODE_CREATE, teamName, parameters, null);
    private final ClientboundSetPlayerTeamPacket updatePacket = createTeamsPacket(TeamConstants.MODE_UPDATE, teamName, parameters, null);
    private Component displayName, prefix, suffix;

    public TeamDisplayPacketAdapterImpl(@NotNull ImmutableTeamProperties<Component> properties) {
      super(properties);
    }

    @Override
    public void updateTeamPackets(@NotNull Collection<String> entries) {
      fillParameters(parameters, null);
      fillTeamPacket(createPacket, entries);
      fillTeamPacket(updatePacket, entries);
    }

    @Override
    public void sendProperties(@NotNull PropertiesPacketType packetType, @NotNull Collection<Player> players) {
      switch (packetType) {
        case CREATE:
          sender.sendPacket(players, createPacket);
          break;
        case UPDATE:
          sender.sendPacket(players, updatePacket);
          break;
      }
    }

    @Override
    protected void fillParameters(ClientboundSetPlayerTeamPacket.@NotNull Parameters parameters, @UnknownNullability Locale locale) {
      super.fillParameters(parameters, locale);

      if (properties.displayName() != displayName) {
        net.minecraft.network.chat.Component vanilla = NativeAdventureUtil.fromAdventureComponent(properties.displayName());
        PacketAccessors.DISPLAY_NAME_FIELD.set(parameters, vanilla);
        displayName = properties.displayName();
      }

      if (properties.prefix() != prefix) {
        net.minecraft.network.chat.Component vanilla = NativeAdventureUtil.fromAdventureComponent(properties.prefix());
        PacketAccessors.PREFIX_FIELD.set(parameters, vanilla);
        prefix = properties.prefix();
      }

      if (properties.suffix() != suffix) {
        net.minecraft.network.chat.Component vanilla = NativeAdventureUtil.fromAdventureComponent(properties.suffix());
        PacketAccessors.SUFFIX_FIELD.set(parameters, vanilla);
        suffix = properties.suffix();
      }
    }
  }
}
