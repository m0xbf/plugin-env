package com.janetfilter.plugins.env;

import com.janetfilter.core.models.FilterRule;
import com.janetfilter.core.plugin.MyTransformer;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class ProcessEnvironmentTransformer implements MyTransformer {
    private final List<FilterRule> rules;

    public ProcessEnvironmentTransformer(List<FilterRule> rules) {
        this.rules = rules;
    }

    @Override
    public String getHookClassName() {
        return "java/lang/ProcessEnvironment";
    }

    @Override
    public byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        EnvFilter.setRules(rules);

        ClassReader reader = new ClassReader(classBytes);
        ClassNode node = new ClassNode(ASM5);
        reader.accept(node, 0);

        for (MethodNode m : node.methods) {
            if (m.name.equals("getenv") && m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                ListIterator<AbstractInsnNode> iterator = m.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insn = iterator.next();
                    if (insn.getOpcode() == ARETURN) {
                        InsnList toInject = new InsnList();
                        toInject.add(new VarInsnNode(ALOAD, 0));  // Load "name"
                        toInject.add(new MethodInsnNode(INVOKESTATIC, "com/janetfilter/plugins/env/EnvFilter", "testGetEnv", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                        m.instructions.insertBefore(insn, toInject);

                        break;
                    }
                }

                continue;
            }

            if (m.name.equals("getenv") && m.desc.equals("()Ljava/util/Map;")) {
                ListIterator<AbstractInsnNode> iterator = m.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insn = iterator.next();
                    if (insn.getOpcode() == ARETURN) {
                        InsnList toInject = new InsnList();
                        toInject.add(new MethodInsnNode(INVOKESTATIC, "com/janetfilter/plugins/env/EnvFilter", "testGetEnv", "(Ljava/util/Map;)Ljava/util/Map;", false));
                        m.instructions.insertBefore(insn, toInject);

                        break;
                    }
                }

                continue;
            }

            if (m.name.equals("environment") && m.desc.equals("()Ljava/util/Map;")) {
                ListIterator<AbstractInsnNode> iterator = m.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insn = iterator.next();
                    if (insn.getOpcode() == ARETURN) {
                        InsnList toInject = new InsnList();
                        toInject.add(new MethodInsnNode(INVOKESTATIC, "com/janetfilter/plugins/env/EnvFilter", "testEnvironment", "(Ljava/util/Map;)Ljava/util/Map;", false));
                        m.instructions.insertBefore(insn, toInject);

                        break;
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }
}
