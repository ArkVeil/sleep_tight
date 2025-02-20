package net.mehvahdjukaar.sleep_tight.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.sleep_tight.SleepTightClient;
import net.mehvahdjukaar.sleep_tight.SleepTightPlatformStuff;
import net.mehvahdjukaar.sleep_tight.core.PlayerSleepData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;

import javax.annotation.Nullable;
import java.util.UUID;

public class ClientBoundSyncPlayerSleepCapMessage implements Message {
    @Nullable
    private final UUID id;
    private final long insomniaElapse;
    private final long sleepTime;
    private final int consecutiveNights;
    private final int homeBedNights;
    private final boolean doubleBed;

    public ClientBoundSyncPlayerSleepCapMessage(FriendlyByteBuf buf) {
        if (buf.readBoolean()) this.id = buf.readUUID();
        else id = null;
        this.insomniaElapse = buf.readLong();
        this.sleepTime = buf.readLong();
        this.consecutiveNights = buf.readInt();
        this.homeBedNights = buf.readInt();
        this.doubleBed = buf.readBoolean();
    }

    public ClientBoundSyncPlayerSleepCapMessage(PlayerSleepData c) {
        this.id = c.getHomeBed();
        this.insomniaElapse = c.getInsomniaWillElapseTimeStamp();
        this.sleepTime = c.getLastWokenUpTimeStamp();
        this.consecutiveNights = c.getConsecutiveNightsSlept();
        this.homeBedNights = c.getNightsSleptInHomeBed();
        this.doubleBed = c.usingDoubleBed();
    }

    public ClientBoundSyncPlayerSleepCapMessage(Player player) {
        this(SleepTightPlatformStuff.getPlayerSleepData(player));
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeBoolean(id != null);
        if (id != null) buf.writeUUID(id);
        buf.writeLong(insomniaElapse);
        buf.writeLong(sleepTime);
        buf.writeInt(consecutiveNights);
        buf.writeInt(homeBedNights);
        buf.writeBoolean(doubleBed);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        Player p = SleepTightClient.getPlayer();
        if(p == null){
            return;
        }
        var c = SleepTightPlatformStuff.getPlayerSleepData(p);
        c.acceptFromServer(this.id, this.insomniaElapse, this.sleepTime, this.consecutiveNights, this.homeBedNights, this.doubleBed);
    }
}
