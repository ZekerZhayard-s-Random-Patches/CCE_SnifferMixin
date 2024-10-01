package io.github.zekerzhayard.cce_sniffermixin;

import java.util.List;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfigPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        MappingResolver mr = FabricLoader.getInstance().getMappingResolver();
        for (MethodNode mn : targetClass.methods) {
            if (mn.name.contains("carpetskyadditions$lambda$getLootTable$0") && mn.desc.equals("(L" + mr.mapClassName("intermediary", "net.minecraft.class_2338").replace('.', '/') + ";Ljava/util/Map$Entry;)L" + mr.mapClassName("intermediary", "net.minecraft.class_5321").replace('.', '/') + ";")) {
                AbstractInsnNode[] ains = mn.instructions.toArray();
                LabelNode ln = null;
                boolean foundLabel = false;

                for (AbstractInsnNode ain : ains) {
                    if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode min = (MethodInsnNode) ain;
                        if (min.owner.equals(mr.mapClassName("intermediary", "net.minecraft.class_3449").replace('.', '/')) && min.name.equals(mr.mapMethodName("intermediary", "net.minecraft.class_3449", "method_16657", "()Z")) && min.desc.equals("()Z")) {
                            foundLabel = true;
                        }
                    } else if (foundLabel && ain.getOpcode() == Opcodes.IFEQ) {
                        ln = ((JumpInsnNode) ain).label;
                    }
                }

                if (ln != null) {
                    LabelNode ln0 = new LabelNode();
                    for (AbstractInsnNode ain : ains) {
                        if (ain.getOpcode() == Opcodes.CHECKCAST && ((TypeInsnNode) ain).desc.equals("net/minecraft/registry/entry/RegistryEntryList")) {
                            mn.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP));
                            mn.instructions.insertBefore(ain, new TypeInsnNode(Opcodes.INSTANCEOF, "net/minecraft/registry/entry/RegistryEntryList"));
                            mn.instructions.insertBefore(ain, new JumpInsnNode(Opcodes.IFEQ, ln0));
                        } else if (ain.equals(ln)) {
                            LabelNode ln1 = new LabelNode();
                            mn.instructions.insertBefore(ain, new JumpInsnNode(Opcodes.GOTO, ln1));
                            mn.instructions.insertBefore(ain, ln0);
                            mn.instructions.insertBefore(ain, new InsnNode(Opcodes.POP));
                            mn.instructions.insertBefore(ain, new InsnNode(Opcodes.POP2));
                            mn.instructions.insertBefore(ain, ln1);
                        }
                    }
                }
            }
        }
    }
}
