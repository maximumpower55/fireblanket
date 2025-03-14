package net.modfest.fireblanket.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.modfest.fireblanket.client.ClientState;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class BERMaskCommand {
	public static void init(LiteralArgumentBuilder<FabricClientCommandSource> base, CommandRegistryAccess access) {
		base.then(literal("be")
			.then(literal("add")
				.then(argument("type", RegistryEntryArgumentType.registryEntry(access, RegistryKeys.BLOCK_ENTITY_TYPE))
					.executes(client -> {
						RegistryEntry.Reference<BlockEntityType<?>> type = getRegistryEntry(client, "type", RegistryKeys.BLOCK_ENTITY_TYPE);

						if (type.getKey().isEmpty()) {
							client.getSource().sendFeedback(Text.literal("This BE seems to not be registered??"));
							return 1;
						}

						client.getSource().sendFeedback(Text.literal("Added " + type.registryKey().getValue() + " to the mask."));

						MinecraftClient.getInstance().submit(() -> ClientState.MASKED_BERS.add(type.registryKey().getValue()));
						return 0;
					})
				)
			)
			.then(literal("remove")
				.then(argument("type", RegistryEntryArgumentType.registryEntry(access, RegistryKeys.BLOCK_ENTITY_TYPE))
					.executes(client -> {
						RegistryEntry.Reference<BlockEntityType<?>> type = getRegistryEntry(client, "type", RegistryKeys.BLOCK_ENTITY_TYPE);

						if (type.getKey().isEmpty()) {
							client.getSource().sendFeedback(Text.literal("This BE seems to not be registered??"));
							return 1;
						}

						client.getSource().sendFeedback(Text.literal("Removed " + type.registryKey().getValue() + " to the mask."));

						MinecraftClient.getInstance().submit(() -> ClientState.MASKED_BERS.remove(type.registryKey().getValue()));
						return 0;
					})
				)
			)
			.then(literal("list")
				.executes(client -> {
					if (ClientState.MASKED_BERS.isEmpty()) {
						client.getSource().sendFeedback(Text.literal("Your mask is empty."));
					} else {
						client.getSource().sendFeedback(Text.literal("You are currently masking: " + ClientState.MASKED_BERS));
					}
					return 0;
				})
			)
			.then(literal("clear")
				.executes(client -> {
					int size = ClientState.MASKED_BERS.size();
					MinecraftClient.getInstance().submit(() -> ClientState.MASKED_BERS.clear());

					client.getSource().sendFeedback(Text.literal("Cleared " + size + " BE" + (size == 1 ? "" : "s") + " out of the mask."));
					return 0;
				})
			)
		);
	}

	private static final Dynamic3CommandExceptionType WRONG_TYPE_EXCEPTION = new Dynamic3CommandExceptionType(
			(tag, type, expectedType) -> Text.translatable("argument.resource_tag.invalid_type", tag, type, expectedType)
	);

	public static <T> RegistryEntry.Reference<T> getRegistryEntry(CommandContext<FabricClientCommandSource> context, String name, RegistryKey<Registry<T>> registryRef) throws CommandSyntaxException {
		RegistryEntry.Reference<T> reference = context.getArgument(name, RegistryEntry.Reference.class);
		RegistryKey<?> registryKey = reference.registryKey();
		if (registryKey.isOf(registryRef)) {
			return reference;
		} else {
			throw WRONG_TYPE_EXCEPTION.create(registryKey.getValue(), registryKey.getRegistry(), registryRef.getValue());
		}
	}
}
