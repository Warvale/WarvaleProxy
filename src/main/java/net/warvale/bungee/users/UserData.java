package net.warvale.bungee.users;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.simple.JSONObject;
import net.warvale.bungee.utils.misc.Pair;

import java.util.UUID;

public class UserData {

    private UUID uuid;

    private int currency;
    private boolean playerVisibility;
    private boolean lobbyFlight;

    private JSONObject cases;
    private JSONObject cosmetics;
    private JSONObject minigamesSettings;
    private JSONObject arenaSettings;
    private JSONObject uhcSettings;
    private JSONObject disguiseSettings;
    private JSONObject chatSettings;


    public UserData(UUID uuid, int currency, boolean playerVisibility, boolean lobbyFlight, JSONObject cosmetics,
                    JSONObject cases, JSONObject minigamesSettings, JSONObject arenaSettings, JSONObject uhcSettings,
                    JSONObject disguiseSettings, JSONObject chatSettings) {
        this.uuid = uuid;
        this.currency = currency;
        this.playerVisibility = playerVisibility;
        this.lobbyFlight = lobbyFlight;
        this.cosmetics = cosmetics;
        this.cases = cases;
        this.minigamesSettings = minigamesSettings;
        this.arenaSettings = arenaSettings;
        this.uhcSettings = uhcSettings;
        this.disguiseSettings = disguiseSettings;
        this.chatSettings = chatSettings;

        // Insert default values
        if (!minigamesSettings.containsKey("rating_visibility")) {
            minigamesSettings.put("rating_visibility", true);
        }

        if (!minigamesSettings.containsKey("stats_visibility")) {
            minigamesSettings.put("stats_visibility", true);
        }

        if (!uhcSettings.containsKey("stats_visibility")) {
            uhcSettings.put("stats_visibility", true);
        }

        if (!disguiseSettings.containsKey("is_disguised")) {
            disguiseSettings.put("is_disguised", false);
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public ProxiedPlayer getPlayer() {
        return ProxyServer.getInstance().getPlayer(this.uuid);
    }

    public int getCurrency() {
        return this.currency;
    }

    public void addCurrency(int num) {
        this.currency += num;

        new UserDataUpdate(this, Pair.of("currency", this.currency)).queue();
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public boolean arePlayersVisible() {
        return this.playerVisibility;
    }

    public void setArePlayerVisibile(boolean playerVisibility) {
        this.playerVisibility = playerVisibility;

        new UserDataUpdate(this, Pair.of("player_visibility", this.playerVisibility)).queue();
    }

    public boolean isLobbyFlight() {
        return this.lobbyFlight;
    }

    public void setLobbyFlight(boolean lobbyFlight) {
        this.lobbyFlight = lobbyFlight;

        new UserDataUpdate(this, Pair.of("lobby_flight", this.lobbyFlight)).queue();
    }

    public JSONObject getCases() {
        return cases;
    }

    public void setCases(JSONObject cases, boolean update) {
        this.cases = cases;

        if (update) {
            new UserDataUpdate(this, Pair.of("cases", cases.toJSONString())).queue();
        }
    }

    public JSONObject getCosmetics() {
        return cosmetics;
    }

    public void setCosmetics(JSONObject cosmetics, boolean update) {
        this.cosmetics = cosmetics;

        if (update) {
            /*PGobject jsonWrapper = new PGobject();
            jsonWrapper.setType("json");

            try {
                jsonWrapper.setValue(cosmetics.toJSONString());
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }*/

            new UserDataUpdate(this, Pair.of("cosmetics", cosmetics.toJSONString())).queue();
        }
    }

    public JSONObject getMinigamesSettings() {
        return minigamesSettings;
    }

    public void setMinigamesSettings(JSONObject minigamesSettings, boolean update) {
        this.minigamesSettings = minigamesSettings;

        if (update) {
            /*PGobject jsonWrapper = new PGobject();
            jsonWrapper.setType("json");

            try {
                jsonWrapper.setValue(sgSettings.toJSONString());
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }*/

            new UserDataUpdate(this, Pair.of("minigames_settings", minigamesSettings.toJSONString())).queue();
        }

    }

    public JSONObject getUhcSettings() {
        return uhcSettings;
    }

    public void setUhcSettings(JSONObject uhcSettings, boolean update) {
        this.uhcSettings = uhcSettings;

        if (update) {
            /*PGobject jsonWrapper = new PGobject();
            jsonWrapper.setType("json");

            try {
                jsonWrapper.setValue(disguiseSettings.toJSONString());
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }*/

            new UserDataUpdate(this, Pair.of("uhc_settings", uhcSettings.toJSONString())).queue();
        }
    }

    public JSONObject getArenaSettings() {
        return arenaSettings;
    }

    public void setArenaSettings(JSONObject arenaSettings, boolean update) {
        this.arenaSettings = arenaSettings;

        if (update) {
            /*PGobject jsonWrapper = new PGobject();
            jsonWrapper.setType("json");

            try {
                jsonWrapper.setValue(arenaSettings.toJSONString());
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }*/

            new UserDataUpdate(this, Pair.of("arena_settings", arenaSettings.toJSONString())).queue();
        }
    }


    public JSONObject getDisguiseSettings() {
        return disguiseSettings;
    }

    public void setDisguiseSettings(JSONObject disguiseSettings, boolean update) {
        this.disguiseSettings = disguiseSettings;

        if (update) {
            /*PGobject jsonWrapper = new PGobject();
            jsonWrapper.setType("json");

            try {
                jsonWrapper.setValue(disguiseSettings.toJSONString());
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }*/

            new UserDataUpdate(this, Pair.of("disguise_settings", disguiseSettings.toJSONString())).queue();
        }
    }

    public JSONObject getChatSettings() {
        return chatSettings;
    }

    public void setChatSettings(JSONObject chatSettings, boolean update) {
        this.chatSettings = chatSettings;

        if (update) {
            /*PGobject jsonWrapper = new PGobject();
            jsonWrapper.setType("json");

            try {
                jsonWrapper.setValue(disguiseSettings.toJSONString());
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }*/

            new UserDataUpdate(this, Pair.of("chat_settings", chatSettings.toJSONString())).queue();
        }
    }

}
