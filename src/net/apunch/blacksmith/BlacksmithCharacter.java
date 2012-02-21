package net.apunch.blacksmith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.apunch.blacksmith.util.Settings.Setting;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.trait.Character;
import net.citizensnpcs.api.npc.trait.SaveId;
import net.citizensnpcs.api.util.DataKey;

@SaveId("blacksmith")
public class BlacksmithCharacter extends Character {
    private final Blacksmith plugin;
    private final List<Material> reforgeableItems = new ArrayList<Material>();
    private RepairSession session;

    // Defaults
    private String busyWithPlayerMsg = Setting.BUSY_WITH_PLAYER_MESSAGE.asString();
    private String busyReforgingMsg = Setting.BUSY_WITH_REFORGE_MESSAGE.asString();
    private String costMsg = Setting.COST_MESSAGE.asString();
    private String invalidItemMsg = Setting.INVALID_ITEM_MESSAGE.asString();
    private String startReforgeMsg = Setting.START_REFORGE_MESSAGE.asString();
    private String successMsg = Setting.SUCCESS_MESSAGE.asString();
    private String failMsg = Setting.FAIL_MESSAGE.asString();
    private String insufficientFundsMsg = Setting.INSUFFICIENT_FUNDS_MESSAGE.asString();
    private int minReforgeDelay = Setting.MIN_REFORGE_DELAY.asInt();
    private int maxReforgeDelay = Setting.MAX_REFORGE_DELAY.asInt();
    private int failChance = Setting.FAIL_CHANCE.asInt();

    public BlacksmithCharacter() {
        plugin = (Blacksmith) Bukkit.getServer().getPluginManager().getPlugin("Blacksmith");
    }

    @Override
    public void load(DataKey key) {
        for (DataKey sub : key.getRelative("reforgeable-items").getIntegerSubKeys())
            if (Material.getMaterial(sub.getString("").toUpperCase().replace('-', '_')) != null)
                reforgeableItems.add(Material.getMaterial(sub.getString("").toUpperCase().replace('-', '_')));

        // Override defaults if they exist
        if (key.keyExists("messages.busy-with-player"))
            busyWithPlayerMsg = key.getString("messages.busy-with-player");
        if (key.keyExists("messages.busy-with-reforge"))
            busyReforgingMsg = key.getString("messages.busy-with-reforge");
        if (key.keyExists("messages.cost"))
            costMsg = key.getString("messages.cost");
        if (key.keyExists("messages.invalid-item"))
            invalidItemMsg = key.getString("messages.invalid-item");
        if (key.keyExists("messages.start-reforge"))
            startReforgeMsg = key.getString("messages.start-reforge");
        if (key.keyExists("messages.successful-reforge"))
            successMsg = key.getString("messages.successful-reforge");
        if (key.keyExists("messages.fail-reforge"))
            failMsg = key.getString("messages.fail-reforge");
        if (key.keyExists("messages.insufficient-funds"))
            insufficientFundsMsg = key.getString("messages.insufficient-funds");
        if (key.keyExists("delays-in-seconds.minimum"))
            minReforgeDelay = key.getInt("delays-in-seconds.minimum");
        if (key.keyExists("delays-in-seconds.maximum"))
            maxReforgeDelay = key.getInt("delays-in-seconds.maximum");
        if (key.keyExists("percent-chance-to-fail-reforge"))
            failChance = key.getInt("percent-chance-to-fail-reforge");
    }

    @Override
    public void onRightClick(NPC npc, Player player) {
        // TODO cooldowns
        ItemStack hand = player.getItemInHand();
        if (session != null) {
            if (!session.isInSession(player)) {
                npc.chat(busyWithPlayerMsg);
                return;
            }

            if (session.isRunning()) {
                npc.chat(player, busyReforgingMsg);
                return;
            }
            if (session.handleClick())
                session = null;
            else
                reforge(npc, player);
        } else {
            if ((!plugin.isTool(hand) && !plugin.isArmor(hand))
                    || (!reforgeableItems.isEmpty() && !reforgeableItems.contains(hand.getType()))) {
                npc.chat(player, invalidItemMsg);
                return;
            }
            session = new RepairSession(player, npc);
            npc.chat(player, costMsg.replace("<price>", String.valueOf(plugin.getCost(hand))).replace("<item>",
                    hand.getType().name().toLowerCase().replace('_', ' ')));
        }
    }

    @Override
    public void save(DataKey key) {
        for (int i = 0; i < reforgeableItems.size(); i++)
            key.getRelative("reforgeable-items").setString(String.valueOf(i),
                    reforgeableItems.get(i).name().toLowerCase().replace('_', '-'));

        key.setString("messages.busy-with-player", busyWithPlayerMsg);
        key.setString("messages.busy-with-reforge", busyReforgingMsg);
        key.setString("messages.cost", costMsg);
        key.setString("messages.invalid-item", invalidItemMsg);
        key.setString("messages.start-reforge", startReforgeMsg);
        key.setString("messages.successful-reforge", successMsg);
        key.setString("messages.fail-reforge", failMsg);
        key.setString("messages.insufficient-funds", insufficientFundsMsg);
        key.setInt("delays-in-seconds.minimum", minReforgeDelay);
        key.setInt("delays-in-seconds.maximum", maxReforgeDelay);
        key.setInt("percent-chance-to-fail-reforge", failChance);
    }

    public String getInsufficientFundsMessage() {
        return insufficientFundsMsg;
    }

    private void reforge(NPC npc, Player player) {
        npc.chat(player, startReforgeMsg);
        session.setTask(plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin,
                new ReforgeTask(npc, player), (new Random().nextInt(maxReforgeDelay) + minReforgeDelay) * 20));
        if (npc.getBukkitEntity() instanceof Player)
            ((Player) npc.getBukkitEntity()).setItemInHand(player.getItemInHand());
        player.setItemInHand(null);
    }

    private class ReforgeTask implements Runnable {
        private final NPC npc;
        private final Player player;
        private final ItemStack reforge;

        private ReforgeTask(NPC npc, Player player) {
            this.npc = npc;
            this.player = player;
            reforge = player.getItemInHand();
        }

        @Override
        public void run() {
            npc.chat(player, reforgeItemInHand() ? successMsg : failMsg);
            if (npc.getBukkitEntity() instanceof Player)
                ((Player) npc.getBukkitEntity()).setItemInHand(null);
            player.getWorld().dropItemNaturally(npc.getBukkitEntity().getLocation(), reforge);
            session = null;
        }

        private boolean reforgeItemInHand() {
            Random random = new Random();
            if (random.nextInt(100) < failChance) {
                for (Enchantment enchantment : reforge.getEnchantments().keySet()) {
                    // Remove or downgrade enchantments
                    if (random.nextBoolean())
                        reforge.removeEnchantment(enchantment);
                    else {
                        if (reforge.getEnchantmentLevel(enchantment) > 1) {
                            reforge.removeEnchantment(enchantment);
                            reforge.addEnchantment(enchantment, 1);
                        }
                    }
                }
                // Damage the item
                short durability = (short) (reforge.getDurability() + reforge.getDurability() * random.nextInt(5));
                short maxDurability = reforge.getType().getMaxDurability();
                if (durability <= 0)
                    durability = (short) (maxDurability / 3);
                else if (reforge.getDurability() + durability > maxDurability)
                    durability = (short) (maxDurability - random.nextInt(maxDurability - 25));
                reforge.setDurability((short) (durability));
                return false;
            }
            int chance = 50;
            if (reforge.getDurability() == 0)
                chance *= 2;
            else
                reforge.setDurability((short) 0);
            // Add random enchantments
            for (int i = 0; i < chance; i++) {
                int id = random.nextInt(100);
                Enchantment enchantment = Enchantment.getById(id);
                if (enchantment != null && enchantment.canEnchantItem(reforge))
                    reforge.addEnchantment(Enchantment.getById(id), random.nextInt(enchantment.getMaxLevel()) + 1);
            }
            return true;
        }
    }
}