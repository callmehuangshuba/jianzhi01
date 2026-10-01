package com.anno.app;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * 项目统一命令行入口。
 *
 * <p>本基线工程只提供入口骨架与最小示例命令，业务引擎（工作流、配额、对账等）
 * 由后续任务在本工程中实现，并挂接到此入口的子命令下。</p>
 *
 * <p>运行方式（构建后）：{@code java -jar target/jianzhi01.jar [command]}</p>
 */
public final class Main {

    public static final String VERSION = "1.0.0";

    private Main() {
    }

    public static void main(String[] args) {
        // Windows 上 JVM 默认按控制台代码页（GBK）编码 stdout，
        // 与 UTF-8 源码及 Git Bash 终端不一致会导致中文输出乱码。
        // 这里仅在真实入口重设编码；run() 不触碰标准流，
        // 以免覆盖单元测试注入的捕获流。
        System.setOut(new PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.err), true, StandardCharsets.UTF_8));

        int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    /**
     * 解析并执行命令，返回进程退出码。抽成独立方法以便单元测试直接调用。
     */
    static int run(String[] args) {
        if (args.length == 0) {
            printUsage();
            return 0;
        }
        String command = args[0];
        return switch (command) {
            case "--version", "version" -> {
                System.out.println("jianzhi01 " + VERSION);
                yield 0;
            }
            case "ping" -> {
                System.out.println("pong");
                yield 0;
            }
            case "--help", "help" -> {
                printUsage();
                yield 0;
            }
            default -> {
                System.err.println("未知命令: " + command);
                printUsage();
                yield 2;
            }
        };
    }

    private static void printUsage() {
        System.out.println("""
                jianzhi01 - 基线工程命令行入口

                用法: java -jar target/jianzhi01.jar [command]

                命令:
                  version    打印版本号
                  ping       最小验证命令，输出 pong
                  help       显示本帮助

                业务子命令将在具体任务实现后注册到此入口。""");
    }
}
