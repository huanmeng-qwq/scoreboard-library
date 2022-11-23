package net.megavex.scoreboardlibrary.implementation.packetAdapter.base;

import java.util.Collection;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.entity.Player;

public abstract class SidebarPacketAdapter<P, T extends ScoreboardLibraryPacketAdapter<P>> {
  protected final T impl;
  protected final Sidebar sidebar;

  public SidebarPacketAdapter(T impl, Sidebar sidebar) {
    this.impl = impl;
    this.sidebar = sidebar;
  }

  public abstract void updateTitle(Component displayName);

  public void create(Collection<Player> players) {
    sendObjectivePacket(players, true);
  }

  public void update(Collection<Player> players) {
    sendObjectivePacket(players, false);
  }

  protected abstract void sendObjectivePacket(Collection<Player> players, boolean create);

  public abstract void removeLine(Collection<Player> players, String line);

  public abstract void score(Collection<Player> players, int score, String line);
}