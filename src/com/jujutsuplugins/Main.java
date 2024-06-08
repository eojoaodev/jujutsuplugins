package com.jujutsuplugins;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.jujutsuplugins.commands.UpdateCommand;

public class Main extends JavaPlugin {

    private static final String ALLOWED_IP = "162.55.3.102";
    private static final int ALLOWED_PORT = 31710;
    private static final String EXTERNAL_IP_SERVICE_URL = "http://checkip.amazonaws.com";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/seu_usuario/seu_repositorio/releases/latest";

    @Override
    public void onEnable() {
        String serverIP = getExternalIP();
        int serverPort = getServerPort();

        if (serverIP == null) {
            Bukkit.getConsoleSender().sendMessage("[JujutsuPlugins] §cNão foi possível obter o license.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!serverIP.equals(ALLOWED_IP) || serverPort != ALLOWED_PORT) {
            Bukkit.getConsoleSender().sendMessage("[JujutsuPlugins] §cInformações para a license. (Adquira sua license em: discord.gg/7aW4AYPR7Y)");
            Bukkit.getConsoleSender().sendMessage("[JujutsuPlugins] §eIP detectado: §f" + serverIP + "§e, Porta detectada: §f" + serverPort);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!isWebConnectionAvailable("https://www.google.com/")) {
            Bukkit.getConsoleSender().sendMessage("[JujutsuPlugins] §cError, não conseguiu conectar com web!");
            return;
        }

        Bukkit.getConsoleSender().sendMessage("[JujutsuPlugins] §aConexão com a web estabelecida com sucesso.");

        Bukkit.getConsoleSender().sendMessage("[JujutsuPlugins] §eA tentar conectar..");
        Bukkit.getConsoleSender().sendMessage("[JujutsuPlugins] §eIP: §f" + serverIP + "§e, Porta: §f" + serverPort);
        Bukkit.getConsoleSender().sendMessage("[JujutsuPlugins] §aConexão estabelecida com sucesso.");

        // Registrar comando de atualização
        getCommand("jujutsu").setExecutor(new UpdateCommand(this));
    }

    @Override
    public void onDisable() {

    }

    private String getExternalIP() {
        try {
            URL url = new URL(EXTERNAL_IP_SERVICE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String externalIP = in.readLine();
            in.close();
            return externalIP;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getServerPort() {
        return getServer().getPort();
    }

    private boolean isWebConnectionAvailable(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUpdateAvailable() {
        try {
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject release = new JSONObject(response.toString());
            String latestVersion = release.getString("tag_name");

            // Comparar com a versão atual do plugin
            String currentVersion = getDescription().getVersion();
            return !currentVersion.equals(latestVersion);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}