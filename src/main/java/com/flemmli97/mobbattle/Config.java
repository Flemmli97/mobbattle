package com.flemmli97.mobbattle;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    static final ForgeConfigSpec clientSpec;
    public static final ClientConfig clientConf;

    static final ForgeConfigSpec commonSpec;
    public static final ServerConfig commonConf;

    public static class ClientConfig {

        public final BooleanValue showTeamParticleTypes;

        public ClientConfig(ForgeConfigSpec.Builder builder) {
            showTeamParticleTypes = builder.translation("conf.mobbattle.particle").define("showTeamParticle", true);
        }
    }

    public static class ServerConfig {

        public final BooleanValue autoAddAI;

        public ServerConfig(ForgeConfigSpec.Builder builder) {
            autoAddAI = builder.comment("Auto target mobs from other teams (if e.g. done per command)").translation("conf.mobbattle.addai")
                    .define("autoAddAI", true);
            //builder.pop();
        }
    }

    static {
        Pair<ClientConfig, ForgeConfigSpec> specPair1 = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        clientSpec = specPair1.getRight();
        clientConf = specPair1.getLeft();

        Pair<ServerConfig, ForgeConfigSpec> specPair2 = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        commonSpec = specPair2.getRight();
        commonConf = specPair2.getLeft();
    }
}
