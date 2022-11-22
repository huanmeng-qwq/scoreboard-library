package net.megavex.scoreboardlibrary.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Locale;
import net.megavex.scoreboardlibrary.api.exception.PacketAdapterNotFoundException;
import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface ScoreboardLibrary extends Closeable, HasScoreboardLibrary {
  static @NotNull ScoreboardLibrary loadScoreboardLibrary(@NotNull Plugin plugin) throws PacketAdapterNotFoundException {
    return loadScoreboardLibrary(plugin, false);
  }

  static @NotNull ScoreboardLibrary loadScoreboardLibrary(@NotNull Plugin plugin, boolean debug) throws PacketAdapterNotFoundException {
    Class<?> clazz;
    try {
      clazz = Class.forName("net.megavex.scoreboardlibrary.internal.ScoreboardLibraryImpl");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("scoreboard-library implementation is not shaded into the classpath");
    }

    try {
      return (ScoreboardLibrary) clazz.getDeclaredConstructor(Plugin.class, Boolean.class).newInstance(plugin, debug);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      if (e instanceof InvocationTargetException invocationTargetException) {
        if (invocationTargetException.getTargetException() instanceof PacketAdapterNotFoundException adapterNotFoundException) {
          throw adapterNotFoundException;
        }
      }

      throw new RuntimeException("failed to load scoreboard-library implementation", e);
    }
  }

  static @NotNull ScoreboardLibrary noopScoreboardLibrary(@NotNull Plugin plugin) {
    throw new UnsupportedOperationException("TODO");
  }

  @Override
  default @NotNull ScoreboardLibrary scoreboardLibrary() {
    return this;
  }

  /**
   * Gets the Plugin owner of this ScoreboardManager
   *
   * @return Plugin owner
   */
  @NotNull Plugin plugin();

  /**
   * Creates a {@link Sidebar}
   *
   * @param maxLines Max sidebar lines
   * @return Sidebar
   */
  default @NotNull Sidebar sidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines) {
    return sidebar(maxLines, null);
  }

  /**
   * Creates a {@link Sidebar}
   *
   * @param maxLines Max sidebar lines
   * @param locale   Locale which will be used for translating {@link net.kyori.adventure.text.TranslatableComponent}s
   *                 or null if the locale should depend on the player
   * @return Sidebar
   */
  default @NotNull Sidebar sidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines, @Nullable Locale locale) {
    return sidebar(maxLines, ComponentTranslator.GLOBAL, locale);
  }

  /**
   * Creates a {@link Sidebar}
   *
   * @param maxLines            Max sidebar lines
   * @param componentTranslator Component translator
   * @param locale              Locale which will be used for translating {@link net.kyori.adventure.text.TranslatableComponent}s
   *                            or null if the locale should depend on the player
   * @return Sidebar
   */
  @NotNull Sidebar sidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines, @NotNull ComponentTranslator componentTranslator, @Nullable Locale locale);

  /**
   * Gets the sidebars associated with this ScoreboardManager
   *
   * @return Sidebars
   */
  @NotNull Collection<Sidebar> sidebars();

  /**
   * Creates a {@link TeamManager} with the global component translator
   *
   * @return TeamManager
   */
  default @NotNull TeamManager teamManager() {
    return teamManager(ComponentTranslator.GLOBAL);
  }

  /**
   * Creates a {@link TeamManager}
   *
   * @param componentTranslator Component translator
   * @return TeamManager
   */
  @NotNull TeamManager teamManager(@NotNull ComponentTranslator componentTranslator);

  /**
   * Gets the team managers associated with this {@link JavaPlugin}
   *
   * @return Team Managers
   */
  @NotNull Collection<TeamManager> teamManagers();
}
